package com.example.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val transactionId: String, // e.g. TXN-89412
    val title: String,
    val type: String, // DEPOSIT, WITHDRAWAL, TASK_REWARD, REFERRAL_BONUS, MARKETPLACE_SALE, MARKETPLACE_PURCHASE
    val amount: Double,
    val currency: String = "USD",
    val status: String, // COMPLETED, PENDING, REJECTED
    val paymentMethod: String, // VISA, MASTERCARD, PAYPAL, CRYPTO_USDT, STRIPE, GOOGLE_PAY, BAIZPAY_WALLET
    val timestamp: Long = System.currentTimeMillis(),
    val note: String = ""
)
