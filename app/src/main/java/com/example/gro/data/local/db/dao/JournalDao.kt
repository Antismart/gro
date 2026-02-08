package com.example.gro.data.local.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.gro.data.local.db.entity.JournalEntryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface JournalDao {

    @Query("SELECT * FROM journal_entries WHERE walletAddress = :walletAddress ORDER BY timestamp DESC")
    fun observeEntries(walletAddress: String): Flow<List<JournalEntryEntity>>

    @Query("SELECT * FROM journal_entries WHERE walletAddress = :walletAddress ORDER BY timestamp DESC LIMIT :limit")
    suspend fun getRecentEntries(walletAddress: String, limit: Int): List<JournalEntryEntity>

    @Query("SELECT * FROM journal_entries WHERE walletAddress = :walletAddress AND timestamp >= :since ORDER BY timestamp DESC")
    suspend fun getEntriesSince(walletAddress: String, since: Long): List<JournalEntryEntity>

    @Insert
    suspend fun insertEntry(entry: JournalEntryEntity)
}
