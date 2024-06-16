package com.tikorst.satset.ui.customer.order

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.maps.GeoApiContext
import com.tikorst.satset.data.UserPreference
import com.tikorst.satset.di.Injection
import com.tikorst.satset.ui.technician.MainTechnicianViewModel

class OrderViewModelFactory(private val geoApiContext: GeoApiContext) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(OrderViewModel::class.java) -> {
                OrderViewModel(geoApiContext) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: OrderViewModelFactory? = null
        @JvmStatic
        fun getInstance(context: Context): OrderViewModelFactory {
            if (INSTANCE == null) {
                synchronized(OrderViewModelFactory::class.java) {
                    INSTANCE = OrderViewModelFactory(Injection.provideGeoAPi())
                }
            }
            return INSTANCE as OrderViewModelFactory
        }
    }
}