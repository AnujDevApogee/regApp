package com.example.tcp_ble.utility;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.Nullable;

import com.example.tcp_ble.Database.DatabaseOperation;
import com.example.tcp_ble.model.BleModel;

public class BLEService extends IntentService {
Context context;
DatabaseOperation dbTask;

    public BLEService() {
        super("CableService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
       dbTask=new DatabaseOperation(BLEService.this);
        dbTask.open();

        BleModel model = new BleModel(BLEService.this);
        String serverip = dbTask.getServerIp();
        /*model.setServer_ip(serverip.split("_")[0]);
        model.setPort(serverip.split("_")[1]);*/
        dbTask.close();
        long result = model.requestBleDetail();

    }

    @Override
    public void onCreate() {
        super.onCreate(); // if you override onCreate(), make sure to call super().
        // If a Context object is needed, call getApplicationContext() here.
        context = getApplicationContext();

    }

}
