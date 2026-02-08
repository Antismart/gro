package com.example.gro.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.gro.domain.repository.GardenRepository
import com.example.gro.domain.repository.WalletRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class GardenSyncWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val gardenRepository: GardenRepository,
    private val walletRepository: WalletRepository,
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val address = walletRepository.getConnectedAddress() ?: return Result.success()
        return try {
            gardenRepository.syncGardenWithChain(address)
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }
}
