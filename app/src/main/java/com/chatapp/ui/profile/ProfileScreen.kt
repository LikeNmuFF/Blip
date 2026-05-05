package com.chatapp.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.chatapp.data.model.User
import com.chatapp.ui.RoomViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    user: User?,
    onBack: () -> Unit,
    onLogout: () -> Unit,
    onUserUpdated: (User) -> Unit = {},
    viewModel: RoomViewModel? = null
) {
    val avatarColor = try {
        Color(android.graphics.Color.parseColor("#${(user?.avatarColor ?: "6C63FF").replace("#", "")}"))
    } catch (e: Exception) {
        Color(0xFF6C63FF)
    }

    var showEditNameDialog by remember { mutableStateOf(false) }
    var showChangePasswordDialog by remember { mutableStateOf(false) }
    var snackbarMessage by remember { mutableStateOf<String?>(null) }
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(snackbarMessage) {
        snackbarMessage?.let {
            snackbarHostState.showSnackbar(it)
            snackbarMessage = null
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Profile", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Profile Header with gradient
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(
                        Brush.linearGradient(
                            colors = listOf(Color(0xFF6C63FF), Color(0xFF9B93FF), Color(0xFFB8B3FF))
                        )
                    )
                    .padding(vertical = 32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    // Avatar
                    Box(
                        modifier = Modifier
                            .size(90.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.25f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .clip(CircleShape)
                                .background(avatarColor),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = user?.username?.firstOrNull()?.uppercaseChar()?.toString() ?: "?",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 32.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Username
                    Text(
                        text = user?.username ?: "User",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 24.sp
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    // Status badge
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color.White.copy(alpha = 0.2f)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF4CAF50))
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                "Online",
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Account Info Section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
            ) {
                Text(
                    "Account Info",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray,
                    modifier = Modifier.padding(start = 4.dp, bottom = 12.dp)
                )

                ProfileInfoCard(
                    icon = Icons.Default.Person,
                    label = "Username",
                    value = user?.username ?: "—",
                    iconColor = Color(0xFF6C63FF)
                )

                Spacer(modifier = Modifier.height(8.dp))

                ProfileInfoCard(
                    icon = Icons.Default.CalendarMonth,
                    label = "Member Since",
                    value = formatDate(user?.createdAt ?: ""),
                    iconColor = Color(0xFF00BFA5)
                )

                Spacer(modifier = Modifier.height(8.dp))

                ProfileInfoCard(
                    icon = Icons.Default.Fingerprint,
                    label = "User ID",
                    value = "#${user?.id ?: "—"}",
                    iconColor = Color(0xFFFF9800)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Edit Profile Section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
            ) {
                Text(
                    "Edit Profile",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray,
                    modifier = Modifier.padding(start = 4.dp, bottom = 12.dp)
                )

                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(1.dp)
                ) {
                    Column {
                        SettingsItem(
                            icon = Icons.Default.Edit,
                            label = "Change Username",
                            iconColor = Color(0xFF6C63FF),
                            onClick = { showEditNameDialog = true }
                        )
                        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = Color(0xFFF0F0F0))
                        SettingsItem(
                            icon = Icons.Default.Lock,
                            label = "Change Password",
                            iconColor = Color(0xFFFF6B6B),
                            onClick = { showChangePasswordDialog = true }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Settings Section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
            ) {
                Text(
                    "Settings",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray,
                    modifier = Modifier.padding(start = 4.dp, bottom = 12.dp)
                )

                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(1.dp)
                ) {
                    Column {
                        SettingsItem(
                            icon = Icons.Default.Notifications,
                            label = "Notifications",
                            iconColor = Color(0xFF6C63FF)
                        )
                        HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = Color(0xFFF0F0F0))
                        SettingsItem(
                            icon = Icons.Default.Info,
                            label = "About Blip",
                            iconColor = Color(0xFFFF9800)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Logout button
            Button(
                onClick = onLogout,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .height(52.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFEBEE))
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.Logout,
                    contentDescription = null,
                    tint = Color(0xFFE53935),
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Log Out", color = Color(0xFFE53935), fontWeight = FontWeight.Bold, fontSize = 15.sp)
            }

            Spacer(modifier = Modifier.height(20.dp))
            Text("Blip v1.0.0", color = Color.LightGray, fontSize = 12.sp, textAlign = TextAlign.Center)
            Spacer(modifier = Modifier.height(32.dp))
        }
    }

    // Edit Username Dialog
    if (showEditNameDialog) {
        EditNameDialog(
            currentName = user?.username ?: "",
            onDismiss = { showEditNameDialog = false },
            onSave = { newName ->
                viewModel?.updateProfile(
                    newUsername = newName,
                    onSuccess = { updatedUser ->
                        showEditNameDialog = false
                        snackbarMessage = "Username updated!"
                        onUserUpdated(updatedUser)
                    },
                    onError = { error ->
                        snackbarMessage = "Error: $error"
                    }
                )
            }
        )
    }

    // Change Password Dialog
    if (showChangePasswordDialog) {
        ChangePasswordDialog(
            onDismiss = { showChangePasswordDialog = false },
            onSave = { currentPass, newPass ->
                viewModel?.changePassword(
                    currentPassword = currentPass,
                    newPassword = newPass,
                    onSuccess = {
                        showChangePasswordDialog = false
                        snackbarMessage = "Password changed!"
                    },
                    onError = { error ->
                        snackbarMessage = "Error: $error"
                    }
                )
            }
        )
    }
}

@Composable
private fun EditNameDialog(
    currentName: String,
    onDismiss: () -> Unit,
    onSave: (String) -> Unit
) {
    var name by remember { mutableStateOf(currentName) }

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(20.dp),
        title = {
            Text("Change Username", fontWeight = FontWeight.Bold)
        },
        text = {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("New username") },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            Button(
                onClick = { if (name.isNotBlank()) onSave(name) },
                enabled = name.isNotBlank() && name != currentName,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6C63FF))
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
private fun ChangePasswordDialog(
    onDismiss: () -> Unit,
    onSave: (String, String) -> Unit
) {
    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    val passwordsMatch = newPassword == confirmPassword && newPassword.isNotBlank()

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(20.dp),
        title = {
            Text("Change Password", fontWeight = FontWeight.Bold)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = currentPassword,
                    onValueChange = { currentPassword = it },
                    label = { Text("Current password") },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = newPassword,
                    onValueChange = { newPassword = it },
                    label = { Text("New password") },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    label = { Text("Confirm new password") },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    isError = confirmPassword.isNotBlank() && !passwordsMatch,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )
                if (confirmPassword.isNotBlank() && !passwordsMatch) {
                    Text(
                        "Passwords don't match",
                        color = Color(0xFFE53935),
                        fontSize = 12.sp
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onSave(currentPassword, newPassword) },
                enabled = currentPassword.isNotBlank() && passwordsMatch,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6C63FF))
            ) {
                Text("Change")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
private fun ProfileInfoCard(
    icon: ImageVector,
    label: String,
    value: String,
    iconColor: Color
) {
    Card(
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(iconColor.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(22.dp))
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column {
                Text(label, fontSize = 12.sp, color = Color.Gray)
                Text(value, fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
            }
        }
    }
}

@Composable
private fun SettingsItem(
    icon: ImageVector,
    label: String,
    iconColor: Color,
    onClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(iconColor.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(20.dp))
        }
        Spacer(modifier = Modifier.width(14.dp))
        Text(label, fontSize = 14.sp, fontWeight = FontWeight.Medium, modifier = Modifier.weight(1f))
        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = Color(0xFFD0D0D0), modifier = Modifier.size(20.dp))
    }
}

private fun formatDate(isoDate: String): String {
    return try {
        val inputFormat = java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", java.util.Locale.US)
        inputFormat.timeZone = java.util.TimeZone.getTimeZone("UTC")
        val date = inputFormat.parse(isoDate)
        val outputFormat = java.text.SimpleDateFormat("MMMM dd, yyyy", java.util.Locale.US)
        outputFormat.format(date!!)
    } catch (e: Exception) {
        "—"
    }
}
