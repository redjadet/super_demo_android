package com.redjadet.superdemo.app.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp

/** Phone layouts stay full-bleed; wider windows cap primary content. */
const val ReviewerWideBreakpointDp = 600
const val ReviewerContentMaxWidthDp = 840

fun shouldCapReviewerContentWidth(maxWidthDp: Float): Boolean =
    maxWidthDp >= ReviewerWideBreakpointDp

/**
 * Keeps primary content readable on tablets / wide windows by capping max width
 * while remaining full-bleed on phones.
 */
@Composable
fun ReviewerContentWidth(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .testTag("reviewerContentWidth"),
    ) {
        val wide = shouldCapReviewerContentWidth(this.maxWidth.value)
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxHeight()
                .then(
                    if (wide) {
                        Modifier
                            .widthIn(max = ReviewerContentMaxWidthDp.dp)
                            .fillMaxWidth()
                    } else {
                        Modifier.fillMaxWidth()
                    },
                ),
        ) {
            content()
        }
    }
}
