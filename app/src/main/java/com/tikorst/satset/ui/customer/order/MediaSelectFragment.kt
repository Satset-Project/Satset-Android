package com.tikorst.satset.ui.customer.order

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

import com.tikorst.satset.databinding.FragmentMediaSelectBinding


/**
 * A simple [Fragment] subclass.
 * Use the [ImageSelectFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MediaSelectFragment : BottomSheetDialogFragment() {
    private var _binding: FragmentMediaSelectBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentMediaSelectBinding.inflate(inflater, container, false)
        binding.camera.setOnClickListener {
            (activity as MediaListener).onMediaSelected(0)
            dismiss()
        }
        binding.gallery.setOnClickListener {
            (activity as MediaListener).onMediaSelected(1)
            dismiss()
        }
        return binding.root
    }
    interface MediaListener {
        fun onMediaSelected(item: Int)
    }

}