package com.chatapp.data.network

import io.socket.client.IO
import io.socket.client.Socket
import io.socket.emitter.Emitter
import org.json.JSONObject

class SocketService private constructor() {
    private var socket: Socket? = null
    private var isConnected = false

    companion object {
        val instance = SocketService()
    }

    fun connect(token: String) {
        if (isConnected) return

        try {
            val opts = IO.Options().apply {
                transports = arrayOf("websocket")
                auth = mapOf("token" to token)
            }
            socket = IO.socket(ApiConfig.SOCKET_URL, opts)
            socket?.connect()
            isConnected = true
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun disconnect() {
        socket?.disconnect()
        isConnected = false
    }

    fun joinRoom(roomId: Int, token: String) {
        socket?.emit("join_room", JSONObject().apply {
            put("room_id", roomId)
            put("token", token)
        })
    }

    fun leaveRoom(roomId: Int) {
        socket?.emit("leave_room", JSONObject().apply {
            put("room_id", roomId)
        })
    }

    fun sendMessage(roomId: Int, content: String, token: String) {
        socket?.emit("message", JSONObject().apply {
            put("room_id", roomId)
            put("content", content)
            put("token", token)
        })
    }

    fun onMessage(listener: Emitter.Listener) {
        socket?.on("message", listener)
    }

    fun offMessage() {
        socket?.off("message")
    }

    fun isConnected(): Boolean = isConnected
}
