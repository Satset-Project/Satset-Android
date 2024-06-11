package com.tikorst.satset.ui.customer.dashboard

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

import com.tikorst.satset.data.OrderID
import com.tikorst.satset.databinding.ItemOrderBinding
import com.tikorst.satset.ui.customer.order.OrderViewActivity


class OrderListAdapter : ListAdapter<OrderID, OrderListAdapter.OrderViewHolder>(DIFF_CALLBACK) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val binding = ItemOrderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return OrderViewHolder(binding, parent.context)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val address = getItem(position)
        holder.bind(address)
    }
    class OrderViewHolder(private val binding: ItemOrderBinding, private val context: Context) : RecyclerView.ViewHolder(binding.root) {

        fun bind(order: OrderID){
            binding.label.text = order.order?.serviceType
            binding.status.text = order.order?.status
            binding.description.text = order.order?.description
            if(order.order?.status != "completed"){
                itemView.setOnClickListener{
                    val intent = Intent(context, OrderViewActivity::class.java)
                    intent.putExtra("order_id", order.orderId)
                    intent.putExtra("service", order.order?.serviceType)
                    intent.putExtra("tag", "Dashboard")
                    context.startActivity(intent)

                }
            }


        }
    }
    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<OrderID>() {
            override fun areItemsTheSame(
                oldItem: OrderID,
                newItem: OrderID
            ): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(
                oldItem: OrderID,
                newItem: OrderID
            ): Boolean {
                return oldItem == newItem
            }
        }
    }
}