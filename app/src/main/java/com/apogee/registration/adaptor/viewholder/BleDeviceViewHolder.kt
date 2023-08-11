package com.apogee.registration.adaptor.viewholder

import android.annotation.SuppressLint
import android.bluetooth.le.ScanResult
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.apogee.registration.adaptor.itemClicked
import com.apogee.registration.databinding.BleDeviceItemBinding
import com.apogee.registration.databinding.ProgressLayoutDeviceItemBinding
import com.apogee.registration.model.BleDeviceConnection
import com.apogee.registration.utils.setHtmlBoldTxt
import com.apogee.registration.utils.setHtmlTxt

sealed class BleDeviceViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    class ProgressViewHolder(private val binding: ProgressLayoutDeviceItemBinding) :
        BleDeviceViewHolder(binding.root) {
        fun setData(data: BleDeviceConnection.BleDeviceMessage) {
            binding.pbTitle.text = data.msg
        }
    }

    class BleDevice(private val binding: BleDeviceItemBinding) :
        BleDeviceViewHolder(binding.root) {
        @SuppressLint("MissingPermission")
        fun setData(
            data: BleDeviceConnection.BleDevice,
            itemClicked: itemClicked
        ) {
            binding.connectionInfo.text = setHtmlBoldTxt("Device Name ")
            binding.connectionInfo.append(setHtmlTxt(data.scanResult?.device?.name.toString(), "'#EC938F'"))
            binding.connectionInfo.append("\n")
            binding.connectionInfo.append("\n")
            binding.connectionInfo.append(setHtmlBoldTxt("MAC ADDRESS "))
            binding.connectionInfo.append(setHtmlTxt(data.scanResult?.device?.address.toString(), "'#EC938F'"))
            binding.connectionInfo.append("\n")

            binding.cardView.setOnClickListener {
                itemClicked.invoke(data.scanResult!!)
            }

        }
    }

}