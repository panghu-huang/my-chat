package com.util;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.telephony.TelephonyManager;
import android.util.Log;

import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.serialization.ObjectSerializationCodecFactory;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.nio.NioSocketConnector;
import org.json.JSONArray;

import android.support.v4.content.LocalBroadcastManager;

import com.entity.Information;
import com.google.gson.Gson;
import com.mychat.App;
import com.mychat.LoginActivity;
import com.mychat.MainActivity;
import com.mychat.R;

import java.lang.ref.WeakReference;
import java.net.InetSocketAddress;
import java.util.List;

/**
 * Created by Logan on 2017/1/7.
 */

public class ConnectionManager {
    private ConnectionConfig mConfig;
    private WeakReference<Context> mContext;
    private NioSocketConnector mConnection;
    private InetSocketAddress mAddress;
    private IoSession mSession;

    public ConnectionManager(ConnectionConfig config) {
        this.mConfig = config;
        this.mContext = new WeakReference<>(mConfig.getContext());
        init();
    }

    private void init() {
        mAddress = new InetSocketAddress(mConfig.getIp(), mConfig.getPort());
        mConnection = new NioSocketConnector();
        mConnection.getSessionConfig().setReadBufferSize(mConfig.getReadBufferSize());
        mConnection.getFilterChain().addLast("logging", new LoggingFilter());
        mConnection.getFilterChain().addLast("codec", new ProtocolCodecFilter(new ObjectSerializationCodecFactory()));
        mConnection.setHandler(new ChatHandler(mContext.get()));
        mConnection.setDefaultRemoteAddress(mAddress);
    }

    public boolean connect() {
        try {
            ConnectFuture future = mConnection.connect();
            future.awaitUninterruptibly();
            mSession = future.getSession();
            SessionManager.getInstance().setSession(mSession);
        } catch (Exception e) {
            return false;
        }
        return mSession != null;
    }

    public void disConnection() {
        mConnection.dispose();
        mConnection = null;
        mSession = null;
        mAddress = null;
        mContext = null;
    }

    private static class ChatHandler extends IoHandlerAdapter {
        private Context context;
        private Gson gson = new Gson();
        private NotificationManager manager;
        private String username;

        ChatHandler(Context context) {
            this.context = context;
            gson = new Gson();
            manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        }

        @Override
        public void sessionOpened(IoSession session) throws Exception {
            username = context.getSharedPreferences("User", Context.MODE_PRIVATE)
                    .getString("username", null);
            final String IMEI = ((TelephonyManager)
                    context.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceId();
            // 向服务器发起登录
            Information info = new Information();
            info.setSender(username);
            info.setType(Information.PERSONAL);
            info.setExtra("login");
            info.setMessage(IMEI);
            SessionManager.getInstance().writeToServer(gson.toJson(info));
        }


        @Override
        public void messageReceived(IoSession session, Object message) throws Exception {
            Information information = gson.fromJson(message.toString(), Information.class);
            Log.e("information", information.toString());
            switch (information.getType()) {
                case Information.PERSONAL:
                    Intent logout = new Intent(context, LoginActivity.class);
                    logout.putExtra("showDialog", true);
                    logout.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(logout);
                    App.getInstance().exit();
                    return;
                case Information.SYSTEM:
                    if (information.getExtra().equals("hint")) {
                        // 系统操作提示消息，直接显示
                        new MyToast(context, information.getMessage());
                    } else if (information.getExtra().equals("unReadInformation")) {
                        // 处理未读消息,递归读取每条信息
                        JSONArray jsonArray = new JSONArray(information.getMessage());
                        for (int i = 0; i < jsonArray.length(); i++) {
                            messageReceived(session, jsonArray.get(i));
                        }
                    }
                    break;
                case Information.FRIEND:
                    String activityName = getRunningActivityName();
                    if (activityName.equals("com.mychat.AddFriendActivity")
                            || activityName.equals("com.mychat.NewFriendActivity")) {
                        // 在添加朋友页面或者新朋友页面，更新朋友申请记录
                        Intent intent = new Intent("com.mychat.AddFriendActivity");
                        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                    } else {
                        // 在在主页面，转发
                        Intent intent = new Intent("com.mychat.MainActivity");
                        intent.putExtra("message", message.toString());
                        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                    }
                    break;
                case Information.PRIVATE:
                    App.getInstance().setUnReadMsgCount(App.getInstance().getUnReadMsgCount() + 1);
                    Intent intent = new Intent("com.mychat.MainActivity");
                    intent.putExtra("message", message.toString());
                    LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                    break;
            }
            if (isBackground(context)) {
                //Push Notification
                showNotification(information);
            }
        }

        @Override
        public void sessionIdle(IoSession session, IdleStatus status) throws Exception {
            Log.e("sessionIdle", status.toString());
        }

        private boolean isBackground(Context context) {
            ActivityManager activityManager = (ActivityManager) context
                    .getSystemService(Context.ACTIVITY_SERVICE);
            List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager
                    .getRunningAppProcesses();
            for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
                if (appProcess.processName.equals(context.getPackageName())) {
                    return appProcess.importance != ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND;
                }
            }
            return false;
        }

        private String getRunningActivityName() {
            ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            return activityManager.getRunningTasks(1).get(0).topActivity.getClassName();
        }

        private void showNotification(Information info) {
            Intent intent = new Intent(context, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
            Notification.Builder builder = new Notification.Builder(context);
            builder.setSmallIcon(R.mipmap.ic_logo)
                    .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_logo))
                    .setTicker(info.getSenderName() + ":" + info.getMessage())
                    .setWhen(System.currentTimeMillis())
                    .setContentTitle(info.getSenderName())
                    .setContentText(info.getMessage())
                    .setContentIntent(pendingIntent)
                    .setDefaults(Notification.DEFAULT_SOUND)
                    .setAutoCancel(true);
            Notification notification = builder.build();
            manager.notify(0, notification);
        }
    }

}
