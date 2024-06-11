package com.tikorst.satset.ui.technician

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.tikorst.satset.data.Address
import com.tikorst.satset.data.DetailAddress
import com.tikorst.satset.data.Order
import com.tikorst.satset.data.OrderID
import com.tikorst.satset.data.User
import com.tikorst.satset.data.UserPreference
import kotlinx.coroutines.launch

class MainTechnicianViewModel(private val userPreference: UserPreference) : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val _order = MutableLiveData<OrderID?>()
    val order: LiveData<OrderID?>
        get() = _order
    private val _address= MutableLiveData<DetailAddress>()
    val address: LiveData<DetailAddress>
        get() = _address
    private var listener: ListenerRegistration? = null
    fun orderListener(userId: String) {
        viewModelScope.launch {
            val orderCollection = db.collection("orders").whereEqualTo("technicianId", userId).whereNotEqualTo("status", "completed")
            listener = orderCollection.addSnapshotListener{ value, error ->
                if(error != null) {
                    _order.value = null
                    return@addSnapshotListener
                }
                if(value == null || value.isEmpty) {
                    _order.value = null
                }else{
                    for (document in value.documents) {
                        val orders = document.toObject(Order::class.java) as Order
                        _order.value = OrderID(document.id, orders)
                    }
                }
            }
        }
    }
    fun getAddress(addressId: String, userId: String) {
        viewModelScope.launch {
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
            val orderCollection = db.collection("orders").document(orderId)
            orderCollection
                .get()
                .addOnSuccessListener {
                    val orders = it.toObject(Order::class.java) as Order
                    _order.value = OrderID(it.id, orders)
                }
        }
    }
    fun getCus(userId: String?,callback: (User?) -> Unit) {
        db.collection("users").document(userId!!).get()
            .addOnSuccessListener {
                Log.d("user aaaaaaaaaaaa", it.toString())
                val user = it.toObject(User::class.java) as User
                callback(user)
            }
            .addOnFailureListener {
                callback(null)
            }
    }
    override fun onCleared() {
        super.onCleared()
        listener?.remove()
    }
    fun logout() {
        viewModelScope.launch {
            userPreference.logout()
        }
    }
    fun locationUpdate(technicianId: String, latitude: Double, longitude: Double) {
        viewModelScope.launch {
            db.collection("technicians").document(technicianId).update("latitude", latitude, "longitude", longitude)
        }
    }
    fun finishOrder(orderId: String) {
        viewModelScope.launch {
            db.collection("orders").document(orderId).update("status", "completed")
        }
    }
}