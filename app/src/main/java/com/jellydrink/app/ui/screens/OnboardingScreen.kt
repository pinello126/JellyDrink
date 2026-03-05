package com.jellydrink.app.ui.screens

import android.content.Context
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
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
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.jellydrink.app.R
import com.jellydrink.app.ui.components.JellyFishView
import kotlinx.coroutines.launch

private data class OnboardingPage(
    val emoji: String,
    val titleRes: Int,
    val descRes: Int,
    val accentColor: Color
)

private val pages = listOf(
    OnboardingPage(
        emoji = "🪼",
        titleRes = R.string.onboarding_title_1,
        descRes = R.string.onboarding_desc_1,
        accentColor = Color(0xFF4FC3F7)
    ),
    OnboardingPage(
        emoji = "💧",
        titleRes = R.string.onboarding_title_2,
        descRes = R.string.onboarding_desc_2,
        accentColor = Color(0xFF29B6F6)
    ),
    OnboardingPage(
        emoji = "🏆",
        titleRes = R.string.onboarding_title_3,
        descRes = R.string.onboarding_desc_3,
        accentColor = Color(0xFFFFD700)
    )
)

@Composable
fun OnboardingScreen(onComplete: () -> Unit) {
    val context = LocalContext.current
    val pagerState = rememberPagerState(pageCount = { pages.size })
    val scope = rememberCoroutineScope()

    fun complete() {
        context.getSharedPreferences("onboarding_prefs", Context.MODE_PRIVATE)
            .edit().putBoolean("completed", true).apply()
        onComplete()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFF0A1628), Color(0xFF0D2137), Color(0xFF0A1628))
                )
            )
    ) {
        // Pager con le pagine
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 160.dp)
        ) { pageIndex ->
            val page = pages[pageIndex]
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 40.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Illustrazione: contenitore fisso 220dp per allineare tutte le pagine
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp),
                    contentAlignment = Alignment.Center
                ) {
                    if (pageIndex == 0) {
                        JellyFishView(
                            fillPercentage = 1.0f,
                            modifier = Modifier.size(220.dp),
                            isStatic = true
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .size(140.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.radialGradient(
                                        colors = listOf(
                                            page.accentColor.copy(alpha = 0.25f),
                                            page.accentColor.copy(alpha = 0.05f)
                                        )
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = page.emoji,
                                fontSize = 72.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(40.dp))

                // Linea decorativa colorata
                Box(
                    modifier = Modifier
                        .width(48.dp)
                        .height(3.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(page.accentColor)
                )

                Spacer(modifier = Modifier.height(24.dp))

                Text(
                    text = stringResource(page.titleRes),
                    fontSize = 26.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = stringResource(page.descRes),
                    fontSize = 16.sp,
                    color = Color.White.copy(alpha = 0.65f),
                    textAlign = TextAlign.Center,
                    lineHeight = 24.sp
                )
            }
        }

        // Bottom controls
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(horizontal = 32.dp, vertical = 40.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Dot indicators
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(pages.size) { index ->
                    val isSelected = pagerState.currentPage == index
                    val dotWidth by animateDpAsState(
                        targetValue = if (isSelected) 24.dp else 8.dp,
                        animationSpec = tween(300),
                        label = "dotWidth"
                    )
                    val accentColor = pages[pagerState.currentPage].accentColor
                    Box(
                        modifier = Modifier
                            .height(8.dp)
                            .width(dotWidth)
                            .clip(CircleShape)
                            .background(
                                if (isSelected) accentColor
                                else Color.White.copy(alpha = 0.25f)
                            )
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            val isLastPage = pagerState.currentPage == pages.size - 1
            val accentColor = pages[pagerState.currentPage].accentColor

            // Bottone principale
            Button(
                onClick = {
                    if (isLastPage) {
                        complete()
                    } else {
                        scope.launch {
                            pagerState.animateScrollToPage(pagerState.currentPage + 1)
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = accentColor,
                    contentColor = Color(0xFF0A1628)
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    text = if (isLastPage) stringResource(R.string.onboarding_start)
                           else stringResource(R.string.onboarding_next),
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 16.sp
                )
            }

            // Salta (nascosto sull'ultima pagina)
            if (!isLastPage) {
                TextButton(onClick = { complete() }) {
                    Text(
                        text = stringResource(R.string.onboarding_skip),
                        color = Color.White.copy(alpha = 0.45f),
                        fontSize = 14.sp
                    )
                }
            } else {
                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}
