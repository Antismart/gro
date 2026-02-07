package com.example.gro.domain.usecase

import com.example.gro.domain.model.WalletState
import com.example.gro.domain.repository.WalletRepository
import com.solana.mobilewalletadapter.clientlib.ActivityResultSender
import javax.inject.Inject

class ConnectWalletUseCase @Inject constructor(
    private val walletRepository: WalletRepository,
) {
    suspend operator fun invoke(sender: ActivityResultSender): WalletState {
        return walletRepository.connect(sender)
    }
}
