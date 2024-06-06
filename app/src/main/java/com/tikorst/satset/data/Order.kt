package com.tikorst.satset.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.io.File
import java.util.Date
@Parcelize
data class OrderID(
    val orderId: String? = null,
    val order: Order? = null
) : Parcelable
@Parcelize
data class Order (
    val userId: String? = null,
    val technicianId: String? = null,
    val addressId: String? = null,
    val status: String? = null,
    val description: String? = null,
    val serviceType: String? = null,
    val timestamp: Date? = null,
    val imageUrl: String? = null
    ) : Parcelable