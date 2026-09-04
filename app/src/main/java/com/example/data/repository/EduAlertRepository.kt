package com.example.data.repository

import com.example.data.local.EduAlertDao
import com.example.data.local.NotificationLogEntity
import com.example.data.local.UserApplicationEntity
import com.example.data.local.UserPreferenceEntity
import com.example.data.model.EligibilityDetails
import com.example.data.model.ExamItem
import com.example.data.model.ExamStatus
import com.example.data.model.FeeItem
import com.example.data.sync.SyncEngine
import com.example.data.sync.SyncEventType
import com.example.data.sync.SyncMode
import com.example.data.sync.SyncState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class EduAlertRepository(
    private val dao: EduAlertDao,
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.IO)
) {
    private val syncEngine = SyncEngine(scope)
    val syncState: StateFlow<SyncState> = syncEngine.syncState

    private val _exams = MutableStateFlow<List<ExamItem>>(SampleExamsData.getInitialExams())
    val exams: Flow<List<ExamItem>> = _exams.asStateFlow()

    val userPreference: Flow<UserPreferenceEntity?> = dao.getUserPreference()
    val allApplications: Flow<List<UserApplicationEntity>> = dao.getAllApplications()
    val allNotifications: Flow<List<NotificationLogEntity>> = dao.getAllNotifications()

    init {
        scope.launch {
            // Seed default user preferences if not present
            val existing = dao.getUserPreference().firstOrNull()
            if (existing == null) {
                dao.saveUserPreference(
                    UserPreferenceEntity(
                        id = 1,
                        stream = "PCM",
                        homeState = "Maharashtra",
                        targetPathway = "Engineering",
                        followedExamIds = "jee-main-2026,bitsat-2026,viteee-2026,mht-cet-2026",
                        dreamExamIds = "jee-main-2026,bitsat-2026",
                        pushEnabled = true,
                        whatsappEnabled = true,
                        dndBypassDreamExams = true
                    )
                )

                // Seed initial urgent notifications
                dao.insertNotification(
                    NotificationLogEntity(
                        examId = "jee-main-2026",
                        examShortCode = "JEE Main",
                        title = "⭐ [Dream Exam] Admit Card Live!",
                        message = "Admit cards for Session 1 are now downloadable from jeemain.nta.nic.in. Check your exam center and shift.",
                        channel = "High-Priority Push",
                        isDreamExam = true,
                        timestampMs = System.currentTimeMillis() - 2 * 3600_000L,
                        isRead = false
                    )
                )
                dao.insertNotification(
                    NotificationLogEntity(
                        examId = "viteee-2026",
                        examShortCode = "VITEEE",
                        title = "🚨 Last 48 Hours: Registration Closes",
                        message = "VITEEE 2026 applications close tomorrow at 11:59 PM. Server rush expected; finalize fee payment.",
                        channel = "Push Notification",
                        isDreamExam = false,
                        timestampMs = System.currentTimeMillis() - 8 * 3600_000L,
                        isRead = true
                    )
                )
                dao.insertNotification(
                    NotificationLogEntity(
                        examId = "mht-cet-2026",
                        examShortCode = "MHT CET",
                        title = "⏳ Late-Fee Window Closing in 28h",
                        message = "Maharashtra State CET Cell late registration window with ₹500 fee closes tomorrow night.",
                        channel = "Push Notification",
                        isDreamExam = false,
                        timestampMs = System.currentTimeMillis() - 14 * 3600_000L,
                        isRead = true
                    )
                )
            }
        }
    }

    suspend fun savePreferences(pref: UserPreferenceEntity) {
        dao.saveUserPreference(pref)
    }

    suspend fun toggleFollowExam(examId: String) {
        val currentPref = dao.getUserPreference().firstOrNull() ?: UserPreferenceEntity()
        val currentFollowed = currentPref.followedExamIds.split(",").filter { it.isNotBlank() }.toMutableSet()

        if (currentFollowed.contains(examId)) {
            currentFollowed.remove(examId)
            // Also remove from dream if unfollowed
            val currentDream = currentPref.dreamExamIds.split(",").filter { it.isNotBlank() }.toMutableSet()
            currentDream.remove(examId)
            dao.saveUserPreference(
                currentPref.copy(
                    followedExamIds = currentFollowed.joinToString(","),
                    dreamExamIds = currentDream.joinToString(",")
                )
            )
        } else {
            currentFollowed.add(examId)
            dao.saveUserPreference(currentPref.copy(followedExamIds = currentFollowed.joinToString(",")))
        }
    }

    suspend fun toggleDreamExam(examId: String): Boolean {
        val currentPref = dao.getUserPreference().firstOrNull() ?: UserPreferenceEntity()
        val currentDream = currentPref.dreamExamIds.split(",").filter { it.isNotBlank() }.toMutableSet()
        val currentFollowed = currentPref.followedExamIds.split(",").filter { it.isNotBlank() }.toMutableSet()

        if (currentDream.contains(examId)) {
            currentDream.remove(examId)
            dao.saveUserPreference(currentPref.copy(dreamExamIds = currentDream.joinToString(",")))
            return true
        } else {
            // Check max 3 dream exams rule
            if (currentDream.size >= 3) {
                return false // limit reached
            }
            currentDream.add(examId)
            currentFollowed.add(examId) // automatically follow dream exam
            dao.saveUserPreference(
                currentPref.copy(
                    dreamExamIds = currentDream.joinToString(","),
                    followedExamIds = currentFollowed.joinToString(",")
                )
            )
            return true
        }
    }

    suspend fun updateApplicationDetails(
        examId: String,
        isApplied: Boolean,
        isFeePaid: Boolean,
        isAdmitCardDownloaded: Boolean,
        appNumber: String,
        loginId: String,
        notes: String
    ) {
        dao.saveApplication(
            UserApplicationEntity(
                examId = examId,
                isApplied = isApplied,
                isFeePaid = isFeePaid,
                isAdmitCardDownloaded = isAdmitCardDownloaded,
                applicationNumber = appNumber,
                loginId = loginId,
                personalNotes = notes,
                lastUpdatedMs = System.currentTimeMillis()
            )
        )
    }

    suspend fun sendSimulatedNotification(
        examId: String,
        examShortCode: String,
        title: String,
        message: String,
        isDream: Boolean = false
    ) {
        dao.insertNotification(
            NotificationLogEntity(
                examId = examId,
                examShortCode = examShortCode,
                title = title,
                message = message,
                channel = if (isDream) "⭐ Dream Bypass Push" else "In-App FCM",
                isDreamExam = isDream,
                timestampMs = System.currentTimeMillis(),
                isRead = false
            )
        )
    }

    suspend fun markNotificationAsRead(id: Long) {
        dao.markNotificationAsRead(id)
    }

    suspend fun markAllNotificationsAsRead() {
        dao.markAllNotificationsAsRead()
    }

    // Admin CMS Functionality: Modify exam details, deadlines, and eligibility criteria
    fun adminUpdateExamStatus(examId: String, newStatus: ExamStatus, newMilestone: String, remainingHours: Long) {
        var updatedExamCode = ""
        var examTargetYear = 2026
        val updated = _exams.value.map { exam ->
            if (exam.id == examId) {
                updatedExamCode = exam.shortCode
                examTargetYear = exam.targetYear
                exam.copy(
                    currentStatus = newStatus,
                    nextMilestoneTitle = newMilestone,
                    nextMilestoneTimestampMs = System.currentTimeMillis() + remainingHours * 3600_000L
                )
            } else exam
        }
        _exams.value = updated

        // Dispatch Webhook & Delta Sync Event to Next.js & React Native clients
        val diffJson = """{"event": "deadline.extended", "examId": "$examId", "targetYear": $examTargetYear, "status": "${newStatus.name}", "milestone": "$newMilestone", "remainingHours": $remainingHours, "targetTimestamp": ${System.currentTimeMillis() + remainingHours * 3600_000L}}"""
        syncEngine.dispatchSyncEvent(
            eventType = SyncEventType.DEADLINE_EXTENDED,
            examShortCode = updatedExamCode.ifBlank { examId },
            summary = "Deadline updated to '$newMilestone' (${remainingHours}h left, Cycle $examTargetYear). Pushed to Next.js & React Native clients.",
            payloadDiffJson = diffJson,
            targetYear = examTargetYear
        )
    }

    suspend fun adminUpdateFullExam(
        examId: String,
        fullName: String,
        conductingBody: String,
        officialWebsite: String,
        applicationUrl: String,
        newStatus: ExamStatus,
        newMilestone: String,
        remainingHours: Long,
        min12thPercentage: String,
        compulsorySubjects: String,
        ageLimits: String,
        attemptLimit: String,
        generalFee: String,
        reservedFee: String,
        targetYear: Int = 2026
    ) {
        var updatedCode = ""
        val updated = _exams.value.map { exam ->
            if (exam.id == examId) {
                updatedCode = exam.shortCode
                val updatedFees = listOf(
                    FeeItem("General / OBC-NCL", generalFee),
                    FeeItem("SC / ST / PwD / Female", reservedFee)
                )
                val updatedEligibility = EligibilityDetails(
                    min12thPercentage = min12thPercentage,
                    ageLimits = ageLimits,
                    compulsorySubjects = compulsorySubjects,
                    attemptLimit = attemptLimit
                )
                exam.copy(
                    fullName = fullName,
                    conductingBody = conductingBody,
                    officialWebsite = officialWebsite,
                    applicationUrl = applicationUrl,
                    currentStatus = newStatus,
                    nextMilestoneTitle = newMilestone,
                    nextMilestoneTimestampMs = System.currentTimeMillis() + remainingHours * 3600_000L,
                    eligibilityDetails = updatedEligibility,
                    fees = updatedFees,
                    targetYear = targetYear
                )
            } else exam
        }
        _exams.value = updated

        // 1. Dispatch Webhook to Next.js and React Native with targetYear tag
        val payloadDiff = """{"event": "exam.full_update", "examId": "$examId", "targetYear": $targetYear, "name": "$fullName", "status": "${newStatus.name}", "milestone": "$newMilestone", "remainingHours": $remainingHours, "min12th": "$min12thPercentage", "fees": {"general": "$generalFee", "reserved": "$reservedFee"}}"""
        syncEngine.dispatchSyncEvent(
            eventType = SyncEventType.EXAM_UPDATED,
            examShortCode = updatedCode.ifBlank { examId },
            summary = "Admin CMS published full update for $updatedCode (Cycle $targetYear: Details, Deadlines & Eligibility).",
            payloadDiffJson = payloadDiff,
            targetYear = targetYear
        )

        // 2. Insert notification log for students
        dao.insertNotification(
            NotificationLogEntity(
                examId = examId,
                examShortCode = updatedCode.ifBlank { examId },
                title = "⚡ Official Update: $updatedCode ($targetYear)",
                message = "Exam details, deadlines, and eligibility criteria for $targetYear were updated by the examination authority.",
                channel = "Live Sync Broadcast",
                isDreamExam = false,
                timestampMs = System.currentTimeMillis(),
                isRead = false
            )
        )
    }

    suspend fun adminBroadcastAnnouncement(title: String, body: String, examShortCode: String = "ALL EXAMS") {
        val activeSyncYear = syncEngine.syncState.value.syncTargetYear
        dao.insertNotification(
            NotificationLogEntity(
                examId = "admin-broadcast",
                examShortCode = examShortCode,
                title = "📢 [Official Notice $activeSyncYear] $title",
                message = body,
                channel = "NTA / Exam Authority Broadcast",
                isDreamExam = true,
                timestampMs = System.currentTimeMillis(),
                isRead = false
            )
        )

        // Dispatch emergency broadcast to Next.js and React Native endpoints
        val payload = """{"event": "broadcast.emergency", "title": "$title", "body": "$body", "shortCode": "$examShortCode", "targetYear": $activeSyncYear, "timestamp": ${System.currentTimeMillis()}}"""
        syncEngine.dispatchSyncEvent(
            eventType = SyncEventType.EMERGENCY_BROADCAST,
            examShortCode = examShortCode,
            summary = "Emergency notice '$title' broadcasted across all channels (Cycle $activeSyncYear).",
            payloadDiffJson = payload,
            targetYear = activeSyncYear
        )
    }

    // Sync Controls & Diagnostics
    fun setSyncTargetYear(year: Int) {
        syncEngine.setSyncTargetYear(year)
    }
    suspend fun forceManualSync(): Long {
        return syncEngine.forceManualSync()
    }

    fun setSyncMode(mode: SyncMode) {
        syncEngine.setSyncMode(mode)
    }

    fun setPollingInterval(seconds: Int) {
        syncEngine.setPollingInterval(seconds)
    }

    fun setAdaptiveEnabled(enabled: Boolean) {
        syncEngine.setAdaptiveEnabled(enabled)
    }

    fun toggleWebhook(id: String) {
        syncEngine.toggleWebhookEnabled(id)
    }

    fun testWebhookPing(endpointId: String) {
        syncEngine.triggerTestPing(endpointId)
    }
}
