package com.example.gro.domain.repository

import com.example.gro.domain.model.WalletState
import com.solana.mobilewalletadapter.clientlib.ActivityResultSender
import kotlinx.coroutines.flow.Flow

interface WalletRepository {
    val walletState: Flow<WalletState>
    suspend fun connect(sender: ActivityResultSender): WalletState
    suspend fun disconnect()
    fun getConnectedAddress(): String?
}
