package com.tikorst.satset.ui.customer.dashboard

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.tikorst.satset.data.Order
import com.tikorst.satset.data.OrderID
import kotlinx.coroutines.launch

class HistoryViewModel : ViewModel() {
    val db = FirebaseFirestore.getInstance()
    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean>
        get() = _loading
    val _error = MutableLiveData<String>()
    val error: LiveData<String>
        get() = _error
    private val _orders = MutableLiveData<List<OrderID>>()
    val orders: LiveData<List<OrderID>>
        get() = _orders
    private val _history = MutableLiveData<List<OrderID>>()
    val history: LiveData<List<OrderID>>
        get() = _history

    fun loadOrders(userId: String) {
        _loading.value = true

        viewModelScope.launch{
            val ordersCollection = db.collection("orders")
            val query = ordersCollection.whereEqualTo("userId", userId).whereNotEqualTo("status", "completed")
            query.get()
                .addOnSuccessListener { querySnapshot ->
                    val orders = mutableListOf<OrderID>()
                    for (document in querySnapshot.documents) {
                        val order = document.toObject(Order::class.java)
                        if (order != null) {
                            orders.add(OrderID(document.id, order))
                        }
                    }
                    _orders.value = orders
                    _loading.value = false
                }
                .addOnFailureListener { e ->
                    _error.value = "Error getting orders: $e"
                    Log.d("HistoryViewModel", "Error getting orders: $e")
                    _loading.value = false
                }
        }
    }
    fun loadHistory(userId: String) {
        _loading.value = true

        viewModelScope.launch{
            val ordersCollection = db.collection("orders")
            val query = ordersCollection.whereEqualTo("userId", userId).whereEqualTo("status", "completed")
            query.get()
                .addOnSuccessListener { querySnapshot ->
                    val orders = mutableListOf<OrderID>()
                    for (document in querySnapshot.documents) {
                        val order = document.toObject(Order::class.java)
                        if (order != null) {
                            orders.add(OrderID(document.id, order))
                        }
                    }
                    _history.value = orders
                    _loading.value = false
                }
                .addOnFailureListener { e ->
                    _error.value = "Error getting orders: $e"
                    Log.d("HistoryViewModel", "Error getting orders: $e")
                    _loading.value = false
                }
        }
    }
}