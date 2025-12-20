package com.example.gestodeestacionamento.data.remote

import android.util.Log
import com.example.gestodeestacionamento.data.remote.dto.CloseSessionResponse
import com.example.gestodeestacionamento.data.remote.dto.LoginResponse
import com.example.gestodeestacionamento.data.remote.dto.ManualLoadResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

interface ApiService {
    suspend fun login(email: String, password: String): Result<LoginResponse>
    suspend fun manualLoad(userId: Long, establishmentId: Long, token: String): Result<ManualLoadResponse>
    suspend fun closeSession(
        userId: Long,
        establishmentId: Long,
        sessionId: Long,
        token: String,
        dateTime: String? = null
    ): Result<CloseSessionResponse>
}

class ApiServiceImpl(
    private val baseUrl: String = "https://dev.app.jumpparkapi.com.br/api"
) : ApiService {

    private val client = HttpClient(Android) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                isLenient = true
                encodeDefaults = false
            })
        }
        install(Logging) {
            level = LogLevel.INFO
        }
    }

    override suspend fun login(email: String, password: String): Result<LoginResponse> {
        return try {
            val response = client.post("$baseUrl/user/login") {
                header("clientSide", "app")
                contentType(ContentType.Application.FormUrlEncoded)
                setBody("email=$email&password=$password")
            }
            
            Log.d("ApiService", "Login response status: ${response.status}")
            Log.d("ApiService", "Login response headers: ${response.headers}")
            
            if (response.status.isSuccess()) {
                try {
                    // Ler como string primeiro para poder fazer debug
                    val responseText = response.body<String>()
                    Log.d("ApiService", "Login response body: $responseText")
                    
                    // Tentar parsear como LoginResponse
                    val json = Json { 
                        ignoreUnknownKeys = true
                        isLenient = true
                    }
                    val loginResponse = json.decodeFromString<LoginResponse>(responseText)
                    val accessToken = loginResponse.data?.user?.accessToken
                    val userId = loginResponse.data?.user?.userId
                    val sessionId = loginResponse.data?.session?.sessionId
                    Log.d("ApiService", "Parsed login response: accessToken=${accessToken?.take(20)}..., userId=$userId, sessionId=$sessionId")
                    
                    // Se não tem token, verificar estrutura da resposta
                    if (accessToken == null) {
                        Log.w("ApiService", "AccessToken is null. Checking response structure...")
                        try {
                            val map = json.parseToJsonElement(responseText).jsonObject
                            Log.d("ApiService", "Response keys: ${map.keys}")
                            Log.d("ApiService", "Response content: $map")
                        } catch (e: Exception) {
                            Log.e("ApiService", "Error parsing JSON structure", e)
                        }
                    }
                    
                    Result.success(loginResponse)
                } catch (e: Exception) {
                    Log.e("ApiService", "Error parsing login response", e)
                    Result.failure(Exception("Erro ao processar resposta: ${e.message}"))
                }
            } else {
                val errorMessage = try {
                    response.body<String>()
                } catch (e: Exception) {
                    "Login failed: ${response.status}"
                }
                Log.e("ApiService", "Login failed: $errorMessage")
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Log.e("ApiService", "Login exception", e)
            Result.failure(e)
        }
    }

    override suspend fun manualLoad(
        userId: Long,
        establishmentId: Long,
        token: String
    ): Result<ManualLoadResponse> {
        return try {
            val response = client.get("$baseUrl/$userId/establishment/$establishmentId/sync/manual") {
                header("Authorization", "Bearer $token")
                header("clientSide", "app")
            }
            
            Log.d("ApiService", "Manual load response status: ${response.status}")
            
            if (response.status.isSuccess()) {
                try {
                    val responseText = response.body<String>()
                    Log.d("ApiService", "Manual load response body (first 2000 chars): ${responseText.take(2000)}")
                    if (responseText.length > 2000) {
                        Log.d("ApiService", "Manual load response body (remaining): ${responseText.drop(2000).take(2000)}")
                    }
                    
                    // Verificar estrutura do JSON antes de fazer parse
                    val json = Json {
                        ignoreUnknownKeys = true
                        isLenient = true
                    }
                    try {
                        val jsonElement = json.parseToJsonElement(responseText)
                        val jsonObject = jsonElement.jsonObject
                        Log.d("ApiService", "Response JSON keys: ${jsonObject.keys}")
                        
                        // Verificar estrutura de data
                        if (jsonObject.containsKey("data")) {
                            val dataObject = jsonObject["data"]?.jsonObject
                            Log.d("ApiService", "Data object keys: ${dataObject?.keys}")
                            
                            if (dataObject != null) {
                                if (dataObject.containsKey("prices")) {
                                    val pricesElement = dataObject["prices"]
                                    Log.d("ApiService", "Prices field exists in data: $pricesElement")
                                    try {
                                        val pricesArray = pricesElement?.jsonArray
                                        if (pricesArray != null && pricesArray.isNotEmpty()) {
                                            Log.d("ApiService", "Prices array size: ${pricesArray.size}")
                                            pricesArray.forEachIndexed { index, priceElement ->
                                                Log.d("ApiService", "Price[$index] element type: ${priceElement::class.simpleName}")
                                                if (priceElement.jsonObject != null) {
                                                    val priceObj = priceElement.jsonObject
                                                    Log.d("ApiService", "Price[$index] keys: ${priceObj.keys}")
                                                    // Log todos os campos do preço
                                                    priceObj.keys.forEach { key ->
                                                        val value = priceObj[key]
                                                        val valueStr = when {
                                                            value?.jsonPrimitive != null -> value.jsonPrimitive.contentOrNull ?: "null"
                                                            value?.jsonArray != null -> "Array[${value.jsonArray.size}]"
                                                            value?.jsonObject != null -> "Object[${value.jsonObject.keys.size} keys]"
                                                            else -> value.toString()
                                                        }
                                                        Log.d("ApiService", "  Price[$index].$key = $valueStr")
                                                    }
                                                } else {
                                                    Log.w("ApiService", "Price[$index] is not a JSON object: $priceElement")
                                                }
                                            }
                                        } else {
                                            Log.w("ApiService", "Prices array is null or empty")
                                        }
                                    } catch (e: Exception) {
                                        Log.e("ApiService", "Error inspecting prices array", e)
                                    }
                                } else {
                                    Log.w("ApiService", "WARNING: 'prices' field not found in data!")
                                }
                                
                                if (dataObject.containsKey("paymentMethods")) {
                                    val paymentMethodsElement = dataObject["paymentMethods"]
                                    Log.d("ApiService", "PaymentMethods field exists in data: $paymentMethodsElement")
                                } else {
                                    Log.w("ApiService", "WARNING: 'paymentMethods' field not found in data!")
                                }
                            }
                        } else {
                            Log.w("ApiService", "WARNING: 'data' field not found in response!")
                        }
                    } catch (e: Exception) {
                        Log.e("ApiService", "Error analyzing JSON structure", e)
                    }
                    
                    val manualLoadResponse = json.decodeFromString<ManualLoadResponse>(responseText)
                    val prices = manualLoadResponse.data?.prices
                    val paymentMethods = manualLoadResponse.data?.paymentMethods
                    Log.d("ApiService", "Parsed manual load: prices=${prices?.size ?: "null"}, paymentMethods=${paymentMethods?.size ?: "null"}")
                    
                    if (prices == null || prices.isEmpty()) {
                        Log.w("ApiService", "WARNING: prices is null or empty in parsed response!")
                    } else {
                        prices.forEachIndexed { index, price ->
                            Log.d("ApiService", "Price table[$index]: normalizedId=${price.normalizedId}, normalizedName=${price.normalizedName}")
                            Log.d("ApiService", "  establishmentId=${price.establishmentId}, typePrice=${price.typePrice}")
                            Log.d("ApiService", "  tolerance=${price.tolerance}, maximumPeriod=${price.maximumPeriod}, maximumValue=${price.maximumValue}")
                            Log.d("ApiService", "  items count: ${price.items?.size ?: 0}")
                            price.items?.forEachIndexed { itemIndex, item ->
                                Log.d("ApiService", "    Item[$itemIndex]: period=${item.normalizedPeriod}, price=${item.normalizedPrice}, since=${item.normalizedSince}")
                            }
                        }
                    }
                    if (paymentMethods == null || paymentMethods.isEmpty()) {
                        Log.w("ApiService", "WARNING: paymentMethods is null or empty in parsed response!")
                    } else {
                        paymentMethods.forEach { method ->
                            Log.d("ApiService", "Payment method: id=${method.establishmentPaymentMethodId}, name=${method.paymentMethodName}")
                        }
                    }
                    
                    Result.success(manualLoadResponse)
                } catch (e: Exception) {
                    Log.e("ApiService", "Error parsing manual load response", e)
                    Result.failure(e)
                }
            } else {
                val errorMessage = try {
                    response.body<String>()
                } catch (e: Exception) {
                    "Manual load failed: ${response.status}"
                }
                Log.e("ApiService", "Manual load failed: $errorMessage")
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Log.e("ApiService", "Manual load exception", e)
            Result.failure(e)
        }
    }

    override suspend fun closeSession(
        userId: Long,
        establishmentId: Long,
        sessionId: Long,
        token: String,
        dateTime: String?
    ): Result<CloseSessionResponse> {
        return try {
            val response = client.post("$baseUrl/$userId/establishment/$establishmentId/session/close/$sessionId") {
                header("Authorization", "Bearer $token")
                header("clientSide", "app")
                if (dateTime != null) {
                    contentType(ContentType.Application.FormUrlEncoded)
                    setBody("dateTime=$dateTime")
                }
            }
            
            if (response.status.isSuccess()) {
                val closeResponse = response.body<CloseSessionResponse>()
                Result.success(closeResponse)
            } else {
                Result.failure(Exception("Close session failed: ${response.status}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

