package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.GroupAdd
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entities.ReferralEntity
import com.example.data.local.entities.UserEntity

data class SalaryTierInfo(
    val rankName: String,
    val requiredDirects: Int,
    val monthlySalaryUsd: Double,
    val accentColor: Color,
    val isAmbassador: Boolean = false
)

val SALARY_TIERS_LIST = listOf(
    SalaryTierInfo("Bronze", 10, 10.0, Color(0xFFCD7F32)),
    SalaryTierInfo("Silver", 25, 30.0, Color(0xFFC0C0C0)),
    SalaryTierInfo("Gold", 50, 75.0, Color(0xFFFFD700)),
    SalaryTierInfo("Platinum", 100, 200.0, Color(0xFFE2E8F0)),
    SalaryTierInfo("Diamond", 250, 600.0, Color(0xFF38BDF8)),
    SalaryTierInfo("Elite", 500, 1500.0, Color(0xFFA855F7)),
    SalaryTierInfo("Crown", 1000, 3500.0, Color(0xFFEC4899)),
    SalaryTierInfo("Royal", 2500, 10000.0, Color(0xFFEF4444)),
    SalaryTierInfo("Legend", 5000, 25000.0, Color(0xFF10B981)),
    SalaryTierInfo("Global Ambassador", 10000, 0.0, Color(0xFF3B82F6), isAmbassador = true)
)

@Composable
fun ReferralScreen(
    userProfile: UserEntity?,
    referrals: List<ReferralEntity>,
    onAddDemoReferral: (name: String, email: String, level: Int) -> Unit,
    onClaimMonthlySalary: (amount: Double, rankName: String) -> Unit
) {
    var selectedTab by remember { mutableStateOf(0) } // 0 = 5-Level Tree, 1 = Monthly Salary, 2 = Badges & History
    var selectedLevelFilter by remember { mutableStateOf(0) } // 0 = All, 1..5 = Level 1..5
    var showAddReferralModal by remember { mutableStateOf(false) }
    var copiedNotice by remember { mutableStateOf(false) }
    var salaryClaimedSuccessNotice by remember { mutableStateOf(false) }

    val referralCode = userProfile?.referralCode ?: "BAIZ2026"
    val referralLink = "https://baizpay.com/ref/$referralCode"
    val monthlyDirects = userProfile?.monthlyDirectReferrals ?: 28
    val currentSalaryRank = userProfile?.salaryRank ?: "Silver"
    val monthlySalaryEarnings = userProfile?.monthlySalaryEarnings ?: 30.0

    // Find current tier & next tier
    val currentTier = SALARY_TIERS_LIST.find { it.rankName.equals(currentSalaryRank, ignoreCase = true) }
        ?: SALARY_TIERS_LIST[1]
    val nextTierIndex = (SALARY_TIERS_LIST.indexOf(currentTier) + 1).coerceAtMost(SALARY_TIERS_LIST.size - 1)
    val nextTier = SALARY_TIERS_LIST[nextTierIndex]

    val filteredReferrals = remember(referrals, selectedLevelFilter) {
        if (selectedLevelFilter == 0) referrals
        else referrals.filter { it.level == selectedLevelFilter }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item { Spacer(modifier = Modifier.height(8.dp)) }

        // Top Banner - 5-Level Referral & Monthly Salary Program
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.linearGradient(
                                colors = listOf(Color(0xFFD97706), Color(0xFF7C3AED), Color(0xFF0F172A))
                            )
                        )
                        .padding(20.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(44.dp)
                                        .clip(CircleShape)
                                        .background(Color.White.copy(alpha = 0.2f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.WorkspacePremium, null, tint = Color(0xFFFBBF24), modifier = Modifier.size(26.dp))
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "5-Level Referral & Monthly Salary",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                    Text(
                                        text = "Current Rank: ${currentTier.rankName} ($${if (currentTier.isAmbassador) "Custom" else String.format("%.0f", currentTier.monthlySalaryUsd)}/mo)",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color(0xFFFBBF24)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Stats Grid
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            HeaderStatBox("Direct Referrals", "$monthlyDirects This Month")
                            HeaderStatBox("Total Earnings", "$" + String.format("%.2f", userProfile?.referralEarnings ?: 0.0))
                            HeaderStatBox("Salary Claimed", "$" + String.format("%.0f", monthlySalaryEarnings))
                        }
                    }
                }
            }
        }

        // Navigation Tabs (5-Level Network | Monthly Salary | Badges & History)
        item {
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = Color(0xFF2563EB)
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("5-Level Network", fontWeight = FontWeight.Bold, fontSize = 12.sp) }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("Monthly Salary", fontWeight = FontWeight.Bold, fontSize = 12.sp) }
                )
                Tab(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    text = { Text("Badges & Ranks", fontWeight = FontWeight.Bold, fontSize = 12.sp) }
                )
            }
        }

        // TAB 0: 5-LEVEL REFERRAL NETWORK
        if (selectedTab == 0) {
            // Level Commission Rates Banner
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "5-Level Referral Commission Structure",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            LevelRateBadge("Level 1", "$2.00", Color(0xFF10B981))
                            LevelRateBadge("Level 2", "$0.50", Color(0xFF3B82F6))
                            LevelRateBadge("Level 3", "$0.25", Color(0xFF8B5CF6))
                            LevelRateBadge("Level 4", "$0.15", Color(0xFFEC4899))
                            LevelRateBadge("Level 5", "$0.10", Color(0xFFF59E0B))
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "• Commissions are paid automatically up to 5 levels deep. No referral commissions paid beyond Level 5.",
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Share Referral Link & Code
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "Your Level 1 Invite Code & Link",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(6.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(Color(0xFF2563EB).copy(alpha = 0.15f))
                                    .padding(10.dp)
                            ) {
                                Text(
                                    text = referralCode,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color(0xFF2563EB)
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(
                                onClick = { copiedNotice = true },
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB)),
                                modifier = Modifier.testTag("copy_ref_link_button")
                            ) {
                                Icon(Icons.Default.ContentCopy, null, tint = Color.White, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Copy", fontSize = 12.sp)
                            }
                        }

                        if (copiedNotice) {
                            Text(
                                text = "Copied link to clipboard!",
                                fontSize = 11.sp,
                                color = Color(0xFF10B981),
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }
                }
            }

            // Add Demo Referral Trigger
            item {
                Button(
                    onClick = { showAddReferralModal = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(46.dp)
                        .testTag("add_network_referral_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981))
                ) {
                    Icon(Icons.Default.PersonAdd, null, tint = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Simulate New Network Downline (Level 1 - 5)", fontWeight = FontWeight.Bold)
                }
            }

            // Level Filter Pills
            item {
                Column {
                    Text(
                        text = "Filter Network by Level:",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        item {
                            FilterChip(
                                selected = selectedLevelFilter == 0,
                                onClick = { selectedLevelFilter = 0 },
                                label = { Text("All Levels (${referrals.size})") },
                                colors = FilterChipDefaults.filterChipColors(selectedContainerColor = Color(0xFF2563EB), selectedLabelColor = Color.White)
                            )
                        }
                        (1..5).forEach { lvl ->
                            val count = referrals.count { it.level == lvl }
                            item {
                                FilterChip(
                                    selected = selectedLevelFilter == lvl,
                                    onClick = { selectedLevelFilter = lvl },
                                    label = { Text("Level $lvl ($count)") },
                                    colors = FilterChipDefaults.filterChipColors(selectedContainerColor = getLevelColor(lvl), selectedLabelColor = Color.White)
                                )
                            }
                        }
                    }
                }
            }

            // Referral Network Items List
            items(filteredReferrals) { ref ->
                ReferralNetworkCard(ref)
            }
        }

        // TAB 1: MONTHLY PERFORMANCE SALARY PROGRAM
        if (selectedTab == 1) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Monthly Performance Salary",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "Based on qualified Direct Referrals in calendar month",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(currentTier.accentColor.copy(alpha = 0.2f))
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = "${currentTier.rankName} Rank",
                                    fontWeight = FontWeight.Bold,
                                    color = currentTier.accentColor,
                                    fontSize = 12.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Progress Bar to Next Target
                        val currentTarget = nextTier.requiredDirects
                        val progress = (monthlyDirects.toFloat() / currentTarget.toFloat()).coerceIn(0f, 1f)

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Current: $monthlyDirects Direct Referrals",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "Next: ${nextTier.rankName} (${nextTier.requiredDirects} Directs)",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF2563EB)
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        LinearProgressIndicator(
                            progress = { progress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(10.dp)
                                .clip(RoundedCornerShape(5.dp)),
                            color = currentTier.accentColor,
                            trackColor = MaterialTheme.colorScheme.outlineVariant
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        val remaining = (nextTier.requiredDirects - monthlyDirects).coerceAtLeast(0)
                        Text(
                            text = if (remaining == 0) "Top Rank Target Met! Maximum salary unlocked."
                            else "Complete $remaining more direct referrals to unlock ${nextTier.rankName} Rank ($${String.format("%.0f", nextTier.monthlySalaryUsd)}/mo)!",
                            fontSize = 11.sp,
                            color = Color(0xFF10B981),
                            fontWeight = FontWeight.SemiBold
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Claim Salary Action Button
                        val claimableAmount = currentTier.monthlySalaryUsd
                        Button(
                            onClick = {
                                if (claimableAmount > 0) {
                                    onClaimMonthlySalary(claimableAmount, currentTier.rankName)
                                    salaryClaimedSuccessNotice = true
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("claim_monthly_salary_button"),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = currentTier.accentColor)
                        ) {
                            Icon(Icons.Default.MonetizationOn, null, tint = Color.Black)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Claim ${currentTier.rankName} Salary ($${String.format("%.0f", claimableAmount)})",
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )
                        }

                        if (salaryClaimedSuccessNotice) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Salary of $${String.format("%.0f", claimableAmount)} credited to your BaizPay wallet!",
                                fontSize = 12.sp,
                                color = Color(0xFF10B981),
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }

            item {
                Text(
                    text = "Full Monthly Salary Structure & Ranks",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                )
            }

            // List of All 10 Salary Tiers
            items(SALARY_TIERS_LIST) { tier ->
                val isCurrent = tier.rankName.equals(currentSalaryRank, ignoreCase = true)
                val isUnlocked = monthlyDirects >= tier.requiredDirects

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isCurrent) tier.accentColor.copy(alpha = 0.15f)
                        else MaterialTheme.colorScheme.surface
                    ),
                    border = if (isCurrent) androidx.compose.foundation.BorderStroke(2.dp, tier.accentColor) else null
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(tier.accentColor.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (isUnlocked) Icons.Default.CheckCircle else Icons.Default.Lock,
                                    contentDescription = null,
                                    tint = tier.accentColor,
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = tier.rankName,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    if (isCurrent) {
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(6.dp))
                                                .background(tier.accentColor)
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Text("YOUR RANK", fontSize = 9.sp, fontWeight = FontWeight.Black, color = Color.Black)
                                        }
                                    }
                                }
                                Text(
                                    text = "${tier.requiredDirects}+ Qualified Direct Referrals",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Text(
                            text = if (tier.isAmbassador) "Custom Reward" else "$" + String.format("%.0f", tier.monthlySalaryUsd) + "/mo",
                            fontWeight = FontWeight.Black,
                            fontSize = 15.sp,
                            color = if (isUnlocked) Color(0xFF10B981) else Color(0xFF94A3B8)
                        )
                    }
                }
            }
        }

        // TAB 2: BADGES & SALARY HISTORY
        if (selectedTab == 2) {
            item {
                Text(
                    text = "Achievement Badges & Milestones",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                )
            }

            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    SALARY_TIERS_LIST.chunked(2).forEach { pair ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            pair.forEach { tier ->
                                val isUnlocked = monthlyDirects >= tier.requiredDirects
                                Card(
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(16.dp),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                                ) {
                                    Column(
                                        modifier = Modifier.padding(14.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(50.dp)
                                                .clip(CircleShape)
                                                .background(
                                                    if (isUnlocked) tier.accentColor.copy(alpha = 0.25f)
                                                    else Color(0xFF334155)
                                                ),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = if (isUnlocked) Icons.Default.EmojiEvents else Icons.Default.Lock,
                                                contentDescription = null,
                                                tint = if (isUnlocked) tier.accentColor else Color(0xFF64748B),
                                                modifier = Modifier.size(28.dp)
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(8.dp))
                                        Text(
                                            text = tier.rankName,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        Text(
                                            text = if (isUnlocked) "UNLOCKED" else "${tier.requiredDirects} Directs Needed",
                                            fontSize = 10.sp,
                                            color = if (isUnlocked) Color(0xFF10B981) else Color(0xFF94A3B8),
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "Salary & Network Commission Log",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                )
            }

            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        HistoryRow("Monthly Salary Payout - Silver Rank", "$30.00", "2026-08-01")
                        HistoryRow("Level 1 Direct Referral Bonus - Alex Turner", "$2.00", "2026-08-02")
                        HistoryRow("Level 2 Downline Commission - Marcus Vance", "$0.50", "2026-08-03")
                        HistoryRow("Level 3 Downline Commission - Olivia Rodrigo", "$0.25", "2026-08-03")
                        HistoryRow("Level 4 Downline Commission - Chloe Bennett", "$0.15", "2026-08-04")
                        HistoryRow("Level 5 Downline Commission - Daniel Craig", "$0.10", "2026-08-04")
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(24.dp)) }
    }

    // Modal to add a simulated downline across levels 1 to 5
    if (showAddReferralModal) {
        var refName by remember { mutableStateOf("David Miller") }
        var refEmail by remember { mutableStateOf("david.m@global.org") }
        var selectedLevel by remember { mutableStateOf(1) }

        AlertDialog(
            onDismissRequest = { showAddReferralModal = false },
            title = { Text("Add Network Downline Referral", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    OutlinedTextField(
                        value = refName,
                        onValueChange = { refName = it },
                        label = { Text("Referral Full Name") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("network_ref_name_input")
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = refEmail,
                        onValueChange = { refEmail = it },
                        label = { Text("Referral Email") },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("network_ref_email_input")
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Text("Select Referral Level:", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(4.dp))

                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        (1..5).forEach { lvl ->
                            val rateStr = when (lvl) {
                                1 -> "$2.00 (Direct)"
                                2 -> "$0.50"
                                3 -> "$0.25"
                                4 -> "$0.15"
                                5 -> "$0.10"
                                else -> "$0.00"
                            }
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { selectedLevel = lvl }
                            ) {
                                RadioButton(
                                    selected = selectedLevel == lvl,
                                    onClick = { selectedLevel = lvl },
                                    colors = RadioButtonDefaults.colors(selectedColor = Color(0xFF2563EB))
                                )
                                Text(
                                    text = "Level $lvl ($rateStr)",
                                    fontSize = 12.sp,
                                    fontWeight = if (selectedLevel == lvl) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onAddDemoReferral(refName, refEmail, selectedLevel)
                        showAddReferralModal = false
                    },
                    modifier = Modifier.testTag("confirm_add_network_ref_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981))
                ) {
                    Text("Add & Credit Commission")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddReferralModal = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun HeaderStatBox(title: String, value: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White.copy(alpha = 0.15f))
            .padding(horizontal = 10.dp, vertical = 8.dp)
    ) {
        Column {
            Text(text = title, fontSize = 10.sp, color = Color.White.copy(alpha = 0.8f))
            Text(text = value, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
        }
    }
}

@Composable
private fun LevelRateBadge(levelLabel: String, rate: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(color.copy(alpha = 0.2f))
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Text(text = levelLabel, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = color)
        }
        Spacer(modifier = Modifier.height(2.dp))
        Text(text = rate, fontSize = 12.sp, fontWeight = FontWeight.Black, color = MaterialTheme.colorScheme.onSurface)
    }
}

@Composable
private fun ReferralNetworkCard(ref: ReferralEntity) {
    val levelColor = getLevelColor(ref.level)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(levelColor.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "L${ref.level}",
                        fontWeight = FontWeight.Bold,
                        color = levelColor,
                        fontSize = 13.sp
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = ref.referredName,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(levelColor.copy(alpha = 0.2f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = if (ref.level == 1) "DIRECT" else "LVL ${ref.level}",
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Bold,
                                color = levelColor
                            )
                        }
                    }
                    Text(
                        text = ref.referredEmail,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(
                        if (ref.status == "QUALIFIED") Color(0xFF10B981).copy(alpha = 0.2f)
                        else Color(0xFFF59E0B).copy(alpha = 0.2f)
                    )
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = if (ref.status == "QUALIFIED") "+ $" + String.format("%.2f", ref.rewardAmount) else "Pending",
                    color = if (ref.status == "QUALIFIED") Color(0xFF10B981) else Color(0xFFF59E0B),
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp
                )
            }
        }
    }
}

@Composable
private fun HistoryRow(title: String, amount: String, date: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(text = title, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
            Text(text = date, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Text(text = "+ $amount", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color(0xFF10B981))
    }
}

private fun getLevelColor(level: Int): Color {
    return when (level) {
        1 -> Color(0xFF10B981) // Level 1 (Direct)
        2 -> Color(0xFF3B82F6) // Level 2
        3 -> Color(0xFF8B5CF6) // Level 3
        4 -> Color(0xFFEC4899) // Level 4
        5 -> Color(0xFFF59E0B) // Level 5
        else -> Color(0xFF64748B)
    }
}
