package com.example.ui.components

import android.view.HapticFeedbackConstants
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.Spring
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalView

/**
 * Custom tactile press-like animation for automotive head unit buttons.
 * Gives immediate visual depression (scale down to 0.93) with spring bounce
 * and haptic feedback to keep driver focus on the road.
 */
fun Modifier.carPressable(
    onClick: () -> Unit,
    enabled: Boolean = true,
    scaleDownFactor: Float = 0.94f,
    onLongClick: (() -> Unit)? = null
): Modifier = composed {
    var isPressed by remember { mutableStateOf(false) }
    val view = LocalView.current

    val scale by animateFloatAsState(
        targetValue = if (isPressed) scaleDownFactor else 1.0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "car_button_press_scale"
    )

    this
        .graphicsLayer {
            scaleX = scale
            scaleY = scale
            alpha = if (enabled) (if (isPressed) 0.88f else 1.0f) else 0.45f
        }
        .pointerInput(enabled) {
            if (!enabled) return@pointerInput
            while (true) {
                awaitPointerEventScope {
                    awaitFirstDown(requireUnconsumed = false)
                    isPressed = true
                    try {
                        view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                    } catch (_: Exception) {}
                    
                    val up = waitForUpOrCancellation()
                    isPressed = false
                    if (up != null) {
                        try {
                            view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                        } catch (_: Exception) {}
                        onClick()
                    }
                }
            }
        }
}
