package com.apogee.registration.dialog

import android.app.Activity
import android.app.Dialog
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.Point
import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import android.view.Window
import android.view.WindowManager
import com.apogee.registration.R
import com.apogee.registration.databinding.ProgressBrLayoutBinding
import com.apogee.registration.model.pb.BlePbError
import com.apogee.registration.model.pb.BlePbLoading
import com.apogee.registration.model.pb.BlePbSuccess
import com.apogee.registration.utils.DataResponse
import com.apogee.registration.utils.OnItemClickListener
import com.apogee.registration.utils.getEmojiByUnicode
import com.apogee.registration.utils.hide
import com.apogee.registration.utils.invisible
import com.apogee.registration.utils.show


class ProgressDialog(private val context: Activity, private val listener: OnItemClickListener) {
    private val dialog = Dialog(context)

    init {
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        showLoadingDialog()
    }


    private lateinit var binding: ProgressBrLayoutBinding


    private fun showLoadingDialog() {
        binding = ProgressBrLayoutBinding.inflate(context.layoutInflater)
        dialog.setContentView(binding.root)
        dialog.setCancelable(false)
        val window: Window? = dialog.window
        val size = Point()
        val display = window?.windowManager?.defaultDisplay
        display?.getSize(size)
        window?.setLayout((size.x * 0.90).toInt(), WindowManager.LayoutParams.WRAP_CONTENT)
        window?.setGravity(Gravity.CENTER)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))


        binding.apply {
   /*         configImeiNumberPb.pb.isIndeterminate = true
            configImeiNumberPb.pbTxt.apply {

                text = "Please Wait Connecting.."
                setTextColor(Color.BLACK)
            }*/
            /* regSubPb.apply {
                 pb.invisible()
                 img.show()
                 img.setImageResource(R.drawable.ic_success)
                 pbTxt.apply {
                     text="Device Connect"
                     setTextColor(Color.BLACK)
                 }
             }

             regSubDeviceConfig.apply {
                 pb.invisible()
                 img.show()
                 img.setImageResource(R.drawable.ic_error)
                 img.imageTintList= ColorStateList.valueOf(Color.RED)
                 pbTxt.apply {
                     text="Error while connoting"
                     setTextColor(Color.BLACK)
                 }
                 moreInfo.show()
             }*/
        }

        binding.retry.setOnClickListener {
            listener.onClickListener(null)
        }

        binding.retry.hide()
        dialog.show()
    }

    fun dismiss() {
        dialog.dismiss()
    }


    fun showLoading(pbLoading: BlePbLoading) {
        when (pbLoading) {
            is BlePbLoading.BLERenamingAndApILoading -> {
                binding.bleRenamePb.pb.isIndeterminate = true
                binding.bleRenamePb.pbTxt.apply {
                    text = pbLoading.msg
                    setTextColor(Color.BLACK)
                }
            }

            is BlePbLoading.BLESubBLEAndDeviceSubBleApiLoading -> {
                binding.bleSubPb.pb.isIndeterminate = true
                binding.bleSubPb.pbTxt.apply {
                    text = pbLoading.msg
                    setTextColor(Color.BLACK)
                }
            }

            is BlePbLoading.ConnectionAndImeiLoading -> {
                binding.configImeiNumberPb.pb.isIndeterminate = true
                binding.configImeiNumberPb.pbTxt.apply {
                    text = pbLoading.msg
                    setTextColor(Color.BLACK)
                }
            }

            is BlePbLoading.DeviceRegAndSubLoading -> {
                binding.regSubPb.pb.isIndeterminate = true
                binding.regSubPb.pbTxt.apply {
                    text = pbLoading.msg
                    setTextColor(Color.BLACK)
                }
            }

            is BlePbLoading.DeviceRegBleAndDeviceConfirmLoading -> {
                binding.regSubDeviceConfig.pb.isIndeterminate = true
                binding.regSubDeviceConfig.pbTxt.apply {
                    text = pbLoading.msg
                    setTextColor(Color.BLACK)
                }
            }

            is BlePbLoading.ValidateUserDetailLoading -> {
                binding.validNameDetail.pb.isIndeterminate = true
                binding.validNameDetail.pbTxt.apply {
                    text = pbLoading.msg
                    setTextColor(Color.BLACK)
                }
            }
        }

    }

    fun showPbError(ble: BlePbError) {
        when (ble) {
            is BlePbError.BLESubBLEAndDeviceSubBleApiError -> {
                val err = (ble.error ?: "") + (ble.e?.localizedMessage ?: "")
                binding.bleSubPb.apply {
                    pb.invisible()
                    img.show()
                    img.setImageResource(R.drawable.ic_error)
                    img.imageTintList = ColorStateList.valueOf(Color.RED)
                    pbTxt.apply {
                        text = err
                        setTextColor(Color.BLACK)
                    }
                    moreInfo.show()
                    moreInfo.setOnClickListener {
                        listener.onClickListener(DataResponse.Error(err, null))
                    }
                }
                binding.retry.show()
            }

            is BlePbError.ValidateUserDetailError -> {
                val err = (ble.error ?: "") + (ble.e?.localizedMessage ?: "")
                binding.validNameDetail.apply {
                    pb.invisible()
                    img.show()
                    img.setImageResource(R.drawable.ic_error)
                    img.imageTintList = ColorStateList.valueOf(Color.RED)
                    pbTxt.apply {
                        text = err
                        setTextColor(Color.BLACK)
                    }
                    moreInfo.show()
                    moreInfo.setOnClickListener {
                        listener.onClickListener(DataResponse.Error(err, null))
                    }
                }
                binding.retry.show()
            }

            is BlePbError.BLERenamingAndApIError -> {
                val err = (ble.error ?: "") + (ble.e?.localizedMessage ?: "")
                binding.bleRenamePb.apply {
                    pb.invisible()
                    img.show()
                    img.setImageResource(R.drawable.ic_error)
                    img.imageTintList = ColorStateList.valueOf(Color.RED)
                    pbTxt.apply {
                        text = err
                        setTextColor(Color.BLACK)
                    }
                    moreInfo.show()
                    moreInfo.setOnClickListener {
                        listener.onClickListener(DataResponse.Error(err, null))
                    }
                }
                binding.retry.show()
            }

            is BlePbError.ConnectionAndImeiError -> {
                val err = (ble.error ?: "") + (ble.e?.localizedMessage ?: "")
                binding.configImeiNumberPb.apply {
                    pb.invisible()
                    img.show()
                    img.setImageResource(R.drawable.ic_error)
                    img.imageTintList = ColorStateList.valueOf(Color.RED)
                    pbTxt.apply {
                        text = err
                        setTextColor(Color.BLACK)
                    }
                    moreInfo.show()
                    moreInfo.setOnClickListener {
                        listener.onClickListener(DataResponse.Error(err, null))
                    }
                }
                binding.retry.show()
            }

            is BlePbError.DeviceRegAndSubError -> {
                //regSubPb
                val err = (ble.error ?: "") + (ble.e?.localizedMessage ?: "")
                binding.regSubPb.apply {
                    pb.invisible()
                    img.show()
                    img.setImageResource(R.drawable.ic_error)
                    img.imageTintList = ColorStateList.valueOf(Color.RED)
                    pbTxt.apply {
                        text = err
                        setTextColor(Color.BLACK)
                    }
                    moreInfo.show()
                    moreInfo.setOnClickListener {
                        listener.onClickListener(DataResponse.Error(err, null))
                    }
                }
                binding.retry.show()
            }

            is BlePbError.DeviceRegBleAndDeviceConfirmError -> {
                val err = (ble.error ?: "") + (ble.e?.localizedMessage ?: "")
                binding.regSubDeviceConfig.apply {
                    pb.invisible()
                    img.show()
                    img.setImageResource(R.drawable.ic_error)
                    img.imageTintList = ColorStateList.valueOf(Color.RED)
                    pbTxt.apply {
                        text = err
                        setTextColor(Color.BLACK)
                    }
                    moreInfo.show()
                    moreInfo.setOnClickListener {
                        listener.onClickListener(DataResponse.Error(err, null))
                    }
                }
                binding.retry.show()
            }
        }
    }

    fun showSuccess(blePbSuccess: BlePbSuccess) {
        when (blePbSuccess) {
            is BlePbSuccess.BLERenamingAndApISuccess -> {
                binding.bleRenamePb.apply {
                    pb.invisible()
                    img.show()
                    img.setImageResource(R.drawable.ic_success)
                    pbTxt.apply {
                        text = "${blePbSuccess.data}"
                        setTextColor(Color.BLACK)
                    }
                }
                binding.retry.hide()
            }

            is BlePbSuccess.BLESubBLEAndDeviceSubBleApiSuccess -> {
                binding.bleSubPb.apply {
                    pb.invisible()
                    img.show()
                    img.setImageResource(R.drawable.ic_success)
                    pbTxt.apply {
                        text = "${blePbSuccess.data}"
                        setTextColor(Color.BLACK)
                    }
                }
                binding.retry.hide()
            }

            is BlePbSuccess.ConnectionAndImeiSuccess -> {
                binding.configImeiNumberPb.apply {
                    pb.invisible()
                    img.show()
                    img.setImageResource(R.drawable.ic_success)
                    pbTxt.apply {
                        text = "${blePbSuccess.data}"
                        maxLines = 2
                        setTextColor(Color.BLACK)
                    }
                }
                binding.retry.hide()
            }

            is BlePbSuccess.DeviceRegAndSubSuccess -> {
                binding.regSubPb.apply {
                    pb.invisible()
                    img.show()
                    img.setImageResource(R.drawable.ic_success)
                    pbTxt.apply {
                        text = "${blePbSuccess.data}"
                        setTextColor(Color.BLACK)
                    }
                }
                binding.retry.hide()
            }

            is BlePbSuccess.DeviceRegBleAndDeviceConfirmSuccess -> {
                binding.regSubDeviceConfig.apply {
                    pb.invisible()
                    img.show()
                    img.setImageResource(R.drawable.ic_success)
                    pbTxt.apply {
                        text = "${blePbSuccess.data}"
                        setTextColor(Color.BLACK)
                    }
                }
                binding.retry.hide()
            }

            is BlePbSuccess.ValidateUserDetailSuccess -> {
                binding.validNameDetail.apply {
                    pb.invisible()
                    img.show()
                    img.setImageResource(R.drawable.ic_success)
                    pbTxt.apply {
                        text = "${blePbSuccess.data}"
                        maxLines=2
                        setTextColor(Color.BLACK)
                    }
                }
                "Continue ${getEmojiByUnicode(0x2705)}".let { binding.retry.text = it }
                binding.retry.show()
                listener.onClickListener(DataResponse.Success("${blePbSuccess.data}"))
            }
        }

    }















}