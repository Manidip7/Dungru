package com.aksar.dungru.ui.auth

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import com.aksar.dungru.R

class OtpWatcher(
    private val currentEditText: EditText,
    private val nextEditText: EditText?,
    private val previousEditText: EditText?,
) : TextWatcher {
    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
    override fun afterTextChanged(s: Editable?) {
        if (s.isNullOrEmpty()) {
            previousEditText?.requestFocus()
            currentEditText.setBackgroundResource(R.drawable.otp_empty_shape)
        } else {
            nextEditText?.requestFocus()
            currentEditText.setBackgroundResource(R.drawable.otp_filled_shape)
        }
    }
}

