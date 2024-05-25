package com.tikorst.satset.data

import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

data class Address(
    var id: String? = "",
    val address: String = "",
    val coordinates: GeoPoint? = null,
    val timestamp: Date? = null,
){
    constructor(address: String, coordinates: GeoPoint? = null, timestamp: Date? = null) : this(null, address, coordinates,timestamp )
}