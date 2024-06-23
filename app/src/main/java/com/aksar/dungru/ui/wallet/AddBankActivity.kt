package com.aksar.dungru.ui.wallet

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.aksar.dungru.R
import com.aksar.dungru.databinding.ActivityAddBankBinding
import com.aksar.dungru.databinding.SuccessfullDiglogBinding
import com.aksar.dungru.utils.Constance.Amount
import com.aksar.dungru.utils.NetworkUtils

class AddBankActivity : AppCompatActivity(), OnClickListener {
    private var selectedAmount = 0
    private lateinit var binding: ActivityAddBankBinding
    private var isSuccessDialogOpen = false

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddBankBinding.inflate(layoutInflater)
        setContentView(binding.root)

        NetworkUtils.checkConnection(this)

        selectedAmount = intent.getIntExtra(Amount, 0)

        binding.txtRequestAmount.text = "${getString(R.string.rupee)} $selectedAmount"

        binding.etAccountNo.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(enterText: Editable?) {
                if (enterText.toString().length < 11) {
                    binding.etAccountNo.error = "Invalid Account No"
                }
            }
        })

        binding.etConfirmAccountNo.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(enterText: Editable?) {
                if (enterText.toString().length < 11) {
                    binding.etConfirmAccountNo.error = "Invalid Account No"
                }
            }
        })
    }

    private fun showSuccessDialog() {
        isSuccessDialogOpen = true
        val dialogBinding = SuccessfullDiglogBinding.inflate(layoutInflater)
        val dialog = Dialog(this, R.style.FullScreenDialogTheme)
        dialog.setContentView(dialogBinding.root)
        dialog.setCancelable(false)
        dialog.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        dialog.show()
        dialogBinding.btnWallet.setOnClickListener {
            val intent = Intent(this, WalletActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            dialog.dismiss()
        }
    }

//    override fun onBackPressed() {
//        return if (isSuccessDialogOpen) {
////            val intent = Intent(this, WalletActivity::class.java)
////            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
////            startActivity(intent)
//            finish()
//            super.onBackPressed()
//        } else {
//            super.onBackPressed()
//        }
//    }

    override fun onClick(view: View?) {
        when (view) {
            binding.btnBack -> onBackPressed()

            binding.btnProceed -> {
//            val isallfilded = areAllFieldsFilled(
//                binding.etAccountHolderName,
//                binding.IfscCode,
//                binding.etAccountNo,
//                binding.etConfirmAccountNo
//            )
//            if (isallfilded) {
                showSuccessDialog()
//       }
            }
        }
    }


}