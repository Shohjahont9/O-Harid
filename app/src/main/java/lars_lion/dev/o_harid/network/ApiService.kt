package lars_lion.dev.o_harid.network

import lars_lion.dev.o_harid.network.response.bestSeller.BestSellerResponse
import lars_lion.dev.o_harid.network.response.login.LoginResponse
import lars_lion.dev.o_harid.network.response.register.RegisterResponse
import retrofit2.Response
import retrofit2.http.*


interface ApiService {

    @Headers("Content-Type: application/json")
    @POST("api/auth/register")
    suspend fun register(
        @Body data: String,
    ): RegisterResponse

    @Headers("Content-Type: application/json")
    @POST("api/auth/login")
    suspend fun login(
        @Body data: String,
    ): LoginResponse

    @Headers("Content-Type: application/json")
    @GET("api/book/get/best/seller")
    suspend fun bestSeller(
        @Header("Authorization") token: String,
        @Query("size") page: Int
    ): Response<BestSellerResponse>


}