package com.example.foodmart.data.remote

import com.example.foodmart.data.remote.dto.PurchaseRequestDto
import com.example.foodmart.data.remote.dto.PurchaseResponseDto
import java.util.UUID
import javax.inject.Inject
import kotlinx.coroutines.delay
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * Implementation of the purchase API as outlined by the contract doc :)
 */
interface PurchaseApi {
    suspend fun purchase(request: PurchaseRequestDto): PurchaseResponseDto
}

interface PurchaseService {
    @POST("api/purchase")
    suspend fun purchase(@Body request: PurchaseRequestDto): PurchaseResponseDto
}

class RetrofitPurchaseApi @Inject constructor(
    private val service: PurchaseService,
) : PurchaseApi {
    override suspend fun purchase(request: PurchaseRequestDto): PurchaseResponseDto =
        service.purchase(request)
}

class FakePurchaseApi @Inject constructor() : PurchaseApi {
    override suspend fun purchase(request: PurchaseRequestDto): PurchaseResponseDto {
        delay(FAKE_NETWORK_DELAY_MS)
        return PurchaseResponseDto(
            orderUuid = UUID.randomUUID().toString(),
            status = "completed",
        )
    }

    private companion object {
        const val FAKE_NETWORK_DELAY_MS = 800L
    }
}
