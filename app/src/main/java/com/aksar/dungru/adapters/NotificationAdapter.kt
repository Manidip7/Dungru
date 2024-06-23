package com.aksar.dungru.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.aksar.dungru.databinding.ItemNotificationRecyclerBinding
import com.aksar.dungru.models.NotificationModel

class NotificationAdapter(
    private var itemList: List<NotificationModel>,
    private var clickListener: (position: Int) -> Unit
) : RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder>() {

    class NotificationViewHolder(val binding: ItemNotificationRecyclerBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: NotificationModel) {
            binding.notititle.text = item.notificationTitle
            binding.notiTime.text = item.notificationTime
            binding.notimsg.text = item.notificationBody
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        return NotificationViewHolder(
            ItemNotificationRecyclerBinding.inflate(
                LayoutInflater.from(
                    parent.context
                ), parent, false
            )
        )
    }

    override fun getItemCount(): Int = itemList.size

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        val item = itemList[position]
        holder.bind(item)

    }
}