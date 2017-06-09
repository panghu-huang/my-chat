package com.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.entity.Message;
import com.entity.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Logan on 2017/3/8.
 */

public class DatabaseHelper extends SQLiteOpenHelper {

    private static String username = null;
    private SQLiteDatabase db;
    private static DatabaseHelper instance = null;

    public static DatabaseHelper getInstance(Context context, String name) {
        if (instance == null && !name.equals(username)) {
            synchronized (DatabaseHelper.class) {
                if (instance == null && !name.equals(username)) {
                    instance = new DatabaseHelper(context, name);
                }
            }
        }
        return instance;
    }

    private DatabaseHelper(Context context, String name) {
        super(context, "User.db", null, 1);
        username = name;
        db = getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // 创建消息表
        db.execSQL("create table if not exists message(" +
                "_id integer primary key autoincrement," +
                "username text not null," +
                "date text not null," +
                "sender text not null," +
                "receiver text not null," +
                "type text not null," +
                "message text not null)");

        // 创建用户表
        db.execSQL("create table if not exists user(" +
                "_id integer primary key autoincrement," +
                "username text not null," +
                "password text not null," +
                "name text not null," +
                "image text not null," +
                "friends text," +
                "phone text)");

        // 创建会话表
//        db.execSQL("create table if not exists conversation(" +
//                "_id integer primary key autoincrement," +
//                "sender text not null," +
//                "senderName text not null," +
//                "receiver text not null," +
//                "unReadCount integer not null," +
//                "time text not null," +
//                "lastMessage text not null," +
//                "type integer not null," +
//                "senderImg text not null)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }


    public void insertOrUpdateUser(User user) {
        ContentValues values = new ContentValues();
        values.put("username", user.getUsername());
        values.put("password", user.getPassword());
        values.put("name", user.getName());
        values.put("image", user.getImage());
        values.put("phone", user.getPhone());
        values.put("friends", user.getFriends());
        Cursor cursor = db.query("user", null, "username=?", new String[]{username}, null, null, null);
        if (cursor.moveToNext()) {
            db.update("user", values, "username=?", new String[]{username});
        } else {
            db.insert("message", null, values);
        }
    }

    public User getUser() {
        User user = new User();
        Cursor cursor = db.query("user", null, "username=?", new String[]{username}, null, null, null);
        user.setUsername(username);
        user.setPassword(cursor.getString(cursor.getColumnIndex("password")));
        user.setImage(cursor.getString(cursor.getColumnIndex("image")));
        user.setName(cursor.getString(cursor.getColumnIndex("name")));
        user.setPhone(cursor.getString(cursor.getColumnIndex("phone")));
        return user;
    }

    public void insertMessage(Message message) {
        ContentValues values = new ContentValues();
        values.put("message", message.getText());
        values.put("sender", message.getSender());
        values.put("receiver", message.getReceiver());
        values.put("date", message.getDate());
        values.put("type", message.getMessageType());
        db.insert("message", null, values);
    }

    public List<Message> getAllMessageByReceiver(String receiver) {
        Cursor cursor = db.query("message", null, "username=? and (sender=? or receiver=?)", new String[]{username, receiver, receiver}, "_id", null, "_id");
        List<Message> messages = new ArrayList<>();
        while (cursor.moveToNext()) {
            // 获取发送人
            String sender = cursor.getString(cursor.getColumnIndex("sender"));
            Message message = new Message();
            message.setText(cursor.getString(cursor.getColumnIndex("message")));
            if (sender.equals(receiver)) {
                message.setType(Message.Type.MESSAGE_FROM);
            } else {
                message.setType(Message.Type.MESSAGE_TO);
            }
            message.setMessageType(cursor.getInt(cursor.getColumnIndex("type")));
            message.setDate(cursor.getString(cursor.getColumnIndex("date")));
            messages.add(message);
        }
        cursor.close();
        return messages;
    }
}
