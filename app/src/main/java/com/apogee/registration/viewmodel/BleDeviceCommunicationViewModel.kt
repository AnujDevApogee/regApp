package com.apogee.registration.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.apogee.registration.instance.BluetoothCommunication
import com.apogee.registration.model.DeviceRegModel
import com.apogee.registration.repository.BleDeviceCommunicationRepository
import com.apogee.registration.repository.BleStatusCheckRepository
import com.apogee.registration.repository.BleSubscriptionStatusRepository
import com.apogee.registration.repository.DeviceRegRepository
import com.apogee.registration.repository.DeviceRegistrationConfirmRepository
import com.apogee.registration.repository.SavePersonDeviceRegistrationRepository
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


    private val _deviceSubDateConfirmResponse = MutableStateFlow<DataResponse<out Any?>?>(null)
    val deviceSubDateConfirmResponse: StateFlow<DataResponse<out Any?>?>
        get() = _deviceSubDateConfirmResponse


    private val _deviceSubDateStatusResponse = MutableStateFlow<DataResponse<out Any?>?>(null)
    val deviceSubDateStatusResponse: StateFlow<DataResponse<out Any?>?>
        get() = _deviceSubDateStatusResponse


    private val _bleStatusCheckResponse = MutableStateFlow<DataResponse<out Any?>?>(null)
    val bleStatusCheckResponse: StateFlow<DataResponse<out Any?>?>
        get() = _bleStatusCheckResponse

    private val _savePersonCheckResponse = MutableStateFlow<DataResponse<out Any?>?>(null)
    val savePersonCheckResponse: StateFlow<DataResponse<out Any?>?>
        get() = _savePersonCheckResponse


    private val deviceConnectionRepo = BleDeviceCommunicationRepository(
        BluetoothCommunication.getInstance(application), application
    )

    private val deviceRegRepo = DeviceRegRepository()

    private val deviceSubscriptionDateRepository = SubSubscriptionDateRepository()


    private val deviceSubscriptionDateConfirmRepository = DeviceRegistrationConfirmRepository()


    private val deviceSubscriptionDateStatusRepository = BleSubscriptionStatusRepository()

    private val bleStatusCheckRepository = BleStatusCheckRepository()

    private val savePersonDeviceRegistrationRepository = SavePersonDeviceRegistrationRepository()


    init {
        bleCommunication()
        getRegModel()
        getDeviceSubDateModel()
        getBleSubDateConfirm()
        getBleSubscriptionStatus()
        getBleSubStatus()
        getPersonDeviceResponse()
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

    //DeviceRegRecordsConformation API
    fun sendDeviceSubscriptionConfirmDate(req: String) {
        viewModelScope.launch {
            deviceSubscriptionDateConfirmRepository.sendDeviceRegConfirmResult(req)
        }
    }

    //BleSubscriptionStatus API
    fun sendDeviceSubscriptionStatus(request: String) {
        viewModelScope.launch {
            deviceSubscriptionDateStatusRepository.sendDeviceRegConfirmResult(request)
        }
    }

    //BleStatusCheck API
    fun sendBleStatusCheckStatus(request: String) {
        viewModelScope.launch {
            bleStatusCheckRepository.sendBleStatusResult(request)
        }
    }

    //SavePersonCheck API
    fun sendSavePersonResponse(request: String, deviceName: String) {
        viewModelScope.launch {
            savePersonDeviceRegistrationRepository.sendSavePersonResult(request, deviceName)
        }
    }


    private fun getPersonDeviceResponse() {
        viewModelScope.launch {
            savePersonDeviceRegistrationRepository.data.collect {
                _savePersonCheckResponse.value = it
            }
        }
    }

    private fun getBleSubStatus() {
        viewModelScope.launch {
            bleStatusCheckRepository.data.collect {
                _bleStatusCheckResponse.value = it
            }
        }
    }

    private fun getBleSubscriptionStatus() {
        viewModelScope.launch {
            deviceSubscriptionDateStatusRepository.data.collect {
                _deviceSubDateStatusResponse.value = it
            }
        }
    }


    private fun getBleSubDateConfirm() {
        viewModelScope.launch {
            deviceSubscriptionDateConfirmRepository.data.collect {
                _deviceSubDateConfirmResponse.value = it
            }
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