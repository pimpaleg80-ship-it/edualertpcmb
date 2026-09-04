package com.example.data.sync

enum class SyncMode(val label: String, val description: String) {
    PUSH_WEBHOOK("Push & Webhook (Real-Time)", "Sub-second event streaming via Webhooks & Server-Sent Events"),
    ADAPTIVE_POLLING("Adaptive Polling (Delta-Sync)", "Interval-based polling with 15s acceleration during active deadlines"),
    HYBRID("Hybrid Architecture (Recommended)", "Real-time webhooks with automatic polling fallback for offline recovery")
}

enum class SyncEventType(val title: String, val iconName: String) {
    EXAM_UPDATED("Exam Core Details Updated", "Edit"),
    DEADLINE_EXTENDED("Deadline Extension Window", "Timer"),
    ELIGIBILITY_MODIFIED("Eligibility & Fee Criteria Changed", "Verified"),
    EMERGENCY_BROADCAST("Official Authority Emergency Alert", "Campaign"),
    WEBHOOK_PING("Webhook Test Ping (Handshake)", "CheckCircle")
}

data class WebhookEndpoint(
    val id: String,
    val name: String,
    val url: String,
    val clientPlatform: String, // e.g. "Next.js Web / SSR", "React Native Mobile (iOS/Android)"
    val secretToken: String,
    val isEnabled: Boolean = true,
    val lastDeliveryStatus: String = "200 OK",
    val lastLatencyMs: Long = 42,
    val totalDispatches: Int = 1
)

data class SyncAuditLog(
    val id: String,
    val eventType: SyncEventType,
    val examShortCode: String,
    val targetClient: String,
    val timestampMs: Long = System.currentTimeMillis(),
    val summary: String,
    val latencyMs: Long,
    val httpStatusCode: Int = 200,
    val payloadPreview: String,
    val targetYear: Int = 2026
)

data class SyncState(
    val mode: SyncMode = SyncMode.HYBRID,
    val pollingIntervalSeconds: Int = 30,
    val isSyncing: Boolean = false,
    val lastSyncTimestampMs: Long = System.currentTimeMillis(),
    val isAdaptive: Boolean = true,
    val registeredWebhooks: List<WebhookEndpoint> = emptyList(),
    val auditLogs: List<SyncAuditLog> = emptyList(),
    val totalEventsDispatched: Int = 0,
    val syncTargetYear: Int = 2026
)
