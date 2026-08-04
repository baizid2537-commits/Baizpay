package com.example.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "micro_tasks")
data class TaskEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val category: String, // DAILY_CHECKIN, WATCH_VIDEO, SURVEY, APP_DOWNLOAD, ARTICLE, QUIZ, SPIN_WHEEL, SCRATCH_CARD
    val description: String,
    val rewardAmount: Double, // USD e.g. 0.50
    val durationSeconds: Int = 15,
    val isCompleted: Boolean = false,
    val completionStatus: String = "AVAILABLE", // AVAILABLE, PENDING_APPROVAL, COMPLETED
    val iconName: String = "ic_task"
)
