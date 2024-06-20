package com.tikorst.satset.ui.message

import android.os.Bundle
import android.view.MenuItem
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.tikorst.satset.databinding.ActivityChatBinding

class ChatActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChatBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var firebaseUser: FirebaseUser
    private val viewModel: ChatViewModel by viewModels()
    private var orderId: String? = null
    private var ref: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()
        firebaseUser = auth.currentUser!!
        setup()
        setupViewModel()
        setupRecyclerView()
        binding.sendButton.setOnClickListener{
            val messageText = binding.messageEditText.text.toString()
            viewModel.sendMessage(orderId!!, messageText, firebaseUser.uid)
            binding.messageEditText.setText("")
        }
    }

    private fun setupRecyclerView() {
        val manager = LinearLayoutManager (this, RecyclerView.VERTICAL, false)
        manager.stackFromEnd = true
        binding.messageRecyclerView.layoutManager = manager

        setListeners()
    }

    private fun setupViewModel() {
        viewModel.initAdapter(orderId!!, firebaseUser.uid)
        viewModel.adapter.observe(this, Observer { adapter ->
            binding.messageRecyclerView.adapter = adapter
            adapter.startListening()
        })
    }

    private fun setup() {
        enableEdgeToEdge()
        orderId = intent.getStringExtra("orderId")
        ref = intent.getStringExtra("ref")
        supportActionBar?.apply{
            title = "Chat - $ref"
            setDisplayHomeAsUpEnabled(true)
            elevation = 0f
        }
    }

    public override fun onResume() {
        super.onResume()
        viewModel.adapter.value?.startListening()
    }
    public override fun onPause() {
        super.onPause()
        viewModel.adapter.value?.stopListening()
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
        }
        return true
    }
    private fun setListeners() {
        val recyclerView = binding.messageRecyclerView
        val adapter = viewModel.adapter.value!!
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