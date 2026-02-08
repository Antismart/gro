package com.example.gro.di

import com.example.gro.BuildConfig
import com.example.gro.data.remote.PriceFeedService
import com.example.gro.data.remote.SolanaConfig
import com.example.gro.data.remote.SolanaRpcClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideJson(): Json = Json {
        ignoreUnknownKeys = true
        isLenient = true
    }

    @Provides
    @Singleton
    fun provideHttpClient(json: Json): HttpClient = HttpClient(Android) {
        install(ContentNegotiation) {
            json(json)
        }
    }

    @Provides
    @Singleton
    fun provideSolanaConfig(): SolanaConfig = SolanaConfig(
        rpcEndpoint = BuildConfig.SOLANA_RPC_URL,
        cluster = BuildConfig.SOLANA_CLUSTER,
    )

    @Provides
    @Singleton
    fun provideSolanaRpcClient(httpClient: HttpClient, config: SolanaConfig): SolanaRpcClient {
        return SolanaRpcClient(httpClient, config)
    }

    @Provides
    @Singleton
    fun providePriceFeedService(httpClient: HttpClient): PriceFeedService {
        return PriceFeedService(httpClient)
    }
}
