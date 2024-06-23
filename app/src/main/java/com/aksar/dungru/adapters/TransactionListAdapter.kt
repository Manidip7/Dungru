package com.aksar.dungru.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.aksar.dungru.R
import com.aksar.dungru.databinding.ItemTransactionHistoryRecyclerBinding
import com.aksar.dungru.models.TransactionListDataModel
import com.aksar.dungru.utils.Constance.Transaction_History_Add
import com.aksar.dungru.utils.Constance.Transaction_History_Withdraw
import com.aksar.dungru.utils.Constance.Wallet_History

class TransactionListAdapter(
    private val context: Context,
    private val itemList: List<TransactionListDataModel>,
    private val flag:String,
    private val clickListener : (position: Int) -> Unit,
    ) :
    RecyclerView.Adapter<TransactionListAdapter.TransactionViewHolder>() {
    class TransactionViewHolder(var binding: ItemTransactionHistoryRecyclerBinding) :
        ViewHolder(binding.root) {
        fun bind(context: Context, item: TransactionListDataModel,flag: String) {
            when (flag){
               Wallet_History-> when (item.transactionType) {
                    /**Deposit*/
                    0 -> {
                        binding.imgIndicator.setImageResource(R.drawable.ic_trans_out)
                        binding.txtAmount.setTextColor(context.getColor(R.color.action_color))
                    }
                    /**Withdraw*/
                    1 -> {
                        binding.imgIndicator.setImageResource(R.drawable.ic_trans_in)
                        binding.txtAmount.setTextColor(context.getColor(R.color.green))
                    }
                }

                Transaction_History_Add->when(item.transactionType){
                    0 ->{
                        binding.imgIndicator.setImageResource(R.drawable.ic_trans_out)
                        binding.txtAmount.setTextColor(context.getColor(R.color.action_color))
                    }
                }
                Transaction_History_Withdraw->when(item.transactionType){
                    1 ->{
                        binding.imgIndicator.setImageResource(R.drawable.ic_trans_in)
                        binding.txtAmount.setTextColor(context.getColor(R.color.green))
                    }
                }
            }
            binding.txtTitle.text = item.title
            binding.txtAmount.text = "₹ " + item.amount
            binding.txtDateTime.text = item.dateTime
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        return TransactionViewHolder(
            ItemTransactionHistoryRecyclerBinding.inflate(
                LayoutInflater.from(
                    parent.context
                ), parent, false
            )
        )
    }
    override fun getItemCount() = itemList.size

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        val item = itemList[position]
        holder.bind(context, item,flag)
        holder.itemView.setOnClickListener {
            clickListener.invoke(position)
        }
    }
}