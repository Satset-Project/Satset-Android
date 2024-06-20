package com.tikorst.satset.ui.customer.home.detail

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.tikorst.satset.data.Service
import com.tikorst.satset.databinding.FragmentServicesBinding
import com.tikorst.satset.ui.customer.order.OrderActivity


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val SERVICE = "service"

/**
 * A simple [Fragment] subclass.
 * Use the [ServicesFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ServicesFragment : BottomSheetDialogFragment() {
    // TODO: Rename and change types of parameters
    private var service: Service? = null
    private var _binding: FragmentServicesBinding? = null
    private var dialogView: View? = null
    private val binding get() = _binding!!
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            service = if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                it.getParcelable(SERVICE, Service::class.java)
            }else{
                it.getParcelable(SERVICE)
            }

        }

    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentServicesBinding.inflate(inflater, container, false)
        binding.serviceName.text = service!!.name
        binding.serviceDescription.text = service!!.description

        binding.orderButton.setOnClickListener {
            val intent = Intent(requireContext(), OrderActivity::class.java)
            intent.putExtra("service", service!!.type + " " + service!!.name)
            startActivity(intent)
        }
//        dialog?.setOnShowListener{
//            Log.d("onCreateDialog", "onCreateDialogaaa")
//            val sheetBehavior = BottomSheetBehavior.from(binding.serviceBottomSheet.rootView)
//            sheetBehavior.let {
//                sheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
//                val screenHeight = requireActivity().window.decorView.height
//                val targetHeight = (screenHeight * 0.45).toInt()
//                Log.d("targetHeight", targetHeight.toString())
//                sheetBehavior.peekHeight = targetHeight
//            }
//        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        view.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {

                view.viewTreeObserver.removeOnGlobalLayoutListener(this)
                val dialog = dialog as BottomSheetDialog
                val bottomSheet = dialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet) as FrameLayout?
                val behavior = BottomSheetBehavior.from(bottomSheet!!)
                behavior.state = BottomSheetBehavior.STATE_EXPANDED

                val newHeight = activity?.window?.decorView?.height?.times(0.35)?.toInt()
                val viewGroupLayoutParams = bottomSheet.layoutParams
                viewGroupLayoutParams.height = newHeight ?: 0
                bottomSheet.layoutParams = viewGroupLayoutParams
            }
        })
        dialogView = view
    }
    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ServicesFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(service: Service) =
            ServicesFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(SERVICE, service)
                }
            }
    }

    override fun onDestroyView() {
        dialogView?.viewTreeObserver?.addOnGlobalLayoutListener(null)
        super.onDestroyView()
        _binding = null
    }
}