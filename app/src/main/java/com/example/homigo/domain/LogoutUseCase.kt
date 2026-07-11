package com.example.homigo.domain

  import com.example.homigo.data.repository.LogoutRepository
  import com.example.homigo.data.repository.LogoutRepositoryImpl
  import com.example.homigo.data.repository.HomigoRepository
  import retrofit2.HttpException
  import java.io.IOException

  sealed class LogoutResult {
      data class Success(val message: String) : LogoutResult()
      data class NoInternet(val message: String) : LogoutResult()
      data class Failure(val message: String) : LogoutResult()
  }

  class LogoutUseCase(
      private val logoutRepository: LogoutRepository = LogoutRepositoryImpl()
  ) {
      suspend fun execute(): LogoutResult {
          return try {
              // Try calling the remote logout endpoint
              logoutRepository.logout()
              
              // Clear local session after successful remote call
              HomigoRepository.clearSession()
              LogoutResult.Success("You've been logged out successfully.")
          } catch (e: IOException) {
              // Internet is unavailable
              // We STILL log out locally as per "Still log out locally."
              HomigoRepository.clearSession()
              LogoutResult.NoInternet("No internet connection.")
          } catch (e: HttpException) {
              // Server logout fails (e.g. HTTP 500, 502, etc.)
              // We STILL log out locally and show "Logged out successfully."
              HomigoRepository.clearSession()
              LogoutResult.Success("Logged out successfully.")
          } catch (e: Exception) {
              // For any other unexpected/local failures (e.g., local storage errors)
              // If local logout succeeds but server logout fails: Still log out locally
              try {
                  HomigoRepository.clearSession()
                  LogoutResult.Success("Logged out successfully.")
              } catch (localEx: Exception) {
                  LogoutResult.Failure("Unable to log out. Please try again.")
              }
          }
      }
  }
