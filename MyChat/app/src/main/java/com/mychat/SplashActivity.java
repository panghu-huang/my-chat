package com.mychat;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.TextView;

import com.service.ChatService;

public class SplashActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        TextView tvTitle = (TextView) findViewById(R.id.tvTitle);
        tvTitle.setTypeface(Typeface.createFromAsset(getAssets(), "GBYenRound.TTF"));
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2500);
                    SharedPreferences preferences = getSharedPreferences("User", MODE_PRIVATE);
                    Boolean isFirstOpen = preferences.getBoolean("isFirstOpen", true);
                    String username = preferences.getString("username", null);
                    if (isFirstOpen) {
                        //第一次打开App
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putBoolean("isFirstOpen", false);
                        editor.apply();
                        startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                    } else {
                        if (username != null) {
                            startService(new Intent(SplashActivity.this, ChatService.class));
                            startActivity(new Intent(SplashActivity.this, MainActivity.class));
                        } else {
                            startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                        }
                    }
                    SplashActivity.this.finish();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
