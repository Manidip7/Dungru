package com.aksar.dungru.ui.setting.transactionFragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.aksar.dungru.adapters.TransactionListAdapter
import com.aksar.dungru.databinding.FragmentWithdrawBinding
import com.aksar.dungru.models.TransactionListDataModel
import com.aksar.dungru.utils.Constance

class WithdrawFragment : Fragment() {
    private lateinit var binding:FragmentWithdrawBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentWithdrawBinding.inflate(layoutInflater)

        val transactionList = listOf(
            TransactionListDataModel(1, "Withdraw", "29 September 10.25 Am", "200"),
            TransactionListDataModel(1, "Withdraw", "29 September 09.15 Am", "100"),
            TransactionListDataModel(1, "Withdraw", "29 September 07.55 Am", "500"),
            TransactionListDataModel(1, "Withdraw", "29 September 07.05 Am", "100"),
            TransactionListDataModel(1, "Withdraw", "29 September 06.10 Am", "900"),
            TransactionListDataModel(1, "Withdraw", "29 September 05.19 Am", "400"),
            TransactionListDataModel(1, "Withdraw", "29 September 01.50 Am", "600"),
            TransactionListDataModel(1, "Withdraw", "29 September 03.23 Am", "900")
        )
        binding.recWithdrawFragment.adapter = TransactionListAdapter(requireContext(), transactionList,Constance.Wallet_History) {

        }

        return binding.root
    }

}