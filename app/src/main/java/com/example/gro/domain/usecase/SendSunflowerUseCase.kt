package com.example.gro.domain.usecase

import com.example.gro.domain.model.JournalAction
import com.example.gro.domain.repository.DepositRepository
import com.example.gro.domain.repository.DepositResult
import com.example.gro.domain.repository.JournalRepository
import com.solana.mobilewalletadapter.clientlib.ActivityResultSender
import javax.inject.Inject

class SendSunflowerUseCase @Inject constructor(
    private val depositRepository: DepositRepository,
    private val journalRepository: JournalRepository,
) {
    suspend operator fun invoke(
        sender: ActivityResultSender,
        fromAddress: String,
        toAddress: String,
    ): DepositResult {
        val result = depositRepository.sendSunflower(sender, fromAddress, toAddress)

        if (result is DepositResult.Success) {
            val shortAddr = "${toAddress.take(4)}...${toAddress.takeLast(4)}"
            journalRepository.logEntry(
                walletAddress = fromAddress,
                action = JournalAction.VISIT,
                details = "Left a sunflower in $shortAddr's garden",
            )
        }

        return result
    }
}
