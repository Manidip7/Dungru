package com.aksar.dungru.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.aksar.dungru.R
import com.aksar.dungru.databinding.GiftItemLayoutBinding
import com.aksar.dungru.models.GiftDataModel

class GiftUserAdapter(private val data: List<GiftDataModel>, private val onItemClick: (GiftDataModel) -> Unit) :
    RecyclerView.Adapter<GiftUserAdapter.GiftUserHolder>() {
    private var selectedPosition : Int = RecyclerView.NO_POSITION

    class GiftUserHolder(val binding: GiftItemLayoutBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: GiftDataModel){
            binding.imgGift.setImageResource(item.imageCoin)
            binding.giftPoint.text = item.point
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GiftUserHolder {
        val binding = GiftItemLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return GiftUserHolder(binding)
    }


    override fun onBindViewHolder(holder: GiftUserHolder, position: Int) {
        val item = data[position]
        holder.bind(item)
        holder.itemView.setOnClickListener {
            onItemClick.invoke(item)
            setSelectItemPosition(position)
        }

        if (selectedPosition == position){
            holder.binding.root.setBackgroundResource(R.drawable.bg_wallet_coin_selected)
        }else{
            holder.binding.root.setBackgroundResource(R.drawable.bg_transparent)
        }
    }
    override fun getItemCount(): Int {
        return data.size
    }

    private fun setSelectItemPosition(position: Int) {
        val previousSelectedItem = selectedPosition
        selectedPosition =position
        notifyItemChanged(previousSelectedItem)
        notifyItemChanged(selectedPosition)
    }
}