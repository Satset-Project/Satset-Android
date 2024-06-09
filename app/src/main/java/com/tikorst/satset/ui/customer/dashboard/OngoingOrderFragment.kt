package com.tikorst.satset.ui.customer.dashboard

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.tikorst.satset.R
import com.tikorst.satset.data.Order
import com.tikorst.satset.data.OrderID
import com.tikorst.satset.databinding.FragmentOngoingOrderBinding

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [OngoingOrderFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class OngoingOrderFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var viewModel: HistoryViewModel
    private lateinit var orderAdapter: OrderListAdapter
    private lateinit var _binding: FragmentOngoingOrderBinding
    private lateinit var auth: FirebaseAuth
    private val binding get() = _binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOngoingOrderBinding.inflate(inflater, container, false)

        val layoutManager = LinearLayoutManager(requireContext())
        binding.ongoingOrderRecyclerView.layoutManager = layoutManager
        setupViewModel()
        setupFirebase()
        setupNav()
        return binding.root
    }

    private fun setupNav() {
        val navView : BottomNavigationView
        navView = requireActivity().findViewById(R.id.nav_view)

        val recyclerView = binding.ongoingOrderRecyclerView

        // Set constraints programmatically to constrain the RecyclerView to the bottom of the navigation view
        val layoutParams = recyclerView.layoutParams as ConstraintLayout.LayoutParams
        layoutParams.topToBottom = navView.id
        recyclerView.layoutParams = layoutParams
    }

    private fun setOrdersList(orders: List<OrderID>) {
        val adapter = OrderListAdapter()
        adapter.submitList(orders)
        binding.ongoingOrderRecyclerView.adapter = adapter
    }


    private fun setupViewModel() {
        viewModel = ViewModelProvider(this).get(HistoryViewModel::class.java)
        viewModel.loading.observe(viewLifecycleOwner){
            loading(it)
        }
        viewModel.orders.observe(viewLifecycleOwner){
            if(it.isNotEmpty()){
                setOrdersList(it)
                binding.noOngoingOrders.visibility = View.GONE
            }
            else{
                if(binding.progressBar.visibility == View.VISIBLE){
                    binding.noOngoingOrders.visibility = View.VISIBLE
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
            viewModel.loadOrders(it.uid)
        }
    }
    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment OngoingOrderFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            OngoingOrderFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}