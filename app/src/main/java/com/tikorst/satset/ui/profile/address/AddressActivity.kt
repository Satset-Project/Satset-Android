package com.tikorst.satset.ui.profile.address

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

import com.tikorst.satset.R
import com.tikorst.satset.data.Address

import com.tikorst.satset.databinding.ActivityAddressBinding

class AddressActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAddressBinding
    private lateinit var viewModel: AddressViewModel
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
        supportActionBar?.title = "Addresses"

        binding = ActivityAddressBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val layoutManager = LinearLayoutManager(this)
        binding.recyclerView.layoutManager = layoutManager
        viewModel = ViewModelProvider(this).get(AddressViewModel::class.java)
        viewModel.addressList.observe(this){
            if(it.isEmpty()){
                showEmptyView(true)
            }else{
                Log.d("AddressActivity", "Address list: $it")
                setAddressList(it)
            }
        }
        auth = Firebase.auth
        val currentUser = auth.currentUser
        currentUser?.let {
            loadAddresses(it.uid)
        }
        binding.fabAdd.setOnClickListener {
            val intent = Intent(this, AddAddressActivity::class.java)
            startActivity(intent)
        }
    }

    private fun loadAddresses(userId: String) { // Replace with actual user ID
        viewModel.loadAddresses(userId)
    }
    private fun showEmptyView(value: Boolean) {
        binding.rvEmpty.visibility = if (value) View.VISIBLE else View.GONE
    }
    private fun setAddressList(news: List<Address>?) {
        val adapter = AddressAdapter()
        adapter.submitList(news)
        binding.recyclerView.adapter = adapter
    }

    override fun onResume() {
        super.onResume()
        val currentUser = auth.currentUser
        if (currentUser != null) {
            loadAddresses(currentUser.uid)
        }
    }
}