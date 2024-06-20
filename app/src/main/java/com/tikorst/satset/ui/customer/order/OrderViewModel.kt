package com.tikorst.satset.ui.customer.order

import android.net.Uri
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.getField
import com.google.firebase.storage.FirebaseStorage
import com.google.maps.DirectionsApiRequest
import com.google.maps.GeoApiContext
import com.google.maps.PendingResult
import com.google.maps.model.DirectionsResult
import com.google.maps.model.TravelMode
import com.tikorst.satset.data.Address
import com.tikorst.satset.data.DetailAddress
import com.tikorst.satset.data.Order
import com.tikorst.satset.data.Technician
import kotlinx.coroutines.launch
import java.io.File

class OrderViewModel(private val geoApiContext: GeoApiContext) : ViewModel() {
    val db = FirebaseFirestore.getInstance()
    private val _addressList = MutableLiveData<List<Address>>()
    val addressList: LiveData<List<Address>>
        get() = _addressList
    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean>
        get() = _loading
    private val _error = MutableLiveData<Boolean>()
    val error: LiveData<Boolean>
        get() = _error
    private val _order = MutableLiveData<Order?>()
    val order: LiveData<Order?>
        get() = _order
    private val _orderId = MutableLiveData<String>()
    val orderId: LiveData<String>
        get() = _orderId
    private val _address= MutableLiveData<DetailAddress>()
    val address: LiveData<DetailAddress>
        get() = _address
    private var listener: ListenerRegistration? = null
    private var mapListener: ListenerRegistration? = null
    private val _location= MutableLiveData<LatLng?>()
    val location: LiveData<LatLng?>
        get() = _location
    fun loadAddresses(userId: String) {
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
    fun getTech(techId: String, callback: (Technician?) -> Unit) {
        db.collection("technicians").document(techId).get()
            .addOnSuccessListener {
                val technician = it.toObject(Technician::class.java)
                callback(technician)
        }

    }
    private fun saveOrderToFirestore(order: Order) {
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
    fun orderListener(orderId: String) {
        viewModelScope.launch {
            val orderCollection = db.collection("orders").document(orderId)
            listener = orderCollection.addSnapshotListener{ value, error ->
                if(error != null) {
                    _order.value = null
                    return@addSnapshotListener
                }
                if(value == null ) {
                    _order.value = null
                }else{
                    val order = value.toObject(Order::class.java) as Order
                    _order.value = order
                }
            }
        }

    }
    fun getTechnicianLocation(technicianId: String){
        val technicianCollection = db.collection("technicians").document(technicianId)
        mapListener = technicianCollection.addSnapshotListener{ value, error ->
            if(value == null) {
                _location.value = null
                return@addSnapshotListener
            }else{
                _location.value = LatLng(value.getField("latitude")!!, value.getField("longitude")!!)
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
    fun fetchDirections( origin: LatLng, destination: LatLng, callback: PendingResult.Callback<DirectionsResult>) {
        viewModelScope.launch {
            val request = DirectionsApiRequest(geoApiContext)
            request.origin(com.google.maps.model.LatLng(origin.latitude, origin.longitude))
            request.destination(com.google.maps.model.LatLng(destination.latitude, destination.longitude))
            request.mode(TravelMode.DRIVING)

            request.setCallback(object : PendingResult.Callback<DirectionsResult> {
                override fun onResult(result: DirectionsResult?) {
                    callback.onResult(result)
                }

                override fun onFailure(e: Throwable?) {
                    callback.onFailure(e)
                }
            })
        }
    }
    override fun onCleared() {
        super.onCleared()
        listener?.remove()
        mapListener?.remove()
    }

}