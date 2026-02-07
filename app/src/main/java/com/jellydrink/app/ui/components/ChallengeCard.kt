package com.jellydrink.app.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jellydrink.app.data.db.entity.DailyChallengeEntity
import com.jellydrink.app.data.repository.WaterRepository

// Challenge colors
private val ChallengeOrange = Color(0xFFFF9800)
private val ChallengeGreen = Color(0xFF4CAF50)
private val ChallengePurple = Color(0xFF9C27B0)
private val ChallengeBlue = Color(0xFF2196F3)

@Composable
fun ChallengeCard(
    challenge: DailyChallengeEntity?,
    modifier: Modifier = Modifier
) {
    if (challenge == null) return

    val challengeInfo = WaterRepository.CHALLENGE_TYPES.find { it.id == challenge.type }
    val description = challengeInfo?.description ?: "Sfida giornaliera"

    val progress = when (challenge.type) {
        "consistent" -> challenge.currentProgress.toFloat() / challenge.targetValue
        "full_tank" -> challenge.currentProgress.toFloat() / challenge.targetValue
        else -> if (challenge.completed) 1f else 0f
    }.coerceIn(0f, 1f)

    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = 500),
        label = "challenge_progress"
    )

    val (icon, color) = when (challenge.type) {
        "early_bird" -> Icons.Default.Schedule to ChallengeOrange
        "consistent" -> Icons.Default.WaterDrop to ChallengeBlue
        "big_gulp" -> Icons.Default.LocalFireDepartment to ChallengePurple
        "afternoon_goal" -> Icons.Default.Star to ChallengeOrange
        "full_tank" -> Icons.Default.EmojiEvents to ChallengeGreen
        else -> Icons.Default.Star to ChallengeBlue
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Black.copy(alpha = 0.4f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        color = color.copy(alpha = 0.2f),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (challenge.completed) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Completato",
                        tint = ChallengeGreen,
                        modifier = Modifier.size(24.dp)
                    )
                } else {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = color,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Sfida del giorno",
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "+${challenge.xpReward} XP",
                        color = Color(0xFFFFD700),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Text(
                    text = description,
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Progress bar
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp))
                        .background(Color.White.copy(alpha = 0.2f))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(animatedProgress)
                            .height(6.dp)
                            .background(
                                brush = if (challenge.completed) {
                                    Brush.horizontalGradient(
                                        colors = listOf(ChallengeGreen, Color(0xFF8BC34A))
                                    )
                                } else {
                                    Brush.horizontalGradient(
                                        colors = listOf(color, color.copy(alpha = 0.7f))
                                    )
                                }
                            )
                    )
                }

                // Progress text for multi-step challenges
                if (challenge.type == "consistent" || challenge.type == "full_tank") {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = when (challenge.type) {
                            "consistent" -> "${challenge.currentProgress}/${challenge.targetValue}"
                            "full_tank" -> "${challenge.currentProgress}%/${challenge.targetValue}%"
                            else -> ""
                        },
                        color = Color.White.copy(alpha = 0.6f),
                        fontSize = 10.sp
                    )
                }
            }

            // Completed celebration
            AnimatedVisibility(
                visible = challenge.completed,
                enter = scaleIn() + fadeIn()
            ) {
                Text(
                    text = "✓",
                    color = ChallengeGreen,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }
    }
}
