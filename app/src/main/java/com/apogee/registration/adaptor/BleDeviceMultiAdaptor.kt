package com.apogee.registration.adaptor

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import com.apogee.registration.R
import com.apogee.registration.adaptor.viewholder.BleDeviceViewHolder
import com.apogee.registration.databinding.BleDeviceItemBinding
import com.apogee.registration.databinding.ProgressLayoutDeviceItemBinding
import com.apogee.registration.model.BleDeviceConnection

class BleDeviceMultiAdaptor(private val itemClicked: itemClicked) :
    ListAdapter<BleDeviceConnection, BleDeviceViewHolder>(diff) {

    companion object {
        val diff = object : DiffUtil.ItemCallback<BleDeviceConnection>() {
            override fun areItemsTheSame(
                oldItem: BleDeviceConnection,
                newItem: BleDeviceConnection
            ): Boolean {
                return getValue(oldItem) == getValue(newItem)
            }

            override fun areContentsTheSame(
                oldItem: BleDeviceConnection,
                newItem: BleDeviceConnection
            ): Boolean {
                return oldItem == newItem
            }

        }

        private fun getValue(data: BleDeviceConnection): String? {
            return when (data) {
                is BleDeviceConnection.BleDevice -> data.scanResult?.device?.address
                is BleDeviceConnection.BleDeviceMessage -> data.msg
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BleDeviceViewHolder {
        return when (viewType) {
            R.layout.ble_device_item -> {
                val binding = BleDeviceItemBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                BleDeviceViewHolder.BleDevice(binding)
            }

            R.layout.progress_layout_device_item -> {
                val binding =
                    ProgressLayoutDeviceItemBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                BleDeviceViewHolder.ProgressViewHolder(binding)
            }

            else -> throw IllegalArgumentException("Unknown Layout")
        }
    }

    override fun onBindViewHolder(holder: BleDeviceViewHolder, position: Int) {
        val curr = getItem(position)
        curr?.let {
            when (holder) {
                is BleDeviceViewHolder.BleDevice -> {
                    holder.setData(it as BleDeviceConnection.BleDevice, itemClicked)
                }

                is BleDeviceViewHolder.ProgressViewHolder -> {
                    holder.setData(it as BleDeviceConnection.BleDeviceMessage)
                }
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is BleDeviceConnection.BleDevice -> R.layout.ble_device_item
            is BleDeviceConnection.BleDeviceMessage -> R.layout.progress_layout_device_item
            else -> 0
        }
    }
}