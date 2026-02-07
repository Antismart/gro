package com.example.gro.domain.model

sealed class WalletState {
    data object Disconnected : WalletState()
    data object Connecting : WalletState()
    data class Connected(
        val publicKey: String,
        val authToken: String,
    ) : WalletState()
    data class Error(val message: String) : WalletState()
}
