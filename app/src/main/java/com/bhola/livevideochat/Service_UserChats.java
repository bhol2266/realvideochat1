package com.bhola.livevideochat;


import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import java.lang.reflect.Type;
import java.util.ArrayList;


public class Service_UserChats extends Service {
    private Handler handler;
    ArrayList<ChatItem_ModelClass> userListTemp;

    @Override
    public void onCreate() {
        super.onCreate();
        handler = new Handler();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Retrieve data from the intent

        getIntentData(intent);

        // Use the handler to post a delayed task
        handler.postDelayed(() -> {
            // Create an intent for the broadcast message
            Intent broadcastIntent = new Intent("my_broadcast_action");

            // Add data to the intent if needed
            broadcastIntent.putExtra("result", "data");

            // Send the broadcast
            sendBroadcast(broadcastIntent);

            // Stop the service
            stopSelf();
        }, 3000); // 3 seconds delay

        return START_NOT_STICKY;
    }

    private void getIntentData(Intent intent) {
        String json_userList = intent.getStringExtra("userListAll");
// Convert the JSON string to ArrayList<ChatItem_ModelClass>
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<ChatItem_ModelClass>>() {
        }.getType();
        userListTemp = gson.fromJson(json_userList, type);

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
