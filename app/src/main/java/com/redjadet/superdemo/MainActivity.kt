package com.redjadet.superdemo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.redjadet.superdemo.app.navigation.AppDeepLink
import com.redjadet.superdemo.app.navigation.AppRoot
import com.redjadet.superdemo.app.ui.theme.SuperDemoTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private var onNewDeepLink: ((AppDeepLink?) -> Unit)? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val initialLink = AppDeepLink.fromIntent(intent)
        setContent {
            SuperDemoTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    AppRoot(
                        initialDeepLink = initialLink,
                    ) { consumer ->
                        onNewDeepLink = consumer
                    }
                }
            }
        }
        addOnNewIntentListener { newIntent ->
            intent = newIntent
            onNewDeepLink?.invoke(AppDeepLink.fromIntent(newIntent))
        }
    }

    /** Instrumentation helper: deliver a deep link without replacing the launch Intent. */
    fun deliverDeepLinkForTest(link: AppDeepLink?) {
        onNewDeepLink?.invoke(link)
    }
}
