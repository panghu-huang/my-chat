package com.mychat;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Looper;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.entity.Information;
import com.entity.Record;
import com.entity.User;
import com.google.gson.Gson;
import com.util.AddFriendAdapter;
import com.util.MyToast;
import com.util.SessionManager;
import com.view.CircleImageView;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class AddFriendActivity extends Activity implements View.OnClickListener {
    private TextView etQuery;
    private String username;
    private OkHttpClient client;
    private Gson gson;
    private ListView lvAddFriend;
    private List<Record> records;
    private UpdateBroadCast receiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_addfriend);
        client = new OkHttpClient();
        gson = new Gson();
        records = new ArrayList<>();
        initViews();
        registerBroadCast();
        App.getInstance().addActivity(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getRecords();
    }

    // 初始化View
    private void initViews() {
        TextView title = (TextView) findViewById(R.id.tv_title);
        title.setText("添加朋友");
        findViewById(R.id.lyt_return).setOnClickListener(this);
        findViewById(R.id.btnQuery).setOnClickListener(this);
        lvAddFriend = (ListView) findViewById(R.id.lvAddFriend);
        etQuery = (EditText) findViewById(R.id.etQuery);
        // 获取用户名
        username = getSharedPreferences("User", MODE_PRIVATE).getString("username", null);
        lvAddFriend.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Record record = records.get(records.size() - 1 - position);
                Intent intent = new Intent(AddFriendActivity.this, NewFriendActivity.class);
                intent.putExtra("record", gson.toJson(record));
                startActivity(intent);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            // 返回上一级（ContactView）
            case R.id.lyt_return:
                this.finish();
                break;

            // 查询按钮点击事件
            case R.id.btnQuery:
                String keyword = etQuery.getText().toString();
                if (keyword.equals("")) {
                    new MyToast(this, "请输入要查询的账号");
                } else if (keyword.equals(username)) {
                    new MyToast(this, "你不能添加自己为好友");
                } else {
                    queryUser(keyword);
                }
                break;
        }


    }

    // 查找用户
    private void queryUser(String keyword) {
        String url = App.getInstance().getAddress()
                + "QueryUserServlet?keyword=" + keyword
                + "&username=" + username;
        Request request = new Request.Builder().get().url(url).build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                new MyToast(AddFriendActivity.this, "查询出错了");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string();
                if (result.equals("0")) {
                    Looper.prepare();
                    new MyToast(AddFriendActivity.this, "没有找到该用户");
                    Looper.loop();
                } else {
                    final User user = gson.fromJson(result, User.class);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            new Dialog(AddFriendActivity.this, user);
                        }
                    });
                }
            }
        });
    }

    public void getRecords() {
        String url = App.getInstance().getAddress() + "GetRecordServlet?username=" + username;
        Request request = new Request.Builder().get().url(url).build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Looper.prepare();
                new MyToast(AddFriendActivity.this, "获取好友申请失败");
                Looper.loop();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    JSONArray jsonArray = new JSONArray(response.body().string());
                    if (records.size() > 0) {
                        records.clear();
                    }
                    for (int i = 0; i < jsonArray.length(); i++) {
                        Record record = gson.fromJson(jsonArray.get(i).toString(), Record.class);
                        records.add(record);
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            lvAddFriend.setAdapter(new AddFriendAdapter(AddFriendActivity.this, records, username));
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    private class Dialog extends AlertDialog {
        private User user;
        private LayoutInflater inflater;

        public Dialog(Context context, User user) {
            super(context);
            inflater = LayoutInflater.from(context);
            this.user = user;
            init();
        }

        private void init() {
            View view = inflater.inflate(R.layout.af_dialog, null);
            setView(view);
            // ------显示用户信息
            TextView tvName = (TextView) view.findViewById(R.id.tvName);
            final CircleImageView ivHead = (CircleImageView) view.findViewById(R.id.ivHead);
            tvName.setText(user.getName());
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        URL url = new URL(App.getInstance().getAddress() + user.getImage());
                        final Bitmap bitmap = BitmapFactory.decodeStream(url.openStream());
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

            // 添加朋友
            EditText etDesc = (EditText) view.findViewById(R.id.etDesc);
            final String desc = etDesc.getText().toString();
            Button btnAddFriend = (Button) view.findViewById(R.id.btnAddFriend);
            btnAddFriend.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Record record = new Record();
                    record.setUsername(username);
                    record.setResult("add");
                    record.setTargetUsername(user.getName());
                    record.setDesc(desc);
                    Information info = new Information();
                    info.setSender(username);
                    info.setReceiver(user.getName());
                    info.setType(Information.FRIEND);
                    info.setMessage(gson.toJson(record));
                    SessionManager.getInstance().writeToServer(gson.toJson(info));
                    dismiss();
                }
            });

            // 判断是否为好友
            if (user.getPassword().equals("1")) {
                etDesc.setEnabled(false);
                btnAddFriend.setEnabled(false);
                btnAddFriend.setBackgroundColor(Color.GRAY);
                btnAddFriend.setText("你们已经是朋友了");
            }
            show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterBroadCast();
        App.getInstance().removeActivity();
    }

    private void registerBroadCast() {
        receiver = new UpdateBroadCast();
        IntentFilter filter = new IntentFilter("com.mychat.AddFriendActivity");
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, filter);
    }

    private void unregisterBroadCast() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
    }

    private class UpdateBroadCast extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            getRecords();
        }
    }
}
