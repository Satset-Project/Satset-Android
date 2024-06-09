package com.tikorst.satset.ui.customer.order

import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.tikorst.satset.R
import com.tikorst.satset.data.Address
import com.tikorst.satset.data.Addresses
import com.tikorst.satset.data.Service
import com.tikorst.satset.databinding.FragmentAddressBinding


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ADDRESSES = "addresses"

/**
 * A simple [Fragment] subclass.
 * Use the [AddressFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AddressFragment : BottomSheetDialogFragment() {
    // TODO: Rename and change types of parameters
    private var addresses: Addresses? = null
    private var dialogView: View? = null
    private var _binding: FragmentAddressBinding? = null
    private val binding get() = _binding!!
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            addresses = if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                it.getParcelable(ADDRESSES, Addresses::class.java)
            }else{
                it.getParcelable(ADDRESSES)
            }

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddressBinding.inflate(inflater, container, false)
        val layoutManager = LinearLayoutManager(requireContext())
        binding.addressList.layoutManager = layoutManager
        setAddressList(addresses?.addresses)
        return binding.root
    }
    private fun setAddressList(news: List<Address>?) {
        val adapter = AddressAdapter {
            (activity as AddressListener).onItemSelected(it)
            dismiss()}
        adapter.submitList(news)
        binding.addressList.adapter = adapter
    }
    interface AddressListener {
        fun onItemSelected(item: Address)
    }
    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment AddressFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(addresses: Addresses) =
            AddressFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(ADDRESSES, addresses)
                }
            }
    }
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//        view.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
//            override fun onGlobalLayout() {
//
//                view.viewTreeObserver.removeOnGlobalLayoutListener(this)
//                val dialog = dialog as BottomSheetDialog
//                val bottomSheet = dialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet) as FrameLayout?
//                val behavior = BottomSheetBehavior.from(bottomSheet!!)
//                behavior.state = BottomSheetBehavior.STATE_EXPANDED
//
//                val newHeight = activity?.window?.decorView?.height?.times(0.35)?.toInt()
//                val viewGroupLayoutParams = bottomSheet.layoutParams
//                viewGroupLayoutParams.height = newHeight ?: 0
//                bottomSheet.layoutParams = viewGroupLayoutParams
//            }
//        })
//        dialogView = view
//    }
}