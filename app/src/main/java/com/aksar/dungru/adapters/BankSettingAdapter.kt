package com.aksar.dungru.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.aksar.dungru.databinding.BankSettingItemBinding
import com.aksar.dungru.models.BankDetailsModel

class BankSettingAdapter(
    private var itemList: ArrayList<BankDetailsModel>,
    private var clickListener: (position: Int, id: String) -> Unit
) : RecyclerView.Adapter<BankSettingAdapter.BankViewHolder>() {
    class BankViewHolder(var binding: BankSettingItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(
            item: BankDetailsModel,
            clickListener: (position: Int, id: String) -> Unit,
            position: Int
        ) {
            binding.bankImage.setImageResource(item.bankImage)
            binding.bankName.text = item.bankName

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BankViewHolder {
        return BankViewHolder(
            BankSettingItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int = itemList.size


    @SuppressLint("NotifyDataSetChanged")
    override fun onBindViewHolder(holder: BankViewHolder, position: Int) {
        val item = itemList[position]
        holder.bind(item, clickListener, position)
        holder.binding.btnDelete.setOnClickListener {
            itemList.removeAt(position)
            clickListener(position, item.bankName)
        }
    }
}