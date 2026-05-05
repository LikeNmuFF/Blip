package com.chatapp.ui.rooms

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.chatapp.data.local.ThemeManager
import com.chatapp.data.model.User
import com.chatapp.ui.RoomViewModel
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun CreateRoomScreen(
    onBack: () -> Unit,
    onRoomCreated: () -> Unit,
    viewModel: RoomViewModel
) {
    var roomName by remember { mutableStateOf("") }
    var roomDescription by remember { mutableStateOf("") }
    var isPrivate by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    var invitedUsers by remember { mutableStateOf<List<User>>(emptyList()) }

    val state by viewModel.state.collectAsState()
    val themeManager = ThemeManager(LocalContext.current)
    val isDark by themeManager.isDarkMode.collectAsState(initial = false)

    LaunchedEffect(Unit) {
        viewModel.resetCreateRoomState()
    }

    LaunchedEffect(state.roomCreated) {
        if (state.roomCreated) {
            delay(300)
            onRoomCreated()
        }
    }

    LaunchedEffect(searchQuery) {
        if (searchQuery.length >= 2) {
            delay(400)
            viewModel.searchUsers(searchQuery)
        } else {
            viewModel.searchUsers("")
        }
    }

    val surfaceColor = if (isDark) Color(0xFF111827) else Color.White
    val onSurfaceColor = if (isDark) Color(0xFFF1F5F9) else Color(0xFF0F172A)
    val onSurfaceVariantColor = if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B)
    val outlineColor = if (isDark) Color(0xFF334155) else Color(0xFFE2E8F0)
    val inputBg = if (isDark) Color(0xFF1E293B) else Color(0xFFF8FAFC)

    Scaffold(
        containerColor = if (isDark) Color(0xFF0B1120) else Color(0xFFF8FAFC),
        topBar = {
            Surface(
                color = surfaceColor,
                shadowElevation = 1.dp
            ) {
                TopAppBar(
                    title = {
                        Text(
                            "Create Room",
                            fontWeight = FontWeight.Bold,
                            color = onSurfaceColor
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = onSurfaceVariantColor)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = surfaceColor)
                )
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier.fillMaxSize().padding(padding).imePadding()
        ) {
            Column(
                modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                // Header
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(
                            Brush.linearGradient(
                                colors = listOf(Color(0xFF3B82F6), Color(0xFF06D6A0))
                            )
                        )
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.Forum,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(44.dp)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            "Start a Conversation",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                        Text(
                            "Create a room and invite others",
                            color = Color.White.copy(alpha = 0.8f),
                            fontSize = 13.sp
                        )
                    }
                }

                // Error
                AnimatedVisibility(visible = state.createRoomError != null) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (isDark) Color(0xFF450A0A) else Color(0xFFFEE2E2)
                    ) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Error, contentDescription = null, tint = if (isDark) Color(0xFFF87171) else Color(0xFFEF4444))
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(state.createRoomError ?: "", fontSize = 13.sp, color = if (isDark) Color(0xFFFCA5A5) else Color(0xFFB91C1C))
                        }
                    }
                }

                // Room Name
                Column {
                    Text("Room Name", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.SemiBold, color = onSurfaceColor)
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = roomName,
                        onValueChange = { roomName = it },
                        placeholder = { Text("e.g. Project Team, Gaming") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        leadingIcon = {
                            Icon(Icons.Default.Tag, contentDescription = null, tint = if (isDark) Color(0xFF3B82F6) else Color(0xFF2563EB))
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = if (isDark) Color(0xFF3B82F6) else Color(0xFF2563EB),
                            unfocusedBorderColor = outlineColor,
                            focusedContainerColor = inputBg,
                            unfocusedContainerColor = inputBg
                        )
                    )
                }

                // Description
                Column {
                    Text("Description", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.SemiBold, color = onSurfaceColor)
                    Spacer(modifier = Modifier.height(6.dp))
                    OutlinedTextField(
                        value = roomDescription,
                        onValueChange = { roomDescription = it },
                        placeholder = { Text("What's this room about?") },
                        modifier = Modifier.fillMaxWidth().heightIn(min = 70.dp),
                        maxLines = 3,
                        shape = RoundedCornerShape(12.dp),
                        leadingIcon = {
                            Icon(Icons.Default.Description, contentDescription = null, tint = if (isDark) Color(0xFF3B82F6) else Color(0xFF2563EB))
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = if (isDark) Color(0xFF3B82F6) else Color(0xFF2563EB),
                            unfocusedBorderColor = outlineColor,
                            focusedContainerColor = inputBg,
                            unfocusedContainerColor = inputBg
                        )
                    )
                }

                // Room Type
                Column {
                    Text("Room Type", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.SemiBold, color = onSurfaceColor)
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp)).border(
                            width = 1.dp,
                            color = outlineColor,
                            shape = RoundedCornerShape(14.dp)
                        )
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { isPrivate = false }
                                .background(if (!isPrivate) (if (isDark) Color(0xFF1E3A5F) else Color(0xFF2563EB)) else Color.Transparent)
                                .padding(vertical = 14.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Public, contentDescription = null, tint = if (!isPrivate) Color.White else onSurfaceVariantColor, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Public", color = if (!isPrivate) Color.White else onSurfaceVariantColor, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                            }
                        }
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { isPrivate = true }
                                .background(if (isPrivate) (if (isDark) Color(0xFF064E3B) else Color(0xFF06D6A0)) else Color.Transparent)
                                .padding(vertical = 14.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Lock, contentDescription = null, tint = if (isPrivate) Color.White else onSurfaceVariantColor, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Private", color = if (isPrivate) Color.White else onSurfaceVariantColor, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                            }
                        }
                    }
                    Text(
                        if (isPrivate) "Only invited members can join" else "Anyone on the app can join",
                        style = MaterialTheme.typography.bodySmall,
                        color = onSurfaceVariantColor
                    )
                }

                // Invite (private only)
                AnimatedVisibility(
                    visible = isPrivate,
                    enter = fadeIn() + expandVertically(),
                    exit = fadeOut() + shrinkVertically()
                ) {
                    Column {
                        Text("Invite Members", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.SemiBold, color = onSurfaceColor)
                        Spacer(modifier = Modifier.height(6.dp))

                        if (invitedUsers.isNotEmpty()) {
                            FlowRow(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                invitedUsers.forEach { user ->
                                    InputChip(
                                        selected = true,
                                        onClick = { invitedUsers = invitedUsers.filter { it.id != user.id } },
                                        label = { Text(user.username, fontSize = 12.sp) },
                                        leadingIcon = {
                                            Box(
                                                modifier = Modifier.size(22.dp).clip(CircleShape).background(
                                                    try {
                                                        Color(android.graphics.Color.parseColor("#${user.avatarColor.replace("#", "")}"))
                                                    } catch (e: Exception) {
                                                        Color(0xFF3B82F6)
                                                    }
                                                ),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(
                                                    user.username.first().uppercaseChar().toString(),
                                                    color = Color.White,
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                        },
                                        trailingIcon = { Icon(Icons.Default.Close, contentDescription = "Remove", modifier = Modifier.size(14.dp)) },
                                        shape = RoundedCornerShape(16.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                        }

                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            placeholder = { Text("Search users...") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            leadingIcon = {
                                Icon(Icons.Default.PersonSearch, contentDescription = null, tint = if (isDark) Color(0xFF3B82F6) else Color(0xFF2563EB))
                            },
                            trailingIcon = {
                                if (state.isSearching) {
                                    CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                                }
                            },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = if (isDark) Color(0xFF3B82F6) else Color(0xFF2563EB),
                                unfocusedBorderColor = outlineColor,
                                focusedContainerColor = inputBg,
                                unfocusedContainerColor = inputBg
                            )
                        )

                        if (state.searchResults.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Surface(shape = RoundedCornerShape(14.dp), shadowElevation = 2.dp, color = surfaceColor) {
                                Column {
                                    state.searchResults
                                        .filter { user -> invitedUsers.none { it.id == user.id } }
                                        .take(5)
                                        .forEachIndexed { index, user ->
                                            Row(
                                                modifier = Modifier.fillMaxWidth().clickable {
                                                    invitedUsers = invitedUsers + user
                                                    searchQuery = ""
                                                    viewModel.searchUsers("")
                                                }.padding(horizontal = 14.dp, vertical = 10.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Box(
                                                    modifier = Modifier.size(32.dp).clip(CircleShape).background(
                                                        try {
                                                            Color(android.graphics.Color.parseColor("#${user.avatarColor.replace("#", "")}"))
                                                        } catch (e: Exception) {
                                                            Color(0xFF3B82F6)
                                                        }
                                                    ),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Text(user.username.first().uppercaseChar().toString(), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                                }
                                                Spacer(modifier = Modifier.width(10.dp))
                                                Text(user.username, fontWeight = FontWeight.Medium, color = onSurfaceColor)
                                                Spacer(modifier = Modifier.weight(1f))
                                                Icon(Icons.Default.PersonAdd, contentDescription = "Add", tint = if (isDark) Color(0xFF3B82F6) else Color(0xFF2563EB), modifier = Modifier.size(18.dp))
                                            }
                                            if (index < state.searchResults.count { invitedUsers.none { u -> u.id == it.id } } - 1) {
                                                HorizontalDivider(modifier = Modifier.padding(horizontal = 14.dp), color = outlineColor)
                                            }
                                        }
                                }
                            }
                        }
                    }
                }

                // Create button
                Button(
                    onClick = {
                        viewModel.createRoom(
                            name = roomName.trim().lowercase().replace(" ", "-"),
                            description = roomDescription.trim(),
                            isPrivate = isPrivate,
                            invites = invitedUsers.map { it.username }
                        )
                    },
                    modifier = Modifier.fillMaxWidth().height(52.dp),
                    enabled = roomName.isNotBlank() && !state.isCreatingRoom,
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isDark) Color(0xFF3B82F6) else Color(0xFF2563EB)
                    )
                ) {
                    if (state.isCreatingRoom) {
                        CircularProgressIndicator(modifier = Modifier.size(22.dp), strokeWidth = 2.5.dp, color = Color.White)
                    } else {
                        Icon(Icons.Default.Add, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Create Room", fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                    }
                }

                AnimatedVisibility(visible = state.roomCreated) {
                    Surface(shape = RoundedCornerShape(12.dp), color = if (isDark) Color(0xFF064E3B) else Color(0xFFD1FAE5)) {
                        Row(
                            modifier = Modifier.padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF06D6A0))
                            Spacer(modifier = Modifier.width(10.dp))
                            Text("Room created!", color = Color(0xFF06D6A0), fontWeight = FontWeight.Medium)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}
