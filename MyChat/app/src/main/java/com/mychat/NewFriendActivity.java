package com.mychat;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.entity.Information;
import com.entity.Record;
import com.google.gson.Gson;
import com.util.MyToast;
import com.util.SessionManager;
import com.view.CircleImageView;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by Logan on 2017/2/5.
 */

public class NewFriendActivity extends Activity implements View.OnClickListener {
    private Gson gson;
    private Record record;
    private String username, target;
    private Bitmap head;
    private Button btnAccept, btnRefuse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_newfriend);
        App.getInstance().addActivity(this);
        gson = new Gson();
        record = gson.fromJson(getIntent().getStringExtra("record"), Record.class);
        username = getSharedPreferences("User", MODE_PRIVATE).getString("username", null);
        try {
            initViews();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    private void initViews() throws MalformedURLException {
        final URL url;
        TextView tvName = (TextView) findViewById(R.id.tvName);
        TextView tvDesc = (TextView) findViewById(R.id.tvDesc);
        final CircleImageView ivHead = (CircleImageView) findViewById(R.id.ivHead);
        btnAccept = (Button) findViewById(R.id.btnAccept);
        btnRefuse = (Button) findViewById(R.id.btnRefuse);
        // 设置显示数据
        tvDesc.setText(record.getDesc());
        if (username.equals(record.getUsername())) {
            // 我发起的添加请求
            target = record.getTargetUsername();
            tvName.setText(record.getTargetName());
            url = new URL(App.getInstance().getAddress() + record.getTargetImg());
            switch (record.getResult()) {
                case "add":
                    btnAccept.setText("等待对方接受");
                    btnRefuse.setVisibility(View.GONE);
                    break;
                case "accept":
                    btnAccept.setText("已接受你的添加请求");
                    btnRefuse.setVisibility(View.GONE);
                    break;
                case "refuse":
                    btnRefuse.setText("已拒绝你的添加请求");
                    btnAccept.setVisibility(View.GONE);
                    break;
            }
        } else {
            // 对方发起的添加请求
            target = record.getUsername();
            tvName.setText(record.getUsername());
            url = new URL(App.getInstance().getAddress() + record.getImg());
            switch (record.getResult()) {
                case "add":
                    btnAccept.setText("接受");
                    btnRefuse.setText("拒绝");
                    btnAccept.setOnClickListener(this);
                    btnRefuse.setOnClickListener(this);
                    break;
                case "accept":
                    btnAccept.setText("已接受对方的添加请求");
                    btnRefuse.setVisibility(View.GONE);
                    break;
                case "refuse":
                    btnRefuse.setText("已拒绝对方的添加请求");
                    btnAccept.setVisibility(View.GONE);
                    break;
            }
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    head = BitmapFactory.decodeStream(url.openStream());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ivHead.setImageBitmap(head);
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            this.finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btnAccept:
                Information info = new Information();
                info.setSender(username);
                info.setReceiver(target);
                info.setType(Information.FRIEND);
                record.setResult("accept");
                info.setMessage(gson.toJson(record));
                SessionManager.getInstance().writeToServer(gson.toJson(info));
                btnRefuse.setVisibility(View.GONE);
                btnAccept.setText("已接受对方的添加请求");
                new MyToast(this, "已接受对方的添加请求");
                break;
            case R.id.btnRefuse:
                Information info1 = new Information();
                info1.setSender(username);
                info1.setReceiver(target);
                info1.setType(Information.FRIEND);
                record.setResult("refuse");
                info1.setMessage(gson.toJson(record));
                SessionManager.getInstance().writeToServer(gson.toJson(info1));
                btnAccept.setVisibility(View.GONE);
                btnRefuse.setText("已拒绝对方的添加请求");
                new MyToast(this, "已拒绝对方的添加请求");
                break;
            case R.id.lyt_return:
                this.finish();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        App.getInstance().removeActivity();
    }
}
