package com.example.gro.data.remote

import android.util.Log
import com.example.gro.util.withRetry
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PriceFeedService @Inject constructor(
    private val httpClient: HttpClient,
) {
    private val json = Json { ignoreUnknownKeys = true }
    private val cacheMutex = Mutex()
    private var cachedPrices: Map<String, Double> = emptyMap()
    private var cacheTimestamp: Long = 0L

    suspend fun getTokenPrices(mintAddresses: List<String>): Map<String, Double> {
        if (mintAddresses.isEmpty()) return emptyMap()

        cacheMutex.withLock {
            val now = System.currentTimeMillis()
            if (now - cacheTimestamp < CACHE_TTL_MS && cachedPrices.isNotEmpty()) {
                return cachedPrices
            }
        }

        return withContext(Dispatchers.IO) {
            try {
                val prices = withRetry(tag = "PriceFeed") {
                    val ids = mintAddresses.joinToString(",")
                    val response: String = httpClient.get(JUPITER_PRICE_API) {
                        parameter("ids", ids)
                    }.body()

                    val parsed = json.parseToJsonElement(response).jsonObject
                    val data = parsed["data"]?.jsonObject
                        ?: return@withRetry emptyMap<String, Double>()

                    val result = mutableMapOf<String, Double>()
                    for (mint in mintAddresses) {
                        val tokenData = data[mint]?.jsonObject ?: continue
                        val price = tokenData["price"]?.jsonPrimitive?.content?.toDoubleOrNull()
                        if (price != null) {
                            result[mint] = price
                        }
                    }
                    result
                }

                cacheMutex.withLock {
                    cachedPrices = prices
                    cacheTimestamp = System.currentTimeMillis()
                }

                Log.d(TAG, "Fetched prices for ${prices.size} tokens")
                prices
            } catch (e: Exception) {
                Log.e(TAG, "Failed to fetch prices", e)
                cacheMutex.withLock { cachedPrices }
            }
        }
    }

    suspend fun getMarinadeApy(): Double {
        apyCacheMutex.withLock {
            val now = System.currentTimeMillis()
            if (now - apyCacheTimestamp < APY_CACHE_TTL_MS && cachedApy > 0) {
                return cachedApy
            }
        }

        return withContext(Dispatchers.IO) {
            try {
                val response: String = httpClient.get(MARINADE_STATS_API).body()
                val parsed = json.parseToJsonElement(response).jsonObject
                val apy = parsed["avg_staking_apy"]?.jsonPrimitive?.content?.toDoubleOrNull()
                    ?: FALLBACK_APY

                apyCacheMutex.withLock {
                    cachedApy = apy
                    apyCacheTimestamp = System.currentTimeMillis()
                }

                Log.d(TAG, "Marinade APY: ${"%.2f".format(apy * 100)}%")
                apy
            } catch (e: Exception) {
                Log.w(TAG, "Failed to fetch Marinade APY, using fallback", e)
                FALLBACK_APY
            }
        }
    }

    private val apyCacheMutex = Mutex()
    private var cachedApy: Double = 0.0
    private var apyCacheTimestamp: Long = 0L

    companion object {
        private const val TAG = "PriceFeed"
        private const val JUPITER_PRICE_API = "https://api.jup.ag/price/v2"
        private const val MARINADE_STATS_API = "https://api.marinade.finance/msol/apy/30d"
        private const val CACHE_TTL_MS = 60_000L
        private const val APY_CACHE_TTL_MS = 300_000L // 5 min cache for APY
        private const val FALLBACK_APY = 0.068 // ~6.8% fallback
    }
}
