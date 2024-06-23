package com.aksar.dungru.adapters

import android.animation.ObjectAnimator
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.aksar.dungru.databinding.HelpcenterItemBinding
import com.aksar.dungru.models.FAQItem


class FAQAdapter(private val data: List<FAQItem>) :
    RecyclerView.Adapter<FAQAdapter.FaqHolder>() {

    class FaqHolder(private val binding: HelpcenterItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: FAQItem){
            binding.questionTextView.text = item.question
            binding.answerTextView.text = item.answer

            binding.root.setOnClickListener {
                val isVisible = binding.answerTextView.visibility == View.VISIBLE
                binding.answerTextView.visibility = if (isVisible) View.GONE else View.VISIBLE

                if (isVisible){
                    binding.viewGreyLine.visibility = View.VISIBLE
                }else{
                    binding.viewGreyLine.visibility = View.GONE
                }

                val rotation = if (isVisible) 0f else 180f
                val animator = ObjectAnimator.ofFloat(binding.dropdownIcon, View.ROTATION, rotation)
                animator.duration = 250
                animator.start()
            }
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FaqHolder {
        val binding = HelpcenterItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FaqHolder(binding)
    }

    override fun onBindViewHolder(holder: FaqHolder, position: Int) {
        val item = data[position]
        holder.bind(item)
    }
    override fun getItemCount(): Int {
        return data.size
    }
}