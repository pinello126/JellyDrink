package com.jellydrink.app.util

import android.content.Context
import android.content.res.Configuration
import java.util.Locale

object LanguagePreference {

    private const val PREFS_NAME = "language_prefs"
    private const val KEY_LANGUAGE = "selected_language"

    fun getStoredTag(context: Context): String =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getString(KEY_LANGUAGE, "") ?: ""

    fun setTag(context: Context, tag: String) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY_LANGUAGE, tag)
            .apply()
    }

    fun getCurrentTag(context: Context): String {
        val stored = getStoredTag(context)
        return stored.ifEmpty {
            context.resources.configuration.locales[0].language
        }
    }

    fun applyLocale(base: Context, tag: String): Context {
        val locale = Locale(tag)
        Locale.setDefault(locale)
        val config = Configuration(base.resources.configuration)
        config.setLocale(locale)
        return base.createConfigurationContext(config)
    }
}
