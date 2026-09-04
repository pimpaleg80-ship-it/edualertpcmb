package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.ExamItem
import com.example.data.model.ExamStatus
import com.example.data.sync.SyncEngine
import com.example.data.sync.SyncMode
import com.example.data.sync.SyncState
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminCmsDialog(
    exams: List<ExamItem>,
    syncState: SyncState,
    onUpdateFullExam: (
        examId: String,
        fullName: String,
        conductingBody: String,
        officialWebsite: String,
        applicationUrl: String,
        status: ExamStatus,
        milestone: String,
        remainingHours: Long,
        min12thPercentage: String,
        compulsorySubjects: String,
        ageLimits: String,
        attemptLimit: String,
        generalFee: String,
        reservedFee: String
    ) -> Unit,
    onUpdateExamStatus: (examId: String, status: ExamStatus, milestone: String, remainingHours: Long) -> Unit,
    onBroadcastAnnouncement: (title: String, message: String, shortCode: String) -> Unit,
    onSetSyncMode: (SyncMode) -> Unit,
    onSetPollingInterval: (Int) -> Unit,
    onToggleAdaptivePolling: (Boolean) -> Unit,
    onToggleWebhook: (String) -> Unit,
    onTestWebhookPing: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedExamId by remember { mutableStateOf(exams.firstOrNull()?.id ?: "") }
    val currentExam = exams.find { it.id == selectedExamId } ?: exams.firstOrNull()

    // Core Details
    var fullNameText by remember(selectedExamId) { mutableStateOf(currentExam?.fullName ?: "") }
    var conductingBodyText by remember(selectedExamId) { mutableStateOf(currentExam?.conductingBody ?: "") }
    var websiteText by remember(selectedExamId) { mutableStateOf(currentExam?.officialWebsite ?: "") }
    var applicationUrlText by remember(selectedExamId) { mutableStateOf(currentExam?.applicationUrl ?: "") }

    // Deadlines & Milestones
    var selectedStatus by remember(selectedExamId) { mutableStateOf(currentExam?.currentStatus ?: ExamStatus.REGISTRATION_OPEN) }
    var milestoneText by remember(selectedExamId) { mutableStateOf(currentExam?.nextMilestoneTitle ?: "") }
    var remainingHoursText by remember(selectedExamId) {
        val hrs = currentExam?.let {
            ((it.nextMilestoneTimestampMs - System.currentTimeMillis()) / 3600_000L).coerceAtLeast(1)
        } ?: 24L
        mutableStateOf(hrs.toString())
    }

    // Eligibility & Fees
    var min12thText by remember(selectedExamId) {
        mutableStateOf(currentExam?.eligibilityDetails?.min12thPercentage ?: "75% aggregate in 12th Board")
    }
    var compulsorySubjectsText by remember(selectedExamId) {
        mutableStateOf(currentExam?.eligibilityDetails?.compulsorySubjects ?: "Physics & Mathematics mandatory")
    }
    var ageLimitsText by remember(selectedExamId) {
        mutableStateOf(currentExam?.eligibilityDetails?.ageLimits ?: "No age bar")
    }
    var attemptLimitText by remember(selectedExamId) {
        mutableStateOf(currentExam?.eligibilityDetails?.attemptLimit ?: "Max 3 consecutive attempts")
    }
    var generalFeeText by remember(selectedExamId) {
        val fee = currentExam?.fees?.firstOrNull { it.categoryLabel.contains("General", ignoreCase = true) }?.amount ?: "₹1,000"
        mutableStateOf(fee)
    }
    var reservedFeeText by remember(selectedExamId) {
        val fee = currentExam?.fees?.firstOrNull { it.categoryLabel.contains("SC", ignoreCase = true) }?.amount ?: "₹500"
        mutableStateOf(fee)
    }

    // Broadcast Notice Fields
    var broadcastTitle by remember { mutableStateOf("NTA Portal Server Notice: Deadline Extended by 48 Hours") }
    var broadcastBody by remember { mutableStateOf("Due to intense traffic surges, the registration window has been officially extended. Candidates may pay fees without late penalty.") }

    var selectedTab by remember { mutableStateOf(0) } // 0: Details & Deadlines, 1: Eligibility & Fees, 2: Real-time Sync & Webhooks, 3: Client Integration Code
    var showExamDropdown by remember { mutableStateOf(false) }
    var codeSnippetTab by remember { mutableStateOf(0) } // 0: Next.js, 1: React Native

    val timeFormatter = remember { SimpleDateFormat("HH:mm:ss", Locale.getDefault()) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.92f)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
            ) {
                // Top Header with Sync Strategy Status Badge
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.AdminPanelSettings,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Admin CMS & Sync Console",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.ExtraBold
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(7.dp)
                                        .background(Color(0xFF16A34A), CircleShape)
                                )
                                Spacer(modifier = Modifier.width(5.dp))
                                Text(
                                    text = "Cross-Platform Sync: ${syncState.mode.label.take(20)}...",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Scrollable Tabs
                ScrollableTabRow(
                    selectedTabIndex = selectedTab,
                    edgePadding = 0.dp,
                    divider = {}
                ) {
                    Tab(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        text = { Text("1. Details & Dates", fontSize = 12.sp, fontWeight = FontWeight.SemiBold) }
                    )
                    Tab(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        text = { Text("2. Eligibility & Fees", fontSize = 12.sp, fontWeight = FontWeight.SemiBold) }
                    )
                    Tab(
                        selected = selectedTab == 2,
                        onClick = { selectedTab = 2 },
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("3. Sync & Webhooks", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                                if (syncState.isSyncing) {
                                    Spacer(modifier = Modifier.width(4.dp))
                                    CircularProgressIndicator(modifier = Modifier.size(10.dp), strokeWidth = 1.5.dp)
                                }
                            }
                        }
                    )
                    Tab(
                        selected = selectedTab == 3,
                        onClick = { selectedTab = 3 },
                        text = { Text("4. Client SDKs", fontSize = 12.sp, fontWeight = FontWeight.SemiBold) }
                    )
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp))

                // Tab Content Area (Vertical Scroll)
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                ) {
                    // Shared Exam Selector (visible in Tabs 0 & 1)
                    if (selectedTab == 0 || selectedTab == 1) {
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = "Target Examination",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                ExposedDropdownMenuBox(
                                    expanded = showExamDropdown,
                                    onExpandedChange = { showExamDropdown = !showExamDropdown }
                                ) {
                                    OutlinedTextField(
                                        value = "${currentExam?.shortCode ?: ""} - ${currentExam?.fullName ?: "Select Exam"}",
                                        onValueChange = {},
                                        readOnly = true,
                                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = showExamDropdown) },
                                        modifier = Modifier
                                            .menuAnchor()
                                            .fillMaxWidth(),
                                        shape = RoundedCornerShape(8.dp),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedContainerColor = MaterialTheme.colorScheme.surface,
                                            unfocusedContainerColor = MaterialTheme.colorScheme.surface
                                        )
                                    )

                                    ExposedDropdownMenu(
                                        expanded = showExamDropdown,
                                        onDismissRequest = { showExamDropdown = false }
                                    ) {
                                        exams.forEach { exam ->
                                            DropdownMenuItem(
                                                text = {
                                                    Column {
                                                        Text("${exam.shortCode} • ${exam.category.shortLabel}", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                                        Text(exam.fullName, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                                    }
                                                },
                                                onClick = {
                                                    selectedExamId = exam.id
                                                    showExamDropdown = false
                                                }
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))
                    }

                    when (selectedTab) {
                        0 -> {
                            // TAB 0: EXAM DETAILS & DEADLINES
                            Text(
                                text = "Examination Core Information",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(8.dp))

                            OutlinedTextField(
                                value = fullNameText,
                                onValueChange = { fullNameText = it },
                                label = { Text("Full Examination Name") },
                                singleLine = true,
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            OutlinedTextField(
                                value = conductingBodyText,
                                onValueChange = { conductingBodyText = it },
                                label = { Text("Conducting Body (e.g. NTA, State CET Cell)") },
                                singleLine = true,
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                OutlinedTextField(
                                    value = websiteText,
                                    onValueChange = { websiteText = it },
                                    label = { Text("Official Portal URL") },
                                    singleLine = true,
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.weight(1f)
                                )
                                OutlinedTextField(
                                    value = applicationUrlText,
                                    onValueChange = { applicationUrlText = it },
                                    label = { Text("Direct Apply Link") },
                                    singleLine = true,
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.weight(1f)
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Text(
                                text = "Deadline Milestone & Status",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(6.dp))

                            // Status Radio Grid
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                ExamStatus.values().forEach { status ->
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable { selectedStatus = status }
                                            .padding(vertical = 2.dp)
                                    ) {
                                        RadioButton(
                                            selected = selectedStatus == status,
                                            onClick = { selectedStatus = status }
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(text = status.label, fontSize = 13.sp)
                                        if (status == ExamStatus.LAST_48_HOURS) {
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Box(
                                                modifier = Modifier
                                                    .background(Color(0xFFDC2626), RoundedCornerShape(4.dp))
                                                    .padding(horizontal = 5.dp, vertical = 1.dp)
                                            ) {
                                                Text("High Urgency", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                            }
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            OutlinedTextField(
                                value = milestoneText,
                                onValueChange = { milestoneText = it },
                                label = { Text("Active Milestone Label (e.g. Registration Closes)") },
                                singleLine = true,
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                OutlinedTextField(
                                    value = remainingHoursText,
                                    onValueChange = { remainingHoursText = it },
                                    label = { Text("Countdown (Remaining Hours)") },
                                    singleLine = true,
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.weight(1f)
                                )

                                // Quick Extension Presets
                                Button(
                                    onClick = { remainingHoursText = "48"; selectedStatus = ExamStatus.LAST_48_HOURS },
                                    shape = RoundedCornerShape(8.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondaryContainer, contentColor = MaterialTheme.colorScheme.onSecondaryContainer)
                                ) {
                                    Text("+48h Ext", fontSize = 11.sp)
                                }

                                Button(
                                    onClick = { remainingHoursText = "168" }, // 7 days
                                    shape = RoundedCornerShape(8.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondaryContainer, contentColor = MaterialTheme.colorScheme.onSecondaryContainer)
                                ) {
                                    Text("+7 Days", fontSize = 11.sp)
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // Action Button: Save & Push
                            Button(
                                onClick = {
                                    val hrs = remainingHoursText.toLongOrNull() ?: 24L
                                    onUpdateFullExam(
                                        selectedExamId,
                                        fullNameText,
                                        conductingBodyText,
                                        websiteText,
                                        applicationUrlText,
                                        selectedStatus,
                                        milestoneText,
                                        hrs,
                                        min12thText,
                                        compulsorySubjectsText,
                                        ageLimitsText,
                                        attemptLimitText,
                                        generalFeeText,
                                        reservedFeeText
                                    )
                                    onDismiss()
                                },
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(imageVector = Icons.Default.Sync, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Publish & Push Update to Clients", fontWeight = FontWeight.Bold)
                            }
                        }

                        1 -> {
                            // TAB 1: ELIGIBILITY & FEES
                            Text(
                                text = "Academic Eligibility & Subject Rules",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(8.dp))

                            OutlinedTextField(
                                value = min12thText,
                                onValueChange = { min12thText = it },
                                label = { Text("Minimum 12th Board Percentage") },
                                singleLine = true,
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            OutlinedTextField(
                                value = compulsorySubjectsText,
                                onValueChange = { compulsorySubjectsText = it },
                                label = { Text("Compulsory Subjects (PCM/PCB Combinations)") },
                                singleLine = true,
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            OutlinedTextField(
                                value = ageLimitsText,
                                onValueChange = { ageLimitsText = it },
                                label = { Text("Age Cutoffs & Year of Passing") },
                                singleLine = true,
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            OutlinedTextField(
                                value = attemptLimitText,
                                onValueChange = { attemptLimitText = it },
                                label = { Text("Maximum Attempts Allowed") },
                                singleLine = true,
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            Text(
                                text = "Category-Wise Application Fee Slabs",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(8.dp))

                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                OutlinedTextField(
                                    value = generalFeeText,
                                    onValueChange = { generalFeeText = it },
                                    label = { Text("General / OBC Fee") },
                                    singleLine = true,
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.weight(1f)
                                )
                                OutlinedTextField(
                                    value = reservedFeeText,
                                    onValueChange = { reservedFeeText = it },
                                    label = { Text("SC / ST / PwD / Female Fee") },
                                    singleLine = true,
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.weight(1f)
                                )
                            }

                            Spacer(modifier = Modifier.height(20.dp))

                            Button(
                                onClick = {
                                    val hrs = remainingHoursText.toLongOrNull() ?: 24L
                                    onUpdateFullExam(
                                        selectedExamId,
                                        fullNameText,
                                        conductingBodyText,
                                        websiteText,
                                        applicationUrlText,
                                        selectedStatus,
                                        milestoneText,
                                        hrs,
                                        min12thText,
                                        compulsorySubjectsText,
                                        ageLimitsText,
                                        attemptLimitText,
                                        generalFeeText,
                                        reservedFeeText
                                    )
                                    onDismiss()
                                },
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(imageVector = Icons.Default.CloudUpload, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Publish Eligibility & Sync to Apps", fontWeight = FontWeight.Bold)
                            }
                        }

                        2 -> {
                            // TAB 2: DATA SYNC & WEBHOOKS CONSOLE
                            Text(
                                text = "Data Synchronization Architecture",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Configure how exam mutations are propagated to React Native and Next.js clients with minimal latency.",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            // Sync Mode Selector
                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                SyncMode.values().forEach { mode ->
                                    Card(
                                        shape = RoundedCornerShape(10.dp),
                                        colors = CardDefaults.cardColors(
                                            containerColor = if (syncState.mode == mode)
                                                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                                            else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                                        ),
                                        border = if (syncState.mode == mode)
                                            BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary)
                                        else null,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable { onSetSyncMode(mode) }
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            modifier = Modifier.padding(10.dp)
                                        ) {
                                            RadioButton(
                                                selected = syncState.mode == mode,
                                                onClick = { onSetSyncMode(mode) }
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Column {
                                                Text(mode.label, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                                Text(mode.description, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                            }
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // Polling & Delta Frequency Settings
                            Card(
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Column {
                                            Text("Adaptive Delta Polling", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                            Text("Accelerates polling to 15s during Last 48 Hours deadlines", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        }
                                        Switch(
                                            checked = syncState.isAdaptive,
                                            onCheckedChange = { onToggleAdaptivePolling(it) }
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text("Standard Polling Interval", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                                        modifier = Modifier.padding(top = 4.dp)
                                    ) {
                                        listOf(15 to "15s", 30 to "30s", 60 to "60s", 300 to "5m").forEach { (sec, label) ->
                                            FilterChip(
                                                selected = syncState.pollingIntervalSeconds == sec,
                                                onClick = { onSetPollingInterval(sec) },
                                                label = { Text(label, fontSize = 11.sp) }
                                            )
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // Registered Client Webhook Endpoints
                            Text(
                                text = "Registered Client Webhooks (Push Endpoints)",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(6.dp))

                            syncState.registeredWebhooks.forEach { endpoint ->
                                Card(
                                    shape = RoundedCornerShape(10.dp),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp)
                                ) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Column(modifier = Modifier.weight(1f)) {
                                                Text(endpoint.name, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                                Text(endpoint.clientPlatform, fontSize = 10.sp, color = MaterialTheme.colorScheme.primary)
                                            }
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Box(
                                                    modifier = Modifier
                                                        .background(
                                                            if (endpoint.isEnabled) Color(0xFFDCFCE7) else Color(0xFFF3F4F6),
                                                            RoundedCornerShape(4.dp)
                                                        )
                                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                                ) {
                                                    Text(
                                                        text = if (endpoint.isEnabled) "${endpoint.lastLatencyMs}ms • 200 OK" else "Disabled",
                                                        fontSize = 10.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = if (endpoint.isEnabled) Color(0xFF166534) else Color.Gray
                                                    )
                                                }
                                                Spacer(modifier = Modifier.width(6.dp))
                                                FilledTonalButton(
                                                    onClick = { onTestWebhookPing(endpoint.id) },
                                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                                    modifier = Modifier.height(28.dp)
                                                ) {
                                                    Text("Ping", fontSize = 10.sp)
                                                }
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(6.dp))
                                        Text(
                                            text = endpoint.url,
                                            fontFamily = FontFamily.Monospace,
                                            fontSize = 10.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        Text(
                                            text = "Secret: ${endpoint.secretToken} • Dispatches: ${endpoint.totalDispatches}",
                                            fontFamily = FontFamily.Monospace,
                                            fontSize = 9.sp,
                                            color = MaterialTheme.colorScheme.outline
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // Emergency Broadcast Accordion
                            Card(
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF2F2)),
                                border = BorderStroke(1.dp, Color(0xFFFECACA)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(imageVector = Icons.Default.Campaign, contentDescription = null, tint = Color(0xFFDC2626), modifier = Modifier.size(20.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("Emergency Authority Notice Broadcast", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Color(0xFF991B1B))
                                    }
                                    Spacer(modifier = Modifier.height(6.dp))
                                    OutlinedTextField(
                                        value = broadcastTitle,
                                        onValueChange = { broadcastTitle = it },
                                        label = { Text("Headline") },
                                        singleLine = true,
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    OutlinedTextField(
                                        value = broadcastBody,
                                        onValueChange = { broadcastBody = it },
                                        label = { Text("Announcement Body") },
                                        maxLines = 3,
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Button(
                                        onClick = {
                                            onBroadcastAnnouncement(broadcastTitle, broadcastBody, currentExam?.shortCode ?: "ALL")
                                            onDismiss()
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626)),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text("Broadcast Emergency Notice via Webhooks", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // Live Sync Audit Stream
                            Text(
                                text = "Live Sync & Webhook Delivery Logs",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(6.dp))

                            syncState.auditLogs.take(6).forEach { log ->
                                Card(
                                    shape = RoundedCornerShape(8.dp),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.25f)),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 3.dp)
                                ) {
                                    Column(modifier = Modifier.padding(8.dp)) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            modifier = Modifier.fillMaxWidth()
                                        ) {
                                            Text(
                                                text = "${log.eventType.title} [${log.examShortCode}]",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 11.sp
                                            )
                                            Text(
                                                text = "${timeFormatter.format(Date(log.timestampMs))} • ${log.latencyMs}ms",
                                                fontSize = 10.sp,
                                                color = MaterialTheme.colorScheme.outline
                                            )
                                        }
                                        Text(
                                            text = "Target: ${log.targetClient} • Status: ${log.httpStatusCode} OK",
                                            fontSize = 10.sp,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                        Text(
                                            text = log.summary,
                                            fontSize = 10.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        }

                        3 -> {
                            // TAB 3: NEXT.JS & REACT NATIVE CODE TEMPLATES
                            Text(
                                text = "Client Application Integration SDKs",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Plug-and-play implementations for Next.js web portals and React Native mobile clients.",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                FilterChip(
                                    selected = codeSnippetTab == 0,
                                    onClick = { codeSnippetTab = 0 },
                                    label = { Text("Next.js 15 (Route Handler)") }
                                )
                                FilterChip(
                                    selected = codeSnippetTab == 1,
                                    onClick = { codeSnippetTab = 1 },
                                    label = { Text("React Native (Sync Hook)") }
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            val codeSnippet = if (codeSnippetTab == 0) {
                                SyncEngine.getNextJsWebhookSnippet()
                            } else {
                                SyncEngine.getReactNativeSyncSnippet()
                            }

                            Card(
                                shape = RoundedCornerShape(10.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text(
                                            text = if (codeSnippetTab == 0) "app/api/webhooks/edualert/route.ts" else "hooks/useEduAlertSync.ts",
                                            fontFamily = FontFamily.Monospace,
                                            color = Color(0xFF94A3B8),
                                            fontSize = 10.sp
                                        )
                                        Text(
                                            text = "HMAC Verified",
                                            color = Color(0xFF38BDF8),
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = codeSnippet,
                                        fontFamily = FontFamily.Monospace,
                                        color = Color(0xFFE2E8F0),
                                        fontSize = 10.sp,
                                        lineHeight = 14.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
