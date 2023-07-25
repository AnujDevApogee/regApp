package com.apogee.registration.adaptor

import android.annotation.SuppressLint
import android.bluetooth.le.ScanResult
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.apogee.registration.databinding.BleDeviceItemBinding
import com.apogee.registration.utils.setHtmlBoldTxt
import com.apogee.registration.utils.setHtmlTxt


typealias itemClicked = (data: ScanResult) -> Unit

@SuppressLint("MissingPermission")
class BleDeviceAdaptor(
    private val itemClicked: itemClicked
) :
    ListAdapter<ScanResult, BleDeviceAdaptor.BleDeviceViewHolder>(diffUtil) {
    inner class BleDeviceViewHolder(private val binding: BleDeviceItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun setData(
            data: ScanResult,
            itemClicked: itemClicked
        ) {
            binding.connectionInfo.text = setHtmlBoldTxt("Device Name ")
            binding.connectionInfo.append(setHtmlTxt(data.device.name.toString(), "'#EC938F'"))
            binding.connectionInfo.append("\n")
            binding.connectionInfo.append("\n")
            binding.connectionInfo.append(setHtmlBoldTxt("MAC ADDRESS "))
            binding.connectionInfo.append(setHtmlTxt(data.device.address.toString(), "'#EC938F'"))
            binding.connectionInfo.append("\n")

            binding.cardView.setOnClickListener {
                itemClicked.invoke(data)
            }

        }
    }


    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<ScanResult>() {
            override fun areItemsTheSame(
                oldItem: ScanResult,
                newItem: ScanResult
            ) = oldItem.device.address == newItem.device.address

            override fun areContentsTheSame(
                oldItem: ScanResult,
                newItem: ScanResult
            ) = oldItem == newItem
        }

       /* fun isConnected(device: BluetoothDevice): Boolean? {
            return try {
                val m: Method = device.javaClass.getMethod("isConnected")
                m.invoke(device) as Boolean
            } catch (e: Exception) {
                createLog("BLE_CONNECT", "isConnected: ${e.localizedMessage}")
                null
            }
        }*/
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BleDeviceViewHolder {
        val binding =
            BleDeviceItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BleDeviceViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BleDeviceViewHolder, position: Int) {
        val currItem = getItem(position)
        currItem?.let {
            holder.setData(it, itemClicked)
        }
    }

}