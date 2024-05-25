package com.tikorst.satset.ui.profile.address

import android.content.Context
import android.content.Intent
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
        return AddressViewHolder(binding, parent.context)
    }

    override fun onBindViewHolder(holder: AddressViewHolder, position: Int) {
        val address = getItem(position)
        holder.bind(address)
    }


    class AddressViewHolder(private val binding: ItemAddressBinding, private val context: Context) : RecyclerView.ViewHolder(binding.root) {

        fun bind(address: Address) {
            val detailAddress = address.detailAddress
            val formattedAddress = context.getString(R.string.detail_address, detailAddress?.address, detailAddress?.generatedAddress)
            binding.address.text = formattedAddress
            binding.label.text = detailAddress?.label
            itemView.setOnClickListener{
                val intent = Intent(itemView.context, AddAddressActivity::class.java)
                intent.putExtra("EDIT_ADDRESS", address)
                context.startActivity(intent)
            }
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