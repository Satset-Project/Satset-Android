package com.tikorst.satset.ui.technician

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.Color
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.bumptech.glide.Glide
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.Priority
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
import com.google.android.gms.maps.model.RoundCap
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.maps.PendingResult
import com.google.maps.model.DirectionsResult
import com.tikorst.satset.LoginActivity
import com.tikorst.satset.R
import com.tikorst.satset.data.OrderID
import com.tikorst.satset.data.ServicesData
import com.tikorst.satset.databinding.ActivityMainTechnicianBinding
import com.tikorst.satset.ui.message.ChatActivity

class MainTechnicianActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityMainTechnicianBinding
    private  var order: OrderID? = null
    private lateinit var mMap: GoogleMap
    private var userMarker: Marker? = null
    private var routePolyline: Polyline? = null
    private var routePolylineDriven: Polyline? = null
    private var completeRoutePoints = mutableListOf<LatLng>()
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>
    private var destination: LatLng? = null
    private var userLatLng: LatLng? = null
    private val handler = Handler()
    private val boundsBuilder = LatLngBounds.Builder()
    private val updateInterval: Long = 5000
    val points = mutableListOf<LatLng>()
    private var lastUpdateLocation: LatLng? = null
    private val viewModel by viewModels<MainTechnicianViewModel> {
        TechnicianViewModelFactory.getInstance(this)
    }
    val callback = object : PendingResult.Callback<DirectionsResult> {
        override fun onResult(result: DirectionsResult?) {
            result?.routes?.let { routes ->
                for (route in routes) {
                    points.clear()
                    route.legs.forEach { leg ->
                        leg.steps.forEach { step ->
                            step.polyline.decodePath().forEach { point ->
                                points.add(LatLng(point.lat, point.lng))
                            }
                        }
                    }
                    runOnUiThread {
                        routePolyline?.remove()
                        routePolyline = mMap.addPolyline(
                            PolylineOptions()
                                .addAll(points)
                                .color(Color.BLUE)
                                .width(20f)
                                .jointType(2)
                                .startCap(RoundCap())
                                .endCap(RoundCap())


                        )
                    }
                }
            }
        }
        override fun onFailure(e: Throwable?) {

        }
    }
//    private var order: OrderID? = null
    private var firebaseUser: FirebaseUser? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainTechnicianBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        auth = Firebase.auth
        firebaseUser = auth.currentUser
        if (firebaseUser == null) {
            // Not signed in, launch the Login activity
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }
        val bottomsheet: LinearLayout = binding.bottomSheet
        bottomSheetBehavior = BottomSheetBehavior.from(bottomsheet)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        bottomSheetBehavior.peekHeight = 400
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        setupViewModel()
        binding.buttonFinishOrder.setOnClickListener{
            finishOrder()
        }
    }

    private fun finishOrder() {
        viewModel.finishOrder(order?.orderId!!)
        mMap.clear()
        userMarker = null
        destination = null
        routePolyline?.remove()
        getUserLocation()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        setMapStyle(isDarkTheme())

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED) {
            getUserLocation()
        }
        startLocationUpdates()
    }
    private fun requestLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        }else{
            getUserLocation()
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
                if(userMarker == null){
                    userMarker = mMap.addMarker(
                        MarkerOptions()
                            .position(userLatLng)
                            .anchor(0.5f, 0.5f)
                            .title("User Marker")
                            .icon(Utils.vectorToBitmap(R.drawable.baseline_directions_car_24, getColor(R.color.md_theme_primary), resources)))
                }
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLatLng, 17f))
            }
        }
    }
    private fun setupView() {
        binding.services.text = order?.order?.serviceType
        binding.descriptionTextView.text = order?.order?.description
        Glide.with(this)
            .load(order?.order?.imageUrl)
            .into(binding.orderImageView)
        binding.serviceImageView.setImageDrawable(
            AppCompatResources.getDrawable(this,
                ServicesData.findServiceDrawable(order?.order?.serviceType!!)!!
            )
        )
        binding.chatImageView.setOnClickListener {
            val intent = Intent(this, ChatActivity::class.java)
            intent.putExtra("orderId", order?.orderId)
            intent.putExtra("ref", binding.customerName.text.toString())
            startActivity(intent)
        }
    }

    private fun setupViewModel() {
        viewModel.order.observe(this){
            if(it != null){
                this.order = it
                setupView()
                binding.bottomSheet.visibility = View.VISIBLE
                viewModel.getCus(it.order?.userId){
                    binding.customerName.text = it?.name
                }

                handler.postDelayed(updateLocationRunnable, 2500)
            }else{
                binding.bottomSheet.visibility = View.GONE
                getUserLocation()
            }
        }
        viewModel.address.observe(this){
            val formattedAddress = getString(R.string.detail_address, it.address, it.generatedAddress)
            binding.address.text = formattedAddress
            Log.d("destination", "destination is called")
            if(destination == null && userLatLng != null){
                viewModel.fetchDirections(userLatLng!!, LatLng(it.latitude!!, it.longitude!!), callback)
                boundsBuilder.include(LatLng(it.latitude, it.longitude))
                boundsBuilder.include(userLatLng!!)
                val bounds: LatLngBounds = boundsBuilder.build()
                mMap.animateCamera(
                    CameraUpdateFactory.newLatLngBounds(
                        bounds,
                        resources.displayMetrics.widthPixels,
                        resources.displayMetrics.heightPixels,
                        150
                    )
                )
            }
            destination = LatLng(it?.latitude!!, it.longitude!!)
            mMap.addMarker(
                MarkerOptions()
                    .position(destination!!)
                    .title("Destination"))
        }
        viewModel.orderListener(firebaseUser!!.uid)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.logout_menu, menu)
        return true
    }


    private val updateLocationRunnable = object : Runnable {
        override fun run() {
            if (userLatLng != null) {
                viewModel.locationUpdate(firebaseUser!!.uid, userLatLng!!.latitude, userLatLng!!.longitude)
            }
            handler.postDelayed(this, updateInterval)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_logout -> {
                auth.signOut()
                viewModel.logout()
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun setMapStyle(darkTheme: Boolean) {
        val styleResId = if (darkTheme) R.raw.map_style_dark else R.raw.map_style_light
        mMap.setMapStyle(
            MapStyleOptions.loadRawResourceStyle(
                this, styleResId
            )
        )
    }
    private fun startLocationUpdates() {
        val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 2000)
            .build()
        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                val latestLocation = locationResult.lastLocation
                userLatLng = latestLocation?.let { LatLng(it.latitude, latestLocation.longitude) }
                if(destination == null && order != null){
                    viewModel.getAddress(order?.order?.addressId!!, order?.order?.userId!!)
                }
                if(routePolylineDriven == null){
                    routePolylineDriven = mMap.addPolyline(
                        PolylineOptions()
                            .addAll(points)
                            .color(Color.DKGRAY)
                            .width(20f)
                    )
                }else{
                    routePolylineDriven?.points?.add(userLatLng!!)
                }
                updateLocationOnMap(userLatLng!!)
                if(routePolyline != null){
                    updateRoutePolyline(userLatLng!!)
                    if( Utils.calculateDistance(userLatLng!!,routePolyline?.points!![0] ) > 100){
                        viewModel.fetchDirections(userLatLng!!, destination!!, callback)
                    }
                }

            }
        }
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestLocationPermission()
            return
        }
        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            mainLooper
        )
    }

    private fun updateLocationOnMap(userLatLng: LatLng) {
        userMarker?.position = userLatLng
    }
    private fun isDarkTheme(): Boolean {
        return resources.configuration.uiMode and
                Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES
    }
    private fun updateRoutePolyline(driverLocation: LatLng) {

//        routePolyline?.let { polyline ->
//            val completeRoutePoints = polyline.points.toMutableList()
//            val pointsToRemove = mutableListOf<LatLng>()
//
//            for (point in completeRoutePoints) {
//                if (Utils.calculateDistance(driverLocation, point) <= 10) {
//                    pointsToRemove.add(point)
//                } else {
//                    break
//                }
//            }
//
//            completeRoutePoints.removeAll(pointsToRemove)
//
//            runOnUiThread {
//                polyline.points = completeRoutePoints
//            }
//        }
        runOnUiThread{
            val remainingRoute = Utils.getRemainingRoute(driverLocation,routePolyline?.points!!)
            routePolyline?.remove()
            routePolyline = mMap.addPolyline(remainingRoute)
        }

    }
    override fun onPause() {
        super.onPause()
        handler.removeCallbacks(updateLocationRunnable)
    }
    override fun onResume() {
        super.onResume()
        handler.post(updateLocationRunnable)
    }
    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }
}
