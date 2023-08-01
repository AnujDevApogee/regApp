package com.apogee.registration.ui.registion

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.apogee.registration.R
import com.apogee.registration.databinding.DeviceRegistrationLayoutBinding
import com.apogee.registration.datastore.RegistrationAppSharedPref
import com.apogee.registration.dialog.ProgressDialog
import com.apogee.registration.model.BleErrorStatus
import com.apogee.registration.model.BleLoadingStatus
import com.apogee.registration.model.BleSuccessStatus
import com.apogee.registration.model.DeviceRegModel
import com.apogee.registration.model.pb.BlePbError
import com.apogee.registration.model.pb.BlePbLoading
import com.apogee.registration.model.pb.BlePbSuccess
import com.apogee.registration.user_case.BleProtocolFilter
import com.apogee.registration.user_case.DateConverter
import com.apogee.registration.utils.BleCmd
import com.apogee.registration.utils.BleHelper
import com.apogee.registration.utils.DataResponse
import com.apogee.registration.utils.OnItemClickListener
import com.apogee.registration.utils.calenderPicker
import com.apogee.registration.utils.checkVaildString
import com.apogee.registration.utils.createLog
import com.apogee.registration.utils.displayActionBar
import com.apogee.registration.utils.getDate
import com.apogee.registration.utils.getTimeStamp
import com.apogee.registration.utils.setUpDialogBox
import com.apogee.registration.utils.showToastMsg
import com.apogee.registration.viewmodel.BleDeviceCommunicationViewModel
import com.google.android.material.transition.MaterialFadeThrough
import kotlinx.coroutines.launch

@Suppress("UNCHECKED_CAST")
class DeviceRegistrationFragment : Fragment(R.layout.device_registration_layout) {

    private lateinit var binding: DeviceRegistrationLayoutBinding

    private val args: DeviceRegistrationFragmentArgs by navArgs()

    private val viewModel: BleDeviceCommunicationViewModel by viewModels()

    private var deviceRegModel: DeviceRegModel? = null

    private val sharedPref by lazy {
        RegistrationAppSharedPref.getInstance(requireActivity())
    }

    private var progressDialog: ProgressDialog? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val fadeThrough = MaterialFadeThrough().apply {
            duration = 1000
        }
        enterTransition = fadeThrough
        reenterTransition = fadeThrough

    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = DeviceRegistrationLayoutBinding.bind(view)
        displayActionBar(
            getString(R.string.ble_reg), binding.actionLayout, bckIc = R.drawable.ic_back_arrow
        )
        setupUI()

        getBleUpdates()
        getDeviceRegResponse()
        getSubscriptionDateResponse()
        getSubscriptionDateConfirmResponse()
        getSubscriptionDataStatusResponse()
        getBleStatusCheck()
        getSavePersonResponse()




        binding.submitBtn.setOnClickListener {
            val bleModel = binding.bleNme.text.toString()
            val manufacture = binding.manufacturerLs.text.toString()
            val deviceType = binding.deviceLs.text.toString()
            val modelName = binding.modelNameLs.text.toString()
            val modelNo = binding.modelNo.text.toString()
            val subscriptionDate = binding.subscriptionDate.text.toString()

            if (checkVaildString(bleModel)) {
                showToastMsg("Cannot find the BleModel")
                return@setOnClickListener
            }

            if (checkVaildString(manufacture)) {
                showToastMsg("Cannot find the Manufacture")
                return@setOnClickListener
            }

            if (checkVaildString(bleModel)) {
                showToastMsg("Cannot find the BleModel")
                return@setOnClickListener
            }

            if (checkVaildString(deviceType)) {
                showToastMsg("Cannot find the DeviceType")
                return@setOnClickListener
            }

            if (checkVaildString(modelName)) {
                showToastMsg("Cannot find the ModelNo")
                return@setOnClickListener
            }

            if (checkVaildString(modelNo)) {
                showToastMsg("Cannot find the ModelNo")
                return@setOnClickListener
            }

            if (checkVaildString(subscriptionDate) || DateConverter.getConvertDate(subscriptionDate) == -1) {
                showToastMsg("Cannot find the Subscription Date")
                return@setOnClickListener
            }

            deviceRegModel = DeviceRegModel(
                imei = null,
                manufacturer = manufacture,
                deviceType = deviceType,
                modelName = modelName,
                modelNo = modelNo,
                subDate = DateConverter.getConvertDate(subscriptionDate).toString()
            )
            viewModel.setUpConnection()
            //viewModel.sendDeviceSubscriptionConfirmDate("\$\$\$\$,04,D_342,06,D_342,P_56726,120.138.10.146,8060,45.114.142.35,8060,12,60,474208,0000,####")
            //viewModel.sendDeviceSubscriptionStatus("\$\$\$\$,01,D_342,01,1,0000,####")
            //viewModel.sendBleStatusCheckStatus("\$\$\$\$,08,D_184,02,1,NAVIK50-1.0__2338342,0000,####")
            //viewModel.sendSavePersonResponse("13324,4545", "NAVIK50-1.0__2338342")
        }


    }

    private fun getSavePersonResponse() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.savePersonCheckResponse.collect {
                    if (it != null) {
                        when (it) {
                            is DataResponse.Error -> {
                                showPb(
                                    BlePbError.ValidateUserDetailError(
                                        it.data as String?,
                                        it.exception
                                    )
                                )
                                createLog(
                                    "BLE_INFO",
                                    "SAVE_PERSON_RESPONSE Response Error is ${it.data} and ${it.exception?.localizedMessage}"
                                )
                            }

                            is DataResponse.Loading -> {
                                showPb(BlePbLoading.ValidateUserDetailLoading("${it.data}"))
                                createLog(
                                    "BLE_INFO",
                                    "SAVE_PERSON_RESPONSE Response Loading is ${it.data}"
                                )
                            }

                            is DataResponse.Success -> {
                                showPb(BlePbSuccess.ValidateUserDetailSuccess("${it.data}"))
                                createLog(
                                    "BLE_INFO",
                                    "SAVE_PERSON_RESPONSE Response SUCCESS is ${it.data}"
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    private fun getBleStatusCheck() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.bleStatusCheckResponse.collect {
                    if (it != null) {
                        when (it) {
                            is DataResponse.Error -> {
                                showPb(
                                    BlePbError.BLERenamingAndApIError(
                                        it.data as String?,
                                        it.exception
                                    )
                                )
                                createLog(
                                    "BLE_INFO",
                                    "BLE_DEVICE_STATUS_CHECK Response Error is ${it.data} and ${it.exception?.localizedMessage}"
                                )
                            }

                            is DataResponse.Loading -> {
                                showPb(BlePbLoading.BLERenamingAndApILoading("${it.data}"))
                                createLog(
                                    "BLE_INFO",
                                    "BLE_DEVICE_STATUS_CHECK Response Loading is ${it.data}"
                                )
                            }

                            is DataResponse.Success -> {
                                showPb(BlePbSuccess.BLERenamingAndApISuccess("Ble Rename Successfully"))
                                createLog(
                                    "BLE_INFO",
                                    "BLE_DEVICE_STATUS_CHECK Response Success is ${it.data}"
                                )
                                val res = it.data as Pair<String, String>
                                val str =
                                    "${sharedPref.getLoginResponse()!!.data!!.first().keyPersonId},${res.second}"
                                viewModel.sendSavePersonResponse(str, res.first)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun getSubscriptionDataStatusResponse() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.deviceSubDateStatusResponse.collect {
                    if (it != null) {
                        when (it) {
                            is DataResponse.Error -> {
                                showPb(
                                    BlePbError.BLESubBLEAndDeviceSubBleApiError(
                                        it.data as String?,
                                        it.exception
                                    )
                                )
                                createLog(
                                    "BLE_INFO",
                                    "Subscription_Date_STATUS Response Error is ${it.data} and ${it.exception?.localizedMessage}"
                                )
                            }

                            is DataResponse.Loading -> {
                                showPb(BlePbLoading.BLESubBLEAndDeviceSubBleApiLoading("${it.data}"))
                                createLog(
                                    "BLE_INFO",
                                    "Subscription_Date_STATUS Response Loading is ${it.data}"
                                )
                            }

                            is DataResponse.Success -> {
                                showPb(BlePbSuccess.BLESubBLEAndDeviceSubBleApiSuccess("Subscription Successful"))
                                createLog(
                                    "BLE_INFO",
                                    "Subscription_Date_STATUS Response Success is ${it.data}"
                                )
                                viewModel.sendRequest(
                                    it.data as String,
                                    BleHelper.BLERENAMESTATUS.name
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    private fun getSubscriptionDateConfirmResponse() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.deviceSubDateConfirmResponse.collect {
                    if (it != null) {
                        when (it) {
                            is DataResponse.Error -> {
                                showPb(
                                    BlePbError.DeviceRegBleAndDeviceConfirmError(
                                        it.data as String?, it.exception
                                    )
                                )
                                createLog(
                                    "BLE_INFO",
                                    "Subscription_Date_CONFIRM Response Error is ${it.data} and ${it.exception?.localizedMessage}"
                                )
                            }

                            is DataResponse.Loading -> {
                                showPb(BlePbLoading.DeviceRegBleAndDeviceConfirmLoading("${it.data}"))
                                createLog(
                                    "BLE_INFO",
                                    "Subscription_Date_CONFIRM Response Loading is ${it.data}"
                                )
                            }

                            is DataResponse.Success -> {
                                createLog(
                                    "BLE_INFO",
                                    "Subscription_Date_CONFIRM Response Success is ${it.data}"
                                )
                                showPb(BlePbSuccess.DeviceRegBleAndDeviceConfirmSuccess("Received Reg Token"))
                                viewModel.sendRequest(
                                    it.data as String,
                                    BleHelper.DEVICEREGCONFIRM.name
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    private fun getSubscriptionDateResponse() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.deviceSubRecordDateResponse.collect {
                    if (it != null) {
                        when (it) {
                            is DataResponse.Error -> {
                                showPb(
                                    BlePbError.DeviceRegAndSubError(
                                        it.data as String?, it.exception
                                    )
                                )
                                createLog(
                                    "BLE_INFO",
                                    "DEVICE SUB_DATE API Error is ${it.data} and ${it.exception?.localizedMessage}"
                                )
                            }

                            is DataResponse.Loading -> {
                                showPb(BlePbLoading.DeviceRegAndSubLoading("${it.data}"))
                                createLog(
                                    "BLE_INFO", "DEVICE SUB_DATE API LOADING is ${it.data}"
                                )
                            }

                            is DataResponse.Success -> {
                                showPb(BlePbSuccess.DeviceRegAndSubSuccess("Data Saved"))
                                createLog(
                                    "BLE_INFO", "DEVICE SUB_DATE API Success is ${it.data}"
                                )

                                viewModel.sendRequest(
                                    it.data as String,
                                    BleHelper.DEVICEREGRECORD.name
                                )
                            }
                        }
                    }
                }
            }
        }
    }


    private fun getDeviceRegResponse() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.deviceRegResponse.collect {
                    if (it != null) {
                        when (it) {
                            is DataResponse.Error -> {
                                showPb(
                                    BlePbError.DeviceRegAndSubError(
                                        it.data as String?, it.exception
                                    )
                                )
                                createLog(
                                    "BLE_INFO",
                                    "DEVICE REG API Error is ${it.data} and ${it.exception?.localizedMessage}"
                                )
                            }

                            is DataResponse.Loading -> {
                                showPb(BlePbLoading.DeviceRegAndSubLoading("${it.data}"))
                                createLog("BLE_INFO", "DEVICE REG API LOADING ${it.data}")
                            }

                            is DataResponse.Success -> {
                                createLog("BLE_INFO", "DEVICE REG API SUCCESS ${it.data}")
                                if (it.data is Pair<*, *>) {
                                    val obj = it.data.first as DeviceRegModel
                                    val res = it.data.second as String
                                    viewModel.sendDeviceSubscriptionDate(
                                        Pair(
                                            obj.subscriptionDateRequestBody,
                                            res
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun getBleUpdates() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.bleCommunicationData.collect {
                    if (it != null) {
                        when (it) {
                            is DataResponse.Error -> {
                                if (it.data is String?) {
                                    createLog(
                                        "BLE_INFO",
                                        " Error ${it.data} and ${it.exception?.localizedMessage}"
                                    )
                                }
                                if (it.data is BleErrorStatus) {
                                    bleError(it.data)
                                }
                            }

                            is DataResponse.Loading -> {
                                if (it.data is String?) {
                                    createLog("BLE_INFO", " Loading ${it.data}")
                                }
                                if (it.data is BleLoadingStatus) {
                                    bleLoading(it.data)
                                }
                            }

                            is DataResponse.Success -> {
                                if (it.data is String?) {
                                    createLog("BLE_INFO", " Success ${it.data}")
                                }
                                if (it.data is BleSuccessStatus) {
                                    bleSuccess(it.data)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun bleSuccess(data: BleSuccessStatus) {
        when (data) {
            is BleSuccessStatus.BleConnectSuccess -> {
                createLog(
                    "BLE_INFO",
                    "Ble Connection Success -> ${data.data} "
                )
                viewModel.sendRequest(BleCmd.imeiNumber, BleHelper.IEMINUMBER.name)
            }

            is BleSuccessStatus.BleImeiNumberSuccess -> {
                showPb(BlePbSuccess.ConnectionAndImeiSuccess("Imei Number ${data.data}"))
                createLog(
                    "BLE_INFO", "IMEI  Ble Connection Success-> ${data.data} "
                )
                deviceRegModel?.let { model ->
                    model.imei = data.data as String
                    viewModel.sendDeviceReg(model)
                } ?: showToastMsg("some thing went wrong try again!!")
            }

            is BleSuccessStatus.BleSetUpConnectionSuccess -> {
                createLog(
                    "BLE_INFO",
                    "Ble Connection Success -> ${data.data} "
                )
                viewModel.connectWithDevice(args.macaddress)
            }

            is BleSuccessStatus.BleDeviceRegRecordSuccess -> {
                createLog("BLE_INFO", "Ble DEVICE_REG_CONNECTION Success ${data.data}")

                viewModel.sendDeviceSubscriptionConfirmDate(data.data as String)
            }

            is BleSuccessStatus.BleDeviceConfirmationSuccess -> {
                createLog("BLE_INFO", "Ble DEVICE_REG_CONFIRM Success ${data.data}")
                viewModel.sendDeviceSubscriptionStatus(data.data as String)
            }

            is BleSuccessStatus.BleRenamingStatusSuccess -> {
                createLog("BLE_INFO", "Ble BLE_RENAME Success ${data.data}")
                viewModel.sendBleStatusCheckStatus(data.data as String)
            }
        }
    }

    private fun bleLoading(data: BleLoadingStatus) {
        when (data) {
            is BleLoadingStatus.BleConnectDeviceLoading -> {
                showPb(BlePbLoading.ConnectionAndImeiLoading(data.msg))
                createLog(
                    "BLE_INFO", "Ble Connection Loading -> ${data.msg} "
                )
            }

            is BleLoadingStatus.BleSetUpConnectionLoading -> {
                showPb(BlePbLoading.ConnectionAndImeiLoading(data.msg))
                createLog(
                    "BLE_INFO", "Ble Connection Loading -> ${data.msg} "
                )
            }

            is BleLoadingStatus.ImeiNumberLoading -> {
                showPb(BlePbLoading.ConnectionAndImeiLoading(data.msg))
                createLog(
                    "BLE_INFO", "Ble Connection Loading -> ${data.msg} "
                )
            }

            is BleLoadingStatus.BleDeviceRegRecordLoading -> {
                showPb(BlePbLoading.DeviceRegBleAndDeviceConfirmLoading(data.msg))
                createLog("BLE_INFO", "Ble Record Loading ${data.msg}")
            }

            is BleLoadingStatus.BleDeviceConfirmationLoading -> {
                showPb(BlePbLoading.BLESubBLEAndDeviceSubBleApiLoading(data.msg))
                createLog("BLE_INFO", "Ble DEVICE_REG_CONNECTION Loading ${data.msg}")
            }

            is BleLoadingStatus.BleRenamingStatusLoading -> {
                showPb(BlePbLoading.BLERenamingAndApILoading(data.msg))
                createLog("BLE_INFO", "Ble BLE_RENAME Loading ${data.msg}")
            }
        }
    }

    private fun bleError(data: BleErrorStatus) {
        when (data) {
            is BleErrorStatus.BleConnectError -> {
                showPb(BlePbError.ConnectionAndImeiError(data.error, data.e))
                createLog(
                    "BLE_INFO",
                    "Ble Connection Error -> ${data.error} and ${data.e?.localizedMessage}"
                )
            }

            is BleErrorStatus.BleImeiError -> {
                showPb(BlePbError.ConnectionAndImeiError(data.error, data.e))
                createLog(
                    "BLE_INFO", "Ble BleImei Error -> ${data.error} and ${data.e?.localizedMessage}"
                )
            }

            is BleErrorStatus.BleSetUpConnectionError -> {
                showPb(BlePbError.ConnectionAndImeiError(data.error, data.e))
                createLog(
                    "BLE_INFO",
                    "Ble setupConnectionError -> ${data.error} and ${data.e?.localizedMessage}"
                )
            }

            is BleErrorStatus.BleDeviceRegRecordError -> {
                showPb(BlePbError.DeviceRegBleAndDeviceConfirmError(data.error, data.e))
                createLog(
                    "BLE_INFO",
                    "Ble Device Reg Error -> ${data.error} and ${data.e?.localizedMessage}"
                )
            }

            is BleErrorStatus.BleDeviceConfirmationError -> {
                showPb(BlePbError.BLESubBLEAndDeviceSubBleApiError(data.error, data.e))
                createLog(
                    "BLE_INFO",
                    "Ble DEVICE REG Error -> ${data.error} and ${data.e?.localizedMessage}"
                )
            }

            is BleErrorStatus.BleRenamingStatusError -> {
                showPb(BlePbError.BLERenamingAndApIError(data.error, data.e))
                createLog(
                    "BLE_INFO",
                    "Ble BLE_RENAME Error -> ${data.error} and ${data.e?.localizedMessage}"
                )
            }
        }
    }


    private fun showPb(ble: Any) {
        if (activity == null && !isAdded) {
            showToastMsg("Unknown error Try Again")
            findNavController().popBackStack()
            return
        }
        if (progressDialog == null && activity != null && isAdded) {
            progressDialog = ProgressDialog(requireActivity(), object : OnItemClickListener {
                override fun <T> onClickListener(response: T) {
                    if (response == null) {
                        progressDialog?.dismiss()
                        findNavController().popBackStack()
                    }
                    if (response is DataResponse.Error<*>) {
                        dialog("Failed", "${response.data}")
                    }
                    if (response is DataResponse.Success<*>) {
                        dialog("Success", "${response.data}")
                    }
                }

            })
        }
        if (ble is BlePbSuccess) {
            progressDialog?.showSuccess(ble)
        }
        if (ble is BlePbError) {
            progressDialog?.showPbError(ble)
        }

        if (ble is BlePbLoading) {
            progressDialog?.showLoading(ble)
        }
    }

    private fun setupUI() {
        binding.bleNme.setText(args.devicename)


        val modelAndNo=BleProtocolFilter.getModelNameAndNumber(args.devicename,requireContext())

        binding.subscriptionDate.setText(getDate(getTimeStamp().timeInMillis))
        binding.modelNo.apply {
            setText(resources.getStringArray(R.array.model_ls)[modelAndNo.second].toString())
            setAdapter(setAdaptor(R.array.model_ls))
        }
        binding.deviceLs.apply {
            setText(resources.getStringArray(R.array.device_ls)[0].toString())
            setAdapter(setAdaptor(R.array.device_ls))
        }
        binding.manufacturerLs.apply {
            setText(resources.getStringArray(R.array.manufact_ls)[0].toString())
            setAdapter(setAdaptor(R.array.manufact_ls))
        }
        binding.modelNameLs.apply {
            setText(resources.getStringArray(R.array.model_name_ls)[modelAndNo.first].toString())
            setAdapter(setAdaptor(R.array.model_name_ls))
        }
        binding.subscriptionDate.setOnClickListener {
            calenderPicker(parentFragmentManager, {}, {
                binding.subscriptionDate.setText(it)
                createLog("TAG_INFO", "${DateConverter.getConvertDate(it)}")
            })
        }
    }

    private fun setAdaptor(strAdp: Int): ArrayAdapter<String> {
        return ArrayAdapter<String>(
            this.requireContext(),
            android.R.layout.select_dialog_item,
            resources.getStringArray(strAdp)
        )
    }

    private fun dialog(title: String, msg: String) {
        activity?.setUpDialogBox(title, msg, "OK", success = {

        }, cancelListener = {})
    }

    override fun onPause() {
        super.onPause()
        showToastMsg("Device Disconnected!!")
        viewModel.disconnect()
    }
}