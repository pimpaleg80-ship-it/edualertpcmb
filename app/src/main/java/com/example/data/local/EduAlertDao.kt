package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface EduAlertDao {

    // User Preferences
    @Query("SELECT * FROM user_preferences WHERE id = 1 LIMIT 1")
    fun getUserPreference(): Flow<UserPreferenceEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveUserPreference(preference: UserPreferenceEntity)

    // User Applications & Checklists
    @Query("SELECT * FROM user_applications")
    fun getAllApplications(): Flow<List<UserApplicationEntity>>

    @Query("SELECT * FROM user_applications WHERE examId = :examId LIMIT 1")
    fun getApplication(examId: String): Flow<UserApplicationEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveApplication(application: UserApplicationEntity)

    // Notification Logs
    @Query("SELECT * FROM notification_logs ORDER BY timestampMs DESC")
    fun getAllNotifications(): Flow<List<NotificationLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: NotificationLogEntity)

    @Query("UPDATE notification_logs SET isRead = 1 WHERE id = :id")
    suspend fun markNotificationAsRead(id: Long)

    @Query("UPDATE notification_logs SET isRead = 1")
    suspend fun markAllNotificationsAsRead()

    @Query("DELETE FROM notification_logs")
    suspend fun clearNotificationLogs()
}
