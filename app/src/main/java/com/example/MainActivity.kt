package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.screens.*
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.EduAlertViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: EduAlertViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                EduAlertApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun EduAlertApp(viewModel: EduAlertViewModel) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val exams by viewModel.exams.collectAsStateWithLifecycle()
    val userPreference by viewModel.userPreference.collectAsStateWithLifecycle()
    val applications by viewModel.applications.collectAsStateWithLifecycle()
    val notifications by viewModel.notifications.collectAsStateWithLifecycle()
    val syncState by viewModel.syncState.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.snackbarMessage) {
        uiState.snackbarMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearSnackbar()
        }
    }

    val unreadNotifsCount = notifications.count { !it.isRead }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 8.dp
            ) {
                NavigationBarItem(
                    selected = uiState.currentTab == 0,
                    onClick = { viewModel.setTab(0) },
                    icon = {
                        Icon(
                            imageVector = if (uiState.currentTab == 0) Icons.Filled.DateRange else Icons.Outlined.DateRange,
                            contentDescription = "Exams"
                        )
                    },
                    label = { Text("Exams", fontSize = 11.sp) }
                )

                NavigationBarItem(
                    selected = uiState.currentTab == 1,
                    onClick = { viewModel.setTab(1) },
                    icon = {
                        Icon(
                            imageVector = if (uiState.currentTab == 1) Icons.Filled.FactCheck else Icons.Outlined.FactCheck,
                            contentDescription = "Tracker"
                        )
                    },
                    label = { Text("Tracker", fontSize = 11.sp) }
                )

                NavigationBarItem(
                    selected = uiState.currentTab == 2,
                    onClick = { viewModel.setTab(2) },
                    icon = {
                        BadgedBox(
                            badge = {
                                if (unreadNotifsCount > 0) {
                                    Badge { Text(unreadNotifsCount.toString()) }
                                }
                            }
                        ) {
                            Icon(
                                imageVector = if (uiState.currentTab == 2) Icons.Filled.Notifications else Icons.Outlined.Notifications,
                                contentDescription = "Alerts"
                            )
                        }
                    },
                    label = { Text("Alerts", fontSize = 11.sp) }
                )

                NavigationBarItem(
                    selected = uiState.currentTab == 3,
                    onClick = { viewModel.setTab(3) },
                    icon = {
                        Icon(
                            imageVector = if (uiState.currentTab == 3) Icons.Filled.Person else Icons.Outlined.Person,
                            contentDescription = "Profile"
                        )
                    },
                    label = { Text("Profile", fontSize = 11.sp) }
                )
            }
        }
    ) { innerPadding ->
        when (uiState.currentTab) {
            0 -> DashboardScreen(
                exams = exams,
                userPreference = userPreference,
                syncState = syncState,
                searchQuery = uiState.searchQuery,
                selectedCategory = uiState.selectedCategory,
                selectedFilterChip = uiState.selectedFilterChip,
                currentTimeMs = uiState.currentTimeMs,
                onSearchQueryChange = { viewModel.setSearchQuery(it) },
                onSelectCategory = { viewModel.selectCategory(it) },
                onSelectFilterChip = { viewModel.selectFilterChip(it) },
                onToggleFollow = { viewModel.toggleFollowExam(it) },
                onToggleDream = { viewModel.toggleDreamExam(it) },
                onOpenDetails = { viewModel.openExamDetails(it) },
                onTriggerAlert = { exam, type -> viewModel.simulateTriggerAlert(exam, type) },
                onTriggerManualSync = { viewModel.triggerManualSync() },
                onOpenAdminCms = { viewModel.toggleAdminCms(true) },
                modifier = Modifier.padding(innerPadding)
            )
            1 -> TrackerScreen(
                exams = exams,
                userPreference = userPreference,
                applications = applications,
                onOpenExamDetails = { viewModel.openExamDetails(it) },
                onSaveApplicationData = { examId, applied, fee, admit, appNo, login, notes ->
                    viewModel.updatePersonalChecklist(examId, applied, fee, admit, appNo, login, notes)
                },
                modifier = Modifier.padding(innerPadding)
            )
            2 -> AlertsScreen(
                notifications = notifications,
                userPreference = userPreference,
                exams = exams,
                onUpdateNotificationSettings = { push, wa, dnd ->
                    viewModel.updateNotificationSettings(push, wa, dnd)
                },
                onSimulateTrigger = { exam, type ->
                    viewModel.simulateTriggerAlert(exam, type)
                },
                onMarkAsRead = { viewModel.markNotificationAsRead(it) },
                onMarkAllAsRead = { viewModel.markAllNotificationsAsRead() },
                modifier = Modifier.padding(innerPadding)
            )
            3 -> ProfileScreen(
                userPreference = userPreference,
                exams = exams,
                onUpdateStream = { viewModel.updateStream(it) },
                onUpdateHomeState = { viewModel.updateHomeState(it) },
                onUpdatePathway = { viewModel.updateCareerPathway(it) },
                onOpenAdminCms = { viewModel.toggleAdminCms(true) },
                modifier = Modifier.padding(innerPadding)
            )
        }

        // Exam Details Sheet
        uiState.selectedExamForDetails?.let { exam ->
            val appData = applications.find { it.examId == exam.id }
            val isDream = userPreference.dreamExamIds.split(",").contains(exam.id)
            val isFollowed = userPreference.followedExamIds.split(",").contains(exam.id)

            ExamDetailSheet(
                exam = exam,
                applicationData = appData,
                isDream = isDream,
                isFollowed = isFollowed,
                onToggleDream = { viewModel.toggleDreamExam(exam) },
                onToggleFollow = { viewModel.toggleFollowExam(exam.id) },
                onSaveApplicationData = { applied, fee, admit, appNo, login, notes ->
                    viewModel.updatePersonalChecklist(exam.id, applied, fee, admit, appNo, login, notes)
                },
                onDismiss = { viewModel.openExamDetails(null) }
            )
        }

        // Admin CMS Dialog
        if (uiState.isAdminCmsOpen) {
            AdminCmsDialog(
                exams = exams,
                syncState = syncState,
                onUpdateFullExam = { id, name, body, web, appUrl, status, milestone, hrs, min12, comp, age, att, genFee, resFee ->
                    viewModel.adminUpdateFullExam(
                        examId = id,
                        fullName = name,
                        conductingBody = body,
                        officialWebsite = web,
                        applicationUrl = appUrl,
                        newStatus = status,
                        newMilestone = milestone,
                        remainingHours = hrs,
                        min12thPercentage = min12,
                        compulsorySubjects = comp,
                        ageLimits = age,
                        attemptLimit = att,
                        generalFee = genFee,
                        reservedFee = resFee
                    )
                },
                onUpdateExamStatus = { examId, status, milestone, hrs ->
                    viewModel.adminUpdateExam(examId, status, milestone, hrs)
                },
                onBroadcastAnnouncement = { title, body, code ->
                    viewModel.adminBroadcast(title, body, code)
                },
                onSetSyncMode = { viewModel.setSyncMode(it) },
                onSetPollingInterval = { viewModel.setPollingInterval(it) },
                onToggleAdaptivePolling = { viewModel.toggleAdaptivePolling(it) },
                onToggleWebhook = { viewModel.toggleWebhook(it) },
                onTestWebhookPing = { viewModel.testWebhookPing(it) },
                onDismiss = { viewModel.toggleAdminCms(false) }
            )
        }
    }
}
