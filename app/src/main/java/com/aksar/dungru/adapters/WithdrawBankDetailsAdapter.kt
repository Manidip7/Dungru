package com.aksar.dungru.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.aksar.dungru.R
import com.aksar.dungru.databinding.ItemWithdrawBankBtnRecyclerBinding
import com.aksar.dungru.models.BankDetailsModel
import com.aksar.dungru.utils.extensions.hideKeyboard

class WithdrawBankDetailsAdapter(
    private val itemList: List<BankDetailsModel>,
    private val clickListener: BankButtonClickListener
) : RecyclerView.Adapter<WithdrawBankDetailsAdapter.BankViewHolder>() {
    private var selectedPosition = RecyclerView.NO_POSITION
    interface BankButtonClickListener {
        fun bankSelected(position: Int, item: BankDetailsModel)
        fun bankDelete(position: Int)
    }
    class BankViewHolder(private var binding: ItemWithdrawBankBtnRecyclerBinding) :
        RecyclerView.ViewHolder(binding.root) {
            fun bind( item: BankDetailsModel, isSelected: Boolean) {
                binding.imgBank.setImageResource(item.bankImage)
                binding.txtBankName.text = item.bankName

                if (isSelected)
                    binding.cardBackground.setBackgroundResource(R.drawable.bg_rounded_blue_stroke_shape_16dp)
                else
                    binding.cardBackground.setBackgroundResource(R.drawable.bg_transparent)

            }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BankViewHolder {
        val binding = ItemWithdrawBankBtnRecyclerBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return BankViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: BankViewHolder, @SuppressLint("RecyclerView") position: Int) {
        val item = itemList[position]
        val isSelected = selectedPosition == position
        holder.bind(item, isSelected)
        holder.itemView.setOnClickListener {
            if(selectedPosition!= position){
                notifyItemChanged(selectedPosition)
                selectedPosition = position
            }
            it.hideKeyboard()
            clickListener.bankSelected(selectedPosition,item)
        }
    }
}