package com.tikorst.satset.technician

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.tikorst.satset.data.Address
import com.tikorst.satset.data.DetailAddress
import com.tikorst.satset.data.Order
import com.tikorst.satset.data.OrderID
import kotlinx.coroutines.launch

class MainTechnicianViewModel : ViewModel() {
    private val _order = MutableLiveData<OrderID?>()
    val order: LiveData<OrderID?>
        get() = _order
    private val _address= MutableLiveData<DetailAddress>()
    val address: LiveData<DetailAddress>
        get() = _address
    fun getOrders(userId: String) {
        viewModelScope.launch {
            val db = FirebaseFirestore.getInstance()
            val orderCollection = db.collection("orders").whereEqualTo("technicianId", userId).whereNotEqualTo("status", "completed")
            orderCollection.get()
                .addOnSuccessListener { querySnapshot ->
                    if(querySnapshot.isEmpty) {
                        _order.value = null
                    }else{
                        for (document in querySnapshot.documents) {
                            val orders = document.toObject(Order::class.java) as Order
                            _order.value = OrderID(document.id, orders)
                        }
                    }

                }
                .addOnFailureListener { e ->
                    println("Error getting orders: $e")
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
    fun checkOrderStatus(orderId: String) {
        viewModelScope.launch {
            val db = FirebaseFirestore.getInstance()
            val orderCollection = db.collection("orders").document(orderId)
            orderCollection
                .get()
                .addOnSuccessListener {
                    val orders = it.toObject(Order::class.java) as Order
                    _order.value = OrderID(it.id, orders)
                }
        }

    }
}