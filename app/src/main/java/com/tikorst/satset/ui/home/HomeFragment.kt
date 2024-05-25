package com.tikorst.satset.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.tikorst.satset.data.Category
import com.tikorst.satset.data.CategoryData
import com.tikorst.satset.databinding.FragmentHomeBinding
import com.tikorst.satset.ui.home.detail.DetailCategory
import com.tikorst.satset.R


class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private var serviceCategoriesGridView: GridView? = null
    private var categories: List<Category>? = null
    private var categoryAdapter: CategoryAdapter? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root
        serviceCategoriesGridView = binding.serviceCategoriesGridView
        categories = CategoryData.categoryList

        categoryAdapter = CategoryAdapter(requireContext(), categories as ArrayList<Category>)
        serviceCategoriesGridView!!.setAdapter(categoryAdapter)

        serviceCategoriesGridView!!.setOnItemClickListener { parent, view, position, id -> // Handle item click here
            val selectedCategory: Category = (categories as ArrayList<Category>).get(position)
            val fragment =  DetailCategory.newInstance(selectedCategory)
            childFragmentManager.beginTransaction()
                .replace(R.id.homeFragment, fragment)
                .addToBackStack(null)
                .commit()
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}