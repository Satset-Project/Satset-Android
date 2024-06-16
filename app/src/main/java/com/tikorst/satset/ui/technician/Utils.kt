package com.tikorst.satset.ui.technician

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
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
import com.google.android.gms.maps.model.RoundCap
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

    fun driverHasPassed(driverLocation: LatLng, point: LatLng): Boolean {
        val distance = calculateDistance(driverLocation, point)
        Log.d("distance", distance.toString())

        return distance <= 15
    }
    fun calculateDistance(point1: LatLng, point2: LatLng): Float {
        val results = FloatArray(1)
        Location.distanceBetween(point1.latitude, point1.longitude, point2.latitude, point2.longitude, results)
        return results[0]
    }
    fun getRemainingRoute(userLocation: LatLng, routePoints: List<LatLng>): PolylineOptions {

        val nearestPointIndex = findNearestPointIndex(userLocation, routePoints)

        val remainingRoutePoints = routePoints.subList(nearestPointIndex, routePoints.size)

        return PolylineOptions()
            .addAll(remainingRoutePoints)
            .color(Color.BLUE)
            .width(20f)
            .jointType(2)
            .startCap(RoundCap())
            .endCap(RoundCap())

    }
    private fun findNearestPointIndex(userLocation: LatLng, routePoints: List<LatLng>): Int {
        var nearestDistance = Float.MAX_VALUE
        var nearestIndex = -1

        for ((index, point) in routePoints.withIndex()) {
            val results = FloatArray(1)
            Location.distanceBetween(userLocation.latitude, userLocation.longitude, point.latitude, point.longitude, results)
            val distance = results[0]
            if (distance < nearestDistance) {
                nearestDistance = distance
                nearestIndex = index
            }
        }

        return nearestIndex
    }
}


