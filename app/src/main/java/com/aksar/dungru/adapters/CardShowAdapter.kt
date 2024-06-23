package com.aksar.dungru.adapters
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.aksar.dungru.databinding.DebitcardCreditcardItemBinding
import com.aksar.dungru.models.CardModel

class CardShowAdapter(private val data: ArrayList<CardModel>, private val onItemClick: CardClickListener) :
    RecyclerView.Adapter<CardShowAdapter.CardViewHolder>() {

    interface CardClickListener{
        fun defaultButtonClicked(position: Int, item: CardModel)
        fun deleteButtonClicked(position: Int)
    }
    class CardViewHolder(var binding: DebitcardCreditcardItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item:CardModel){
            binding.txtCardNumber.text = item.cardNo
            binding.txtCardExpiryDate.text = item.cardExp
            binding.txtCardHolderName.text = item.cardholderName
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val binding = DebitcardCreditcardItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CardViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        val item = data[position]
        holder.bind(item)
        holder.itemView.setOnClickListener {

        }
        holder.binding.btnSetDefault.setOnClickListener {
            onItemClick.defaultButtonClicked(position, item)
        }
        holder.binding.btnDelete.setOnClickListener {
            data.removeAt(position)
            onItemClick.deleteButtonClicked(position)
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }
}