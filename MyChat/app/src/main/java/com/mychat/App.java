package com.mychat;

import android.app.Activity;
import android.app.Application;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Logan on 2017/2/3.
 */

public class App extends Application {
    private static App instance;
    private int unReadMsgCount = 0;
    private List<Activity> activities;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        activities = new ArrayList<>();
    }

    public void exit() {
        for (Activity activity : activities) {
            if (activity != null) {
                activity.finish();
            }
        }
    }

    public void addActivity(Activity activity) {
        activities.add(activity);
    }

    public void removeActivity() {
        activities.remove(activities.size() - 1);
    }

    public void setUnReadMsgCount(int unReadMsgCount) {
        this.unReadMsgCount = unReadMsgCount;
    }

    public int getUnReadMsgCount() {
        return unReadMsgCount;
    }

    public static App getInstance() {
        return instance;
    }

    public String getAddress() {
        return "http://192.168.1.108:8080/MyChat/";
    }

}
