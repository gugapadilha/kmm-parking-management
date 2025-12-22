package com.example.gestodeestacionamento.data.remote

import com.example.gestodeestacionamento.data.remote.dto.CloseSessionResponse
import com.example.gestodeestacionamento.data.remote.dto.LoginResponse
import com.example.gestodeestacionamento.data.remote.dto.ManualLoadResponse
import com.example.gestodeestacionamento.platform.PlatformLogger
import com.example.gestodeestacionamento.platform.createHttpClientWithConfig
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess
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

    private val client = createHttpClientWithConfig()

    override suspend fun login(email: String, password: String): Result<LoginResponse> {
        return try {
            val response = client.post("$baseUrl/user/login") {
                header("clientSide", "app")
                contentType(ContentType.Application.FormUrlEncoded)
                setBody("email=$email&password=$password")
            }
            
            PlatformLogger.d("ApiService", "Login response status: ${response.status}")
            PlatformLogger.d("ApiService", "Login response headers: ${response.headers}")
            
            if (response.status.isSuccess()) {
                try {
                    // Ler como string primeiro para poder fazer debug
                    val responseText = response.body<String>()
                    PlatformLogger.d("ApiService", "Login response body: $responseText")
                    
                    // Tentar parsear como LoginResponse
                    val json = Json { 
                        ignoreUnknownKeys = true
                        isLenient = true
                    }
                    val loginResponse = json.decodeFromString<LoginResponse>(responseText)
                    val accessToken = loginResponse.data?.user?.accessToken
                    val userId = loginResponse.data?.user?.userId
                    val sessionId = loginResponse.data?.session?.sessionId
                    PlatformLogger.d("ApiService", "Parsed login response: accessToken=${accessToken?.take(20)}..., userId=$userId, sessionId=$sessionId")
                    
                    // Se não tem token, verificar estrutura da resposta
                    if (accessToken == null) {
                        PlatformLogger.w("ApiService", "AccessToken is null. Checking response structure...")
                        try {
                            val map = json.parseToJsonElement(responseText).jsonObject
                            PlatformLogger.d("ApiService", "Response keys: ${map.keys}")
                            PlatformLogger.d("ApiService", "Response content: $map")
                        } catch (e: Exception) {
                            PlatformLogger.e("ApiService", "Error parsing JSON structure", e)
                        }
                    }
                    
                    Result.success(loginResponse)
                } catch (e: Exception) {
                    PlatformLogger.e("ApiService", "Error parsing login response", e)
                    Result.failure(Exception("Erro ao processar resposta: ${e.message}"))
                }
            } else {
                val errorMessage = try {
                    response.body<String>()
                } catch (e: Exception) {
                    "Login failed: ${response.status}"
                }
                PlatformLogger.e("ApiService", "Login failed: $errorMessage", null)
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            PlatformLogger.e("ApiService", "Login exception", e)
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
            
            PlatformLogger.d("ApiService", "Manual load response status: ${response.status}")
            
            if (response.status.isSuccess()) {
                try {
                    val responseText = response.body<String>()
                    PlatformLogger.d("ApiService", "Manual load response body (first 2000 chars): ${responseText.take(2000)}")
                    if (responseText.length > 2000) {
                        PlatformLogger.d("ApiService", "Manual load response body (remaining): ${responseText.drop(2000).take(2000)}")
                    }
                    
                    // Verificar estrutura do JSON antes de fazer parse
                    val json = Json {
                        ignoreUnknownKeys = true
                        isLenient = true
                    }
                    try {
                        val jsonElement = json.parseToJsonElement(responseText)
                        val jsonObject = jsonElement.jsonObject
                        PlatformLogger.d("ApiService", "Response JSON keys: ${jsonObject.keys}")
                        
                        // Verificar estrutura de data
                        if (jsonObject.containsKey("data")) {
                            val dataObject = jsonObject["data"]?.jsonObject
                            PlatformLogger.d("ApiService", "Data object keys: ${dataObject?.keys}")
                            
                            if (dataObject != null) {
                                if (dataObject.containsKey("prices")) {
                                    val pricesElement = dataObject["prices"]
                                    PlatformLogger.d("ApiService", "Prices field exists in data: $pricesElement")
                                    try {
                                        val pricesArray = pricesElement?.jsonArray
                                        if (pricesArray != null && pricesArray.isNotEmpty()) {
                                            PlatformLogger.d("ApiService", "Prices array size: ${pricesArray.size}")
                                            pricesArray.forEachIndexed { index, priceElement ->
                                                PlatformLogger.d("ApiService", "Price[$index] element type: ${priceElement::class.simpleName}")
                                                if (priceElement.jsonObject != null) {
                                                    val priceObj = priceElement.jsonObject
                                                    PlatformLogger.d("ApiService", "Price[$index] keys: ${priceObj.keys}")
                                                    // Log todos os campos do preço
                                                    priceObj.keys.forEach { key ->
                                                        val value = priceObj[key]
                                                        val valueStr = when {
                                                            value?.jsonPrimitive != null -> value.jsonPrimitive.contentOrNull ?: "null"
                                                            value?.jsonArray != null -> "Array[${value.jsonArray.size}]"
                                                            value?.jsonObject != null -> "Object[${value.jsonObject.keys.size} keys]"
                                                            else -> value.toString()
                                                        }
                                                        PlatformLogger.d("ApiService", "  Price[$index].$key = $valueStr")
                                                    }
                                                } else {
                                                    PlatformLogger.w("ApiService", "Price[$index] is not a JSON object: $priceElement")
                                                }
                                            }
                                        } else {
                                            PlatformLogger.w("ApiService", "Prices array is null or empty")
                                        }
                                    } catch (e: Exception) {
                                        PlatformLogger.e("ApiService", "Error inspecting prices array", e)
                                    }
                                } else {
                                    PlatformLogger.w("ApiService", "WARNING: 'prices' field not found in data!")
                                }
                                
                                if (dataObject.containsKey("paymentMethods")) {
                                    val paymentMethodsElement = dataObject["paymentMethods"]
                                    PlatformLogger.d("ApiService", "PaymentMethods field exists in data: $paymentMethodsElement")
                                } else {
                                    PlatformLogger.w("ApiService", "WARNING: 'paymentMethods' field not found in data!")
                                }
                            }
                        } else {
                            PlatformLogger.w("ApiService", "WARNING: 'data' field not found in response!")
                        }
                    } catch (e: Exception) {
                        PlatformLogger.e("ApiService", "Error analyzing JSON structure", e)
                    }
                    
                    val manualLoadResponse = json.decodeFromString<ManualLoadResponse>(responseText)
                    val prices = manualLoadResponse.data?.prices
                    val paymentMethods = manualLoadResponse.data?.paymentMethods
                    PlatformLogger.d("ApiService", "Parsed manual load: prices=${prices?.size ?: "null"}, paymentMethods=${paymentMethods?.size ?: "null"}")
                    
                    if (prices == null || prices.isEmpty()) {
                        PlatformLogger.w("ApiService", "WARNING: prices is null or empty in parsed response!")
                    } else {
                        prices.forEachIndexed { index, price ->
                            PlatformLogger.d("ApiService", "Price table[$index]: normalizedId=${price.normalizedId}, normalizedName=${price.normalizedName}")
                            PlatformLogger.d("ApiService", "  establishmentId=${price.establishmentId}, typePrice=${price.typePrice}")
                            PlatformLogger.d("ApiService", "  tolerance=${price.tolerance}, maximumPeriod=${price.maximumPeriod}, maximumValue=${price.maximumValue}")
                            PlatformLogger.d("ApiService", "  items count: ${price.items?.size ?: 0}")
                            price.items?.forEachIndexed { itemIndex, item ->
                                PlatformLogger.d("ApiService", "    Item[$itemIndex]: period=${item.normalizedPeriod}, price=${item.normalizedPrice}, since=${item.normalizedSince}")
                            }
                        }
                    }
                    if (paymentMethods == null || paymentMethods.isEmpty()) {
                        PlatformLogger.w("ApiService", "WARNING: paymentMethods is null or empty in parsed response!")
                    } else {
                        paymentMethods.forEach { method ->
                            PlatformLogger.d("ApiService", "Payment method: id=${method.establishmentPaymentMethodId}, name=${method.paymentMethodName}")
                        }
                    }
                    
                    Result.success(manualLoadResponse)
                } catch (e: Exception) {
                    PlatformLogger.e("ApiService", "Error parsing manual load response", e)
                    Result.failure(e)
                }
            } else {
                val errorMessage = try {
                    response.body<String>()
                } catch (e: Exception) {
                    "Manual load failed: ${response.status}"
                }
                PlatformLogger.e("ApiService", "Manual load failed: $errorMessage", null)
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            PlatformLogger.e("ApiService", "Manual load exception", e)
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
            PlatformLogger.e("ApiService", "Close session exception", e)
            Result.failure(e)
        }
    }
}

