package com.suplz.aichat.presentation.main

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.suplz.aichat.R
import com.suplz.aichat.presentation.auth.AuthScreen
import com.suplz.aichat.presentation.chat.ChatScreen
import com.suplz.aichat.presentation.chat_list.ChatListScreen
import com.suplz.aichat.presentation.images.ImageGalleryScreen
import com.suplz.aichat.presentation.profile.ProfileScreen
import kotlinx.coroutines.launch

@Composable
fun MainScreen(startDestination: String) {
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val gesturesEnabled = currentRoute?.startsWith("chat_list") == true ||
            currentRoute == Screen.Profile.route ||
            currentRoute == Screen.ImageGallery.route

    val closeDrawer = { scope.launch { drawerState.close() } }
    val openDrawer = { scope.launch { drawerState.open() } }

    val onCreateChat = {
        closeDrawer()
        val newChatId = java.util.UUID.randomUUID().toString()
        navController.navigate(Screen.Chat.createRoute(newChatId, isNew = true))
    }

    LaunchedEffect(currentRoute) {
        if (drawerState.isOpen) {
            drawerState.close()
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = gesturesEnabled,
        drawerContent = {
            ModalDrawerSheet(
                modifier = Modifier.width(320.dp),
                drawerContainerColor = MaterialTheme.colorScheme.surface,
            ) {
                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp)
                            .clip(RoundedCornerShape(25.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                            .clickable {
                                closeDrawer()
                                navController.navigate(Screen.ChatList.createRoute(focusSearch = true)) {
                                    popUpTo(Screen.ChatList.route) { inclusive = true }
                                }
                            },
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = stringResource(R.string.search_title),
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    IconButton(
                        onClick = onCreateChat,
                        modifier = Modifier
                            .size(50.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                    ) {
                        Icon(Icons.Default.Create, contentDescription = stringResource(R.string.desc_add_chat))
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.Create, contentDescription = null) },
                    label = { Text(stringResource(R.string.new_chat)) },
                    selected = false,
                    onClick = onCreateChat,
                    modifier = Modifier.padding(horizontal = 12.dp)
                )

                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.Image, contentDescription = null) },
                    label = { Text(stringResource(R.string.images)) },
                    selected = currentRoute == Screen.ImageGallery.route,
                    onClick = {
                        closeDrawer()
                        navController.navigate(Screen.ImageGallery.route) {
                            popUpTo(Screen.ChatList.route)
                        }
                    },
                    modifier = Modifier.padding(horizontal = 12.dp)
                )

                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.Home, contentDescription = null) },
                    label = { Text(stringResource(R.string.main_chat_list)) },
                    selected = currentRoute?.startsWith("chat_list") == true,
                    onClick = {
                        closeDrawer()
                        navController.navigate(Screen.ChatList.createRoute(focusSearch = false)) {
                            popUpTo(Screen.ChatList.route) { inclusive = true }
                        }
                    },
                    modifier = Modifier.padding(horizontal = 12.dp)
                )

                NavigationDrawerItem(
                    icon = { Icon(Icons.Default.Person, contentDescription = null) },
                    label = { Text(stringResource(R.string.profile)) },
                    selected = currentRoute == Screen.Profile.route,
                    onClick = {
                        closeDrawer()
                        navController.navigate(Screen.Profile.route) {
                            popUpTo(Screen.ChatList.route)
                        }
                    },
                    modifier = Modifier.padding(horizontal = 12.dp)
                )
            }
        }
    ) {
        NavHost(navController = navController, startDestination = startDestination) {
            composable(Screen.Auth.route) {
                AuthScreen(
                    onNavigateToChatList = {
                        navController.navigate(Screen.ChatList.createRoute(focusSearch = false)) {
                            popUpTo(Screen.Auth.route) { inclusive = true }
                        }
                    }
                )
            }

            composable(
                route = Screen.ChatList.route,
                arguments = listOf(
                    navArgument("focusSearch") { type = NavType.BoolType; defaultValue = false }
                )
            ) { backStackEntry ->
                val focusSearch = backStackEntry.arguments?.getBoolean("focusSearch") ?: false
                ChatListScreen(
                    focusSearch = focusSearch,
                    onNavigateToChat = { chatId, isNew ->
                        navController.navigate(Screen.Chat.createRoute(chatId, isNew))
                    },
                    onOpenDrawer = { openDrawer() }
                )
            }

            composable(
                route = Screen.Chat.route,
                arguments = listOf(
                    navArgument("chatId") { type = NavType.StringType },
                    navArgument("isNew") { type = NavType.BoolType; defaultValue = false }
                )
            ) {
                ChatScreen(onNavigateBack = { navController.popBackStack() })
            }

            composable(Screen.Profile.route) {
                ProfileScreen(
                    onOpenDrawer = { openDrawer() },
                    onNavigateToAuth = {
                        navController.navigate(Screen.Auth.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
            }

            composable(Screen.ImageGallery.route) {
                ImageGalleryScreen(
                    onNavigateToChat = { chatId ->
                        navController.navigate(Screen.Chat.createRoute(chatId, false))
                    },
                    onOpenDrawer = { openDrawer() }
                )
            }
        }
    }
}