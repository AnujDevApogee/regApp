package com.apogee.registration.viewmodel

import android.app.Application
import android.bluetooth.le.ScanResult
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
        adaptor, application
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
        startConnection()
        listenerBle()
    }

    private fun listenerBle() {
        viewModelScope.launch {
            repo.bleConnection.collect {
                _bleDeviceAvailable.postValue(it)
            }
        }
    }

    fun connectDevice(scanResult: ScanResult) {
        repo.setConnection(scanResult)
    }


    fun disconnectConnection(){
        repo.disconnectBle()
    }

    override fun onCleared() {
        super.onCleared()
        viewModelScope.cancel()
    }

}