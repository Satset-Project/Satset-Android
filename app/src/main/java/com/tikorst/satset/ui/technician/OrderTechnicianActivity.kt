package com.tikorst.satset.ui.technician

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.firebase.firestore.FirebaseFirestore
import com.tikorst.satset.R
import com.tikorst.satset.ViewModelFactory
import com.tikorst.satset.data.OrderID
import com.tikorst.satset.databinding.ActivityOrderTechnicianBinding
import com.tikorst.satset.ui.message.ChatActivity

class OrderTechnicianActivity : AppCompatActivity() {
    private var order: OrderID? = null
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>
    private val viewModel by viewModels<MainTechnicianViewModel> {
        ViewModelFactory.getInstance(this)
    }
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