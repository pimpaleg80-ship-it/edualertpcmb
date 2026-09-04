package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_preferences")
data class UserPreferenceEntity(
    @PrimaryKey val id: Int = 1,
    val stream: String = "PCM", // PCM, PCB, PCMB
    val homeState: String = "Maharashtra",
    val targetPathway: String = "Engineering",
    val followedExamIds: String = "jee-main-2026,bitsat-2026,mht-cet-2026",
    val dreamExamIds: String = "jee-main-2026,bitsat-2026", // max 3
    val pushEnabled: Boolean = true,
    val whatsappEnabled: Boolean = false,
    val dndBypassDreamExams: Boolean = true,
    val targetYear: Int = 2026
)

@Entity(tableName = "user_applications")
data class UserApplicationEntity(
    @PrimaryKey val examId: String,
    val isApplied: Boolean = false,
    val isFeePaid: Boolean = false,
    val isAdmitCardDownloaded: Boolean = false,
    val applicationNumber: String = "",
    val loginId: String = "",
    val personalNotes: String = "",
    val lastUpdatedMs: Long = System.currentTimeMillis()
)

@Entity(tableName = "notification_logs")
data class NotificationLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val examId: String,
    val examShortCode: String,
    val title: String,
    val message: String,
    val channel: String = "In-App Push",
    val isDreamExam: Boolean = false,
    val timestampMs: Long = System.currentTimeMillis(),
    val isRead: Boolean = false
)
