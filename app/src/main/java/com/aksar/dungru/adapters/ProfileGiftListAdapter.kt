package com.aksar.dungru.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.aksar.dungru.databinding.ItemProfileGiftRecyclerBinding
import com.aksar.dungru.models.ProfileGiftDataModel

class ProfileGiftListAdapter(
    private var itemList: List<ProfileGiftDataModel>,
    private var onClickListener: (position: Int) -> Unit,
) : RecyclerView.Adapter<ProfileGiftListAdapter.ProfileGiftViewHolder>() {
    class ProfileGiftViewHolder(val binding:ItemProfileGiftRecyclerBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ProfileGiftDataModel) {
            binding.giftIconIv.setImageResource(item.giftImg)
            binding.coinCountTv.text = item.coinCount.toString()
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): ProfileGiftViewHolder {
        return ProfileGiftViewHolder(
            ItemProfileGiftRecyclerBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    }

    override fun onBindViewHolder(
        holder: ProfileGiftViewHolder,
        position: Int,
    ) {
        val item = itemList[position]
        holder.bind(item)

    }

    override fun getItemCount(): Int = itemList.size
}