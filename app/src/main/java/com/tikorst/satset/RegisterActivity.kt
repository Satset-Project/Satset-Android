package com.tikorst.satset

import android.content.Intent
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.Log
import android.util.Patterns
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.tikorst.satset.databinding.ActivityRegisterBinding
import com.tikorst.satset.ui.technician.TechnicianRegisterActivity

class RegisterActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityRegisterBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        auth = Firebase.auth
        binding.registerButton.setOnClickListener {
            val name = binding.edRegisterName.text.toString()
            val email = binding.edRegisterEmail.text.toString()
            val password = binding.edRegisterPassword.text.toString()
            val phone = binding.edRegisterPhone.text.toString()
            if (name.isEmpty()) {
                Toast.makeText(
                    baseContext, "Name cannot be empty.",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            } else if (!isValidEmail(email)) {
                Toast.makeText(
                    baseContext, "Invalid email.",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }else if(phone.length < 12){
                Toast.makeText(
                    baseContext, "Invalid Phone.",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }else if(password.length < 6){
                Toast.makeText(baseContext, "Invalid password.",
                    Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }else{
                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            updateProfile(auth.currentUser, name, phone)
                        } else {
                            Log.w("RegisterActivity", "createUserWithEmail:failure", task.exception)
                            Toast.makeText(baseContext, task.exception.toString(),
                                Toast.LENGTH_SHORT).show()
                        }
                    }
            }
        }
        loginButton()
        binding.registerTech.setOnClickListener{
            startActivity(Intent(this, TechnicianRegisterActivity::class.java))
        }

    }
    fun isValidEmail(email: CharSequence?): Boolean {
        return !email.isNullOrBlank() && Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
    private fun updateProfile(user: FirebaseUser?, name: String, phone: String) {
        val profileUpdates = UserProfileChangeRequest.Builder()
            .setDisplayName(name)
            .build()
        val db = FirebaseFirestore.getInstance()
        val tech = db.collection("users").document(user!!.uid)
        tech.set(hashMapOf("name" to name, "email" to user.email, "phone" to phone ))
        user.updateProfile(profileUpdates)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "User registered successfully", Toast.LENGTH_SHORT).show()
                    auth.currentUser!!
                    auth.signOut()
                    startActivity(Intent(this, LoginActivity::class.java))
                    finish()
                } else {
                    Toast.makeText(this, "Profile update failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }
    private fun loginButton() {
        val spannableString = SpannableString("Have an account? Login")
        val clickableSpan: ClickableSpan = object : ClickableSpan() {
            override fun onClick(widget: android.view.View) {
                startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.color = getResources().getColor(R.color.md_theme_primary,null)
                ds.isUnderlineText = false
            }
        }
        spannableString.setSpan(clickableSpan, 17, 22, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        binding.loginPrompt.setText(spannableString, TextView.BufferType.SPANNABLE)
        binding.loginPrompt.movementMethod = LinkMovementMethod.getInstance()
        binding.loginPrompt.highlightColor = resources.getColor(android.R.color.transparent)
    }
}