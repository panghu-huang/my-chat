package com.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.HandlerThread;
import android.os.IBinder;
import android.widget.Toast;

import com.util.*;

/**
 * Created by Logan on 2017/1/7.
 */

public class ChatService extends Service {
    private ConnetionThread thread;

    @Override
    public void onCreate() {
        super.onCreate();
        thread = new ConnetionThread("mina", getApplicationContext());
        thread.start();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        thread.disConnection();
        thread = null;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    class ConnetionThread extends HandlerThread {

        private Context context;
        private boolean isConnection;
        private ConnectionManager manager;

        ConnetionThread(String name, Context context) {
            super(name);
            this.context = context;
            ConnectionConfig config = new ConnectionConfig.Builder(context)
                    .setIp("192.168.1.108")
                    .setPort(9898)
                    .setConnectionTimeout(10000)
                    .setReadBufferSize(10240).builder();
            manager = new ConnectionManager(config);
        }

        @Override
        protected void onLooperPrepared() {
            while (true) {
                isConnection = manager.connect();
                if (isConnection) {
                    break;
                }
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    Toast.makeText(context, "----------isConnection:" + isConnection, Toast.LENGTH_SHORT).show();
                }
            }
        }

        private void disConnection() {
            manager.disConnection();
        }
    }
}
