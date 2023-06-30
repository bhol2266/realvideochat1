package com.bhola.livevideochat;


import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class BotMessageService extends Service {
    @Override
    public void onCreate() {
        super.onCreate();
        // Service initialization code
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Service logic code
        return START_STICKY; // or other return values depending on your requirements
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null; // You can override this method if your service is bound
    }
}
