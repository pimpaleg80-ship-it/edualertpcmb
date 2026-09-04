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
        payloadDiffJson: String
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
                    payloadPreview = payloadDiffJson
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

    // Code Generation Utilities for Client Developers
    companion object {
        fun getNextJsWebhookSnippet(): String {
            return """
// Next.js App Router: app/api/webhooks/edualert/route.ts
import { NextRequest, NextResponse } from 'next/server';
import crypto from 'crypto';
import { revalidatePath, revalidateTag } from 'next/cache';

const WEBHOOK_SECRET = process.env.EDUALERT_WEBHOOK_SECRET || 'whsec_edualert_nextjs_98fa71d3';

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
  const { event, examId, timestamp } = payload;

  // 2. Execute Immediate On-Demand Cache Invalidation
  switch (event) {
    case 'deadline.extended':
    case 'exam.updated':
    case 'eligibility.modified':
      revalidatePath('/exams');
      if (examId) revalidatePath(`/exams/${'$'}{examId}`);
      revalidateTag('exams-catalog');
      break;

    case 'broadcast.emergency':
      // Trigger Web Push or SSE broadcast to connected browser clients
      break;
  }

  return NextResponse.json({ received: true, event, timestamp: Date.now() }, { status: 200 });
}
""".trimIndent()
        }

        fun getReactNativeSyncSnippet(): String {
            return """
// React Native: hooks/useEduAlertSync.ts
import { useEffect, useState } from 'react';
import AsyncStorage from '@react-native-async-storage/async-storage';
import * as Notifications from 'expo-notifications';

const SYNC_API_URL = 'https://api.edualert.ac.in/v1/exams';
const WS_STREAM_URL = 'wss://stream.edualert.ac.in/v1/live-sync';

export function useEduAlertSync() {
  const [exams, setExams] = useState([]);
  const [syncStatus, setSyncStatus] = useState<'connected' | 'polling' | 'offline'>('connected');
  const [lastSyncMs, setLastSyncMs] = useState(Date.now());

  // 1. Primary Real-Time WebSocket / Push Listener
  useEffect(() => {
    let ws: WebSocket | null = null;

    try {
      ws = new WebSocket(WS_STREAM_URL);
      ws.onopen = () => setSyncStatus('connected');
      ws.onmessage = async (event) => {
        const message = JSON.parse(event.data);
        if (message.type === 'EXAM_UPDATED' || message.type === 'DEADLINE_EXTENDED') {
          // Merge delta update into local client cache
          setExams((prev) => prev.map((e) => (e.id === message.examId ? { ...e, ...message.delta } : e)));
          setLastSyncMs(Date.now());
          
          // Show Local Push Alert if starred
          if (message.isUrgent) {
            await Notifications.scheduleNotificationAsync({
              content: { title: message.title, body: message.body },
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
  }, []);

  // 2. Adaptive Delta Polling Fallback (Runs every 30s or when app returns to foreground)
  const pollDelta = async () => {
    try {
      const lastModified = await AsyncStorage.getItem('edualert_last_sync');
      const res = await fetch(`${'$'}{SYNC_API_URL}?since=${'$'}{lastModified || 0}`);
      if (res.status === 200) {
        const { updatedExams, serverTime } = await res.json();
        if (updatedExams.length > 0) {
          setExams((prev) => {
            const map = new Map(prev.map(item => [item.id, item]));
            updatedExams.forEach(item => map.set(item.id, item));
            return Array.from(map.values());
          });
          await AsyncStorage.setItem('edualert_last_sync', serverTime.toString());
        }
      }
      setLastSyncMs(Date.now());
    } catch (e) {
      console.warn('Polling delta sync error', e);
    }
  };

  return { exams, syncStatus, lastSyncMs, pollDelta };
}
""".trimIndent()
        }
    }
}
