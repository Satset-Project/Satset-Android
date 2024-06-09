package com.tikorst.satset.data

import com.google.firebase.firestore.PropertyName

data class Technician (
    @get:PropertyName("email")
    var email: String = "",
    @get:PropertyName("name")
    var name: String = "",
    @get:PropertyName("phone")
    var phone: String = "",
    @get:PropertyName("rating")
    var rating: Double = 0.0,
    @get:PropertyName("status")
    var status: String = ""
)