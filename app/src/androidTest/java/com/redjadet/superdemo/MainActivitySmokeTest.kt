package com.redjadet.superdemo

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@HiltAndroidTest
class MainActivitySmokeTest {

    @get:Rule(order = 0)
    val hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun setUp() {
        hiltRule.inject()
    }

    @Test
    fun dashboard_isDefaultTab() {
        composeRule.onNodeWithTag("dashboardHeadline").assertIsDisplayed()
        composeRule.onNodeWithText("Android engineering portfolio").assertIsDisplayed()
    }

    @Test
    fun itemsTab_showsSeededListAndDraftField() {
        composeRule.onNodeWithTag("itemsTab").performClick()
        composeRule.onNodeWithTag("itemsDraft").assertIsDisplayed()
        // Wait for seed + Room Flow to settle (loading → list).
        composeRule.waitUntil(timeoutMillis = 15_000) {
            composeRule.onAllNodesWithTag("itemsList").fetchSemanticsNodes().isNotEmpty()
        }
        composeRule.onNodeWithTag("itemsList").assertIsDisplayed()
        composeRule.onNodeWithText("Room persistence", substring = true).assertIsDisplayed()
    }

    @Test
    fun feedTab_showsListOrLoading() {
        composeRule.onNodeWithTag("feedTab").performClick()
        composeRule.waitUntil(timeoutMillis = 15_000) {
            runCatching {
                composeRule.onNodeWithTag("feedList").assertExists()
                true
            }.getOrElse {
                runCatching {
                    composeRule.onNodeWithTag("feedLoading").assertExists()
                    true
                }.getOrElse {
                    runCatching {
                        composeRule.onNodeWithTag("feedError").assertExists()
                        true
                    }.getOrDefault(false)
                }
            }
        }
    }

    @Test
    fun dashboardRisks_navigatesToRisksList() {
        composeRule.onNodeWithTag("dashboardRisksLink").performClick()
        composeRule.onNodeWithTag("risksList").assertIsDisplayed()
        composeRule.onNodeWithText("Production risks").assertIsDisplayed()
    }
}
