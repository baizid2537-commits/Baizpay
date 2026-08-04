package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.BaizPayDatabase
import com.example.data.local.entities.ProductEntity
import com.example.data.local.entities.TaskEntity
import com.example.data.local.entities.TransactionEntity
import com.example.data.local.entities.UserEntity
import com.example.data.repository.BaizPayRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

enum class Screen {
    SPLASH,
    ONBOARDING,
    AUTH,
    VERIFY_ACCOUNT,
    DASHBOARD,
    WALLET,
    TASKS,
    REFERRALS,
    MARKETPLACE,
    ADMIN,
    AI_ASSISTANT,
    SETTINGS
}

data class ChatMessage(
    val sender: String, // "USER" or "AI"
    val message: String,
    val timestamp: Long = System.currentTimeMillis()
)

class BaizPayViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: BaizPayRepository

    val userProfile: StateFlow<UserEntity?>
    val transactions: StateFlow<List<TransactionEntity>>
    val tasks: StateFlow<List<TaskEntity>>
    val referrals: StateFlow<List<ReferralEntityState>>
    val products: StateFlow<List<ProductEntity>>
    val notifications: StateFlow<List<com.example.data.local.entities.NotificationEntity>>

    private val _currentScreen = MutableStateFlow(Screen.AUTH) // Start on Auth (Register/Login screen)
    val currentScreen: StateFlow<Screen> = _currentScreen.asStateFlow()

    private val _isDarkTheme = MutableStateFlow(true) // Modern Dark Navy by default
    val isDarkTheme: StateFlow<Boolean> = _isDarkTheme.asStateFlow()

    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    // Filter & Search states
    private val _transactionFilter = MutableStateFlow("ALL") // ALL, DEPOSIT, WITHDRAWAL, TASK_REWARD, REFERRAL_BONUS
    val transactionFilter: StateFlow<String> = _transactionFilter.asStateFlow()

    private val _productCategoryFilter = MutableStateFlow("ALL") // ALL, DIGITAL, GIFTCARD, ELECTRONICS, SUBSCRIPTION
    val productCategoryFilter: StateFlow<String> = _productCategoryFilter.asStateFlow()

    // AI Chat History
    private val _chatMessages = MutableStateFlow<List<ChatMessage>>(
        listOf(
            ChatMessage("AI", "Hello Baizid! I'm your BaizPay AI Advisor. Ask me anything about optimizing daily task rewards, referral strategies, or digital wallet security.")
        )
    )
    val chatMessages: StateFlow<List<ChatMessage>> = _chatMessages.asStateFlow()

    private val _isAiLoading = MutableStateFlow(false)
    val isAiLoading: StateFlow<Boolean> = _isAiLoading.asStateFlow()

    private val httpClient = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .build()

    init {
        val dao = BaizPayDatabase.getDatabase(application).baizPayDao()
        repository = BaizPayRepository(dao)

        userProfile = repository.userProfile.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)
        transactions = repository.transactions.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
        tasks = repository.tasks.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
        referrals = repository.referrals.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
        products = repository.products.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
        notifications = repository.notifications.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

        viewModelScope.launch {
            repository.seedInitialDataIfNeeded()
        }
    }

    fun navigateTo(screen: Screen) {
        _currentScreen.value = screen
    }

    fun toggleTheme() {
        _isDarkTheme.value = !_isDarkTheme.value
    }

    fun setTransactionFilter(filter: String) {
        _transactionFilter.value = filter
    }

    fun setProductCategoryFilter(category: String) {
        _productCategoryFilter.value = category
    }

    fun verifyAccount(paymentMethod: String) {
        viewModelScope.launch {
            repository.verifyAccount(paymentMethod)
        }
    }

    fun depositFunds(amount: Double, method: String) {
        viewModelScope.launch {
            repository.addDeposit(amount, method)
        }
    }

    fun withdrawFunds(amount: Double, method: String, payoutAccount: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val success = repository.requestWithdrawal(amount, method, payoutAccount)
            onResult(success)
        }
    }

    fun completeTask(task: TaskEntity) {
        viewModelScope.launch {
            repository.completeTask(task)
        }
    }

    fun addReferral(name: String, email: String) {
        viewModelScope.launch {
            repository.addReferral(name, email)
        }
    }

    fun buyProduct(product: ProductEntity, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val success = repository.purchaseProduct(product)
            onResult(success)
        }
    }

    fun listProduct(title: String, category: String, price: Double, desc: String) {
        viewModelScope.launch {
            val product = ProductEntity(
                title = title,
                category = category,
                price = price,
                sellerName = userProfile.value?.fullName ?: "Baizid Ahmed",
                description = desc,
                rating = 5.0f,
                reviewCount = 1
            )
            repository.addProduct(product)
        }
    }

    fun approveWithdrawal(txId: String) {
        viewModelScope.launch {
            repository.approveWithdrawal(txId)
        }
    }

    fun login(email: String, pass: String) {
        _isLoggedIn.value = true
        _currentScreen.value = Screen.DASHBOARD
    }

    fun authenticateUser(email: String, name: String) {
        _isLoggedIn.value = true
        viewModelScope.launch {
            userProfile.value?.let { current ->
                repository.updateUser(current.copy(fullName = name, email = email))
            }
        }
        _currentScreen.value = Screen.DASHBOARD
    }

    fun addReferral(name: String, email: String, level: Int = 1) {
        viewModelScope.launch {
            repository.addReferral(name, email, level)
        }
    }

    fun claimMonthlySalary(amount: Double, rankName: String) {
        viewModelScope.launch {
            repository.claimMonthlySalary(amount, rankName)
        }
    }

    fun logout() {
        _isLoggedIn.value = false
        _currentScreen.value = Screen.AUTH
    }

    fun sendAiPrompt(userQuery: String) {
        if (userQuery.isBlank()) return
        val updatedList = _chatMessages.value.toMutableList()
        updatedList.add(ChatMessage("USER", userQuery))
        _chatMessages.value = updatedList
        _isAiLoading.value = true

        viewModelScope.launch {
            val apiKey = getGeminiApiKey()
            if (apiKey.isNotBlank()) {
                val responseText = callGeminiApi(userQuery, apiKey)
                val finalMessages = _chatMessages.value.toMutableList()
                finalMessages.add(ChatMessage("AI", responseText))
                _chatMessages.value = finalMessages
            } else {
                // Smart fallback AI response
                val fallbackAnswer = generateSmartFallbackAiResponse(userQuery)
                val finalMessages = _chatMessages.value.toMutableList()
                finalMessages.add(ChatMessage("AI", fallbackAnswer))
                _chatMessages.value = finalMessages
            }
            _isAiLoading.value = false
        }
    }

    private fun getGeminiApiKey(): String {
        return try {
            val field = com.example.BuildConfig::class.java.getField("GEMINI_API_KEY")
            field.get(null) as? String ?: ""
        } catch (e: Exception) {
            ""
        }
    }

    private suspend fun callGeminiApi(promptText: String, apiKey: String): String {
        return try {
            val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=$apiKey"
            val jsonBody = JSONObject().apply {
                put("contents", JSONArray().apply {
                    put(JSONObject().apply {
                        put("parts", JSONArray().apply {
                            put(JSONObject().apply {
                                put("text", "You are BaizPay AI Advisor for the user ${userProfile.value?.fullName ?: "Baizid"}. Provide concise, helpful financial advice regarding digital wallet earnings, referrals ($2/referral), micro tasks, and account verification ($5 USD). User query: $promptText")
                            })
                        })
                    })
                })
            }

            val request = Request.Builder()
                .url(url)
                .post(jsonBody.toString().toRequestBody("application/json".toMediaType()))
                .build()

            val response = httpClient.newCall(request).execute()
            if (response.isSuccessful) {
                val bodyStr = response.body?.string() ?: ""
                val jsonObj = JSONObject(bodyStr)
                val candidates = jsonObj.getJSONArray("candidates")
                if (candidates.length() > 0) {
                    val firstCandidate = candidates.getJSONObject(0)
                    val content = firstCandidate.getJSONObject("content")
                    val parts = content.getJSONArray("parts")
                    if (parts.length() > 0) {
                        return parts.getJSONObject(0).getString("text")
                    }
                }
            }
            generateSmartFallbackAiResponse(promptText)
        } catch (e: Exception) {
            generateSmartFallbackAiResponse(promptText)
        }
    }

    private fun generateSmartFallbackAiResponse(query: String): String {
        val q = query.lowercase()
        return when {
            q.contains("earn") || q.contains("task") ->
                "To maximize your daily earnings on BaizPay, maintain your 7-day Check-In streak, complete high-reward partner surveys, and spin the Lucky Wheel every 24 hours. Your task earnings are deposited directly into your available balance!"
            q.contains("refer") || q.contains("link") || q.contains("code") ->
                "You earn USD $2.00 for every verified referral who completes the account activation. Share your unique code '${userProfile.value?.referralCode ?: "BAIZ2026"}' or personal referral link via WhatsApp, Telegram, or email."
            q.contains("withdraw") || q.contains("payout") ->
                "Withdrawals are supported via PayPal, Bank Wire, Crypto USDT, Visa/Mastercard, and Mobile Wallets. The minimum withdrawal threshold is $10.00. Processing typically takes under 2 hours."
            q.contains("verify") || q.contains("5") ->
                "Account verification is a one-time USD $5.00 activation that unlocks all earning opportunities, priority payouts, and grants you an instant $5.00 welcome bonus in your wallet!"
            else ->
                "BaizPay is built to empower global digital earnings securely. You currently have $${String.format("%.2f", userProfile.value?.walletBalance ?: 0.0)} in your digital wallet. Let me know if you need help with tasks, marketplace sales, or withdrawal setups!"
        }
    }
}

typealias ReferralEntityState = com.example.data.local.entities.ReferralEntity
