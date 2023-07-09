package com.bhola.livevideochat;


import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class Service_UserChats extends Service {

    private ArrayList<String> dataArrayList = new ArrayList<>();
    private RecyclerView.Adapter recyclerViewAdapter;

    public void addToArrayList(String data) {
        dataArrayList.add(data);
        updateRecyclerView();
    }

    public void updateRecyclerView() {
        if (recyclerViewAdapter != null) {
            recyclerViewAdapter.notifyDataSetChanged();
        }
    }

    public void setRecyclerViewAdapter(RecyclerView.Adapter adapter) {
        recyclerViewAdapter = adapter;
    }

    public ArrayList<String> getDataArrayList() {
        return dataArrayList;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}

