package com.example.tcp_ble.model;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.example.tcp_ble.IpActivity;
import com.example.tcp_ble.R;
import com.example.tcp_ble.databinding.HomeBinding;
import com.example.tcp_ble.utility.DeviceScanActivity;
import com.example.tcp_ble.utility.SavedDeviceList;

public class
Home extends AppCompatActivity {
    HomeBinding binding;
    String mDeviceName, mDeviceAddress,d_id,dgpsid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= DataBindingUtil.setContentView(this, R.layout.home);
        setOnClickListener();

        final Intent intent = getIntent();
        mDeviceName = intent.getStringExtra("DEVICE_NAME");
        mDeviceAddress = intent.getStringExtra("EXTRAS_DEVICE_ADDRESS");
         d_id=intent.getStringExtra("device_id");
         dgpsid=    intent.getStringExtra("dgps_device_id");
    }

    private void setOnClickListener() {

        binding.cvDeviceRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentRegister=new Intent(getApplication(),DataSelection.class);
                intentRegister.putExtra("DEVICE_NAME", mDeviceName);
                intentRegister.putExtra("EXTRAS_DEVICE_ADDRESS", mDeviceAddress);
                intentRegister.putExtra("device_id",d_id);
                intentRegister.putExtra("dgps_device_id",dgpsid);
                startActivity(intentRegister);
            }
        });
       /* binding.cvDeviceConfiguration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 Intent i = new Intent(Home.this, DeviceScanActivity.class);
                 startActivity(i);
            }
        });
       binding.svdlist.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               Intent intent = new Intent(Home.this, SavedDeviceList.class);
               startActivity(intent);
           }
       });*/

        binding.ipport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Home.this, IpActivity.class);
                startActivity(intent);
            }
        });
    }

}
