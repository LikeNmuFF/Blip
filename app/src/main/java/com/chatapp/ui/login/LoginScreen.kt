package com.chatapp.ui.login

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.chatapp.data.local.ThemeManager
import com.chatapp.ui.AuthViewModel
import com.chatapp.ui.AuthViewModelFactory
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onNavigateToRegister: () -> Unit,
    viewModel: AuthViewModel = viewModel(
        factory = AuthViewModelFactory(LocalContext.current)
    )
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    val state by viewModel.state.collectAsState()
    val themeManager = ThemeManager(LocalContext.current)
    val isDark by themeManager.isDarkMode.collectAsState(initial = false)

    val cardElevation = animateFloatAsState(if (state.isLoading) 8f else 2f, label = "elevation")

    LaunchedEffect(state.isAuthenticated) {
        if (state.isAuthenticated) onLoginSuccess()
    }

    val bgGradient = if (isDark) {
        Brush.verticalGradient(
            colors = listOf(
                Color(0xFF0B1120),
                Color(0xFF0F172A),
                Color(0xFF111827)
            )
        )
    } else {
        Brush.verticalGradient(
            colors = listOf(
                Color(0xFF0F172A),
                Color(0xFF1E3A5F),
                Color(0xFF2563EB)
            )
        )
    }

    val surfaceColor = if (isDark) Color(0xFF111827) else Color.White
    val onSurfaceColor = if (isDark) Color(0xFFF1F5F9) else Color(0xFF0F172A)
    val outlineColor = if (isDark) Color(0xFF334155) else Color(0xFFE2E8F0)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(bgGradient)
    ) {
        // Decorative circles
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(x = 60.dp, y = (-40).dp)
                .size(200.dp)
                .clip(androidx.compose.foundation.shape.CircleShape)
                .background(Color(0xFF3B82F6).copy(alpha = 0.15f))
        )
        Box(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .offset(x = (-30).dp, y = 40.dp)
                .size(150.dp)
                .clip(androidx.compose.foundation.shape.CircleShape)
                .background(Color(0xFF06D6A0).copy(alpha = 0.1f))
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp)
                .imePadding(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.height(48.dp))

            // Logo / Brand
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(androidx.compose.foundation.shape.RoundedCornerShape(20.dp))
                    .background(
                        Brush.linearGradient(
                            colors = listOf(Color(0xFF3B82F6), Color(0xFF06D6A0))
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "B",
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Black,
                    color = Color.White,
                    letterSpacing = (-1).sp
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Blip",
                fontSize = 32.sp,
                fontWeight = FontWeight.Black,
                color = Color.White,
                letterSpacing = (-0.5).sp
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Chat with anyone, anywhere",
                fontSize = 14.sp,
                color = Color.White.copy(alpha = 0.6f),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(40.dp))

            // Login card
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .graphicsLayer { shadowElevation = cardElevation.value }
                    .clip(androidx.compose.foundation.shape.RoundedCornerShape(24.dp)),
                color = surfaceColor,
                shadowElevation = 4.dp
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Welcome back",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = onSurfaceColor
                    )

                    // Username field
                    OutlinedTextField(
                        value = username,
                        onValueChange = { username = it },
                        label = { Text("Username") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = if (isDark) Color(0xFF3B82F6) else Color(0xFF2563EB),
                            unfocusedBorderColor = outlineColor,
                            focusedContainerColor = if (isDark) Color(0xFF1E293B) else Color(0xFFF8FAFC),
                            unfocusedContainerColor = if (isDark) Color(0xFF1E293B) else Color(0xFFF8FAFC)
                        )
                    )

                    // Password field
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Password") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Password,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(onDone = {
                            if (username.isNotBlank() && password.isNotBlank()) {
                                viewModel.login(username, password)
                            }
                        }),
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                    contentDescription = null,
                                    tint = if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B)
                                )
                            }
                        },
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = if (isDark) Color(0xFF3B82F6) else Color(0xFF2563EB),
                            unfocusedBorderColor = outlineColor,
                            focusedContainerColor = if (isDark) Color(0xFF1E293B) else Color(0xFFF8FAFC),
                            unfocusedContainerColor = if (isDark) Color(0xFF1E293B) else Color(0xFFF8FAFC)
                        )
                    )

                    // Error message
                    if (state.error != null) {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = androidx.compose.foundation.shape.RoundedCornerShape(10.dp),
                            color = if (isDark) Color(0xFF450A0A) else Color(0xFFFEE2E2)
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Error,
                                    contentDescription = null,
                                    tint = if (isDark) Color(0xFFF87171) else Color(0xFFEF4444),
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = state.error!!,
                                    color = if (isDark) Color(0xFFFCA5A5) else Color(0xFFB91C1C),
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }

                    // Login button
                    Button(
                        onClick = {
                            if (username.isNotBlank() && password.isNotBlank()) {
                                viewModel.login(username, password)
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        enabled = !state.isLoading && username.isNotBlank() && password.isNotBlank(),
                        shape = androidx.compose.foundation.shape.RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isDark) Color(0xFF3B82F6) else Color(0xFF2563EB)
                        )
                    ) {
                        if (state.isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(22.dp),
                                strokeWidth = 2.5.dp,
                                color = Color.White
                            )
                        } else {
                            Text(
                                text = "Sign In",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Register link
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Don't have an account?",
                    color = Color.White.copy(alpha = 0.6f),
                    fontSize = 14.sp
                )
                TextButton(onClick = onNavigateToRegister) {
                    Text(
                        text = "Sign Up",
                        color = Color(0xFF06D6A0),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Theme toggle
            Spacer(modifier = Modifier.height(16.dp))
            Surface(
                modifier = Modifier
                    .clip(androidx.compose.foundation.shape.RoundedCornerShape(20.dp)),
                color = Color.White.copy(alpha = 0.1f)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = if (isDark) "\uD83C\uDF19" else "\u2600\uFE0F",
                        fontSize = 14.sp
                    )
                    Switch(
                        checked = isDark,
                        onCheckedChange = { value ->
                            kotlinx.coroutines.MainScope().launch {
                                themeManager.setDarkMode(value)
                            }
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = Color(0xFF3B82F6),
                            uncheckedThumbColor = Color.White,
                            uncheckedTrackColor = Color.White.copy(alpha = 0.3f)
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
