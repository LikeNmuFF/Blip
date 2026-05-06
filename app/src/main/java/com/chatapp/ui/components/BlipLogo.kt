package com.chatapp.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.chatapp.R

/**
 * Reusable Blip Logo component with optional animation
 * @param size The size of the logo
 * @param animated Whether to apply a scale animation on load
 * @param modifier Additional modifier for the Box containing the logo
 */
@Composable
fun BlipLogo(
    size: Dp = 80.dp,
    animated: Boolean = false,
    modifier: Modifier = Modifier
) {
    val isAnimating = remember { mutableStateOf(animated) }
    val animationProgress by animateFloatAsState(
        targetValue = if (isAnimating.value) 1f else 0f,
        animationSpec = tween(durationMillis = 600),
        label = "LogoAnimation"
    )

    // Scale from 0.8 to 1.0 during animation
    val scale = 0.8f + (0.2f * animationProgress)

    Box(
        modifier = modifier
            .size(size)
            .scale(scale)
            .alpha(animationProgress),
        contentAlignment = Alignment.Center
    ) {
        androidx.compose.foundation.Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "Blip Logo",
            modifier = Modifier.size(size),
            contentScale = ContentScale.Fit
        )
    }
}

/**
 * Compact version of the Blip Logo for headers and top bars
 */
@Composable
fun BlipLogoCompact(
    size: Dp = 40.dp,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center
    ) {
        androidx.compose.foundation.Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "Blip Logo",
            modifier = Modifier.size(size),
            contentScale = ContentScale.Fit
        )
    }
}
