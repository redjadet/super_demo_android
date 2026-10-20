package com.redjadet.superdemo

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.redjadet.superdemo.app.navigation.AppDeepLink
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
class FeedDeepLinkInstrumentationTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun setUp() {
        hiltRule.inject()
    }

    @Test
    fun warmDeepLink_missingPost_showsNotFoundDetail() {
        // Warm start: open Feed so Room seed / refresh can settle.
        composeRule.onNodeWithTag("feedTab").performClick()
        composeRule.waitUntil(timeoutMillis = 20_000) {
            composeRule.onAllNodesWithTag("feedList").fetchSemanticsNodes().isNotEmpty() ||
                composeRule.onAllNodesWithTag("feedStaleBanner").fetchSemanticsNodes().isNotEmpty() ||
                composeRule.onAllNodesWithTag("feedError").fetchSemanticsNodes().isNotEmpty() ||
                composeRule.onAllNodesWithTag("feedEmpty").fetchSemanticsNodes().isNotEmpty()
        }

        // Deliver via activity helper so ActivityScenario keeps its launch Intent
        // (startActivity + setIntent breaks scenario teardown).
        composeRule.runOnUiThread {
            composeRule.activity.deliverDeepLinkForTest(AppDeepLink.FeedPost(999001))
        }

        composeRule.waitUntil(timeoutMillis = 15_000) {
            composeRule.onAllNodesWithTag("feedPostMissing").fetchSemanticsNodes().isNotEmpty()
        }
        composeRule.onNodeWithTag("feedPostMissing").assertIsDisplayed()
        composeRule.onNodeWithText("Post not found.").assertIsDisplayed()
    }
}
