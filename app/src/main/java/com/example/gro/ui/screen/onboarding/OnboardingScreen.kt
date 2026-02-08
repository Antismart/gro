package com.example.gro.ui.screen.onboarding

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.gro.ui.component.GroButton
import com.example.gro.ui.component.GroButtonStyle
import com.example.gro.ui.theme.GroCream
import com.example.gro.ui.theme.GroDivider
import com.example.gro.ui.theme.GroEarth
import com.example.gro.ui.theme.GroGreen
import com.example.gro.ui.theme.GroSpacing
import com.solana.mobilewalletadapter.clientlib.ActivityResultSender
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

private val SoilBrown = Color(0xFF5C4033)
private val SoilLight = Color(0xFF7C6955)
private val SeedTan = Color(0xFF8B7355)
private val StemGreen = Color(0xFF4A6B4A)
private val LeafGreen = Color(0xFF6B9E6F)
private val LeafLight = Color(0xFFA8D5A2)
private val FlowerPink = Color(0xFFD4726A)
private val SunYellow = Color(0xFFE8B849)
private val SkyBlue = Color(0xFFD4E8F0)

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

    LaunchedEffect(pagerState.currentPage) { viewModel.setPage(pagerState.currentPage) }
    LaunchedEffect(uiState.isConnected) { if (uiState.isConnected) onNavigateToGarden() }

    Box(
        modifier = Modifier.fillMaxSize().background(GroCream),
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(modifier = Modifier.height(GroSpacing.xxxl))
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxWidth().weight(1f),
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
            Row(
                modifier = Modifier.padding(bottom = GroSpacing.xxl),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                repeat(pages.size) { index ->
                    val isActive = pagerState.currentPage == index
                    val dotSize by animateDpAsState(
                        if (isActive) 10.dp else 7.dp, label = "dot",
                    )
                    val dotColor by animateColorAsState(
                        if (isActive) GroGreen else GroDivider, label = "dotColor",
                    )
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 4.dp)
                            .size(dotSize)
                            .clip(CircleShape)
                            .background(dotColor),
                    )
                }
            }
        }
        // Skip — rendered AFTER Column so it sits on top for touch events
        if (pagerState.currentPage < pages.size - 1) {
            GroButton(
                text = "Skip",
                onClick = { scope.launch { pagerState.animateScrollToPage(pages.size - 1) } },
                style = GroButtonStyle.Tertiary,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = GroSpacing.xxl, end = GroSpacing.md),
            )
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
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // Illustration
        Box(
            modifier = Modifier.fillMaxWidth().weight(0.55f),
            contentAlignment = Alignment.Center,
        ) {
            OnboardingIllustration(pageIndex = pageIndex)
        }
        // Text + CTA
        Column(
            modifier = Modifier
                .weight(0.45f)
                .padding(horizontal = GroSpacing.xl),
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
    val progress = remember { Animatable(0f) }
    LaunchedEffect(pageIndex) {
        progress.snapTo(0f)
        progress.animateTo(1f, tween(2000, easing = FastOutSlowInEasing))
    }

    val ambient = rememberInfiniteTransition(label = "ambient")
    val float by ambient.animateFloat(
        -1f, 1f,
        infiniteRepeatable(tween(3000, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "float",
    )

    Canvas(modifier = Modifier.size(240.dp)) {
        when (pageIndex) {
            0 -> drawSeedScene(progress.value, float)
            1 -> drawGrowthScene(progress.value, float)
            2 -> drawGardenScene(progress.value, float)
        }
    }
}

// ── Screen 1: Seed sprouting ─────────────────────────────────────────
private fun DrawScope.drawSeedScene(p: Float, float: Float) {
    val w = size.width
    val h = size.height
    val cx = w / 2f
    val groundY = h * 0.62f

    // Ground
    drawRoundRect(
        color = SoilBrown,
        topLeft = Offset(0f, groundY),
        size = Size(w, h - groundY),
        cornerRadius = CornerRadius(w * 0.08f),
    )
    drawRoundRect(
        color = SoilLight,
        topLeft = Offset(0f, groundY),
        size = Size(w, h * 0.06f),
        cornerRadius = CornerRadius(w * 0.08f),
    )

    // Seed — oval sitting on the soil
    val seedAlpha = (p * 3f).coerceAtMost(1f)
    val seedCy = groundY - h * 0.02f
    drawOval(
        color = SeedTan.copy(alpha = seedAlpha),
        topLeft = Offset(cx - w * 0.06f, seedCy - h * 0.035f),
        size = Size(w * 0.12f, h * 0.07f),
    )
    // Seed highlight
    drawOval(
        color = Color.White.copy(alpha = 0.2f * seedAlpha),
        topLeft = Offset(cx - w * 0.03f, seedCy - h * 0.025f),
        size = Size(w * 0.04f, h * 0.02f),
    )

    // Sprout stem — grows upward
    val stemProgress = ((p - 0.3f) / 0.5f).coerceIn(0f, 1f)
    if (stemProgress > 0f) {
        val stemTop = seedCy - h * 0.18f * stemProgress + float * 2f
        val stemPath = Path().apply {
            moveTo(cx, seedCy - h * 0.03f)
            quadraticTo(cx + w * 0.02f, (seedCy - h * 0.03f + stemTop) / 2f, cx, stemTop)
        }
        drawPath(stemPath, color = StemGreen, style = Stroke(3.dp.toPx(), cap = StrokeCap.Round))

        // Two small leaves at the top
        val leafProgress = ((p - 0.6f) / 0.3f).coerceIn(0f, 1f)
        if (leafProgress > 0f) {
            val leafSize = w * 0.06f * leafProgress
            // Left leaf
            val leftLeaf = Path().apply {
                moveTo(cx, stemTop + h * 0.01f)
                quadraticTo(cx - leafSize * 1.2f, stemTop - leafSize * 0.3f, cx - leafSize * 0.4f, stemTop - leafSize * 0.8f)
                quadraticTo(cx - leafSize * 0.1f, stemTop, cx, stemTop + h * 0.01f)
            }
            drawPath(leftLeaf, color = LeafGreen)

            // Right leaf
            val rightLeaf = Path().apply {
                moveTo(cx, stemTop + h * 0.01f)
                quadraticTo(cx + leafSize * 1.2f, stemTop - leafSize * 0.3f, cx + leafSize * 0.4f, stemTop - leafSize * 0.8f)
                quadraticTo(cx + leafSize * 0.1f, stemTop, cx, stemTop + h * 0.01f)
            }
            drawPath(rightLeaf, color = LeafLight)
        }
    }

    // Small sparkle
    if (p > 0.8f) {
        val sparkAlpha = ((p - 0.8f) / 0.2f).coerceAtMost(1f) * 0.6f
        drawCircle(
            color = SunYellow.copy(alpha = sparkAlpha),
            radius = 3.dp.toPx(),
            center = Offset(cx + w * 0.12f, groundY - h * 0.2f + float * 4f),
        )
    }
}

// ── Screen 2: Growing plant ──────────────────────────────────────────
private fun DrawScope.drawGrowthScene(p: Float, float: Float) {
    val w = size.width
    val h = size.height
    val cx = w / 2f
    val groundY = h * 0.72f

    // Sun circle — top right
    val sunAlpha = (p * 2f).coerceAtMost(1f)
    drawCircle(
        color = SunYellow.copy(alpha = 0.2f * sunAlpha),
        radius = w * 0.18f,
        center = Offset(w * 0.78f, h * 0.12f),
    )
    drawCircle(
        color = SunYellow.copy(alpha = 0.4f * sunAlpha),
        radius = w * 0.1f,
        center = Offset(w * 0.78f, h * 0.12f),
    )

    // Ground
    drawRoundRect(
        color = SoilBrown,
        topLeft = Offset(0f, groundY),
        size = Size(w, h - groundY),
        cornerRadius = CornerRadius(w * 0.08f),
    )
    drawRoundRect(
        color = SoilLight,
        topLeft = Offset(0f, groundY),
        size = Size(w, h * 0.04f),
        cornerRadius = CornerRadius(w * 0.08f),
    )

    // Main stem — S-curve growing upward
    val stemProgress = (p * 1.3f).coerceAtMost(1f)
    val stemHeight = h * 0.42f * stemProgress
    val stemTop = groundY - stemHeight + float * 2f

    val stemPath = Path().apply {
        moveTo(cx, groundY)
        cubicTo(
            cx + w * 0.04f, groundY - stemHeight * 0.33f,
            cx - w * 0.03f, groundY - stemHeight * 0.66f,
            cx, stemTop,
        )
    }
    drawPath(stemPath, color = StemGreen, style = Stroke(3.5f.dp.toPx(), cap = StrokeCap.Round))

    // Leaves — 3 pairs staggered
    data class LeafInfo(val startTime: Float, val stemFrac: Float, val leafSize: Float)
    val leaves = listOf(
        LeafInfo(0.25f, 0.3f, w * 0.07f),
        LeafInfo(0.4f, 0.55f, w * 0.06f),
        LeafInfo(0.55f, 0.78f, w * 0.05f),
    )

    leaves.forEachIndexed { i, leaf ->
        val lp = ((p - leaf.startTime) / 0.25f).coerceIn(0f, 1f)
        if (lp > 0f) {
            val ly = groundY - stemHeight * leaf.stemFrac
            val ls = leaf.leafSize * lp
            val isLeft = i % 2 == 0

            // Primary leaf
            val dir = if (isLeft) -1f else 1f
            val leafPath = Path().apply {
                moveTo(cx, ly)
                quadraticTo(
                    cx + dir * ls * 1.3f, ly - ls * 0.4f,
                    cx + dir * ls * 0.5f, ly - ls * 0.9f,
                )
                quadraticTo(cx + dir * ls * 0.1f, ly - ls * 0.1f, cx, ly)
            }
            drawPath(leafPath, color = if (i == 2) LeafLight else LeafGreen)

            // Opposite smaller leaf
            val oppDir = -dir
            val oppSize = ls * 0.8f
            val oppLeaf = Path().apply {
                moveTo(cx, ly)
                quadraticTo(
                    cx + oppDir * oppSize * 1.2f, ly - oppSize * 0.3f,
                    cx + oppDir * oppSize * 0.4f, ly - oppSize * 0.8f,
                )
                quadraticTo(cx + oppDir * oppSize * 0.1f, ly - oppSize * 0.1f, cx, ly)
            }
            drawPath(oppLeaf, color = LeafGreen.copy(alpha = 0.7f))
        }
    }

    // Flower bud at the top
    val budProgress = ((p - 0.7f) / 0.2f).coerceIn(0f, 1f)
    if (budProgress > 0f) {
        val budR = w * 0.025f * budProgress
        drawCircle(color = FlowerPink, radius = budR, center = Offset(cx, stemTop))
        drawCircle(color = FlowerPink.copy(alpha = 0.4f), radius = budR * 1.4f, center = Offset(cx, stemTop))
    }
}

// ── Screen 3: Full garden ────────────────────────────────────────────
private fun DrawScope.drawGardenScene(p: Float, float: Float) {
    val w = size.width
    val h = size.height
    val groundY = h * 0.7f

    // Sun
    val sunAlpha = (p * 2f).coerceAtMost(1f)
    drawCircle(
        color = SunYellow.copy(alpha = 0.15f * sunAlpha),
        radius = w * 0.22f,
        center = Offset(w * 0.2f, h * 0.1f),
    )
    drawCircle(
        color = SunYellow.copy(alpha = 0.35f * sunAlpha),
        radius = w * 0.12f,
        center = Offset(w * 0.2f, h * 0.1f),
    )
    // Sun rays
    for (i in 0..5) {
        val angle = (i * 60f) * PI.toFloat() / 180f
        val innerR = w * 0.14f
        val outerR = w * 0.2f
        drawLine(
            color = SunYellow.copy(alpha = 0.15f * sunAlpha),
            start = Offset(w * 0.2f + cos(angle) * innerR, h * 0.1f + sin(angle) * innerR),
            end = Offset(w * 0.2f + cos(angle) * outerR, h * 0.1f + sin(angle) * outerR),
            strokeWidth = 2.dp.toPx(),
            cap = StrokeCap.Round,
        )
    }

    // Ground — gentle hill
    val groundPath = Path().apply {
        moveTo(0f, groundY + h * 0.02f)
        quadraticTo(w * 0.5f, groundY - h * 0.02f, w, groundY + h * 0.02f)
        lineTo(w, h)
        lineTo(0f, h)
        close()
    }
    drawPath(groundPath, color = SoilBrown)
    // Top soil layer
    val topSoil = Path().apply {
        moveTo(0f, groundY + h * 0.02f)
        quadraticTo(w * 0.5f, groundY - h * 0.02f, w, groundY + h * 0.02f)
        lineTo(w, groundY + h * 0.06f)
        quadraticTo(w * 0.5f, groundY + h * 0.02f, 0f, groundY + h * 0.06f)
        close()
    }
    drawPath(topSoil, color = SoilLight)

    // 3 plants at different positions
    data class PlantDef(val x: Float, val height: Float, val startTime: Float, val hasFlower: Boolean, val flowerColor: Color)
    val plants = listOf(
        PlantDef(w * 0.22f, h * 0.32f, 0.1f, true, FlowerPink),
        PlantDef(w * 0.52f, h * 0.24f, 0.2f, false, Color.Transparent),
        PlantDef(w * 0.78f, h * 0.28f, 0.15f, true, SunYellow),
    )

    plants.forEachIndexed { pi, plant ->
        val pp = ((p - plant.startTime) / 0.6f).coerceIn(0f, 1f)
        if (pp <= 0f) return@forEachIndexed

        val baseY = groundY - h * 0.005f
        val stemH = plant.height * pp
        val tipY = baseY - stemH + float * (1.5f + pi * 0.5f)

        // Stem
        val stemPath = Path().apply {
            moveTo(plant.x, baseY)
            cubicTo(
                plant.x + w * 0.02f, baseY - stemH * 0.33f,
                plant.x - w * 0.015f, baseY - stemH * 0.66f,
                plant.x, tipY,
            )
        }
        drawPath(stemPath, color = StemGreen, style = Stroke(3.dp.toPx(), cap = StrokeCap.Round))

        // 2 leaves
        val leafTime = ((p - plant.startTime - 0.15f) / 0.3f).coerceIn(0f, 1f)
        if (leafTime > 0f) {
            val ly1 = baseY - stemH * 0.4f
            val ly2 = baseY - stemH * 0.65f
            val ls1 = w * 0.055f * leafTime
            val ls2 = w * 0.045f * leafTime

            // Lower left leaf
            val ll1 = Path().apply {
                moveTo(plant.x, ly1)
                quadraticTo(plant.x - ls1 * 1.2f, ly1 - ls1 * 0.4f, plant.x - ls1 * 0.4f, ly1 - ls1 * 0.8f)
                quadraticTo(plant.x - ls1 * 0.1f, ly1, plant.x, ly1)
            }
            drawPath(ll1, color = LeafGreen)

            // Lower right leaf
            val lr1 = Path().apply {
                moveTo(plant.x, ly1)
                quadraticTo(plant.x + ls1 * 1.1f, ly1 - ls1 * 0.3f, plant.x + ls1 * 0.35f, ly1 - ls1 * 0.7f)
                quadraticTo(plant.x + ls1 * 0.05f, ly1, plant.x, ly1)
            }
            drawPath(lr1, color = LeafLight)

            // Upper leaf
            val ul = Path().apply {
                moveTo(plant.x, ly2)
                quadraticTo(plant.x + ls2 * 1.2f * (if (pi % 2 == 0) -1f else 1f), ly2 - ls2 * 0.4f,
                    plant.x + ls2 * 0.4f * (if (pi % 2 == 0) -1f else 1f), ly2 - ls2 * 0.8f)
                quadraticTo(plant.x + ls2 * 0.05f * (if (pi % 2 == 0) -1f else 1f), ly2, plant.x, ly2)
            }
            drawPath(ul, color = LeafGreen.copy(alpha = 0.8f))
        }

        // Flower or top
        if (plant.hasFlower) {
            val fp = ((p - plant.startTime - 0.35f) / 0.25f).coerceIn(0f, 1f)
            if (fp > 0f) {
                val fr = w * 0.03f * fp
                if (plant.flowerColor == SunYellow) {
                    // Sunflower — petals around center
                    for (i in 0..7) {
                        val angle = (i * 45f) * PI.toFloat() / 180f
                        val petalR = fr * 0.6f
                        drawCircle(
                            color = SunYellow.copy(alpha = 0.8f),
                            radius = petalR,
                            center = Offset(plant.x + cos(angle) * fr, tipY + sin(angle) * fr),
                        )
                    }
                    drawCircle(color = SoilBrown, radius = fr * 0.45f, center = Offset(plant.x, tipY))
                } else {
                    // Simple flower — 5 petals
                    for (i in 0..4) {
                        val angle = (i * 72f - 90f) * PI.toFloat() / 180f
                        drawCircle(
                            color = plant.flowerColor.copy(alpha = 0.75f),
                            radius = fr * 0.55f,
                            center = Offset(plant.x + cos(angle) * fr * 0.6f, tipY + sin(angle) * fr * 0.6f),
                        )
                    }
                    drawCircle(color = SunYellow, radius = fr * 0.3f, center = Offset(plant.x, tipY))
                }
            }
        }
    }

    // Floating sparkles
    if (p > 0.75f) {
        val sa = ((p - 0.75f) / 0.25f).coerceAtMost(1f) * 0.5f
        drawCircle(
            color = SunYellow.copy(alpha = sa),
            radius = 2.5f.dp.toPx(),
            center = Offset(w * 0.4f, h * 0.3f + float * 5f),
        )
        drawCircle(
            color = SunYellow.copy(alpha = sa * 0.7f),
            radius = 2.dp.toPx(),
            center = Offset(w * 0.65f, h * 0.22f - float * 3f),
        )
    }
}

// ── Previews ─────────────────────────────────────────────────────────

@Preview(showBackground = true, backgroundColor = 0xFFF5F0E8)
@Composable
private fun PreviewScreen1() {
    OnboardingPageContent(
        page = pages[0],
        pageIndex = 0,
        isLastPage = false,
        isConnecting = false,
        connectionError = null,
        onConnectWallet = {},
    )
}

@Preview(showBackground = true, backgroundColor = 0xFFF5F0E8)
@Composable
private fun PreviewScreen2() {
    OnboardingPageContent(
        page = pages[1],
        pageIndex = 1,
        isLastPage = false,
        isConnecting = false,
        connectionError = null,
        onConnectWallet = {},
    )
}

@Preview(showBackground = true, backgroundColor = 0xFFF5F0E8)
@Composable
private fun PreviewScreen3() {
    OnboardingPageContent(
        page = pages[2],
        pageIndex = 2,
        isLastPage = true,
        isConnecting = false,
        connectionError = null,
        onConnectWallet = {},
    )
}
