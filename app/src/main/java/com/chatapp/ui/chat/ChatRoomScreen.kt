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
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material3.LocalTextStyle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.chatapp.data.local.TokenStorage
import com.chatapp.data.model.Message
import com.chatapp.data.network.SocketService
import com.chatapp.ui.RoomViewModel
import com.chatapp.ui.RoomViewModelFactory
import kotlinx.coroutines.flow.first
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatRoomScreen(
    roomId: Int,
    roomName: String,
    onBack: () -> Unit,
    viewModel: RoomViewModel = viewModel(
        factory = RoomViewModelFactory(LocalContext.current.applicationContext)
    )
) {
    var messageText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()
    val context = LocalContext.current.applicationContext
    val state by viewModel.state.collectAsState()
    
    // Scroll to bottom when new messages arrive
    LaunchedEffect(state.messages.size) {
        if (state.messages.isNotEmpty()) {
            listState.animateScrollToItem(state.messages.size - 1)
        }
    }
    
    LaunchedEffect(Unit) {
        viewModel.loadMessages(roomId)
        
        // Connect WebSocket safely
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
    
    Scaffold(
        modifier = Modifier.imePadding(),
        containerColor = Color(0xFFF5F5F5),
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            roomName,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            maxLines = 1
                        )
                        Text(
                            text = if (SocketService.instance.isConnected()) "Connected" else "Connecting...",
                            style = MaterialTheme.typography.bodySmall,
                            fontSize = 11.sp,
                            color = if (SocketService.instance.isConnected()) 
                                Color(0xFF00BFA5) 
                            else 
                                Color.Gray
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            modifier = Modifier.size(22.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Messages list
            if (state.isLoading && state.messages.isEmpty()) {
                Box(
                    modifier = Modifier.weight(1f).fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(32.dp),
                        strokeWidth = 3.dp
                    )
                }
            } else if (state.messages.isEmpty()) {
                Box(
                    modifier = Modifier.weight(1f).fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Chat,
                            contentDescription = null,
                            tint = Color.LightGray,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "No messages yet",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Gray
                        )
                        Text(
                            text = "Be the first to say hello!",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.LightGray
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    state = listState,
                    reverseLayout = false,
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(1.dp)
                ) {
                    items(state.messages) { message ->
                        MessageBubble(message = message)
                    }
                }
            }
            
            // Message input
            MessageInputBar(
                text = messageText,
                onTextChange = { messageText = it },
                onSend = {
                    if (messageText.isNotBlank()) {
                        viewModel.sendMessage(roomId, messageText)
                        messageText = ""
                    }
                }
            )
        }
    }
}

@Composable
fun MessageBubble(message: Message) {
    // Get current user ID from token
    val currentUserId = remember { -1 }
    val isMe = message.userId == currentUserId
    
    // Use screen width fraction instead of fixed dp
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val maxBubbleWidth = screenWidth * 0.7f // 70% of screen width
    
    val bubbleShape = if (isMe) {
        RoundedCornerShape(16.dp, 16.dp, 4.dp, 16.dp)
    } else {
        RoundedCornerShape(16.dp, 16.dp, 16.dp, 4.dp)
    }
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalArrangement = if (isMe) Arrangement.End else Arrangement.Start,
        verticalAlignment = Alignment.Bottom
    ) {
        if (!isMe) {
            // Avatar
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(
                        try {
                            Color(android.graphics.Color.parseColor("#${message.avatarColor.replace("#", "")}"))
                        } catch (e: Exception) {
                            Color(0xFF6C63FF)
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = message.username.firstOrNull()?.uppercaseChar()?.toString() ?: "?",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
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
                    color = Color.Gray,
                    modifier = Modifier.padding(start = 4.dp, bottom = 2.dp)
                )
            }
            
            Surface(
                color = if (isMe) MaterialTheme.colorScheme.primary else Color.White,
                shape = bubbleShape,
                shadowElevation = 0.5.dp
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = message.content,
                        fontSize = 13.sp,
                        lineHeight = 17.sp,
                        color = if (isMe) Color.White else Color(0xFF2D2D2D)
                    )
                    
                    Spacer(modifier = Modifier.height(1.dp))
                    
                    Text(
                        text = formatTime(message.createdAt),
                        fontSize = 9.sp,
                        color = if (isMe) Color.White.copy(alpha = 0.7f) else Color.Gray,
                        modifier = Modifier.align(Alignment.End)
                    )
                }
            }
        }
    }
}

@Composable
fun MessageInputBar(
    text: String,
    onTextChange: (String) -> Unit,
    onSend: () -> Unit
) {
    Surface(
        tonalElevation = 2.dp,
        color = MaterialTheme.colorScheme.surface,
        modifier = Modifier.navigationBarsPadding()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 6.dp, vertical = 6.dp),
            verticalAlignment = Alignment.Bottom
        ) {
            OutlinedTextField(
                value = text,
                onValueChange = onTextChange,
                modifier = Modifier
                    .weight(1f)
                    .heightIn(min = 40.dp, max = 100.dp),
                placeholder = {
                    Text(
                        "Message...",
                        color = Color.Gray,
                        fontSize = 13.sp
                    )
                },
                shape = RoundedCornerShape(20.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f),
                    unfocusedBorderColor = Color(0xFFE0E0E0),
                    focusedContainerColor = Color(0xFFFAFAFA),
                    unfocusedContainerColor = Color(0xFFFAFAFA)
                ),
                textStyle = LocalTextStyle.current.copy(fontSize = 13.sp),
                maxLines = 4
            )
            
            Spacer(modifier = Modifier.width(4.dp))
            
            FloatingActionButton(
                onClick = onSend,
                modifier = Modifier.size(40.dp),
                containerColor = MaterialTheme.colorScheme.primary,
                shape = CircleShape
            ) {
                Icon(
                    Icons.Default.Send,
                    contentDescription = "Send",
                    tint = Color.White,
                    modifier = Modifier
                        .size(18.dp)
                        .padding(start = 2.dp)
                )
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
        val outputFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
        outputFormat.format(date!!)
    } catch (e: Exception) {
        ""
    }
}
