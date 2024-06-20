package com.tikorst.satset.ui.customer.dashboard

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.tikorst.satset.R
import com.tikorst.satset.data.OrderID
import com.tikorst.satset.databinding.FragmentHistoryBinding
import com.tikorst.satset.databinding.FragmentOngoingOrderBinding

class HistoryFragment : Fragment() {
    private lateinit var viewModel: HistoryViewModel
    private lateinit var _binding: FragmentHistoryBinding
    private lateinit var auth: FirebaseAuth
    private val binding get() = _binding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentHistoryBinding.inflate(inflater, container, false)

        val layoutManager = LinearLayoutManager(requireContext())
        binding.historyOrderRecyclerView.layoutManager = layoutManager
        setupViewModel()
        setupFirebase()
        setupNav()
        return binding.root
    }

    private fun setupNav() {
        val navView : BottomNavigationView
        navView = requireActivity().findViewById(R.id.nav_view)

        val recyclerView = binding.historyOrderRecyclerView

        // Set constraints programmatically to constrain the RecyclerView to the bottom of the navigation view
        val layoutParams = recyclerView.layoutParams as ConstraintLayout.LayoutParams
        layoutParams.topToBottom = navView.id
        recyclerView.layoutParams = layoutParams
    }

    private fun setOrdersList(orders: List<OrderID>) {
        val adapter = OrderListAdapter()
        adapter.submitList(orders)
        binding.historyOrderRecyclerView.adapter = adapter
    }


    private fun setupViewModel() {
        viewModel = ViewModelProvider(this).get(HistoryViewModel::class.java)
        viewModel.loading.observe(viewLifecycleOwner){
            loading(it)
        }
        viewModel.history.observe(viewLifecycleOwner){
            if(it.isNotEmpty()){
                setOrdersList(it)
                binding.noHistoryOrders.visibility = View.GONE
            }
            else{
                if(binding.progressBar.visibility == View.VISIBLE){
                    binding.noHistoryOrders.visibility = View.VISIBLE
                }
            }

        }

    }

    private fun loading(it: Boolean) {
        if(it){
            binding.progressBar.visibility = View.VISIBLE
        }else{
            binding.progressBar.visibility = View.GONE
        }
    }

    private fun setupFirebase() {
        auth = Firebase.auth
        val currentUser = auth.currentUser
        currentUser?.let {
            viewModel.loadHistory(it.uid)
        }
    }
}