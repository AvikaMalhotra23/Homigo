package com.example.homigo.data.repository

import com.example.homigo.data.api.ApiClient
import com.example.homigo.data.api.ApiService
import com.example.homigo.data.model.OkResponse

interface LogoutRepository {
    suspend fun logout(): OkResponse
}

class LogoutRepositoryImpl(
    private val api: ApiService = ApiClient.service
) : LogoutRepository {
    override suspend fun logout(): OkResponse {
        val currentToken = HomigoRepository.token.value ?: throw Exception("Not authenticated")
        return api.logout(currentToken)
    }
}
