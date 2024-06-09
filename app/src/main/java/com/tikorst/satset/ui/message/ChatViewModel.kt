package com.tikorst.satset.ui.message

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Date

class ChatViewModel : ViewModel() {
    private val db = FirebaseFirestore.getInstance()
    private val _adapter = MutableLiveData<FirebaseMessageAdapter>()
    val adapter: LiveData<FirebaseMessageAdapter> get() = _adapter

    fun initAdapter(orderId: String, userId: String) {
        val messagesRef = db.collection("orders").document(orderId).collection("messages").orderBy("timestamp")
        val options = FirestoreRecyclerOptions.Builder<Message>()
            .setQuery(messagesRef, Message::class.java)
            .build()
        _adapter.value = FirebaseMessageAdapter(options, userId)
    }
    fun sendMessage(orderId: String, message: String, userId: String) {
        val messagesRef = db.collection("orders").document(orderId).collection("messages")
        messagesRef.add(
            Message(
                message,
                userId,
                Date().time
            )
        )
    }
}