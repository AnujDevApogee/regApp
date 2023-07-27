package com.apogee.registration.user_case

class BlueProtocolFilter {
    companion object {
        fun getImeiNumber(res: String): String? {
            return if (res.contains("$$$$,03") || res.contains("$$$$,3")
                || res.contains("$$$$,0003")
                || res.contains("$$$$,00003")
                || res.contains("$$$$,000003")
            ) {
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


        fun getDeviceGeogProtocol(checkString: String): String? {
            return if (checkString.contains("$$$$,4")
                || checkString.contains("$$$$,04")
                || checkString.contains("$$$$,0004")
                || checkString.contains("$$$$,00004")
                || checkString.contains("$$$$,000004")
            ) {
                checkString
            } else {
                null
            }
        }

    }
}