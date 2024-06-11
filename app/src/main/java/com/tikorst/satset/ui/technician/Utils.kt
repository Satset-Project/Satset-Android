package com.tikorst.satset.ui.technician

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Canvas
import android.location.Location
import android.util.Log
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.DrawableCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.PolylineOptions
import com.google.maps.DirectionsApiRequest
import com.google.maps.GeoApiContext
import com.google.maps.PendingResult
import com.google.maps.model.DirectionsResult
import com.google.maps.model.TravelMode
import java.lang.Math.pow
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

object Utils {
    fun vectorToBitmap(@DrawableRes id: Int, @ColorInt color: Int, resources: Resources): BitmapDescriptor {
        val vectorDrawable = ResourcesCompat.getDrawable(resources, id, null)
        if (vectorDrawable == null) {
            Log.e("BitmapHelper", "Resource not found")
            return BitmapDescriptorFactory.defaultMarker()
        }
        val bitmap = Bitmap.createBitmap(
            vectorDrawable.intrinsicWidth,
            vectorDrawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        vectorDrawable.setBounds(0, 0, canvas.width, canvas.height)
        DrawableCompat.setTint(vectorDrawable, color)
        vectorDrawable.draw(canvas)
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }
     fun fetchDirections(geoApiContext: GeoApiContext, origin: LatLng, destination: LatLng, callback: PendingResult.Callback<DirectionsResult>) {
        Log.d("Directions", "Fetching directions")
        val request = DirectionsApiRequest(geoApiContext)
        request.origin(com.google.maps.model.LatLng(origin.latitude, origin.longitude))
        request.destination(com.google.maps.model.LatLng(destination.latitude, destination.longitude))
        request.mode(TravelMode.DRIVING) // You can specify other travel modes here

        // Execute the request asynchronously
        request.setCallback(object : com.google.maps.PendingResult.Callback<DirectionsResult> {
            override fun onResult(result: DirectionsResult?) {
                callback.onResult(result)
            }

            override fun onFailure(e: Throwable?) {
                callback.onFailure(e)
            }
        })
    }
    fun showRouteToDestination(
        fusedLocationClient: FusedLocationProviderClient,
        destination: LatLng,
        context: Context,
        requestCode: Int,
        geoApiContext: GeoApiContext,
        callback: PendingResult.Callback<DirectionsResult>
    ) {
        // Check for location permission and fetch user's current location
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                location?.let {
                    val origin = LatLng(location.latitude, location.longitude)
                    fetchDirections(geoApiContext, origin, destination, callback)

                }
            }
        } else {
            // Request location permission
            ActivityCompat.requestPermissions(
                context as Activity,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
               requestCode
            )
        }
    }

    fun driverHasPassed(driverLocation: LatLng, point: LatLng): Boolean {
        val distance = calculateDistance(driverLocation, point)
        Log.d("distance", distance.toString())

        return distance <= 7
    }
    fun calculateDistance(point1: LatLng, point2: LatLng): Float {
        val results = FloatArray(1)
        Location.distanceBetween(point1.latitude, point1.longitude, point2.latitude, point2.longitude, results)
        return results[0]
    }
}