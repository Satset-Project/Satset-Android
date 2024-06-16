package com.tikorst.satset.di

import android.content.Context
import com.google.maps.GeoApiContext
import com.tikorst.satset.BuildConfig
import com.tikorst.satset.data.UserPreference
import com.tikorst.satset.data.dataStore

object Injection {
    fun providePreferences(context: Context): UserPreference {
        val pref = UserPreference.getInstance(context.dataStore)
        return pref
    }
    fun provideGeoAPi(): GeoApiContext {
        val geoApiContext = GeoApiContext.Builder()
            .apiKey(BuildConfig.MAPS_API_KEY)
            .build()
        return geoApiContext
    }
}