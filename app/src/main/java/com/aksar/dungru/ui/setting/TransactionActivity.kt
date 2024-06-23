package com.aksar.dungru.ui.setting

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.RadioButton
import android.widget.RadioGroup
import com.aksar.dungru.R
import com.aksar.dungru.databinding.ActivityTransactionBinding
import com.aksar.dungru.ui.MyApp
import com.aksar.dungru.ui.setting.transactionFragment.AddFragment
import com.aksar.dungru.ui.setting.transactionFragment.WithdrawFragment
import com.aksar.dungru.utils.NetworkUtils
import com.aksar.dungru.utils.extensions.addFragment
import com.aksar.dungru.utils.extensions.replaceFragment

class TransactionActivity : AppCompatActivity() {
    private lateinit var binding:ActivityTransactionBinding
    private var text :String =""
    @SuppressLint("SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTransactionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val radioGroup = findViewById<RadioGroup>(R.id.btn_radio_group)

            addFragment(binding.loaderAddWithdraw.id,AddFragment(),false)
            radioGroup.setOnCheckedChangeListener { _, checkedId ->
                val radioButton = findViewById<RadioButton>(checkedId)
                text = radioButton.text.toString()
                when (text) {
                    "Add" -> {
                        binding.btnAddNew.setBackgroundResource(R.drawable.bg_getcoin_withdraw)
                        binding.btnWithdraw.setBackgroundResource(R.drawable.bg_transparent)
                        binding.btnAddNew.setTextColor(resources.getColor(R.color.white))
                        binding.btnWithdraw.setTextColor(resources.getColor(R.color.black))
                        replaceFragment(binding.loaderAddWithdraw.id,AddFragment(),false)
                    }
                    "Withdraw" -> {
                        binding.btnWithdraw.setBackgroundResource(R.drawable.bg_getcoin_withdraw)
                        binding.btnAddNew.setBackgroundResource(R.drawable.bg_transparent)
                        binding.btnWithdraw.setTextColor(resources.getColor(R.color.white))
                        binding.btnAddNew.setTextColor(resources.getColor(R.color.black))
                        replaceFragment(binding.loaderAddWithdraw.id,WithdrawFragment(),false)
                    }
                }
            }
        binding.btnBack.setOnClickListener {
            onBackPressed()
        }
    }

    override fun onStart() {
        super.onStart()
        NetworkUtils.checkConnection(this)
    }
}