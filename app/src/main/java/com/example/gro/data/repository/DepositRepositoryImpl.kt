package com.example.gro.data.repository

import android.content.Context
import android.os.PowerManager
import android.util.Log
import com.example.gro.data.remote.MarinadeService
import com.example.gro.data.remote.SolanaConfig
import com.example.gro.data.remote.SolanaRpcClient
import com.example.gro.domain.model.PlantSpecies
import com.example.gro.domain.repository.DepositRepository
import com.example.gro.domain.repository.DepositResult
import com.solana.mobilewalletadapter.clientlib.ActivityResultSender
import com.solana.mobilewalletadapter.clientlib.MobileWalletAdapter
import com.solana.mobilewalletadapter.clientlib.TransactionParams
import com.solana.mobilewalletadapter.clientlib.TransactionResult
import dagger.hilt.android.qualifiers.ApplicationContext
import org.sol4k.Base58
import org.sol4k.PublicKey
import org.sol4k.Transaction
import org.sol4k.instruction.TransferInstruction
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DepositRepositoryImpl @Inject constructor(
    private val solanaRpcClient: SolanaRpcClient,
    private val mobileWalletAdapter: MobileWalletAdapter,
    private val config: SolanaConfig,
    private val marinadeService: MarinadeService,
    @ApplicationContext private val context: Context,
) : DepositRepository {

    override suspend fun depositSol(
        sender: ActivityResultSender,
        fromAddress: String,
        lamports: Long,
        species: PlantSpecies,
    ): DepositResult {
        val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        val wakeLock = powerManager.newWakeLock(
            PowerManager.PARTIAL_WAKE_LOCK,
            "gro:mwa-deposit",
        )
        wakeLock.acquire(120_000L)

        return try {
            val blockhash = solanaRpcClient.getLatestBlockhash()
            Log.d(TAG, "Got blockhash: $blockhash, cluster=${config.cluster}")

            val fromPubkey = PublicKey(fromAddress)

            // SPL Memo instruction for on-chain Grō deposit identification
            val memoData = "gro:deposit:v1:${species.name}:$lamports"
            val memoProgramId = PublicKey(MEMO_PROGRAM_ID)
            val memoIx = org.sol4k.instruction.BaseInstruction(
                memoData.toByteArray(Charsets.UTF_8),
                listOf(org.sol4k.AccountMeta.signer(fromPubkey)),
                memoProgramId,
            )

            val instructions = if (config.cluster == "mainnet-beta") {
                // Mainnet: Marinade liquid staking (SOL → mSOL)
                Log.d(TAG, "Building Marinade deposit instruction for mainnet")
                marinadeService.verifyAccounts()
                marinadeService.buildDepositInstructions(fromPubkey, lamports) + memoIx
            } else {
                // Devnet: self-transfer (safe for demos, user keeps SOL)
                val transferIx = TransferInstruction(fromPubkey, fromPubkey, lamports)
                listOf(transferIx, memoIx)
            }

            val transaction = Transaction(blockhash, instructions, fromPubkey)
            val serializedTx = transaction.serialize()
            Log.d(TAG, "Built tx with ${instructions.size} instructions, ${serializedTx.size} bytes")

            val result = mobileWalletAdapter.transact(sender) {
                signAndSendTransactions(
                    arrayOf(serializedTx),
                    TransactionParams(null, null, null, null, null),
                )
            }

            when (result) {
                is TransactionResult.Success -> {
                    val payload = result.payload
                    val sigBytes = payload.signatures.firstOrNull()
                    val sigStr = sigBytes?.let { Base58.encode(it) } ?: "unknown"
                    Log.d(TAG, "Deposit success: sig=$sigStr")
                    DepositResult.Success(sigStr)
                }
                is TransactionResult.NoWalletFound -> {
                    Log.w(TAG, "No wallet found")
                    DepositResult.Error("No wallet app found")
                }
                is TransactionResult.Failure -> {
                    Log.e(TAG, "Deposit failed: ${result.message}", result.e)
                    DepositResult.Error(result.message ?: "Transaction failed")
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Deposit exception", e)
            DepositResult.Error(e.message ?: "Unexpected error")
        } finally {
            if (wakeLock.isHeld) {
                wakeLock.release()
                Log.d(TAG, "WakeLock released")
            }
        }
    }

    override suspend fun sendSunflower(
        sender: ActivityResultSender,
        fromAddress: String,
        toAddress: String,
    ): DepositResult {
        val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        val wakeLock = powerManager.newWakeLock(
            PowerManager.PARTIAL_WAKE_LOCK,
            "gro:mwa-sunflower",
        )
        wakeLock.acquire(120_000L)

        return try {
            val blockhash = solanaRpcClient.getLatestBlockhash()
            val fromPubkey = PublicKey(fromAddress)
            val toPubkey = PublicKey(toAddress)

            val transferIx = TransferInstruction(fromPubkey, toPubkey, SUNFLOWER_LAMPORTS)
            val memoData = "gro:sunflower:v1"
            val memoIx = org.sol4k.instruction.BaseInstruction(
                memoData.toByteArray(Charsets.UTF_8),
                listOf(org.sol4k.AccountMeta.signer(fromPubkey)),
                PublicKey(MEMO_PROGRAM_ID),
            )

            val transaction = Transaction(blockhash, listOf(transferIx, memoIx), fromPubkey)
            val serializedTx = transaction.serialize()
            Log.d(TAG, "Built sunflower tx, ${serializedTx.size} bytes")

            val result = mobileWalletAdapter.transact(sender) {
                signAndSendTransactions(
                    arrayOf(serializedTx),
                    TransactionParams(null, null, null, null, null),
                )
            }

            when (result) {
                is TransactionResult.Success -> {
                    val sigBytes = result.payload.signatures.firstOrNull()
                    val sigStr = sigBytes?.let { Base58.encode(it) } ?: "unknown"
                    Log.d(TAG, "Sunflower sent: sig=$sigStr")
                    DepositResult.Success(sigStr)
                }
                is TransactionResult.NoWalletFound -> {
                    DepositResult.Error("No wallet app found")
                }
                is TransactionResult.Failure -> {
                    DepositResult.Error(result.message ?: "Transaction failed")
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Sunflower exception", e)
            DepositResult.Error(e.message ?: "Unexpected error")
        } finally {
            if (wakeLock.isHeld) {
                wakeLock.release()
            }
        }
    }

    companion object {
        private const val TAG = "DepositRepo"
        private const val MEMO_PROGRAM_ID = "MemoSq4gqABAXKb96qnH8TysNcWxMyWCqXgDLGmfcHr"
        private const val SUNFLOWER_LAMPORTS = 1000L // 0.000001 SOL
    }
}
