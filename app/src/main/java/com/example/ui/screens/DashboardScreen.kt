package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.AdminPanelSettings
import androidx.compose.material.icons.outlined.Bookmark
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.UserPreferenceEntity
import com.example.data.model.ExamCategory
import com.example.data.model.ExamItem
import com.example.data.model.ExamStatus
import com.example.data.sync.SyncState
import com.example.ui.components.ExamCard
import com.example.viewmodel.ExamFilterChip

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    exams: List<ExamItem>,
    userPreference: UserPreferenceEntity,
    syncState: SyncState,
    searchQuery: String,
    selectedCategory: ExamCategory?,
    selectedFilterChip: ExamFilterChip,
    selectedYearFilter: Int? = null,
    currentTimeMs: Long,
    onSearchQueryChange: (String) -> Unit,
    onSelectCategory: (ExamCategory?) -> Unit,
    onSelectFilterChip: (ExamFilterChip) -> Unit,
    onSelectYearFilter: (Int?) -> Unit = {},
    onUpdateTargetYearPreference: (Int) -> Unit = {},
    onToggleFollow: (String) -> Unit,
    onToggleDream: (ExamItem) -> Unit,
    onOpenDetails: (ExamItem) -> Unit,
    onTriggerAlert: (ExamItem, String) -> Unit,
    onTriggerManualSync: () -> Unit,
    onOpenAdminCms: () -> Unit,
    modifier: Modifier = Modifier
) {
    val followedList = userPreference.followedExamIds.split(",").filter { it.isNotBlank() }
    val dreamList = userPreference.dreamExamIds.split(",").filter { it.isNotBlank() }

    // Target year to filter by: if explicitly selected, use that; otherwise if null, show all or match userPreference
    val activeYearFilter = selectedYearFilter

    // Filter exams based on search, category, target year, and filter chips
    val filteredExams = exams.filter { exam ->
        val matchesSearch = searchQuery.isBlank() ||
                exam.fullName.contains(searchQuery, ignoreCase = true) ||
                exam.shortCode.contains(searchQuery, ignoreCase = true) ||
                exam.conductingBody.contains(searchQuery, ignoreCase = true) ||
                (exam.stateDomicile?.contains(searchQuery, ignoreCase = true) == true)

        val matchesCategory = selectedCategory == null || exam.category == selectedCategory

        val matchesYear = activeYearFilter == null || exam.targetYear == activeYearFilter

        val matchesChip = when (selectedFilterChip) {
            ExamFilterChip.ALL -> true
            ExamFilterChip.FOLLOWED_ONLY -> followedList.contains(exam.id)
            ExamFilterChip.DREAM_ONLY -> dreamList.contains(exam.id)
            ExamFilterChip.REGISTRATION_OPEN -> exam.currentStatus == ExamStatus.REGISTRATION_OPEN
            ExamFilterChip.LAST_48_HOURS -> exam.currentStatus == ExamStatus.LAST_48_HOURS
        }

        matchesSearch && matchesCategory && matchesYear && matchesChip
    }

    // Check if any followed or dream exams are in Last 48 Hours or Admit Card Live
    val urgentExams = exams.filter {
        (followedList.contains(it.id) || dreamList.contains(it.id)) &&
                (it.currentStatus == ExamStatus.LAST_48_HOURS || it.currentStatus == ExamStatus.ADMIT_CARD_LIVE)
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 90.dp)
    ) {
        // App Header Banner with Personalization Indicators
        item {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "EduAlert PCMB",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(4.dp))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = userPreference.stream,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                        Text(
                            text = "Domicile: ${userPreference.homeState} • ${dreamList.size}/3 Dream Exams",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // Admin CMS Shortcut Icon
                    IconButton(
                        onClick = onOpenAdminCms,
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.surfaceVariant, CircleShape)
                            .size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.AdminPanelSettings,
                            contentDescription = "Admin CMS Panel",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                // Target Admission Cycle & Synchronization Scope Banner
                Card(
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.35f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                ) {
                    Column(modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Event,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(15.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Target Admission Year: Cycle ${userPreference.targetYear}",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                            Badge(containerColor = MaterialTheme.colorScheme.primary) {
                                Text("Sync: ${syncState.syncTargetYear}", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                // Real-Time Data Synchronization Status Banner
                Card(
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 6.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .background(if (syncState.isSyncing) Color(0xFFEAB308) else Color(0xFF16A34A), CircleShape)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = if (syncState.isSyncing) "Synchronizing delta stream..." else "⚡ Live Sync Active (Next.js & React Native)",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "Webhooks: ~38ms • Polling: ${syncState.pollingIntervalSeconds}s • ${syncState.totalEventsDispatched} events pushed",
                                    fontSize = 10.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        TextButton(
                            onClick = onTriggerManualSync,
                            enabled = !syncState.isSyncing,
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                            modifier = Modifier.height(28.dp)
                        ) {
                            if (syncState.isSyncing) {
                                CircularProgressIndicator(modifier = Modifier.size(12.dp), strokeWidth = 1.5.dp)
                            } else {
                                Icon(imageVector = Icons.Default.Sync, contentDescription = "Sync", modifier = Modifier.size(13.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Sync Now", fontSize = 11.sp)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Search Bar
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = onSearchQueryChange,
                    placeholder = { Text("Search JEE, NEET, NDA, CETs, conducting body...") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Outlined.Search,
                            contentDescription = "Search",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotBlank()) {
                            IconButton(onClick = { onSearchQueryChange("") }) {
                                Icon(imageVector = Icons.Default.Close, contentDescription = "Clear")
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        // Urgent Alert Banner (if any)
        if (urgentExams.isNotEmpty()) {
            item {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF2F2)),
                    border = BorderStroke(1.dp, Color(0xFFF87171)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = "Urgent",
                            tint = Color(0xFFDC2626),
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Action Required: ${urgentExams.size} Followed Exam(s)",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF991B1B)
                            )
                            Text(
                                text = "${urgentExams.joinToString(", ") { it.shortCode }} has deadlines or admit cards live now.",
                                fontSize = 11.sp,
                                color = Color(0xFF7F1D1D)
                            )
                        }
                    }
                }
            }
        }

        // Horizontal Category Tabs
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = selectedCategory == null,
                    onClick = { onSelectCategory(null) },
                    label = { Text("All Streams") }
                )
                ExamCategory.values().forEach { category ->
                    FilterChip(
                        selected = selectedCategory == category,
                        onClick = { onSelectCategory(category) },
                        label = { Text(category.displayName) }
                    )
                }
            }
        }

        // Quick Sub-Filter Chips
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                SubFilterPill(
                    label = "All (${exams.size})",
                    isSelected = selectedFilterChip == ExamFilterChip.ALL,
                    onClick = { onSelectFilterChip(ExamFilterChip.ALL) }
                )
                SubFilterPill(
                    label = "⭐ Dream (${dreamList.size})",
                    isSelected = selectedFilterChip == ExamFilterChip.DREAM_ONLY,
                    onClick = { onSelectFilterChip(ExamFilterChip.DREAM_ONLY) }
                )
                SubFilterPill(
                    label = "Followed (${followedList.size})",
                    isSelected = selectedFilterChip == ExamFilterChip.FOLLOWED_ONLY,
                    onClick = { onSelectFilterChip(ExamFilterChip.FOLLOWED_ONLY) }
                )
                SubFilterPill(
                    label = "Registration Open",
                    isSelected = selectedFilterChip == ExamFilterChip.REGISTRATION_OPEN,
                    onClick = { onSelectFilterChip(ExamFilterChip.REGISTRATION_OPEN) }
                )
                SubFilterPill(
                    label = "🚨 Last 48 Hours",
                    isSelected = selectedFilterChip == ExamFilterChip.LAST_48_HOURS,
                    onClick = { onSelectFilterChip(ExamFilterChip.LAST_48_HOURS) }
                )
            }
        }

        // Target Year Filter Chips Row
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Admission Year:",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                SubFilterPill(
                    label = "All Years",
                    isSelected = selectedYearFilter == null,
                    onClick = { onSelectYearFilter(null) }
                )
                listOf(2025, 2026, 2027, 2028).forEach { year ->
                    val isMyPref = userPreference.targetYear == year
                    SubFilterPill(
                        label = if (isMyPref) "Cycle $year (My Target)" else "Cycle $year",
                        isSelected = selectedYearFilter == year,
                        onClick = { onSelectYearFilter(year) }
                    )
                }
            }
        }

        // Exam Cards Count and List
        item {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Showing ${filteredExams.size} Examinations",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Live Deadlines & Portals",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        if (filteredExams.isEmpty()) {
            item {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 32.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Search,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(36.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "No exams matching the selected filter",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Try clearing the search or category filters above.",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        } else {
            items(filteredExams, key = { it.id }) { exam ->
                val isFollowed = followedList.contains(exam.id)
                val isDream = dreamList.contains(exam.id)

                ExamCard(
                    exam = exam,
                    isFollowed = isFollowed,
                    isDream = isDream,
                    currentTimeMs = currentTimeMs,
                    onToggleFollow = { onToggleFollow(exam.id) },
                    onToggleDream = { onToggleDream(exam) },
                    onOpenDetails = { onOpenDetails(exam) },
                    onTriggerTestAlert = { onTriggerAlert(exam, "T-48H") }
                )
            }
        }
    }
}

@Composable
private fun SubFilterPill(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(100.dp),
        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
        contentColor = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
        border = if (isSelected) null else BorderStroke(0.5.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)),
        modifier = Modifier.clickable { onClick() }
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
        )
    }
}
