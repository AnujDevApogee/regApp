package com.example.tcp_ble.utility;


import static com.example.tcp_ble.model.DataSelection.service;

import static java.lang.System.currentTimeMillis;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;


import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.tcp_ble.Database.DatabaseOperation;
import com.example.tcp_ble.R;
import com.example.tcp_ble.RetroFit.RetroFitClient;
import com.example.tcp_ble.TextUtil;
import com.example.tcp_ble.bluetooth.SerialListener;
import com.example.tcp_ble.bluetooth.SerialService;
import com.example.tcp_ble.bluetooth.SerialSocket;
import com.example.tcp_ble.model.BleModel;
import com.example.tcp_ble.model.DataSelection;
import com.example.tcp_ble.model.RequestData;
import com.google.gson.JsonParser;


import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;


import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DeviceControlActivity extends AppCompatActivity implements ServiceConnection, SerialListener {
    public static final String EXTRAS_DEVICE_NAME = "DEVICE_NAME";
    public static final String EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS";
    private final static String TAG = DeviceControlActivity.class.getSimpleName();
    public static String textview;
    public static String lat_lang;
    public static String latlongvalue;
    public static String StatusData;
    public static String finalResponse;
    static String ip = "120.138.10.146";
    static String port = "8080";
    Spinner mode;
    ImageView calender;
    TextView dateSelected;
    List<String> models;
    Button btn1, btn2, btn3;
    String mDeviceName;
    String selectedDate;
    Double noOfDays;
    String imeiNo;
    int keyPersonID;
    boolean sendst = true;
    ImageButton refresh, tcpConnect, end;
    ImageView connect;
    String item = "";
    ListView deviceListView;
    String latitude = "";
    String longitude = "";
    String responseval = "";
    String key = "aesEncryptionKey";
    String initVector = "ncryptionIntVecc";
    String val;
    ArrayAdapter<String> listAdapter;
    ArrayList<String> deviceList;
    DatabaseOperation dbTask = new DatabaseOperation(this);
    int device_id = 0, opid = 0, dgps_id = 0;
    Boolean isFirstTime = true;
    String bleCmd, resCmd;
    String resRegNo = "", resDeviceId = "", resPassword = "", bleRegNo = "", bleDeviceId = "", blePassword = "", resHexCmd = "", bleHexCmd = "";
    Boolean isCmdMatch = false;
    String bleConfirmation = "";
    String deviceId = "";
    String bleName = "";

    SharedPreferences sharedPreferences;
    com.marwaeltayeb.progressdialog.ProgressDialog progressDialog;
    Boolean isAcknowledge = false;
    String acknowledgementString = "";
    Boolean isSendData = false;
    private TextView mConnectionState;
    private String mDeviceAddress;
    private BluetoothLeService mBluetoothLeService;
    private ArrayList<ArrayList<BluetoothGattCharacteristic>> mGattCharacteristics = new ArrayList<>();
    private boolean mConnected = false;
    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
                mConnected = true;
                connect.setImageResource(R.drawable.connected7);
                updateConnectionState(R.string.connected);
                isSendData = true;
                Toast.makeText(context, getString(R.string.your_connection_request_), Toast.LENGTH_SHORT).show();
                invalidateOptionsMenu();
            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
                mConnected = false;
                connect.setImageResource(R.drawable.disconnected7);
                updateConnectionState(R.string.disconnected);
                isSendData = false;
                Toast.makeText(context, getString(R.string.your_connection_request_fail), Toast.LENGTH_SHORT).show();
                invalidateOptionsMenu();
                clearUI();
            } else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                btn1.setVisibility(View.VISIBLE);
                btn2.setVisibility(View.VISIBLE);
                btn3.setVisibility(View.VISIBLE);
                // Show all the supported services and characteristics on the user interface.
                //displayGattServices(mBluetoothLeService.getSupportedGattServices());
            } else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
                displayData(intent.getStringExtra(BluetoothLeService.EXTRA_DATA));
            }
        }
    };
    private BluetoothGattCharacteristic mNotifyCharacteristic;


    /*BROADCAST RECIEVER USED FOR CONNECTION OF BLE */
    private final ExpandableListView.OnChildClickListener servicesListClickListner =
            new ExpandableListView.OnChildClickListener() {
                @Override
                public boolean onChildClick(ExpandableListView parent, View v, int groupPosition,
                                            int childPosition, long id) {
                    if (mGattCharacteristics != null) {
                        final BluetoothGattCharacteristic characteristic =
                                mGattCharacteristics.get(groupPosition).get(childPosition);
                        final int charaProp = characteristic.getProperties();
                        if ((charaProp | BluetoothGattCharacteristic.PROPERTY_READ) > 0) {
                            // If there is an active notification on a characteristic, clear
                            // it first so it doesn't update the data field on the user interface.
                            if (mNotifyCharacteristic != null) {
                                mBluetoothLeService.setCharacteristicNotification(
                                        mNotifyCharacteristic, false);
                                mNotifyCharacteristic = null;
                            }
                            mBluetoothLeService.readCharacteristic(characteristic);
                        }
                        if ((charaProp | BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {
                            mNotifyCharacteristic = characteristic;
                            mBluetoothLeService.setCharacteristicNotification(
                                    characteristic, true);
                        }
                        return true;
                    }
                    return false;
                }
            };
    private String newline = TextUtil.newline_crlf;
    private AdapterView.OnItemClickListener mDeviceClickListener
            = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView<?> av, View v, int arg2, long arg3) {
        }
    };
    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
            if (!mBluetoothLeService.initialize()) {
                Log.e(TAG, "Unable to initialize Bluetooth");
                finish();
            }
            // Automatically connects to the device upon successful start-up initialization.
            mBluetoothLeService.connect(mDeviceAddress, DeviceControlActivity.this, device_id, opid);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBluetoothLeService = null;
        }
    };

    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        return intentFilter;
    }

    private void clearUI() {
        //mGattServicesList.setAdapter((SimpleExpandableListAdapter) null);
        // mDataField.setText(R.string.no_data);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gatt_services_characteristics);
        final Intent intent = getIntent();
        responseval = intent.getStringExtra("responsevalue");
        mDeviceName = intent.getStringExtra(EXTRAS_DEVICE_NAME);
        mDeviceAddress = intent.getStringExtra("EXTRAS_DEVICE_ADDRESS");
        String d_id = intent.getStringExtra("device_id");
        device_id = Integer.parseInt("24");
        String dgpsid = intent.getStringExtra("dgps_device_id");
        dgps_id = Integer.parseInt("23");
        deviceListView = findViewById(R.id.deviceListView);

        mode = findViewById(R.id.type);
        calender = findViewById(R.id.ic_calender);
        dateSelected = findViewById(R.id.tvSelectedDate);

        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        TextView tvmDeviceAddress = (TextView) findViewById(R.id.device_address);
        mConnectionState = findViewById(R.id.connection_state);
        connect = findViewById(R.id.conect);
        refresh = findViewById(R.id.img1);
        tcpConnect = findViewById(R.id.tcp);
        dbTask = new DatabaseOperation(DeviceControlActivity.this);
        listAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1);
        deviceList = new ArrayList<>();
        deviceListView.setVisibility(View.GONE);
        deviceListView.setAdapter(listAdapter);
        deviceListView.setOnItemClickListener(mDeviceClickListener);
        btn1 = findViewById(R.id.send);
        btn2 = findViewById(R.id.recieve);
        btn3 = findViewById(R.id.button);
        btn1.setVisibility(View.GONE);
        btn2.setVisibility(View.GONE);
        btn3.setVisibility(View.GONE);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(DeviceControlActivity.this);
         imeiNo = sharedPreferences.getString(Constants.IMEI_NO, "");
         keyPersonID = Integer.parseInt(sharedPreferences.getString(Constants.KEY_PERSON_ID, ""));

        tvmDeviceAddress.setText(mDeviceAddress);

        if (DataSelection.connected == DataSelection.Connected.True) {
            updateConnectionState(R.string.connected);
            connect.setImageResource(R.drawable.connected7);
        } else {
            updateConnectionState(R.string.disconnected);
            connect.setImageResource(R.drawable.disconnected7);
        }

        calender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                try {
                    openCalender();

                } catch (Exception e) {
                    Log.d(TAG, "onClick:Exception "+e.getMessage());
                }

            }
        });

        connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (DataSelection.connected == DataSelection.Connected.True) {

                    //    mBluetoothLeService.disconnect();
                    disconnect();
                    connect.setImageResource(R.drawable.disconnected7);
                } else {
                    //  mBluetoothLeService.connect(mDeviceAddress,DeviceControlActivity.this,device_id,opid);
                    connectBt();
                }
            }
        });

        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Callingserver callingserver = new Callingserver();
                callingserver.execute();
            }
        });

//        tcpConnect.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//              Intent intent1=new Intent(DeviceControlActivity.this,TCPActivity.class);
//            //  finish();
//                startActivity(intent1);
//
//
//            }
//        });


        /*ALL THE SPINNER DEFINE BELOW WITH THEIR DATA*/
        dbTask.open();
        models = dbTask.getBleoperations();
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, models);
        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // attaching data adapter to spinner
        mode.setAdapter(dataAdapter);
        mode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String item = parent.getItemAtPosition(position).toString();
                if (!item.equals("--select--")) {
                    Log.d(TAG, "onItemSelected:imeiNo+noOfdays \n"+imeiNo+"---"+noOfDays);
//**********************************************************************************************************************************************************
                    sendSubscriptionDate_NoOfdays();
//                    deviceConfirmation ();

//**********************************************************************************************************************************************************

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        bindService(new Intent(this, SerialService.class), this, Context.BIND_AUTO_CREATE);

    }
    public void sendSubscriptionDate_NoOfdays(){
//*******************************************************************************************************************************
        String result = "";
        HttpResponse response;
        Log.d(TAG, "sendSubscriptionDate_NoOfdays: "+noOfDays);

        int day=noOfDays.intValue();
        Log.d(TAG, "sendSubscriptionDate_NoOfdays: "+day);

        try {

            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
            HttpPost httppost;
//            httppost = new HttpPost("http://" + "192.168.1.28" + ":" + "8082" + "/RegistrationCumAllotment/deviceSubscriptionDate");
            httppost = new HttpPost("http://" + "120.138.10.146" + ":" + "8080" + "/RegistrationCumAllotment/deviceSubscriptionDate");
            HttpParams httpParameters = new BasicHttpParams();
            HttpClient httpClient = new DefaultHttpClient(httpParameters);
            httppost.setHeader("Content-type", "application/json");
            httppost.setEntity(new StringEntity(imeiNo+","+day, "UTF-8"));
            try {
                response = httpClient.execute(httppost);
                result = EntityUtils.toString(response.getEntity());
                httpClient.getConnectionManager().shutdown();
                if(result.equals("Data Saved")){
                    Log.d(TAG, "onResponse:Data Saved "+result);
                    deviceConfirmation ();
                }else if(result.equals("Device Already Registered")){
                    Toast.makeText(DeviceControlActivity.this, "Device Already Registered", Toast.LENGTH_SHORT).show();
                }
                Log.d(TAG, "sendSubscriptionDate_NoOfdays: "+result);
            } catch (Exception e) {
                Log.e(TAG, "Error in http data " + e.toString());
            }
        } catch (Exception e) {
            Log.e(TAG, "Error in http connection " + e.toString());
        }

//*******************************************************************************************************************************




        /*RequestData requestData=new RequestData(imeiNo+","+noOfDays.toString());
        Call<String> call = RetroFitClient.getRetofitClient(20).subscriptionDate(requestData);
        call.enqueue(new Callback<String>() {

                         @Override
                         public void onResponse(Call<String> call, Response<String> response) {
                            if(response.isSuccessful()){
                                String msg=response.body();
                                Log.d(TAG, "onResponseSubsd: "+msg);
                                if(msg!=null){
                                    if(msg.equals("Data Saved")){
                                        Log.d(TAG, "onResponse:Data Saved "+msg);
//                                        deviceConfirmation ();
                                    }else if(msg.equals("Device Already Registered")){
                                        Toast.makeText(DeviceControlActivity.this, "Device Already Registered", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }else{
                                Toast.makeText(DeviceControlActivity.this, "Failed to get data! Try again later", Toast.LENGTH_SHORT).show();
                            }


                         }

                         @Override
                         public void onFailure(Call<String> call, Throwable t) {
                             Log.d(TAG, "onFailure: "+t.getMessage());
                         }
                     });
*/
    }
    public void deviceConfirmation (){
        dbTask.open();
        int bleOperation_id = dbTask.bleOperation_id(item);

        // mBluetoothLeService.conectToService(device_id,bleOperation_id);
        //  responseval = responseval + "\r\n";
        // mBluetoothLeService.send( item,DeviceControlActivity.this,false,false,responseval);

        byte[] msgs = (responseval + newline).getBytes(StandardCharsets.UTF_8);
        Log.d(TAG, "onItemSelected: responseval " + responseval);
        try {
            service.write(msgs);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //   btn1.setVisibility(View.VISIBLE);
        btn2.setVisibility(View.VISIBLE);
        // btn3.setVisibility(View.VISIBLE);

        //finish();
    }

    public void senddata() {

        final Handler ha = new Handler();
        ha.post(new Runnable() {

            @Override
            public void run() {
                BleModel dbmodel = new BleModel(DeviceControlActivity.this);

                if (resCmd != null && !resCmd.equals("not ready") && !resCmd.equals("end of file")) {
                    String[] spilitCmd = resCmd.split(",");
                    resRegNo = spilitCmd[2];
                    resDeviceId = spilitCmd[3];
                    resPassword = spilitCmd[4];
                    if (spilitCmd.length > 9) {
                        resHexCmd = spilitCmd[9];
                    }
                }

                if (bleCmd != null) {
                    String[] spilitCmd = bleCmd.split(",");
                    bleRegNo = spilitCmd[2];
                    bleDeviceId = spilitCmd[3];
                    blePassword = spilitCmd[4];
                    if (spilitCmd.length > 9) {
                        bleHexCmd = spilitCmd[9];
                    }
                }

                if (resRegNo != null && !resRegNo.isEmpty() && resDeviceId != null && !resDeviceId.isEmpty() && resPassword != null && !resPassword.isEmpty() && bleRegNo != null && !bleRegNo.isEmpty()
                        && bleDeviceId != null && !bleDeviceId.isEmpty() && blePassword != null && !blePassword.isEmpty()) {
                    if (resRegNo.equals(bleRegNo) && resDeviceId.equals(bleDeviceId) && resPassword.equals(blePassword)) {
                        isCmdMatch = true;
                    }
                }

                String value = "data";
                if (val != null && isCmdMatch) {/*val from ble */
                    String[] splitVal = val.split("####");
                    responseval = dbmodel.sendcontinueedata(splitVal[0] + "####");  /*val send to server*/
                    resCmd = responseval;
                } else if (!isCmdMatch && !isFirstTime) {
                    responseval = dbmodel.sendcontinueedata(responseval);
                    resCmd = responseval;
                } else {
                    responseval = dbmodel.sendcontinueedata(value);
                    resCmd = responseval;
                }
                Toast.makeText(DeviceControlActivity.this, responseval, Toast.LENGTH_SHORT).show();
                sendst = true;
                // mBluetoothLeService.send( item,DeviceControlActivity.this,false,false,responseval);/* send responseval send to ble */
                byte[] msgs = (responseval + newline).getBytes(StandardCharsets.UTF_8);
                Log.d(TAG, "senddata: responseval " + responseval);

                try {
                    service.write(msgs);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if (!responseval.equals("end of file")) {
                    ha.postDelayed(this, 2000);
                }

            }
        });
    }

    /**
     * customizable toast
     *
     * @param message
     */
    private void toastMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    public void sendclick(View view) {

        senddata();
    }

    public void resendclick(View view) {
        BleModel dbmodel = new BleModel(DeviceControlActivity.this);
        if (val != null) {/*val from ble */
            progressDialog = new com.marwaeltayeb.progressdialog.ProgressDialog(this)
                    .setDialogPadding(50)
                    .setTitle("Please Wait..")
                    .setText("Sending Registration Confirmation.");

            progressDialog.show();

            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    // Write whatever to want to do after delay specified (1 sec)
                    String result = dbmodel.sendRegcnfrmdata(val);
                    Log.d(TAG, "run:sendRegcnfrmdata "+result);
                    if (result != null && result.contains("@")) {
                        isAcknowledge = true;
                        //progressDialog.setText("Sending BLE Name to device");
                        progressDialog.setText("Sending Subscription String to device");
                        String msg = result.split("@")[0];
                        String bleRename = result.split("@")[1];
                        bleName = bleRename.split(",")[5];

                        Toast.makeText(DeviceControlActivity.this, msg, Toast.LENGTH_SHORT).show();
                        byte[] msgs = (bleRename + newline).getBytes(StandardCharsets.UTF_8);
                        Log.d(TAG, "resendclick: bleRename responseval " + bleRename);
                        try {
                            service.write(msgs);
                        } catch (IOException e) {
                            e.printStackTrace();
                            Log.d(TAG, "run: ");
                        }
                        //  mBluetoothLeService.send( item,DeviceControlActivity.this,false,false,bleRename);
                    } else if (result != null) {
                        progressDialog.dismiss();
                        Toast.makeText(DeviceControlActivity.this, result, Toast.LENGTH_SHORT).show();
                    }
                }
            }, 1000);

           /*if(result.equalsIgnoreCase("Registration Complete")){
               Toast.makeText(DeviceControlActivity.this, "Registration Complete", Toast.LENGTH_SHORT).show();*//*val send to server*//*
           }else{
               Toast.makeText(DeviceControlActivity.this, "Registration not Completed", Toast.LENGTH_SHORT).show();
           }*/

        } else {
            Toast.makeText(DeviceControlActivity.this, "Registration String not coming.Please Retry.", Toast.LENGTH_SHORT).show();
            Intent i = new Intent(DeviceControlActivity.this, DataSelection.class);
            startActivity(i);
        }

    }

    public void clearclick(View view) {
        sendst = true;
        mBluetoothLeService.send(item, DeviceControlActivity.this, false, false, "$$$$,05,01,6oQY1s8YaGAs98xUvZTPRtA/tydSXypdqsAkvwdjNNg=,0000,####");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // if the result is capturing Image
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            try {
                String ip = data.getStringExtra("ip");
                String port = data.getStringExtra("port");
                mBluetoothLeService.connectTcp();
            } catch (Exception e) {
                System.out.println("err" + e);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
       /* registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
        if (mBluetoothLeService != null) {
            final boolean result = mBluetoothLeService.connect(mDeviceAddress,DeviceControlActivity.this,device_id,opid);
            Log.d(TAG, "Connect request result=" + result);
        }*/
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    private void updateConnectionState(final int resourceId) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                mConnectionState.setText(resourceId);
            }
        });
    }

    public String encrypt(String value) {
        try {
            IvParameterSpec iv = new IvParameterSpec(initVector.getBytes("UTF-8"));
            SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes("UTF-8"), "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);

            byte[] encrypted = cipher.doFinal(value.getBytes());
            return Base64.encodeToString(encrypted, Base64.DEFAULT);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    /*HERE WE GET FINAL RESPONSE AND STRING FROM BASE AND ROVER MAINLY ROVER'S $GNGGA STRING*/

    public String decrypt(String encrypted) {
        try {
            IvParameterSpec iv = new IvParameterSpec(initVector.getBytes("UTF-8"));
            SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes("UTF-8"), "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);
            byte[] original = cipher.doFinal(Base64.decode(encrypted, Base64.DEFAULT));

            return new String(original);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }

    private void displayData(String data) {
        Log.d(TAG, "displayData: " + data);
        if (data != null) {
            try {
                if (deviceList.size() > 10) {
                    listAdapter.clear();
                    deviceList.clear();
                }
                if (data.contains("$$$$,04")) {
                    Log.d(TAG, "displayData 04: "+data);
                    Log.d(TAG, "displayData: IN $$$$,04");
                    String[] getString = data.split(",04");
                    val = "$$$$,04" + getString[1];
                    bleCmd = "$$$$,04" + getString[1];
                }

                if (data.contains("$$$$,02")) {
                    String[] getString = data.split(",02");
                    String bleMessage = getString[1].split(",")[2];
                    if (Objects.equals(bleMessage, "Bluetooth is renaming. Please reconnect after 10 sec.")) {
                        isSendData = false;
                        alertdialog("Device Alert!", bleMessage + "Ble Name : " + bleName, this);
                    }
                }

                if (data.contains("$$$$,01") && isAcknowledge) {
                    String[] getString = data.split("\\$\\$\\$\\$,01");
                    acknowledgementString = "$$$$,01" + getString[1];
                    //  alertdialog("Device Alert!",acknowledgementString,this);
                    subscriptionConfirmation cs = new subscriptionConfirmation();
                    cs.execute();
                }

                if (data.contains("$$$$,06")) {
                    Log.d(TAG, "displayData: 06" + data);
                    String[] getString = data.split("\\$\\$\\$\\$,06");
                    deviceId = getString[1].split(",")[1];
                }

                if (data.contains("$GNGGA")) {
                    String fixTime = data.split(",")[1];
                    latitude = data.split(",")[2];
                    longitude = data.split(",")[4];
                    latlongvalue = latitude + "," + longitude;


                    String fix = data.split(",")[6];
                    StatusData = "";
                    if (fix.equalsIgnoreCase("0")) {
                        StatusData = "invalid";
                        listAdapter.add("invalid");
                        deviceList.add(data);
                    } else if (fix.equalsIgnoreCase("1")) {
                        StatusData = "GPS fix";
                        listAdapter.add("GPS fix");
                        deviceList.add("GPS fix");
                    } else if (fix.equalsIgnoreCase("2")) {
                        StatusData = "DGPS fix";
                        listAdapter.add("DGPS fix");
                        deviceList.add("DGPS fix");
                    } else if (fix.equalsIgnoreCase("3")) {
                        StatusData = "PPS fix";
                        listAdapter.add("PPS fix");
                        deviceList.add("PPS fix");
                    } else if (fix.equalsIgnoreCase("4")) {
                        StatusData = "Real time kinematic";
                        listAdapter.add("Real time kinematic");
                        deviceList.add("Real time kinematic");
                    } else if (fix.equalsIgnoreCase("5")) {
                        StatusData = "Float RTK";
                        listAdapter.add("Float RTK");
                        deviceList.add("Float RTK");
                    } else if (fix.equalsIgnoreCase("6")) {
                        StatusData = "estimated";
                        listAdapter.add("estimated");
                        deviceList.add("estimated");
                    } else if (fix.equalsIgnoreCase("7")) {
                        StatusData = "manual input mode";
                        listAdapter.add("manual input mode");
                        deviceList.add("manual input mode");
                    } else {
                        StatusData = "simulation mode";
                        listAdapter.add("simulation mode");
                        deviceList.add("simulation mode");
                    }
                    lat_lang = latitude + "_" + longitude + "_" + StatusData + "_" + fixTime;
                }

                listAdapter.add(data);
                deviceList.add(data);

            } catch (Exception e) {
                e.printStackTrace();
            }
            //String decrypt = decrypt(data);
        }

    }

    public void alertdialog(String title, String msg, Context context) {
        android.app.AlertDialog.Builder builder1 = new android.app.AlertDialog.Builder(context);
        builder1.setTitle(title);
        builder1.setMessage(msg);

        builder1.setPositiveButton("oK", (dialog, which) -> {
            progressDialog.dismiss();
            connectBt();
            bleConfirmation = "$$$$,08," + deviceId.trim() + ",02,1," + bleName.trim() + ",0000,####";
            Log.d(TAG, "bleConfirmation: " + bleConfirmation);
            bleNameConfirmation cs = new bleNameConfirmation();
            cs.execute();
            dialog.cancel();
        });

        builder1.setCancelable(false);
        AlertDialog alert11 = builder1.create();
        alert11.setCanceledOnTouchOutside(false);
        alert11.show();
    }
//    private boolean checkConnection() {
//        boolean isConnected = ConnectivityReciever.isConnected();
//        return isConnected;
//    }

   /* @Override
    public void onBackPressed() {
        mBluetoothLeService.serverDisconnect();
        Intent intent=new Intent(DeviceControlActivity.this, Home.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        finish();
        startActivity(intent);
    }*/

    public void alertdialog(final String data, final String subsString) {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(DeviceControlActivity.this);
        builder1.setTitle("Alert!");
        builder1.setMessage(data);
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                getString(R.string.ok),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {



                        Log.d(TAG, "onClick: Data"+data);

                        if(data.equals("Registration, Ble Renaming and Subscription Successfull")){
                            Log.d(TAG, "onClick BleModel.finalApi: "+BleModel.finalApi);
                            String reg_No= sharedPreferences.getString(Constants.REG_NO, "");
                            Log.d(TAG, "onClick: BleModel.finalApi "+reg_No);
                            sendKeyPerson(reg_No);
                        }

                        if (DataSelection.connected == DataSelection.Connected.True) {
                            dialog.cancel();
//                            connectBt();
                            dbTask.open();
                            int bleOperation_id = dbTask.bleOperation_id(item);


                      /*  mBluetoothLeService.disconnect();
                       mBluetoothLeService.conectToService(device_id, bleOperation_id);
                        mBluetoothLeService.connect(mDeviceAddress,DeviceControlActivity.this,device_id,opid);
                       mBluetoothLeService.send( item,DeviceControlActivity.this,false,false,subsString);*//* send subscription send to ble */

                            byte[] msgs = (subsString + newline).getBytes(StandardCharsets.UTF_8);
                            Log.d(TAG, "resendclick: responseval subsString " + subsString);
                            try {

                                service.write(msgs);




                            } catch (IOException e) {
                                Log.d(TAG, "onClick: resendclick: responseval subsString Error"+ e.getMessage());
                                e.printStackTrace();
                            }
                        }
//                        else {
//                            // alertdialog(data, subsString);
//                            connectBt();
//                            Toast.makeText(DeviceControlActivity.this, "Please wait Bluetooth to connect.",Toast.LENGTH_SHORT).show();
//                        }

                    }
                });
        AlertDialog alert11 = builder1.create();
        alert11.show();
    }
    public void sendKeyPerson(String regNo){
        String result="";
        HttpResponse response;



        try {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
            HttpPost httppost;
//            httppost = new HttpPost("http://" + "192.168.1.28" + ":" + "8082" + "/RegistrationCumAllotment/savePersonDeviceRegistration");
            httppost = new HttpPost("http://" + "120.138.10.146" + ":" + "8080" + "/RegistrationCumAllotment/savePersonDeviceRegistration");
            HttpParams httpParameters = new BasicHttpParams();
            HttpClient httpClient = new DefaultHttpClient(httpParameters);
            httppost.setHeader("Content-type", "application/json");
            httppost.setEntity(new StringEntity(String.valueOf(keyPersonID+","+regNo), "UTF-8"));
            try {
                response = httpClient.execute(httppost);
                result = EntityUtils.toString(response.getEntity());
                httpClient.getConnectionManager().shutdown();
                Log.d(TAG, "sendKeyPerson: "+result);
                if(result.equals("Device Person Mapped Successfully")){
                    Log.d(TAG, "onResponse: Person Mapped "+result);
                    Toast.makeText(DeviceControlActivity.this, result, Toast.LENGTH_SHORT).show();
                }else if(result.equals("Device Not Mapped")){
                    Toast.makeText(DeviceControlActivity.this, result, Toast.LENGTH_SHORT).show();
                }

            } catch (Exception e) {
                Log.e(TAG, "sendKeyPersonResponseError " + e.toString());
            }
        } catch (Exception e) {
            Log.e(TAG, "Error in http connection sendKeyPerson " + e.toString());
        }

    }

    public void alertdialog(final String data) {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(DeviceControlActivity.this);
        builder1.setTitle("Alert!");
        builder1.setMessage(data);
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                getString(R.string.ok),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        finish();
                    }
                });
        AlertDialog alert11 = builder1.create();
        alert11.show();
    }
    Calendar calendar = Calendar.getInstance();
    int year = calendar.get(Calendar.YEAR);
    int month = calendar.get(Calendar.MONTH);
    int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
public void openCalender() {
    DatePickerDialog datePickerDialog = new DatePickerDialog(DeviceControlActivity.this,
            new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                    selectedDate=day + "/" + (month + 1) + "/" + year;
                    dateSelected.setText(day + "/" + (month + 1) + "/" + year);
                    Log.d(TAG, "onClick: selectedDate"+selectedDate);
                    convertDateTodays();
                }
            }, year, month, dayOfMonth);

    datePickerDialog.show();
    }
    public void convertDateTodays()  {

// Assuming you have two date strings in the format "yyyy-MM-dd"
try{

    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy",
            Locale.ENGLISH);
    String datestart = sdf.format(currentTimeMillis());

    Date start = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH)
            .parse(datestart);
    Date end = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH)
            .parse(selectedDate);

    double mDifference = end.getTime() - start.getTime();
    noOfDays = (mDifference / (24 * 60 * 60 * 1000));
    Log.d(TAG, "onClick: No.Of Days"+noOfDays);
// Print the result
}catch (Exception e){
    Log.d(TAG, "convertDateTodays:Exception "+e.getMessage());
}
    }

    @Override
    public void onStart() {
        super.onStart();
        if (service != null)
            service.attach(this);
        // prevents service destroy on unbind from recreated activity caused by orientation change
    }

    @Override
    protected void onDestroy() {

        try {
            unbindService(this);
        } catch (Exception ignored) {

        }
        super.onDestroy();
    }

    private void disconnect() {
        DataSelection.connected = DataSelection.Connected.False;
        service.disconnect();
        connect.setImageResource(R.drawable.disconnected7);
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {

    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
    }

    @Override
    public void onSerialConnect() {
        DataSelection.connected = DataSelection.Connected.True;
        connect.setImageResource(R.drawable.connected7);
    }

    @Override
    public void onSerialConnectError(Exception e) {
        disconnect();
    }

    @Override
    public void onSerialRead(byte[] data) {
        receive(data);
    }

    @Override
    public void onSerialIoError(Exception e) {
//        Toast.makeText(DeviceControlActivity.this,"onSerialConnectError" , Toast.LENGTH_SHORT).show();
//        Intent i = new Intent(DeviceControlActivity.this, DataSelection.class);
//        startActivity(i);
//        disconnect();
    }

    private void receive(byte[] data) {
        String msg = new String(data);
        String msgss = String.valueOf(TextUtil.toCaretString(msg, newline.length() != 0));
        displayData(msgss);
    }

    private void connectBt() {
        try {
            BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            BluetoothDevice device = bluetoothAdapter.getRemoteDevice(mDeviceAddress);
            DataSelection.connected = DataSelection.Connected.Pending;
            SerialSocket socket = new SerialSocket(getApplicationContext(), device);
            service.connect(socket);
            //    progressDialog =  new Utils().progressDialog(this, "Conecting Bluetooth...");
            //   progressDialog.show();
        } catch (Exception e) {
            onSerialConnectError(e);
        }
    }

    private class Callingserver extends AsyncTask<String, String, Long> {
        ProgressDialog dialog;

        @Override
        protected Long doInBackground(String... params) {
            dbTask.open();
            BleModel model = new BleModel(DeviceControlActivity.this);
            String serverip = dbTask.getServerIp();

          /*  model.setServer_ip(serverip.split("_")[0]);
            model.setPort(serverip.split("_")[1]);*/

            dbTask.close();
            long result = model.requestBleDetail();
            return result;

        }

        @Override
        protected void onPostExecute(Long result) {
            dialog.dismiss();
            if (result > 0) {
                Toast.makeText(DeviceControlActivity.this, "Data successfully recieved", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(DeviceControlActivity.this, " no updation  Found", Toast.LENGTH_LONG).show();
            }
        }

        @Override
        protected void onPreExecute() {
            dialog = ProgressDialog.show(DeviceControlActivity.this, "", "Proccessing....Please wait");
            dialog.show();
            // Things to be done before execution of long running operation. For
            // example showing ProgessDialog
        }

        @Override
        protected void onProgressUpdate(String... text) {
            //firstBar.
            // Things to be done while execution of long running operation is in
            // progress. For example updating ProgessDialog
        }
    }

    private class bleNameConfirmation extends AsyncTask<String, String, String> {
        ProgressDialog dialog;

        @SuppressLint("WrongThread")
        @Override
        protected String doInBackground(String... params) {
            String result = "";
            JSONObject json1 = new JSONObject();
            try {
                BleModel dbmodel = new BleModel(DeviceControlActivity.this);
                result = dbmodel.sendBleConfirmation(bleConfirmation);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            // execution of result of Long time consuming operation
            //   val= result;
            if (!result.isEmpty()) {

                dialog.dismiss();
                String msg = result;
                String subsString = "";
                if (result.contains("@")) {
                    msg = result.split("@")[0];
                    subsString = result.split("@")[1];

                }
                // finish();
                Log.d(TAG, "onPostExecute: "+result);
                alertdialog(msg, subsString);

            } else {
                dialog.dismiss();
                Toast.makeText(DeviceControlActivity.this, getString(R.string.failed_fetching_data), Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected void onPreExecute() {
            dialog = ProgressDialog.show(DeviceControlActivity.this, "Alert!", "Sending  BLE Confirmation to server.");
            dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            dialog.setProgress(0);
            dialog.setMax(100);
            dialog.show();
            // Things to be done before execution of long running operation. For
            // example showing ProgessDialog
        }

        @Override
        protected void onProgressUpdate(String... text) {
            //firstBar.
            // Things to be done while execution of long running operation is in
            // progress. For example updating ProgessDialog
        }
    }

    private class subscriptionConfirmation extends AsyncTask<String, String, String> {
        ProgressDialog dialog;

        @SuppressLint("WrongThread")
        @Override
        protected String doInBackground(String... params) {
            String result = "";


            JSONObject json1 = new JSONObject();
            try {

                BleModel dbmodel = new BleModel(DeviceControlActivity.this);
                result = dbmodel.sendSubscriptionConfirmation(acknowledgementString);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            // execution of result of Long time consuming operation
            //   val= result;
            if (!result.isEmpty()) {
                dialog.dismiss();
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(Constants.REG_NO, result.split(",")[2]);
                editor.apply();
                progressDialog.setText("Sending BLE Rename String to device");
                String bleRename = result.split("@")[1];
                bleName = bleRename.split(",")[5];
                //    Toast.makeText(DeviceControlActivity.this, msg, Toast.LENGTH_SHORT).show();
                byte[] msgs = (bleRename + newline).getBytes(StandardCharsets.UTF_8);
                Log.d(TAG, "resendclick: responseval subsString " + bleRename);
                try {
                    service.write(msgs);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                //   alertdialog(result);
            } else {
                dialog.dismiss();
                Toast.makeText(DeviceControlActivity.this, getString(R.string.failed_fetching_data), Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected void onPreExecute() {
            dialog = ProgressDialog.show(DeviceControlActivity.this, "Alert!", "Sending  Subscription Confirmation to server.");
            dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            dialog.setProgress(0);
            dialog.setMax(100);
            dialog.show();
            // Things to be done before execution of long running operation. For
            // example showing ProgessDialog
        }

        @Override
        protected void onProgressUpdate(String... text) {
            //firstBar.
            // Things to be done while execution of long running operation is in
            // progress. For example updating ProgessDialog
        }
    }
}
