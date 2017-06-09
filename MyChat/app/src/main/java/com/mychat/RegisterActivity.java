package com.mychat;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Looper;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;

import com.util.MyToast;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class RegisterActivity extends Activity implements View.OnClickListener {

    private EditText etUsername, etPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_register);
        initViews();
    }

    private void initViews() {
        etUsername = (EditText) this.findViewById(R.id.etUsername);
        etPassword = (EditText) this.findViewById(R.id.etPassword);
        this.findViewById(R.id.btnLogin).setOnClickListener(this);
        this.findViewById(R.id.btnRegister).setOnClickListener(this);
        TextView tvTitle = (TextView) findViewById(R.id.tvTitle);
        tvTitle.setTypeface(Typeface.createFromAsset(getAssets(), "GBYenRound.TTF"));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnRegister:
                String username = etUsername.getText().toString();
                String password = etPassword.getText().toString();
                if (username.equals("") || password.equals("")) {
                    new MyToast(this, "用户名和密码不为空！");
                } else {
                    register(username, password);
                }
                break;
            case R.id.btnLogin:
                startActivity(new Intent(this, LoginActivity.class));
                this.finish();
                break;
        }
    }

    private void register(String username, String password) {
        OkHttpClient client = new OkHttpClient();
        String url = App.getInstance().getAddress()
                + "RegisterServlet?username="
                + username + "&password="
                + password;
        Request request = new Request.Builder().get().url(url).build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Looper.prepare();
                new MyToast(RegisterActivity.this, "请求注册出错了，请稍后再试");
                Looper.loop();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string();
                Looper.prepare();
                switch (result) {
                    case "1":
                        new MyToast(RegisterActivity.this, "该用户已存在");
                        break;
                    case "error":
                        new MyToast(RegisterActivity.this, "注册出错，请稍后重试");
                        break;
                    default:
                        new MyToast(RegisterActivity.this, "注册成功，为您跳转至登录页面");
                        try {
                            Thread.sleep(1500);
                            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                            RegisterActivity.this.finish();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        break;
                }
                Looper.loop();
            }
        });
    }

}
