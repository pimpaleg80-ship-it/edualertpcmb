package com.example.data.sync

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.util.UUID
import kotlin.random.Random

class SyncEngine(
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.Default + SupervisorJob())
) {
    private val _syncState = MutableStateFlow(
        SyncState(
            mode = SyncMode.HYBRID,
            pollingIntervalSeconds = 30,
            isSyncing = false,
            lastSyncTimestampMs = System.currentTimeMillis() - 45_000L,
            isAdaptive = true,
            registeredWebhooks = listOf(
                WebhookEndpoint(
                    id = "wh-nextjs-prod",
                    name = "Next.js Web Portal (ISR / Revalidate)",
                    url = "https://portal.edualert.ac.in/api/webhooks/edualert",
                    clientPlatform = "Next.js 15 (App Router)",
                    secretToken = "whsec_edualert_nextjs_98fa71d3",
                    isEnabled = true,
                    lastDeliveryStatus = "200 OK (Revalidated)",
                    lastLatencyMs = 38,
                    totalDispatches = 14
                ),
                WebhookEndpoint(
                    id = "wh-rn-mobile",
                    name = "React Native Mobile Gateway",
                    url = "https://mobile-gw.edualert.ac.in/v1/rn-sync",
                    clientPlatform = "React Native (iOS & Android)",
                    secretToken = "whsec_edualert_rn_20b4ec71",
                    isEnabled = true,
                    lastDeliveryStatus = "200 OK (Pushed to FCM)",
                    lastLatencyMs = 45,
                    totalDispatches = 18
                ),
                WebhookEndpoint(
                    id = "wh-inngest-pcmb",
                    name = "Inngest Orchestrator (edualert-pcmb)",
                    url = "https://inn.gs/e/edualert-pcmb",
                    clientPlatform = "Inngest Serverless Workflows (edualert-pcmb)",
                    secretToken = "ingkey_pcmb_live_77a942bc",
                    isEnabled = true,
                    lastDeliveryStatus = "200 Event Dispatched",
                    lastLatencyMs = 24,
                    totalDispatches = 21
                ),
                WebhookEndpoint(
                    id = "wh-state-cet",
                    name = "Regional State CET Observer Node",
                    url = "https://cetcell.edualert.gov.in/sync-consumer",
                    clientPlatform = "State CET Server Node",
                    secretToken = "whsec_edualert_cet_55f190ca",
                    isEnabled = false,
                    lastDeliveryStatus = "Standby (Disabled)",
                    lastLatencyMs = 0,
                    totalDispatches = 3
                )
            ),
            auditLogs = listOf(
                SyncAuditLog(
                    id = "log-init-1",
                    eventType = SyncEventType.DEADLINE_EXTENDED,
                    examShortCode = "JEE Main",
                    targetClient = "Next.js Web Portal",
                    timestampMs = System.currentTimeMillis() - 120_000L,
                    summary = "Registration milestone updated. Deadline set to 28h remaining. revalidatePath('/exams/jee-main') triggered.",
                    latencyMs = 34,
                    httpStatusCode = 200,
                    payloadPreview = """{"event": "deadline.extended", "examId": "jee-main-2026", "status": "LAST_48_HOURS", "remainingHours": 28, "signature": "sha256=d41d8cd98f00b204e9800998ecf8427e"}"""
                ),
                SyncAuditLog(
                    id = "log-init-2",
                    eventType = SyncEventType.EMERGENCY_BROADCAST,
                    examShortCode = "ALL EXAMS",
                    targetClient = "React Native Mobile",
                    timestampMs = System.currentTimeMillis() - 360_000L,
                    summary = "Emergency NTA bulletin pushed to all mobile devices via WebSocket + Push notification channel.",
                    latencyMs = 52,
                    httpStatusCode = 200,
                    payloadPreview = """{"event": "broadcast.emergency", "title": "NTA Server Maintenance Window", "priority": "high", "deliveryChannel": "fcm_data_message"}"""
                ),
                SyncAuditLog(
                    id = "log-init-3",
                    eventType = SyncEventType.ELIGIBILITY_MODIFIED,
                    examShortCode = "BITSAT",
                    targetClient = "Next.js Web Portal",
                    timestampMs = System.currentTimeMillis() - 720_000L,
                    summary = "Eligibility criteria updated: 75% aggregate in PCM with min 60% in each subject.",
                    latencyMs = 29,
                    httpStatusCode = 200,
                    payloadPreview = """{"event": "eligibility.modified", "examId": "bitsat-2026", "min12thPercentage": "75% aggregate", "fees": {"general": "₹5400"}}"""
                )
            ),
            totalEventsDispatched = 35
        )
    )

    val syncState: StateFlow<SyncState> = _syncState.asStateFlow()

    private var pollingJob: Job? = null

    init {
        startAdaptivePolling()
    }

    private fun startAdaptivePolling() {
        pollingJob?.cancel()
        pollingJob = scope.launch {
            while (isActive) {
                val currentMode = _syncState.value.mode
                val intervalSeconds = _syncState.value.pollingIntervalSeconds

                delay(intervalSeconds * 1000L)

                if (currentMode == SyncMode.ADAPTIVE_POLLING || currentMode == SyncMode.HYBRID) {
                    performDeltaPollingCheck()
                }
            }
        }
    }

    private suspend fun performDeltaPollingCheck() {
        _syncState.update { it.copy(isSyncing = true) }
        delay(Random.nextLong(150, 350)) // simulated network delta handshake

        val deltaLatency = Random.nextLong(18, 45)
        _syncState.update { state ->
            state.copy(
                isSyncing = false,
                lastSyncTimestampMs = System.currentTimeMillis()
            )
        }
    }

    fun dispatchSyncEvent(
        eventType: SyncEventType,
        examShortCode: String,
        summary: String,
        payloadDiffJson: String,
        targetYear: Int = 2026
    ) {
        scope.launch {
            _syncState.update { it.copy(isSyncing = true) }

            // Simulate network transit delay to webhooks
            delay(Random.nextLong(100, 250))

            val newLogs = mutableListOf<SyncAuditLog>()
            val activeWebhooks = _syncState.value.registeredWebhooks.filter { it.isEnabled }

            activeWebhooks.forEach { endpoint ->
                val latency = Random.nextLong(22, 65)
                val log = SyncAuditLog(
                    id = "log-${UUID.randomUUID().toString().take(8)}",
                    eventType = eventType,
                    examShortCode = examShortCode,
                    targetClient = endpoint.name,
                    timestampMs = System.currentTimeMillis(),
                    summary = summary,
                    latencyMs = latency,
                    httpStatusCode = 200,
                    payloadPreview = payloadDiffJson,
                    targetYear = targetYear
                )
                newLogs.add(log)
            }

            _syncState.update { state ->
                val updatedWebhooks = state.registeredWebhooks.map { wh ->
                    if (wh.isEnabled) {
                        wh.copy(
                            lastDeliveryStatus = "200 OK (Delivered)",
                            lastLatencyMs = Random.nextLong(25, 55),
                            totalDispatches = wh.totalDispatches + 1
                        )
                    } else wh
                }

                state.copy(
                    isSyncing = false,
                    lastSyncTimestampMs = System.currentTimeMillis(),
                    registeredWebhooks = updatedWebhooks,
                    auditLogs = (newLogs + state.auditLogs).take(25),
                    totalEventsDispatched = state.totalEventsDispatched + activeWebhooks.size
                )
            }
        }
    }

    fun triggerTestPing(endpointId: String) {
        scope.launch {
            _syncState.update { it.copy(isSyncing = true) }
            delay(180)
            val latency = Random.nextLong(20, 48)

            val endpoint = _syncState.value.registeredWebhooks.find { it.id == endpointId }
            val targetName = endpoint?.name ?: "Client Endpoint"

            val pingLog = SyncAuditLog(
                id = "ping-${UUID.randomUUID().toString().take(8)}",
                eventType = SyncEventType.WEBHOOK_PING,
                examShortCode = "PING",
                targetClient = targetName,
                timestampMs = System.currentTimeMillis(),
                summary = "Webhook verification ping dispatched. Endpoint returned 200 OK in ${latency}ms.",
                latencyMs = latency,
                httpStatusCode = 200,
                payloadPreview = """{"event": "webhook.ping", "timestamp": ${System.currentTimeMillis()}, "status": "active", "signature": "sha256=verified_hmac_handshake"}"""
            )

            _syncState.update { state ->
                state.copy(
                    isSyncing = false,
                    lastSyncTimestampMs = System.currentTimeMillis(),
                    auditLogs = listOf(pingLog) + state.auditLogs,
                    totalEventsDispatched = state.totalEventsDispatched + 1
                )
            }
        }
    }

    suspend fun forceManualSync(): Long {
        _syncState.update { it.copy(isSyncing = true) }
        val simulatedLatency = Random.nextLong(35, 80)
        delay(simulatedLatency)
        _syncState.update { state ->
            state.copy(
                isSyncing = false,
                lastSyncTimestampMs = System.currentTimeMillis()
            )
        }
        return simulatedLatency
    }

    fun setSyncMode(mode: SyncMode) {
        _syncState.update { it.copy(mode = mode) }
    }

    fun setPollingInterval(seconds: Int) {
        _syncState.update { it.copy(pollingIntervalSeconds = seconds) }
        startAdaptivePolling()
    }

    fun setAdaptiveEnabled(enabled: Boolean) {
        _syncState.update { it.copy(isAdaptive = enabled) }
    }

    fun toggleWebhookEnabled(id: String) {
        _syncState.update { state ->
            val updated = state.registeredWebhooks.map { wh ->
                if (wh.id == id) wh.copy(isEnabled = !wh.isEnabled) else wh
            }
            state.copy(registeredWebhooks = updated)
        }
    }

    fun setSyncTargetYear(year: Int) {
        _syncState.update { it.copy(syncTargetYear = year) }
    }

    // Code Generation Utilities for Client Developers
    companion object {
        fun getNextJsWebhookSnippet(targetYear: Int = 2026): String {
            return """
// Next.js App Router: app/api/webhooks/edualert/route.ts
// Target Academic Cycle: $targetYear Entrances & Notifications
import { NextRequest, NextResponse } from 'next/server';
import crypto from 'crypto';
import { revalidatePath, revalidateTag } from 'next/cache';

const WEBHOOK_SECRET = process.env.EDUALERT_WEBHOOK_SECRET || 'whsec_edualert_nextjs_98fa71d3';
const DEFAULT_TARGET_YEAR = $targetYear;

export async function POST(req: NextRequest) {
  const signature = req.headers.get('x-edualert-signature');
  const bodyText = await req.text();

  // 1. Verify HMAC SHA-256 Signature
  const expectedSig = crypto
    .createHmac('sha256', WEBHOOK_SECRET)
    .update(bodyText)
    .digest('hex');

  if (signature !== `sha256=${'$'}{expectedSig}`) {
    return NextResponse.json({ error: 'Invalid HMAC signature' }, { status: 401 });
  }

  const payload = JSON.parse(bodyText);
  const { event, examId, targetYear = DEFAULT_TARGET_YEAR, timestamp } = payload;

  // 2. Validate Target Year: Only revalidate caches matching the active target cycle ($targetYear)
  if (targetYear && targetYear !== DEFAULT_TARGET_YEAR) {
    console.log(`[EduAlert Sync] Skipping event for year ${'$'}{targetYear}, server target is ${'$'}{DEFAULT_TARGET_YEAR}`);
    return NextResponse.json({ skipped: true, reason: 'Target year mismatch', targetYear }, { status: 200 });
  }

  // 3. Execute Immediate On-Demand Cache Invalidation
  switch (event) {
    case 'deadline.extended':
    case 'exam.updated':
    case 'eligibility.modified':
      revalidatePath('/exams');
      if (examId) revalidatePath(`/exams/${'$'}{examId}`);
      revalidateTag(`exams-catalog-${'$'}{targetYear}`);
      break;

    case 'broadcast.emergency':
      // Trigger Web Push or SSE broadcast to connected browser clients for target cycle
      break;
  }

  return NextResponse.json({ received: true, event, targetYear, timestamp: Date.now() }, { status: 200 });
}
""".trimIndent()
        }

        fun getReactNativeSyncSnippet(targetYear: Int = 2026): String {
            return """
// React Native: hooks/useEduAlertSync.ts
// Target Academic Year: $targetYear Exam Tracking
import { useEffect, useState } from 'react';
import AsyncStorage from '@react-native-async-storage/async-storage';
import * as Notifications from 'expo-notifications';

const SYNC_API_URL = 'https://api.edualert.ac.in/v1/exams';
const WS_STREAM_URL = 'wss://stream.edualert.ac.in/v1/live-sync';
const TARGET_YEAR = $targetYear;

export function useEduAlertSync(targetYear = TARGET_YEAR) {
  const [exams, setExams] = useState([]);
  const [syncStatus, setSyncStatus] = useState<'connected' | 'polling' | 'offline'>('connected');
  const [lastSyncMs, setLastSyncMs] = useState(Date.now());

  // 1. Primary Real-Time WebSocket / Push Listener filtered by targetYear
  useEffect(() => {
    let ws: WebSocket | null = null;

    try {
      ws = new WebSocket(`${'$'}{WS_STREAM_URL}?year=${'$'}{targetYear}`);
      ws.onopen = () => setSyncStatus('connected');
      ws.onmessage = async (event) => {
        const message = JSON.parse(event.data);
        
        // Target Year Check: ignore out-of-cycle broadcasts
        if (message.targetYear && message.targetYear !== targetYear) return;

        if (message.type === 'EXAM_UPDATED' || message.type === 'DEADLINE_EXTENDED') {
          // Merge delta update into local client cache for target year
          setExams((prev) => prev.map((e) => (e.id === message.examId ? { ...e, ...message.delta } : e)));
          setLastSyncMs(Date.now());
          
          // Show Local Push Alert if starred
          if (message.isUrgent) {
            await Notifications.scheduleNotificationAsync({
              content: { 
                title: `[${'$'}{targetYear}] ${'$'}{message.title}`, 
                body: message.body 
              },
              trigger: null, // instant
            });
          }
        }
      };
      ws.onerror = () => setSyncStatus('polling');
    } catch {
      setSyncStatus('polling');
    }

    return () => ws?.close();
  }, [targetYear]);

  // 2. Adaptive Delta Polling Fallback (Filtered on target year)
  const pollDelta = async () => {
    try {
      const lastModified = await AsyncStorage.getItem(`edualert_last_sync_${'$'}{targetYear}`);
      const res = await fetch(`${'$'}{SYNC_API_URL}?year=${'$'}{targetYear}&since=${'$'}{lastModified || 0}`);
      if (res.status === 200) {
        const { updatedExams, serverTime } = await res.json();
        if (updatedExams.length > 0) {
          setExams((prev) => {
            const map = new Map(prev.map(item => [item.id, item]));
            updatedExams.forEach(item => map.set(item.id, item));
            return Array.from(map.values());
          });
          await AsyncStorage.setItem(`edualert_last_sync_${'$'}{targetYear}`, serverTime.toString());
        }
      }
      setLastSyncMs(Date.now());
    } catch (e) {
      console.warn('Polling delta sync error', e);
    }
  };

  return { exams, syncStatus, lastSyncMs, targetYear, pollDelta };
}
""".trimIndent()
        }

        fun getInngestIntegrationSnippet(targetYear: Int = 2026): String {
            return """
// 1. Client Definition: src/lib/inngest/client.ts
import { Inngest } from "inngest";

// Create a client to send and receive events
export const inngest = new Inngest({ id: "edualert-pcmb" });

// 2. Workflow Functions: src/inngest/functions.ts
import { inngest } from "@/lib/inngest/client";

// Workflow A: Automated Multi-Stage Countdown & Reminders
export const deadlineExtensionReminder = inngest.createFunction(
  { id: "edualert-deadline-countdown", name: "PCMB Exam Deadline Sequence" },
  { event: "edualert/deadline.extended" },
  async ({ event, step }) => {
    const { examId, shortCode, milestone, targetTimestamp, targetYear = $targetYear } = event.data;

    // Step 1: Invalidate ISR cache immediately
    await step.run("revalidate-catalogs", async () => {
      console.log(`[EduAlert PCMB] Invalidating web caches for ${'$'}{shortCode} (${'$'}{targetYear})`);
      return { revalidated: true, examId, cycle: targetYear };
    });

    // Step 2: Calculate T-24h reminder sleep milestone
    const reminderTime = new Date(targetTimestamp - 24 * 60 * 60 * 1000);
    if (reminderTime > new Date()) {
      await step.sleepUntil("wait-until-24h-warning", reminderTime);

      // Step 3: Trigger High-Priority WhatsApp & Push Alert
      await step.run("send-pcmb-urgency-alert", async () => {
        return {
          status: "dispatched",
          channel: "whatsapp_sms_push",
          message: `🚨 Final 24h for ${'$'}{shortCode} ${'$'}{milestone}! (Cycle ${'$'}{targetYear})`
        };
      });
    }
  }
);

// Workflow B: Exam Core Details Fan-out & Notification Delivery
export const examUpdateFanout = inngest.createFunction(
  { id: "edualert-exam-update-fanout", name: "PCMB Exam Updates Fanout" },
  { event: "edualert/exam.updated" },
  async ({ event, step }) => {
    const { examId, shortCode, targetYear = $targetYear, name } = event.data;
    await step.run("sync-cdn-and-mobile-fcm", async () => {
      // Fan-out to FCM topic: pcmb_${'$'}{targetYear}
      return { topic: `pcmb_${'$'}{targetYear}`, success: true };
    });
  }
);

// 3. Next.js Route Handler: src/app/api/inngest/route.ts
import { serve } from "inngest/next";
import { inngest } from "@/lib/inngest/client";
import { deadlineExtensionReminder, examUpdateFanout } from "@/inngest/functions";

export const { GET, POST, PUT } = serve({
  client: inngest,
  functions: [deadlineExtensionReminder, examUpdateFanout],
});
""".trimIndent()
        }
    }
}
