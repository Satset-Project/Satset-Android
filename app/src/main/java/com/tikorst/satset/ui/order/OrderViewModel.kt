package com.tikorst.satset.ui.order

import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.getField
import com.google.firebase.storage.FirebaseStorage
import com.tikorst.satset.data.Address
import com.tikorst.satset.data.DetailAddress
import com.tikorst.satset.data.Order
import kotlinx.coroutines.launch
import java.io.File

class OrderViewModel : ViewModel() {
    private val _addressList = MutableLiveData<List<Address>>()
    val addressList: LiveData<List<Address>>
        get() = _addressList
    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean>
        get() = _loading
    private val _error = MutableLiveData<Boolean>()
    val error: LiveData<Boolean>
        get() = _error
    private val _order = MutableLiveData<Order>()
    val order: LiveData<Order>
        get() = _order
    private val _orderId = MutableLiveData<String>()
    val orderId: LiveData<String>
        get() = _orderId
    private val _address= MutableLiveData<DetailAddress>()
    val address: LiveData<DetailAddress>
        get() = _address
    fun loadAddresses(userId: String) {
        val db = FirebaseFirestore.getInstance()
        _loading.value = true
        viewModelScope.launch{
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
        _loading.value = false
    }
    fun order(order: Order, image: File) {
        val db = FirebaseFirestore.getInstance()
        val storage = FirebaseStorage.getInstance()
        val storageRef = storage.reference
        _loading.value = true
        viewModelScope.launch {

            val imageUri = Uri.fromFile(image)
            val imagesRef = storageRef.child("images/${image.name}")
            val uploadTask = imagesRef.putFile(imageUri)

            uploadTask.addOnSuccessListener { taskSnapshot ->
                imagesRef.downloadUrl.addOnSuccessListener { uri ->
                    val orderWithImageUrl = order.copy(imageUrl = uri.toString())
                    saveOrderToFirestore(orderWithImageUrl)
                }.addOnFailureListener {
                    _error.value = true
                    _loading.value = false
                }
            }.addOnFailureListener {
                _error.value = true
                _loading.value = false
            }

        }

    }

    private fun saveOrderToFirestore(order: Order) {
        val db = FirebaseFirestore.getInstance()
        val orderCollection = db.collection("orders")

        orderCollection.add(order)
            .addOnSuccessListener {documentReference ->
                _error.value = false
                _orderId.value = documentReference.id
                _loading.value = false
            }
            .addOnFailureListener {
                _error.value = true
                _loading.value = false
            }
    }
    fun checkOrderStatus(orderId: String) {
        viewModelScope.launch {
            val db = FirebaseFirestore.getInstance()
            val orderCollection = db.collection("orders").document(orderId)
            orderCollection
                .get()
                .addOnSuccessListener {
                    val order = it.toObject(Order::class.java) as Order
                    _order.value = order
                }
        }

    }
    fun getAddress(addressId: String, userId: String) {
        viewModelScope.launch {
            val db = FirebaseFirestore.getInstance()
            val addressCollection = db.collection("users")
                .document(userId)
                .collection("addresses")
                .document(addressId)
            addressCollection
                .get()
                .addOnSuccessListener {documentSnapshot ->
                    val address = documentSnapshot.toObject(DetailAddress::class.java) as DetailAddress
                    _address.value = address
                }
        }

    }

}