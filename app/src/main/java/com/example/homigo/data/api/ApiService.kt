package com.example.homigo.data.api

import com.example.homigo.data.model.*
import retrofit2.http.*

interface ApiService {

    @POST("auth/register")
    suspend fun register(@Body body: Map<String, String>): AuthResponse

    @POST("auth/login")
    suspend fun login(@Body body: Map<String, String>): AuthResponse

    @GET("profile/me")
    suspend fun getProfile(@Header("Authorization") token: String): Profile

    @POST("profile/update")
    suspend fun updateProfile(
        @Header("Authorization") token: String,
        @Body body: Map<String, Any>
    ): OkResponse

    @POST("profile/verify")
    suspend fun verifyProfile(
        @Header("Authorization") token: String,
        @Body body: Map<String, String>
    ): OkResponse

    @GET("profile/colleges")
    suspend fun getColleges(): List<College>

    @GET("matches")
    suspend fun getMatches(@Header("Authorization") token: String): List<Match>

    @POST("requests/send")
    suspend fun sendRequest(
        @Header("Authorization") token: String,
        @Body body: Map<String, String>
    ): OkResponse

    @GET("requests/list")
    suspend fun getRequests(@Header("Authorization") token: String): RequestsResponse

    @POST("requests/respond")
    suspend fun respondToRequest(
        @Header("Authorization") token: String,
        @Body body: Map<String, Any>
    ): OkResponse

    @GET("chat/list")
    suspend fun getChatList(@Header("Authorization") token: String): List<Profile>

    @GET("chat/messages/{otherUserId}")
    suspend fun getChatMessages(
        @Header("Authorization") token: String,
        @Path("otherUserId") otherUserId: Int
    ): List<ChatMessage>

    @POST("chat/send")
    suspend fun sendChatMessage(
        @Header("Authorization") token: String,
        @Body body: Map<String, Any>
    ): ChatMessage

    @POST("expenses/add")
    suspend fun addExpense(
        @Header("Authorization") token: String,
        @Body body: Map<String, Any>
    ): OkResponse

    @GET("expenses/list")
    suspend fun getExpenses(@Header("Authorization") token: String): List<Expense>

    @POST("expenses/pay")
    suspend fun payExpense(
        @Header("Authorization") token: String,
        @Body body: Map<String, Int>
    ): OkResponse

    @GET("expenses/summary")
    suspend fun getExpenseSummary(@Header("Authorization") token: String): ExpenseSummary

    @POST("reviews/add")
    suspend fun addReview(
        @Header("Authorization") token: String,
        @Body body: Map<String, Any>
    ): OkResponse

    @GET("reviews/list/{userId}")
    suspend fun getReviews(
        @Header("Authorization") token: String,
        @Path("userId") userId: Int
    ): ReviewSummary

    @POST("ai/generate-bio")
    suspend fun generateBio(
        @Header("Authorization") token: String,
        @Body body: Map<String, String>
    ): BioResponse

    @POST("ai/chatbot")
    suspend fun chatbot(
        @Header("Authorization") token: String,
        @Body body: Map<String, String>
    ): ChatbotResponse

    @GET("ai/detect-fake/{userId}")
    suspend fun detectFake(
        @Header("Authorization") token: String,
        @Path("userId") userId: Int
    ): RiskFactorReport
}
