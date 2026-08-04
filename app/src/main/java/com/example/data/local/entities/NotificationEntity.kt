package com.example.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notifications")
data class NotificationEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val message: String,
    val type: String, // SYSTEM, EARNING, VERIFICATION, MARKETPLACE, REFERRAL
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = false
)
