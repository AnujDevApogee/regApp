package com.apogee.registration.user_case

class ImeiNumber {
    companion object {
        fun getImeiNumber(res: String): String? {
            return if (res.contains("$$$$,03") || res.contains("$$$$,3")) {
                val split = res.split(",".toRegex())
                if (split.last().contains("####")) {
                    split[4]
                } else {
                    null
                }
            } else {
                null
            }
        }
    }
}