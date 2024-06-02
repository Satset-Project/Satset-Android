package com.tikorst.satset.data



import android.os.Parcelable
import com.google.firebase.firestore.GeoPoint
import com.google.type.LatLng
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue
import java.util.Date

@Parcelize
data class Addresses(
    val addresses: List<Address> = emptyList()
) : Parcelable
@Parcelize
data class Address(
    var id: String? = "",
    var detailAddress: DetailAddress? = null,
) : Parcelable
@Parcelize
data class DetailAddress(
    val label: String = "",
    val address: String = "",
    val generatedAddress: String? = null,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val timestamp: Date? = null,
): Parcelable


