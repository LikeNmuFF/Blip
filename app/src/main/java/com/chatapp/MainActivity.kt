package com.chatapp

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
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
import com.chatapp.ui.profile.ProfileScreen
import com.chatapp.ui.register.RegisterScreen
import com.chatapp.ui.rooms.CreateRoomScreen
import com.chatapp.ui.rooms.RoomListScreen
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        RetrofitClient.init(this)

        runBlocking {
            val token = TokenStorage(this@MainActivity).token.first()
            if (token != null) {
                RetrofitClient.setToken(token)
            }
        }

        setContent {
            val themeManager = com.chatapp.data.local.ThemeManager(applicationContext)
            val isDark by themeManager.isDarkMode.collectAsState(initial = false)
            com.chatapp.ui.theme.ChatAppTheme(darkTheme = isDark) {
                ChatApp()
            }
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
                currentUser = authState.user,
                onRoomClick = { room ->
                    val encodedName = Uri.encode(room.name)
                    navController.navigate("chat/${room.id}/$encodedName/${room.isPrivate}")
                },
                onLogout = {
                    authViewModel.logout()
                    navController.navigate("login") {
                        popUpTo("rooms") { inclusive = true }
                    }
                },
                onCreateRoom = {
                    navController.navigate("createRoom")
                },
                onProfileClick = {
                    navController.navigate("profile")
                },
                viewModel = roomViewModel
            )
        }
        composable("createRoom") {
            CreateRoomScreen(
                onBack = { navController.popBackStack() },
                onRoomCreated = {
                    navController.popBackStack()
                    roomViewModel.loadRooms()
                },
                viewModel = roomViewModel
            )
        }
        composable("profile") {
            ProfileScreen(
                user = authState.user,
                onBack = { navController.popBackStack() },
                onLogout = {
                    authViewModel.logout()
                    navController.navigate("login") {
                        popUpTo("profile") { inclusive = true }
                        popUpTo("rooms") { inclusive = true }
                    }
                },
                viewModel = roomViewModel
            )
        }
        composable("chat/{roomId}/{roomName}/{isPrivate}") { backStackEntry ->
            val roomId = backStackEntry.arguments?.getString("roomId")?.toIntOrNull() ?: return@composable
            val encodedRoomName = backStackEntry.arguments?.getString("roomName") ?: "Chat"
            val roomName = Uri.decode(encodedRoomName)
            val isPrivate = backStackEntry.arguments?.getString("isPrivate")?.toBooleanStrictOrNull() ?: false
            ChatRoomScreen(
                roomId = roomId,
                roomName = roomName,
                isPrivate = isPrivate,
                currentUser = authState.user,
                currentUsername = authState.user?.username,
                onBack = { navController.popBackStack() },
                viewModel = roomViewModel
            )
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
