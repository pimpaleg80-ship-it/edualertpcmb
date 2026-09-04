package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.StageMilestone

@Composable
fun MiniStageStepper(stages: List<StageMilestone>, modifier: Modifier = Modifier) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = modifier.fillMaxWidth()
    ) {
        val total = stages.size.coerceAtMost(6)
        stages.take(total).forEachIndexed { index, stage ->
            val isPassed = stage.isCompleted
            val isCurrent = stage.isCurrent

            val circleColor = when {
                isCurrent -> Color(0xFF2563EB)
                isPassed -> Color(0xFF10B981)
                else -> MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
            }

            Box(
                modifier = Modifier
                    .size(if (isCurrent) 10.dp else 7.dp)
                    .clip(CircleShape)
                    .background(circleColor)
            )

            if (index < total - 1) {
                val lineColor = when {
                    isPassed -> Color(0xFF10B981)
                    else -> MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                }
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(2.dp)
                        .padding(horizontal = 2.dp)
                        .background(lineColor)
                )
            }
        }
    }
}

@Composable
fun FullStageTimeline(stages: List<StageMilestone>, modifier: Modifier = Modifier) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(12.dp)) {
        stages.forEachIndexed { index, stage ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                // Circle with line
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.width(28.dp)
                ) {
                    val circleBg = when {
                        stage.isCurrent -> Color(0xFF2563EB)
                        stage.isCompleted -> Color(0xFF10B981)
                        else -> MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
                    }

                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(20.dp)
                            .clip(CircleShape)
                            .background(circleBg)
                    ) {
                        Text(
                            text = (index + 1).toString(),
                            color = Color.White,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    if (index < stages.size - 1) {
                        Box(
                            modifier = Modifier
                                .width(2.dp)
                                .height(32.dp)
                                .background(
                                    if (stage.isCompleted) Color(0xFF10B981).copy(alpha = 0.5f)
                                    else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
                                )
                        )
                    }
                }

                // Stage content
                Column(
                    modifier = Modifier
                        .padding(start = 10.dp)
                        .weight(1f)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = stage.title,
                            fontSize = 13.sp,
                            fontWeight = if (stage.isCurrent) FontWeight.Bold else FontWeight.SemiBold,
                            color = if (stage.isCurrent) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                        )
                        if (stage.isExtended) {
                            Text(
                                text = "Extended",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFD97706),
                                modifier = Modifier
                                    .background(Color(0xFFFEF3C7), CircleShape)
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        } else if (stage.isTentative) {
                            Text(
                                text = "Tentative",
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Text(
                        text = stage.dateDisplay,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
