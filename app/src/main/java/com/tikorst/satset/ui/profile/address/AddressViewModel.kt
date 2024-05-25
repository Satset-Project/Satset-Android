package com.tikorst.satset.ui.profile.address

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.tikorst.satset.data.Address
import com.tikorst.satset.data.DetailAddress
import kotlinx.coroutines.launch

class AddressViewModel : ViewModel() {

    private val _addressList = MutableLiveData<List<Address>>()
    val addressList: LiveData<List<Address>>
        get() = _addressList

     fun loadAddresses(userId: String) {
         viewModelScope.launch{
             val db = FirebaseFirestore.getInstance()
             val addressesCollection = db.collection("users")
                 .document(userId)
                 .collection("addresses")
                 .orderBy("timestamp")
             addressesCollection.get()
                 .addOnSuccessListener { querySnapshot ->
                     val addresses = mutableListOf<Address>()
                     for (document in querySnapshot.documents) {
                         val address = document.toObject(DetailAddress::class.java)
                         if (address != null) {
                             addresses.add(Address(document.id, address))
                         }

                     }
                     Log.d("AddressViewModel", "Loading addresses for user1 $addresses")
                     _addressList.value = addresses

                 }
                 .addOnFailureListener { e ->
                     println("Error getting addresses: $e")
                 }
         }

    }

    data class User(val addresses: List<Address> = listOf())
}