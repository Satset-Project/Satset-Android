package com.tikorst.satset.ui.home.detail

import android.app.Dialog
import android.content.DialogInterface.OnShowListener
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.tikorst.satset.R
import com.tikorst.satset.data.Service
import com.tikorst.satset.databinding.FragmentServicesBinding


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
        dialog?.setOnShowListener{
            val d = it as BottomSheetDialog
            Log.d("onShow", "onShow")
            val bottomSheet = d.findViewById<View>(R.id.serviceBottomSheet)
            bottomSheet?.let {
                val behavior = BottomSheetBehavior.from(it)
                behavior.state = BottomSheetBehavior.STATE_EXPANDED
                val screenHeight = requireActivity().window.decorView.height
                val targetHeight = (screenHeight * 0.45).toInt()
                Log.d("targetHeight", targetHeight.toString())
                it.layoutParams.height = targetHeight
                behavior.peekHeight = targetHeight
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

        return binding.root
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
    override fun onStart() {
        super.onStart()

//        dialog?.let { dialog ->
//            val bottomSheet = dialog.findViewById<View>(R.id.serviceBottomSheet)
//            bottomSheet?.let {
//                val behavior = BottomSheetBehavior.from(it)
//                behavior.state = BottomSheetBehavior.STATE_EXPANDED
//
//                it.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
//                    override fun onGlobalLayout() {
//                        it.viewTreeObserver.removeOnGlobalLayoutListener(this)
//                        val screenHeight = requireActivity().window.decorView.height
//                        val targetHeight = (screenHeight * 0.45).toInt()
//                        it.layoutParams.height = targetHeight
//                        it.layoutParams = it.layoutParams
//                        behavior.peekHeight = targetHeight
//                    }
//                })
//            }
//        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}