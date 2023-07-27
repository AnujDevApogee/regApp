package com.apogee.registration.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.apogee.registration.instance.BluetoothCommunication
import com.apogee.registration.repository.BleDeviceCommunicationRepository
import com.apogee.registration.utils.DataResponse
import com.apogee.registration.utils.newline_crlf
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class BleDeviceCommunicationViewModel(application: Application) : AndroidViewModel(application) {

    private val _bleCommunicationData = MutableStateFlow<DataResponse<out Any>?>(null)
    val bleCommunicationData: MutableStateFlow<DataResponse<out Any>?>
        get() = _bleCommunicationData


    private val repo = BleDeviceCommunicationRepository(
        BluetoothCommunication.getInstance(application),
        application
    )


    init {
        bleCommunication()
    }

    fun setUpConnection() {
        repo.setUpConnection()
    }


    fun connectWithDevice(macAddress: String) {
        repo.connect(macAddress)
    }

    fun disconnect() {
        repo.disconnect()
    }

    fun sendRequest(cmd: String,status: String) {
        val req = cmd + newline_crlf
        repo.sendRequest(req.toByteArray(), status =status )
    }


    private fun bleCommunication() {
        viewModelScope.launch {
            repo.data.collect {
                _bleCommunicationData.value = it
            }
        }
    }


    private fun disConnectService(){
        repo.disconnectService()
    }

    override fun onCleared() {
        super.onCleared()
        viewModelScope.cancel()
        disConnectService()
    }

}