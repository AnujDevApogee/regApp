package com.apogee.registration.dialog

import android.app.Activity
import android.app.Dialog
import android.graphics.Color
import android.graphics.Point
import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import android.view.Window
import android.view.WindowManager
import com.apogee.registration.databinding.ProgressBrLayoutBinding
import com.apogee.registration.utils.hide


class ProgressDialog(private val context: Activity) {
    private val dialog = Dialog(context)

    init {
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
    }


    private lateinit var binding: ProgressBrLayoutBinding


    fun showLoadingDialog(){
        binding= ProgressBrLayoutBinding.inflate(context.layoutInflater)
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
            configImeiNumberPb.pb.isIndeterminate=true
            configImeiNumberPb.pbTxt.apply {

                text="Please Wait Connecting.."
                setTextColor(Color.BLACK)
            }
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
binding.retry.hide()
        dialog.show()
    }


}