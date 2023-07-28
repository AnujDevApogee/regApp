package com.apogee.registration.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.text.Html
import android.text.SpannableString
import android.text.Spanned
import android.text.style.StyleSpan
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.text.HtmlCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.gson.Gson
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.TimeZone


object ApiUrl {
    private const val BASE_URL="http://120.138.10.146:8080"
    val loginUrl = Pair("$BASE_URL/login_module/api/getLoginPersonData/", 1)
    val deviceRegUrl = Pair("$BASE_URL/RegistrationCumAllotment/deviceRegRecords/", 2)
    val deviceSubscriptionUrl = Pair("$BASE_URL/RegistrationCumAllotment/deviceSubscriptionDate/", 3)
    val deviceRegRecordsConformationUrl = Pair("$BASE_URL/RegistrationCumAllotment/deviceRegRecordsConformation/", 4)
    val bleSubscriptionStatus = Pair("$BASE_URL/RegistrationCumAllotment/bleSubscriptionStatus/", 5)
    val bleStatusCheck = Pair("$BASE_URL/RegistrationCumAllotment/bleStatusCheck/", 6)
    val saveDevInfoReg = Pair("$BASE_URL/RegistrationCumAllotment/savePersonDeviceRegistration/", 7)
    const val loginProjectName = "RegistrationCumAllotment"
}

object BleCmd{
    const val imeiNumber="$$$$,03,03,3,1,0,0000,####"
}

enum class BleHelper {
    IEMINUMBER,
    DEVICEREGRECORD
}



var newline_crlf = "\r\n"

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

fun createLog(tag: String, msg: String) {
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


fun checkVaildString(string: String?) =
    string.isNullOrEmpty() || string.isBlank() || string == "null"


fun setHtmlTxt(txt: String, color: String): Spanned {
    return Html.fromHtml(
        "<font color=$color>$txt</font>", HtmlCompat.FROM_HTML_MODE_COMPACT
    )
}

fun setHtmlBoldTxt(txt: String): SpannableString {
    val ss = SpannableString(txt)
    val boldSpan = StyleSpan(Typeface.BOLD)
    ss.setSpan(boldSpan, 0, txt.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
    return ss
}

fun Activity.showToastMsg(msg: String) {
    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
}

fun Fragment.showToastMsg(msg: String) {
    Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show()
}

fun Activity.getColorInt(color: Int): Int {
    return resources.getColor(color, null)
}

fun NavController.safeNavigate(direction: NavDirections) {
    currentDestination?.getAction(direction.actionId)?.run {
        navigate(direction)
    }
}


fun getEmojiByUnicode(unicode: Int) = String(Character.toChars(unicode))

fun NavController.safeNavigate(direction: Int) {
    currentDestination?.getAction(direction)?.run {
        navigate(direction)
    }
}


fun calenderPicker(
    fragmentManager: FragmentManager, cancel: () -> Unit, dateListener: (txt: String) -> Unit
) {
    val constraint = CalendarConstraints.Builder().setValidator(DateValidatorPointForward.now())
    val datePicker = MaterialDatePicker.Builder.datePicker().setTitleText("Select Booking Date")
        .setCalendarConstraints(constraint.build()).build()


    datePicker.addOnCancelListener {
        cancel.invoke()
        it.dismiss()
    }

    datePicker.addOnDismissListener {
        cancel.invoke()
        it.dismiss()
    }

    datePicker.addOnPositiveButtonClickListener { time ->
        val calendar: Calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
        calendar.timeInMillis = time
        val format = SimpleDateFormat("dd/MM/yyyy")//"yyyy-MM-dd"
        val formattedDate: String = format.format(calendar.time)
        dateListener.invoke(formattedDate)
    }
    datePicker.show(fragmentManager, "DatePicker")
}