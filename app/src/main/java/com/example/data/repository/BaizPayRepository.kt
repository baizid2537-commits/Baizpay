package com.example.data.repository

import com.example.data.local.dao.BaizPayDao
import com.example.data.local.entities.NotificationEntity
import com.example.data.local.entities.ProductEntity
import com.example.data.local.entities.ReferralEntity
import com.example.data.local.entities.TaskEntity
import com.example.data.local.entities.TransactionEntity
import com.example.data.local.entities.UserEntity
import kotlinx.coroutines.flow.Flow
import java.util.UUID

class BaizPayRepository(private val dao: BaizPayDao) {

    val userProfile: Flow<UserEntity?> = dao.getUserProfile()
    val transactions: Flow<List<TransactionEntity>> = dao.getAllTransactions()
    val tasks: Flow<List<TaskEntity>> = dao.getAllTasks()
    val referrals: Flow<List<ReferralEntity>> = dao.getAllReferrals()
    val products: Flow<List<ProductEntity>> = dao.getAllProducts()
    val notifications: Flow<List<NotificationEntity>> = dao.getAllNotifications()

    suspend fun seedInitialDataIfNeeded() {
        var user = dao.getUserProfileSync()
        if (user == null) {
            user = UserEntity(
                id = "user_main",
                fullName = "Baizid Ahmed",
                email = "baizid2537@gmail.com",
                isVerified = true, // Initialized as verified by default for owner Baizid Ahmed
                verificationDate = System.currentTimeMillis() - 86400000 * 5,
                walletBalance = 155.50,
                availableBalance = 140.00,
                pendingBalance = 15.50,
                referralEarnings = 58.00,
                taskEarnings = 48.50,
                salesEarnings = 53.00,
                referralCode = "BAIZ2026",
                totalReferrals = 28,
                activeReferrals = 26,
                pendingReferrals = 2,
                vipTier = "GOLD",
                monthlySalaryEarnings = 30.0,
                monthlyDirectReferrals = 28,
                salaryRank = "Silver",
                dailyCheckInStreak = 5,
                isAdmin = true
            )
            dao.insertOrUpdateUser(user)

            // Seed Initial Transactions
            val initialTxns = listOf(
                TransactionEntity(
                    transactionId = "TXN-BAIZ-8901",
                    title = "Account Verification $5 USD",
                    type = "DEPOSIT",
                    amount = 5.00,
                    status = "COMPLETED",
                    paymentMethod = "VISA",
                    timestamp = System.currentTimeMillis() - 86400000 * 5,
                    note = "BaizPay Instant Verification Fee & $5 Bonus credited"
                ),
                TransactionEntity(
                    transactionId = "TXN-BAIZ-8902",
                    title = "Referral Bonus - Alex Turner",
                    type = "REFERRAL_BONUS",
                    amount = 2.00,
                    status = "COMPLETED",
                    paymentMethod = "BAIZPAY_WALLET",
                    timestamp = System.currentTimeMillis() - 86400000 * 4,
                    note = "Qualified referral reward"
                ),
                TransactionEntity(
                    transactionId = "TXN-BAIZ-8903",
                    title = "Micro Task: App Review & Download",
                    type = "TASK_REWARD",
                    amount = 1.50,
                    status = "COMPLETED",
                    paymentMethod = "BAIZPAY_WALLET",
                    timestamp = System.currentTimeMillis() - 86400000 * 3,
                    note = "Task verified by system"
                ),
                TransactionEntity(
                    transactionId = "TXN-BAIZ-8904",
                    title = "Marketplace Sale - UI Kit Template",
                    type = "MARKETPLACE_SALE",
                    amount = 15.00,
                    status = "COMPLETED",
                    paymentMethod = "BAIZPAY_WALLET",
                    timestamp = System.currentTimeMillis() - 86400000 * 2,
                    note = "Digital product commission payout"
                ),
                TransactionEntity(
                    transactionId = "TXN-BAIZ-8905",
                    title = "Withdrawal to PayPal",
                    type = "WITHDRAWAL",
                    amount = 20.00,
                    status = "COMPLETED",
                    paymentMethod = "PAYPAL",
                    timestamp = System.currentTimeMillis() - 86400000,
                    note = "Payout sent to baizid2537@gmail.com"
                )
            )
            initialTxns.forEach { dao.insertTransaction(it) }

            // Seed Tasks
            val initialTasks = listOf(
                TaskEntity(
                    title = "Daily Check-In Streak Bonus",
                    category = "DAILY_CHECKIN",
                    description = "Claim your daily login reward. Complete 7 consecutive days for 2x multiplier!",
                    rewardAmount = 0.50,
                    durationSeconds = 2,
                    isCompleted = false,
                    completionStatus = "AVAILABLE"
                ),
                TaskEntity(
                    title = "Lucky Wheel Spin",
                    category = "SPIN_WHEEL",
                    description = "Spin the wheel to win instant cash rewards between $0.20 and $5.00!",
                    rewardAmount = 1.00,
                    durationSeconds = 5,
                    isCompleted = false,
                    completionStatus = "AVAILABLE"
                ),
                TaskEntity(
                    title = "Gold Scratch Card",
                    category = "SCRATCH_CARD",
                    description = "Scratch 3 matching coins to reveal instant wallet prize up to $3.00!",
                    rewardAmount = 0.75,
                    durationSeconds = 5,
                    isCompleted = false,
                    completionStatus = "AVAILABLE"
                ),
                TaskEntity(
                    title = "Watch Promotional Video Ad (30s)",
                    category = "WATCH_VIDEO",
                    description = "Watch high-converting fintech ad video to completion to claim $0.25 reward.",
                    rewardAmount = 0.25,
                    durationSeconds = 15,
                    isCompleted = false,
                    completionStatus = "AVAILABLE"
                ),
                TaskEntity(
                    title = "Global Consumer Finance Survey",
                    category = "SURVEY",
                    description = "Answer 5 quick questions about digital wallet usage in your region.",
                    rewardAmount = 1.20,
                    durationSeconds = 20,
                    isCompleted = false,
                    completionStatus = "AVAILABLE"
                ),
                TaskEntity(
                    title = "Download & Test NeoBank App",
                    category = "APP_DOWNLOAD",
                    description = "Install the verified partner app and open it for 30 seconds to earn.",
                    rewardAmount = 2.50,
                    durationSeconds = 10,
                    isCompleted = false,
                    completionStatus = "AVAILABLE"
                ),
                TaskEntity(
                    title = "Read Article: Crypto & Digital Currencies",
                    category = "ARTICLE",
                    description = "Read 2 minute article on decentralized finance and complete 1 question quiz.",
                    rewardAmount = 0.40,
                    durationSeconds = 12,
                    isCompleted = false,
                    completionStatus = "AVAILABLE"
                )
            )
            dao.insertTasks(initialTasks)

            // Seed 5-Level Referral Network
            val initialReferrals = listOf(
                // Level 1: Direct Referrals ($2.00)
                ReferralEntity(referredName = "Alex Turner", referredEmail = "alex.t@global.org", dateJoined = System.currentTimeMillis() - 86400000 * 6, status = "QUALIFIED", rewardAmount = 2.00, level = 1),
                ReferralEntity(referredName = "Sophia Chen", referredEmail = "sophia.chen@tech.io", dateJoined = System.currentTimeMillis() - 86400000 * 5, status = "QUALIFIED", rewardAmount = 2.00, level = 1),
                ReferralEntity(referredName = "Tariq Hassan", referredEmail = "tariq.h@fintech.net", dateJoined = System.currentTimeMillis() - 86400000 * 4, status = "QUALIFIED", rewardAmount = 2.00, level = 1),
                ReferralEntity(referredName = "Elena Rostova", referredEmail = "elena.r@design.co", dateJoined = System.currentTimeMillis() - 86400000 * 3, status = "QUALIFIED", rewardAmount = 2.00, level = 1),
                ReferralEntity(referredName = "Maya Lin", referredEmail = "maya.l@venture.org", dateJoined = System.currentTimeMillis() - 86400000 * 1, status = "PENDING_VERIFICATION", rewardAmount = 2.00, level = 1),

                // Level 2 Downlines ($0.50)
                ReferralEntity(referredName = "Marcus Vance", referredEmail = "marcus.v@invest.com", dateJoined = System.currentTimeMillis() - 86400000 * 5, status = "QUALIFIED", rewardAmount = 0.50, level = 2),
                ReferralEntity(referredName = "Liam Wright", referredEmail = "liam.w@code.io", dateJoined = System.currentTimeMillis() - 86400000 * 4, status = "QUALIFIED", rewardAmount = 0.50, level = 2),
                ReferralEntity(referredName = "Noah Becker", referredEmail = "noah.b@startup.de", dateJoined = System.currentTimeMillis() - 86400000 * 3, status = "QUALIFIED", rewardAmount = 0.50, level = 2),

                // Level 3 Downlines ($0.25)
                ReferralEntity(referredName = "Olivia Rodrigo", referredEmail = "olivia.r@artist.com", dateJoined = System.currentTimeMillis() - 86400000 * 4, status = "QUALIFIED", rewardAmount = 0.25, level = 3),
                ReferralEntity(referredName = "James Watson", referredEmail = "james.w@data.org", dateJoined = System.currentTimeMillis() - 86400000 * 2, status = "QUALIFIED", rewardAmount = 0.25, level = 3),

                // Level 4 Downlines ($0.15)
                ReferralEntity(referredName = "Chloe Bennett", referredEmail = "chloe.b@media.co", dateJoined = System.currentTimeMillis() - 86400000 * 3, status = "QUALIFIED", rewardAmount = 0.15, level = 4),
                ReferralEntity(referredName = "Emma Watson", referredEmail = "emma.w@films.io", dateJoined = System.currentTimeMillis() - 86400000 * 2, status = "QUALIFIED", rewardAmount = 0.15, level = 4),

                // Level 5 Downlines ($0.10)
                ReferralEntity(referredName = "Daniel Craig", referredEmail = "daniel.c@agents.org", dateJoined = System.currentTimeMillis() - 86400000 * 2, status = "QUALIFIED", rewardAmount = 0.10, level = 5),
                ReferralEntity(referredName = "Gabriel Silva", referredEmail = "gabriel.s@br.com", dateJoined = System.currentTimeMillis() - 86400000 * 1, status = "QUALIFIED", rewardAmount = 0.10, level = 5)
            )
            dao.insertReferrals(initialReferrals)

            // Seed Marketplace Products
            val initialProducts = listOf(
                ProductEntity(
                    title = "SaaS Mobile UI Kit (Figma & Kotlin)",
                    category = "DIGITAL",
                    price = 14.99,
                    sellerName = "Baizid Ahmed",
                    rating = 4.9f,
                    reviewCount = 38,
                    stockCount = 999,
                    description = "Complete fintech UI kit containing 45+ screens, light & dark theme.",
                    isFeatured = true
                ),
                ProductEntity(
                    title = "$25 Amazon Global Gift Card Code",
                    category = "GIFTCARD",
                    price = 24.50,
                    sellerName = "BaizPay Official Store",
                    rating = 5.0f,
                    reviewCount = 142,
                    stockCount = 85,
                    description = "Instant email delivery of Amazon digital voucher code.",
                    isFeatured = true
                ),
                ProductEntity(
                    title = "Premium Spotify 1-Year Membership Key",
                    category = "SUBSCRIPTION",
                    price = 39.00,
                    sellerName = "DigitalVoucher Hub",
                    rating = 4.8f,
                    reviewCount = 64,
                    stockCount = 12,
                    description = "12 Months Individual Spotify Premium activation code.",
                    isFeatured = false
                ),
                ProductEntity(
                    title = "Hardware Crypto Wallet Cold Storage",
                    category = "ELECTRONICS",
                    price = 89.00,
                    sellerName = "SecureTech Global",
                    rating = 4.9f,
                    reviewCount = 27,
                    stockCount = 15,
                    description = "Military grade offline hardware wallet with Bluetooth encryption.",
                    isFeatured = true
                )
            )
            dao.insertProducts(initialProducts)

            // Seed Initial Notifications
            val initialNotifs = listOf(
                NotificationEntity(
                    title = "Welcome to BaizPay!",
                    message = "Your account is active. Explore tasks, referrals, and marketplace earnings.",
                    type = "SYSTEM",
                    timestamp = System.currentTimeMillis() - 86400000 * 5,
                    isRead = true
                ),
                NotificationEntity(
                    title = "Account Verified ($5 USD)",
                    message = "Congratulations! Your account verification was successful. $5 bonus unlocked.",
                    type = "VERIFICATION",
                    timestamp = System.currentTimeMillis() - 86400000 * 5,
                    isRead = true
                ),
                NotificationEntity(
                    title = "New Referral Bonus +$2.00",
                    message = "Alex Turner completed account verification. $2.00 added to your wallet.",
                    type = "REFERRAL",
                    timestamp = System.currentTimeMillis() - 86400000 * 4,
                    isRead = false
                )
            )
            initialNotifs.forEach { dao.insertNotification(it) }
        }
    }

    suspend fun updateUser(user: UserEntity) {
        dao.insertOrUpdateUser(user)
    }

    suspend fun verifyAccount(paymentMethod: String) {
        val user = dao.getUserProfileSync() ?: return
        val updatedUser = user.copy(
            isVerified = true,
            verificationDate = System.currentTimeMillis(),
            walletBalance = user.walletBalance + 5.00, // $5 USD welcome bonus upon verification!
            availableBalance = user.availableBalance + 5.00
        )
        dao.insertOrUpdateUser(updatedUser)

        val txn = TransactionEntity(
            transactionId = "TXN-" + UUID.randomUUID().toString().take(8).uppercase(),
            title = "Account $5 USD Verification Fee & $5 Bonus",
            type = "DEPOSIT",
            amount = 5.00,
            status = "COMPLETED",
            paymentMethod = paymentMethod,
            timestamp = System.currentTimeMillis(),
            note = "Verified Account via $paymentMethod. Earn features unlocked!"
        )
        dao.insertTransaction(txn)

        dao.insertNotification(
            NotificationEntity(
                title = "Account Verification Successful!",
                message = "Your $5 USD verification was processed via $paymentMethod. Full access unlocked!",
                type = "VERIFICATION"
            )
        )
    }

    suspend fun addDeposit(amount: Double, paymentMethod: String) {
        val user = dao.getUserProfileSync() ?: return
        val updatedUser = user.copy(
            walletBalance = user.walletBalance + amount,
            availableBalance = user.availableBalance + amount
        )
        dao.insertOrUpdateUser(updatedUser)

        val txn = TransactionEntity(
            transactionId = "TXN-" + UUID.randomUUID().toString().take(8).uppercase(),
            title = "Wallet Deposit via $paymentMethod",
            type = "DEPOSIT",
            amount = amount,
            status = "COMPLETED",
            paymentMethod = paymentMethod,
            timestamp = System.currentTimeMillis(),
            note = "Direct wallet deposit"
        )
        dao.insertTransaction(txn)

        dao.insertNotification(
            NotificationEntity(
                title = "Deposit Received +$$amount",
                message = "Successfully deposited $$amount via $paymentMethod into your BaizPay wallet.",
                type = "EARNING"
            )
        )
    }

    suspend fun requestWithdrawal(amount: Double, method: String, payoutAccount: String): Boolean {
        val user = dao.getUserProfileSync() ?: return false
        if (user.availableBalance < amount) return false

        val updatedUser = user.copy(
            walletBalance = user.walletBalance - amount,
            availableBalance = user.availableBalance - amount,
            pendingBalance = user.pendingBalance + amount
        )
        dao.insertOrUpdateUser(updatedUser)

        val txn = TransactionEntity(
            transactionId = "WD-" + UUID.randomUUID().toString().take(8).uppercase(),
            title = "Withdrawal Request to $method",
            type = "WITHDRAWAL",
            amount = amount,
            status = "PENDING",
            paymentMethod = method,
            timestamp = System.currentTimeMillis(),
            note = "Account: $payoutAccount"
        )
        dao.insertTransaction(txn)

        dao.insertNotification(
            NotificationEntity(
                title = "Withdrawal Request Submitted",
                message = "Your withdrawal request of $$amount to $method ($payoutAccount) is currently pending admin review.",
                type = "EARNING"
            )
        )
        return true
    }

    suspend fun completeTask(task: TaskEntity) {
        val user = dao.getUserProfileSync() ?: return
        val updatedTask = task.copy(isCompleted = true, completionStatus = "COMPLETED")
        dao.updateTask(updatedTask)

        val updatedUser = user.copy(
            walletBalance = user.walletBalance + task.rewardAmount,
            availableBalance = user.availableBalance + task.rewardAmount,
            taskEarnings = user.taskEarnings + task.rewardAmount
        )
        dao.insertOrUpdateUser(updatedUser)

        val txn = TransactionEntity(
            transactionId = "TASK-" + UUID.randomUUID().toString().take(8).uppercase(),
            title = "Task Reward: ${task.title}",
            type = "TASK_REWARD",
            amount = task.rewardAmount,
            status = "COMPLETED",
            paymentMethod = "BAIZPAY_WALLET",
            timestamp = System.currentTimeMillis(),
            note = "Micro task reward"
        )
        dao.insertTransaction(txn)

        dao.insertNotification(
            NotificationEntity(
                title = "Task Reward Claimed +$${task.rewardAmount}",
                message = "You earned $${task.rewardAmount} for completing '${task.title}'.",
                type = "EARNING"
            )
        )
    }

    private fun calculateSalaryRank(directReferrals: Int): String {
        return when {
            directReferrals >= 10000 -> "Global Ambassador"
            directReferrals >= 5000 -> "Legend"
            directReferrals >= 2500 -> "Royal"
            directReferrals >= 1000 -> "Crown"
            directReferrals >= 500 -> "Elite"
            directReferrals >= 250 -> "Diamond"
            directReferrals >= 100 -> "Platinum"
            directReferrals >= 50 -> "Gold"
            directReferrals >= 25 -> "Silver"
            directReferrals >= 10 -> "Bronze"
            else -> "Member"
        }
    }

    suspend fun addReferral(name: String, email: String, level: Int = 1) {
        val reward = when (level) {
            1 -> 2.00
            2 -> 0.50
            3 -> 0.25
            4 -> 0.15
            5 -> 0.10
            else -> 0.00
        }

        val referral = ReferralEntity(
            referredName = name,
            referredEmail = email,
            dateJoined = System.currentTimeMillis(),
            status = "QUALIFIED",
            rewardAmount = reward,
            level = level
        )
        dao.insertReferral(referral)

        val user = dao.getUserProfileSync() ?: return
        val newMonthlyDirects = if (level == 1) user.monthlyDirectReferrals + 1 else user.monthlyDirectReferrals
        val updatedUser = user.copy(
            walletBalance = user.walletBalance + reward,
            availableBalance = user.availableBalance + reward,
            referralEarnings = user.referralEarnings + reward,
            totalReferrals = user.totalReferrals + 1,
            activeReferrals = user.activeReferrals + 1,
            monthlyDirectReferrals = newMonthlyDirects,
            salaryRank = calculateSalaryRank(newMonthlyDirects)
        )
        dao.insertOrUpdateUser(updatedUser)

        val levelTag = if (level == 1) "Level 1 (Direct $2.00)" else "Level $level ($" + String.format("%.2f", reward) + ")"
        val txn = TransactionEntity(
            transactionId = "REF-" + UUID.randomUUID().toString().take(8).uppercase(),
            title = "Referral Commission - $name ($levelTag)",
            type = "REFERRAL_BONUS",
            amount = reward,
            status = "COMPLETED",
            paymentMethod = "BAIZPAY_WALLET",
            timestamp = System.currentTimeMillis(),
            note = "$$reward USD 5-level commission credited"
        )
        dao.insertTransaction(txn)

        dao.insertNotification(
            NotificationEntity(
                title = "Referral Commission +$$reward",
                message = "$name joined your network at $levelTag. $$reward added to your wallet!",
                type = "REFERRAL"
            )
        )
    }

    suspend fun claimMonthlySalary(amount: Double, rankName: String) {
        val user = dao.getUserProfileSync() ?: return
        val updatedUser = user.copy(
            walletBalance = user.walletBalance + amount,
            availableBalance = user.availableBalance + amount,
            monthlySalaryEarnings = user.monthlySalaryEarnings + amount
        )
        dao.insertOrUpdateUser(updatedUser)

        val txn = TransactionEntity(
            transactionId = "SALARY-" + UUID.randomUUID().toString().take(8).uppercase(),
            title = "Monthly Performance Salary ($rankName)",
            type = "MONTHLY_SALARY",
            amount = amount,
            status = "COMPLETED",
            paymentMethod = "BAIZPAY_WALLET",
            timestamp = System.currentTimeMillis(),
            note = "Achieved $rankName Salary Rank for direct performance"
        )
        dao.insertTransaction(txn)

        dao.insertNotification(
            NotificationEntity(
                title = "Monthly Salary Credited +$$amount!",
                message = "Congratulations! Your $rankName Rank monthly salary payout of $$amount has been added to your balance.",
                type = "EARNING"
            )
        )
    }

    suspend fun purchaseProduct(product: ProductEntity): Boolean {
        val user = dao.getUserProfileSync() ?: return false
        if (user.availableBalance < product.price) return false

        val updatedUser = user.copy(
            walletBalance = user.walletBalance - product.price,
            availableBalance = user.availableBalance - product.price
        )
        dao.insertOrUpdateUser(updatedUser)

        val txn = TransactionEntity(
            transactionId = "SHOP-" + UUID.randomUUID().toString().take(8).uppercase(),
            title = "Purchased: ${product.title}",
            type = "MARKETPLACE_PURCHASE",
            amount = product.price,
            status = "COMPLETED",
            paymentMethod = "BAIZPAY_WALLET",
            timestamp = System.currentTimeMillis(),
            note = "Marketplace purchase from seller ${product.sellerName}"
        )
        dao.insertTransaction(txn)

        dao.insertNotification(
            NotificationEntity(
                title = "Order Confirmed!",
                message = "Purchased '${product.title}' for $${product.price} using BaizPay wallet.",
                type = "MARKETPLACE"
            )
        )
        return true
    }

    suspend fun addProduct(product: ProductEntity) {
        dao.insertProduct(product)
        dao.insertNotification(
            NotificationEntity(
                title = "Product Listed",
                message = "'${product.title}' is now live on the BaizPay Marketplace.",
                type = "MARKETPLACE"
            )
        )
    }

    suspend fun approveWithdrawal(transactionId: String) {
        val user = dao.getUserProfileSync() ?: return
        val updatedUser = user.copy(
            pendingBalance = (user.pendingBalance - 20.0).coerceAtLeast(0.0)
        )
        dao.insertOrUpdateUser(updatedUser)
        dao.insertNotification(
            NotificationEntity(
                title = "Withdrawal Approved!",
                message = "Transaction $transactionId was processed and approved by Admin.",
                type = "EARNING"
            )
        )
    }
}
