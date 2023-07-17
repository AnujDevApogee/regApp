package com.example.tcp_ble.model;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tcp_ble.Database.DatabaseOperation;
import com.example.tcp_ble.R;
import com.example.tcp_ble.TextUtil;
import com.example.tcp_ble.bluetooth.SerialListener;
import com.example.tcp_ble.bluetooth.SerialService;
import com.example.tcp_ble.bluetooth.SerialSocket;
import com.example.tcp_ble.utility.BluetoothLeService;
import com.example.tcp_ble.utility.Constants;
import com.example.tcp_ble.utility.DeviceControlActivity;
import com.example.tcp_ble.utility.DeviceScanActivity;

import org.json.JSONObject;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Objects;

public class DataSelection extends AppCompatActivity implements ServiceConnection, SerialListener {
    public  enum Connected { False, Pending, True }
    private final static String TAG = DataSelection.class.getSimpleName();
    DatabaseOperation dbTask;
    Spinner maufactrur, device_type, model_name, modelno;
    ArrayList<String> maufactrurlist,device_typelist,model_namelist,modelnolist,devicelist;
    String maufactruritem;
    String device_typeitem;
    String model_nameitem;
    String modelnoitem;
    String val;
    EditText logminute,logsecond,uploadminute,uploadsecond;
    public static BluetoothLeService mBluetoothLeService;
    String mDeviceName, mDeviceAddress,d_id,dgpsid;
    int device_id=0, opid=0,dgps_id=0;
    ImageButton connect;
    public static boolean mConnected = false;
    TextView namedevice;
    TextView userName;
    Button getNumber;
    String imeiNumber = "";
    SharedPreferences sharedPreferences;
    com.marwaeltayeb.progressdialog.ProgressDialog progressDialog;
    boolean isSecondtime = false;
    String deviceId = "";
    String bleConfirmation = "";
    String fourgId = "";
    Boolean isWrongSelection = false;
   public static SerialService service = null;
    public static Connected connected = Connected.False;
    boolean isResumed = false;
    private String newline = TextUtil.newline_crlf;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_selection);
        dbTask=new DatabaseOperation(DataSelection.this);
        maufactrur = findViewById(R.id.mt);
        device_type = findViewById(R.id.dt);
        model_name = findViewById(R.id.mname);
        modelno= findViewById(R.id.mnumber);
        connect = findViewById(R.id.conect);
        namedevice = findViewById(R.id.namedevice);
        getNumber = findViewById(R.id.getNumber);

        final Intent intent = getIntent();
        mDeviceName = intent.getStringExtra("DEVICE_NAME");
        mDeviceAddress = intent.getStringExtra("EXTRAS_DEVICE_ADDRESS");
        d_id=intent.getStringExtra("device_id");
        dgpsid=    intent.getStringExtra("dgps_device_id");
        device_id=Integer.parseInt("24");
        dgps_id=Integer.parseInt("23");
        isSecondtime = intent.getBooleanExtra("isSecondtime",false);
        maufactrurlist=new ArrayList<>();
        device_typelist=new ArrayList<>();
        model_namelist=new ArrayList<>();
        modelnolist=new ArrayList<>();
        devicelist = new ArrayList<>();

        namedevice.setText(mDeviceName);

        dbTask.open();
       // maufactrurlist = dbTask.getmanufacturer();
        //device_typelist = dbTask.devicetype();
        //model_namelist = dbTask.modelname();



        maufactrurlist.add("Apogee");
        device_typelist.add("Finished");
        model_namelist.add("NAVIK200_1.1");
        model_namelist.add("TNAVIK50");
        model_namelist.add("NAVIK300_1.1");

        modelnolist.add("NAVIK200-1.1");
        modelnolist.add("NAVIK50-1.0_");
        modelnolist.add("NAVIK300_1.1");


        logminute=findViewById(R.id.logminute);
        logsecond=findViewById(R.id.second);
        uploadminute=findViewById(R.id.uploadminute);
        uploadsecond=findViewById(R.id.uploadsecond);
        userName=findViewById(R.id.usrName);


         logminute.setText(getString(R.string.zero));
         uploadminute.setText(getString(R.string.zero));
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(DataSelection.this);
        String userNameConst = sharedPreferences.getString(Constants.NAME, "");
         bindService(new Intent(this, SerialService.class), this, Context.BIND_AUTO_CREATE);
         userName.setText(userNameConst);


        final ArrayAdapter<String> Adapter1 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, maufactrurlist);
        Adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        maufactrur.setAdapter(Adapter1);

        maufactrur.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                maufactruritem = parent.getItemAtPosition(position).toString();
               // devicelist = dbTask.getmanufactureId(maufactruritem);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        final ArrayAdapter<String> Adapter2 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, device_typelist);
        Adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        device_type.setAdapter(Adapter2);

        device_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                device_typeitem = parent.getItemAtPosition(position).toString();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        final ArrayAdapter<String> Adapter3 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, model_namelist);
        Adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        model_name.setAdapter(Adapter3);

        model_name.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                model_nameitem = parent.getItemAtPosition(position).toString();
              /*  modelnolist = dbTask.modelno(model_nameitem);
                final ArrayAdapter<String> Adapter4 = new ArrayAdapter<String>(DataSelection.this, android.R.layout.simple_spinner_item, modelnolist);
                Adapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                modelno.setAdapter(Adapter4);*/
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        final ArrayAdapter<String> Adapter4 = new ArrayAdapter<String>(DataSelection.this, android.R.layout.simple_spinner_item, modelnolist);
        Adapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        modelno.setAdapter(Adapter4);
        modelno.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                modelnoitem = parent.getItemAtPosition(position).toString();
                fourgId = "";
               /* String spilitModelNo = modelnoitem.split("-")[0];
                String spilitbleName = mDeviceName.split("-")[0];
                if(!spilitModelNo.equals(spilitbleName)){
                    isWrongSelection = true;
                    alertdialog("Alert!","Wrong Selection, Please select right selection",DataSelection.this);
                }else {
                    isWrongSelection = false;
                    dbTask.open();
                    String modelId = dbTask.getModelId(modelnoitem);
                    String  getdevice = dbTask.getdeviceId(modelId);
                    String deviceId = getdevice.split(",")[0];
                    String finishedModelType = dbTask.getMakeName(getdevice.split(",")[1]);
                    ArrayList<String> moduleDeviceID = dbTask.getModuleFinishedId(deviceId);
                    String joined = TextUtils.join(", ", moduleDeviceID);
                    ArrayList<String> deviceDetails = dbTask.getDeviceDetail(joined);
                    String bleTypeId = dbTask.getDeviceTypeeId("4G");


                    for(int i =0 ; i< deviceDetails.size() ; i++) {
                        if(Objects.equals(deviceDetails.get(i).split(",")[1], bleTypeId)){
                            fourgId = deviceDetails.get(i).split(",")[2];
                            Log.d("fourg",fourgId);
                        }
                    }

                    if(fourgId.isEmpty()){
                        getNumber.setVisibility(View.GONE);
                    }else {
                        getNumber.setVisibility(View.VISIBLE);
                    }
                }
*/
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (connected == Connected.True) {
                    disconnect();
                    connect.setImageResource(R.drawable.disconnected7);
                } else {
                    connectBt();
                    connect.setImageResource(R.drawable.connected7);
                }
            }
        });

        getNumber.setOnClickListener(v -> {
            progressDialog  = new  com.marwaeltayeb.progressdialog.ProgressDialog(this)
                    .setDialogPadding(50)
                    .setTitle("Please Wait..")
                    .setText("Fetching IMEI Number.");

            progressDialog.show();
            final Handler handler = new Handler(Looper.getMainLooper());
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    // Write whatever to want to do after delay specified (1 sec)
                    Log.d("Handler", "Running Handler");
                    //Do something after 100ms
               /* mBluetoothLeService.conectToService(device_id,0);
                mBluetoothLeService.send( "",DataSelection.this,false,false,imeiQuery);*/
                    String imeiQuery = "$$$$,03,03,3,0,0,0000,####";
                    byte[] msgs = (imeiQuery + newline).getBytes(StandardCharsets.UTF_8);
                    try {
                        service.write(msgs);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }, 1000);
        });


        if(isSecondtime){

             bleConfirmation = "$$$$,08,"+deviceId+",02,1,"+mDeviceName+",0000, ####";
            /*bleNameConfirmation cs=new bleNameConfirmation();
            cs.execute();*/
        }
    }
    public void datasend(View view){
        if(imeiNumber.isEmpty()){
            Toast.makeText(DataSelection.this,"First Fetch IMEI Number.",Toast.LENGTH_SHORT).show();
        }/*else if(isWrongSelection){
            Toast.makeText(DataSelection.this,"Wrong Selection.",Toast.LENGTH_SHORT).show();
        }*/else {
            Callingserver cs=new Callingserver();
            cs.execute();
        }
    }

    int a;
    int b;

    private class Callingserver extends AsyncTask<String, String, String> {
        ProgressDialog dialog;
        @SuppressLint("WrongThread")
        @Override
        protected String doInBackground(String... params) {
            String result = "";


            JSONObject json1 = new JSONObject();
            try {

                a = 10;
                b = 20;
                String value = maufactruritem+","+device_typeitem+","+model_nameitem+","+modelnoitem+","+a+","+b+","+imeiNumber;
                Log.d(TAG, "doInBackground Value: "+value);
                BleModel dbmodel = new BleModel(DataSelection.this);
                result = dbmodel.sendData(value);
                Log.d(TAG, "doInBackground:result "+result);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
             // execution of result of Long time consuming operation
            val= result;
            if (result.contains("$$$$") )
            {
                dialog.dismiss();
                DatabaseOperation dbTask = new DatabaseOperation(DataSelection.this);
                dbTask.open();
                boolean rtes = dbTask.insertsaveddevice(maufactruritem, device_typeitem, model_nameitem, modelnoitem , a , b );
                if (rtes) {
                    System.out.println("Data inserted Successfully");
                    Toast.makeText(DataSelection.this, getString(R.string.data_inserted), Toast.LENGTH_SHORT).show();
                } else {
                    System.out.println("Insertion Problem");
                }
                dbTask.close();
                alertdialog(val);

            } else {
                dialog.dismiss();
                Toast.makeText(DataSelection.this, result, Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected void onPreExecute() {
            dialog = ProgressDialog.show(DataSelection.this, getString(R.string.fetching_data_from_server), getString(R.string.getting_id_password));
            dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            dialog.setProgress(0);
            dialog.setMax(100);
            dialog.show();
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

    public void alertdialog( final String data) {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(DataSelection.this);
//     builder1.setTitle(getString(R.string.data_getting_from_server));
        builder1.setTitle("Start Device Registration Process");
//      builder1.setMessage(data);
        Log.d(TAG, "alertdialog: "+data);
        builder1.setCancelable(true);
        builder1.setPositiveButton(
                getString(R.string.ok),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent i = new Intent(DataSelection.this, DeviceControlActivity.class);
                        String strName = null;
                        i.putExtra("responsevalue", data);
                        i.putExtra("DEVICE_NAME", mDeviceName);
                        i.putExtra("EXTRAS_DEVICE_ADDRESS", mDeviceAddress);
                        startActivity(i);
                        dialog.cancel();
                        finish();
                    }
                });

        AlertDialog alert11 = builder1.create();
        alert11.show();
    }

    /*BROADCAST RECIEVER USED FOR CONNECTION OF BLE */

    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
               mConnected = true;
                connect.setImageResource(R.drawable.connected7);
              //  updateConnectionState(R.string.connected);
                Toast.makeText(context, getString(R.string.your_connection_request_), Toast.LENGTH_SHORT).show();
                invalidateOptionsMenu();
            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
                mConnected = false;
                connect.setImageResource(R.drawable.disconnected7);
             //   updateConnectionState(R.string.disconnected);
                Toast.makeText(context, getString(R.string.your_connection_request_fail), Toast.LENGTH_SHORT).show();
                invalidateOptionsMenu();
            //    clearUI();
            } else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
            //    btn1.setVisibility(View.VISIBLE);
             //   btn2.setVisibility(View.VISIBLE);
             //   btn3.setVisibility(View.VISIBLE);
                // Show all the supported services and characteristics on the user interface.
                //displayGattServices(mBluetoothLeService.getSupportedGattServices());
            } else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
                displayData(intent.getStringExtra(BluetoothLeService.EXTRA_DATA));
            }
        }
    };

  /*  protected void onStart() {
        super.onStart();
        Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
        bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);
    }*/

    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
            if (!mBluetoothLeService.initialize()) {
                Log.e(TAG, "Unable to initialize Bluetooth");
                finish();
            }
            // Automatically connects to the device upon successful start-up initialization.
            mBluetoothLeService.connect(mDeviceAddress, DataSelection.this,device_id,opid);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBluetoothLeService = null;
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
       /* registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
        if (mBluetoothLeService != null) {
            final boolean result = mBluetoothLeService.connect(mDeviceAddress,DataSelection.this,device_id,opid);
            Log.d(TAG, "Connect request result=" + result);
        }*/
        isResumed = true;
    }



    /*HERE WE GET FINAL RESPONSE AND STRING FROM BASE AND ROVER MAINLY ROVER'S $GNGGA STRING*/

    private void displayData(String data) {
        Log.d(TAG, "displayData: "+data);
        if (data != null) {
            //String decrypt = decrypt(data);

            if(data.contains("$$$$,03")||data.contains("$$$$,3")){

                imeiNumber =  data.split(",")[4];
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(Constants.IMEI_NO, imeiNumber);
                editor.apply();
                progressDialog.dismiss();
                Toast.makeText(DataSelection.this,"imeiNumber "+imeiNumber,Toast.LENGTH_SHORT).show();

            }

            if(data.contains("$$$$,06")){
                Log.d(TAG, "displayData: 06"+data);
                String[] getString = data.split("\\$\\$\\$\\$,06");
                deviceId =  getString[1].split(",")[1];
            }






        }

    }

    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        return intentFilter;
    }

    public void alertdialog(String title , String msg, Context context) {
        AlertDialog.Builder builder1 =  new AlertDialog.Builder(context);
        builder1.setTitle(title);
        builder1.setMessage(msg);
        builder1.setCancelable(true);
        builder1.setPositiveButton("ok",new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // Continue with delete operation
                dialog.dismiss();
            }
        });
        AlertDialog alert11 = builder1.create();
        alert11.show();
    }

    @Override
    public void onStart() {
        super.onStart();
        if(service != null)
            service.attach(this);
        else
            startService(new Intent(this, SerialService.class));
        // prevents service destroy on unbind from recreated activity caused by orientation change
    }

    @Override
    protected void onDestroy() {

        try {
            unbindService(this);
        } catch(Exception ignored) {

        }

        if (connected != Connected.False){
            //disconnect();
            stopService(new Intent(this, SerialService.class));
        }



        super.onDestroy();

    }

    private void disconnect() {
        connected = Connected.False;
        service.disconnect();
        connect.setImageResource(R.drawable.disconnected7);
    }




    @Override
    public void onServiceConnected(ComponentName name, IBinder binder) {
        service = ((SerialService.SerialBinder) binder).getService();
        service.attach(this);
        if(isResumed ) {
            //initialStart = false;
            runOnUiThread(this::connectBt);
        }
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        service = null;

    }

    @Override
    public void onSerialConnect() {
        connected = Connected.True;
        connect.setImageResource(R.drawable.connected7);
    }

    @Override
    public void onSerialConnectError(Exception e) {
        if(e.getMessage().contains("gatt status 133")){
            runOnUiThread(this::connectBt);
        }else {
            disconnect();
        }

    }

    @Override
    public void onSerialRead(byte[] data) {
        receive(data);
    }

    @Override
    public void onSerialIoError(Exception e) {
        disconnect();
    }

    private void connectBt() {
        try {
            BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            BluetoothDevice device = bluetoothAdapter.getRemoteDevice(mDeviceAddress);
            connected = Connected.Pending;
            SerialSocket socket = new SerialSocket(getApplicationContext(), device);
            service.connect(socket);
            //    progressDialog =  new Utils().progressDialog(this, "Conecting Bluetooth...");
            //   progressDialog.show();



        } catch (Exception e) {
            onSerialConnectError(e);
        }
    }

    private void receive(byte[] data) {
        String msg = new String(data);
        String msgss = String.valueOf(TextUtil.toCaretString(msg, newline.length() != 0));
        displayData(msgss);
    }


}
