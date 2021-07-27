package lars_lion.dev.o_harid.network

import lars_lion.dev.o_harid.network.response.login.LoginResponse
import lars_lion.dev.o_harid.network.response.register.RegisterResponse
import retrofit2.Response
import retrofit2.http.*


interface ApiService {

    @Headers("Content-Type: application/json")
    @POST("api/auth/register")
    suspend fun register(
        @Body data: String,
    ): Response<RegisterResponse>

    @Headers("Content-Type: application/json")
    @POST("api/auth/login")
    suspend fun login(
        @Body data: String,
    ): Response<LoginResponse>

    @Headers("Content-Type: application/json")
    @GET("book/get/best/seller")
    suspend fun bestSeller(
        @Header("Authorization") token: String,
        @Query("page") page: Int
    ): Response<LoginResponse>


}