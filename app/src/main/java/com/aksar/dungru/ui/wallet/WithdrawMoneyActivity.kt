package com.aksar.dungru.ui.wallet

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.View.GONE
import android.view.View.OnClickListener
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.aksar.dungru.R
import com.aksar.dungru.adapters.WalletCoinAdapter
import com.aksar.dungru.adapters.WithdrawBankDetailsAdapter
import com.aksar.dungru.databinding.ActivityWithdrawMoneyBinding
import com.aksar.dungru.databinding.AddBankDetailsDiglogBinding
import com.aksar.dungru.databinding.AddUpiDiglogBinding
import com.aksar.dungru.databinding.ConfirmationDiglogBinding
import com.aksar.dungru.databinding.SuccessfullDiglogBinding
import com.aksar.dungru.models.BankDetailsModel
import com.aksar.dungru.models.WalletCoinDataModel
import com.aksar.dungru.ui.setting.TransactionActivity
import com.aksar.dungru.utils.Constance.Amount
import com.aksar.dungru.utils.NetworkUtils
import com.google.android.material.bottomsheet.BottomSheetDialog

class WithdrawMoneyActivity : AppCompatActivity(), OnClickListener,
    WithdrawBankDetailsAdapter.BankButtonClickListener {
    private lateinit var binding: ActivityWithdrawMoneyBinding
    private lateinit var bankList: List<BankDetailsModel>
    private lateinit var bankDetailsAdapter: WithdrawBankDetailsAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWithdrawMoneyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        NetworkUtils.checkConnection(this)

        val walletCoinList = listOf(
            WalletCoinDataModel(100, 80),
            WalletCoinDataModel(200, 170),
            WalletCoinDataModel(500, 450),
            WalletCoinDataModel(1000, 980),
        )
        binding.walletCoinRecycler.adapter = WalletCoinAdapter(walletCoinList) {
            binding.etAmount.setText(it.toString())
        }

        /** This list will fetch from api*/
        bankList = listOf(
            BankDetailsModel(
                R.drawable.ic_bank,
                "SBI",
                "Joyjit Bhandari",
                "1234567890000",
                "SBI012345"
            ),
            BankDetailsModel(
                R.drawable.ic_bank,
                "Union Bank Of India",
                "Joyjit Bhandari",
                "1234567890000",
                "SBI012345"
            ),
            BankDetailsModel(
                R.drawable.ic_bank,
                "HDFC Bank",
                "Joyjit Bhandari",
                "1234567890000",
                "SBI012345"
            )
        )
        bankDetailsAdapter = WithdrawBankDetailsAdapter(bankList, this)
        binding.recyclerBankBtnView.adapter = bankDetailsAdapter

        if (bankList.isNotEmpty()) {
            binding.bankListLayout.visibility = VISIBLE
            binding.btnAddBankAcc.visibility = VISIBLE
            binding.btnBankFirstTime.visibility = GONE

        } else {
            binding.bankListLayout.visibility = GONE
            binding.btnAddBankAcc.visibility = GONE
            binding.btnBankFirstTime.visibility = VISIBLE
        }
    }

    override fun onClick(view: View?) {
        when (view) {
            binding.btnBack -> onBackPressed()
            binding.btnBankFirstTime -> {
                if (binding.etAmount.text.toString().isNotEmpty()) {
                    val selectedAmount = binding.etAmount.text.toString().toInt()
                    val intent = Intent(this, AddBankActivity::class.java)
                    intent.putExtra(Amount, selectedAmount)
                    startActivity(intent)
                } else
                    Toast.makeText(this, "Please select the amount above.", Toast.LENGTH_SHORT)
                        .show()
            }

            binding.btnUpi -> {
                if (binding.etAmount.text.toString().isNotEmpty()) showUpiDialog()
                else Toast.makeText(this, "Please select the amount above.", Toast.LENGTH_SHORT).show()
            }

            binding.btnAddBankAcc -> showAddBankDetailsDialog()

        }
    }

    private fun showUpiDialog() {
        val dialogBinding = AddUpiDiglogBinding.inflate(layoutInflater)
        val dialog = BottomSheetDialog(this)
        dialog.setContentView(dialogBinding.root)
        dialog.show()
        dialogBinding.proceed.setOnClickListener {
            dialog.dismiss()
            showSuccessDialog()
        }
    }

    private fun showAddBankDetailsDialog() {
        val addBankDialog = BottomSheetDialog(this)
        val addBankDialogBinding = AddBankDetailsDiglogBinding.inflate(layoutInflater)
        addBankDialog.setContentView(addBankDialogBinding.root)
        addBankDialogBinding.btnAddBank.setOnClickListener {
            /** Add bank api call */
            addBankDialog.dismiss()
        }
        addBankDialog.show()
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
        dialog.show()
        dialogBinding.btnTransactionHistory.setOnClickListener {
            val intent = Intent(this, TransactionActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            dialog.dismiss()
        }
        dialogBinding.btnWallet.setOnClickListener {
            val intent = Intent(this, WalletActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            dialog.dismiss()
        }
    }

    private fun showConfirmationDialog() {
        val dialogBinding = ConfirmationDiglogBinding.inflate(layoutInflater)
        val dialog = BottomSheetDialog(this)
        dialog.setContentView(dialogBinding.root)
        dialog.show()
        dialogBinding.btnSubmit.setOnClickListener {
            dialog.dismiss()
            showSuccessDialog()
        }
        dialogBinding.btnCancel.setOnClickListener {
            dialog.dismiss()
        }
    }

    override fun bankSelected(position: Int, item: BankDetailsModel) {
        if (binding.etAmount.text.toString().isNotEmpty()) {
            bankDetailsAdapter.notifyItemChanged(position)
            showConfirmationDialog()
        } else
            Toast.makeText(this, "Please select the amount above.", Toast.LENGTH_SHORT).show()
    }
    override fun bankDelete(position: Int) {

    }
}