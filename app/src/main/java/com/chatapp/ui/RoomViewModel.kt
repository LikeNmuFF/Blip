package com.chatapp.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chatapp.data.local.TokenStorage
import com.chatapp.data.model.*
import com.chatapp.data.network.RetrofitClient
import com.chatapp.data.network.SocketService
import io.socket.emitter.Emitter
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.json.JSONObject

data class ChatState(
    val rooms: List<Room> = emptyList(),
    val messages: List<Message> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

class RoomViewModel(private val context: Context) : ViewModel() {
    private val tokenStorage = TokenStorage(context)
    
    private val _state = MutableStateFlow(ChatState())
    val state: StateFlow<ChatState> = _state.asStateFlow()

    fun loadRooms() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            try {
                val response = RetrofitClient.apiService.getRooms()
                if (response.isSuccessful) {
                    _state.value = _state.value.copy(
                        rooms = response.body() ?: emptyList(),
                        isLoading = false
                    )
                } else {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        error = "Failed to load rooms"
                    )
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(isLoading = false, error = e.message)
            }
        }
    }

    fun loadMessages(roomId: Int) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            try {
                val response = RetrofitClient.apiService.getMessages(roomId)
                if (response.isSuccessful) {
                    _state.value = _state.value.copy(
                        messages = response.body() ?: emptyList(),
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(isLoading = false, error = e.message)
            }
        }
    }

    fun sendMessage(roomId: Int, content: String) {
        viewModelScope.launch {
            try {
                val token = tokenStorage.token.first()
                if (token != null) {
                    val response = RetrofitClient.apiService.sendMessage(
                        roomId,
                        MessageRequest(content)
                    )
                    if (response.isSuccessful && response.body() != null) {
                        val currentMessages = _state.value.messages.toMutableList()
                        currentMessages.add(response.body()!!)
                        _state.value = _state.value.copy(messages = currentMessages)
                        
                        // Also send via WebSocket
                        SocketService.instance.sendMessage(roomId, content, token)
                    }
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(error = e.message)
            }
        }
    }

    fun setupSocketListener() {
        val listener = Emitter.Listener { args ->
            try {
                val data = args[0] as JSONObject
                val message = Message(
                    id = data.optInt("id"),
                    roomId = data.optInt("room_id"),
                    userId = data.optInt("user_id"),
                    username = data.optString("username"),
                    avatarColor = data.optString("avatar_color"),
                    content = data.optString("content"),
                    createdAt = data.optString("created_at")
                )
                
                viewModelScope.launch {
                    val currentMessages = _state.value.messages.toMutableList()
                    if (!currentMessages.any { it.id == message.id }) {
                        currentMessages.add(message)
                        _state.value = _state.value.copy(messages = currentMessages)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        SocketService.instance.onMessage(listener)
    }

    fun cleanupSocketListener() {
        SocketService.instance.offMessage()
    }
}
