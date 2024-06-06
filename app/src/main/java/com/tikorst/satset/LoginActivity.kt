package com.tikorst.satset


import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.TextWatcher
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
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.tikorst.satset.databinding.ActivityLoginBinding
import com.tikorst.satset.technician.MainTechnicianActivity
import java.lang.String


class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        // Configure Google Sign In

        auth = Firebase.auth
        loginButton()
        registerButton()
        val colorInt = binding.messageTextView.currentTextColor
        val colorHex = String.format("#%08X", (-0x1 and colorInt))
        Log.d("Login color", colorHex)
    }

    private fun registerButton() {
        val spannableString = SpannableString("Don't have an account? Register")
        val clickableSpan: ClickableSpan = object : ClickableSpan() {
            override fun onClick(widget: android.view.View) {
                startActivity(Intent(this@LoginActivity, RegisterActivity::class.java))
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.color = getResources().getColor(R.color.md_theme_primary,null)
                ds.isUnderlineText = false
            }
        }
        spannableString.setSpan(clickableSpan, 23, 31, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        binding.registerPrompt.setText(spannableString, TextView.BufferType.SPANNABLE)
        binding.registerPrompt.movementMethod = LinkMovementMethod.getInstance()
        binding.registerPrompt.highlightColor = resources.getColor(android.R.color.transparent)
    }

    private fun updateUI(currentUser: FirebaseUser?) {
        val db = FirebaseFirestore.getInstance()

        if (currentUser != null) {
            val technicianRef = db.collection("technicians").document(currentUser.uid)
            technicianRef.get()
                .addOnSuccessListener { documentSnapshot ->
                    if (documentSnapshot.exists()) {
                        startActivity(
                            Intent(
                                this@LoginActivity,
                                MainTechnicianActivity::class.java
                            )
                        )
                        finish()
                    } else {
                        startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                        finish()
                    }
                }
                .addOnFailureListener { e ->
                    Log.w("LoginActivity", "Error getting documents.", e)
                }
        }
    }

    override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        updateUI(currentUser)
    }
    fun isValidEmail(email: CharSequence?): Boolean {
        return !email.isNullOrBlank() && Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
    private fun loginButton(){
        var valid = false
        binding.edLoginEmail.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {

            }
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if(isValidEmail(s)){
                    binding.emailEditTextLayout.error = null
                    valid = true
                }else{
                    binding.emailEditTextLayout.error = "Invalid email."
                    valid = false
                }
            }
        })
        binding.edLoginPassword.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {

            }
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if(s.toString().length < 6){
                    binding.passwordEditTextLayout.error = "Invalid password."
                    valid = false
                }else{
                    binding.passwordEditTextLayout.error = null
                    valid = true
                }
            }
        })
        binding.loginButton.setOnClickListener{

            val email = binding.edLoginEmail.text.toString()
            val password = binding.edLoginPassword.text.toString()
            if(email.isEmpty()){
                binding.emailEditTextLayout.error = "Email is required."
                valid = false
            }
            if(password.isEmpty()){
                binding.passwordEditTextLayout.error = "Password is required."
                valid = false
            }
            if(valid){
                auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            val user = auth.currentUser
                            Toast.makeText(baseContext, "Welcome ${user?.displayName}!",
                                Toast.LENGTH_LONG).show()
                            updateUI(user)
                        } else {
                            Toast.makeText(baseContext, "Email or Password is incorrect",
                                Toast.LENGTH_LONG).show()
                            Log.w("LoginActivity", "signInWithEmail:failure", task.exception)
                        }
                    }
            }else{
                Toast.makeText(baseContext, "Please fill the form correctly",
                    Toast.LENGTH_LONG).show()
            }


        }
    }
}