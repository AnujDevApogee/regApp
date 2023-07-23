package com.apogee.registration.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import androidx.core.view.isVisible
import com.google.gson.Gson


object ApiUrl{
    val loginUrl=Pair("http://120.138.10.146:8080/login_module/api/getLoginPersonData/",1)
    val loginProjectName="RegistrationCumAllotment"
}

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

fun createLog(tag:String,msg:String){
    Log.i(tag, "createLog: $msg")
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

fun <T> serializeToJson(bmp: T): String? {
    val gson = Gson()
    return gson.toJson(bmp)
}


inline fun <reified T> deserializeFromJson(jsonFile: String?): T? {
    val gson = Gson()
    return gson.fromJson(jsonFile, T::class.java)
}

fun checkVaildString(string: String?) = string.isNullOrEmpty() || string.isBlank() || string=="null"