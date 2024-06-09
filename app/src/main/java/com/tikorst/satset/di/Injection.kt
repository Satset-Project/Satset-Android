package com.tikorst.satset.di

import android.content.Context
import com.tikorst.satset.data.UserPreference
import com.tikorst.satset.data.dataStore

object Injection {
    fun providePreferences(context: Context): UserPreference {
        val pref = UserPreference.getInstance(context.dataStore)
        return pref
    }
}