package com.aksar.dungru.utils.extensions

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.text.InputType
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup.LayoutParams
import android.view.Window
import android.view.WindowInsets
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.aksar.dungru.R
import com.aksar.dungru.databinding.LoadingDialogBinding

fun View.hideKeyboard() {
    val keyboard = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    keyboard.hideSoftInputFromWindow(windowToken, 0)
}

/** Toast Message visibility*/
fun Activity.showToast(msg: String, isLong: Boolean?) {
    when (isLong) {
        true -> Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
        false -> Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
        else -> Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }
}

fun Fragment.showToast(msg: String, isLong: Boolean?) {
    when (isLong) {
        true -> Toast.makeText(requireContext(), msg, Toast.LENGTH_LONG ).show()
        false -> Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
        else -> Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
    }
}

/** Loading view visibility*/
lateinit var dialog: Dialog
fun Activity.showProgressDialog(animation: Int) {
    dialog = Dialog(this)
    val binding = LoadingDialogBinding.inflate(layoutInflater)
    dialog.apply {
        setContentView(binding.root)
        setCancelable(false)
        window?.let { window ->
            window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            window.setLayout(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        } ?: run {
            Log.e("CustomDialog", "Window is null")
        }
    }.show()
    binding.loading.setAnimation(animation)
    binding.loading.playAnimation()
}

fun Fragment.showProgressDialog(animation: Int) {
    dialog = Dialog(context as Activity)
    val binding = LoadingDialogBinding.inflate(layoutInflater)
    dialog.apply {
        setContentView(binding.root)
        setCancelable(false)
        window?.let { window ->
            window.setLayout(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
            window.setBackgroundDrawable(resources.getDrawable(R.drawable.bg_transparent))
        } ?: run {
            Log.e("CustomDialog", "Window is null")
        }

    }.show()
    binding.loading.setAnimation(animation)
    binding.loading.playAnimation()
}

fun Activity.hideProgressDialog() {
    dialog.let { it.dismiss() }
}

fun Fragment.hideProgressDialog() {
    dialog.let { it.dismiss() }
}

@SuppressLint("ClickableViewAccessibility", "UseCompatLoadingForDrawables")
fun setPasswordVisible(context: Context, editText: EditText?) {
    editText?.setOnTouchListener { v, event ->
        val drawableEnd = editText.compoundDrawablesRelative[2]
        if (event.action == MotionEvent.ACTION_DOWN) {
            if (drawableEnd != null && event.rawX >= editText.right - drawableEnd.bounds.width()) {
                editText.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD // show
                editText.setCompoundDrawablesRelativeWithIntrinsicBounds(
                    context.resources.getDrawable(
                        R.drawable.ic_et_password
                    ), null, context.resources.getDrawable(R.drawable.ic_show_password), null
                )
                editText.setSelection(editText.length())
                return@setOnTouchListener true
            }
        } else if (event.action == MotionEvent.ACTION_UP || event.action == MotionEvent.ACTION_CANCEL) {
            if (drawableEnd != null && event.rawX >= editText.right - drawableEnd.bounds.width()) {
                editText.inputType =
                    InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD // hide
                editText.setCompoundDrawablesRelativeWithIntrinsicBounds(
                    context.resources.getDrawable(
                        R.drawable.ic_et_password
                    ), null, context.resources.getDrawable(R.drawable.ic_hide_password), null
                )
                editText.setSelection(editText.length())
                return@setOnTouchListener true
            }
        }
        false
    }
}

fun adjustSystemNavigationBar(window: Window, contentView: View) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        window.decorView.setOnApplyWindowInsetsListener { view, windowInsets ->
            val insets = windowInsets.getInsetsIgnoringVisibility(
                WindowInsets.Type.systemBars() or WindowInsets.Type.displayCutout()
            )
            val navigationBarHeight = insets.bottom
            contentView.setPadding(0, 0, 0, navigationBarHeight)
            windowInsets
        }
    }
}

