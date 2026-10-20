package com.redjadet.superdemo.features.items.data

import android.content.Context
import androidx.core.content.edit
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

/** Persists one-time Items seed so emptying the table does not re-seed. */
@Singleton
class ItemSeedStore @Inject constructor(
    @ApplicationContext context: Context,
) {
    private val prefs = context.getSharedPreferences("items_seed", Context.MODE_PRIVATE)

    fun hasSeeded(): Boolean = prefs.getBoolean(KEY, false)

    fun markSeeded() {
        prefs.edit { putBoolean(KEY, true) }
    }

    companion object {
        private const val KEY = "seeded_once"
    }
}
