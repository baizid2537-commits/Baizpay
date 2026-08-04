package com.example.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profile")
data class UserEntity(
    @PrimaryKey val id: String = "user_main",
    val fullName: String = "Baizid Ahmed",
    val email: String = "baizid2537@gmail.com",
    val isVerified: Boolean = false, // USD $5 verification status
    val verificationDate: Long = 0L,
    val walletBalance: Double = 25.0, // USD
    val availableBalance: Double = 20.0,
    val pendingBalance: Double = 5.0,
    val referralEarnings: Double = 12.0,
    val taskEarnings: Double = 8.50,
    val salesEarnings: Double = 4.50,
    val referralCode: String = "BAIZ2026",
    val totalReferrals: Int = 6,
    val activeReferrals: Int = 5,
    val pendingReferrals: Int = 1,
    val vipTier: String = "GOLD", // BRONZE, SILVER, GOLD, PLATINUM
    val monthlySalaryEarnings: Double = 30.0, // Accumulated salary rewards
    val monthlyDirectReferrals: Int = 28, // Direct referrals completed this month
    val salaryRank: String = "Silver", // Current rank: Bronze, Silver, Gold, Platinum, Diamond, Elite, Crown, Royal, Legend, Global Ambassador
    val dailyCheckInStreak: Int = 3,
    val lastCheckInDate: Long = 0L,
    val isAdmin: Boolean = true
)
