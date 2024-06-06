package com.tikorst.satset.message

import android.text.format.DateUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.tikorst.satset.R
import com.tikorst.satset.databinding.ItemMessageBinding

class FirebaseMessageAdapter(
    options: FirestoreRecyclerOptions<Message>,
    private val currentId: String?
) : FirestoreRecyclerAdapter<Message, FirebaseMessageAdapter.MessageViewHolder>(options) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MessageViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_message, parent, false)
        val binding = ItemMessageBinding.bind(view)
        return MessageViewHolder(binding)
    }

    inner class MessageViewHolder(private val binding: ItemMessageBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(message: Message) {
            if (message.senderId == currentId) {
                binding.rightMessageLayout.visibility = View.VISIBLE
                binding.leftMessageLayout.visibility = View.GONE
                binding.tvMessageRight.text = message.message
                if (message.timestamp != null) {
                    binding.tvTimestampRight.text = DateUtils.getRelativeTimeSpanString(message.timestamp)
                }
            } else {
                binding.rightMessageLayout.visibility = View.GONE
                binding.leftMessageLayout.visibility = View.VISIBLE

                binding.tvMessageLeft.text = message.message
                if (message.timestamp != null) {
                    binding.tvTimestampLeft.text = DateUtils.getRelativeTimeSpanString(message.timestamp)
                }
            }
        }


    }

    override fun onBindViewHolder(
        holder: MessageViewHolder,
        position: Int,
        model: Message
    ) {
        holder.bind(model)
    }
}

