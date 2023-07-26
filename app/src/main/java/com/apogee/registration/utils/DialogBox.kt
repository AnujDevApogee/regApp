package com.apogee.registration.utils

import android.app.Activity
import com.google.android.material.dialog.MaterialAlertDialogBuilder


fun Activity.setUpDialogBox(
    title: String,
    message: String,
    okBtn: String,
    cancel: String?=null,
    icon: Int = -1,
    success: () -> Unit,
    cancelListener: () -> Unit
) {
    val dialog = MaterialAlertDialogBuilder(
        this, com.google.android.material.R.style.MaterialAlertDialog_Material3
    )

    dialog.setTitle(title)
    dialog.setMessage(message)
    if (icon != -1) {
        dialog.setIcon(icon)
    }
    dialog.setPositiveButton(
        okBtn
    ) { dialogInterface, _ ->
        success.invoke()
        dialogInterface.dismiss()
    }
    if (!cancel.isNullOrEmpty()) {
        dialog.setNegativeButton(
            cancel
        ) { dialogInterface, _ ->

            dialogInterface.dismiss()
        }
    }
    dialog.setOnDismissListener {
        cancelListener.invoke()
    }
    dialog.show()
}