package com.example.gro.domain.repository

import com.example.gro.domain.model.JournalAction
import com.example.gro.domain.model.JournalEntry
import kotlinx.coroutines.flow.Flow

interface JournalRepository {
    fun observeEntries(walletAddress: String): Flow<List<JournalEntry>>
    suspend fun logEntry(walletAddress: String, action: JournalAction, details: String, gardenSnapshot: Int = 0)
}
