package com.example.homigo.data.local

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import com.example.homigo.data.model.User
import com.google.gson.Gson

class SessionManager(context: Context) {
    private val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
    
    private val prefs: SharedPreferences = EncryptedSharedPreferences.create(
        "homigo_secure_prefs",
        masterKeyAlias,
        context,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )
    
    private val gson = Gson()

    fun saveSession(token: String, user: User) {
        prefs.edit().apply {
            putString("jwt_token", token)
            putString("user_info", gson.toJson(user))
            apply()
        }
    }

    fun getToken(): String? {
        return prefs.getString("jwt_token", null)
    }

    fun getUser(): User? {
        val userJson = prefs.getString("user_info", null) ?: return null
        return try {
            gson.fromJson(userJson, User::class.java)
        } catch (e: Exception) {
            null
        }
    }

    fun clearSession() {
        prefs.edit().apply {
            remove("jwt_token")
            remove("user_info")
            apply()
        }
    }
}
