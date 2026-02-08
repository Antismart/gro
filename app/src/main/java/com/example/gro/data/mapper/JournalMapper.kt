package com.example.gro.data.mapper

import com.example.gro.data.local.db.entity.JournalEntryEntity
import com.example.gro.domain.model.JournalAction
import com.example.gro.domain.model.JournalEntry

fun JournalEntryEntity.toDomain(): JournalEntry = JournalEntry(
    id = id,
    walletAddress = walletAddress,
    timestamp = timestamp,
    action = JournalAction.valueOf(action),
    details = details,
    gardenSnapshot = gardenSnapshot,
)

fun JournalEntry.toEntity(): JournalEntryEntity = JournalEntryEntity(
    id = id,
    walletAddress = walletAddress,
    timestamp = timestamp,
    action = action.name,
    details = details,
    gardenSnapshot = gardenSnapshot,
)
