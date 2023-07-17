package com.example.tcp_ble.utility;
import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tcp_ble.Database.DatabaseOperation;
import com.example.tcp_ble.R;
import com.example.tcp_ble.adapter.BluetoothScanAdapter;
import com.example.tcp_ble.model.DataSelection;
import com.example.tcp_ble.model.Home;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
public class DeviceScanActivity extends AppCompatActivity {
    /*private BluetoothAdapter mBluetoothAdapter;
     boolean mScanning;
    private Handler mHandler;
    DatabaseOperation dbTask;
     BluetoothGatt mGatt;
    public static final int RequestPermissionCode = 1;
    private ArrayAdapter<String> mNewDevicesArrayAdapter;
    private static final long SCAN_PERIOD = 10000;
    BluetoothLeScanner bluetoothLeScanner;
    List<String> ls = new ArrayList<>();
    BluetoothAdapter bluetoothAdapter;
    BluetoothManager bluetoothManager;
    String device_name1="",address="";
    private final static int REQUEST_ENABLE_BT = 1;
    boolean is_deviceAvailable=false;
    String device_id="",dgps_device_id;
    String responsevalue;
    boolean isSecondtime = false;*/

    public   int RequestPermissionCode = 1;
    public int  REQUEST_ENABLE_BT = 1;
    boolean mScanning = false;
    Handler mHandler;
    Long SCAN_PERIOD = 3000L;
    BluetoothScanAdapter bluetoothScanAdapter ;
    BluetoothAdapter mBluetoothAdapter;
    BluetoothAdapter bluetoothAdapter;
    BluetoothLeScanner bluetoothLeScanner;
    HashSet<String> ls = new HashSet<>();
    RecyclerView list ;
    Button button_scan ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_device_scan);

        list = findViewById(R.id.list);
        button_scan = findViewById(R.id.button_scan);


        bluetoothScanAdapter = new BluetoothScanAdapter();
        list.setAdapter(bluetoothScanAdapter);
        bluetoothScanAdapter.setListerner(new BluetoothScanAdapter.ClickListerner() {
            @Override
            public void onSuccess(String item) {
                String deviceName = item.split(",")[0];
                String deviceAddress = item.split(",")[1];
                Intent intent = new Intent(DeviceScanActivity.this, DataSelection.class);
                intent.putExtra("DEVICE_NAME", deviceName);
                intent.putExtra("EXTRAS_DEVICE_ADDRESS", deviceAddress);
                intent.putExtra("device_id","");
                intent.putExtra("dgps_device_id","");
                startActivity(intent);
            }
        });

        locationcheck();
        mHandler = new Handler();

        if (!checkPermission()) {
            requestPermission();
        } else {
            scanLeDevice(true);
        }

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        initialiseBluetooth();

        if (bluetoothAdapter != null && !bluetoothAdapter.isEnabled()) {
            Intent enableIntent = new  Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
            scanLeDevice(true);
        }

        button_scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!checkPermission()) {
                    requestPermission();
                } else {
                    scanLeDevice(true);
                }
            }
        });
    }

   /* @Override
    protected void onResume() {
        super.onResume();
        if (!checkPermission()) {
            requestPermission();
        } else {
            scanLeDevice(true);
        }
    }*/

    public void scanLeDevice(boolean enable) {
        try {
            if (enable) {
                // Stops scanning after a pre-defined scan period.
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mScanning = false;
                        mBluetoothAdapter.getBluetoothLeScanner().stopScan(mLeScanCallback);
                        invalidateOptionsMenu();
                    }
                }, SCAN_PERIOD);
                mScanning = true;
                mBluetoothAdapter.getBluetoothLeScanner().startScan(mLeScanCallback);
            } else {
                mScanning = false;
                mBluetoothAdapter.getBluetoothLeScanner().stopScan(mLeScanCallback);
            }
            invalidateOptionsMenu();
        }catch (Exception ex){

        }

    }



    ScanCallback mLeScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    BluetoothDevice device = result.getDevice();
                    if (!ls.contains("" + device)) {


                        if (device.getName() != null && !ls.contains(device.getName()+","+device.getAddress())) {
                            ls.add(device.getName() + "," + device.getAddress());
                            ArrayList<String> list = new ArrayList<>(ls);
                            bluetoothScanAdapter.setAdapter(DeviceScanActivity.this, list);

                        }
                    }

                }
            });
        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            super.onBatchScanResults(results);
        }

        @Override
        public void onScanFailed(int errorCode) {
            super.onScanFailed(errorCode);
        }
    };


/*    BluetoothAdapter.LeScanCallback mLeScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (!ls.contains("" + device)) {
                        is_deviceAvailable = true

                        if (device.name != null && !ls.contains(device.name+","+device.address) && device.getName().startsWith("G_")) {
                            ls.add(device.name + "," + device.address)
                            val list : ArrayList<String> = ArrayList()
                            list.addAll(ls)
                            bluetoothScanAdapter?.setAdapter(this, list)

                        }
                    }

                }
            });
        }
    };*/


    public boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION);
        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION);
        int result2 = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.BLUETOOTH);
        int result3 = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.BLUETOOTH_ADMIN);
        boolean  check = false;
        check = result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED && result2 == PackageManager.PERMISSION_GRANTED && result3 == PackageManager.PERMISSION_GRANTED;
        return check;
    }

    public void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.BLUETOOTH, Manifest.permission.BLUETOOTH_ADMIN}, RequestPermissionCode);
    }


    public void initialiseBluetooth() {
        BluetoothManager  bluetoothManager = (BluetoothManager) getSystemService(BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();
        bluetoothLeScanner = bluetoothAdapter.getBluetoothLeScanner();
    }

    private void locationcheck() {
        LocationManager lm = (LocationManager) this.getSystemService(LOCATION_SERVICE);
        boolean gpsenabled = false;
        boolean networkenabled = false;
        try {
            gpsenabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ex) {
        }
        try {
            networkenabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception ex) {
        }
        if (!gpsenabled && !networkenabled) {
            AlertDialog dialogBuilder = new AlertDialog.Builder(this).create();
            LayoutInflater inflater = this.getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.alertyesorno, null);
            Button button1 = dialogView.findViewById(R.id.negativebutton);
            Button button2 = dialogView.findViewById(R.id.positive);
            TextView textView1 = dialogView.findViewById(R.id.header);
            TextView textView2 = dialogView.findViewById(R.id.messaggg);
            textView1.setText(getString(R.string.location));
            textView2.setText(getString(R.string.location_not_enabled));
            button1.setText(getString(R.string.open_setting));
            button2.setText(getString(R.string.cancel));
            dialogBuilder.setCancelable(true);
            button2.setOnClickListener(v -> dialogBuilder.dismiss());
            button1.setOnClickListener(v -> {
                dialogBuilder.dismiss();
                this.startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            });


            dialogBuilder.setView(dialogView);
            Window window = dialogBuilder.getWindow();
            dialogBuilder.show();
            if (window != null) { // After the window is created, get the SoftInputMode
                window.getAttributes().windowAnimations = R.style.DialogAnimation;
                  window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            }
        }
    }

       /* dbTask = new DatabaseOperation(DeviceScanActivity.this);
        dbTask.open();
        dbTask.getuserdetail();
        mHandler = new Handler();
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mNewDevicesArrayAdapter = new ArrayAdapter<>(this, R.layout.device_name);
        ListView newDevicesListView = findViewById(R.id.new_devices);
        newDevicesListView.setAdapter(mNewDevicesArrayAdapter);
        newDevicesListView.setOnItemClickListener(mDeviceClickListener);
        Intent intent=getIntent();
        device_name1=intent.getStringExtra("device_name");
        address=intent.getStringExtra("device_address");
        device_id=intent.getStringExtra("device_id");
        dgps_device_id=intent.getStringExtra("dgps_device_id");
        responsevalue=intent.getStringExtra("responsevalue");
        isSecondtime = intent.getBooleanExtra("isSecondtime",false);

        initialiseBluetooth();
        if (bluetoothAdapter != null && !bluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
            scanLeDevice(true);
        }
        if (!checkPermission()) {
            requestPermission();
        } else {
            scanLeDevice(true);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        scanLeDevice(true);
    }

    private void initialiseBluetooth() {
        bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();
        bluetoothLeScanner = bluetoothAdapter.getBluetoothLeScanner();
    }

    public void scanclk(View view) {
       // mNewDevicesArrayAdapter.clear();
        scanLeDevice(true);
    }

    public boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION);
        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION);
        int result2 = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.BLUETOOTH);
        int result3 = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.BLUETOOTH_ADMIN);
        return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED && result2 == PackageManager.PERMISSION_GRANTED
                && result3 == PackageManager.PERMISSION_GRANTED;
    }
    private void requestPermission() {
        ActivityCompat.requestPermissions(DeviceScanActivity.this, new
                String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.BLUETOOTH, Manifest.permission.BLUETOOTH_ADMIN}, RequestPermissionCode);
    }
    private void scanLeDevice(final boolean enable) {
        if (enable) {
            // Stops scanning after a pre-defined scan period.
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mScanning = false;
                    mBluetoothAdapter.stopLeScan(mLeScanCallback);
                }
            }, SCAN_PERIOD);

                mScanning = true;
                mBluetoothAdapter.startLeScan(mLeScanCallback);


        } else {
            mScanning = false;
            mBluetoothAdapter.stopLeScan(mLeScanCallback);
        }
    }

    private BluetoothAdapter.LeScanCallback mLeScanCallback =
            new BluetoothAdapter.LeScanCallback() {
                @Override
                public void onLeScan(final BluetoothDevice device, int rssi,
                                     byte[] scanRecord) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(!ls.contains(""+device))
                            {
//                                    if (device.getAddress().equalsIgnoreCase(address))
                                    //{
                                        is_deviceAvailable = true;
                                        ls.add("" + device);
                                        mNewDevicesArrayAdapter.add(device.getName() + "\n" + device.getAddress());
                                    //}
                            }

                        }
                    });
                }
            };

    public void connectToDevice(String name) {
        // if (mGatt == null) {
        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(name);
        mGatt = device.connectGatt(this, false, gattCallback);
        scanLeDevice(false);// will stop after first device detection
        // }
    }

    private final BluetoothGattCallback gattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            Log.i("onConnectionStateChange", "Status: " + status);
            switch (newState) {
                case BluetoothProfile.STATE_CONNECTED:
                    Log.i("gattCallback", "STATE_CONNECTED");
                    gatt.discoverServices();
                    break;
                case BluetoothProfile.STATE_DISCONNECTED:
                    Log.e("gattCallback", "STATE_DISCONNECTED");
                    break;
                default:
                    Log.e("gattCallback", "STATE_OTHER");
            }

        }

    };
    private AdapterView.OnItemClickListener mDeviceClickListener
            = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView<?> av, View v, int arg2, long arg3) {
            // Cancel discovery because it's costly and we're about to connect
            // mBtAdapter.cancelDiscovery();
            // Get the device MAC address, which is the last 17 chars in the View
            String info = ((TextView) v).getText().toString();
            String address = info.substring(info.length() - 17);
            BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
            *//*final Intent intent = new Intent(DeviceScanActivity.this, DeviceControlActivity.class);
            intent.putExtra(DeviceControlActivity.EXTRAS_DEVICE_NAME, device.getName());
            intent.putExtra(DeviceControlActivity.EXTRAS_DEVICE_ADDRESS, device.getAddress());
            intent.putExtra("device_id",device_id);
            intent.putExtra("dgps_device_id",dgps_device_id);
            intent.putExtra("responsevalue",responsevalue);
            startActivity(intent);*//*

            Intent intent = new Intent(DeviceScanActivity.this, DataSelection.class);
            intent.putExtra("DEVICE_NAME", device.getName());
            intent.putExtra("EXTRAS_DEVICE_ADDRESS", device.getAddress());
            intent.putExtra("device_id",device_id);
            intent.putExtra("dgps_device_id",dgps_device_id);
            intent.putExtra("isSecondtime",isSecondtime);
            startActivity(intent);
        }
    };
//    private void showAlert(String msg) {
//
//        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(DeviceScanActivity.this);
//        alertDialogBuilder.setTitle("Message");
//        alertDialogBuilder.setMessage(msg);
//        alertDialogBuilder
//                .setCancelable(false)
//                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int id) {
//                        Intent intent=new Intent(DeviceScanActivity.this,Device_detail.class);
//                        startActivity(intent);
//                    }
//                });
//
//        AlertDialog alertDialog = alertDialogBuilder.create();
//        alertDialog.show();
//
//    }*/

}
