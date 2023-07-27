package com.apogee.registration.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.apogee.registration.instance.BluetoothCommunication
import com.apogee.registration.model.DeviceRegModel
import com.apogee.registration.repository.BleDeviceCommunicationRepository
import com.apogee.registration.repository.DeviceRegRepository
import com.apogee.registration.repository.SubSubscriptionDateRepository
import com.apogee.registration.utils.DataResponse
import com.apogee.registration.utils.newline_crlf
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class BleDeviceCommunicationViewModel(application: Application) : AndroidViewModel(application) {

    private val _bleCommunicationData = MutableStateFlow<DataResponse<out Any>?>(null)
    val bleCommunicationData: MutableStateFlow<DataResponse<out Any>?>
        get() = _bleCommunicationData

    private val _deviceRegResponse = MutableStateFlow<DataResponse<out Any?>?>(null)
    val deviceRegResponse: StateFlow<DataResponse<out Any?>?>
        get() = _deviceRegResponse

    private val _deviceSubRecordDateResponse = MutableStateFlow<DataResponse<out Any?>?>(null)
    val deviceSubRecordDateResponse: StateFlow<DataResponse<out Any?>?>
        get() = _deviceSubRecordDateResponse

    private val deviceConnectionRepo = BleDeviceCommunicationRepository(
        BluetoothCommunication.getInstance(application), application
    )

    private val deviceRegRepo = DeviceRegRepository()

    private val deviceSubscriptionDateRepository = SubSubscriptionDateRepository()

    init {
        bleCommunication()
        getRegModel()
        getDeviceSubDateModel()
    }

    fun setUpConnection() {
        deviceConnectionRepo.setUpConnection()
    }

    // deviceRegRecords API
    fun sendDeviceReg(deviceRegModel: DeviceRegModel) {
        viewModelScope.launch {
            deviceRegRepo.sendRequest(deviceRegModel)
        }
    }

    //deviceSubRecord API
    fun sendDeviceSubscriptionDate(req: Pair<String, String>) {
        viewModelScope.launch {
            deviceSubscriptionDateRepository.sendSubSubscriptionRequest(req)
        }
    }

    private fun getRegModel() {
        viewModelScope.launch {
            deviceRegRepo.data.collect {
                _deviceRegResponse.value = it
            }
        }
    }
    private fun getDeviceSubDateModel() {
        viewModelScope.launch {
            deviceSubscriptionDateRepository.data.collect {
                _deviceSubRecordDateResponse.value = it
            }
        }
    }


    fun connectWithDevice(macAddress: String) {
        deviceConnectionRepo.connect(macAddress)
    }

    fun disconnect() {
        deviceConnectionRepo.disconnect()
    }

    fun sendRequest(cmd: String,status: String) {
        val req = cmd + newline_crlf
        deviceConnectionRepo.sendRequest(req.toByteArray(), status = status)
    }


    private fun bleCommunication() {
        viewModelScope.launch {
            deviceConnectionRepo.data.collect {
                _bleCommunicationData.value = it
            }
        }
    }


   private fun disConnectService(){
       deviceConnectionRepo.disconnectService()
    }

    override fun onCleared() {
        super.onCleared()
        viewModelScope.cancel()
        disConnectService()
    }

}