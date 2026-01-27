package com.imtbytes.samazarqa.screens.splash

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Analytics
import androidx.compose.material.icons.rounded.DoneAll
import androidx.compose.material.icons.rounded.TrendingUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.imtbytes.samazarqa.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun SplashScreen(
    // অনবোর্ডিং শেষ হলে মূল স্ক্রিনে যাওয়ার জন্য কলব্যাক
    onOnboardingFinished: () -> Unit = {}
) {
    // --- Existing States ---
    val alphaAnim = remember { Animatable(0f) }

    // --- New States for Transition & Onboarding ---
    var isSplashFinished by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        // Existing Animation
        alphaAnim.animateTo(
            targetValue = 1f,
            animationSpec = tween(900, easing = FastOutSlowInEasing)
        )

        // New: Wait a bit after animation finishes, then transition to onboarding
        delay(1500)
        isSplashFinished = true
    }

    // Root Container with Background Color
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(PrimaryBlue),
        contentAlignment = Alignment.Center
    ) {
        // Smooth transition between Splash and Onboarding
        AnimatedContent(
            targetState = isSplashFinished,
            label = "SplashToOnboardingTransition",
            transitionSpec = {
                fadeIn(animationSpec = tween(600)) togetherWith
                        fadeOut(animationSpec = tween(400))
            }
        ) { showOnboarding ->
            if (showOnboarding) {
                // --- NEW ONBOARDING UI ---
                OnboardingContent(onFinished = onOnboardingFinished)
            } else {
                // --- EXISTING SPLASH UI (Wrapped here, unchanged internal logic) ---
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "SAMAZARQA",
                            color = Color.White.copy(alpha = alphaAnim.value),
                            fontSize = 36.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 6.sp
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        CircularProgressIndicator(
                            color = Color.White,
                            strokeWidth = 3.dp,
                            modifier = Modifier.size(40.dp)
                        )
                    }
                }
            }
        }
    }
}

// ==========================================
// NEW ADDITIONS: INTERNAL COMPOSABLES & DATA
// ==========================================

// Data model for individual onboarding pages
private data class OnboardingPageData(
    val title: String,
    val description: String,
    val icon: ImageVector
)

// Sample Data matching the Figma concept vibe
private val onboardingPages = listOf(
    OnboardingPageData(
        title = "Track Your Work",
        description = "Monitor your daily tasks and progress seamlessly with intuitive tracking tools designed for efficiency.",
        icon = Icons.Rounded.Analytics
    ),
    OnboardingPageData(
        title = "Boost Productivity",
        description = "Analyze your performance patterns to identify areas for improvement and maximize your output.",
        icon = Icons.Rounded.TrendingUp
    ),
    OnboardingPageData(
        title = "Achieve Goals",
        description = "Set milestones and finish tasks efficiently. Get started today and take control of your workflow.",
        icon = Icons.Rounded.DoneAll
    )
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun OnboardingContent(onFinished: () -> Unit) {
    val pagerState = rememberPagerState(pageCount = { onboardingPages.size })
    val scope = rememberCoroutineScope()
    val isLastPage by remember { derivedStateOf { pagerState.currentPage == onboardingPages.size - 1 } }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding() // Ensure content doesn't overlap status bars
            .padding(24.dp)
    ) {
        // Top Bar with Skip Button
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            contentAlignment = Alignment.CenterEnd
        ) {
            AnimatedVisibility(
                visible = !isLastPage,
                exit = fadeOut(),
                enter = fadeIn()
            ) {
                TextButton(onClick = onFinished) {
                    Text(
                        text = "Skip",
                        color = Color.White.copy(alpha = 0.7f),
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }
        }

        // Pager Content Section
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) { pageIndex ->
            val pageData = onboardingPages[pageIndex]
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
            ) {
                // Placeholder Icon/Image container styled like Figma
                Box(
                    modifier = Modifier
                        .size(200.dp)
                        .clip(RoundedCornerShape(32.dp))
                        .background(Color.White.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = pageData.icon,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(100.dp)
                    )
                }

                Spacer(modifier = Modifier.height(48.dp))

                Text(
                    text = pageData.title,
                    style = MaterialTheme.typography.headlineMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = pageData.description,
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.White.copy(alpha = 0.8f),
                    textAlign = TextAlign.Center,
                    lineHeight = 24.sp
                )
            }
        }

        // Bottom Controls Section (Indicators & Button)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 24.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Pager Indicators
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(pagerState.pageCount) { iteration ->
                    val isSelected = pagerState.currentPage == iteration
                    val width by animateDpAsState(
                        targetValue = if (isSelected) 24.dp else 8.dp,
                        animationSpec = tween(300), label = "indicatorWidth"
                    )
                    val color by animateColorAsState(
                        targetValue = if (isSelected) Color.White else Color.White.copy(alpha = 0.3f),
                        animationSpec = tween(300), label = "indicatorColor"
                    )

                    Box(
                        modifier = Modifier
                            .height(8.dp)
                            .width(width)
                            .clip(CircleShape)
                            .background(color)
                    )
                }
            }

            // Next / Get Started Button
            Button(
                onClick = {
                    if (isLastPage) {
                        onFinished()
                    } else {
                        scope.launch {
                            pagerState.animateScrollToPage(pagerState.currentPage + 1)
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.White,
                    contentColor = PrimaryBlue
                ),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.height(50.dp)
            ) {
                AnimatedContent(targetState = isLastPage, label = "ButtonTextAnim") { last ->
                    Text(
                        text = if (last) "Get Started" else "Next",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
        }
    }
}