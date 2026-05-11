package com.chatapp.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chatapp.data.local.TokenStorage
import com.chatapp.data.model.*
import com.chatapp.data.network.RetrofitClient
import com.chatapp.data.network.SocketService
import com.chatapp.data.notification.NotificationManagerService
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
    val error: String? = null,
    val searchResults: List<User> = emptyList(),
    val isSearching: Boolean = false,
    val isCreatingRoom: Boolean = false,
    val roomCreated: Boolean = false,
    val createRoomError: String? = null
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
                        
                        // Show notification for new message
                        NotificationManagerService.showMessageNotification(
                            context = context,
                            title = message.username,
                            message = message.content,
                            username = message.username
                        )
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

    fun searchUsers(query: String) {
        if (query.isBlank()) {
            _state.value = _state.value.copy(searchResults = emptyList(), isSearching = false)
            return
        }
        viewModelScope.launch {
            _state.value = _state.value.copy(isSearching = true)
            try {
                val response = RetrofitClient.apiService.searchUsers(query)
                if (response.isSuccessful) {
                    _state.value = _state.value.copy(
                        searchResults = response.body() ?: emptyList(),
                        isSearching = false
                    )
                } else {
                    _state.value = _state.value.copy(searchResults = emptyList(), isSearching = false)
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(searchResults = emptyList(), isSearching = false)
            }
        }
    }

    fun createRoom(name: String, description: String, isPrivate: Boolean, invites: List<String>) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isCreatingRoom = true, createRoomError = null, roomCreated = false)
            try {
                val request = CreateRoomRequest(
                    name = name,
                    description = description,
                    isPrivate = isPrivate,
                    invites = invites
                )
                val response = RetrofitClient.apiService.createRoom(request)
                if (response.isSuccessful && response.body() != null) {
                    val newRoom = response.body()!!.room
                    val updatedRooms = _state.value.rooms.toMutableList()
                    updatedRooms.add(0, newRoom)
                    _state.value = _state.value.copy(
                        rooms = updatedRooms,
                        isCreatingRoom = false,
                        roomCreated = true
                    )
                } else {
                    val errorBody = response.errorBody()?.string()
                    _state.value = _state.value.copy(
                        isCreatingRoom = false,
                        createRoomError = "Failed: ${response.code()} - $errorBody"
                    )
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isCreatingRoom = false,
                    createRoomError = e.message ?: "Network error"
                )
            }
        }
    }

    fun resetCreateRoomState() {
        _state.value = _state.value.copy(
            roomCreated = false,
            createRoomError = null,
            searchResults = emptyList()
        )
    }

    fun updateProfile(newUsername: String, onSuccess: (User) -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.apiService.updateProfile(
                    UpdateProfileRequest(newUsername)
                )
                if (response.isSuccessful && response.body() != null) {
                    onSuccess(response.body()!!)
                } else {
                    val errorBody = response.errorBody()?.string()
                    onError(errorBody ?: "Failed to update profile")
                }
            } catch (e: Exception) {
                onError(e.message ?: "Network error")
            }
        }
    }

    fun changePassword(currentPassword: String, newPassword: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.apiService.changePassword(
                    ChangePasswordRequest(currentPassword, newPassword)
                )
                if (response.isSuccessful) {
                    onSuccess()
                } else {
                    val errorBody = response.errorBody()?.string()
                    onError(errorBody ?: "Failed to change password")
                }
            } catch (e: Exception) {
                onError(e.message ?: "Network error")
            }
        }
    }

    fun addRoomMember(roomId: Int, username: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.apiService.addRoomMember(
                    roomId, AddMemberRequest(listOf(username))
                )
                if (response.isSuccessful) {
                    onSuccess()
                } else {
                    val errorBody = response.errorBody()?.string()
                    onError(errorBody ?: "Failed to add member")
                }
            } catch (e: Exception) {
                onError(e.message ?: "Network error")
            }
        }
    }
}
