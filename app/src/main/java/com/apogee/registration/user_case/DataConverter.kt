package com.apogee.registration.user_case

import java.text.SimpleDateFormat
import java.util.Locale

class DataConverter {

    companion object{
        fun getConvertDate(date: String): Int {
            try {
                val sdf = SimpleDateFormat(
                    "dd/MM/yyyy", Locale.ENGLISH
                )
                val datestart = sdf.format(System.currentTimeMillis())
                val start = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH).parse(datestart) ?: return -1
                val end = SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH).parse(date) ?: return -1
                val mDifference = ((end.time) - start.time).toDouble()
                val noOfDays = mDifference / (24 * 60 * 60 * 1000)
                return if (noOfDays != 0.0 && noOfDays > 0) {
                    noOfDays.toInt()
                } else {
                    -1
                }
            } catch (e: Exception) {
                return -1
            }
        }
    }

}