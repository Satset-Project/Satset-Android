package com.tikorst.satset.ui.order

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
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
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.tikorst.satset.MainActivity
import com.tikorst.satset.R
import com.tikorst.satset.databinding.ActivityOrderBinding
import com.tikorst.satset.databinding.ActivityOrderViewBinding
import com.tikorst.satset.databinding.FragmentOrderBinding

class OrderViewActivity : AppCompatActivity() {
    private var order_id: String? = null
    private var tag: String? = null
    private var serviceType: String? = null
    private lateinit var hourglassBottom: ImageView
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>
    private lateinit var viewModel: OrderViewModel
    private lateinit var handler: Handler
    private lateinit var statusCheckRunnable: Runnable
    private lateinit var binding: ActivityOrderViewBinding
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
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        bottomSheetBehavior.peekHeight = 300

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
        handler = Handler()
        viewModel = ViewModelProvider(this).get(OrderViewModel::class.java)
        viewModel.status.observe(this){
            if(it == "Pending"){
                hourglassBottom.visibility = View.VISIBLE
                startHourglassAnimation()
            }else{
                hourglassBottom.clearAnimation()
                hourglassBottom.visibility = View.GONE
            }
            binding.textView.text = it
        }
        statusCheckRunnable = object : Runnable {
            override fun run() {
                viewModel.checkOrderStatus(order_id!!)

                handler.postDelayed(this, 3000)
            }
        }

        handler.post(statusCheckRunnable)
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
}