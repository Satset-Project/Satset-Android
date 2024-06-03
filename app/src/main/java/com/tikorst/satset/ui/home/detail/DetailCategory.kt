package com.tikorst.satset.ui.home.detail

import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridView
import android.widget.Toast
import androidx.core.view.WindowCompat
import androidx.lifecycle.findViewTreeViewModelStoreOwner
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.tikorst.satset.R
import com.tikorst.satset.data.Category
import com.tikorst.satset.data.CategoryData
import com.tikorst.satset.data.Service
import com.tikorst.satset.databinding.FragmentDetailCategoryBinding
import com.tikorst.satset.databinding.FragmentHomeBinding
import com.tikorst.satset.ui.home.CategoryAdapter

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "category"

/**
 * A simple [Fragment] subclass.
 * Use the [DetailCategory.newInstance] factory method to
 * create an instance of this fragment.
 */
class DetailCategory : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: Category? = null
    private var _binding: FragmentDetailCategoryBinding? = null
    private lateinit var navView: BottomNavigationView
    private var servicesGridView: GridView? = null
    private var categories: List<Service>? = null
    private var categoryAdapter: ServicesAdapter? = null
    private val binding get() = _binding!!
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
                it.getParcelable(ARG_PARAM1, Category::class.java)
            }else{
                it.getParcelable(ARG_PARAM1)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentDetailCategoryBinding.inflate(inflater, container, false)
        val root: View = binding.root
//        binding.photo.setImageResource(param1!!.logoResId)
        WindowCompat.setDecorFitsSystemWindows(requireActivity().window, false);
        binding.title.text = param1!!.name
        binding.description.text = param1!!.description
        navView = requireActivity().findViewById(R.id.nav_view)
        navView.visibility = View.GONE
        servicesGridView = binding.servicesGridView
        categories = param1!!.services

        categoryAdapter = ServicesAdapter(requireContext(), categories as ArrayList<Service>)
        servicesGridView!!.setAdapter(categoryAdapter)

        servicesGridView!!.setOnItemClickListener { parent, view, position, id -> // Handle item click here
            val selectedService: Service = (categories as ArrayList<Service>).get(position)
            val fragment =  ServicesFragment.newInstance(selectedService)
            fragment.show(childFragmentManager, "service")
        }

        binding.checkButton.setOnClickListener {
            Toast.makeText(requireContext(), "Coming Soon", Toast.LENGTH_SHORT).show()
        }

        return root
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment DetailCategory.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(category: Category) =
            DetailCategory().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_PARAM1, category)
                }
            }
    }

    override fun onDestroy() {
        super.onDestroy()
        navView.visibility = View.VISIBLE
    }

}