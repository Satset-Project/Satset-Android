package com.tikorst.satset.technician

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthProvider
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.tikorst.satset.MainActivity
import com.tikorst.satset.R
import com.tikorst.satset.data.OrderID
import com.tikorst.satset.databinding.ActivityOrderTechnicianBinding
import com.tikorst.satset.message.ChatActivity

class OrderTechnicianActivity : AppCompatActivity() {
    private var order: OrderID? = null
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>
    private lateinit var viewModel: MainTechnicianViewModel
    private lateinit var handler: Handler
    private lateinit var statusCheckRunnable: Runnable
    private lateinit var binding: ActivityOrderTechnicianBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderTechnicianBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setup()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        viewModelSetup()

        binding.chatImageView.setOnClickListener {
            val intent = Intent(this, ChatActivity::class.java)
            intent.putExtra("orderId", order?.orderId)
            intent.putExtra("ref", binding.customerName.text.toString())
            startActivity(intent)
        }
    }

    private fun getCus(userId: String?) {
        val db = FirebaseFirestore.getInstance()
        db.collection("users").document(userId!!).get().addOnSuccessListener {
            val name = it.getString("name")
            val phone = it.getString("phone")
            binding.customerName.text = name
        }

    }


    private fun setup() {
        val intent = intent

        order = if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
            intent.getParcelableExtra("order", OrderID::class.java)
        }else{
            intent.getParcelableExtra("order")
        }
        enableEdgeToEdge()
        supportActionBar?.apply{
            title = "Order - ${order?.order?.serviceType}"
            setDisplayHomeAsUpEnabled(true)
            elevation = 0f
        }
        binding.descriptionTextView.text = order?.order?.description
        Glide.with(this)
            .load(order?.order?.imageUrl)
            .into(binding.orderImageView)
        getCus(order?.order?.userId)
        val bottomsheet: LinearLayout = binding.bottomSheet
        bottomSheetBehavior = BottomSheetBehavior.from(bottomsheet)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        bottomSheetBehavior.peekHeight = 300
    }


    private fun viewModelSetup() {
        handler = Handler()
        viewModel = ViewModelProvider(this).get(MainTechnicianViewModel::class.java)
        viewModel.order.observe(this){

        }
        statusCheckRunnable = object : Runnable {
            override fun run() {
                viewModel.checkOrderStatus(order?.orderId!!)

                handler.postDelayed(this, 3000)
            }
        }
        handler.post(statusCheckRunnable)
        viewModel.address.observe(this){
            binding.label.text = it.label
            val formattedAddress = getString(R.string.detail_address, it.address, it.generatedAddress)
            binding.address.text = formattedAddress
        }

        viewModel.address.observe(this){
            binding.label.text = it.label
            val formattedAddress = getString(R.string.detail_address, it.address, it.generatedAddress)
            binding.address.text = formattedAddress
        }
        viewModel.getAddress(order?.order?.addressId!!, order?.order?.userId!!)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
        }
        return true
    }
}