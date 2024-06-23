package com.aksar.dungru.adapters
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.aksar.dungru.R
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.textview.MaterialTextView

class WalletBankAdapter(private val dataSet: List<String>) :

        RecyclerView.Adapter<WalletBankAdapter.ViewHolder>() {

        class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val imageView: ShapeableImageView
            val bankName: MaterialTextView
            init {
                imageView = view.findViewById(R.id.imgbank_new)
                bankName = view.findViewById(R.id.bank_name)
            }
        }

        override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {

            val view = LayoutInflater.from(viewGroup.context)
                .inflate(R.layout.bank_rec_item_view, viewGroup, false)

            return ViewHolder(view)
        }

        override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {

            viewHolder.bankName.text = dataSet[position]
        }
        override fun getItemCount() = dataSet.size

    }

