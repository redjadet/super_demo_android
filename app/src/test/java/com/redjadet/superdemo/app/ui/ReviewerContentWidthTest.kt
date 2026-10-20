package com.redjadet.superdemo.app.ui

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class ReviewerContentWidthTest {

    @Test
    fun shouldCap_phoneWidth_isFalse() {
        assertThat(shouldCapReviewerContentWidth(411f)).isFalse()
        assertThat(shouldCapReviewerContentWidth(599f)).isFalse()
    }

    @Test
    fun shouldCap_tabletWidth_isTrue() {
        assertThat(shouldCapReviewerContentWidth(600f)).isTrue()
        assertThat(shouldCapReviewerContentWidth(1024f)).isTrue()
    }

    @Test
    fun constants_matchDocumentedCap() {
        assertThat(ReviewerWideBreakpointDp).isEqualTo(600)
        assertThat(ReviewerContentMaxWidthDp).isEqualTo(840)
    }
}
