package com.util;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.mychat.R;

/**
 * Created by Logan on 2017/1/30.
 */

public class MyToast extends Toast {
    /**
     * Construct an empty Toast object.  You must call {@link #setView} before you
     * can call {@link #show}.
     *
     * @param context The context to use.  Usually your {@link Application}
     * or {@link Activity} object.
     */
    private String info;
    private LayoutInflater inflater;

    public MyToast(Context context, String info) {
        super(context);
        this.info = info;
        inflater = LayoutInflater.from(context);
        init();
    }

    private void init() {
        View view = inflater.inflate(R.layout.lyt_toast, null);
        setView(view);
        TextView tv_msg = (TextView) view.findViewById(R.id.tv_msg);
        tv_msg.setText(info);
        setDuration(Toast.LENGTH_SHORT);
        show();
    }
}
