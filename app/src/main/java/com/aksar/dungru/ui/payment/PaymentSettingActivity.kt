package com.aksar.dungru.ui.payment

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.View.OnClickListener
import android.widget.Toast
import com.aksar.dungru.R
import com.aksar.dungru.adapters.BankSettingAdapter
import com.aksar.dungru.adapters.CardShowAdapter
import com.aksar.dungru.databinding.ActivityPaymentSettingBinding
import com.aksar.dungru.databinding.AddBankDetailsDiglogBinding
import com.aksar.dungru.databinding.CardDetailsDiglogBinding
import com.aksar.dungru.models.BankDetailsModel
import com.aksar.dungru.models.CardModel
import com.aksar.dungru.utils.NetworkUtils
import com.aksar.dungru.utils.setPreviewBothSideWithScale
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.tabs.TabLayoutMediator

class PaymentSettingActivity : AppCompatActivity(),OnClickListener,
    CardShowAdapter.CardClickListener {
    private lateinit var binding: ActivityPaymentSettingBinding
    private lateinit var cardList: ArrayList<CardModel>
    private lateinit var bankList : ArrayList<BankDetailsModel>
    private lateinit var bankSettingAdapter: BankSettingAdapter
    private lateinit var cardShowAdapter: CardShowAdapter
    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPaymentSettingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        NetworkUtils.checkConnection(this)

        cardList = arrayListOf(
            CardModel("123456789012","Joyjit", "10/25", "123"),
            CardModel("123456789012","Joyjit123", "10/25", "123"),
        )
        cardShowAdapter =  CardShowAdapter(cardList,this)
        binding.viewPager.adapter = cardShowAdapter
        binding.viewPager.setPreviewBothSideWithScale(nextItemVisibleSize = 20, currentItemHorizontalMargin = 20)

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->

        }.attach()


        bankList = arrayListOf(
            BankDetailsModel(R.drawable.ic_bank,"State Bank Of India","Joyjit Bhandari","12345678901","SBI000123"),
            BankDetailsModel(R.drawable.ic_bank,"Bank Of Baroda","Joyjit Bhandari","12345678901","BOB000123"),
            BankDetailsModel(R.drawable.ic_bank,"Punjab Bank of India","Joyjit Bhandari","12345678901","PNB000123")
        )

        bankSettingAdapter = BankSettingAdapter(bankList){position, id ->
            bankSettingAdapter.notifyItemRemoved(position)
            bankSettingAdapter.notifyDataSetChanged()
            Toast.makeText(this, "$id is removed" , Toast.LENGTH_SHORT).show()

        }
        binding.bankRecycler.adapter = bankSettingAdapter

    }

    override fun onClick(view: View?) {
        when(view){
            binding.btnBack -> onBackPressed()
            binding.btnAddCard -> showAddCardDialog()
            binding.btnAddBank -> showAddBankDetailsDialog()
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

    private fun showAddCardDialog() {
        val dialogBinding = CardDetailsDiglogBinding.inflate(layoutInflater)
        val dialog = BottomSheetDialog(this)
        dialog.setContentView(dialogBinding.root)
        dialogBinding.btnAddCard.setOnClickListener {
            /** api call to add card*/
            dialog.dismiss()
        }

        var digitsOnly: String? = null

        dialogBinding.etCardNo.addTextChangedListener(object :TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {

//                CardNo
               dialogBinding.etCardNo.removeTextChangedListener(this)
                    if (s != null && s.toString().length == 19){
                        digitsOnly = s.toString().replace("[^\\d]".toRegex(), "")
                        Log.d( "hi1",digitsOnly.toString())
                        dialogBinding.etCardNo.clearFocus()
                    }
                    else{
                        dialogBinding.etCardNo.isEnabled = true
                        if (s.toString().length % 5 == 4){
                            val formattedCardNo = s.toString() + "-"

                            dialogBinding.etCardNo.setText(formattedCardNo)

                            dialogBinding.etCardNo.setSelection(formattedCardNo.length)
                        }
                    }
                    dialogBinding.etCardNo.addTextChangedListener(this)
            }
        })

        //ExpDate
        dialogBinding.etExpDate.addTextChangedListener(object :TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                if (s != null){
                    val inputExpiryDate = s.toString().replace("[^\\d]".toRegex(), "")
                    Log.d("afterTextChanged: ",inputExpiryDate)
                    if (s != null && s.length == 2 && !s.contains("/")) {
                        dialogBinding.etExpDate.setText("${s}/")
                        dialogBinding.etExpDate.text?.let { dialogBinding.etExpDate.setSelection(it.length) }
                    } else if (s != null && s.length == 5) {
                        dialogBinding.etExpDate.clearFocus()
                    }
                }
            }
        })

        //cvv
        dialogBinding.etCvv.addTextChangedListener(object :TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                val inputText = s.toString()
                Log.d("afterTextChanged: ",inputText)
                // Check if the length of the text exceeds 3 digits
                if (inputText.length > 3) {
                    // If it exceeds 3 digits, remove the extra characters
                    dialogBinding.etCvv.setText(inputText.substring(0, 3))
                    // Move the cursor to the end of the text
                    dialogBinding.etCvv.setSelection(3)
                }
            }
        })
        dialog.show()
    }

    override fun defaultButtonClicked(position: Int, item: CardModel) {

    }

    @SuppressLint("NotifyDataSetChanged")
    override fun deleteButtonClicked(position: Int) {
        cardShowAdapter.notifyItemRemoved(position)
        cardShowAdapter.notifyDataSetChanged()
    }
}