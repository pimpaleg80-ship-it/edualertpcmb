package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ExamStatus

@Composable
fun CountdownTimer(
    targetTimestampMs: Long,
    currentTimeMs: Long,
    status: ExamStatus,
    modifier: Modifier = Modifier
) {
    val diffMs = targetTimestampMs - currentTimeMs

    if (diffMs <= 0) {
        Text(
            text = "Window Closed",
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = modifier
        )
        return
    }

    val totalSeconds = diffMs / 1000
    val days = totalSeconds / 86400
    val hours = (totalSeconds % 86400) / 3600
    val minutes = (totalSeconds % 3600) / 60
    val seconds = totalSeconds % 60

    val isUrgent = status == ExamStatus.LAST_48_HOURS || days < 2

    val boxBg = if (isUrgent) Color(0xFFFEE2E2) else MaterialTheme.colorScheme.surfaceVariant
    val textColor = if (isUrgent) Color(0xFFB91C1C) else MaterialTheme.colorScheme.onSurfaceVariant
    val numColor = if (isUrgent) Color(0xFF991B1B) else MaterialTheme.colorScheme.primary

    Row(
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
    ) {
        if (days > 0) {
            TimerUnitBox(value = days.toString(), label = "d", boxBg = boxBg, numColor = numColor, labelColor = textColor)
        }
        TimerUnitBox(value = hours.toString().padStart(2, '0'), label = "h", boxBg = boxBg, numColor = numColor, labelColor = textColor)
        TimerUnitBox(value = minutes.toString().padStart(2, '0'), label = "m", boxBg = boxBg, numColor = numColor, labelColor = textColor)
        TimerUnitBox(value = seconds.toString().padStart(2, '0'), label = "s", boxBg = boxBg, numColor = numColor, labelColor = textColor)
    }
}

@Composable
private fun TimerUnitBox(
    value: String,
    label: String,
    boxBg: Color,
    numColor: Color,
    labelColor: Color
) {
    Row(
        verticalAlignment = Alignment.Bottom,
        modifier = Modifier
            .background(boxBg, RoundedCornerShape(4.dp))
            .padding(horizontal = 4.dp, vertical = 2.dp)
    ) {
        Text(
            text = value,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace,
            color = numColor
        )
        Text(
            text = label,
            fontSize = 10.sp,
            fontWeight = FontWeight.SemiBold,
            color = labelColor,
            modifier = Modifier.padding(start = 1.dp)
        )
    }
}
