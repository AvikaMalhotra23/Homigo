package com.example.homigo.data.repository

import com.example.homigo.data.api.ApiClient
import com.example.homigo.data.api.ApiService
import com.example.homigo.data.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

object HomigoRepository {
    private val api: ApiService = ApiClient.service

    private val _token = MutableStateFlow<String?>(null)
    val token: StateFlow<String?> = _token.asStateFlow()

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    private val _myProfile = MutableStateFlow<Profile?>(null)
    val myProfile: StateFlow<Profile?> = _myProfile.asStateFlow()

    fun setSession(jwtToken: String, user: User) {
        _token.value = "Bearer $jwtToken"
        _currentUser.value = user
    }

    fun clearSession() {
        _token.value = null
        _currentUser.value = null
        _myProfile.value = null
    }

    fun updateLocalProfile(profile: Profile) {
        _myProfile.value = profile
        // If gender in profile updates, synchronize user gender
        _currentUser.value = _currentUser.value?.copy(gender = profile.gender ?: _currentUser.value?.gender ?: "male")
    }

    suspend fun register(body: Map<String, String>): AuthResponse {
        val res = api.register(body)
        setSession(res.token, res.user)
        return res
    }

    suspend fun login(body: Map<String, String>): AuthResponse {
        val res = api.login(body)
        setSession(res.token, res.user)
        return res
    }

    suspend fun fetchProfile(): Profile {
        val authToken = _token.value ?: throw Exception("Not authenticated")
        val profile = api.getProfile(authToken)
        _myProfile.value = profile
        return profile
    }

    suspend fun fetchColleges(): List<College> {
        return api.getColleges()
    }

    suspend fun updateProfile(fields: Map<String, Any>): OkResponse {
        val authToken = _token.value ?: throw Exception("Not authenticated")
        val res = api.updateProfile(authToken, fields)
        // Refresh local profile
        try {
            fetchProfile()
        } catch (e: Exception) {
            // Profile was just created, handle gracefully
        }
        return res
    }

    suspend fun verifyProfile(idProofUrl: String): OkResponse {
        val authToken = _token.value ?: throw Exception("Not authenticated")
        val res = api.verifyProfile(authToken, mapOf("idProofUrl" to idProofUrl))
        fetchProfile()
        return res
    }

    suspend fun getMatches(): List<Match> {
        val authToken = _token.value ?: throw Exception("Not authenticated")
        return api.getMatches(authToken)
    }

    suspend fun sendRequest(receiverId: Int): OkResponse {
        val authToken = _token.value ?: throw Exception("Not authenticated")
        return api.sendRequest(authToken, mapOf("receiverId" to receiverId.toString()))
    }

    suspend fun getRequests(): RequestsResponse {
        val authToken = _token.value ?: throw Exception("Not authenticated")
        return api.getRequests(authToken)
    }

    suspend fun respondToRequest(requestId: Int, status: String): OkResponse {
        val authToken = _token.value ?: throw Exception("Not authenticated")
        return api.respondToRequest(authToken, mapOf("requestId" to requestId, "status" to status))
    }

    suspend fun getChatList(): List<Profile> {
        val authToken = _token.value ?: throw Exception("Not authenticated")
        return api.getChatList(authToken)
    }

    suspend fun getChatMessages(otherUserId: Int): List<ChatMessage> {
        val authToken = _token.value ?: throw Exception("Not authenticated")
        return api.getChatMessages(authToken, otherUserId)
    }

    suspend fun sendChatMessage(receiverId: Int, message: String): ChatMessage {
        val authToken = _token.value ?: throw Exception("Not authenticated")
        return api.sendChatMessage(authToken, mapOf("receiverId" to receiverId, "message" to message))
    }

    suspend fun addExpense(title: String, amount: Double, category: String, participantIds: List<Int>): OkResponse {
        val authToken = _token.value ?: throw Exception("Not authenticated")
        return api.addExpense(authToken, mapOf(
            "title" to title,
            "amount" to amount,
            "category" to category,
            "participantIds" to participantIds
        ))
    }

    suspend fun getExpenses(): List<Expense> {
        val authToken = _token.value ?: throw Exception("Not authenticated")
        return api.getExpenses(authToken)
    }

    suspend fun payExpense(splitId: Int): OkResponse {
        val authToken = _token.value ?: throw Exception("Not authenticated")
        return api.payExpense(authToken, mapOf("splitId" to splitId))
    }

    suspend fun getExpenseSummary(): ExpenseSummary {
        val authToken = _token.value ?: throw Exception("Not authenticated")
        return api.getExpenseSummary(authToken)
    }

    suspend fun addReview(revieweeId: Int, cleanliness: Int, respect: Int, timeliness: Int, noise: Int, comment: String): OkResponse {
        val authToken = _token.value ?: throw Exception("Not authenticated")
        return api.addReview(authToken, mapOf(
            "revieweeId" to revieweeId,
            "cleanliness" to cleanliness,
            "respect" to respect,
            "timeliness" to timeliness,
            "noise" to noise,
            "comment" to comment
        ))
    }

    suspend fun getReviews(userId: Int): ReviewSummary {
        val authToken = _token.value ?: throw Exception("Not authenticated")
        return api.getReviews(authToken, userId)
    }

    suspend fun generateBio(input: String): BioResponse {
        val authToken = _token.value ?: throw Exception("Not authenticated")
        return api.generateBio(authToken, mapOf("input" to input))
    }

    suspend fun askChatbot(message: String): ChatbotResponse {
        val authToken = _token.value ?: throw Exception("Not authenticated")
        return api.chatbot(authToken, mapOf("message" to message))
    }

    suspend fun detectFakeProfile(userId: Int): RiskFactorReport {
        val authToken = _token.value ?: throw Exception("Not authenticated")
        return api.detectFake(authToken, userId)
    }

    fun getErrorMessage(e: Throwable): String {
        return if (e is retrofit2.HttpException) {
            try {
                val errorBody = e.response()?.errorBody()?.string()
                org.json.JSONObject(errorBody ?: "").getString("error")
            } catch (ex: Exception) {
                e.message() ?: "HTTP error occurred"
            }
        } else {
            e.message ?: "An unexpected error occurred"
        }
    }
}
