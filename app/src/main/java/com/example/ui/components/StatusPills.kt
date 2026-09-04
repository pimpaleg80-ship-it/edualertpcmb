package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ExamCategory
import com.example.data.model.ExamStatus
import com.example.ui.theme.*

@Composable
fun StatusPill(status: ExamStatus, modifier: Modifier = Modifier) {
    val (bgColor, textColor, dotColor) = when (status) {
        ExamStatus.REGISTRATION_OPEN -> Triple(Color(0xFFDCFCE7), Color(0xFF166534), Color(0xFF22C55E))
        ExamStatus.LAST_48_HOURS -> Triple(Color(0xFFFEE2E2), Color(0xFF991B1B), Color(0xFFEF4444))
        ExamStatus.ADMIT_CARD_LIVE -> Triple(Color(0xFFDBEAFE), Color(0xFF1E40AF), Color(0xFF3B82F6))
        ExamStatus.UPCOMING -> Triple(Color(0xFFFEF3C7), Color(0xFF92400E), Color(0xFFF59E0B))
        ExamStatus.CLOSED -> Triple(Color(0xFFF1F5F9), Color(0xFF475569), Color(0xFF94A3B8))
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .background(bgColor, RoundedCornerShape(100.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(6.dp)
                .background(dotColor, CircleShape)
        )
        Text(
            text = status.label,
            color = textColor,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(start = 5.dp)
        )
    }
}

@Composable
fun CategoryPill(category: ExamCategory, modifier: Modifier = Modifier) {
    val (bgColor, textColor) = when (category) {
        ExamCategory.ENGINEERING -> Pair(Color(0xFFEFF6FF), Color(0xFF1D4ED8))
        ExamCategory.MEDICAL -> Pair(Color(0xFFF0FDF4), Color(0xFF15803D))
        ExamCategory.DEFENSE -> Pair(Color(0xFFF5F3FF), Color(0xFF6D28D9))
        ExamCategory.RESEARCH -> Pair(Color(0xFFFFFBEB), Color(0xFFB45309))
        ExamCategory.STATE_CET -> Pair(Color(0xFFFDF2F8), Color(0xFFBE185D))
    }

    Text(
        text = category.shortLabel,
        color = textColor,
        fontSize = 11.sp,
        fontWeight = FontWeight.Medium,
        modifier = modifier
            .background(bgColor, RoundedCornerShape(6.dp))
            .border(0.5.dp, textColor.copy(alpha = 0.25f), RoundedCornerShape(6.dp))
            .padding(horizontal = 7.dp, vertical = 2.5.dp)
    )
}

@Composable
fun DreamExamBadge(modifier: Modifier = Modifier) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .background(Color(0xFFFEF3C7), RoundedCornerShape(100.dp))
            .border(1.dp, Color(0xFFF59E0B).copy(alpha = 0.5f), RoundedCornerShape(100.dp))
            .padding(horizontal = 8.dp, vertical = 3.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Star,
            contentDescription = "Dream Exam",
            tint = Color(0xFFD97706),
            modifier = Modifier.size(13.dp)
        )
        Text(
            text = "Dream Exam",
            color = Color(0xFF92400E),
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = 4.dp)
        )
    }
}
