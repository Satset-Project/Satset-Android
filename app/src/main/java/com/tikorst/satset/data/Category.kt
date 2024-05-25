package com.tikorst.satset.data

import android.os.Parcelable
import com.tikorst.satset.R
import kotlinx.parcelize.Parcelize

@Parcelize
class Category(var name: String,var description: String, var logoResId: Int) : Parcelable
object CategoryData {
    val categoryList: List<Category>
        get() {
            val categories: MutableList<Category> = ArrayList()
            categories.add(Category("Electrical", "Ini Deskripsi", R.drawable.electrical))
            categories.add(Category("Plumbing", "Ini Deskripsi",R.drawable.plumbing))
            categories.add(Category("Air Conditioner","Ini Deskripsi" ,R.drawable.ac))
            return categories
        }
}