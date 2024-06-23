package com.aksar.dungru.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.aksar.dungru.databinding.ReportUserItemBinding
import com.aksar.dungru.models.ReportUsetModel

class ReportUserAdapter(
    private var itemList: List<ReportUsetModel>,
    private var clickListener: (item: ReportUsetModel) -> Unit,
) : RecyclerView.Adapter<ReportUserAdapter.ReportViewHolder>() {

    private var selectedPosition: Int = RecyclerView.NO_POSITION

    class ReportViewHolder(var binding: ReportUserItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ReportUsetModel, isSelected: Boolean) {
            binding.btnReportUser.text = item.reason
            binding.btnReportUser.isChecked = isSelected
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReportViewHolder {
        return ReportViewHolder(
            ReportUserItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }
    override fun getItemCount() = itemList.size
    override fun onBindViewHolder(
        holder: ReportViewHolder,
        @SuppressLint("RecyclerView") position: Int,
    ) {
        val item = itemList[position]
        val isSelected = selectedPosition == position
        holder.bind(item, isSelected)
        holder.itemView.setOnClickListener {
            if (selectedPosition != position) {
                notifyItemChanged(selectedPosition)
                selectedPosition = position
                notifyItemChanged(position)
                clickListener(item)
            }
        }
    }
}