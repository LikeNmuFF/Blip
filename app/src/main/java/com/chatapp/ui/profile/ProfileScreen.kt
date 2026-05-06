package com.chatapp.ui.profile

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.chatapp.data.local.ThemeManager
import com.chatapp.data.model.User
import com.chatapp.ui.RoomViewModel
import com.chatapp.ui.components.BlipLogoCompact

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    user: User?,
    onBack: () -> Unit,
    onLogout: () -> Unit,
    onUserUpdated: (User) -> Unit = {},
    viewModel: RoomViewModel? = null
) {
    val themeManager = ThemeManager(androidx.compose.ui.platform.LocalContext.current)
    val isDark by themeManager.isDarkMode.collectAsState(initial = false)
    
    val avatarColor = try {
        Color(android.graphics.Color.parseColor("#${(user?.avatarColor ?: "2563EB").replace("#", "")}"))
    } catch (e: Exception) {
        Color(0xFF2563EB)
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

    val surfaceColor = if (isDark) Color(0xFF111827) else Color.White
    val surfaceVariantColor = if (isDark) Color(0xFF1E293B) else Color(0xFFF8FAFC)
    val onSurfaceColor = if (isDark) Color(0xFFF1F5F9) else Color(0xFF0F172A)
    val onSurfaceVariantColor = if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B)
    val cardShadow = animateFloatAsState(if (isDark) 0.5f else 2f, label = "cardShadow")

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            Surface(
                color = if (isDark) Color(0xFF0B1120) else Color.White,
                shadowElevation = 1.dp
            ) {
                TopAppBar(
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            BlipLogoCompact(size = 28.dp)
                            Spacer(modifier = Modifier.width(10.dp))
                            Text("Profile", fontWeight = FontWeight.Bold, color = onSurfaceColor)
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = onSurfaceVariantColor)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = if (isDark) Color(0xFF0B1120) else Color.White
                    )
                )
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(if (isDark) Color(0xFF0B1120) else Color(0xFFF8FAFC))
                .padding(padding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Profile Header with gradient
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(24.dp))
                        .background(
                            Brush.linearGradient(
                                colors = listOf(Color(0xFF2563EB), Color(0xFF06D6A0))
                            )
                        )
                        .padding(vertical = 36.dp, horizontal = 24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .size(100.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(90.dp)
                                    .clip(CircleShape)
                                    .background(avatarColor),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = user?.username?.firstOrNull()?.uppercaseChar()?.toString() ?: "?",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 36.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        Text(
                            text = user?.username ?: "User",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 26.sp,
                            letterSpacing = (-0.5).sp
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = Color.White.copy(alpha = 0.2f)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF06D6A0))
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    "Online",
                                    color = Color.White,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))

                Text(
                    "Account Info",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = onSurfaceVariantColor,
                    modifier = Modifier
                        .align(Alignment.Start)
                        .padding(bottom = 12.dp)
                )

                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = surfaceColor),
                    elevation = CardDefaults.cardElevation(defaultElevation = cardShadow.value.dp)
                ) {
                    Column {
                        ProfileInfoCard(
                            icon = Icons.Default.Person,
                            label = "Username",
                            value = user?.username ?: "—",
                            iconColor = Color(0xFF2563EB),
                            isDark = isDark,
                            showDivider = true
                        )
                        ProfileInfoCard(
                            icon = Icons.Default.Fingerprint,
                            label = "User ID",
                            value = "#${user?.id ?: "—"}",
                            iconColor = Color(0xFF06D6A0),
                            isDark = isDark,
                            showDivider = true
                        )
                        ProfileInfoCard(
                            icon = Icons.Default.CalendarMonth,
                            label = "Member Since",
                            value = formatDate(user?.createdAt ?: ""),
                            iconColor = Color(0xFF8B5CF6),
                            isDark = isDark,
                            showDivider = false
                        )
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))

                Text(
                    "Edit Profile",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = onSurfaceVariantColor,
                    modifier = Modifier
                        .align(Alignment.Start)
                        .padding(bottom = 12.dp)
                )

                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = surfaceColor),
                    elevation = CardDefaults.cardElevation(defaultElevation = cardShadow.value.dp)
                ) {
                    Column {
                        SettingsItem(
                            icon = Icons.Default.Edit,
                            label = "Change Username",
                            description = "Update your display name",
                            iconColor = Color(0xFF2563EB),
                            isDark = isDark,
                            onClick = { showEditNameDialog = true },
                            showDivider = true
                        )
                        SettingsItem(
                            icon = Icons.Default.Lock,
                            label = "Change Password",
                            description = "Secure your account",
                            iconColor = Color(0xFFFF6B6B),
                            isDark = isDark,
                            onClick = { showChangePasswordDialog = true },
                            showDivider = false
                        )
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))

                Text(
                    "About",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = onSurfaceVariantColor,
                    modifier = Modifier
                        .align(Alignment.Start)
                        .padding(bottom = 12.dp)
                )

                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = surfaceColor),
                    elevation = CardDefaults.cardElevation(defaultElevation = cardShadow.value.dp)
                ) {
                    Column {
                        SettingsItem(
                            icon = Icons.Default.Info,
                            label = "Version",
                            description = "Blip v1.0.0",
                            iconColor = Color(0xFF06D6A0),
                            isDark = isDark,
                            onClick = {},
                            showDivider = false
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .clip(RoundedCornerShape(16.dp)),
                    color = if (isDark) Color(0xFF1E293B) else Color(0xFFFEE2E2),
                    onClick = onLogout
                ) {
                    Row(
                        modifier = Modifier.fillMaxSize(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            Icons.AutoMirrored.Filled.Logout,
                            contentDescription = null,
                            tint = Color(0xFFEF4444),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            "Log Out",
                            color = Color(0xFFEF4444),
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    "Built with love by Blip Team",
                    color = onSurfaceVariantColor,
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }

    if (showEditNameDialog) {
        EditNameDialog(
            currentName = user?.username ?: "",
            onDismiss = { showEditNameDialog = false },
            onSave = { newName ->
                viewModel?.updateProfile(
                    newUsername = newName,
                    onSuccess = { updatedUser ->
                        showEditNameDialog = false
                        snackbarMessage = "Username updated successfully!"
                        onUserUpdated(updatedUser)
                    },
                    onError = { error ->
                        snackbarMessage = "Error: $error"
                    }
                )
            },
            isDark = isDark
        )
    }

    if (showChangePasswordDialog) {
        ChangePasswordDialog(
            onDismiss = { showChangePasswordDialog = false },
            onSave = { currentPass, newPass ->
                viewModel?.changePassword(
                    currentPassword = currentPass,
                    newPassword = newPass,
                    onSuccess = {
                        showChangePasswordDialog = false
                        snackbarMessage = "Password changed successfully!"
                    },
                    onError = { error ->
                        snackbarMessage = "Error: $error"
                    }
                )
            },
            isDark = isDark
        )
    }
}

@Composable
private fun EditNameDialog(
    currentName: String,
    onDismiss: () -> Unit,
    onSave: (String) -> Unit,
    isDark: Boolean = false
) {
    var name by remember { mutableStateOf(currentName) }

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(20.dp),
        containerColor = if (isDark) Color(0xFF1E293B) else Color.White,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Edit, contentDescription = null, tint = Color(0xFF2563EB))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Change Username", fontWeight = FontWeight.Bold, color = if (isDark) Color.White else Color(0xFF0F172A))
            }
        },
        text = {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("New username") },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF2563EB),
                    unfocusedBorderColor = if (isDark) Color(0xFF334155) else Color(0xFFE2E8F0),
                    focusedContainerColor = if (isDark) Color(0xFF0F172A) else Color(0xFFF8FAFC),
                    unfocusedContainerColor = if (isDark) Color(0xFF0F172A) else Color(0xFFF8FAFC)
                )
            )
        },
        confirmButton = {
            Button(
                onClick = { if (name.isNotBlank()) onSave(name) },
                enabled = name.isNotBlank() && name != currentName,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB))
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
    onSave: (String, String) -> Unit,
    isDark: Boolean = false
) {
    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    val passwordsMatch = newPassword == confirmPassword && newPassword.length >= 4

    AlertDialog(
        onDismissRequest = onDismiss,
        shape = RoundedCornerShape(20.dp),
        containerColor = if (isDark) Color(0xFF1E293B) else Color.White,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Lock, contentDescription = null, tint = Color(0xFF06D6A0))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Change Password", fontWeight = FontWeight.Bold, color = if (isDark) Color.White else Color(0xFF0F172A))
            }
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
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF06D6A0),
                        unfocusedBorderColor = if (isDark) Color(0xFF334155) else Color(0xFFE2E8F0),
                        focusedContainerColor = if (isDark) Color(0xFF0F172A) else Color(0xFFF8FAFC),
                        unfocusedContainerColor = if (isDark) Color(0xFF0F172A) else Color(0xFFF8FAFC)
                    )
                )
                OutlinedTextField(
                    value = newPassword,
                    onValueChange = { newPassword = it },
                    label = { Text("New password (min 4 characters)") },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF06D6A0),
                        unfocusedBorderColor = if (isDark) Color(0xFF334155) else Color(0xFFE2E8F0),
                        focusedContainerColor = if (isDark) Color(0xFF0F172A) else Color(0xFFF8FAFC),
                        unfocusedContainerColor = if (isDark) Color(0xFF0F172A) else Color(0xFFF8FAFC)
                    )
                )
                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    label = { Text("Confirm new password") },
                    singleLine = true,
                    visualTransformation = PasswordVisualTransformation(),
                    isError = confirmPassword.isNotBlank() && !passwordsMatch,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF06D6A0),
                        unfocusedBorderColor = if (isDark) Color(0xFF334155) else Color(0xFFE2E8F0),
                        errorBorderColor = Color(0xFFEF4444),
                        focusedContainerColor = if (isDark) Color(0xFF0F172A) else Color(0xFFF8FAFC),
                        unfocusedContainerColor = if (isDark) Color(0xFF0F172A) else Color(0xFFF8FAFC)
                    )
                )
                if (confirmPassword.isNotBlank() && !passwordsMatch) {
                    Text(
                        "Passwords don't match or too short",
                        color = Color(0xFFEF4444),
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
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF06D6A0))
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
    iconColor: Color,
    isDark: Boolean = false,
    showDivider: Boolean = false
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(iconColor.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(22.dp))
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(label, fontSize = 12.sp, color = if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B))
                Text(value, fontSize = 15.sp, fontWeight = FontWeight.SemiBold, color = if (isDark) Color(0xFFF1F5F9) else Color(0xFF0F172A))
            }
        }
        if (showDivider) {
            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 18.dp),
                color = if (isDark) Color(0xFF334155) else Color(0xFFE2E8F0)
            )
        }
    }
}

@Composable
private fun SettingsItem(
    icon: ImageVector,
    label: String,
    description: String = "",
    iconColor: Color,
    isDark: Boolean = false,
    onClick: () -> Unit = {},
    showDivider: Boolean = false
) {
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick)
                .padding(horizontal = 18.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(iconColor.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(22.dp))
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(label, fontSize = 15.sp, fontWeight = FontWeight.Medium, color = if (isDark) Color(0xFFF1F5F9) else Color(0xFF0F172A))
                if (description.isNotEmpty()) {
                    Text(description, fontSize = 12.sp, color = if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B))
                }
            }
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = if (isDark) Color(0xFF475569) else Color(0xFFCBD5E1), modifier = Modifier.size(20.dp))
        }
        if (showDivider) {
            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 18.dp),
                color = if (isDark) Color(0xFF334155) else Color(0xFFE2E8F0)
            )
        }
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
