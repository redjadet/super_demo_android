package com.redjadet.superdemo

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/**
 * Remote is forced to fail via [com.redjadet.superdemo.di.OfflineNetworkTestModule].
 * Feed must still render Room-seeded posts and a stale/local banner.
 */
@HiltAndroidTest
class OfflineFeedInstrumentationTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun setUp() {
        hiltRule.inject()
    }

    @Test
    fun feedTab_showsSeededLocalPostsAndStaleBannerWhenRemoteFails() {
        composeRule.onNodeWithTag("feedTab").performClick()
        composeRule.waitUntil(timeoutMillis = 20_000) {
            composeRule.onAllNodesWithTag("feedList").fetchSemanticsNodes().isNotEmpty()
        }
        composeRule.onNodeWithTag("feedList").assertIsDisplayed()
        composeRule.onNodeWithText("Offline-first Feed cache", substring = true).assertIsDisplayed()
        composeRule.waitUntil(timeoutMillis = 20_000) {
            composeRule.onAllNodesWithTag("feedStaleBanner").fetchSemanticsNodes().isNotEmpty()
        }
        composeRule.onNodeWithTag("feedStaleBanner").assertIsDisplayed()
        composeRule
            .onNodeWithText("Couldn’t refresh. Showing saved posts.")
            .assertIsDisplayed()
    }
}
