package com.aksar.dungru.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.aksar.dungru.R
import com.aksar.dungru.databinding.ItemWithdrawCoinRecyclerBinding
import com.aksar.dungru.models.WalletCoinDataModel
import com.aksar.dungru.utils.extensions.hideKeyboard

class WalletCoinAdapter(
    private var itemList: List<WalletCoinDataModel>,
    private var clickListener: (amount: Int) -> Unit
) : RecyclerView.Adapter<WalletCoinAdapter.WalletCoinViewHolder>() {
    private var selectedPosition = RecyclerView.NO_POSITION
    class WalletCoinViewHolder(val binding: ItemWithdrawCoinRecyclerBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: WalletCoinDataModel, isSelected: Boolean) {
            binding.coinCountTv.text = item.coinCount.toString()
            binding.amountTv.text = "₹${item.coinAmount}"

            if (isSelected) binding.root.setBackgroundResource(R.drawable.bg_wallet_coin_selected)
            else binding.root.setBackgroundResource(R.drawable.bg_wallet_coin_not_selected)
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): WalletCoinViewHolder {
        return WalletCoinViewHolder(
            ItemWithdrawCoinRecyclerBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(
        holder: WalletCoinViewHolder,
        @SuppressLint("RecyclerView") position: Int
    ) {
        val item = itemList[position]
        val isSelected = position == selectedPosition
        holder.bind(item, isSelected)

        holder.itemView.setOnClickListener {
            if (selectedPosition!= position){
                notifyItemChanged(selectedPosition)
                selectedPosition = position
                notifyItemChanged(selectedPosition)
                it.hideKeyboard()
                clickListener.invoke(itemList[selectedPosition].coinAmount)
            }
        }
    }

    override fun getItemCount(): Int = itemList.size
}