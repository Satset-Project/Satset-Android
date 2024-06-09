package com.tikorst.satset.ui.customer.home.detail


import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.tikorst.satset.R
import com.tikorst.satset.data.Category
import com.tikorst.satset.data.Service


class ServicesAdapter(context: Context, categories: List<Service>) :
    BaseAdapter() {
    private val mContext: Context = context
    private val mCategories: List<Service> = categories

    override fun getCount(): Int {
        return mCategories.size
    }

    override fun getItem(position: Int): Any {
        return mCategories[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var convertView = convertView
        if (convertView == null) {
            convertView =
                LayoutInflater.from(mContext).inflate(R.layout.grid_item_layout, parent, false)
        }

        val logoImageView = convertView!!.findViewById<ImageView>(R.id.categoryLogoImageView)
        val nameTextView = convertView.findViewById<TextView>(R.id.categoryNameTextView)

        val category = mCategories[position]
        logoImageView.setImageResource(category.logoResId)
        nameTextView.text = category.name

        return convertView
    }
}