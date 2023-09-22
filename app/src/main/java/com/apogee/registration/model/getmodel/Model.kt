package com.apogee.registration.model.getmodel

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "ModelInfo")
data class Model(
    @ColumnInfo("DeviceAddress") val device_address: String,
    @ColumnInfo("DeviceName") val device_name: String,
    @ColumnInfo("DeviceNo") val device_no: String,
    @PrimaryKey(autoGenerate = false) @ColumnInfo("ModelId") val model_id: Int,
    @ColumnInfo("ModelTypeID") val model_type_id: Int,
    @ColumnInfo("NoOfModule") val no_of_module: Int,
    @ColumnInfo("Remark") val remark: String?,
    @ColumnInfo("WarrantyType") val warranty_period: String
)