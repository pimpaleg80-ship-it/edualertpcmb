package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.UserApplicationEntity
import com.example.data.local.UserPreferenceEntity
import com.example.data.model.ExamItem
import com.example.ui.components.CategoryPill
import com.example.ui.components.StatusPill

@Composable
fun TrackerScreen(
    exams: List<ExamItem>,
    userPreference: UserPreferenceEntity,
    applications: List<UserApplicationEntity>,
    onOpenExamDetails: (ExamItem) -> Unit,
    onSaveApplicationData: (examId: String, isApplied: Boolean, isFeePaid: Boolean, isAdmitDownloaded: Boolean, appNum: String, loginId: String, notes: String) -> Unit,
    modifier: Modifier = Modifier
) {
    val followedList = userPreference.followedExamIds.split(",").filter { it.isNotBlank() }
    val dreamList = userPreference.dreamExamIds.split(",").filter { it.isNotBlank() }

    // Map followed/dream exams
    val trackedExams = exams.filter { followedList.contains(it.id) || dreamList.contains(it.id) }

    val appMap = applications.associateBy { it.examId }

    val appliedCount = trackedExams.count { appMap[it.id]?.isApplied == true }
    val feePaidCount = trackedExams.count { appMap[it.id]?.isFeePaid == true }
    val admitDownloadedCount = trackedExams.count { appMap[it.id]?.isAdmitCardDownloaded == true }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 90.dp)
    ) {
        // Tracker Summary Card
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Personal Application Tracker",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Outlined.Lock,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Encrypted On-Device",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        TrackerStatBox("Tracked", trackedExams.size.toString(), Color(0xFF2563EB))
                        TrackerStatBox("Applied", "$appliedCount/${trackedExams.size}", Color(0xFF059669))
                        TrackerStatBox("Fee Paid", "$feePaidCount/${trackedExams.size}", Color(0xFFD97706))
                        TrackerStatBox("Admit Card", "$admitDownloadedCount/${trackedExams.size}", Color(0xFF7C3AED))
                    }
                }
            }
        }

        item {
            Text(
                text = "Tracked Examinations Checklist (${trackedExams.size})",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        if (trackedExams.isEmpty()) {
            item {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 24.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.BookmarkBorder,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(36.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "No exams currently in your tracker",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Go to the Exams tab and tap the bookmark or star button to track an exam.",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        } else {
            items(trackedExams, key = { it.id }) { exam ->
                val appData = appMap[exam.id]
                val isDream = dreamList.contains(exam.id)

                TrackerItemCard(
                    exam = exam,
                    appData = appData,
                    isDream = isDream,
                    onOpenDetails = { onOpenExamDetails(exam) },
                    onSave = { applied, fee, admit, appNo, login, notes ->
                        onSaveApplicationData(exam.id, applied, fee, admit, appNo, login, notes)
                    }
                )
            }
        }
    }
}

@Composable
private fun TrackerStatBox(label: String, value: String, accentColor: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value,
            fontSize = 18.sp,
            fontWeight = FontWeight.ExtraBold,
            color = accentColor
        )
        Text(
            text = label,
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun TrackerItemCard(
    exam: ExamItem,
    appData: UserApplicationEntity?,
    isDream: Boolean,
    onOpenDetails: () -> Unit,
    onSave: (Boolean, Boolean, Boolean, String, String, String) -> Unit
) {
    var isExpanded by remember { mutableStateOf(false) }

    var applied by remember(appData) { mutableStateOf(appData?.isApplied ?: false) }
    var feePaid by remember(appData) { mutableStateOf(appData?.isFeePaid ?: false) }
    var admitDownloaded by remember(appData) { mutableStateOf(appData?.isAdmitCardDownloaded ?: false) }
    var appNumber by remember(appData) { mutableStateOf(appData?.applicationNumber ?: "") }
    var loginId by remember(appData) { mutableStateOf(appData?.loginId ?: "") }
    var notes by remember(appData) { mutableStateOf(appData?.personalNotes ?: "") }

    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(
            1.dp,
            if (isDream) Color(0xFFF59E0B) else MaterialTheme.colorScheme.outline.copy(alpha = 0.25f)
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    CategoryPill(category = exam.category)
                    Spacer(modifier = Modifier.width(6.dp))
                    StatusPill(status = exam.currentStatus)
                }

                if (isDream) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = "Dream",
                            tint = Color(0xFFF59E0B),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = "Dream",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFD97706)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = exam.fullName,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Checkbox Stages
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                ChecklistToggle(
                    label = "Applied",
                    checked = applied,
                    onChecked = {
                        applied = it
                        onSave(applied, feePaid, admitDownloaded, appNumber, loginId, notes)
                    }
                )
                ChecklistToggle(
                    label = "Fee Paid",
                    checked = feePaid,
                    onChecked = {
                        feePaid = it
                        onSave(applied, feePaid, admitDownloaded, appNumber, loginId, notes)
                    }
                )
                ChecklistToggle(
                    label = "Admit Card",
                    checked = admitDownloaded,
                    onChecked = {
                        admitDownloaded = it
                        onSave(applied, feePaid, admitDownloaded, appNumber, loginId, notes)
                    }
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Stored App ID Preview or Expand Button
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { isExpanded = !isExpanded }
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Column {
                    Text(
                        text = if (appNumber.isNotBlank()) "App No: $appNumber" else "No Application No. stored",
                        fontSize = 12.sp,
                        fontFamily = if (appNumber.isNotBlank()) FontFamily.Monospace else FontFamily.Default,
                        fontWeight = FontWeight.SemiBold,
                        color = if (appNumber.isNotBlank()) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = if (isExpanded) "Hide Vault" else "Edit Vault",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Icon(
                        imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            // Expanded Vault inputs
            if (isExpanded) {
                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = appNumber,
                    onValueChange = { appNumber = it },
                    label = { Text("Application Number") },
                    singleLine = true,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = loginId,
                    onValueChange = { loginId = it },
                    label = { Text("Login Password / DOB / Roll No.") },
                    singleLine = true,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Exam Center & Notes") },
                    maxLines = 2,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = {
                        onSave(applied, feePaid, admitDownloaded, appNumber, loginId, notes)
                        isExpanded = false
                    },
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Save to Device Vault")
                }
            }
        }
    }
}

@Composable
private fun ChecklistToggle(
    label: String,
    checked: Boolean,
    onChecked: (Boolean) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.clickable { onChecked(!checked) }
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = onChecked,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = if (checked) FontWeight.Bold else FontWeight.Normal,
            color = if (checked) Color(0xFF059669) else MaterialTheme.colorScheme.onSurface
        )
    }
}
