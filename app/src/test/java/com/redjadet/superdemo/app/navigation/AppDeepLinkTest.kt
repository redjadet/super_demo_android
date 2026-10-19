package com.redjadet.superdemo.app.navigation

import android.net.Uri
import com.google.common.truth.Truth.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class AppDeepLinkTest {

    @Test
    fun parsesDashboardRisks() {
        val link = AppDeepLink.fromUri(Uri.parse("superdemo://dashboard/risks"))
        assertThat(link).isEqualTo(AppDeepLink.ProductionRisks)
    }

    @Test
    fun parsesFeedPost() {
        val link = AppDeepLink.fromUri(Uri.parse("superdemo://feed/12"))
        assertThat(link).isEqualTo(AppDeepLink.FeedPost(12))
    }

    @Test
    fun rejectsUnknownHost() {
        val link = AppDeepLink.fromUri(Uri.parse("superdemo://unknown"))
        assertThat(link).isNull()
    }

    @Test
    fun rejectsNonPositiveFeedId() {
        val link = AppDeepLink.fromUri(Uri.parse("superdemo://feed/0"))
        assertThat(link).isNull()
    }
}
