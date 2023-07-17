package com.example.tcp_ble;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.tcp_ble.model.DataSelection;
import com.example.tcp_ble.model.Home;
import com.example.tcp_ble.utility.BLEService;
import com.example.tcp_ble.utility.Constants;
import com.example.tcp_ble.utility.DeviceScanActivity;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.ACCESS_NETWORK_STATE;
import static android.Manifest.permission.CALL_PHONE;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.READ_PHONE_STATE;
import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.Manifest.permission_group.CAMERA;

public class MainActivity extends AppCompatActivity {

    public static final int RequestPermissionCode = 7;
    CardView cd;
    SharedPreferences sharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        cd = findViewById(R.id.csd);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        RequestMultiplePermission();

        CheckingPermissionIsEnabledOrNot();
        isNetworkConnectionAvailable();

        Intent intentService = new Intent(MainActivity.this, BLEService.class);
        startService(intentService);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);

        String userName = sharedPreferences.getString(Constants.NAME, null);

        cd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (userName!=null){
                    Log.d("TAG", "onClick: \n"+userName);
                    Intent intent = new Intent(MainActivity.this, DeviceScanActivity.class);
                    startActivity(intent);
                    finish();
                }else {
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });

    }

    public void checkNetworkConnection() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(R.drawable.noconnection);
        builder.setTitle(getString(R.string.no_internet_connetion));
        builder.setMessage(getString(R.string.please_turn_on_internet_connection_to_continue));
        builder.setNegativeButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public boolean isNetworkConnectionAvailable() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnected();
        if (isConnected) {
            Log.d("Network", "Connected");
            return true;
        } else {
            checkNetworkConnection();
            Log.d("Network", "Not Connected");
            return false;
        }
    }


    //Permission function starts from here
    private void RequestMultiplePermission() {

        // Creating String Array with Permissions.
        ActivityCompat.requestPermissions(MainActivity.this, new String[]
                {
                        READ_PHONE_STATE,
                        ACCESS_FINE_LOCATION,
                        READ_EXTERNAL_STORAGE,
                        WRITE_EXTERNAL_STORAGE,
                        CALL_PHONE,
                        ACCESS_COARSE_LOCATION,
                        CAMERA,
                        ACCESS_NETWORK_STATE,
                        RECORD_AUDIO

                }, RequestPermissionCode);

    }

    // Calling override method.
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {

            case RequestPermissionCode:

                if (grantResults.length > 0) {

                    boolean PhoneStatePermission = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean LocationPermission = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    boolean RExternalStoragePermission = grantResults[2] == PackageManager.PERMISSION_GRANTED;
                    boolean WExternalStoragePermission = grantResults[3] == PackageManager.PERMISSION_GRANTED;
                    boolean PhoneCallPermission = grantResults[4] == PackageManager.PERMISSION_GRANTED;
                    boolean Location2Permission = grantResults[5] == PackageManager.PERMISSION_GRANTED;
                    boolean CameraPermission = grantResults[6] == PackageManager.PERMISSION_GRANTED;
                    boolean ntwrkstatePermission = grantResults[7] == PackageManager.PERMISSION_GRANTED;
                    boolean recordaudioPermission = grantResults[8] == PackageManager.PERMISSION_GRANTED;

                    if (PhoneStatePermission && LocationPermission && RExternalStoragePermission && WExternalStoragePermission && PhoneCallPermission && Location2Permission && CameraPermission && ntwrkstatePermission && recordaudioPermission) {
                        Toast.makeText(MainActivity.this, getString(R.string.permission_granted), Toast.LENGTH_LONG).show();
                    } else {
                        //Toast.makeText(MainActivity.this,getString(R.string.permission_denied),Toast.LENGTH_LONG).show();

                    }
                }

                break;
        }
    }

    //  Checking permission is enabled or not using function starts from here.
    public boolean CheckingPermissionIsEnabledOrNot() {

        int FirstPermissionResult = ContextCompat.checkSelfPermission(getApplicationContext(), READ_PHONE_STATE);
        int SecondPermissionResult = ContextCompat.checkSelfPermission(getApplicationContext(), ACCESS_FINE_LOCATION);
        int ThirdPermissionResult = ContextCompat.checkSelfPermission(getApplicationContext(), READ_EXTERNAL_STORAGE);
        int ForthPermissionResult = ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE);
        int FifthPermissionResult = ContextCompat.checkSelfPermission(getApplicationContext(), CALL_PHONE);
        int SixthPermissionResult = ContextCompat.checkSelfPermission(getApplicationContext(), ACCESS_COARSE_LOCATION);
        int SeventhPermissionResult = ContextCompat.checkSelfPermission(getApplicationContext(), ACCESS_COARSE_LOCATION);
        int eightPermissionResult = ContextCompat.checkSelfPermission(getApplicationContext(), ACCESS_COARSE_LOCATION);
        int ninePermissionResult = ContextCompat.checkSelfPermission(getApplicationContext(), ACCESS_COARSE_LOCATION);

        return FirstPermissionResult == PackageManager.PERMISSION_GRANTED &&
                SecondPermissionResult == PackageManager.PERMISSION_GRANTED &&
                ThirdPermissionResult == PackageManager.PERMISSION_GRANTED &&
                ForthPermissionResult == PackageManager.PERMISSION_GRANTED &&
                FifthPermissionResult == PackageManager.PERMISSION_GRANTED &&
                SixthPermissionResult == PackageManager.PERMISSION_GRANTED &&
                SeventhPermissionResult == PackageManager.PERMISSION_GRANTED &&
                eightPermissionResult == PackageManager.PERMISSION_GRANTED &&
                ninePermissionResult == PackageManager.PERMISSION_GRANTED;
    }
}
