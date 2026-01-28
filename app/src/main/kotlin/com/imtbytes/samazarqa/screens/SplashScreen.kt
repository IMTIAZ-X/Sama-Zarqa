package com.imtbytes.samazarqa.screens.splash

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.imtbytes.samazarqa.ui.theme.PrimaryBlue
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Production-ready Splash Screen
 * Optimized for performance with minimal animations
 */
@Composable
fun SplashScreen(
    isFirstLaunch: Boolean,
    onFinished: () -> Unit
) {
    // Simple state - no heavy animations
    var showContent by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(300) // Short delay for smooth entry
        showContent = true
        
        if (!isFirstLaunch) {
            // Returning user - skip to home quickly
            delay(1500)
            onFinished()
        }
        // First time users will see onboarding, controlled by buttons
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(PrimaryBlue)
    ) {
        if (isFirstLaunch && showContent) {
            // First launch - show onboarding
            OnboardingContent(onFinished = onFinished)
        } else {
            // Returning user - simple splash
            SimpleSplash(show = showContent)
        }
    }
}

/**
 * Simple splash for returning users - minimal animation
 */
@Composable
private fun SimpleSplash(show: Boolean) {
    AnimatedVisibility(
        visible = show,
        enter = fadeIn(tween(400))
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "SAMAZARQA",
                color = Color.White,
                fontSize = 36.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 6.sp
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                text = "Security Suite",
                color = Color.White.copy(alpha = 0.7f),
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                letterSpacing = 2.sp
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            CircularProgressIndicator(
                color = Color.White,
                strokeWidth = 3.dp,
                modifier = Modifier.size(32.dp)
            )
        }
    }
}

/**
 * Onboarding for first-time users
 * Optimized with minimal animations
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun OnboardingContent(
    onFinished: () -> Unit
) {
    val pagerState = rememberPagerState(pageCount = { onboardingPages.size })
    val scope = rememberCoroutineScope()
    
    val isLastPage = pagerState.currentPage == onboardingPages.size - 1
    
    // Button click states to prevent double-clicks
    var isNavigating by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(20.dp)
    ) {
        // Skip Button
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            contentAlignment = Alignment.CenterEnd
        ) {
            if (!isLastPage) {
                TextButton(
                    onClick = {
                        if (!isNavigating) {
                            isNavigating = true
                            onFinished()
                        }
                    },
                    enabled = !isNavigating
                ) {
                    Text(
                        text = "Skip",
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        // Pager
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.weight(1f),
            userScrollEnabled = !isNavigating
        ) { pageIndex ->
            OnboardingPage(data = onboardingPages[pageIndex])
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Bottom Navigation
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Page Indicators
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.weight(1f)
            ) {
                repeat(onboardingPages.size) { index ->
                    PageIndicator(isSelected = pagerState.currentPage == index)
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Next/Get Started Button
            Button(
                onClick = {
                    if (!isNavigating) {
                        if (isLastPage) {
                            isNavigating = true
                            onFinished()
                        } else {
                            scope.launch {
                                pagerState.animateScrollToPage(pagerState.currentPage + 1)
                            }
                        }
                    }
                },
                enabled = !isNavigating,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = PrimaryBlue
                ),
                shape = RoundedCornerShape(14.dp),
                contentPadding = PaddingValues(horizontal = 28.dp, vertical = 12.dp)
            ) {
                Text(
                    text = if (isLastPage) "Get Started" else "Next",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
                
                if (!isLastPage) {
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(
                        imageVector = Icons.Rounded.ArrowForward,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

/**
 * Simplified onboarding page - no heavy animations
 */
@Composable
private fun OnboardingPage(data: OnboardingPageData) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp)
    ) {
        // Icon Container - simple, no pulse animation
        Box(
            modifier = Modifier
                .size(180.dp)
                .clip(RoundedCornerShape(36.dp))
                .background(Color.White.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(130.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = data.icon,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(70.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(48.dp))

        Text(
            text = data.title,
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            textAlign = TextAlign.Center,
            lineHeight = 36.sp
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = data.description,
            fontSize = 15.sp,
            fontWeight = FontWeight.Normal,
            color = Color.White.copy(alpha = 0.85f),
            textAlign = TextAlign.Center,
            lineHeight = 22.sp
        )
    }
}

/**
 * Simple page indicator
 */
@Composable
private fun PageIndicator(isSelected: Boolean) {
    val width: Dp by animateDpAsState(
        targetValue = if (isSelected) 28.dp else 8.dp,
        animationSpec = tween(200),
        label = "indicatorWidth"
    )
    
    Box(
        modifier = Modifier
            .width(width)
            .height(8.dp)
            .clip(CircleShape)
            .background(
                if (isSelected) Color.White else Color.White.copy(alpha = 0.4f)
            )
    )
}

private data class OnboardingPageData(
    val title: String,
    val description: String,
    val icon: ImageVector
)

private val onboardingPages = listOf(
    OnboardingPageData(
        title = "Advanced Security",
        description = "Military-grade encryption and real-time threat detection to keep your data safe.",
        icon = Icons.Rounded.Shield
    ),
    OnboardingPageData(
        title = "Smart Monitoring",
        description = "Intelligent analytics that track vulnerabilities and provide instant insights.",
        icon = Icons.Rounded.Analytics
    ),
    OnboardingPageData(
        title = "Total Protection",
        description = "Complete security suite with vault encryption and automated defense.",
        icon = Icons.Rounded.Verified
    )
)
