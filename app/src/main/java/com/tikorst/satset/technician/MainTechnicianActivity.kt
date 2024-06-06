package com.tikorst.satset.technician

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.tikorst.satset.LoginActivity
import com.tikorst.satset.R
import com.tikorst.satset.data.OrderID
import com.tikorst.satset.databinding.ActivityMainTechnicianBinding
import com.tikorst.satset.databinding.ActivityOrderViewBinding

class MainTechnicianActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityMainTechnicianBinding
    private lateinit var viewModel: MainTechnicianViewModel
//    private var order: OrderID? = null
    private lateinit var statusCheckRunnable: Runnable
    private lateinit var handler: Handler
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
        setupViewModel()

    }

    private fun setupViewModel() {
        handler = Handler()
        viewModel = ViewModelProvider(this).get(MainTechnicianViewModel::class.java)
        viewModel.order.observe(this){od ->
            if(od != null){
                binding.bottomSheet.visibility = View.VISIBLE
                binding.label.text = od.order?.serviceType
                binding.status.text = od.order?.status
                binding.description.text = od.order?.description
                binding.orderCard.setOnClickListener{
                    val intent = Intent(this, OrderTechnicianActivity::class.java)
                    intent.putExtra("order", od)
                    startActivity(intent)
                }
            }else{
                binding.bottomSheet.visibility = View.GONE
            }
        }
        statusCheckRunnable = object : Runnable {
            override fun run() {
                firebaseUser?.let { viewModel.getOrders(it.uid) }

                handler.postDelayed(this, 3000)
            }
        }
        handler.post(statusCheckRunnable)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.logout_menu, menu)
        return true
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_logout -> {
                auth.signOut()
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}