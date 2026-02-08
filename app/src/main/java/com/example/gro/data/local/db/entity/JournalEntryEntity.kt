package com.example.gro.data.local.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "journal_entries")
data class JournalEntryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val walletAddress: String,
    val timestamp: Long,
    val action: String,
    val details: String,
    val gardenSnapshot: Int,
)
