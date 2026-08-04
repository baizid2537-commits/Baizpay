package com.example.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "marketplace_products")
data class ProductEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val category: String, // DIGITAL, GIFTCARD, ELECTRONICS, SUBSCRIPTION
    val price: Double, // USD
    val sellerName: String,
    val rating: Float = 4.8f,
    val reviewCount: Int = 12,
    val stockCount: Int = 50,
    val imageUrl: String = "",
    val description: String = "",
    val isFeatured: Boolean = false
)
