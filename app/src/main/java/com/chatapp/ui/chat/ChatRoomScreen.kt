package com.chatapp.ui.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.chatapp.data.local.ThemeManager
import com.chatapp.data.local.TokenStorage
import com.chatapp.data.model.Message
import com.chatapp.data.model.User
import com.chatapp.data.network.SocketService
import com.chatapp.ui.RoomViewModel
import com.chatapp.ui.RoomViewModelFactory
import com.chatapp.ui.components.BlipLogoCompact
import kotlinx.coroutines.flow.first
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatRoomScreen(
    roomId: Int,
    roomName: String,
    isPrivate: Boolean = false,
    currentUser: User? = null,
    currentUsername: String? = null,
    onBack: () -> Unit,
    viewModel: RoomViewModel = viewModel(
        factory = RoomViewModelFactory(LocalContext.current.applicationContext)
    )
) {
    var messageText by remember { mutableStateOf("") }
    var showAddMemberDialog by remember { mutableStateOf(false) }
    var addMemberMessage by remember { mutableStateOf<String?>(null) }
    val listState = rememberLazyListState()
    val context = LocalContext.current.applicationContext
    val state by viewModel.state.collectAsState()
    val themeManager = ThemeManager(LocalContext.current)
    val isDark by themeManager.isDarkMode.collectAsState(initial = false)

    LaunchedEffect(state.messages.size) {
        if (state.messages.isNotEmpty()) {
            listState.animateScrollToItem(state.messages.size - 1)
        }
    }

    LaunchedEffect(Unit) {
        viewModel.loadMessages(roomId)
        try {
            val token = TokenStorage(context).token.first()
            if (token != null) {
                SocketService.instance.connect(token)
                SocketService.instance.joinRoom(roomId, token)
                viewModel.setupSocketListener()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            try {
                SocketService.instance.leaveRoom(roomId)
                viewModel.cleanupSocketListener()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    val surfaceColor = if (isDark) Color(0xFF111827) else Color.White
    val bgColor = if (isDark) Color(0xFF0B1120) else Color(0xFFF1F5F9)
    val inputBg = if (isDark) Color(0xFF1E293B) else Color(0xFFF8FAFC)
    val outlineColor = if (isDark) Color(0xFF334155) else Color(0xFFE2E8F0)

    Scaffold(
        modifier = Modifier.imePadding(),
        containerColor = bgColor,
        topBar = {
            Surface(
                color = surfaceColor,
                shadowElevation = 1.dp
            ) {
                TopAppBar(
                     title = {
                         Row(verticalAlignment = Alignment.CenterVertically) {
                             BlipLogoCompact(size = 32.dp)
                             Spacer(modifier = Modifier.width(10.dp))
                             Column {
                                 Text(
                                     text = roomName.replaceFirstChar { it.uppercase() },
                                     fontWeight = FontWeight.Bold,
                                     fontSize = 16.sp,
                                     maxLines = 1
                                 )
                                 Row(verticalAlignment = Alignment.CenterVertically) {
                                     Box(
                                         modifier = Modifier
                                             .size(6.dp)
                                             .clip(CircleShape)
                                             .background(
                                                 if (SocketService.instance.isConnected())
                                                     Color(0xFF06D6A0)
                                                 else
                                                     Color.Gray
                                             )
                                     )
                                     Spacer(modifier = Modifier.width(4.dp))
                                     Text(
                                         text = if (SocketService.instance.isConnected()) "Online" else "Offline",
                                         style = MaterialTheme.typography.bodySmall,
                                         fontSize = 10.sp,
                                         color = if (SocketService.instance.isConnected()) Color(0xFF06D6A0) else Color.Gray
                                     )
                                 }
                             }
                         }
                     },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B)
                            )
                        }
                    },
                    actions = {
                        if (isPrivate) {
                            IconButton(onClick = { showAddMemberDialog = true }) {
                                Icon(
                                    Icons.Default.PersonAdd,
                                    contentDescription = "Add Member",
                                    tint = if (isDark) Color(0xFF60A5FA) else Color(0xFF3B82F6)
                                )
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = surfaceColor
                    )
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding)
        ) {
            if (state.isLoading && state.messages.isEmpty()) {
                Box(
                    modifier = Modifier.weight(1f).fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = if (isDark) Color(0xFF3B82F6) else Color(0xFF2563EB),
                        strokeWidth = 3.dp
                    )
                }
            } else if (state.messages.isEmpty()) {
                Box(
                    modifier = Modifier.weight(1f).fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.linearGradient(
                                        colors = listOf(Color(0xFF3B82F6).copy(alpha = 0.12f), Color(0xFF06D6A0).copy(alpha = 0.12f))
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.AutoMirrored.Filled.Chat,
                                contentDescription = null,
                                tint = if (isDark) Color(0xFF3B82F6) else Color(0xFF2563EB),
                                modifier = Modifier.size(32.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("No messages yet", fontWeight = FontWeight.SemiBold, fontSize = 16.sp, color = if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B))
                        Text("Start the conversation", style = MaterialTheme.typography.bodySmall, color = Color.Gray)
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    state = listState,
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    items(state.messages) { message ->
                         MessageBubble(
                             message = message,
                             currentUserId = currentUser?.id ?: -1,
                             currentUsername = currentUsername ?: "",
                             isDark = isDark
                         )
                     }
                }
            }

            // Input bar
            Surface(
                color = surfaceColor,
                shadowElevation = 2.dp,
                shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.Bottom
                ) {
                    OutlinedTextField(
                        value = messageText,
                        onValueChange = { messageText = it },
                        modifier = Modifier
                            .weight(1f)
                            .heightIn(min = 42.dp, max = 100.dp),
                        placeholder = { Text("Message...", color = Color.Gray, fontSize = 13.sp) },
                        shape = RoundedCornerShape(22.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = outlineColor,
                            unfocusedBorderColor = outlineColor,
                            focusedContainerColor = inputBg,
                            unfocusedContainerColor = inputBg
                        ),
                        textStyle = androidx.compose.material3.LocalTextStyle.current.copy(fontSize = 14.sp),
                        maxLines = 4
                    )

                    Spacer(modifier = Modifier.width(6.dp))

                    FloatingActionButton(
                        onClick = {
                            if (messageText.isNotBlank()) {
                                viewModel.sendMessage(roomId, messageText)
                                messageText = ""
                            }
                        },
                        modifier = Modifier.size(42.dp),
                        containerColor = if (isDark) Color(0xFF3B82F6) else Color(0xFF2563EB),
                        shape = CircleShape
                    ) {
                        Icon(
                            Icons.Default.Send,
                            contentDescription = "Send",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }

    // Add Member Dialog
    if (showAddMemberDialog) {
        AddMemberDialog(
            onDismiss = { showAddMemberDialog = false },
            onAddMember = { username ->
                viewModel.addRoomMember(
                    roomId = roomId,
                    username = username,
                    onSuccess = {
                        showAddMemberDialog = false
                        addMemberMessage = "$username added!"
                    },
                    onError = { error ->
                        addMemberMessage = "Error: $error"
                    }
                )
            },
            viewModel = viewModel
        )
    }

    // Snackbar for add member feedback
    addMemberMessage?.let { msg ->
        LaunchedEffect(msg) {
            kotlinx.coroutines.delay(2000)
            addMemberMessage = null
        }
    }
}

@Composable
fun MessageBubble(
    message: Message,
    currentUserId: Int = -1,
    currentUsername: String = "",
    isDark: Boolean = false
) {
    val isMe = (currentUserId != -1 && message.userId == currentUserId) ||
               (currentUsername.isNotEmpty() && message.username.equals(currentUsername, ignoreCase = true))
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val maxBubbleWidth = screenWidth * 0.75f

    val bubbleShape = if (isMe) {
        RoundedCornerShape(18.dp, 18.dp, 4.dp, 18.dp)
    } else {
        RoundedCornerShape(18.dp, 18.dp, 18.dp, 4.dp)
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalArrangement = if (isMe) Arrangement.End else Arrangement.Start,
        verticalAlignment = Alignment.Bottom
    ) {
        if (!isMe) {
            Box(
                modifier = Modifier
                    .size(26.dp)
                    .clip(CircleShape)
                    .background(
                        try {
                            Color(android.graphics.Color.parseColor("#${message.avatarColor.replace("#", "")}"))
                        } catch (e: Exception) {
                            Color(0xFF3B82F6)
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = message.username.firstOrNull()?.uppercaseChar()?.toString() ?: "?",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp
                )
            }
            Spacer(modifier = Modifier.width(6.dp))
        }

        Column(
            horizontalAlignment = if (isMe) Alignment.End else Alignment.Start,
            modifier = Modifier.widthIn(max = maxBubbleWidth)
        ) {
            if (!isMe) {
                Text(
                    text = message.username,
                    fontSize = 11.sp,
                    color = if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B),
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(start = 4.dp, bottom = 2.dp)
                )
            }

            Surface(
                color = if (isMe)
                    (if (isDark) Color(0xFF3B82F6) else Color(0xFF2563EB))
                else
                    (if (isDark) Color(0xFF1E293B) else Color.White),
                shape = bubbleShape,
                shadowElevation = 0.5.dp
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp)
                ) {
                    Text(
                        text = message.content,
                        fontSize = 14.sp,
                        lineHeight = 18.sp,
                        color = if (isMe) Color.White else (if (isDark) Color(0xFFF1F5F9) else Color(0xFF0F172A))
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = formatTime(message.createdAt),
                        fontSize = 9.sp,
                        color = if (isMe)
                            Color.White.copy(alpha = 0.6f)
                        else
                            (if (isDark) Color(0xFF64748B) else Color.Gray),
                        modifier = Modifier.align(Alignment.End)
                    )
                }
            }
        }
    }
}

fun formatTime(timestamp: String): String {
    return try {
        val inputFormat = if (timestamp.contains("T")) {
            SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US)
        } else {
            SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US)
        }
        inputFormat.timeZone = TimeZone.getTimeZone("UTC")
        val date = inputFormat.parse(timestamp)
        SimpleDateFormat("HH:mm", Locale.getDefault()).format(date!!)
    } catch (e: Exception) {
        ""
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddMemberDialog(
    onDismiss: () -> Unit,
    onAddMember: (String) -> Unit,
    viewModel: RoomViewModel
) {
    var searchQuery by remember { mutableStateOf("") }
    val state by viewModel.state.collectAsState()

    LaunchedEffect(searchQuery) {
        if (searchQuery.length >= 2) {
            kotlinx.coroutines.delay(300)
            viewModel.searchUsers(searchQuery)
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(20.dp),
        title = {
            Text("Add Member", fontWeight = FontWeight.Bold)
        },
        text = {
            Column {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    label = { Text("Search username") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth(),
                    leadingIcon = {
                        Icon(
                            Icons.Default.PersonAdd,
                            contentDescription = null,
                            tint = Color(0xFF6C63FF)
                        )
                    }
                )

                Spacer(modifier = Modifier.height(12.dp))

                if (state.isSearching) {
                    Box(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            strokeWidth = 2.dp
                        )
                    }
                } else if (state.searchResults.isNotEmpty()) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        state.searchResults.take(5).forEach { user ->
                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                color = Color(0xFF6C63FF).copy(alpha = 0.08f),
                                onClick = { onAddMember(user.username) }
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    val userColor = try {
                                        Color(android.graphics.Color.parseColor("#${user.avatarColor.replace("#", "")}"))
                                    } catch (e: Exception) {
                                        Color(0xFF6C63FF)
                                    }
                                    Box(
                                        modifier = Modifier
                                            .size(32.dp)
                                            .clip(CircleShape)
                                            .background(userColor),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            user.username.firstOrNull()?.uppercaseChar()?.toString() ?: "?",
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Text(
                                        user.username,
                                        fontWeight = FontWeight.Medium,
                                        fontSize = 14.sp
                                    )
                                }
                            }
                        }
                    }
                } else if (searchQuery.length >= 2) {
                    Text(
                        "No users found",
                        color = Color.Gray,
                        fontSize = 13.sp,
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
