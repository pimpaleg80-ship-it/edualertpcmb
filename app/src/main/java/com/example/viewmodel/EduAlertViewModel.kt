package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.local.NotificationLogEntity
import com.example.data.local.UserApplicationEntity
import com.example.data.local.UserPreferenceEntity
import com.example.data.model.ExamCategory
import com.example.data.model.ExamItem
import com.example.data.model.ExamStatus
import com.example.data.model.StreamType
import com.example.data.repository.EduAlertRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

enum class ExamFilterChip {
    ALL,
    FOLLOWED_ONLY,
    DREAM_ONLY,
    REGISTRATION_OPEN,
    LAST_48_HOURS
}

data class EduAlertUiState(
    val currentTab: Int = 0, // 0: Exams Dashboard, 1: Application Tracker, 2: Alerts & Engine, 3: Profile
    val searchQuery: String = "",
    val selectedCategory: ExamCategory? = null, // null = All categories
    val selectedFilterChip: ExamFilterChip = ExamFilterChip.ALL,
    val selectedExamForDetails: ExamItem? = null,
    val isAdminCmsOpen: Boolean = false,
    val snackbarMessage: String? = null,
    val currentTimeMs: Long = System.currentTimeMillis()
)

class EduAlertViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: EduAlertRepository

    init {
        val db = AppDatabase.getDatabase(application)
        repository = EduAlertRepository(db.eduAlertDao(), viewModelScope)
    }

    val exams: StateFlow<List<ExamItem>> = repository.exams
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val userPreference: StateFlow<UserPreferenceEntity> = repository.userPreference
        .filterNotNull()
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            UserPreferenceEntity()
        )

    val applications: StateFlow<List<UserApplicationEntity>> = repository.allApplications
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val notifications: StateFlow<List<NotificationLogEntity>> = repository.allNotifications
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val syncState: StateFlow<com.example.data.sync.SyncState> = repository.syncState

    private val _uiState = MutableStateFlow(EduAlertUiState())
    val uiState: StateFlow<EduAlertUiState> = _uiState.asStateFlow()

    init {
        // Ticker for real-time countdown updates every 1 second
        viewModelScope.launch {
            while (isActive) {
                delay(1000)
                _uiState.update { it.copy(currentTimeMs = System.currentTimeMillis()) }
            }
        }
    }

    fun setTab(index: Int) {
        _uiState.update { it.copy(currentTab = index) }
    }

    fun setSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun selectCategory(category: ExamCategory?) {
        _uiState.update { it.copy(selectedCategory = category) }
    }

    fun selectFilterChip(filter: ExamFilterChip) {
        _uiState.update { it.copy(selectedFilterChip = filter) }
    }

    fun openExamDetails(exam: ExamItem?) {
        _uiState.update { it.copy(selectedExamForDetails = exam) }
    }

    fun toggleAdminCms(isOpen: Boolean) {
        _uiState.update { it.copy(isAdminCmsOpen = isOpen) }
    }

    fun clearSnackbar() {
        _uiState.update { it.copy(snackbarMessage = null) }
    }

    fun showMessage(message: String) {
        _uiState.update { it.copy(snackbarMessage = message) }
    }

    fun toggleFollowExam(examId: String) {
        viewModelScope.launch {
            repository.toggleFollowExam(examId)
            val currentPref = userPreference.value
            val isNowFollowed = !currentPref.followedExamIds.split(",").contains(examId)
            showMessage(if (isNowFollowed) "Added to Followed Exams" else "Removed from Followed Exams")
        }
    }

    fun toggleDreamExam(exam: ExamItem) {
        viewModelScope.launch {
            val success = repository.toggleDreamExam(exam.id)
            if (!success) {
                showMessage("Maximum 3 Dream Exams allowed! Unstar another exam first.")
            } else {
                val isDream = userPreference.value.dreamExamIds.split(",").contains(exam.id)
                showMessage(
                    if (!isDream) "⭐ Marked as Dream Exam (High-Priority Alert Bypass)"
                    else "Removed from Dream Exams"
                )
            }
        }
    }

    fun updatePersonalChecklist(
        examId: String,
        isApplied: Boolean,
        isFeePaid: Boolean,
        isAdmitCardDownloaded: Boolean,
        appNumber: String,
        loginId: String,
        notes: String
    ) {
        viewModelScope.launch {
            repository.updateApplicationDetails(
                examId = examId,
                isApplied = isApplied,
                isFeePaid = isFeePaid,
                isAdmitCardDownloaded = isAdmitCardDownloaded,
                appNumber = appNumber,
                loginId = loginId,
                notes = notes
            )
            showMessage("Application details saved locally!")
        }
    }

    fun updateStream(stream: String) {
        viewModelScope.launch {
            repository.savePreferences(userPreference.value.copy(stream = stream))
            showMessage("Stream updated to $stream")
        }
    }

    fun updateHomeState(state: String) {
        viewModelScope.launch {
            repository.savePreferences(userPreference.value.copy(homeState = state))
            showMessage("Domicile State set to $state")
        }
    }

    fun updateCareerPathway(pathway: String) {
        viewModelScope.launch {
            repository.savePreferences(userPreference.value.copy(targetPathway = pathway))
        }
    }

    fun updateNotificationSettings(push: Boolean, whatsapp: Boolean, dndBypass: Boolean) {
        viewModelScope.launch {
            repository.savePreferences(
                userPreference.value.copy(
                    pushEnabled = push,
                    whatsappEnabled = whatsapp,
                    dndBypassDreamExams = dndBypass
                )
            )
            showMessage("Notification channels updated")
        }
    }

    fun simulateTriggerAlert(exam: ExamItem, triggerType: String) {
        viewModelScope.launch {
            val isDream = userPreference.value.dreamExamIds.split(",").contains(exam.id)
            val title = when (triggerType) {
                "T-48H" -> "🚨 Last 48 Hours: ${exam.shortCode} Registration Closes"
                "T-7D" -> "⏳ 1 Week Left: ${exam.shortCode} Deadline"
                "ADMIT_CARD" -> "🎫 Admit Card Released: ${exam.shortCode}"
                "ANSWER_KEY" -> "📝 Answer Key Live: ${exam.shortCode}"
                else -> "📢 Official Update: ${exam.shortCode}"
            }
            val message = when (triggerType) {
                "T-48H" -> "Final hours remaining before registration portal shuts. Complete fee payment now at ${exam.officialWebsite}."
                "ADMIT_CARD" -> "Hall ticket is now downloadable using your application number & password."
                else -> "Check official updates and eligibility instructions on ${exam.officialWebsite}."
            }

            repository.sendSimulatedNotification(
                examId = exam.id,
                examShortCode = exam.shortCode,
                title = title,
                message = message,
                isDream = isDream
            )
            showMessage("Alert dispatched: $title")
        }
    }

    fun markNotificationAsRead(id: Long) {
        viewModelScope.launch {
            repository.markNotificationAsRead(id)
        }
    }

    fun markAllNotificationsAsRead() {
        viewModelScope.launch {
            repository.markAllNotificationsAsRead()
            showMessage("All notifications marked as read")
        }
    }

    // Admin CMS Actions
    fun adminUpdateExam(examId: String, newStatus: ExamStatus, newMilestone: String, remainingHours: Long) {
        repository.adminUpdateExamStatus(examId, newStatus, newMilestone, remainingHours)
        showMessage("Exam deadline updated & pushed to Webhook endpoints")
    }

    fun adminUpdateFullExam(
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
        reservedFee: String
    ) {
        viewModelScope.launch {
            repository.adminUpdateFullExam(
                examId = examId,
                fullName = fullName,
                conductingBody = conductingBody,
                officialWebsite = officialWebsite,
                applicationUrl = applicationUrl,
                newStatus = newStatus,
                newMilestone = newMilestone,
                remainingHours = remainingHours,
                min12thPercentage = min12thPercentage,
                compulsorySubjects = compulsorySubjects,
                ageLimits = ageLimits,
                attemptLimit = attemptLimit,
                generalFee = generalFee,
                reservedFee = reservedFee
            )
            showMessage("Published! Details, deadlines & eligibility pushed to clients.")
        }
    }

    fun adminBroadcast(title: String, body: String, examShortCode: String) {
        viewModelScope.launch {
            repository.adminBroadcastAnnouncement(title, body, examShortCode)
            showMessage("Emergency Notice broadcasted & pushed to all clients!")
        }
    }

    // Synchronization Management Actions
    fun triggerManualSync() {
        viewModelScope.launch {
            val latency = repository.forceManualSync()
            showMessage("⚡ Synced with central registry (${latency}ms latency)")
        }
    }

    fun setSyncMode(mode: com.example.data.sync.SyncMode) {
        repository.setSyncMode(mode)
        showMessage("Sync strategy set to: ${mode.label}")
    }

    fun setPollingInterval(seconds: Int) {
        repository.setPollingInterval(seconds)
        showMessage("Delta polling interval set to ${seconds}s")
    }

    fun toggleAdaptivePolling(enabled: Boolean) {
        repository.setAdaptiveEnabled(enabled)
        showMessage(if (enabled) "Adaptive polling enabled (15s during Last 48h)" else "Adaptive polling disabled")
    }

    fun toggleWebhook(endpointId: String) {
        repository.toggleWebhook(endpointId)
    }

    fun testWebhookPing(endpointId: String) {
        repository.testWebhookPing(endpointId)
        showMessage("Webhook ping dispatched. Validating endpoint response...")
    }
}
