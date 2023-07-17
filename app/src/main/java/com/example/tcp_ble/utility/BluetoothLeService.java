package com.example.tcp_ble.utility;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.example.tcp_ble.Database.DatabaseOperation;
import com.example.tcp_ble.R;
import com.example.tcp_ble.model.BleModel;


import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * Created by admin on 11/19/2018.
 */
public class BluetoothLeService extends Service {
    connect_thread connect_thread;
    private final IBinder mBinder = new LocalBinder();
    int counter = 0;
    String purpose = "";
    String timer="";
    public boolean isConnected;
    Context context;
    receive_thread1 thread1;
    public byte[] data1=null;
    DatabaseOperation dbTask;
    private final static String TAG = "BluetoothLeService";
    final static UUID MY_UUID_RN4020_CHARACTERISTIC_WRITE = UUID.fromString("00002a29-0000-1000-8000-00805f9b34fb");
    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;
    private String mBluetoothDeviceAddress;
    private BluetoothGatt mBluetoothGatt;
    private int mConnectionState = STATE_DISCONNECTED;
    BluetoothGattCharacteristic bluetoothGattCharacteristic;
    BluetoothGattCharacteristic bluetoothGattReadCharacteristic;
    BluetoothGattService bluetoothGattService;
    private static final int STATE_DISCONNECTED = 0;
    private static final int STATE_CONNECTING = 1;
    private static final int STATE_CONNECTED = 2;
    public final static String ACTION_GATT_CONNECTED =
            "com.example.bluetooth.le.ACTION_GATT_CONNECTED";
    public final static String ACTION_GATT_DISCONNECTED =
            "com.example.bluetooth.le.ACTION_GATT_DISCONNECTED";
    public final static String ACTION_GATT_SERVICES_DISCOVERED =
            "com.example.bluetooth.le.ACTION_GATT_SERVICES_DISCOVERED";
    public final static String ACTION_DATA_AVAILABLE =
            "com.example.navitus.bleexample.ACTION_DATA_AVAILABLE";
    public final static String EXTRA_DATA =
            "com.example.bluetooth.le.EXTRA_DATA";
    String startdel = "", enddel = "";
    byte stbyt = (byte) 0;
    byte endbt = (byte) 0;
    String command = "";
    String device_address = "";
    int operation_id = 0, device_id = 0;
    private Socket socket = null;
    public static String strIP = "120.138.10.146";
    public static int nPort = 8060;
    private BufferedOutputStream out = null;
    Boolean bConnect = Boolean.valueOf(false);
    Boolean bTelnet = Boolean.valueOf(false);
    TelnetOpt mtelnet;
    static boolean isrover = false;
    String serverData = "";
    byte[] readBuf;
    String data="";
    DeviceControlActivity dle = new DeviceControlActivity();
    Connection_Thread connection_thread;
    int lsSize=0;
    List<String> finalcommand;
    private static final byte STATE_OFF = 0x00;
    private static final byte STATE_ON = 0x01;
    private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            String intentAction;
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                intentAction = ACTION_GATT_CONNECTED;
                mConnectionState = STATE_CONNECTED;
                isConnected = true;
                broadcastUpdate(intentAction);
                Log.i(TAG, "Connected to GATT server.");
                Log.i(TAG, "Attempting to start service discovery:" +
                        mBluetoothGatt.discoverServices());
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                intentAction = ACTION_GATT_DISCONNECTED;
                mConnectionState = STATE_DISCONNECTED;
                isConnected = false;
                Log.i(TAG, "Disconnected from GATT server.");
                broadcastUpdate(intentAction);
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                mBluetoothGatt = gatt;
                dbTask = new DatabaseOperation(context);
                dbTask.open();
                String service_char = dbTask.getserviceCharop(device_id, operation_id);
                String service_uuid = service_char.split("_")[0];
               String charachtristics_uuid = service_char.split("_")[1];
                String char_read_uuid = service_char.split("_")[2];
                List<BluetoothGattService> services = gatt.getServices();
                for (BluetoothGattService service : services) {
                    UUID test = service.getUuid();
                    String original = test.toString();
                    if (original.equals(service_uuid)) {
                        try {
                            bluetoothGattService = service;
                            bluetoothGattCharacteristic = service.getCharacteristic(UUID.fromString(charachtristics_uuid));
                            bluetoothGattReadCharacteristic = service.getCharacteristic(UUID.fromString(charachtristics_uuid));
                            if (bluetoothGattCharacteristic == null) {
                                final Intent intent = new Intent(ACTION_DATA_AVAILABLE);
                                intent.putExtra(EXTRA_DATA, "Error in write charachtristics uuid");
                                sendBroadcast(intent);
                            } else if (bluetoothGattReadCharacteristic == null) {
                                final Intent intent = new Intent(ACTION_DATA_AVAILABLE);
                                intent.putExtra(EXTRA_DATA, "Error in read charachtristics uuid");
                                sendBroadcast(intent);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        for (BluetoothGattDescriptor descriptor : bluetoothGattReadCharacteristic.getDescriptors()) {
                            setCharacteristicNotification(bluetoothGattCharacteristic, true);
                            descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                            gatt.writeDescriptor(descriptor);
                            final Intent intent = new Intent(ACTION_GATT_SERVICES_DISCOVERED);
                            sendBroadcast(intent);
                        }
                    }
                }
                if (bluetoothGattService == null) {
                    final Intent intent = new Intent(ACTION_DATA_AVAILABLE);
                    intent.putExtra(EXTRA_DATA, "service " + service_uuid + "not fount");
                    sendBroadcast(intent);
                }

            } else {
                Log.w(TAG, "onServicesDiscovered received: " + status);
            }

        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            final Intent intent = new Intent(ACTION_DATA_AVAILABLE);
            if (status == BluetoothGatt.GATT_SUCCESS) {
                long startTime = System.currentTimeMillis();
                String result = "success";
                intent.putExtra(EXTRA_DATA, "" + command + getString(R.string.command_write_successfully) + startTime + "_" + timer);
                sendBroadcast(intent);
            } else {
                String result = "not success";
                intent.putExtra(EXTRA_DATA, "" + command + getString(R.string.command_write_fail_click_on_resend_button_for_resend_command));
                sendBroadcast(intent);
            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt,
                                         BluetoothGattCharacteristic characteristic,
                                         int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
            }
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt,
                                            BluetoothGattCharacteristic characteristic) {
            broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
        }

        @Override
        public void onMtuChanged(BluetoothGatt gatt, int mtu, int status) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                String res = "succes";
            }
        }

    };

    private void broadcastUpdate(final String action) {
        final Intent intent = new Intent(action);
        sendBroadcast(intent);
    }

    private void broadcastUpdate(final String action,
                                 final BluetoothGattCharacteristic characteristic) {
        final Intent intent = new Intent(action);
        final byte[] data = characteristic.getValue();
        String finalString = new String(data, 0, data.length);
         String response =   bytesToHex(data);
         if(response.contains("01")){
             System.out.println("Data Acknowledge");
         }else{
             System.out.println("Data not Acknowledge");
         }
        if (!finalString.contains("$G")) {
//            try {
//                finalString = "";
//                for (int rs = 0; rs <= data.length; rs++) {
//                    finalString = finalString + "" + data[rs];
//                }
//                finalString =   finalString;
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
        }
        DeviceControlActivity.finalResponse = finalString;
        intent.putExtra(EXTRA_DATA, finalString);
        sendBroadcast(intent);
    }



    public static String bytesToHex(byte[] in) {
        final StringBuilder builder = new StringBuilder();
        for(byte b : in) {
            builder.append(String.format("%02x", b));
        }
        return builder.toString();
    }


    public class LocalBinder extends Binder {
        public BluetoothLeService getService() {
            return BluetoothLeService.this;
        }
    }




    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        // After using a given device, you should make sure that BluetoothGatt.close() is called
        // such that resources are cleaned up properly.  In this particular example, close() is
        // invoked when the UI is disconnected from the Service.
        close();
        return super.onUnbind(intent);
    }


    /**
     * Initializes a reference to the local Bluetooth adapter.
     *
     * @return Return true if the initialization is successful.
     */
    public boolean initialize() {
        // For API level 18 and above, get a reference to BluetoothAdapter through
        // BluetoothManager.
        if (mBluetoothManager == null) {
            mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
            if (mBluetoothManager == null) {
                Log.e(TAG, "Unable to initialize BluetoothManager.");
                return false;
            }
        }
        mBluetoothAdapter = mBluetoothManager.getAdapter();
        if (mBluetoothAdapter == null) {
            Log.e(TAG, "Unable to obtain a BluetoothAdapter.");
            return false;
        }
        return true;
    }

    /**
     * Connects to the GATT server hosted on the Bluetooth LE device.
     *
     * @param address The device address of the destination device.
     * @return Return true if the connection is initiated successfully. The connection result
     * is reported asynchronously through the
     * {@code BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)}
     * callback.
     */
    public boolean connect(final String address, Context context1, int id, int op_id) {
        context = context1;
        dbTask = new DatabaseOperation(context);
        device_id = id;
        operation_id = op_id;
        device_address = address;
        if (mBluetoothAdapter == null || address == null) {
            Log.w(TAG, "BluetoothAdapter not initialized or unspecified address.");
            return false;
        }
        // Previously connected device.  Try to reconnect.
        if (mBluetoothDeviceAddress != null && address.equals(mBluetoothDeviceAddress)
                && mBluetoothGatt != null) {
            Log.d(TAG, "Trying to use an existing mBluetoothGatt for connection.");
            if (mBluetoothGatt.connect()) {
                mConnectionState = STATE_CONNECTING;
                return true;
            } else {
                return false;
            }
        }
        final BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        if (device == null) {
            Log.w(TAG, "Device not found.  Unable to connect.");
            return false;
        }
        // We want to directly connect to the device, so we are setting the autoConnect
        // parameter to false.
        mBluetoothGatt = device.connectGatt(this, false, mGattCallback);
        Log.d(TAG, "Trying to create a new connection.");
        mBluetoothDeviceAddress = address;
        mConnectionState = STATE_CONNECTING;
        return true;
    }

//    public void send1(String purposest, Context con, boolean sendSt, boolean is_newTask) {
//        boolean is_new = is_newTask;
//        boolean resendStatus = sendSt;
//        purpose = purposest;
//        context = con;
//        dbTask = new DatabaseOperation(context);
//        writeDataToCharacteristic(bluetoothGattCharacteristic, resendStatus, is_new);
//    }

    public void send(String purposest, Context con, boolean sendSt, boolean is_newTask,String val) {
        boolean is_new = is_newTask;
        boolean resendStatus = sendSt;
        purpose = purposest;
        context = con;
        dbTask = new DatabaseOperation(context);
        writeDataToCharacteristic(bluetoothGattCharacteristic, resendStatus, is_new,val);
    }


    public void recieve() {
        mBluetoothGatt.readCharacteristic(bluetoothGattCharacteristic);
    }

    /**
     * Disconnects an existing connection or cancel a pending connection. The disconnection result
     * is reported asynchronously through the
     * {@code BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)}
     * callback.
     */
    public void disconnect() {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            return;
        }
        mBluetoothGatt.disconnect();
    }

    /**
     * After using a given BLE device, the app must call this method to ensure resources are
     * released properly.
     */
    public void close() {
        if (mBluetoothGatt == null) {
            return;
        }
        mBluetoothGatt.close();
        mBluetoothGatt = null;
    }

    /**
     * Request a read on a given {@code
     *
     *
     *
     * }. The read result is reported
     * asynchronously through the {@code BluetoothGattCallback#onCharacteristicRead(android.bluetooth.BluetoothGatt, android.bluetooth.BluetoothGattCharacteristic, int)}
     * callback.
     *
     * @param characteristic The characteristic to read from.
     */
    public void readCharacteristic(BluetoothGattCharacteristic characteristic) {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        mBluetoothGatt.readCharacteristic(characteristic);
    }

    /**
     * Enables or disables notification on a give characteristic.
     *
     * @param characteristic Characteristic to act on.
     * @param enabled        If true, enable notification.  False otherwise.
     */

    public void setCharacteristicNotification(BluetoothGattCharacteristic characteristic,
                                              boolean enabled) {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        mBluetoothGatt.setCharacteristicNotification(characteristic, enabled);
        if (MY_UUID_RN4020_CHARACTERISTIC_WRITE.equals(characteristic.getUuid())) {
            for (BluetoothGattDescriptor descriptor : characteristic.getDescriptors()) {
                String a = descriptor.getUuid().toString();
            }
            BluetoothGattDescriptor descriptor = characteristic.getDescriptor(
                    (MY_UUID_RN4020_CHARACTERISTIC_WRITE));
            descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
            mBluetoothGatt.writeDescriptor(descriptor);
            if (!mBluetoothGatt.writeCharacteristic(characteristic)) {
                Log.w(TAG, "Failed to write characteristic");
            }
        }
    }



//    public void getCommandlstfire(List<String>commandlstfr) {
//        commandlstfire = commandlstfr;
//        HextoByte();
//    }


//    public void HextoByte() {
//
//        for (int y = 0; y < commandlstfire.size(); y++) {
//            String s = commandlstfire.get(y);
//            int len =s.length();
//
//            data1 = new byte[len/2];
//
//            for(int i = 0; i < len; i+=2){
//                data1[i/2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character.digit(s.charAt(i+1), 16));
//            }
//
//            System.out.println(data1);
//
//        }
//        System.out.println(data1);
//
//
//       // writeDataToCharacteristic1(bluetoothGattCharacteristic);
//        // mBluetoothLeService.send(item, DeviceControlActivity.this, false, true);
//        //return data;
//    }




    /**
     * Retrieves a list of supported GATT services on the connected device. This should be
     * invoked only after {@code BluetoothGatt#discoverServices()} completes successfully.
     *
     * @return A {@code List} of supported services.
     */
    public void writeDataToCharacteristic(final BluetoothGattCharacteristic ch, boolean resendStatus, boolean isnewtask,String val) {
        try {
            Thread.sleep(2000);
            boolean mtu = mBluetoothGatt.requestMtu(500);
            if (resendStatus) {
                counter--;
            } else if (isnewtask) {
                counter = 0;
            }
            try {

//                String strArray[] = command.split(",");
//                byte[] messageBytes = new byte[strArray.length];
//                try {
//                    for (int a1 = 0; a1 <= strArray.length; a1++) {
//                        messageBytes[a1] = Byte.parseByte(strArray[a1].trim());
//                    }
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }

               /* int len = val.length();

                byte[] data = new byte[len / 2];

                for (int i = 0; i < len; i += 2) {
                    data[i / 2] = (byte) ((Character.digit(val.charAt(i), 16) << 4) + Character.digit(val.charAt(i + 1), 16));
                }*/

//command="jhdufgh;r4tiogju;tyuo67pyho;p4yt6ji'6uoyojhl;ytkhytlojkhouuuuuuuuuuuuuuuuuijjjjjjjjjjjjjjjjjjjjjjj98989898989898989898989898" +
//        "vbcxbbbbbbbbbbbbbbbbbbbbbhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhiiiiiiiiiiiiiiiiiiiiiii" +
//        "iiiiiiiiii989898989898989bvnnnnnnnnnnn=]]]]]]]]]]]9iiiiiiiiiiiiinnnnnnnnnnnnn" +
//        "jjk,yu ilyiu,kl l.,jkl.;jkl/nnnnnnnnnnio98pppppppppppppppppppppppppppppnnnmmmmmmm" +
//        "jhkj k,lui;.po;m,0ip;'m mmmmmmmmmmmmmmmmmmjhhhhhhhhhhhhhhhhhhhhhhhhhh8nbbbbb";
//                messageBytes=command.getBytes();
//                readBuf=messageBytes;
//                receive_thread2 thread2=new receive_thread2();
//                thread2.start();
               //  byte[] cmndbytes = db.HextoByte();


                ch.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT);
                ch.setValue(val);
                long startTime = System.currentTimeMillis();
//             //   }
                Thread.sleep(1000);
                boolean result = mBluetoothGatt.writeCharacteristic(ch);
                if (counter < lsSize) {
                    final Intent intent = new Intent(ACTION_DATA_AVAILABLE);
                    intent.putExtra(EXTRA_DATA, getString(R.string.here)+" " + (lsSize - counter) + "  " + getString(R.string.more_command_for_sending_next_command_click_on_send_button) + startTime);
                    sendBroadcast(intent);
                } else {
                  //  connectTcp();
                    final Intent intent = new Intent(ACTION_DATA_AVAILABLE);
                    intent.putExtra(EXTRA_DATA, "Here no  more command ");
                    sendBroadcast(intent);
                }
                Thread.sleep(1000);
            } catch (Exception e) {
                e.printStackTrace();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

}
//        if (!command.isEmpty()) {
//            byte[] hexaByte = DatatypeConverter.parseHexBinary(command);
//            String jaya = Arrays.toString(hexaByte);
//            commandBean.setCommand(jaya);
//        }

        //   }

    public void HextoByteCheck1(byte[] data) {
        data1 = data;
    }

    public boolean writeCorrectionDataToCharacteristic(final BluetoothGattCharacteristic ch, byte[] correctionData) {
        try {
            //new receive_thread().start();
            boolean mtu = mBluetoothGatt.requestMtu(512);

            try {

                byte[] myvar = ("123456789012345678901234567890a123456789012345678901234567890a12345678901234567890123" +
                        "4567890a123456789012345678901234567890t" +
                        "123456789012345678901234567890a123456789012345678901234567890a12hjgjhghjjhjkhjkhhjlk," +
                        "kjjlhgjll3456789012345678901234567890a12345678901234567890jklk;lljkhjtgyghftgrtdersaeddsd1234567890t").getBytes();
//                byte[] messageCorrectionBytes = new byte[600];
//                messageCorrectionBytes = correctionData.getBytes("UTF-8");
                //byte[][] var = myvar;
                ch.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT);
                ch.setValue(myvar);
                Thread.sleep(2000);

                boolean a = mBluetoothGatt.writeCharacteristic(ch);
                System.out.println("boolean " + a);
                return a;
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //   }

        return false;
    }

    public void conectToService(int device_id, int opid) {
        // mBluetoothGatt=gatt;
        dbTask = new DatabaseOperation(context);
        dbTask.open();
        String service_char = dbTask.getserviceCharop(device_id, opid);
       /* String service_uuid = service_char.split("_")[0];
        String charachtristics_uuid = service_char.split("_")[1];
        String charachtristics_read_uuid = service_char.split("_")[2];*/
        String service_uuid="6e400001-b5a3-f393-e0a9-e50e24dcca9e";
        String charachtristics_uuid="6e400002-b5a3-f393-e0a9-e50e24dcca9e";
        String charachtristics_read_uuid="6e400003-b5a3-f393-e0a9-e50e24dcca9e";
       /* String service_uuid="00001523-1212-efde-1523-785feabcd123";
        String charachtristics_uuid="00001525-1212-efde-1523-785feabcd123";
        String charachtristics_read_uuid="00001524-1212-efde-1523-785feabcd123";*/
        List<BluetoothGattService> services = mBluetoothGatt.getServices();
        for (BluetoothGattService service : services) {
            UUID test = service.getUuid();
            String original = test.toString();
            if (original.equals(service_uuid)) {
                try {
                    bluetoothGattService = service;
                    bluetoothGattCharacteristic = service.getCharacteristic(UUID.fromString(charachtristics_uuid));
                    bluetoothGattReadCharacteristic = service.getCharacteristic(UUID.fromString(charachtristics_read_uuid));
                    if (bluetoothGattCharacteristic == null) {
                        final Intent intent = new Intent(ACTION_DATA_AVAILABLE);
                        intent.putExtra(EXTRA_DATA, "Error in charachtristics uuid");
                        sendBroadcast(intent);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                for (BluetoothGattDescriptor descriptor : bluetoothGattReadCharacteristic.getDescriptors()) {
                    setCharacteristicNotification(bluetoothGattReadCharacteristic, true);
                    descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                    mBluetoothGatt.writeDescriptor(descriptor);
                    final Intent intent = new Intent(ACTION_GATT_SERVICES_DISCOVERED);
                    sendBroadcast(intent);
                }
            }
        }
        if (bluetoothGattService == null) {
            final Intent intent = new Intent(ACTION_DATA_AVAILABLE);
            intent.putExtra(EXTRA_DATA, "service " + service_uuid + "not fount");
            sendBroadcast(intent);
        }

    }

    class connect_thread extends Thread {
        Boolean bRun = Boolean.valueOf(true);

        public void run() {
            Connect();

        }

        public void onTerminate() {
            this.bRun = Boolean.valueOf(false);

        }
    }

    void Connect() {
        try {
            socket = new Socket();
            dbTask.open();
            String tcpIp = dbTask.getTcpIp();
            strIP = "120.138.10.146";
            nPort = 8060;
            //strIP=
            socket.connect(new InetSocketAddress(strIP, nPort), 5000);
            try {
                this.out = new BufferedOutputStream(this.socket.getOutputStream());
                new receive_thread().start();

                this.bTelnet = Boolean.valueOf(true);
                this.mtelnet = new TelnetOpt();
                this.mtelnet.nState = 0;
                try {
                    sendRequestToServer();

//                    String user_id, password;
//                    if (isrover) {
//                        user_id = "jjjjjjjjrtgrg";
//                        password = "jyoti1";
//                    } else {
//                        user_id = "jjjjjjjjhthyhh";
//                        password = "jyoti12";
//                    }
//                    int user_length = user_id.length();
//                    byte len = (byte) user_length;
//                    byte[] namearr = user_id.getBytes();
//                    int passlength = password.length();
//                    byte pasbyte = (byte) passlength;
//                    byte[] passarray = password.getBytes();
//                    byte[] startdel = {125, 125, 125, 125, 16, 20, 00, 06, 40, 51, 49, 73, 64, 70, 03, 02, 00, 30, 00, 06, 41, 42, 43, 44, 45, 46, 00, len};
//                    //this.out.write(startdel);
//                    this.out.write(namearr);
////                    byte[] bytes = {00, pasbyte};
////                    byte[] enddel = {126, 126, 126};
////                    this.out.write(bytes);
////                    this.out.write(passarray);
////                    this.out.write(enddel);
////                    this.out.flush();
//                    while(true)
//                    {
//                        sendRequestToServer();
//                        try
//                        {
//                            Thread.sleep(200);
//                        }
//                       catch (Exception e){
//                            e.printStackTrace();
//
//                        }
//                    }


                    // pairedDevicesArrayAdapter.add("Server " + connect);

                } catch (Exception e) {
                    e.printStackTrace();

                }

                this.bConnect = Boolean.valueOf(true);
                //this.handler.sendMessage(this.handler.obtainMessage(2, "Connected\n"));

            } catch (UnsupportedEncodingException e2) {
                e2.printStackTrace();

                //  this.handler.sendMessage(this.handler.obtainMessage(2, "Don't support encoding\n"));

            } catch (IOException e3) {
                e3.printStackTrace();

                // this.handler.sendMessage(this.handler.obtainMessage(2, "Don't create object\n"));

            }
        } catch (Exception e4) {
            e4.printStackTrace();
            if (this.socket != null) {
                this.socket = null;
                // this.handler.sendMessage(this.handler.obtainMessage(2, "Fail to connect\n"));

            }
        }
    }

    boolean sendRequestToServer() {
        try {
            this.out = new BufferedOutputStream(this.socket.getOutputStream());
            String data = "jjjjjjjjjjrewrfsfsdgfdgfhyghghgyjyhkj";

            new receive_thread().start();
            byte[] startdel = {125, 125, 125, 125, -126, 20, 00, 06, 40, 51, 49, 73, 64, 70, 03, 02, 126, 126, 126, 126};
            this.out.write(data.getBytes());
            this.out.flush();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

//    boolean sendLatlngToServer(String lat_long) {
//        try {
//            this.out = new BufferedOutputStream(this.socket.getOutputStream());
//             data = "aaaaaaaaaaaaaaaaaaabhi_"+lat_long+"_";
////            new receive_thread().start();
////            byte[] startdel = {125, 125, 125, 125, -126, 20, 00, 06, 40, 51, 49, 73, 64, 70, 03, 02, 126, 126, 126, 126};
//            this.out.write(data.getBytes());
//            this.out.flush();
//            return true;
//        } catch (IOException e) {
//            e.printStackTrace();
//            return false;
//        }
//    }
         /*Sending RTK data to the Server with Multiple Fields*/

//    boolean sendLatlngToServer1(String lat_long) {
//        JSONObject postData = new JSONObject();
//        try {
//            String latitude="";
//            String longitude="";
//            String Statusdata="";
//            String fixTime="";
//            if (lat_long!=null) {
//                latitude = lat_long.split("_")[0];
//                longitude = lat_long.split("_")[1];
//                Statusdata = lat_long.split("_")[2];
//                fixTime = lat_long.split("_")[3];
//            }
//            // lat_lang = latitude+"_"+longitude;
//            String Vehicle_Id = DeviceControlActivity.textview;
//            String fuelcapacity ="100";
//            String retardantcapacity ="100";
////            String Vehicle_Id ="2";
//            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
//            String currentDateandTime = sdf.format(new Date());
//            postData.put("latl", latitude);
//            postData.put("longl", longitude);
//            postData.put("Statusdata", Statusdata);
//            postData.put("fuelcapacity", fuelcapacity);
//            postData.put("retardantcapacity", retardantcapacity);
//            postData.put("fixTime", fixTime);
//            postData.put("currentDateandTime", currentDateandTime);
//            postData.put("Vehicle_Id", Vehicle_Id);
//
//
//            new DeviceControlActivity.SendData().execute("http://120.138.10.146:8001/fireApp/map/location", postData.toString());
//
//            /*For SAving VAlue in DAtaBase*/
//
//
//
//
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        return false;
//    }



    public boolean sendCorrection(byte[] readBuf, String operation) {


        return writeCorrectionDataToCharacteristic(bluetoothGattCharacteristic, readBuf);
    }

    class TelnetOpt {
        byte[] OptBuf = new byte[32];
        Boolean bACK_ECHO;
        Boolean bACK_SGA;
        Boolean bECHO;
        Boolean bSGA;
        int nOptLen;
        int nState = 0;

        TelnetOpt() {

        }
    }

    public void sendData(String message) {
        try {
            byte[] messagebyte = null;
            byte[] startdel = {125, 125, 125, 125, 45};
            byte[] enddel = {126, 126, 126, 126};
            if (isrover) {

            } else {

                messagebyte = message.getBytes();
            }
            out.write(startdel);
            out.write(messagebyte);
            out.write(enddel);
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void connectTcp() {

        connect_thread = new connect_thread();
        this.connect_thread.start();
    }

    class receive_thread extends Thread {
        private byte[] byte_data = new byte[1024];
        private int size = 0;

//        receive_thread() {
//            try {
//                //Thread.sleep(2000);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
        //  }

        public void run() {
            while (true) {

                BleModel bleModel = new BleModel(context);
                // byte_data[]=bleModel.funcRequest("jhdsiufhifugriu");
                try {
                    this.size = socket.getInputStream().read(this.byte_data);
                    if (this.size < 1) {
                        //SubActivity.this.disconnect();
                        return;
                    }
                    if (true) {
                        byte[] telnet_data = new byte[1024];
                        int nLen = this.size;

                        readBuf = Arrays.copyOfRange(byte_data, 0, nLen);
                        this.size = 0;

                        serverData = new String(readBuf, 0, nLen);

                        //readBuf=null;
                        String recmsg = "";
                        char d = 0;
                        String temp = "";
                        boolean connectionServer = false;
                        boolean sendToServer = false;
                        thread1 = new receive_thread1();
                        thread1.start();
                        //sendRequestToServer();
                        //sendCorrection(readBuf,"rover_configuration");
                        if (serverData.contains(getString(R.string.not))) {

                            final Intent intent = new Intent(ACTION_DATA_AVAILABLE);
                            intent.putExtra(EXTRA_DATA, new String("" + serverData));
                            //intent.putExtra(EXTRA_DATA, new String(""+"NOT Authrize shweta"));
                            sendBroadcast(intent);
                        } else if (serverData.contains(getString(R.string.authorised))) {
                            final Intent intent = new Intent(ACTION_DATA_AVAILABLE);
                            intent.putExtra(EXTRA_DATA, " " + "server response:" + serverData);
                            sendBroadcast(intent);
                            //connectionServer = true;
                            //connectionServer = sendRequestToServer();
                        }

//                            if (connectionServer) {
//                                final Intent intent = new Intent(ACTION_DATA_AVAILABLE);
//                                intent.putExtra(EXTRA_DATA, new String(" " + serverData));
//                                sendBroadcast(intent);
//                                sendToServer = sendCorrection(readBuf, "rover_configuration");
//                                while (sendToServer) {
//                                    connectionServer = sendRequestToServer();
//                                    sendToServer = sendCorrection(readBuf, "rover_configuration");
//                                }
//                            }

                    }
                    //  }

                } catch (Exception e) {
                    e.printStackTrace();
                }
//                final Intent intent = new Intent(ACTION_DATA_AVAILABLE);
//                intent.putExtra(EXTRA_DATA, new String(" " + "Error"));
//                sendBroadcast(intent);
            }

        }
    }

    public void serverDisconnect() {
        try {
            socket.close();
            socket = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    class receive_thread1 extends Thread {
        private byte[] byte_data = new byte[600];
        private int size = 0;

        @Override
        public void run() {
            try {
                byte[] myvar1 = readBuf;
                int range = myvar1.length;
                int totalarray = (range / 100);
                ArrayList<byte[]> arrayList = new ArrayList();
                try {
                    for (int as = 0; as <= totalarray; as++) {
                        if (as == 0) {
                            int a = (as + 1) * 100;

                            arrayList.add(Arrays.copyOfRange(myvar1, 0, (as + 1) * 100 + 1));
                        } else if (as == totalarray) {
                            int b = as * 100 + 1;
                            arrayList.add(Arrays.copyOfRange(myvar1, as * 100 + 1, range));
                        } else {
                            int a = (as) * 100 + 1;
                            int b = (as + 1) * 100;
                            arrayList.add(Arrays.copyOfRange(myvar1, (as) * 100 + 1, (as + 1) * 100 + 1));
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
                int test = 1;
                for (int as = 0; as <= arrayList.size(); as++) {
                    Thread.sleep(150);
                     if (as == arrayList.size()) {
                        final Intent intent = new Intent(ACTION_DATA_AVAILABLE);
                        String result = getString(R.string.total)+" " + range;
                        intent.putExtra(EXTRA_DATA, "" + result);
                        sendBroadcast(intent);
                        Thread.sleep(1000);
                        sendRequestToServer();
                        String lat_long = dle.lat_lang;


                    }
                    byte[] val = arrayList.get(as);
                    long startTime = System.currentTimeMillis();
                    timer=""+startTime;
                    command = new String(val, 0, val.length);
                    bluetoothGattCharacteristic.setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT);
                    bluetoothGattCharacteristic.setValue(val);
                    boolean a = mBluetoothGatt.writeCharacteristic(bluetoothGattCharacteristic);
                    System.out.println("boolean " + a);


                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }




    class Connection_Thread extends Thread {
        private byte[] byte_data = new byte[600];
        private int size = 0;

        @Override
        public void run() {
            try {
                while (true) {


                    if (!isConnected) {
                        final BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(device_address);
                        mBluetoothGatt = device.connectGatt(context, false, mGattCallback);
                    }
                    Thread.sleep(2000);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


}






