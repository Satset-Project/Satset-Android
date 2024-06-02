package com.tikorst.satset.data

import java.io.File
import java.util.Date

data class Order (
    val userId: String? = null,
    val technicianId: String? = null,
    val addressId: String? = null,
    val description: String? = null,
    val timestamp: Date? = null,
    val imageUrl: String? = null
    )