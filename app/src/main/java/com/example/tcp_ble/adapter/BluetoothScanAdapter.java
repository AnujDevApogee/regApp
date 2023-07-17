package com.example.tcp_ble.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tcp_ble.R;

import java.util.ArrayList;


public class BluetoothScanAdapter extends RecyclerView.Adapter<BluetoothScanAdapter.RecordViewHolder> {
    Context context;
    ArrayList<String> list;
    ClickListerner clickListerner = null;




    public interface ClickListerner {
        void onSuccess(String item);
    }

    public void setListerner( ClickListerner clickListerner) {
        this.clickListerner = clickListerner;
    }


    @Override
    public RecordViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_bluetooth_scan_adapter, viewGroup, false);
        return new RecordViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecordViewHolder holder, final int position) {
        final String item = list.get(position);

        String deviceName = item.split(",")[0];
        String deviceAddress = item.split(",")[1];
        holder.device_name.setText(deviceName);
        holder.device_address.setText(deviceAddress);


        holder.cv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(clickListerner != null){
                    clickListerner.onSuccess(item);
                }

            }
        });


    }




    @Override
    public int getItemCount() {
        if(list != null){
            return  list.size();
        } else{
            return  0;
        }


    }



    public void setAdapter(Context context, ArrayList<String> list){
        this.context = context;
        this.list = list;
        notifyDataSetChanged();
    }

    public static class RecordViewHolder extends RecyclerView.ViewHolder {

        TextView device_name,device_address;
        CardView cv;

        public RecordViewHolder(View view) {
            super(view);

            device_name = view.findViewById(R.id.device_name);
            device_address = view.findViewById(R.id.device_address);
            cv = view.findViewById(R.id.card_view);

        }

    }




}

