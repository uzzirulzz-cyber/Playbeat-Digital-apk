package com.example.network

import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface PlaybeatApiService {

    @GET("api/products")
    suspend fun getProducts(
        @Query("limit") limit: Int = 100,
        @Query("category") category: String? = null,
        @Query("search") search: String? = null,
        @Query("active") active: String? = "all"
    ): ApiProductListResponse

    @GET("api/products/{slug}")
    suspend fun getProductBySlug(
        @Path("slug") slug: String
    ): ApiSingleProductResponse

    @GET("api/categories")
    suspend fun getCategories(): ApiCategoriesResponse

    @POST("api/auth/admin/login")
    suspend fun adminLogin(
        @Body req: ApiAdminLoginRequest
    ): ApiAdminLoginResponse

    @POST("api/admin/products")
    suspend fun createProduct(
        @Header("Authorization") authHeader: String,
        @Body payload: ApiProductPayload
    ): ApiSingleProductResponse

    @PUT("api/admin/products/{id}")
    suspend fun updateProduct(
        @Header("Authorization") authHeader: String,
        @Path("id") id: String,
        @Body payload: ApiProductPayload
    ): ApiSingleProductResponse

    @DELETE("api/admin/products/{id}")
    suspend fun deleteProduct(
        @Header("Authorization") authHeader: String,
        @Path("id") id: String
    ): ApiGenericResponse
}
