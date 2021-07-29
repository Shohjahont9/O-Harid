package lars_lion.dev.o_harid.network

import lars_lion.dev.o_harid.network.response.addFavourite.AddFavouriteResponse
import lars_lion.dev.o_harid.network.response.bestSeller.BestSellerResponse
import lars_lion.dev.o_harid.network.response.bookDetail.BookDetailResponse
import lars_lion.dev.o_harid.network.response.bookType.BookTypeResponse
import lars_lion.dev.o_harid.network.response.comments.CommentResponse
import lars_lion.dev.o_harid.network.response.createCard.CreateCardResponse
import lars_lion.dev.o_harid.network.response.favourite.FavouriteBookResponse
import lars_lion.dev.o_harid.network.response.login.LoginResponse
import lars_lion.dev.o_harid.network.response.nowadays.NowadaysResponse
import lars_lion.dev.o_harid.network.response.register.RegisterResponse
import lars_lion.dev.o_harid.network.response.verifyCode.VerifyCodeResponse
import lars_lion.dev.o_harid.ui.favourite.FavouriteRepository
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
    ): BestSellerResponse

    @Headers("Content-Type: application/json")
    @GET("api/book/latest/{size}")
    suspend fun nowadaysBooks(
        @Header("Authorization") token: String,
        @Path("size") size: Int
    ): NowadaysResponse

    @Headers("Content-Type: application/json")
    @GET("/api/bookType/get/all")
    suspend fun bookType(
        @Header("Authorization") token: String,
    ): BookTypeResponse

    @Headers("Content-Type: application/json")
    @GET("/api/comment/2")
    suspend fun comments(
        @Header("Authorization") token: String,
    ): CommentResponse

    @Headers("Content-Type: application/json")
    @GET("api/book/one/{id}")
    suspend fun bookDetails(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): BookDetailResponse

    @Headers("Content-Type: application/json")
    @GET("api/book/lib/books")
    suspend fun favouriteBook(
        @Header("Authorization") token: String,
    ): FavouriteBookResponse

    @Headers("Content-Type: application/json")
    @POST("api/auth/add/library/{id}")
    suspend fun addFavouriteBook(
        @Header("Authorization") token: String,
        @Path("id") id:Int
    ): AddFavouriteResponse

    @Headers("Content-Type: application/json")
    @POST("/api/payment/card/create")
    suspend fun createCard(
        @Header("Authorization") token: String,
        @Body body:String
    ): CreateCardResponse

    @Headers("Content-Type: application/json")
    @POST("/api/payment/verify/code")
    suspend fun getVerifyCode(
        @Header("Authorization") token: String,
        @Query("code") code:String
    ): VerifyCodeResponse

}