package com.aksar.dungru.ui.wallet.addcoin

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.View.GONE
import android.view.View.OnClickListener
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.aksar.dungru.R
import com.aksar.dungru.databinding.ActivityCoinPamentMethodBinding
import com.aksar.dungru.databinding.CardDetailsDiglogBinding
import com.aksar.dungru.databinding.SuccessfullDiglogBinding
import com.aksar.dungru.models.CardModel
import com.aksar.dungru.ui.payment.PaymentSettingActivity
import com.aksar.dungru.ui.setting.TransactionActivity
import com.aksar.dungru.ui.wallet.WalletActivity
import com.google.android.material.bottomsheet.BottomSheetDialog

class CoinPaymentMethod : AppCompatActivity(), OnClickListener {

    private lateinit var binding: ActivityCoinPamentMethodBinding
    private lateinit var cardData: ArrayList<CardModel>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCoinPamentMethodBinding.inflate(layoutInflater)
        setContentView(binding.root)

        cardData = arrayListOf(
            CardModel("123456789012","Joyjit", "10/25", "123")
        )

        if (cardData.isNotEmpty()) {
            binding.atmCardLayout.visibility = VISIBLE

            binding.txtCardHolderName.text = cardData[0].cardholderName
            binding.txtCardExpiryDate.text = cardData[0].cardExp
            binding.txtCardNumber.text = cardData[0].cardNo
            binding.btnCard.visibility = GONE
        } else {
            binding.atmCardLayout.visibility = GONE
            binding.btnCard.visibility = VISIBLE
        }
    }
    private fun showAddCardDialog() {
        val dialogBinding = CardDetailsDiglogBinding.inflate(layoutInflater)
        val dialog = BottomSheetDialog(this)
        dialog.setContentView(dialogBinding.root)
        dialog.setOnCancelListener {
            binding.ChBoxAddCard.toggle()
        }
        dialogBinding.btnAddCard.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    override fun onClick(view: View?) {
        when (view) {
            binding.btnBack -> onBackPressed()
            binding.btnCard -> {
                binding.ChBoxAddCard.toggle()
                binding.ChBoxAddUpi.isChecked = false
                showAddCardDialog()
            }
            binding.btnUpi -> {
                binding.ChBoxAddCard.isChecked = false
                binding.ChBoxAddUpi.toggle()
            }
            binding.btnChangeCard -> {
                startActivity(Intent(this, PaymentSettingActivity::class.java))
            }
            binding.btnProceed -> showSuccessDialog()
        }
    }

    private fun showSuccessDialog() {
        val dialogBinding = SuccessfullDiglogBinding.inflate(layoutInflater)
        val dialog = Dialog(this, R.style.FullScreenDialogTheme)
        dialog.setContentView(dialogBinding.root)
        dialog.setCancelable(false)
        dialog.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        dialogBinding.txtSubmitSuccessful.text = "Coin added successfully."
        dialogBinding.txtSuccessfulSubHeading.visibility = GONE
        dialog.show()
        dialogBinding.btnWallet.setOnClickListener {
            val intent = Intent(this, WalletActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            dialog.dismiss()
        }
        dialogBinding.btnTransactionHistory.setOnClickListener {
            startActivity(Intent(this,TransactionActivity::class.java))
        }
    }

}