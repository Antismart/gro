package com.example.gro.ui.screen.onboarding

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.Canvas
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.gro.ui.component.GroButton
import com.example.gro.ui.component.GroButtonStyle
import com.solana.mobilewalletadapter.clientlib.ActivityResultSender
import com.example.gro.ui.theme.GroCream
import com.example.gro.ui.theme.GroDivider
import com.example.gro.ui.theme.GroEarth
import com.example.gro.ui.theme.GroGreen
import com.example.gro.ui.theme.GroSage
import com.example.gro.ui.theme.GroSpacing
import com.example.gro.ui.theme.GroSunlight
import kotlinx.coroutines.launch

private data class OnboardingPage(
    val headline: String,
    val subtitle: String,
)

private val pages = listOf(
    OnboardingPage(
        headline = "Your money is alive",
        subtitle = "Deposits plant seeds. Staking makes them grow.",
    ),
    OnboardingPage(
        headline = "Watch it Gr\u014D",
        subtitle = "Consistency creates beauty. Your garden reflects your journey.",
    ),
    OnboardingPage(
        headline = "Start your garden",
        subtitle = "Connect your Solana wallet and plant your first seed.",
    ),
)

@Composable
fun OnboardingScreen(
    activityResultSender: ActivityResultSender,
    onNavigateToGarden: () -> Unit,
    viewModel: OnboardingViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val pagerState = rememberPagerState(pageCount = { pages.size })
    val scope = rememberCoroutineScope()

    LaunchedEffect(pagerState.currentPage) {
        viewModel.setPage(pagerState.currentPage)
    }

    LaunchedEffect(uiState.isConnected) {
        if (uiState.isConnected) {
            onNavigateToGarden()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(GroCream),
    ) {
        // Skip button
        if (pagerState.currentPage < pages.size - 1) {
            GroButton(
                text = "Skip",
                onClick = {
                    scope.launch { pagerState.animateScrollToPage(pages.size - 1) }
                },
                style = GroButtonStyle.Tertiary,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = GroSpacing.xxl, end = GroSpacing.md),
            )
        }

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(modifier = Modifier.height(GroSpacing.xxxl))

            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
            ) { page ->
                OnboardingPageContent(
                    page = pages[page],
                    pageIndex = page,
                    isLastPage = page == pages.size - 1,
                    isConnecting = uiState.isConnecting,
                    connectionError = uiState.connectionError,
                    onConnectWallet = { viewModel.connectWallet(activityResultSender) },
                )
            }

            // Page indicators
            Row(
                modifier = Modifier.padding(bottom = GroSpacing.xxl),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                repeat(pages.size) { index ->
                    val isActive = pagerState.currentPage == index
                    val size by animateDpAsState(
                        targetValue = when {
                            isActive -> 10.dp
                            index == 0 -> 6.dp
                            index == 1 -> 8.dp
                            else -> 10.dp
                        },
                        label = "indicatorSize",
                    )
                    val color by animateColorAsState(
                        targetValue = if (isActive) GroGreen else GroDivider,
                        label = "indicatorColor",
                    )
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 4.dp)
                            .size(size)
                            .clip(CircleShape)
                            .background(color),
                    )
                }
            }
        }
    }
}

@Composable
private fun OnboardingPageContent(
    page: OnboardingPage,
    pageIndex: Int,
    isLastPage: Boolean,
    isConnecting: Boolean,
    connectionError: String?,
    onConnectWallet: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = GroSpacing.xl),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // Illustration area (60% of content)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.6f),
            contentAlignment = Alignment.Center,
        ) {
            OnboardingIllustration(pageIndex = pageIndex)
        }

        // Text + CTA area
        Column(
            modifier = Modifier.weight(0.4f),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = page.headline,
                style = MaterialTheme.typography.displayMedium,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center,
            )

            Spacer(modifier = Modifier.height(GroSpacing.sm))

            Text(
                text = page.subtitle,
                style = MaterialTheme.typography.bodyLarge,
                color = GroEarth,
                textAlign = TextAlign.Center,
            )

            if (isLastPage) {
                Spacer(modifier = Modifier.height(GroSpacing.xl))

                GroButton(
                    text = "Connect wallet",
                    onClick = onConnectWallet,
                    isLoading = isConnecting,
                    modifier = Modifier.fillMaxWidth(),
                )

                if (connectionError != null) {
                    Spacer(modifier = Modifier.height(GroSpacing.xs))
                    Text(
                        text = connectionError,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error,
                        textAlign = TextAlign.Center,
                    )
                }

                Spacer(modifier = Modifier.height(GroSpacing.sm))

                Text(
                    text = "You\u2019ll need a Solana wallet like Phantom",
                    style = MaterialTheme.typography.bodySmall,
                    color = GroEarth,
                    textAlign = TextAlign.Center,
                )
            }

            Spacer(modifier = Modifier.height(GroSpacing.lg))
        }
    }
}

@Composable
private fun OnboardingIllustration(pageIndex: Int) {
    Canvas(modifier = Modifier.size(200.dp)) {
        val center = Offset(size.width / 2, size.height / 2)
        when (pageIndex) {
            0 -> {
                // Seed illustration
                drawCircle(
                    color = GroSage,
                    radius = 60f,
                    center = center,
                )
                drawCircle(
                    color = GroGreen,
                    radius = 30f,
                    center = center,
                )
            }
            1 -> {
                // Growing plant illustration
                drawCircle(
                    color = GroSage,
                    radius = 80f,
                    center = center,
                )
                drawCircle(
                    color = GroGreen,
                    radius = 50f,
                    center = Offset(center.x, center.y - 20f),
                )
                drawCircle(
                    color = GroSunlight,
                    radius = 20f,
                    center = Offset(center.x, center.y - 60f),
                )
            }
            2 -> {
                // Garden illustration
                drawCircle(
                    color = GroSage,
                    radius = 90f,
                    center = center,
                )
                drawCircle(
                    color = GroGreen,
                    radius = 40f,
                    center = Offset(center.x - 40f, center.y),
                )
                drawCircle(
                    color = GroGreen,
                    radius = 35f,
                    center = Offset(center.x + 40f, center.y - 10f),
                )
                drawCircle(
                    color = GroSunlight,
                    radius = 25f,
                    center = Offset(center.x, center.y - 40f),
                )
            }
        }
    }
}
