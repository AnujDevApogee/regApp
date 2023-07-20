package com.apogee.registration.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import androidx.core.view.isVisible


fun View.hide() {
    this.isVisible = false
}

fun View.show() {
    this.isVisible = true
}


fun View.invisible() {
    this.visibility = View.INVISIBLE
}


fun Activity.closeKeyboard(view: View) {
    val imm =
        (this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
    imm.hideSoftInputFromWindow(view.windowToken, 0)
    this.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
}


inline fun <reified T> Activity.goToNextActivity(isRemoveBckStg: Boolean = false) {
    Intent(this, T::class.java).also {
        startActivity(it)
        if (isRemoveBckStg) {
            this.finishAffinity()
        }
    }
}

fun Activity.openKeyBoard(view: View) {
    val imm =
        getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
    imm!!.toggleSoftInput(
        InputMethodManager.SHOW_FORCED,
        InputMethodManager.HIDE_IMPLICIT_ONLY
    )
    view.requestFocus()
}