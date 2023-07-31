package com.apogee.registration.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.apogee.registration.instance.BluetoothCommunication
import com.apogee.registration.repository.BleDeviceConnectionRepository
import com.apogee.registration.utils.DataResponse
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class BleConnectionViewModel(application: Application) : AndroidViewModel(application) {
    private val adaptor = BluetoothCommunication.getInstance(application).getBluetoothAdaptor()

    private val repo = BleDeviceConnectionRepository(
        adaptor
    )


    private val _bleDeviceAvailable = MutableLiveData<DataResponse<out Any?>>()
    val bleDeviceAvailable: LiveData<DataResponse<out Any?>>
        get() = _bleDeviceAvailable

    fun startConnection() {
        viewModelScope.launch {
            repo.startConnection()
        }
    }


    init {
        listenerBle()
    }

    private fun listenerBle() {
        viewModelScope.launch {
            repo.bleConnection.collect {
                _bleDeviceAvailable.postValue(it)
            }
        }
    }


    override fun onCleared() {
        super.onCleared()
        viewModelScope.cancel()
    }

}