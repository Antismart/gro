package com.example.gro.domain.usecase

import android.util.Log
import com.example.gro.data.remote.SolanaRpcClient
import com.example.gro.domain.model.JournalAction
import com.example.gro.domain.model.PlantSpecies
import com.example.gro.domain.repository.JournalRepository
import javax.inject.Inject

class SyncJournalFromChainUseCase @Inject constructor(
    private val solanaRpcClient: SolanaRpcClient,
    private val journalRepository: JournalRepository,
) {
    suspend operator fun invoke(walletAddress: String) {
        try {
            val signatures = solanaRpcClient.getSignaturesForAddress(walletAddress, limit = 30)

            for (sig in signatures) {
                val memo = sig.memo ?: continue
                // Parse Grō memo format: "[program_id] gro:deposit:v1:<SPECIES>:<LAMPORTS>"
                // The RPC returns memo with a prefix like "[MemoSq4g...] "
                val groMemo = memo.substringAfter("] ", memo)
                if (!groMemo.startsWith("gro:")) continue

                val parts = groMemo.split(":")
                if (parts.size < 5 || parts[1] != "deposit") continue

                val speciesName = parts[3]
                val lamports = parts[4].toLongOrNull() ?: continue
                val species = try {
                    PlantSpecies.valueOf(speciesName)
                } catch (_: Exception) {
                    continue
                }

                val blockTimeMs = (sig.blockTime ?: continue) * 1000L
                val solAmount = lamports / 1_000_000_000.0
                val details = "Deposited ${"%.4f".format(solAmount)} ${species.displayName} to grow ${species.plantName}"

                journalRepository.logEntry(
                    walletAddress = walletAddress,
                    action = JournalAction.DEPOSIT,
                    details = details,
                )

                Log.d(TAG, "Synced on-chain deposit: sig=${sig.signature.take(8)}..., $speciesName, $lamports")
            }
        } catch (e: Exception) {
            Log.w(TAG, "Failed to sync journal from chain", e)
        }
    }

    companion object {
        private const val TAG = "SyncJournal"
    }
}
