package com.aksar.dungru.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.aksar.dungru.databinding.ItemChatRecyclerBinding
import com.aksar.dungru.models.ChatListDataModel

class ChatListAdapter(
    private var itemList: List<ChatListDataModel>,
    private var clickListener: (item: ChatListDataModel) -> Unit
) : RecyclerView.Adapter<ChatListAdapter.ChatViewHolder>() {
    class ChatViewHolder(var binding: ItemChatRecyclerBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ChatListDataModel) {
            if (item.isOnline)
                binding.onlineIndicator.visibility = View.VISIBLE
            else
                binding.onlineIndicator.visibility = View.GONE

            binding.imgChatProfile.setImageResource(item.profileImg)
            binding.txtChatName.text = item.name
            binding.txtChatDesc.text = item.desc
            binding.txtChatTime.text = item.time
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        return ChatViewHolder(
            ItemChatRecyclerBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount() = itemList.size

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        val item = itemList[position]
        holder.bind(item)
        holder.binding.root.setOnClickListener {
            clickListener(item)
        }
    }
}