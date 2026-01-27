package com.imtbytes.samazarqa.screens.onboarding

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
import kotlinx.coroutines.launch

/**
 * Modern Onboarding Screen with smooth page transitions
 * Shows once on first app launch, then saved via DataStore
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreen(
    onFinished: () -> Unit
) {
    val pagerState = rememberPagerState(pageCount = { onboardingPages.size })
    val scope = rememberCoroutineScope()
    
    val isLastPage by remember {
        derivedStateOf { pagerState.currentPage == onboardingPages.size - 1 }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(PrimaryBlue)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(24.dp)
        ) {
            // Skip Button Area
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                AnimatedVisibility(
                    visible = !isLastPage,
                    enter = fadeIn() + slideInHorizontally { it / 2 },
                    exit = fadeOut() + slideOutHorizontally { it / 2 }
                ) {
                    TextButton(
                        onClick = onFinished,
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = Color.White
                        )
                    ) {
                        Text(
                            text = "Skip",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            // Pager Content
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.weight(1f)
            ) { pageIndex ->
                OnboardingPage(
                    data = onboardingPages[pageIndex],
                    pageIndex = pageIndex
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

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
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    repeat(onboardingPages.size) { index ->
                        PageIndicator(
                            isSelected = pagerState.currentPage == index,
                            index = index
                        )
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))

                // Action Button
                AnimatedContent(
                    targetState = isLastPage,
                    transitionSpec = {
                        fadeIn(tween(300)) + scaleIn(tween(300)) togetherWith
                                fadeOut(tween(200)) + scaleOut(tween(200))
                    },
                    label = "ButtonTransition"
                ) { lastPage ->
                    Button(
                        onClick = {
                            if (lastPage) {
                                onFinished()
                            } else {
                                scope.launch {
                                    pagerState.animateScrollToPage(
                                        page = pagerState.currentPage + 1,
                                        animationSpec = spring(
                                            dampingRatio = Spring.DampingRatioMediumBouncy,
                                            stiffness = Spring.StiffnessLow
                                        )
                                    )
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White,
                            contentColor = PrimaryBlue
                        ),
                        shape = RoundedCornerShape(16.dp),
                        elevation = ButtonDefaults.buttonElevation(
                            defaultElevation = 4.dp,
                            pressedElevation = 8.dp
                        ),
                        contentPadding = PaddingValues(
                            horizontal = 32.dp,
                            vertical = 14.dp
                        )
                    ) {
                        Text(
                            text = if (lastPage) "Get Started" else "Next",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        
                        if (!lastPage) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(
                                imageVector = Icons.Rounded.ArrowForward,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun OnboardingPage(
    data: OnboardingPageData,
    pageIndex: Int
) {
    // Entry animation based on page index
    val animationDelay = pageIndex * 100
    
    var isVisible by remember { mutableStateOf(false) }
    
    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(animationDelay.toLong())
        isVisible = true
    }

    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn(tween(600)) + slideInVertically(
            animationSpec = tween(600, easing = FastOutSlowInEasing)
        ) { it / 3 }
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            // Icon Container with pulse animation
            val infiniteTransition = rememberInfiniteTransition(label = "pulse")
            val pulseScale by infiniteTransition.animateFloat(
                initialValue = 1f,
                targetValue = 1.05f,
                animationSpec = infiniteRepeatable(
                    animation = tween(2000, easing = FastOutSlowInEasing),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "pulseScale"
            )

            Box(
                modifier = Modifier
                    .size(200.dp)
                    .graphicsLayer {
                        scaleX = pulseScale
                        scaleY = pulseScale
                    }
                    .clip(RoundedCornerShape(40.dp))
                    .background(Color.White.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                // Decorative circles
                Box(
                    modifier = Modifier
                        .size(140.dp)
                        .clip(CircleShape)
                        .background(Color.White.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = data.icon,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(80.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(56.dp))

            // Title with reveal animation
            Text(
                text = data.title,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center,
                lineHeight = 38.sp
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Description
            Text(
                text = data.description,
                fontSize = 16.sp,
                fontWeight = FontWeight.Normal,
                color = Color.White.copy(alpha = 0.85f),
                textAlign = TextAlign.Center,
                lineHeight = 24.sp,
                modifier = Modifier.padding(horizontal = 8.dp)
            )
        }
    }
}

@Composable
private fun PageIndicator(
    isSelected: Boolean,
    index: Int
) {
    val width: Dp by animateDpAsState(
        targetValue = if (isSelected) 32.dp else 10.dp,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "indicatorWidth"
    )
    
    val height: Dp by animateDpAsState(
        targetValue = if (isSelected) 10.dp else 10.dp,
        label = "indicatorHeight"
    )
    
    val color: Color by animateColorAsState(
        targetValue = if (isSelected) Color.White else Color.White.copy(alpha = 0.4f),
        animationSpec = tween(300),
        label = "indicatorColor"
    )

    Box(
        modifier = Modifier
            .width(width)
            .height(height)
            .clip(CircleShape)
            .background(color)
    )
}

/**
 * Onboarding page data model
 */
private data class OnboardingPageData(
    val title: String,
    val description: String,
    val icon: ImageVector
)

/**
 * Onboarding pages content - customizable
 */
private val onboardingPages = listOf(
    OnboardingPageData(
        title = "Advanced Security",
        description = "Military-grade encryption and real-time threat detection to keep your data safe from any intrusion.",
        icon = Icons.Rounded.Shield
    ),
    OnboardingPageData(
        title = "Smart Monitoring",
        description = "Intelligent system analytics that track vulnerabilities and provide instant security insights.",
        icon = Icons.Rounded.Analytics
    ),
    OnboardingPageData(
        title = "Total Protection",
        description = "Complete security suite with vault encryption, network scanning, and automated defense mechanisms.",
        icon = Icons.Rounded.Verified
    )
)
