package com.mychat;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import com.util.MyToast;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Logan on 2017/2/16.
 */

public class SecurityActivity extends Activity {
    private String password;
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_security);
        App.getInstance().addActivity(this);
        password = getSharedPreferences("User", MODE_PRIVATE).getString("password", null);
        username = getSharedPreferences("User", MODE_PRIVATE).getString("username", null);
        new SecurityDialog(this);
//        getUserPhoneNumber();
    }

    private void getUserPhoneNumber() {
        String url = App.getInstance().getAddress() + "GetUserPhoneNumberServlet?username=" + username;
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(url).get().build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            this.finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    private class SecurityDialog extends AlertDialog.Builder {
        SecurityDialog(final Context context) {
            super(context);
            LayoutInflater inflater = LayoutInflater.from(context);
            setCancelable(false);
            View view = inflater.inflate(R.layout.security_dialog, null);
            Button btnCancel = (Button) view.findViewById(R.id.btnCancel);
            Button btnConfirm = (Button) view.findViewById(R.id.btnConfirm);
            final EditText etPassword = (EditText) view.findViewById(R.id.etPassword);
            btnCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SecurityActivity.this.finish();
                }
            });
            btnConfirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String psw = etPassword.getText().toString();
                    if (psw.equals(password)) {
                        create().dismiss();
                    } else {
                        new MyToast(context, "密码错误");
                    }
                }
            });
            create().show();
        }
    }
}
