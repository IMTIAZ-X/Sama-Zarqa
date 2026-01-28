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
import androidx.compose.ui.graphics.graphicsLayer
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
 * Combined Splash + Onboarding Screen
 * Shows splash animation first, then transitions to onboarding
 */
@Composable
fun SplashScreen(
    onSplashFinished: suspend () -> Unit
) {
    // State to control splash vs onboarding
    var showOnboarding by remember { mutableStateOf(false) }

    // Splash animations
    val alphaAnim = remember { Animatable(0f) }
    val scaleAnim = remember { Animatable(0.3f) }
    var showProgress by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        // Animate logo appearance
        launch {
            scaleAnim.animateTo(
                targetValue = 1f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                )
            )
        }
        
        launch {
            alphaAnim.animateTo(
                targetValue = 1f,
                animationSpec = tween(900, easing = FastOutSlowInEasing)
            )
        }

        // Show progress indicator
        delay(600)
        showProgress = true
        
        // Wait for splash duration
        delay(1400)
        
        // Transition to onboarding
        showOnboarding = true
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(PrimaryBlue)
    ) {
        AnimatedContent(
            targetState = showOnboarding,
            transitionSpec = {
                fadeIn(animationSpec = tween(600)) togetherWith
                        fadeOut(animationSpec = tween(400))
            },
            label = "SplashToOnboarding"
        ) { showOnboard ->
            if (showOnboard) {
                // Show onboarding
                OnboardingContent(onFinished = {
                    onSplashFinished()
                })
            } else {
                // Show splash
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "SAMAZARQA",
                        color = Color.White.copy(alpha = alphaAnim.value),
                        fontSize = (36 * scaleAnim.value).sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 6.sp,
                        modifier = Modifier.graphicsLayer(
                            scaleX = scaleAnim.value,
                            scaleY = scaleAnim.value
                        )
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    androidx.compose.animation.AnimatedVisibility(
                        visible = alphaAnim.value > 0.7f,
                        enter = fadeIn(tween(500)) + slideInVertically { -20 }
                    ) {
                        Text(
                            text = "Security Suite",
                            color = Color.White.copy(alpha = 0.7f),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            letterSpacing = 2.sp
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(40.dp))
                    
                    androidx.compose.animation.AnimatedVisibility(
                        visible = showProgress,
                        enter = fadeIn(tween(400)) + scaleIn(tween(400))
                    ) {
                        CircularProgressIndicator(
                            color = Color.White,
                            strokeWidth = 3.dp,
                            modifier = Modifier.size(40.dp)
                        )
                    }
                }
            }
        }

        // Version info
        androidx.compose.animation.AnimatedVisibility(
            visible = !showOnboarding && alphaAnim.value > 0.8f,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 48.dp),
            enter = fadeIn(tween(600))
        ) {
            Text(
                text = "v1.0.0",
                color = Color.White.copy(alpha = 0.5f),
                fontSize = 12.sp,
                fontWeight = FontWeight.Light
            )
        }
    }
}

/**
 * Onboarding Content - 3 pages with navigation
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun OnboardingContent(
    onFinished: suspend () -> Unit
) {
    val pagerState = rememberPagerState(pageCount = { onboardingPages.size })
    val scope = rememberCoroutineScope()
    
    val isLastPage by remember {
        derivedStateOf { pagerState.currentPage == onboardingPages.size - 1 }
    }

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
            androidx.compose.animation.AnimatedVisibility(
                visible = !isLastPage,
                enter = fadeIn() + slideInHorizontally { it / 2 },
                exit = fadeOut() + slideOutHorizontally { it / 2 }
            ) {
                TextButton(
                    onClick = {
                        scope.launch {
                            onFinished()
                        }
                    },
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
                        isSelected = pagerState.currentPage == index
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
                            scope.launch {
                                onFinished()
                            }
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

@Composable
private fun OnboardingPage(
    data: OnboardingPageData,
    pageIndex: Int
) {
    var isVisible by remember { mutableStateOf(false) }
    
    LaunchedEffect(Unit) {
        delay(pageIndex * 100L)
        isVisible = true
    }

    androidx.compose.animation.AnimatedVisibility(
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
            // Icon Container with pulse
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

            Text(
                text = data.title,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center,
                lineHeight = 38.sp
            )

            Spacer(modifier = Modifier.height(20.dp))

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
    isSelected: Boolean
) {
    val width: Dp by animateDpAsState(
        targetValue = if (isSelected) 32.dp else 10.dp,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "indicatorWidth"
    )
    
    val color: Color by animateColorAsState(
        targetValue = if (isSelected) Color.White else Color.White.copy(alpha = 0.4f),
        animationSpec = tween(300),
        label = "indicatorColor"
    )

    Box(
        modifier = Modifier
            .width(width)
            .height(10.dp)
            .clip(CircleShape)
            .background(color)
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
