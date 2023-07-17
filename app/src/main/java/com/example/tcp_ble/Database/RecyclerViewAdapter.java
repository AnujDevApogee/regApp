package com.example.tcp_ble.Database;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.tcp_ble.R;

import java.util.ArrayList;

public class RecyclerViewAdapter extends  RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder> {
    public ArrayList<String> myValues;
    public RecyclerViewAdapter (ArrayList<String> myValues){
        this.myValues= myValues;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View listItem = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardviewlayout, parent, false);
        return new MyViewHolder(listItem);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.mfnm.setText(myValues.get(position));
    }


    @Override
    public int getItemCount() {
        return myValues.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView mfnm;
        public MyViewHolder(View itemView) {
            super(itemView);
            mfnm = itemView.findViewById(R.id.text_cardview);
        }
    }
}
