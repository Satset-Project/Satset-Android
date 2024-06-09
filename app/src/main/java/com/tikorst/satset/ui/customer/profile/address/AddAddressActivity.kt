package com.tikorst.satset.ui.customer.profile.address

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat
import android.Manifest
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.location.Geocoder
import android.location.Location
import android.os.Build
import android.text.Editable
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.tikorst.satset.databinding.ActivityAddAddressBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.ktx.Firebase
import com.tikorst.satset.R
import com.tikorst.satset.data.Address
import com.tikorst.satset.data.DetailAddress
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Date

class AddAddressActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityAddAddressBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var selectedLocation: LatLng? = null
    private lateinit var auth: FirebaseAuth
    private var detectedAddress: String = "No address detected"
    private var editAddress: Address? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAddAddressBinding.inflate(layoutInflater)
        setContentView(binding.root)
        enableEdgeToEdge()
        supportActionBar?.hide()
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)


        auth = Firebase.auth
        val userId = auth.currentUser?.uid
        binding.buttonSave.setOnClickListener {
            if (userId != null) {
                if(editAddress != null){
                    updateAddress(userId)
                }else{
                    saveAddress(userId)
                }
            }
        }
        binding.locationIcon.setOnClickListener{
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
            requestLocationPermission()
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
                getUserLocation()
            }
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    private fun requestLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        } else {
            getUserLocation()
        }
    }
    private fun setData() {
        editAddress?.let {
            binding.edLabel.text = Editable.Factory.getInstance().newEditable(it.detailAddress?.label)
            binding.edAddress.text = Editable.Factory.getInstance().newEditable(it.detailAddress?.address)
            selectedLocation = LatLng(it.detailAddress?.latitude!!, it.detailAddress?.longitude!!)
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(selectedLocation!!, 15f))
        }
    }
    private fun updateAddress(userId: String) {
        val address = binding.edAddress.text.toString()
        val label = binding.edLabel.text.toString()

        if (address.isNotEmpty() && selectedLocation != null) {
            val alamat = DetailAddress( label,address, detectedAddress, selectedLocation!!.latitude, selectedLocation!!.longitude, Date())

            val db = FirebaseFirestore.getInstance()
            val addressDocRef   = db.collection("users").document(userId).collection("addresses").document(editAddress?.id!!)
            addressDocRef.set(alamat)
                .addOnSuccessListener { documentReference ->
                    Toast.makeText(baseContext, "Address Updated!",
                        Toast.LENGTH_LONG).show()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(baseContext, "Failed Updating Address!",
                        Toast.LENGTH_LONG).show()
                }
            finish()
        } else {
            // Handle validation error
        }
    }
    private fun getUserLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return
        }
        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            location?.let {
                val userLatLng = LatLng(it.latitude, it.longitude)
                selectedLocation = userLatLng
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLatLng, 15f))
//                mMap.addMarker(MarkerOptions().position(userLatLng))
            }
        }
        binding.locationIcon.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.baseline_my_location_24))
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                getUserLocation()
            }
        }
    }
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        setMapStyle(isDarkTheme())
        // Add a marker in Sydney and move the camera
        val defaultLocation = LatLng(-7.790913040144507, 110.36846778178509)
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, 10f))
        selectedLocation = defaultLocation
        mMap.setOnCameraIdleListener {
            val target = mMap.cameraPosition.target
            lifecycleScope.launch(Dispatchers.IO) {
                try {
                    val addresses = Geocoder(this@AddAddressActivity).getFromLocation(target.latitude, target.longitude, 1)
                    val subDistrict = addresses?.getOrNull(0)?.subLocality + ", " + addresses?.getOrNull(0)?.locality + ", " + addresses?.getOrNull(0)?.adminArea
                    Log.d(target.toString(), subDistrict)
                    launch(Dispatchers.Main) {
                        if (!subDistrict.isNullOrEmpty()) {
                            binding.detectedAddress.text = subDistrict
                            detectedAddress = subDistrict
                        } else {
                            binding.detectedAddress.text = "No address detected"
                            detectedAddress = "No address detected"
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    launch(Dispatchers.Main) {
                        binding.detectedAddress.text = "Error: ${e.message}"
                    }
                }
            }
            selectedLocation = target

        }
        if (intent.hasExtra("EDIT_ADDRESS")) {
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
                editAddress = intent.getParcelableExtra("EDIT_ADDRESS", Address::class.java)
            }else{
                editAddress = intent.getParcelableExtra("EDIT_ADDRESS")
            }
            setData()
            binding.locationIcon.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.baseline_location_searching_24))
        }else{
            fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
            requestLocationPermission()
            // Get user location if permission is granted
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
                getUserLocation()
            }
        }
        mMap.setOnCameraMoveListener {
            binding.detectedAddress.text = "Fetching address..."
        }
        mMap.setOnCameraMoveStartedListener{
            if(it == GoogleMap.OnCameraMoveStartedListener.REASON_GESTURE){
                binding.locationIcon.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.baseline_location_searching_24))
            }
        }
    }
    private fun saveAddress(userId: String) {
        val address = binding.edAddress.text.toString()
        val label = binding.edLabel.text.toString()

        if (address.isNotEmpty() && selectedLocation != null) {
            val alamat = DetailAddress(label,address, detectedAddress, selectedLocation!!.latitude, selectedLocation!!.longitude, Date())
            saveUserAddresses(userId, alamat)
        } else {
            // Handle validation error
        }
    }

    private fun saveUserAddresses(userId: String, addresses: DetailAddress) {
        val db = FirebaseFirestore.getInstance()
        val addressesCollection  = db.collection("users").document(userId).collection("addresses")
        addressesCollection.add(addresses)
            .addOnSuccessListener { documentReference ->
                Toast.makeText(baseContext, "Address Saved!",
                    Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(baseContext, "Failed Saving Address!",
                    Toast.LENGTH_LONG).show()
            }
        finish()
    }
    private fun setMapStyle(darkTheme: Boolean) {
        val styleResId = if (darkTheme) R.raw.map_style_dark else R.raw.map_style_light
        mMap.setMapStyle(
            MapStyleOptions.loadRawResourceStyle(
                this, styleResId
            )
        )
    }
    private fun isDarkTheme(): Boolean {
        return resources.configuration.uiMode and
                Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES
    }
    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }
}