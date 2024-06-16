package com.tikorst.satset.ui.technician

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.maps.GeoApiContext
import com.tikorst.satset.AuthViewModel
import com.tikorst.satset.data.UserPreference
import com.tikorst.satset.di.Injection
import com.tikorst.satset.ui.customer.profile.ProfileViewModel

class TechnicianViewModelFactory(private val userPreference: UserPreference, private val geoApiContext: GeoApiContext) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(MainTechnicianViewModel::class.java) -> {
                MainTechnicianViewModel(userPreference, geoApiContext) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: TechnicianViewModelFactory? = null
        @JvmStatic
        fun getInstance(context: Context): TechnicianViewModelFactory {
            if (INSTANCE == null) {
                synchronized(TechnicianViewModelFactory::class.java) {
                    INSTANCE = TechnicianViewModelFactory(Injection.providePreferences(context), Injection.provideGeoAPi())
                }
            }
            return INSTANCE as TechnicianViewModelFactory
        }
    }
}