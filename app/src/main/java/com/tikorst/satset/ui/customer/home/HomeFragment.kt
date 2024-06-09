package com.tikorst.satset.ui.customer.home

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.tikorst.satset.data.Category
import com.tikorst.satset.data.CategoryData
import com.tikorst.satset.databinding.FragmentHomeBinding
import com.tikorst.satset.R
import com.tikorst.satset.ui.customer.home.detail.DetailCategory


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
    private fun showOptionsDialog() {
        val options = arrayOf("Open Gallery", "Open Camera")
        val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Choose an option")
        builder.setItems(options) { dialog, which ->
            if (which === 0) {
                openGallery()
            } else if (which === 1) {
                openCamera()
            }
        }
        builder.show()
    }

    private fun openGallery() {
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(galleryIntent, REQUEST_CODE_GALLERY)
    }

    private fun openCamera() {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(cameraIntent, REQUEST_CODE_CAMERA)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == AppCompatActivity.RESULT_OK) {
            if (requestCode == REQUEST_CODE_GALLERY && data != null) {
                val selectedImage = data.data
                // Handle the image from the gallery
            } else if (requestCode == REQUEST_CODE_CAMERA && data != null) {
                val takenPhoto = data.data
                // Handle the image from the camera
            }
        }
    }
    companion object {
        private const val REQUEST_CODE_GALLERY = 1
        private const val REQUEST_CODE_CAMERA = 2
    }
}