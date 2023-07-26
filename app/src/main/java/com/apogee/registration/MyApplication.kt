package com.apogee.registration

import android.app.Application
import com.apogee.registration.datastore.RegistrationAppSharedPref
import com.apogee.registration.instance.ApiInstance
import com.apogee.registration.instance.BluetoothCommunication

class MyApplication :Application() {

    override fun onCreate() {
        super.onCreate()
        ApiInstance.getInstance()
        RegistrationAppSharedPref.getInstance(applicationContext)
        BluetoothCommunication.getInstance(applicationContext)
    }
}