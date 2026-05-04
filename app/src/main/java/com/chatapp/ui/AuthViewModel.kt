package com.chatapp.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chatapp.data.local.TokenStorage
import com.chatapp.data.model.*
import com.chatapp.data.network.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

data class AuthState(
    val isLoading: Boolean = false,
    val isAuthenticated: Boolean = false,
    val user: User? = null,
    val error: String? = null
)

class AuthViewModel(private val context: Context) : ViewModel() {
    private val tokenStorage = TokenStorage(context)
    
    private val _state = MutableStateFlow(AuthState())
    val state: StateFlow<AuthState> = _state.asStateFlow()

    init {
        checkAuth()
    }

    fun checkAuth() {
        viewModelScope.launch {
            val token = tokenStorage.token.first()
            if (token != null) {
                try {
                    RetrofitClient.setToken(token)
                    val response = RetrofitClient.apiService.verifyToken()
                    if (response.isSuccessful) {
                        val userMap = response.body()?.get("user") as? Map<*, *>
                        val user = userMap?.let { User.fromMap(it) }
                        _state.value = AuthState(isAuthenticated = true, user = user)
                    } else {
                        tokenStorage.clear()
                        RetrofitClient.clearToken()
                    }
                } catch (e: Exception) {
                    tokenStorage.clear()
                    RetrofitClient.clearToken()
                    _state.value = _state.value.copy(error = e.message)
                }
            }
        }
    }

    fun login(username: String, password: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            try {
                val response = RetrofitClient.apiService.login(LoginRequest(username, password))
                if (response.isSuccessful && response.body() != null) {
                    val authResponse = response.body()!!
                    tokenStorage.saveToken(authResponse.token)
                    RetrofitClient.setToken(authResponse.token)
                    _state.value = AuthState(isAuthenticated = true, user = authResponse.user)
                } else {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        error = "Login failed: ${response.code()}"
                    )
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(isLoading = false, error = e.message)
            }
        }
    }

    fun register(username: String, password: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            try {
                val response = RetrofitClient.apiService.register(RegisterRequest(username, password))
                if (response.isSuccessful && response.body() != null) {
                    val authResponse = response.body()!!
                    tokenStorage.saveToken(authResponse.token)
                    RetrofitClient.setToken(authResponse.token)
                    _state.value = AuthState(isAuthenticated = true, user = authResponse.user)
                } else {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        error = "Registration failed: ${response.code()}"
                    )
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(isLoading = false, error = e.message)
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            tokenStorage.clear()
            RetrofitClient.clearToken()
            _state.value = AuthState()
        }
    }

    fun clearError() {
        _state.value = _state.value.copy(error = null)
    }
}
