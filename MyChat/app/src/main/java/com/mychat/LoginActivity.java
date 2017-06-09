package com.mychat;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.service.ChatService;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;
import com.util.MyToast;
import com.util.RemoteLoginAlertDialog;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class LoginActivity extends Activity implements View.OnClickListener {
    private EditText etUsername, etPassword;
    private Tencent mTencent = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_login);
        mTencent = Tencent.createInstance("101378481", getApplicationContext());
        initViews();
        if (getIntent().getBooleanExtra("showDialog", false))
            new RemoteLoginAlertDialog(this);
    }

    private void initViews() {
        etUsername = (EditText) this.findViewById(R.id.etUsername);
        etPassword = (EditText) this.findViewById(R.id.etPassword);
        findViewById(R.id.btnLogin).setOnClickListener(this);
        findViewById(R.id.btnRegister).setOnClickListener(this);
        findViewById(R.id.btnLoginWidthQQ).setOnClickListener(this);
        TextView tvTitle = (TextView) findViewById(R.id.tvTitle);
        tvTitle.setTypeface(Typeface.createFromAsset(getAssets(), "GBYenRound.TTF"));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnLogin:
                String username = etUsername.getText().toString();
                String password = etPassword.getText().toString();
                if (username.equals("") || password.equals("")) {
                    Toast.makeText(this, "用户名和密码不为空！", Toast.LENGTH_SHORT).show();
                } else {
                    login(username, password);
                }
                break;
            case R.id.btnForgetPsw:
                break;
            case R.id.btnRegister:
                startActivity(new Intent(this, RegisterActivity.class));
                this.finish();
                break;
            case R.id.btnLoginWidthQQ:
                if (!mTencent.isSessionValid()) {
                    mTencent.login(this, "all", new IUiListener() {
                        @Override
                        public void onComplete(Object o) {
                            Log.e("tencent onComplete", "----22----");
                            new MyToast(LoginActivity.this, "onComplete");
                        }

                        @Override
                        public void onError(UiError uiError) {
                            Log.e("tencent", "onError");
                        }

                        @Override
                        public void onCancel() {
                            Log.e("tencent", "onCancel");
                        }
                    });
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        mTencent.onActivityResult(requestCode, resultCode, data);
    }

    private void login(final String username, final String password) {
        String url = App.getInstance().getAddress()
                + "LoginServlet?username="
                + username + "&password=" + password;
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().get().url(url).build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Looper.prepare();
                new MyToast(LoginActivity.this, "登录出错了，请稍后重试");
                Looper.loop();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string();
                switch (result) {
                    case "1":
                        Looper.prepare();
                        new MyToast(LoginActivity.this, "该用户不存在");
                        Looper.loop();
                        break;
                    case "2":
                        Looper.prepare();
                        new MyToast(LoginActivity.this, "密码错误");
                        Looper.loop();
                        break;
                    default:
                        SharedPreferences preferences = getSharedPreferences("User", MODE_PRIVATE);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString("username", username);
                        editor.putString("password", password);
                        editor.apply();
                        startService(new Intent(LoginActivity.this, ChatService.class));
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        LoginActivity.this.finish();
                        break;
                }
            }
        });
    }


}
