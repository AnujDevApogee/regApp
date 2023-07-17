package com.example.tcp_ble.utility;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.tcp_ble.Database.DatabaseOperation;
import com.example.tcp_ble.Database.RecyclerTouchlistner;
import com.example.tcp_ble.Database.RecyclerViewAdapter;
import com.example.tcp_ble.R;
import com.example.tcp_ble.model.BleModel;


import org.json.JSONObject;

import java.util.ArrayList;

public class SavedDeviceList extends AppCompatActivity {
    DatabaseOperation dbTask = new DatabaseOperation(this);
    ArrayList<String> myValues = new ArrayList<>();
    String val;
    String finalval;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_device_list);

        dbTask.open();
        ArrayList<String> tlist;
        tlist = dbTask.getsavedDeviceList();

        for (int k = 0; k < tlist.size(); k++) {
            String val = tlist.get(k);
            myValues.add(val);

        }
        dbTask.close();
        //Populate the ArrayList with your own values

        RecyclerViewAdapter adapter = new RecyclerViewAdapter(myValues);
        final RecyclerView myView = findViewById(R.id.recyclerview);
        myView.setHasFixedSize(true);
        myView.setAdapter(adapter);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        myView.setLayoutManager(llm);

        myView.addOnItemTouchListener(
                new RecyclerTouchlistner(getApplicationContext(), new RecyclerTouchlistner.OnItemClickListener() {
                    @Override public void onItemClick(View view, int position) {
                        // get position
                        int pos = position;

                        // check if item still exists
                        if(pos != RecyclerView.NO_POSITION){
                            finalval = myValues.get(pos);
                            Callingserver callingserver = new Callingserver();
                            callingserver.execute();
                         System.out.println(val);
                        }

                    }
                })
        );
    }


    private class Callingserver extends AsyncTask<String, String, String> {
        ProgressDialog dialog;
        @SuppressLint("WrongThread")
        @Override
        protected String doInBackground(String... params) {
            String result = "";

            try {
                BleModel dbmodel = new BleModel(SavedDeviceList.this);
                result = dbmodel.sendData(finalval);
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
                alertdialog(val);

            } else {
                dialog.dismiss();
                Toast.makeText(SavedDeviceList.this, getString(R.string.failed_fetching_data), Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected void onPreExecute() {
            dialog = ProgressDialog.show(SavedDeviceList.this, getString(R.string.fetching_data_from_server), getString(R.string.getting_id_password));
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
        AlertDialog.Builder builder1 = new AlertDialog.Builder(SavedDeviceList.this);
        builder1.setTitle(getString(R.string.data_getting_from_server));
        builder1.setMessage(data);
        builder1.setCancelable(true);

        builder1.setPositiveButton(
                getString(R.string.ok),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent i = new Intent(SavedDeviceList.this, DeviceScanActivity.class);
                        String strName = null;
                        i.putExtra("responsevalue", data);
                        startActivity(i);
                        dialog.cancel();
                    }
                });



        AlertDialog alert11 = builder1.create();
        alert11.show();
    }

}
