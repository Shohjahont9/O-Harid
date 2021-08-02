package lars_lion.dev.o_harid.network

import lars_lion.dev.o_harid.network.response.addComment.AddCommentResponse
import lars_lion.dev.o_harid.network.response.addFavourite.AddFavouriteResponse
import lars_lion.dev.o_harid.network.response.bestSeller.BestSellerResponse
import lars_lion.dev.o_harid.network.response.bookDetail.BookDetailResponse
import lars_lion.dev.o_harid.network.response.bookType.BookTypeResponse
import lars_lion.dev.o_harid.network.response.buyBook.BuyBookResponse
import lars_lion.dev.o_harid.network.response.comments.CommentResponse
import lars_lion.dev.o_harid.network.response.createCard.CreateCardResponse
import lars_lion.dev.o_harid.network.response.deleteBookFromLib.DeleteBookFromLibResponse
import lars_lion.dev.o_harid.network.response.favourite.FavouriteBookResponse
import lars_lion.dev.o_harid.network.response.getBookByBookType.GetBookByBookTypeResponse
import lars_lion.dev.o_harid.network.response.getMoney.GetUserMoneyResponse
import lars_lion.dev.o_harid.network.response.login.LoginResponse
import lars_lion.dev.o_harid.network.response.nowadays.NowadaysResponse
import lars_lion.dev.o_harid.network.response.paidBooks.PaidBooksResponse
import lars_lion.dev.o_harid.network.response.register.RegisterResponse
import lars_lion.dev.o_harid.network.response.search.SearchResponse
import lars_lion.dev.o_harid.network.response.verifyCode.VerifyCodeResponse
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
    @GET("/api/comment/{bookId}")
    suspend fun comments(
        @Header("Authorization") token: String,
        @Path("bookId") bookId: String
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
        @Path("id") id: Int
    ): AddFavouriteResponse

    @Headers("Content-Type: application/json")
    @POST("/api/payment/card/create")
    suspend fun createCard(
        @Header("Authorization") token: String,
        @Body body: String
    ): CreateCardResponse

    @Headers("Content-Type: application/json")
    @POST("/api/payment/verify/code")
    suspend fun getVerifyCode(
        @Header("Authorization") token: String,
        @Query("code") code: String
    ): VerifyCodeResponse

    @Headers("Content-Type: application/json")
    @GET("/api/auth")
    suspend fun getUserMoney(
        @Header("Authorization") token: String,
    ): GetUserMoneyResponse

    @Headers("Content-Type: application/json")
    @POST("/api/auth/buy/{id}")
    suspend fun buyBook(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): BuyBookResponse

    @Headers("Content-Type: application/json")
    @GET("/api/book/paid/books")
    suspend fun paidBooks(
        @Header("Authorization") token: String,
    ): PaidBooksResponse

    @Headers("Content-Type: application/json")
    @GET("/api/book")
    suspend fun search(
        @Header("Authorization") token: String,
        @Query("name") name:String
    ): SearchResponse

    @Headers("Content-Type: application/json")
    @GET("/api/book/get/by/type")
    suspend fun getBookByBookType(
        @Header("Authorization") token: String,
        @Query("typeId") type:String
    ): GetBookByBookTypeResponse


    @Headers("Content-Type: application/json")
    @POST("/api/auth/remove/from/library/{bookId}")
    suspend fun deleteBookFromLibrary(
        @Header("Authorization") token: String,
        @Path("bookId") bookId:String
    ): DeleteBookFromLibResponse


    @Headers("Content-Type: application/json")
    @POST("/api/comment")
    suspend fun addComment(
        @Header("Authorization") token: String,
        @Query("text") commentText:String,
        @Query("bookId") bookId:String,
        @Query("evaluate") evaluate:String
    ): AddCommentResponse



}