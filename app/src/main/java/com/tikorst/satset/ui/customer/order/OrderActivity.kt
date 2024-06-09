package com.tikorst.satset.ui.customer.order

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.tikorst.satset.MainActivity
import com.tikorst.satset.R
import com.tikorst.satset.data.Address
import com.tikorst.satset.data.Addresses
import com.tikorst.satset.data.Order
import com.tikorst.satset.databinding.ActivityOrderBinding
import java.util.Date


class OrderActivity : AppCompatActivity(), AddressFragment.AddressListener,
    MediaSelectFragment.MediaListener {
    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityOrderBinding
    private lateinit var viewModel: OrderViewModel
    private var currentImageUri: Uri? = null
    private  var address: List<Address> = emptyList()
    private var addressId: String? = null
    private var serviceType: String? = null
    private var currentFragment: Int = 0
    private var orderId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setup()
        binding = ActivityOrderBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        setupViewModel()
        setupFirebase()
        setupAddress()
        setupImage()
        order()
        loading(true)

    }

    private fun order() {
        binding.orderButton.setOnClickListener{
            loading(true)
            val currentUser = auth.currentUser
            currentUser?.let {
                val imageFile = uriToFile(currentImageUri!!, this).reduceFileImage()
                val order = Order(
                    userId = it.uid,
                    addressId = addressId,
                    description = binding.descriptionEditText.text.toString(),
                    status = "Pending",
                    serviceType = serviceType,
                    timestamp = Date()
                )
                viewModel.order(order, imageFile)
            }
        }
    }

    private fun setupImage() {
        binding.previewImageView.setOnClickListener {
            val fragment = MediaSelectFragment()
            fragment.show(supportFragmentManager, fragment.tag)
        }
    }

    private fun setupAddress() {
        binding.addressLayout.setOnClickListener {
            val fragment = AddressFragment.newInstance(Addresses(address))
            fragment.show(supportFragmentManager, fragment.tag)
        }
    }

    private fun setupFirebase() {
        auth = Firebase.auth
        val currentUser = auth.currentUser
        currentUser?.let {
            viewModel.loadAddresses(it.uid)
        }
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(this).get(OrderViewModel::class.java)
        viewModel.loading.observe(this){
            loading(it)
        }
        viewModel.orderId.observe(this){
            Log.d("OrderActivity", "Order id: $it")
            val intent = Intent(this, OrderViewActivity::class.java)
            intent.putExtra("order_id", it)
            intent.putExtra("service", serviceType)
            intent.putExtra("tag", "OrderActivity")
            startActivity(intent)

        }
        viewModel.addressList.observe(this){
            if(it.isEmpty()){
                binding.apply {
                    label.text = "No address"
                    address.text = "Please Input address first"
                }
            }else{
                Log.d("AddressActivity", "Address list: $it")
                address = it
                address[0].let {
                    addressId = it.id
                    val formattedAddress = getString(R.string.detail_address, it.detailAddress?.address, it.detailAddress?.generatedAddress)
                    binding.apply {
                        label.text = it.detailAddress?.label
                        address.text = formattedAddress
                    }
                }

            }
        }
    }

    private fun loading(loading: Boolean?): Boolean? {
        if(loading == true) {
            binding.addressView.visibility = View.GONE
            binding.progressBar.visibility = View.VISIBLE
            binding.progressBarMain.visibility = View.VISIBLE
        } else {
            binding.addressView.visibility = View.VISIBLE
            binding.progressBar.visibility = View.GONE
            binding.progressBarMain.visibility = View.GONE
        }
        return loading
    }

    private fun setup() {
        enableEdgeToEdge()
        val intent = intent
        serviceType = intent.getStringExtra("service")
        supportActionBar?.apply{
            title = "Order - $serviceType"
            setDisplayHomeAsUpEnabled(true)
            elevation = 0f
        }
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
        }
        return true
    }

    override fun onItemSelected(item: Address) {
        item.let {
            val formattedAddress = getString(R.string.detail_address, it.detailAddress?.address, it.detailAddress?.generatedAddress)
            binding.apply {
                addressId = it.id
                label.text = it.detailAddress?.label
                address.text = formattedAddress
            }
        }
    }
    private fun startGallery() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            currentImageUri = uri
            showImage()
        }
    }
    private fun showImage() {
        currentImageUri?.let {
            binding.previewImageView.setImageURI(it)
        }
    }

    override fun onMediaSelected(item: Int) {
        if(item == 0){
            startCamera()
        }else{
            startGallery()
        }
    }
    private fun startCamera() {
        if(checkPermission(Manifest.permission.CAMERA)){
            currentImageUri = getImageUri(this)
            launcherIntentCamera.launch(currentImageUri)
        }else{
            requestPermissionLauncher.launch(
                Manifest.permission.CAMERA
            )
        }

    }
    private fun checkPermission(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }
    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { isSuccess ->
        if (isSuccess) {
            showImage()
        }
    }
    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) {
            if (it) {
                startCamera()
            }
        }
    private fun navigateToHomeFragment() {
        // Pop the back stack until reaching the HomeFragment
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }

    override fun onResume() {
        super.onResume()
        currentFragment = 0
    }
}