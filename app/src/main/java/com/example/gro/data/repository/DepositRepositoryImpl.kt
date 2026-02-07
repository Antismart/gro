package com.example.gro.data.repository

import android.content.Context
import android.os.PowerManager
import android.util.Log
import com.example.gro.data.remote.SolanaRpcClient
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
    @ApplicationContext private val context: Context,
) : DepositRepository {

    override suspend fun depositSol(
        sender: ActivityResultSender,
        fromAddress: String,
        lamports: Long,
    ): DepositResult {
        val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        val wakeLock = powerManager.newWakeLock(
            PowerManager.PARTIAL_WAKE_LOCK,
            "gro:mwa-deposit",
        )
        wakeLock.acquire(120_000L)

        return try {
            val blockhash = solanaRpcClient.getLatestBlockhash()
            Log.d(TAG, "Got blockhash: $blockhash")

            val fromPubkey = PublicKey(fromAddress)
            val transferIx = TransferInstruction(fromPubkey, fromPubkey, lamports)
            val transaction = Transaction(blockhash, transferIx, fromPubkey)
            val serializedTx = transaction.serialize()
            Log.d(TAG, "Built transaction, ${serializedTx.size} bytes")

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

    companion object {
        private const val TAG = "DepositRepo"
    }
}
