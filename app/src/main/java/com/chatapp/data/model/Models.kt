package com.chatapp.data.model

import com.google.gson.annotations.SerializedName

data class User(
    val id: Int = 0,
    @SerializedName("username") val username: String = "",
    @SerializedName("avatar_color") val avatarColor: String = "",
    @SerializedName("created_at") val createdAt: String = ""
) {
    companion object {
        fun fromMap(map: Map<*, *>): User {
            return User(
                id = (map["id"] as? Number)?.toInt() ?: 0,
                username = map["username"] as? String ?: "",
                avatarColor = map["avatar_color"] as? String ?: "",
                createdAt = map["created_at"] as? String ?: ""
            )
        }
    }
}

data class Room(
    val id: Int = 0,
    @SerializedName("name") val name: String = "",
    @SerializedName("description") val description: String = "",
    @SerializedName("is_global") val isGlobal: Boolean = false,
    @SerializedName("is_private") val isPrivate: Boolean = false,
    @SerializedName("owner_id") val ownerId: Int? = null,
    @SerializedName("owner_name") val ownerName: String? = null,
    @SerializedName("created_at") val createdAt: String = ""
) {
    companion object {
        fun fromMap(map: Map<*, *>): Room {
            return Room(
                id = (map["id"] as? Number)?.toInt() ?: 0,
                name = map["name"] as? String ?: "",
                description = map["description"] as? String ?: "",
                isGlobal = map["is_global"] as? Boolean ?: false,
                isPrivate = map["is_private"] as? Boolean ?: false,
                ownerId = (map["owner_id"] as? Number)?.toInt(),
                ownerName = map["owner_name"] as? String,
                createdAt = map["created_at"] as? String ?: ""
            )
        }
    }
}

data class Message(
    val id: Int = 0,
    @SerializedName("room_id") val roomId: Int = 0,
    @SerializedName("user_id") val userId: Int = 0,
    @SerializedName("username") val username: String = "",
    @SerializedName("avatar_color") val avatarColor: String = "",
    @SerializedName("content") val content: String = "",
    @SerializedName("created_at") val createdAt: String = ""
) {
    companion object {
        fun fromMap(map: Map<*, *>): Message {
            return Message(
                id = (map["id"] as? Number)?.toInt() ?: 0,
                roomId = (map["room_id"] as? Number)?.toInt() ?: 0,
                userId = (map["user_id"] as? Number)?.toInt() ?: 0,
                username = map["username"] as? String ?: "",
                avatarColor = map["avatar_color"] as? String ?: "",
                content = map["content"] as? String ?: "",
                createdAt = map["created_at"] as? String ?: ""
            )
        }
    }
}

data class LoginRequest(
    val username: String,
    val password: String
)

data class RegisterRequest(
    val username: String,
    val password: String
)

data class AuthResponse(
    @SerializedName("token") val token: String = "",
    @SerializedName("user") val user: User? = null
)

data class MessageRequest(
    val content: String
)

data class CreateRoomRequest(
    val name: String,
    val description: String = "",
    @SerializedName("is_private") val isPrivate: Boolean = false,
    val invites: List<String> = emptyList()
)

data class UpdateProfileRequest(
    val username: String
)

data class ChangePasswordRequest(
    @SerializedName("current_password") val currentPassword: String,
    @SerializedName("new_password") val newPassword: String
)

data class AddMemberRequest(
    val usernames: List<String>
)
