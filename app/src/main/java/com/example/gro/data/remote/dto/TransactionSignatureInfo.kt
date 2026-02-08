package com.example.gro.data.remote.dto

data class TransactionSignatureInfo(
    val signature: String,
    val blockTime: Long?,
    val memo: String?,
)
