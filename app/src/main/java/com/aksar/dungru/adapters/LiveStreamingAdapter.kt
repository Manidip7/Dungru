package com.aksar.dungru.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.aksar.dungru.databinding.ItemLiveRecyclerBinding
import com.aksar.dungru.models.LiveStreamContentModel

class LiveStreamingAdapter(
    private var itemList: List<LiveStreamContentModel>,
    private var clickListener: LiveClickListener
) :
    RecyclerView.Adapter<LiveStreamingAdapter.LiveViewHolder>() {

    interface LiveClickListener{
        fun onItemClick(position: Int)
        fun onAccountClick(id: String)
    }
    class LiveViewHolder(var binding: ItemLiveRecyclerBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: LiveStreamContentModel, clickListener: LiveClickListener) {
            binding.imgThumbnail.setImageResource(item.thumbnailImg)
            binding.imgCreatorProfile.setImageResource(item.creatorProfileImg)
            binding.txtCreatorName.text = item.creatorName
            binding.txtTotalView.text = item.view
            binding.imgCreatorProfile.setOnClickListener{
                clickListener.onAccountClick(item.id)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LiveViewHolder {
        return LiveViewHolder(
            ItemLiveRecyclerBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: LiveViewHolder, position: Int) {
        holder.bind(itemList[position], clickListener)
        holder.binding.root.setOnClickListener {
            clickListener.onItemClick(position)
        }
    }

}