/**
 * EduAlert PCMB - Push Notification Scheduling & Dispatch Worker
 * Runs hourly via Cron (Inngest / Upstash QStash / BullMQ)
 *
 * Evaluates exam registration deadlines & milestones across:
 * - T-30 Days: "Registration Open / Bulletin Published"
 * - T-7 Days: "1 Week Left to Apply"
 * - T-48 Hours & T-12 Hours: "Urgent: Registration closes tonight at 11:50 PM"
 * - Event Triggers: Immediate dispatch on Admit Card Release or Answer Key Live
 * - Priority Filter: Dream Exams bypass normal DND windows
 */

import { PrismaClient, EventType, NotificationStatus, NotificationChannel, PriorityLevel } from "@prisma/client";
import * as admin from "firebase-admin";

const prisma = new PrismaClient();

// Initialize Firebase Admin SDK if not already initialized
if (!admin.apps.length) {
  admin.initializeApp({
    credential: admin.credential.applicationDefault(),
  });
}

interface DeadlineWindowRule {
  triggerKey: string;
  hoursRemainingMin: number;
  hoursRemainingMax: number;
  titleTemplate: (examName: string) => string;
  bodyTemplate: (examName: string, deadlineFormatted: string) => string;
}

const REGISTRATION_DEADLINE_RULES: DeadlineWindowRule[] = [
  {
    triggerKey: "T_MINUS_30_DAYS",
    hoursRemainingMin: 720 - 2, // ~30 days (720h)
    hoursRemainingMax: 720 + 2,
    titleTemplate: (name) => `📢 Registration Open: ${name}`,
    bodyTemplate: (name, dl) => `Applications are now officially live for ${name}. Information bulletin published. Apply before ${dl}.`,
  },
  {
    triggerKey: "T_MINUS_7_DAYS",
    hoursRemainingMin: 168 - 2, // ~7 days (168h)
    hoursRemainingMax: 168 + 2,
    titleTemplate: (name) => `⏳ 1 Week Left to Apply: ${name}`,
    bodyTemplate: (name, dl) => `Only 7 days remain before the registration portal closes on ${dl}. Complete your form and fee payment now!`,
  },
  {
    triggerKey: "T_MINUS_48_HOURS",
    hoursRemainingMin: 48 - 2,  // ~48 hours
    hoursRemainingMax: 48 + 2,
    titleTemplate: (name) => `🚨 Last 48 Hours: ${name} Deadline`,
    bodyTemplate: (name, dl) => `Urgent: The registration window for ${name} closes in 48 hours (${dl}). Server rush expected, apply today!`,
  },
  {
    triggerKey: "T_MINUS_12_HOURS",
    hoursRemainingMin: 10,       // ~12 hours
    hoursRemainingMax: 13,
    titleTemplate: (name) => `🔥 Final Warning: ${name} Closes Tonight!`,
    bodyTemplate: (name, dl) => `Registration and fee payment window for ${name} shuts strictly at ${dl}. Do not miss your chance.`,
  },
];

/**
 * Main scheduled task executed by Cron runner every hour
 */
export async function evaluateDeadlinesAndQueueNotifications(): Promise<{
  queuedCount: number;
  dispatchedCount: number;
  errors: string[];
}> {
  const now = new Date();
  const errors: string[] = [];
  let queuedCount = 0;
  let dispatchedCount = 0;

  console.log(`[EduAlert Scheduler] Starting evaluation at ${now.toISOString()}`);

  // 1. Fetch all active exams with upcoming dates in the next 35 days
  const activeExams = await prisma.exam.findMany({
    where: { isActive: true },
    include: {
      dates: {
        where: {
          startDatetime: { lte: new Date(now.getTime() + 35 * 86400000) },
        },
      },
    },
  });

  // 2. Fetch active student preferences
  const userPreferences = await prisma.userPreference.findMany({
    where: { pushEnabled: true },
  });

  // Map users following exams or with dream exams
  for (const exam of activeExams) {
    const regEvent = exam.dates.find(
      (d) => d.eventType === EventType.REGISTRATION_OPEN && d.endDatetime
    );

    if (!regEvent || !regEvent.endDatetime) continue;

    const msUntilDeadline = regEvent.endDatetime.getTime() - now.getTime();
    const hoursUntilDeadline = msUntilDeadline / (1000 * 60 * 60);

    if (hoursUntilDeadline <= 0) continue; // Already closed

    // Check matched rules
    for (const rule of REGISTRATION_DEADLINE_RULES) {
      if (
        hoursUntilDeadline >= rule.hoursRemainingMin &&
        hoursUntilDeadline < rule.hoursRemainingMax
      ) {
        const deadlineFormatted = regEvent.endDatetime.toLocaleDateString("en-IN", {
          day: "numeric",
          month: "short",
          hour: "2-digit",
          minute: "2-digit",
        });

        // Find interested students:
        // 1. Students following this exam
        // 2. Students matching stream (e.g. PCM for JEE) or State Domicile (for State CETs)
        const targetUsers = userPreferences.filter((u) => {
          const isFollowed = u.followedExamIds.includes(exam.id);
          const isDream = u.dreamExamIds.includes(exam.id);
          const matchesCategory =
            (u.stream === "PCM" && (exam.category === "ENGINEERING_PCM" || exam.category === "DEFENSE_PCM_PCMB")) ||
            (u.stream === "PCB" && exam.category === "MEDICAL_PCB") ||
            (u.stream === "PCMB");
          const matchesState = !exam.stateDomicileCode || exam.stateDomicileCode === u.homeState;

          return isDream || isFollowed || (matchesCategory && matchesState);
        });

        for (const user of targetUsers) {
          const isDream = user.dreamExamIds.includes(exam.id);

          // Check if notification for this triggerKey already queued/sent in past 24h
          const existing = await prisma.notificationQueue.findFirst({
            where: {
              userId: user.userId,
              examId: exam.id,
              payloadJson: {
                path: ["triggerKey"],
                equals: rule.triggerKey,
              },
            },
          });

          if (!existing) {
            await prisma.notificationQueue.create({
              data: {
                userId: user.userId,
                examId: exam.id,
                channel: NotificationChannel.IN_APP_FCM,
                priority: isDream ? PriorityLevel.URGENT_DREAM_EXAM : PriorityLevel.NORMAL,
                scheduledAt: now,
                title: (isDream ? "⭐ [Dream Exam] " : "") + rule.titleTemplate(exam.shortCode),
                body: rule.bodyTemplate(exam.fullName, deadlineFormatted),
                deepLinkUrl: `edualert://exam/${exam.slug}`,
                payloadJson: {
                  triggerKey: rule.triggerKey,
                  examId: exam.id,
                  slug: exam.slug,
                  applicationUrl: exam.applicationUrl,
                  isDream,
                },
                status: NotificationStatus.PENDING,
              },
            });
            queuedCount++;
          }
        }
      }
    }

    // 3. Event-based triggers (Admit card released within last 2 hours)
    const admitCardEvent = exam.dates.find(
      (d) => d.eventType === EventType.ADMIT_CARD_RELEASE
    );

    if (admitCardEvent) {
      const hoursSinceRelease = (now.getTime() - admitCardEvent.startDatetime.getTime()) / (1000 * 60 * 60);
      if (hoursSinceRelease >= 0 && hoursSinceRelease < 2) {
        // Immediate blast for Admit Card Live
        const targetUsers = userPreferences.filter(
          (u) => u.followedExamIds.includes(exam.id) || u.dreamExamIds.includes(exam.id)
        );

        for (const user of targetUsers) {
          const isDream = user.dreamExamIds.includes(exam.id);
          const existing = await prisma.notificationQueue.findFirst({
            where: {
              userId: user.userId,
              examId: exam.id,
              payloadJson: {
                path: ["triggerKey"],
                equals: "ADMIT_CARD_LIVE",
              },
            },
          });

          if (!existing) {
            await prisma.notificationQueue.create({
              data: {
                userId: user.userId,
                examId: exam.id,
                channel: NotificationChannel.IN_APP_FCM,
                priority: PriorityLevel.URGENT_DREAM_EXAM,
                scheduledAt: now,
                title: `🎫 Admit Card Live: ${exam.shortCode}!`,
                body: `Hall ticket & City intimation slips for ${exam.shortCode} have been released. Download immediately using your application number.`,
                deepLinkUrl: `edualert://exam/${exam.slug}/admit-card`,
                payloadJson: {
                  triggerKey: "ADMIT_CARD_LIVE",
                  examId: exam.id,
                  slug: exam.slug,
                  portalUrl: exam.applicationUrl,
                },
                status: NotificationStatus.PENDING,
              },
            });
            queuedCount++;
          }
        }
      }
    }
  }

  // 4. Dispatch Pending Notifications via Firebase Cloud Messaging (FCM)
  const pendingNotifications = await prisma.notificationQueue.findMany({
    where: {
      status: NotificationStatus.PENDING,
      scheduledAt: { lte: now },
    },
    include: {
      userPref: true,
      exam: true,
    },
    take: 200, // Batch limit per cycle
  });

  for (const notif of pendingNotifications) {
    try {
      const currentHour = now.getHours();
      const isDreamExam = notif.priority === PriorityLevel.URGENT_DREAM_EXAM;

      // Check DND Window unless it's a marked Dream Exam
      const inDndWindow =
        notif.userPref.dndStartHour > notif.userPref.dndEndHour
          ? currentHour >= notif.userPref.dndStartHour || currentHour < notif.userPref.dndEndHour
          : currentHour >= notif.userPref.dndStartHour && currentHour < notif.userPref.dndEndHour;

      if (inDndWindow && !isDreamExam) {
        // Postpone until DND expires (e.g. 6:01 AM)
        const postponed = new Date(now);
        postponed.setHours(notif.userPref.dndEndHour, 1, 0, 0);
        if (postponed <= now) postponed.setDate(postponed.getDate() + 1);

        await prisma.notificationQueue.update({
          where: { id: notif.id },
          data: { scheduledAt: postponed },
        });
        continue;
      }

      if (!notif.userPref.fcmToken) {
        await prisma.notificationQueue.update({
          where: { id: notif.id },
          data: { status: NotificationStatus.FAILED, errorMessage: "Missing FCM Token" },
        });
        continue;
      }

      // Dispatch FCM Push Payload
      const message: admin.messaging.Message = {
        token: notif.userPref.fcmToken,
        notification: {
          title: notif.title,
          body: notif.body,
        },
        data: {
          examId: notif.examId,
          deepLink: notif.deepLinkUrl || "",
          priority: notif.priority,
          category: notif.exam.category,
        },
        android: {
          priority: isDreamExam ? "high" : "normal",
          notification: {
            channelId: isDreamExam ? "edualert_urgent_dream" : "edualert_general",
            clickAction: "FLUTTER_NOTIFICATION_CLICK",
            icon: "ic_notification",
            color: isDreamExam ? "#F59E0B" : "#1E3A8A",
          },
        },
      };

      await admin.messaging().send(message);

      await prisma.notificationQueue.update({
        where: { id: notif.id },
        data: {
          status: NotificationStatus.SENT,
          sentAt: new Date(),
        },
      });
      dispatchedCount++;
    } catch (err: any) {
      console.error(`Error sending notification ${notif.id}:`, err);
      errors.push(err.message || String(err));
      await prisma.notificationQueue.update({
        where: { id: notif.id },
        data: {
          status: NotificationStatus.FAILED,
          errorMessage: err.message || "FCM Send Failure",
        },
      });
    }
  }

  console.log(`[EduAlert Scheduler] Completed: Queued ${queuedCount}, Dispatched ${dispatchedCount}`);
  return { queuedCount, dispatchedCount, errors };
}

/**
 * Next.js / Fastify API Route Handler
 * Endpoint: POST /api/v1/cron/deadline-dispatcher
 * Secured via CRON_SECRET authorization header
 */
export async function handleCronRequest(req: Request): Promise<Response> {
  const authHeader = req.headers.get("authorization");
  if (authHeader !== `Bearer ${process.env.CRON_SECRET}`) {
    return new Response(JSON.stringify({ error: "Unauthorized" }), { status: 401 });
  }

  const result = await evaluateDeadlinesAndQueueNotifications();
  return new Response(JSON.stringify({ success: true, ...result }), {
    status: 200,
    headers: { "Content-Type": "application/json" },
  });
}
