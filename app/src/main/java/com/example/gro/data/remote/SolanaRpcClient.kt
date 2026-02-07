package com.example.gro.data.remote

import com.example.gro.data.remote.dto.TokenAccountInfo
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.int
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.long
import kotlinx.serialization.json.put
import kotlinx.serialization.json.putJsonArray
import kotlinx.serialization.json.putJsonObject
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SolanaRpcClient @Inject constructor(
    private val httpClient: HttpClient,
) {
    private val rpcUrl = "https://api.devnet.solana.com"
    private val json = Json { ignoreUnknownKeys = true }

    suspend fun getBalance(publicKey: String): Long = withContext(Dispatchers.IO) {
        val request = buildJsonObject {
            put("jsonrpc", "2.0")
            put("id", 1)
            put("method", "getBalance")
            putJsonArray("params") {
                add(JsonPrimitive(publicKey))
            }
        }

        val response: String = httpClient.post(rpcUrl) {
            contentType(ContentType.Application.Json)
            setBody(request.toString())
        }.body()

        val parsed = json.parseToJsonElement(response).jsonObject
        parsed["result"]?.jsonObject?.get("value")?.jsonPrimitive?.long ?: 0L
    }

    suspend fun getTokenAccounts(publicKey: String): List<TokenAccountInfo> = withContext(Dispatchers.IO) {
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

        val response: String = httpClient.post(rpcUrl) {
            contentType(ContentType.Application.Json)
            setBody(request.toString())
        }.body()

        val parsed = json.parseToJsonElement(response).jsonObject
        val accounts = parsed["result"]?.jsonObject
            ?.get("value")?.jsonArray ?: return@withContext emptyList()

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
