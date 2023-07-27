package com.apogee.registration.model

data class DeviceRegModel(
    var imei: String?,
    val manufacturer: String,
    val deviceType: String,
    val modelName: String,
    val modelNo: String,
    val subDate: String

) {
    val deviceRegRequestBody: String
        get() = "$manufacturer,$deviceType,$modelName,$modelNo,10,20,$imei"


    val subscriptionDateRequestBody: String
        get() = "$imei,$subDate"


}
