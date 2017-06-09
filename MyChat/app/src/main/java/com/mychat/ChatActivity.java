package com.mychat;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Looper;
import android.support.v4.content.LocalBroadcastManager;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.entity.Conversation;
import com.entity.Information;
import com.entity.Message;
import com.entity.Record;
import com.google.gson.Gson;
import com.util.DateUtil;
import com.util.MessageAdapter;
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

public class ChatActivity extends Activity implements View.OnClickListener {

    private EditText text;
    private List<Message> list;
    private MessageAdapter adapter;
    private ListView content;
    private SQLiteDatabase db = null;
    private String SENDER, RECEIVER;
    private String SENDERNAME, RECEIVERNAME;
    private String SENDERIMG, RECEIVERIMG;
    private MessageBroadCastReceiver receiver;
    private Gson gson = new Gson();
    private TextView tvName;
    private CircleImageView ivHead;
    private List<Conversation> conversations;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private Conversation conversation = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_chat);
        initView();
        registerBroadCast();
        getUserInfo();
    }

    @Override
    protected void onResume() {
        super.onResume();
        boolean isExist = false;
        if (conversations == null) {
            conversations = new ArrayList<>();
        } else {
            conversations.clear();
        }
        try {
            JSONArray jsonArray = new JSONArray(preferences.getString(SENDER, "[]"));
            for (int i = 0; i < jsonArray.length(); i++) {
                Conversation conversation = gson.fromJson(jsonArray.get(i).toString(), Conversation.class);
                if (conversation.getSender().equals(RECEIVER)) {
                    conversation.setUnReadCount(0);
                    this.conversation = conversation;
                    isExist = true;
                }
                conversations.add(conversation);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (!isExist) {
            conversation = new Conversation();
            conversation.setSender(RECEIVER);
            conversation.setSenderImg(RECEIVERIMG);
            conversation.setSenderName(RECEIVERNAME);
            conversation.setType(Information.PRIVATE);
            conversation.setReceiver(SENDER);
            conversation.setUnReadCount(0);
        }
    }

    private void getUserInfo() {
        String url = App.getInstance().getAddress() +
                "GetUserInfoServlet?sender=" + SENDER
                + "&receiver=" + RECEIVER;
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().get().url(url).build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Looper.prepare();
                new MyToast(ChatActivity.this, "获取用户信息失败");
                Looper.loop();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final Record record = gson.fromJson(response.body().string(), Record.class);
                SENDERNAME = record.getName();
                SENDERIMG = record.getImg();
                RECEIVERNAME = record.getTargetName();
                RECEIVERIMG = record.getTargetImg();
                final Bitmap head = BitmapFactory.decodeStream(new URL(App.getInstance().getAddress() + record.getTargetImg()).openStream());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ivHead.setImageBitmap(head);
                        tvName.setText(record.getTargetName());
                    }
                });
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterBroadCast();
    }

    private void initView() {
        // 初始化sender and receiver
        preferences = getSharedPreferences("User", MODE_PRIVATE);
        editor = preferences.edit();
        SENDER = preferences.getString("username", null);
        RECEIVER = getIntent().getStringExtra("receiver");
        text = (EditText) this.findViewById(R.id.text);
        content = (ListView) this.findViewById(R.id.content);
        tvName = (TextView) findViewById(R.id.tvName);
        ivHead = (CircleImageView) findViewById(R.id.ivHead);
        this.findViewById(R.id.send).setOnClickListener(this);
        list = new ArrayList<>();
        //读取聊天记录
        db = openOrCreateDatabase("User.db", MODE_PRIVATE, null);
        Cursor cursor = db.query("message", null, "sender=? or receiver=?", new String[]{RECEIVER, RECEIVER}, "_id", null, "_id");
        while (cursor.moveToNext()) {
            String sender = cursor.getString(cursor.getColumnIndex("sender"));
            Message message = new Message();
            message.setText(cursor.getString(cursor.getColumnIndex("message")));
            if (sender.equals(RECEIVER)) {
                message.setType(Message.Type.MESSAGE_FROM);
            } else {
                message.setType(Message.Type.MESSAGE_TO);
            }
            list.add(message);
        }
        cursor.close();
        adapter = new MessageAdapter(this, list);
        content.setAdapter(adapter);
        content.setSelection(content.getAdapter().getCount() - 1);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.send:
                if (text.getText().toString().equals("")) {
                    new MyToast(this, "不能发送空信息");
                    return;
                }
                Information info = new Information();
                info.setType(Information.PRIVATE);
                info.setReceiver(RECEIVER);
                info.setSender(SENDER);
                info.setSenderName(SENDERNAME);
                info.setSenderImg(SENDERIMG);
                info.setMessage(text.getText().toString());
                info.setExtra("text");
                conversation.setTime(DateUtil.getDate());
                conversation.setLastMessage(text.getText().toString());
                conversation.setSenderImg(RECEIVERIMG);
                conversation.setSenderName(RECEIVERNAME);
                SessionManager.getInstance().writeToServer(gson.toJson(info));
                Message message = new Message();
                message.setSender(SENDER);
                message.setReceiver(RECEIVER);
                message.setDate(DateUtil.getDate());
                message.setMessageType(Message.TEXT);
                message.setText(text.getText().toString());
                message.setType(Message.Type.MESSAGE_TO);
                list.add(message);
                adapter.notifyDataSetChanged();
                content.setSelection(content.getAdapter().getCount() - 1);
                ContentValues values = new ContentValues();
                values.put("message", text.getText().toString());
                values.put("sender", SENDER);
                values.put("receiver", RECEIVER);
                values.put("date", DateUtil.getDate());
                db.insert("message", null, values);
                text.setText("");
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            for (int i = 0; i < conversations.size(); i++) {
                if (conversations.get(i).getSender().equals(RECEIVER)) {
                    conversations.remove(i);
                }
            }
            conversations.add(conversation);
            editor.putString(SENDER, gson.toJson(conversations));
            editor.apply();
            this.finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    private void registerBroadCast() {
        receiver = new MessageBroadCastReceiver();
        IntentFilter filter = new IntentFilter("com.mychat.MainActivity");
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, filter);
    }

    private void unregisterBroadCast() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
    }

    private class MessageBroadCastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Information info = gson.fromJson(intent.getStringExtra("message"), Information.class);
            if (info.getSender().equals(RECEIVER) && info.getType() == Information.PRIVATE) {
                Message message = new Message();
                message.setText(info.getMessage());
                message.setType(Message.Type.MESSAGE_FROM);
                list.add(message);

                conversation.setTime(DateUtil.getDate());
                conversation.setLastMessage(info.getMessage());
                conversation.setSenderImg(RECEIVERIMG);
                conversation.setSenderName(RECEIVERNAME);

                ContentValues value = new ContentValues();
                value.put("message", message.getText());
                value.put("sender", RECEIVER);
                value.put("receiver", SENDER);
                value.put("date", DateUtil.getDate());
                db.insert("message", null, value);
                adapter.notifyDataSetChanged();
                content.setSelection(content.getAdapter().getCount() - 1);
            }
        }
    }
}
