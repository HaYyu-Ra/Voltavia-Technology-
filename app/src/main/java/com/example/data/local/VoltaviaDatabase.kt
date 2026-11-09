package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [
        QuoteEntity::class,
        SupportTicketEntity::class,
        TicketReplyEntity::class,
        ProjectEntity::class,
        ProjectMilestoneEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class VoltaviaDatabase : RoomDatabase() {
    abstract fun voltaviaDao(): VoltaviaDao

    companion object {
        @Volatile
        private var INSTANCE: VoltaviaDatabase? = null

        // Sample migration definition for future schema evolution
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // Reserved for schema migrations
            }
        }

        fun getInstance(context: Context): VoltaviaDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    VoltaviaDatabase::class.java,
                    "voltavia_client_portal.db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
