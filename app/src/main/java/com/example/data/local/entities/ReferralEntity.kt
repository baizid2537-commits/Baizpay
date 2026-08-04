package com.example.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "referrals")
data class ReferralEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val referredName: String,
    val referredEmail: String,
    val dateJoined: Long,
    val status: String, // QUALIFIED, PENDING_VERIFICATION, INACTIVE
    val rewardAmount: Double = 2.00, // USD reward
    val level: Int = 1 // Level 1 (Direct), Level 2, Level 3, Level 4, Level 5
)
