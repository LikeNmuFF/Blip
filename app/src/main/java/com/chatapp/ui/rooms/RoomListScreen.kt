package com.chatapp.ui.rooms

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.chatapp.data.local.ThemeManager
import com.chatapp.data.model.Room
import com.chatapp.ui.RoomViewModel
import com.chatapp.ui.RoomViewModelFactory

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoomListScreen(
    onRoomClick: (Room) -> Unit,
    onLogout: () -> Unit,
    onCreateRoom: () -> Unit,
    viewModel: RoomViewModel = viewModel(
        factory = RoomViewModelFactory(LocalContext.current)
    )
) {
    val state by viewModel.state.collectAsState()
    val themeManager = ThemeManager(LocalContext.current)
    val isDark by themeManager.isDarkMode.collectAsState(initial = false)

    LaunchedEffect(Unit) {
        viewModel.loadRooms()
    }

    val surfaceColor = if (isDark) Color(0xFF111827) else Color.White
    val surfaceVariantColor = if (isDark) Color(0xFF1E293B) else Color(0xFFF1F5F9)
    val onSurfaceColor = if (isDark) Color(0xFFF1F5F9) else Color(0xFF0F172A)
    val onSurfaceVariantColor = if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B)
    val outlineColor = if (isDark) Color(0xFF334155) else Color(0xFFE2E8F0)

    Scaffold(
        containerColor = if (isDark) Color(0xFF0B1120) else Color(0xFFF8FAFC),
        topBar = {
            Surface(
                color = surfaceColor,
                shadowElevation = 1.dp
            ) {
                TopAppBar(
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(
                                        Brush.linearGradient(
                                            colors = listOf(Color(0xFF3B82F6), Color(0xFF06D6A0))
                                        )
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "B",
                                    color = Color.White,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 18.sp,
                                    letterSpacing = (-0.5).sp
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    "Blip",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 20.sp,
                                    color = onSurfaceColor
                                )
                                Text(
                                    "${state.rooms.size} room${if (state.rooms.size != 1) "s" else ""}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = onSurfaceVariantColor
                                )
                            }
                        }
                    },
                    actions = {
                        IconButton(onClick = { viewModel.loadRooms() }) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Refresh",
                                tint = if (isDark) Color(0xFF3B82F6) else Color(0xFF2563EB)
                            )
                        }
                        IconButton(onClick = {
                            viewModel.resetCreateRoomState()
                            onCreateRoom()
                        }) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Create Room",
                                tint = if (isDark) Color(0xFF06D6A0) else Color(0xFF06D6A0)
                            )
                        }
                        IconButton(onClick = onLogout) {
                            Icon(
                                imageVector = Icons.Default.Logout,
                                contentDescription = "Logout",
                                tint = if (isDark) Color(0xFFF87171) else Color(0xFFEF4444)
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = surfaceColor
                    )
                )
            }
        }
    ) { padding ->
        when {
            state.isLoading && state.rooms.isEmpty() -> {
                Box(
                    modifier = Modifier.fillMaxSize().padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(
                            color = if (isDark) Color(0xFF3B82F6) else Color(0xFF2563EB),
                            strokeWidth = 3.dp
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Loading rooms...", color = onSurfaceVariantColor)
                    }
                }
            }
            state.error != null && state.rooms.isEmpty() -> {
                Box(
                    modifier = Modifier.fillMaxSize().padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(32.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(72.dp)
                                .clip(CircleShape)
                                .background(if (isDark) Color(0xFF1E293B) else Color(0xFFFEE2E2)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.WifiOff,
                                contentDescription = null,
                                tint = if (isDark) Color(0xFFF87171) else Color(0xFFEF4444),
                                modifier = Modifier.size(32.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Couldn't load rooms", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = onSurfaceColor)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Check your connection", color = onSurfaceVariantColor)
                        Spacer(modifier = Modifier.height(20.dp))
                        Button(
                            onClick = { viewModel.loadRooms() },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isDark) Color(0xFF3B82F6) else Color(0xFF2563EB)
                            )
                        ) {
                            Icon(Icons.Default.Refresh, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Retry")
                        }
                    }
                }
            }
            state.rooms.isEmpty() -> {
                Box(
                    modifier = Modifier.fillMaxSize().padding(padding),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.linearGradient(
                                        colors = listOf(Color(0xFF3B82F6).copy(alpha = 0.15f), Color(0xFF06D6A0).copy(alpha = 0.15f))
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Chat,
                                contentDescription = null,
                                tint = if (isDark) Color(0xFF3B82F6) else Color(0xFF2563EB),
                                modifier = Modifier.size(40.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("No rooms yet", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = onSurfaceColor)
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("Create one to start chatting", color = onSurfaceVariantColor)
                    }
                }
            }
            else -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(padding),
                    contentPadding = PaddingValues(16.dp, top = 12.dp, bottom = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(state.rooms) { room ->
                        RoomCard(room = room, onClick = { onRoomClick(room) }, isDark = isDark)
                    }
                }
            }
        }
    }
}

@Composable
fun RoomCard(room: Room, onClick: () -> Unit, isDark: Boolean = false) {
    val gradientColors = when {
        room.isGlobal -> listOf(Color(0xFF3B82F6), Color(0xFF60A5FA))
        room.isPrivate -> listOf(Color(0xFF06D6A0), Color(0xFF34D399))
        else -> listOf(Color(0xFF8B5CF6), Color(0xFFA78BFA))
    }

    val surfaceColor = if (isDark) Color(0xFF111827) else Color.White
    val onSurfaceColor = if (isDark) Color(0xFFF1F5F9) else Color(0xFF0F172A)
    val outlineColor = if (isDark) Color(0xFF334155) else Color(0xFFE2E8F0)

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .animateContentSize()
            .shadow(
                elevation = 1.dp,
                shape = RoundedCornerShape(16.dp)
            ),
        shape = RoundedCornerShape(16.dp),
        color = surfaceColor
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon with gradient
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(Brush.linearGradient(colors = gradientColors)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = when {
                        room.isGlobal -> Icons.Default.Public
                        room.isPrivate -> Icons.Default.Lock
                        else -> Icons.AutoMirrored.Filled.Chat
                    },
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = room.name.replaceFirstChar { it.uppercase() },
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = onSurfaceColor,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false)
                    )
                    if (room.isPrivate) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = Color(0xFF06D6A0).copy(alpha = if (isDark) 0.2f else 0.15f)
                        ) {
                            Text(
                                "Private",
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF06D6A0)
                            )
                        }
                    }
                    if (room.isGlobal) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = Color(0xFF3B82F6).copy(alpha = if (isDark) 0.2f else 0.15f)
                        ) {
                            Text(
                                "Global",
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF3B82F6)
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = room.description.takeIf { it.isNotEmpty() } ?: "No description",
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Icon(
                imageVector = androidx.compose.material.icons.Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
                tint = if (isDark) Color(0xFF475569) else Color(0xFFCBD5E1),
                modifier = Modifier.size(18.dp)
            )
        }
    }
}
