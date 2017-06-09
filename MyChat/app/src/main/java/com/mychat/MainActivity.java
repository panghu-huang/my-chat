package com.mychat;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Looper;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.entity.Conversation;
import com.entity.Information;
import com.entity.User;
import com.google.gson.Gson;
import com.service.ChatService;
import com.util.ConversationAdapter;
import com.util.FriendAdapter;
import com.util.MyToast;
import com.util.SessionManager;
import com.view.CircleImageView;
import com.view.MyTabView;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends Activity implements View.OnClickListener {

    private View msgView, contactView, dongtaiView, setView;
    private ListView lvMessage, lvFriends;
    private List<View> views;
    private ImageView ivChat, ivContact, ivSet, ivDongtai;
    private MyTabView myTabView;
    private ViewPager viewPager;
    private String username;
    private MessageBroadCastReceiver receiver;
    private Gson gson = new Gson();
    private List<Conversation> conversations = null;
    private TextView tvUnReadMsgCount, tvUnReadContactCount;
    private SharedPreferences.Editor editor;
    private SharedPreferences preferences;
    private List<User> friends;
    private SQLiteDatabase db;
    private ConversationAdapter conversationAdapter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        App.getInstance().addActivity(this);
        preferences = getSharedPreferences("User", MODE_PRIVATE);
        username = preferences.getString("username", null);
        editor = preferences.edit();
        db = openOrCreateDatabase("User.db", MODE_PRIVATE, null);
        registerBroadCast();
        initViews();
        initMsgView();
        initContactView();
        initSetView();
        getFriendsData();
    }

    private void initViews() {
        //设置TITLE字体
        TextView title = (TextView) this.findViewById(R.id.tv_title);
        title.setTypeface(Typeface.createFromAsset(getAssets(), "GBYenRound.TTF"));
        //设置TabView
        findViewById(R.id.layoutChat).setOnClickListener(this);
        findViewById(R.id.layoutContact).setOnClickListener(this);
        findViewById(R.id.layoutDongtai).setOnClickListener(this);
        findViewById(R.id.layoutSet).setOnClickListener(this);
        ivChat = (ImageView) this.findViewById(R.id.ivChat);
        ivContact = (ImageView) this.findViewById(R.id.ivContact);
        ivDongtai = (ImageView) this.findViewById(R.id.ivDongtai);
        ivSet = (ImageView) this.findViewById(R.id.ivSet);
        myTabView = (MyTabView) this.findViewById(R.id.myTabView);
        // 朋友列表
        friends = new ArrayList<>();
        // 未读消息书目显示
        tvUnReadMsgCount = (TextView) findViewById(R.id.tvUnReadMsgCount);
        tvUnReadContactCount = (TextView) findViewById(R.id.tvUnReadContactCount);
        //设置ViewPager
        viewPager = (ViewPager) this.findViewById(R.id.viewPager);
        views = new ArrayList<>();
        LayoutInflater inflater = getLayoutInflater();
        msgView = inflater.inflate(R.layout.activity_msg, null);
        contactView = inflater.inflate(R.layout.activity_contact, null);
        setView = inflater.inflate(R.layout.activity_set, null);
        dongtaiView = inflater.inflate(R.layout.activity_dongtai, null);
        views.add(msgView);
        views.add(contactView);
        views.add(dongtaiView);
        views.add(setView);
        viewPager.setAdapter(new PagerAdapter() {
            @Override
            public int getCount() {
                return views.size();
            }

            @Override
            public boolean isViewFromObject(View view, Object object) {
                return view == object;
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                container.removeView(views.get(position));
            }

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                container.addView(views.get(position));
                return views.get(position);
            }
        });
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                myTabView.setClickTab(position, positionOffset);
            }

            @Override
            public void onPageSelected(int position) {
                setTab(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void initMsgView() {
        lvMessage = (ListView) msgView.findViewById(R.id.lv_message);
        lvMessage.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                List<Conversation> cons = new ArrayList<>();
                for (int i = 0; i < conversations.size(); i++) {
                    if (conversations.get(i).getType() == Information.PRIVATE)
                        cons.add(conversations.get(i));
                }
                Intent intent = new Intent(MainActivity.this, ChatActivity.class);
                intent.putExtra("receiver", cons.get(cons.size() - 1 - position).getSender());
                startActivity(intent);
            }
        });
    }

    private void initContactView() {
        contactView.findViewById(R.id.btn_addFriend).setOnClickListener(this);
        lvFriends = (ListView) contactView.findViewById(R.id.lvFriends);
    }

    private void initSetView() {
        final float scale = getResources().getDisplayMetrics().density;
        // 设置布局
        LinearLayout lytContent = (LinearLayout) setView.findViewById(R.id.lytContent);
        ImageView ivHeadBg = (ImageView) setView.findViewById(R.id.ivHeadBg);
        View view = setView.findViewById(R.id.view);
        CircleImageView ivHead = (CircleImageView) setView.findViewById(R.id.ivHead);
        //获取设备宽度
        int sceenWidth = this.getWindowManager().getDefaultDisplay().getWidth();
        ViewGroup.LayoutParams ivHeadBgParams = ivHeadBg.getLayoutParams();
        ivHeadBgParams.height = sceenWidth;
        FrameLayout.LayoutParams ivHeadParams = new FrameLayout.LayoutParams(ivHead.getLayoutParams());
        int width = ivHeadParams.width / 2;
        ivHeadParams.setMargins(sceenWidth / 2 - width, sceenWidth / 2 - width, sceenWidth / 2 - width, 0);
        ivHead.setLayoutParams(ivHeadParams);
        FrameLayout.LayoutParams viewParams = new FrameLayout.LayoutParams(view.getLayoutParams());
        viewParams.setMargins(0, sceenWidth / 2, 0, 0);
        view.setLayoutParams(viewParams);
        FrameLayout.LayoutParams lytContentParams = new FrameLayout.LayoutParams(lytContent.getLayoutParams());
        lytContentParams.setMargins(0, sceenWidth / 2 + viewParams.height, 0, 0);
        lytContent.setLayoutParams(lytContentParams);

        //添加监听器
        setView.findViewById(R.id.lytSecurity).setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (conversations == null) {
            conversations = new ArrayList<>();
        } else {
            conversations.clear();
        }
        try {
            JSONArray jsonArray = new JSONArray(preferences.getString(username, "[]"));
            for (int i = 0; i < jsonArray.length(); i++) {
                Conversation conversation = gson.fromJson(jsonArray.get(i).toString(), Conversation.class);
                conversations.add(conversation);
                Log.e("conversation", conversation.toString());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        int unReadMsgCount = 0, unReadContactCount = 0;
        for (int i = 0; i < conversations.size(); i++) {
            if (conversations.get(i).getType() == Information.FRIEND) {
                unReadContactCount += conversations.get(i).getUnReadCount();
            } else {
                unReadMsgCount += conversations.get(i).getUnReadCount();
            }
        }
        if (unReadContactCount != 0) {
            tvUnReadContactCount.setText(unReadContactCount + "");
            tvUnReadContactCount.setVisibility(View.VISIBLE);
        } else {
            tvUnReadContactCount.setText(null);
            tvUnReadContactCount.setVisibility(View.GONE);
        }

        if (unReadMsgCount != 0) {
            tvUnReadMsgCount.setText(unReadMsgCount + "");
            tvUnReadMsgCount.setVisibility(View.VISIBLE);
        } else {
            tvUnReadMsgCount.setText(null);
            tvUnReadMsgCount.setVisibility(View.GONE);
        }
        conversationAdapter = null;
        conversationAdapter = new ConversationAdapter(this, conversations);
        lvMessage.setAdapter(conversationAdapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.layoutChat:
                setTab(0);
                viewPager.setCurrentItem(0);
                break;
            case R.id.layoutContact:
                setTab(1);
                viewPager.setCurrentItem(1);
                break;
            case R.id.layoutDongtai:
                setTab(2);
                viewPager.setCurrentItem(2);
                break;
            case R.id.layoutSet:
                setTab(3);
                viewPager.setCurrentItem(3);
                break;

            // ContactView 添加朋友按钮
            case R.id.btn_addFriend:
                if (tvUnReadContactCount.getText() != null) {
                    tvUnReadContactCount.setText(null);
                    tvUnReadContactCount.setVisibility(View.GONE);
                    for (int i = 0; i < conversations.size(); i++) {
                        if (conversations.get(i).getType() == Information.FRIEND) {
                            conversations.remove(i);
                            editor.putString(username, gson.toJson(conversations));
                            editor.apply();
                        }
                    }
                }
                startActivity(new Intent(this, AddFriendActivity.class));
                break;
            case R.id.lytSecurity:
                startActivity(new Intent(this, SecurityActivity.class));
                break;
        }
    }

    private void setTab(int position) {
        ivChat.setBackgroundResource(R.mipmap.ic_chat_gray);
        ivSet.setBackgroundResource(R.mipmap.ic_set_gray);
        ivDongtai.setBackgroundResource(R.mipmap.ic_dongtai_gray);
        ivContact.setBackgroundResource(R.mipmap.ic_contact_gray);
        switch (position) {
            case 0:
                myTabView.setClickTab(0, 0);
                ivChat.setBackgroundResource(R.mipmap.ic_chat);
                break;
            case 1:
                myTabView.setClickTab(1, 0);
                ivContact.setBackgroundResource(R.mipmap.ic_contact);
                break;
            case 2:
                myTabView.setClickTab(2, 0);
                ivDongtai.setBackgroundResource(R.mipmap.ic_dongtai);
                break;
            case 3:
                myTabView.setClickTab(3, 0);
                ivSet.setBackgroundResource(R.mipmap.ic_set);
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            startActivity(new Intent(this, LoginActivity.class));
            this.finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    private class MessageBroadCastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Information information = gson.fromJson(intent.getStringExtra("message"), Information.class);
            Log.e("onReceive information", information.toString());
            if (information.getType() == Information.FRIEND) {
                if (tvUnReadContactCount.getText().equals("")) {
                    tvUnReadContactCount.setVisibility(View.VISIBLE);
                    tvUnReadContactCount.setText(1 + "");
                } else {
                    tvUnReadContactCount.setText(Integer.parseInt(tvUnReadContactCount.getText().toString()) + 1 + "");
                }
            } else if (information.getType() == Information.PRIVATE) {
                if (tvUnReadMsgCount.getText().equals("")) {
                    tvUnReadMsgCount.setVisibility(View.VISIBLE);
                    tvUnReadMsgCount.setText(1 + "");
                } else {
                    tvUnReadMsgCount.setText(Integer.parseInt(tvUnReadMsgCount.getText().toString()) + 1 + "");
                }
                ContentValues value = new ContentValues();
                value.put("message", information.getMessage());
                value.put("sender", information.getSender());
                value.put("receiver", information.getReceiver());
                value.put("date", information.getTime());
                db.insert("message", null, value);
            }

            int index = -1;
            Conversation conversation = new Conversation();
            conversation.setSender(information.getSender());
            conversation.setLastMessage(information.getMessage());
            conversation.setType(information.getType());
            conversation.setReceiver(information.getReceiver());
            conversation.setTime(information.getTime());
            conversation.setSenderImg(information.getSenderImg());
            conversation.setSenderName(information.getSenderName());
            conversation.setUnReadCount(1);
            Log.e("onReceive conversation", conversation.toString());
            for (int i = 0; i < conversations.size(); i++) {
                Conversation con = conversations.get(i);
                Log.e("onReceive", "for i");
                if (conversation.getType() == con.getType()
                        && con.getType() == Information.FRIEND) {
                    index = i;
                } else if (con.getType() == conversation.getType()
                        && con.getType() == Information.PRIVATE) {
                    // 个人消息
                    for (int j = 0; j < conversations.size(); j++) {
                        if (conversation.getSender().equals(conversations.get(j).getSender())) {
                            index = j;
                        }
                    }
                }
            }

            if (index != -1) {
                conversation.setUnReadCount(conversations.get(index).getUnReadCount() + 1);
                conversations.remove(index);
            }
            conversations.add(conversation);
            editor.putString(username, gson.toJson(conversations));
            editor.apply();
            conversationAdapter = null;
            conversationAdapter = new ConversationAdapter(MainActivity.this, conversations);
            lvMessage.setAdapter(conversationAdapter);
        }
    }

    public void getFriendsData() {
        String url = App.getInstance().getAddress()
                + "GetFriendServlet?username=" + username;
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().get().url(url).build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Looper.prepare();
                new MyToast(MainActivity.this, "获取朋友列表出错");
                Looper.loop();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    JSONArray array = new JSONArray(response.body().string());
                    if (friends.size() != 0)
                        friends.clear();
                    for (int i = 0; i < array.length(); i++) {
                        friends.add(gson.fromJson(array.get(i).toString(), User.class));
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                updateFriends();
                            }
                        });
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void updateFriends() {
        lvFriends.setAdapter(new FriendAdapter(this, friends));
        lvFriends.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, UserInfoActivity.class);
                intent.putExtra("user", gson.toJson(friends.get(position)));
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        final String IMEI = ((TelephonyManager)
                getSystemService(TELEPHONY_SERVICE)).getDeviceId();
        Information info = new Information();
        info.setSender(username);
        info.setType(Information.PERSONAL);
        info.setExtra("logout");
        info.setMessage(IMEI);
        SessionManager.getInstance().writeToServer(gson.toJson(info));
        SessionManager.getInstance().closeSession();
        SessionManager.getInstance().removeSession();
        db.close();
        unregisterBroadCast();
        stopService(new Intent(this, ChatService.class));
        App.getInstance().removeActivity();
    }

    private void registerBroadCast() {
        receiver = new MessageBroadCastReceiver();
        IntentFilter filter = new IntentFilter("com.mychat.MainActivity");
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, filter);
    }

    private void unregisterBroadCast() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
    }

}
