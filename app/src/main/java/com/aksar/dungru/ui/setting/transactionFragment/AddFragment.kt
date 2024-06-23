package com.aksar.dungru.ui.setting.transactionFragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.aksar.dungru.adapters.TransactionListAdapter
import com.aksar.dungru.databinding.FragmentAddBinding
import com.aksar.dungru.models.TransactionListDataModel
import com.aksar.dungru.utils.Constance

class AddFragment : Fragment() {
    private lateinit var binding:FragmentAddBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentAddBinding.inflate(layoutInflater)

        val transactionList = listOf(
            TransactionListDataModel(0, "Get coin", "29 September 10.30 Am", "100"),
            TransactionListDataModel(0, "Get coin", "29 September 10.05 Am", "100"),
            TransactionListDataModel(0, "Get coin", "29 September 09.45 Am", "300"),
            TransactionListDataModel(0, "Get coin", "29 September 08.05 Am", "100"),
            TransactionListDataModel(0, "Get coin", "29 September 06.20 Am", "200"),
            TransactionListDataModel(0, "Get coin", "29 September 05.05 Am", "300")
        )
        binding.recAddFragment.adapter = TransactionListAdapter(requireContext(), transactionList,Constance.Transaction_History_Add) {

        }
        return binding.root
    }
}