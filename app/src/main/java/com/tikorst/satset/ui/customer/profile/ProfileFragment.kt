package com.tikorst.satset.ui.customer.profile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.tikorst.satset.AuthViewModel
import com.tikorst.satset.LoginActivity
import com.tikorst.satset.databinding.FragmentProfileBinding
import com.tikorst.satset.R
import com.tikorst.satset.ViewModelFactory
import com.tikorst.satset.ui.customer.profile.address.AddressActivity

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private lateinit var auth: FirebaseAuth
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private val viewModel: ProfileViewModel by viewModels {
        ViewModelFactory.getInstance(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        val root: View = binding.root


        return root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth = Firebase.auth
        val firebaseUser = auth.currentUser
        if (firebaseUser != null) {
            binding.nameTextView.text = firebaseUser.displayName
            binding.emailTextView.text = firebaseUser.email
            if(firebaseUser.photoUrl != null)
            Glide.with(this)
                .load(firebaseUser.photoUrl)
                .into(binding.profileImageView)
            else
                binding.profileImageView.setImageResource(R.drawable.avatar)

        }
        binding.logoutButton.setOnClickListener {
            viewModel.logout()
            signOut()
        }
        binding.addressTextView.setOnClickListener {
            val intent = Intent(activity, AddressActivity::class.java)
            startActivity(intent)
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    private fun signOut() {
        auth.signOut()
        val intent = Intent(activity, LoginActivity::class.java)
        startActivity(intent)
        activity?.finish()
    }
}