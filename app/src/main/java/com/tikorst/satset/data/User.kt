package com.tikorst.satset.data

import com.google.firebase.firestore.PropertyName

data class User (
    @get:PropertyName("email")
    val email: String = "",
    @get:PropertyName("name")
    val name: String = "",
    @get:PropertyName("phone")
    val phone: String = "",
)