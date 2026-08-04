package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.GroupAdd
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.TaskAlt
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BaizPayTopBar(
    currentScreen: Screen,
    unreadNotificationCount: Int,
    isDarkTheme: Boolean,
    onToggleTheme: () -> Unit,
    onNavigate: (Screen) -> Unit
) {
    TopAppBar(
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .border(1.dp, Color(0xFFF59E0B), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.img_baizpay_logo_1785877492392),
                        contentDescription = "BaizPay Logo",
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = when (currentScreen) {
                        Screen.DASHBOARD -> "BaizPay Wallet"
                        Screen.WALLET -> "Digital Wallet"
                        Screen.TASKS -> "Micro Tasks"
                        Screen.REFERRALS -> "USD $2 Referrals"
                        Screen.MARKETPLACE -> "Marketplace"
                        Screen.ADMIN -> "Admin Console"
                        Screen.AI_ASSISTANT -> "AI Advisor"
                        Screen.SETTINGS -> "Settings"
                        Screen.VERIFY_ACCOUNT -> "Account Verification"
                        else -> "BaizPay"
                    },
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
        },
        actions = {
            // Admin Toggle Icon Button
            IconButton(
                onClick = { onNavigate(Screen.ADMIN) },
                modifier = Modifier.testTag("topbar_admin_button")
            ) {
                Icon(
                    imageVector = Icons.Default.AdminPanelSettings,
                    contentDescription = "Admin Console",
                    tint = if (currentScreen == Screen.ADMIN) Color(0xFF8B5CF6) else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Theme Toggle Button
            IconButton(
                onClick = { onToggleTheme() },
                modifier = Modifier.testTag("topbar_theme_button")
            ) {
                Icon(
                    imageVector = if (isDarkTheme) Icons.Default.LightMode else Icons.Default.DarkMode,
                    contentDescription = "Theme Toggle",
                    tint = Color(0xFFF59E0B)
                )
            }

            // Notifications Icon
            IconButton(
                onClick = { onNavigate(Screen.SETTINGS) },
                modifier = Modifier.testTag("topbar_notif_button")
            ) {
                BadgedBox(
                    badge = {
                        if (unreadNotificationCount > 0) {
                            Badge(containerColor = Color(0xFFEF4444)) {
                                Text("$unreadNotificationCount", color = Color.White)
                            }
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = "Notifications",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.background
        )
    )
}

@Composable
fun BaizPayBottomBar(
    currentScreen: Screen,
    onNavigate: (Screen) -> Unit
) {
    val navItems = listOf(
        NavItem(Screen.DASHBOARD, "Home", Icons.Default.Home, "nav_home"),
        NavItem(Screen.WALLET, "Wallet", Icons.Default.AccountBalanceWallet, "nav_wallet"),
        NavItem(Screen.TASKS, "Tasks", Icons.Default.TaskAlt, "nav_tasks"),
        NavItem(Screen.REFERRALS, "Refer $2", Icons.Default.GroupAdd, "nav_referrals"),
        NavItem(Screen.MARKETPLACE, "Market", Icons.Default.ShoppingBag, "nav_market")
    )

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp
    ) {
        navItems.forEach { item ->
            val isSelected = currentScreen == item.screen
            NavigationBarItem(
                selected = isSelected,
                onClick = { onNavigate(item.screen) },
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.label,
                        tint = if (isSelected) Color(0xFF2563EB) else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                label = {
                    Text(
                        text = item.label,
                        fontSize = 10.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = if (isSelected) Color(0xFF2563EB) else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                modifier = Modifier.testTag(item.testTag),
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = Color(0xFF2563EB).copy(alpha = 0.15f)
                )
            )
        }
    }
}

private data class NavItem(
    val screen: Screen,
    val label: String,
    val icon: ImageVector,
    val testTag: String
)
