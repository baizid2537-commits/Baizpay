package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.BaizPayViewModel
import com.example.ui.Screen
import com.example.ui.screens.AdminPanelScreen
import com.example.ui.screens.AiAssistantScreen
import com.example.ui.screens.AuthScreen
import com.example.ui.screens.BaizPayBottomBar
import com.example.ui.screens.BaizPayTopBar
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.MarketplaceScreen
import com.example.ui.screens.MicroTasksScreen
import com.example.ui.screens.OnboardingScreen
import com.example.ui.screens.ReferralScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.SplashScreen
import com.example.ui.screens.VerifyAccountScreen
import com.example.ui.screens.WalletScreen
import com.example.ui.theme.BaizPayTheme

class MainActivity : ComponentActivity() {

    private val viewModel: BaizPayViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val isDarkTheme by viewModel.isDarkTheme.collectAsStateWithLifecycle()
            val currentScreen by viewModel.currentScreen.collectAsStateWithLifecycle()
            val userProfile by viewModel.userProfile.collectAsStateWithLifecycle()
            val transactions by viewModel.transactions.collectAsStateWithLifecycle()
            val tasks by viewModel.tasks.collectAsStateWithLifecycle()
            val referrals by viewModel.referrals.collectAsStateWithLifecycle()
            val products by viewModel.products.collectAsStateWithLifecycle()
            val notifications by viewModel.notifications.collectAsStateWithLifecycle()
            val transactionFilter by viewModel.transactionFilter.collectAsStateWithLifecycle()
            val productCategoryFilter by viewModel.productCategoryFilter.collectAsStateWithLifecycle()
            val chatMessages by viewModel.chatMessages.collectAsStateWithLifecycle()
            val isAiLoading by viewModel.isAiLoading.collectAsStateWithLifecycle()

            val unreadNotifCount = notifications.count { !it.isRead }

            BaizPayTheme(darkTheme = isDarkTheme) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    when (currentScreen) {
                        Screen.SPLASH -> {
                            SplashScreen(
                                onNext = { viewModel.navigateTo(Screen.ONBOARDING) }
                            )
                        }

                        Screen.ONBOARDING -> {
                            OnboardingScreen(
                                onGetStarted = { viewModel.navigateTo(Screen.AUTH) }
                            )
                        }

                        Screen.AUTH -> {
                            AuthScreen(
                                onAuthSuccess = { email, name ->
                                    viewModel.authenticateUser(email, name)
                                    // If account not verified yet, navigate to Verify Account ($5 USD flow)
                                    if (userProfile?.isVerified == false) {
                                        viewModel.navigateTo(Screen.VERIFY_ACCOUNT)
                                    } else {
                                        viewModel.navigateTo(Screen.DASHBOARD)
                                    }
                                }
                            )
                        }

                        Screen.VERIFY_ACCOUNT -> {
                            VerifyAccountScreen(
                                onVerificationSuccess = { paymentMethod ->
                                    viewModel.verifyAccount(paymentMethod)
                                    viewModel.navigateTo(Screen.DASHBOARD)
                                },
                                onSkipForDemo = {
                                    viewModel.navigateTo(Screen.DASHBOARD)
                                }
                            )
                        }

                        else -> {
                            Scaffold(
                                topBar = {
                                    BaizPayTopBar(
                                        currentScreen = currentScreen,
                                        unreadNotificationCount = unreadNotifCount,
                                        isDarkTheme = isDarkTheme,
                                        onToggleTheme = { viewModel.toggleTheme() },
                                        onNavigate = { viewModel.navigateTo(it) }
                                    )
                                },
                                bottomBar = {
                                    BaizPayBottomBar(
                                        currentScreen = currentScreen,
                                        onNavigate = { viewModel.navigateTo(it) }
                                    )
                                }
                            ) { innerPadding ->
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(innerPadding)
                                ) {
                                    Crossfade(
                                        targetState = currentScreen,
                                        label = "ScreenTransition"
                                    ) { screen ->
                                        when (screen) {
                                            Screen.DASHBOARD -> DashboardScreen(
                                                userProfile = userProfile,
                                                transactions = transactions,
                                                onNavigate = { viewModel.navigateTo(it) }
                                            )

                                            Screen.WALLET -> WalletScreen(
                                                userProfile = userProfile,
                                                transactions = transactions,
                                                selectedFilter = transactionFilter,
                                                onFilterChange = { viewModel.setTransactionFilter(it) },
                                                onDeposit = { amount, method ->
                                                    viewModel.depositFunds(amount, method)
                                                },
                                                onWithdraw = { amount, method, payoutAccount, callback ->
                                                    viewModel.withdrawFunds(amount, method, payoutAccount, callback)
                                                }
                                            )

                                            Screen.TASKS -> MicroTasksScreen(
                                                tasks = tasks,
                                                onCompleteTask = { viewModel.completeTask(it) }
                                            )

                                            Screen.REFERRALS -> ReferralScreen(
                                                userProfile = userProfile,
                                                referrals = referrals,
                                                onAddDemoReferral = { name, email, level ->
                                                    viewModel.addReferral(name, email, level)
                                                },
                                                onClaimMonthlySalary = { amount, rankName ->
                                                    viewModel.claimMonthlySalary(amount, rankName)
                                                }
                                            )

                                            Screen.MARKETPLACE -> MarketplaceScreen(
                                                products = products,
                                                selectedCategory = productCategoryFilter,
                                                onCategoryChange = { viewModel.setProductCategoryFilter(it) },
                                                onBuyProduct = { product, callback ->
                                                    viewModel.buyProduct(product, callback)
                                                },
                                                onListProduct = { title, cat, price, desc ->
                                                    viewModel.listProduct(title, cat, price, desc)
                                                }
                                            )

                                            Screen.ADMIN -> AdminPanelScreen(
                                                userProfile = userProfile,
                                                transactions = transactions,
                                                onApproveWithdrawal = { txId ->
                                                    viewModel.approveWithdrawal(txId)
                                                }
                                            )

                                            Screen.AI_ASSISTANT -> AiAssistantScreen(
                                                messages = chatMessages,
                                                isLoading = isAiLoading,
                                                onSendPrompt = { viewModel.sendAiPrompt(it) }
                                            )

                                            Screen.SETTINGS -> SettingsScreen(
                                                userProfile = userProfile,
                                                isDarkTheme = isDarkTheme,
                                                onToggleTheme = { viewModel.toggleTheme() },
                                                onLogout = { viewModel.logout() }
                                            )

                                            else -> DashboardScreen(
                                                userProfile = userProfile,
                                                transactions = transactions,
                                                onNavigate = { viewModel.navigateTo(it) }
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
