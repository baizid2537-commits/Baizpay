package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.PlayCircleFilled
import androidx.compose.material.icons.filled.Poll
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.TaskAlt
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.example.data.local.entities.TaskEntity
import kotlinx.coroutines.delay

@Composable
fun MicroTasksScreen(
    tasks: List<TaskEntity>,
    onCompleteTask: (TaskEntity) -> Unit
) {
    var activeTaskModal by remember { mutableStateOf<TaskEntity?>(null) }
    var taskProgress by remember { mutableStateOf(0f) }
    var taskCountdown by remember { mutableStateOf(0) }
    var isTaskRunning by remember { mutableStateOf(false) }
    var isTaskDone by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item { Spacer(modifier = Modifier.height(8.dp)) }

        // Header Banner
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
                            Brush.horizontalGradient(
                                colors = listOf(Color(0xFF2563EB), Color(0xFF0F172A))
                            )
                        )
                        .padding(20.dp)
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.TaskAlt, null, tint = Color(0xFFFBBF24), modifier = Modifier.size(24.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Micro Task Marketplace",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "Earn verified USD rewards by completing quick daily check-ins, promotional videos, surveys, wheel spins & scratch cards.",
                            fontSize = 12.sp,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }
                }
            }
        }

        // Available Tasks Section Header
        item {
            Text(
                text = "Available Reward Tasks",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            )
        }

        items(tasks) { task ->
            TaskCardItem(
                task = task,
                onStart = {
                    activeTaskModal = task
                    taskProgress = 0f
                    taskCountdown = task.durationSeconds
                    isTaskRunning = false
                    isTaskDone = false
                }
            )
        }

        item { Spacer(modifier = Modifier.height(20.dp)) }
    }

    // Task Execution Modal with Interactive Countdown / Mini Game simulation
    activeTaskModal?.let { task ->
        AlertDialog(
            onDismissRequest = {
                if (!isTaskRunning) activeTaskModal = null
            },
            title = {
                Text(
                    text = task.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            },
            text = {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = task.description,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF10B981).copy(alpha = 0.2f))
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = "Reward: USD $" + String.format("%.2f", task.rewardAmount),
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF10B981),
                            fontSize = 16.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    if (isTaskRunning) {
                        Text(
                            text = "Processing Task... $taskCountdown s remaining",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2563EB)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        LinearProgressIndicator(
                            progress = { taskProgress },
                            modifier = Modifier.fillMaxWidth().height(8.dp).clip(CircleShape),
                            color = Color(0xFF2563EB)
                        )
                    }

                    if (isTaskDone) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = Color(0xFF10B981),
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Task Successfully Verified! Reward credited to your wallet.",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = Color(0xFF10B981),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            },
            confirmButton = {
                if (isTaskDone) {
                    Button(
                        onClick = {
                            onCompleteTask(task)
                            activeTaskModal = null
                        },
                        modifier = Modifier.testTag("claim_task_reward_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981))
                    ) {
                        Text("Claim Reward")
                    }
                } else if (!isTaskRunning) {
                    Button(
                        onClick = {
                            isTaskRunning = true
                        },
                        modifier = Modifier.testTag("start_task_modal_button"),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB))
                    ) {
                        Text("Start Task Now")
                    }
                }
            },
            dismissButton = {
                if (!isTaskRunning && !isTaskDone) {
                    TextButton(onClick = { activeTaskModal = null }) {
                        Text("Close")
                    }
                }
            }
        )

        LaunchedEffect(isTaskRunning) {
            if (isTaskRunning) {
                val totalSec = task.durationSeconds.coerceAtLeast(2)
                for (i in 1..totalSec) {
                    delay(1000L)
                    taskCountdown = totalSec - i
                    taskProgress = i.toFloat() / totalSec.toFloat()
                }
                isTaskRunning = false
                isTaskDone = true
            }
        }
    }
}

@Composable
private fun TaskCardItem(
    task: TaskEntity,
    onStart: () -> Unit
) {
    val isDone = task.isCompleted

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .clickable(enabled = !isDone) { onStart() },
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isDone) MaterialTheme.colorScheme.surface.copy(alpha = 0.6f) else MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(
                            when (task.category) {
                                "DAILY_CHECKIN" -> Color(0xFF2563EB).copy(alpha = 0.2f)
                                "SPIN_WHEEL" -> Color(0xFFF59E0B).copy(alpha = 0.2f)
                                "SCRATCH_CARD" -> Color(0xFF8B5CF6).copy(alpha = 0.2f)
                                "WATCH_VIDEO" -> Color(0xFFEF4444).copy(alpha = 0.2f)
                                else -> Color(0xFF10B981).copy(alpha = 0.2f)
                            }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = when (task.category) {
                            "DAILY_CHECKIN" -> Icons.Default.CheckCircle
                            "SPIN_WHEEL" -> Icons.Default.Casino
                            "SCRATCH_CARD" -> Icons.Default.LocalOffer
                            "WATCH_VIDEO" -> Icons.Default.PlayCircleFilled
                            "SURVEY" -> Icons.Default.Poll
                            "APP_DOWNLOAD" -> Icons.Default.Download
                            else -> Icons.Default.MonetizationOn
                        },
                        contentDescription = null,
                        tint = when (task.category) {
                            "DAILY_CHECKIN" -> Color(0xFF2563EB)
                            "SPIN_WHEEL" -> Color(0xFFF59E0B)
                            "SCRATCH_CARD" -> Color(0xFF8B5CF6)
                            "WATCH_VIDEO" -> Color(0xFFEF4444)
                            else -> Color(0xFF10B981)
                        },
                        modifier = Modifier.size(22.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = task.title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "${task.category.replace("_", " ")} • ${task.durationSeconds}s",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            if (isDone) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF10B981).copy(alpha = 0.2f))
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "DONE",
                        color = Color(0xFF10B981),
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    )
                }
            } else {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF2563EB))
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = "+$" + String.format("%.2f", task.rewardAmount),
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}
