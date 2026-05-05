package com.chatapp.data.network

import com.chatapp.data.model.*
import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    @POST("/api/auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<AuthResponse>

    @POST("/api/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>

    @GET("/api/auth/verify")
    suspend fun verifyToken(): Response<Map<String, Any>>

    @GET("/api/rooms")
    suspend fun getRooms(): Response<List<Room>>

    @GET("/api/rooms/{roomId}/messages")
    suspend fun getMessages(
        @Path("roomId") roomId: Int,
        @Query("limit") limit: Int = 50,
        @Query("offset") offset: Int = 0
    ): Response<List<Message>>

    @POST("/api/rooms/{roomId}/messages")
    suspend fun sendMessage(
        @Path("roomId") roomId: Int,
        @Body request: MessageRequest
    ): Response<Message>

    @POST("/api/rooms")
    suspend fun createRoom(@Body request: CreateRoomRequest): Response<Room>

    @GET("/api/users/search")
    suspend fun searchUsers(@Query("q") query: String): Response<List<User>>
}
