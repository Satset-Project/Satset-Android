package com.tikorst.satset.message

import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.ViewTreeObserver
import android.view.WindowManager
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.firestore.FirebaseFirestore
import com.tikorst.satset.R
import com.tikorst.satset.databinding.ActivityChatBinding
import java.util.Date

class ChatActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChatBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore
    private lateinit var adapter: FirebaseMessageAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        val firebaseUser = auth.currentUser

        db = FirebaseFirestore.getInstance()
        val orderId = intent.getStringExtra("orderId")
        val ref = intent.getStringExtra("ref")
        supportActionBar?.apply{
            title = "Chat - $ref"
            setDisplayHomeAsUpEnabled(true)
            elevation = 0f
        }
        val messagesRef1 = db.collection("orders").document(orderId!!).collection("messages")
        val messagesRef2 = db.collection("orders").document(orderId!!).collection("messages").orderBy("timestamp")
        binding.sendButton.setOnClickListener{
            val friendlyMessage = Message(
                binding.messageEditText.text.toString(),
                firebaseUser!!.uid,
                Date().time
            )
            messagesRef1.add(friendlyMessage)
            binding.messageEditText.setText("")
        }
        val manager = LinearLayoutManager (this, RecyclerView.VERTICAL, false)
        manager.stackFromEnd = true
        binding.messageRecyclerView.layoutManager = manager
        val options = FirestoreRecyclerOptions.Builder<Message>()
            .setQuery(messagesRef2, Message::class.java)
            .build()
        adapter = FirebaseMessageAdapter(options, firebaseUser?.uid)

        binding.messageRecyclerView.adapter = adapter
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        setListeners()
    }
    public override fun onResume() {
        super.onResume()
        adapter.startListening()
    }
    public override fun onPause() {
        adapter.stopListening()
        super.onPause()
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
        }
        return true
    }
    private fun setListeners() {
        val recyclerView = binding.messageRecyclerView

        recyclerView.addOnLayoutChangeListener { v, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom ->
                if (adapter.itemCount == 0) {
                    recyclerView.postDelayed(Runnable {
                        recyclerView.smoothScrollToPosition(adapter.itemCount)
                    }, 100)
                } else {
                    recyclerView.postDelayed(Runnable {
                        recyclerView.smoothScrollToPosition(adapter.itemCount - 1)
                    }, 100)
                }

        }
    }
}