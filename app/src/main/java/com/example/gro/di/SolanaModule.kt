package com.example.gro.di

import android.net.Uri
import com.example.gro.data.repository.DepositRepositoryImpl
import com.example.gro.data.repository.GardenRepositoryImpl
import com.example.gro.data.repository.JournalRepositoryImpl
import com.example.gro.data.repository.StreakRepositoryImpl
import com.example.gro.data.repository.WalletRepositoryImpl
import com.example.gro.domain.repository.DepositRepository
import com.example.gro.domain.repository.GardenRepository
import com.example.gro.domain.repository.JournalRepository
import com.example.gro.domain.repository.StreakRepository
import com.example.gro.domain.repository.WalletRepository
import com.solana.mobilewalletadapter.clientlib.ConnectionIdentity
import com.solana.mobilewalletadapter.clientlib.MobileWalletAdapter
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class SolanaModule {

    @Binds
    @Singleton
    abstract fun bindWalletRepository(impl: WalletRepositoryImpl): WalletRepository

    @Binds
    @Singleton
    abstract fun bindGardenRepository(impl: GardenRepositoryImpl): GardenRepository

    @Binds
    @Singleton
    abstract fun bindDepositRepository(impl: DepositRepositoryImpl): DepositRepository

    @Binds
    @Singleton
    abstract fun bindStreakRepository(impl: StreakRepositoryImpl): StreakRepository

    @Binds
    @Singleton
    abstract fun bindJournalRepository(impl: JournalRepositoryImpl): JournalRepository

    companion object {
        @Provides
        @Singleton
        fun provideConnectionIdentity(): ConnectionIdentity = ConnectionIdentity(
            identityUri = Uri.parse("https://gro.app"),
            iconUri = Uri.parse("favicon.ico"),
            identityName = "Gr\u014D",
        )

        @Provides
        @Singleton
        fun provideMobileWalletAdapter(connectionIdentity: ConnectionIdentity): MobileWalletAdapter {
            return MobileWalletAdapter(connectionIdentity)
        }
    }
}
