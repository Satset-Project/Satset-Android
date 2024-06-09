package com.tikorst.satset.ui.customer.order

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.Polyline
import com.google.android.gms.maps.model.PolylineOptions
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.maps.GeoApiContext
import com.google.maps.PendingResult
import com.google.maps.model.DirectionsResult
import com.tikorst.satset.MainActivity
import com.tikorst.satset.R
import com.tikorst.satset.data.DetailAddress
import com.tikorst.satset.data.Order
import com.tikorst.satset.databinding.ActivityOrderViewBinding
import com.tikorst.satset.ui.message.ChatActivity
import com.tikorst.satset.ui.technician.Utils

class OrderViewActivity : AppCompatActivity(), OnMapReadyCallback {
    private var order_id: String? = null
    private var tag: String? = null
    private var serviceType: String? = null
    private lateinit var hourglassBottom: ImageView
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>
    private lateinit var viewModel: OrderViewModel
    private lateinit var binding: ActivityOrderViewBinding
    private lateinit var mMap: GoogleMap
    private var address: DetailAddress? = null
    private var routePolyline: Polyline? = null
    private var completeRoutePoints = mutableListOf<LatLng>()
    private var userMarker: Marker? = null
    private val boundsBuilder = LatLngBounds.Builder()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setup()
        binding = ActivityOrderViewBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        hourglassBottom = binding.hourglassBottom
        viewModelSetup()
        hourglassBottom = binding.hourglassBottom
        val bottomsheet: LinearLayout = binding.bottomSheet
        bottomSheetBehavior = BottomSheetBehavior.from(bottomsheet)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        bottomSheetBehavior.peekHeight = 300
        binding.chatImageView.setOnClickListener {
            val intent = Intent(this, ChatActivity::class.java)
            intent.putExtra("orderId", order_id)
            intent.putExtra("ref", binding.technicianName.text.toString())
            startActivity(intent)
        }
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    private fun getTech(techId: String?) {
        viewModel.getTech(techId!!){
            if(it == null){
                binding.technicianContainer.visibility = View.GONE
            }else{
                binding.technicianContainer.visibility = View.VISIBLE
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
                binding.technicianName.text = it.name
                binding.rating.text = it.rating.toString()
            }

        }
    }


    private fun setup() {
        val intent = intent
        order_id = intent.getStringExtra("order_id")
        tag = intent.getStringExtra("tag")
        serviceType = intent.getStringExtra("service")
        enableEdgeToEdge()
        supportActionBar?.apply{
            title = "Order - $serviceType"
            setDisplayHomeAsUpEnabled(true)
            elevation = 0f
        }
    }

    private fun startHourglassAnimation() {
        val rotateBottom = ObjectAnimator.ofFloat(hourglassBottom, "rotation", 0f, 180f)
        rotateBottom.duration = 2000
        val animatorSet = AnimatorSet()
        animatorSet.play(rotateBottom)
        animatorSet.startDelay = 1000
        animatorSet.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {
            }

            override fun onAnimationEnd(animation: Animator) {
                startHourglassAnimation()
            }

            override fun onAnimationCancel(animation: Animator) {
            }
            override fun onAnimationRepeat(animation: Animator) {
            }
        })
        animatorSet.start()
    }
    private fun viewModelSetup() {
        viewModel = ViewModelProvider(this).get(OrderViewModel::class.java)
        viewModel.order.observe(this){
            if(it == null){
                return@observe
            }else{
                if(it.status == "Pending"){
                    hourglassBottom.visibility = View.VISIBLE
                    binding.map.visibility = View.GONE
                    binding.textView.visibility = View.VISIBLE
                    startHourglassAnimation()
                    binding.technicianContainer.visibility = View.GONE
                }else{
                    getTech(it.technicianId!!)
                    viewModel.getTechnicianLocation(it.technicianId)
                    hourglassBottom.clearAnimation()
                    hourglassBottom.visibility = View.GONE
                    binding.map.visibility = View.VISIBLE
                    binding.textView.visibility = View.GONE
                }
                viewModel.getAddress(it.addressId!!, it.userId!!)

                binding.descriptionTextView.text = it.description
            }

        }
        viewModel.orderListener(order_id!!)
        viewModel.address.observe(this){
            address = it
            mMap.addMarker(
                MarkerOptions()
                    .position(LatLng(it.latitude!!, it.longitude!!))
                    .title("Destination"))
            binding.label.text = it.label
            val formattedAddress = getString(R.string.detail_address, it.address, it.generatedAddress)
            binding.address.text = formattedAddress
        }

    }

    override fun onPause() {
        super.onPause()
        hourglassBottom.clearAnimation()
    }
    private fun navigateToHomeFragment() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            if(tag == "OrderActivity"){
                navigateToHomeFragment()
            }else{
                finish()
            }
        }
        return true
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        setMapStyle(isDarkTheme())
        viewModel.location.observe(this){techLocation ->
            if(techLocation == null){
                return@observe
            }else{
                val geoApiContext = GeoApiContext.Builder()
                    .apiKey("")
                    .build()
                val callback = object : PendingResult.Callback<DirectionsResult> {
                    override fun onResult(result: DirectionsResult?) {
                        result?.routes?.let { routes ->
                            for (route in routes) {
                                val points = mutableListOf<LatLng>()
                                route.legs.forEach { leg ->
                                    leg.steps.forEach { step ->
                                        step.polyline.decodePath().forEach { point ->
                                            points.add(LatLng(point.lat, point.lng))
                                        }
                                    }
                                }
                                completeRoutePoints = points
                                runOnUiThread {
                                    routePolyline?.remove()
                                    updateLocationOnMap(techLocation)
                                    routePolyline = mMap.addPolyline(
                                        PolylineOptions()
                                            .addAll(points)
                                            .color(Color.BLUE)
                                            .width(10f)
                                    )
                                }
                            }
                        }
                    }
                    override fun onFailure(e: Throwable?) {

                    }
                }
                if(address != null){
                    Utils.fetchDirections(geoApiContext,  techLocation,LatLng(address?.latitude!!, address?.longitude!!), callback)
                    boundsBuilder.include(LatLng(address?.latitude!!, address?.longitude!!))
                    boundsBuilder.include(techLocation)
                    val bounds: LatLngBounds = boundsBuilder.build()
                    mMap.animateCamera(
                        CameraUpdateFactory.newLatLngBounds(
                            bounds,
                            resources.displayMetrics.widthPixels,
                            resources.displayMetrics.heightPixels,
                            300
                        )
                    )
                }
            }
        }
    }
    private fun isDarkTheme(): Boolean {
        return resources.configuration.uiMode and
                Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES
    }
    private fun setMapStyle(darkTheme: Boolean) {
        val styleResId = if (darkTheme) R.raw.map_style_dark else R.raw.map_style_light
        mMap.setMapStyle(
            MapStyleOptions.loadRawResourceStyle(
                this, styleResId
            )
        )
    }
    private fun updateLocationOnMap(userLatLng: LatLng) {
        if (userMarker == null) {
            userMarker = mMap.addMarker(
                MarkerOptions()
                    .position(userLatLng)
                    .title("User Marker")
                    .icon(Utils.vectorToBitmap(R.drawable.baseline_directions_car_24, getColor(R.color.md_theme_primary), resources)))
        } else {
            userMarker?.position = userLatLng
        }
    }
}