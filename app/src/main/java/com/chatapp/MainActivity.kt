package com.chatapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.chatapp.data.local.TokenStorage
import com.chatapp.data.network.RetrofitClient
import com.chatapp.data.network.SocketService
import com.chatapp.ui.AuthViewModel
import com.chatapp.ui.AuthViewModelFactory
import com.chatapp.ui.RoomViewModel
import com.chatapp.ui.RoomViewModelFactory
import com.chatapp.ui.chat.ChatRoomScreen
import com.chatapp.ui.login.LoginScreen
import com.chatapp.ui.register.RegisterScreen
import com.chatapp.ui.rooms.RoomListScreen
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        RetrofitClient.init(this)
        
        // Restore token if available
        runBlocking {
            val token = TokenStorage(this@MainActivity).token.first()
            if (token != null) {
                RetrofitClient.setToken(token)
            }
        }
        
        setContent {
            ChatApp()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        SocketService.instance.disconnect()
    }
}

@Composable
fun ChatApp() {
    val navController = rememberNavController()
    val context = LocalContext.current
    val authViewModel: AuthViewModel = viewModel(
        factory = AuthViewModelFactory(context.applicationContext)
    )
    val roomViewModel: RoomViewModel = viewModel(
        factory = RoomViewModelFactory(context.applicationContext)
    )
    
    val authState by authViewModel.state.collectAsState()

    MaterialTheme(
        colorScheme = lightColorScheme(
            primary = Color(0xFF6C63FF),
            secondary = Color(0xFF03DAC6),
            tertiary = Color(0xFFFF4081)
        )
    ) {
        NavHost(
            navController = navController,
            startDestination = "splash"
        ) {
            composable("splash") {
                SplashScreen(
                    isAuthenticated = authState.isAuthenticated,
                    onNavigate = { route ->
                        navController.navigate(route) {
                            popUpTo("splash") { inclusive = true }
                        }
                    }
                )
            }
            composable("login") {
                LoginScreen(
                    onLoginSuccess = {
                        navController.navigate("rooms") {
                            popUpTo("login") { inclusive = true }
                        }
                    },
                    onNavigateToRegister = {
                        navController.navigate("register")
                    }
                )
            }
            composable("register") {
                RegisterScreen(
                    onRegisterSuccess = {
                        navController.navigate("rooms") {
                            popUpTo("register") { inclusive = true }
                        }
                    },
                    onNavigateToLogin = {
                        navController.popBackStack()
                    }
                )
            }
            composable("rooms") {
                RoomListScreen(
                    onRoomClick = { room ->
                        navController.navigate("chat/${room.id}/${room.name}")
                    },
                    onLogout = {
                        authViewModel.logout()
                        navController.navigate("login") {
                            popUpTo("rooms") { inclusive = true }
                        }
                    }
                )
            }
            composable("chat/{roomId}/{roomName}") { backStackEntry ->
                val roomId = backStackEntry.arguments?.getString("roomId")?.toIntOrNull() ?: return@composable
                val roomName = backStackEntry.arguments?.getString("roomName") ?: "Chat"
                ChatRoomScreen(
                    roomId = roomId,
                    roomName = roomName,
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}

@Composable
fun SplashScreen(
    isAuthenticated: Boolean,
    onNavigate: (String) -> Unit
) {
    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(500)
        onNavigate(if (isAuthenticated) "rooms" else "login")
    }
    
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            CircularProgressIndicator(
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text("Loading...", style = MaterialTheme.typography.bodyLarge)
        }
    }
}
