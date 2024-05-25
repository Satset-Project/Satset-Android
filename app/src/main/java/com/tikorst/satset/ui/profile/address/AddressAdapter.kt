package com.tikorst.satset.ui.profile.address

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.tikorst.satset.data.Address
import com.tikorst.satset.databinding.ItemAddressBinding
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.tikorst.satset.R

class AddressAdapter : ListAdapter<Address, AddressAdapter.AddressViewHolder>(DIFF_CALLBACK){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddressViewHolder {
        val binding = ItemAddressBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AddressViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AddressViewHolder, position: Int) {
        val address = getItem(position)
        holder.bind(address)
    }


    class AddressViewHolder(private val binding: ItemAddressBinding) : RecyclerView.ViewHolder(binding.root) {
        private val streetTextView: TextView = itemView.findViewById(R.id.streetTextView)
        fun bind(address: Address) {
            streetTextView.text = address.address + "," + address.id
        }
    }
    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Address>() {
            override fun areItemsTheSame(
                oldItem: Address,
                newItem: Address
            ): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: Address,
                newItem: Address
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}