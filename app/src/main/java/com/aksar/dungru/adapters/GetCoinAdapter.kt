package com.aksar.dungru.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.aksar.dungru.R
import com.aksar.dungru.databinding.AddCoinLayoutItemBinding
import com.aksar.dungru.models.GetCoinModel
import com.aksar.dungru.utils.Utils
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.textview.MaterialTextView

class GetCoinAdapter(private val context: Context ,private val data: List<GetCoinModel>, private val onItemClick: (GetCoinModel) -> Unit) :
    RecyclerView.Adapter<GetCoinAdapter.ViewHolder>() {
        private var selectedPosition : Int = RecyclerView.NO_POSITION

    class ViewHolder(private val binding: AddCoinLayoutItemBinding) : RecyclerView.ViewHolder(binding.root) {
        val itemImageView: ShapeableImageView = binding.imgCoinLogo
        val itemAmount: MaterialTextView = binding.txtCoinAmount
        val itemPoint : MaterialTextView = binding.txtPoint
        val card: CardView = binding.bteCard
        val popularImage: ShapeableImageView = binding.popularImager
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = AddCoinLayoutItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = data[position]
        holder.itemImageView.setImageResource(item.imageCoin)
        holder.itemAmount.text = "₹"+item.amount
        holder.itemPoint.text = item.point

        holder.itemView.setOnClickListener {
            onItemClick(item)
            setSelectItemPosition(position)
        }

        if (item.amount.toInt()<100){
            holder.itemImageView.setImageResource(R.drawable.coin)
            holder.popularImage.visibility = android.view.View.GONE
        }else if (item.amount.toInt()<200){
            holder.itemImageView.setImageResource(R.drawable.coin_all)
            holder.popularImage.visibility = android.view.View.GONE
        }else if (item.amount.toInt()<500){
            holder.itemImageView.setImageResource(R.drawable.coins_malti)
            holder.popularImage.visibility = android.view.View.GONE
        }else{
            holder.itemImageView.setImageResource(R.drawable.coins_malti)
            holder.popularImage.visibility = android.view.View.VISIBLE
        }

        if (selectedPosition == position){
           holder.card.setBackgroundResource(R.drawable.bg_wallet_coin_selected)
            holder.itemAmount.setBackgroundResource(R.drawable.bg_rounded_gradiant_color_button)
            holder.itemAmount.setTextColor(
                if (position == selectedPosition) {
                    ContextCompat.getColor(holder.itemView.context, R.color.white)
                } else {
                    ContextCompat.getColor(holder.itemView.context, android.R.color.black)
                }
            )
        }else{
            holder.card.setBackgroundResource(R.drawable.bg_wallet_coin_not_selected)
            holder.itemAmount.setBackgroundResource(com.google.android.material.R.color.mtrl_btn_transparent_bg_color)

            if (Utils(context).isDarkTheme()){
                holder.itemAmount.setTextColor(
                    if (position == selectedPosition) {
                        ContextCompat.getColor(holder.itemView.context, R.color.black)
                    } else {
                        ContextCompat.getColor(holder.itemView.context, R.color.white)
                    }
                )
            }else{
                holder.itemAmount.setTextColor(
                    if (position == selectedPosition) {
                        ContextCompat.getColor(holder.itemView.context, R.color.white)
                    } else {
                        ContextCompat.getColor(holder.itemView.context, R.color.black)
                    }
                )
            }
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