package com.tikorst.satset.data

import com.tikorst.satset.R


class Category(var name: String, var logoResId: Int)
object CategoryData {
    val categoryList: List<Category>
        get() {
            val categories: MutableList<Category> = ArrayList()
            categories.add(Category("Electrical", R.drawable.electrical))
            categories.add(Category("Plumbing", R.drawable.plumbing))
            categories.add(Category("Air Conditioner", R.drawable.ac))
            return categories
        }
}