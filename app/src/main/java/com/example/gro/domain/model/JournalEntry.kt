package com.example.gro.domain.model

enum class JournalAction {
    DEPOSIT,
    GROWTH,
    VISIT,
    STREAK,
    BLOOM,
}

data class JournalEntry(
    val id: Long = 0,
    val walletAddress: String,
    val timestamp: Long,
    val action: JournalAction,
    val details: String,
    val gardenSnapshot: Int,
)
