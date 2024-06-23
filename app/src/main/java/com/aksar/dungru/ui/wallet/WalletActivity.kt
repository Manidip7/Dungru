package com.aksar.dungru.ui.wallet

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.View.OnClickListener
import androidx.appcompat.app.AppCompatActivity
import com.aksar.dungru.adapters.TransactionListAdapter
import com.aksar.dungru.databinding.ActivityWalletBinding
import com.aksar.dungru.models.TransactionListDataModel
import com.aksar.dungru.ui.wallet.addcoin.GetCoinActivity
import com.aksar.dungru.utils.Constance.Wallet_History
import com.aksar.dungru.utils.NetworkUtils

class WalletActivity : AppCompatActivity(), OnClickListener {
    lateinit var binding: ActivityWalletBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWalletBinding.inflate(layoutInflater)

        setContentView(binding.root)

        NetworkUtils.checkConnection(this)

        val transactionList = listOf(
            TransactionListDataModel(0, "Get coin", "29 September 10.30 Am", "100"),
            TransactionListDataModel(1, "Withdraw", "29 September 10.25 Am", "200"),
            TransactionListDataModel(0, "Get coin", "29 September 10.05 Am", "100"),
            TransactionListDataModel(0, "Get coin", "29 September 09.45 Am", "300"),
            TransactionListDataModel(1, "Withdraw", "29 September 09.15 Am", "100"),
            TransactionListDataModel(0, "Get coin", "29 September 08.05 Am", "100"),
            TransactionListDataModel(1, "Withdraw", "29 September 07.55 Am", "500"),
            TransactionListDataModel(1, "Withdraw", "29 September 07.05 Am", "100"),
            TransactionListDataModel(0, "Get coin", "29 September 06.20 Am", "200"),
            TransactionListDataModel(1, "Withdraw", "29 September 06.10 Am", "100"),
            TransactionListDataModel(0, "Get coin", "29 September 05.05 Am", "300")
        )
        binding.transactionRecycler.adapter = TransactionListAdapter(this, transactionList,Wallet_History) {

        }
    }

    override fun onClick(view: View?) {
        when (view) {
            binding.btnGetCoin -> {
                startActivity(
                    Intent(
                        this,
                        GetCoinActivity::class.java
                    )
                )
            }

            binding.btnWithdrawMoney -> startActivity(
                Intent(
                    this,
                    WithdrawMoneyActivity::class.java
                )
            )

            binding.btnBack -> onBackPressed()
        }
    }

}