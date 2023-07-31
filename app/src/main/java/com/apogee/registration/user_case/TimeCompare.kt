package com.apogee.registration.user_case

class TimeCompare {
    companion object {
        fun isTimeOut(startTime: Long, endTime: Long): Boolean {
            val timeDifferenceInSeconds = (endTime - startTime) / 1000
            return timeDifferenceInSeconds > 26
        }

    }
}