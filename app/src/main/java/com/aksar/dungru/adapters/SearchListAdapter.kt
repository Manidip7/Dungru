package com.aksar.dungru.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.aksar.dungru.databinding.ItemSearchRecyclerBinding
import com.aksar.dungru.models.SearchListDataModel

class SearchListAdapter(
    private var itemList: List<SearchListDataModel>,
    private var clickListener: (position: Int) -> Unit
) : RecyclerView.Adapter<SearchListAdapter.SearchViewHolder>() {
    class SearchViewHolder(val binding: ItemSearchRecyclerBinding):
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: SearchListDataModel) {
            binding.imgSearchProfile.setImageResource(item.profileImg)
            binding.txtSerachName.text = item.name
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SearchViewHolder {
        return SearchViewHolder(
            ItemSearchRecyclerBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
        val item = itemList[position]
        holder.bind(item)
    }

    override fun getItemCount() = itemList.size

}