package com.mychat;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.entity.User;
import com.google.gson.Gson;
import com.util.MyToast;
import com.view.CircleImageView;

import java.io.IOException;
import java.net.URL;

/**
 * Created by Logan on 2017/2/13.
 */

public class UserInfoActivity extends Activity {
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_userinfo);
        user = new Gson().fromJson(getIntent().getStringExtra("user"), User.class);
        ((TextView) findViewById(R.id.tv_title)).setText("好友详情");
        TextView tvName = (TextView) findViewById(R.id.tvName);
        tvName.setText(user.getName());
        tvName.setTypeface(Typeface.createFromAsset(getAssets(), "huawencaiyun.ttf"));
        TextView tvUserName = (TextView) findViewById(R.id.tvUsername);
        tvUserName.setText(user.getUsername());
        final CircleImageView ivHead = (CircleImageView) findViewById(R.id.ivHead);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final Bitmap bitmap = BitmapFactory.decodeStream(new URL(App.getInstance().getAddress() + user.getImage()).openStream());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ivHead.setImageBitmap(bitmap);
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void onBtnClick(View view) {
        switch (view.getId()) {
            case R.id.btnSend:
                Intent intent = new Intent(this, ChatActivity.class);
                intent.putExtra("receiver", user.getUsername());
                startActivity(intent);
                this.finish();
                break;
            case R.id.btnDelete:
                new MyToast(this, "btn send clicked");
                break;
        }
    }
}
