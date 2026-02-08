package com.example.gro.data.remote

import com.example.gro.data.remote.dto.TokenAccountInfo
import com.example.gro.data.remote.dto.TransactionSignatureInfo
import com.example.gro.util.withRetry
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.int
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.long
import kotlinx.serialization.json.put
import kotlinx.serialization.json.putJsonArray
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SolanaRpcClient @Inject constructor(
    private val httpClient: HttpClient,
    private val config: SolanaConfig,
) {
    private val json = Json { ignoreUnknownKeys = true }

    // In-memory balance cache for offline fallback
    private val balanceCacheMutex = Mutex()
    private val balanceCache = mutableMapOf<String, Long>()

    suspend fun getBalance(publicKey: String): Long = withContext(Dispatchers.IO) {
        try {
            val balance = withRetry(tag = "RPC:getBalance") {
                val request = buildJsonObject {
                    put("jsonrpc", "2.0")
                    put("id", 1)
                    put("method", "getBalance")
                    putJsonArray("params") {
                        add(JsonPrimitive(publicKey))
                    }
                }

                val response: String = httpClient.post(config.rpcEndpoint) {
                    contentType(ContentType.Application.Json)
                    setBody(request.toString())
                }.body()

                val parsed = json.parseToJsonElement(response).jsonObject
                parsed["result"]?.jsonObject?.get("value")?.jsonPrimitive?.long ?: 0L
            }

            balanceCacheMutex.withLock { balanceCache[publicKey] = balance }
            balance
        } catch (e: Exception) {
            // Fallback to cached balance
            balanceCacheMutex.withLock { balanceCache[publicKey] } ?: throw e
        }
    }

    suspend fun getLatestBlockhash(): String = withContext(Dispatchers.IO) {
        withRetry(tag = "RPC:getBlockhash") {
            val request = buildJsonObject {
                put("jsonrpc", "2.0")
                put("id", 1)
                put("method", "getLatestBlockhash")
                putJsonArray("params") {
                    add(buildJsonObject {
                        put("commitment", "finalized")
                    })
                }
            }

            val response: String = httpClient.post(config.rpcEndpoint) {
                contentType(ContentType.Application.Json)
                setBody(request.toString())
            }.body()

            val parsed = json.parseToJsonElement(response).jsonObject
            parsed["result"]?.jsonObject
                ?.get("value")?.jsonObject
                ?.get("blockhash")?.jsonPrimitive?.content
                ?: throw IllegalStateException("Failed to get latest blockhash")
        }
    }

    suspend fun getTokenAccounts(publicKey: String): List<TokenAccountInfo> = withContext(Dispatchers.IO) {
        withRetry(tag = "RPC:getTokenAccounts") {
            val request = buildJsonObject {
                put("jsonrpc", "2.0")
                put("id", 1)
                put("method", "getTokenAccountsByOwner")
                putJsonArray("params") {
                    add(JsonPrimitive(publicKey))
                    add(buildJsonObject {
                        put("programId", "TokenkegQfeZyiNwAJbNbGKPFXCWuBvf9Ss623VQ5DA")
                    })
                    add(buildJsonObject {
                        put("encoding", "jsonParsed")
                    })
                }
            }

            val response: String = httpClient.post(config.rpcEndpoint) {
                contentType(ContentType.Application.Json)
                setBody(request.toString())
            }.body()

            val parsed = json.parseToJsonElement(response).jsonObject
            val accounts = parsed["result"]?.jsonObject
                ?.get("value")?.jsonArray ?: return@withRetry emptyList()

            accounts.mapNotNull { account ->
                try {
                    val info = account.jsonObject["account"]?.jsonObject
                        ?.get("data")?.jsonObject
                        ?.get("parsed")?.jsonObject
                        ?.get("info")?.jsonObject ?: return@mapNotNull null

                    val mint = info["mint"]?.jsonPrimitive?.content ?: return@mapNotNull null
                    val tokenAmount = info["tokenAmount"]?.jsonObject ?: return@mapNotNull null
                    val amount = tokenAmount["amount"]?.jsonPrimitive?.content?.toLongOrNull() ?: 0L
                    val decimals = tokenAmount["decimals"]?.jsonPrimitive?.int ?: 0

                    if (amount > 0) {
                        TokenAccountInfo(mint = mint, amount = amount, decimals = decimals)
                    } else null
                } catch (e: Exception) {
                    null
                }
            }
        }
    }

    suspend fun getSignaturesForAddress(
        publicKey: String,
        limit: Int = 20,
    ): List<TransactionSignatureInfo> = withContext(Dispatchers.IO) {
        withRetry(tag = "RPC:getSignatures") {
            val request = buildJsonObject {
                put("jsonrpc", "2.0")
                put("id", 1)
                put("method", "getSignaturesForAddress")
                putJsonArray("params") {
                    add(JsonPrimitive(publicKey))
                    add(buildJsonObject {
                        put("limit", limit)
                    })
                }
            }

            val response: String = httpClient.post(config.rpcEndpoint) {
                contentType(ContentType.Application.Json)
                setBody(request.toString())
            }.body()

            val parsed = json.parseToJsonElement(response).jsonObject
            val results = parsed["result"]?.jsonArray ?: return@withRetry emptyList()

            results.mapNotNull { item ->
                try {
                    val obj = item.jsonObject
                    val signature = obj["signature"]?.jsonPrimitive?.content ?: return@mapNotNull null
                    val blockTime = obj["blockTime"]?.jsonPrimitive?.long
                    val memo = obj["memo"]?.jsonPrimitive?.content
                    val err = obj["err"]

                    if (err != null && err.toString() != "null") return@mapNotNull null

                    TransactionSignatureInfo(
                        signature = signature,
                        blockTime = blockTime,
                        memo = memo,
                    )
                } catch (e: Exception) {
                    null
                }
            }
        }
    }

    suspend fun getTransaction(signature: String): String? = withContext(Dispatchers.IO) {
        withRetry(tag = "RPC:getTransaction") {
            val request = buildJsonObject {
                put("jsonrpc", "2.0")
                put("id", 1)
                put("method", "getTransaction")
                putJsonArray("params") {
                    add(JsonPrimitive(signature))
                    add(buildJsonObject {
                        put("encoding", "jsonParsed")
                        put("maxSupportedTransactionVersion", 0)
                    })
                }
            }

            val response: String = httpClient.post(config.rpcEndpoint) {
                contentType(ContentType.Application.Json)
                setBody(request.toString())
            }.body()

            response
        }
    }
}
