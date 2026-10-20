package com.redjadet.superdemo.app.navigation

import androidx.annotation.StringRes
import com.redjadet.superdemo.R

enum class AppTab(val route: String, @param:StringRes val labelResId: Int) {
    Dashboard("dashboard", R.string.tab_dashboard),
    Items("items", R.string.tab_items),
    Feed("feed", R.string.tab_feed),
}
