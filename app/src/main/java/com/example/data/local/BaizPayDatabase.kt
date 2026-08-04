package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.data.local.dao.BaizPayDao
import com.example.data.local.entities.NotificationEntity
import com.example.data.local.entities.ProductEntity
import com.example.data.local.entities.ReferralEntity
import com.example.data.local.entities.TaskEntity
import com.example.data.local.entities.TransactionEntity
import com.example.data.local.entities.UserEntity

@Database(
    entities = [
        UserEntity::class,
        TransactionEntity::class,
        TaskEntity::class,
        ReferralEntity::class,
        ProductEntity::class,
        NotificationEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class BaizPayDatabase : RoomDatabase() {

    abstract fun baizPayDao(): BaizPayDao

    companion object {
        @Volatile
        private var INSTANCE: BaizPayDatabase? = null

        fun getDatabase(context: Context): BaizPayDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    BaizPayDatabase::class.java,
                    "baizpay_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
