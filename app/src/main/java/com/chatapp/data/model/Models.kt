package com.chatapp.data.model

data class User(
    val id: Int,
    val username: String,
    val avatarColor: String,
    val createdAt: String
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
    val id: Int,
    val name: String,
    val description: String,
    val isGlobal: Boolean,
    val isPrivate: Boolean,
    val ownerId: Int?,
    val ownerName: String?,
    val createdAt: String
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
    val id: Int,
    val roomId: Int,
    val userId: Int,
    val username: String,
    val avatarColor: String,
    val content: String,
    val createdAt: String
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
    val token: String,
    val user: User
)

data class MessageRequest(
    val content: String
)

data class CreateRoomRequest(
    val name: String,
    val description: String,
    val isPrivate: Boolean,
    val invites: List<String>
)
