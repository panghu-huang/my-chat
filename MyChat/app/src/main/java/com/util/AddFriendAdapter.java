package com.util;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.entity.Record;
import com.mychat.App;
import com.mychat.R;
import com.view.CircleImageView;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

/**
 * Created by Logan on 2017/2/12.
 */

public class AddFriendAdapter extends BaseAdapter {
    private List<Record> records;
    private LayoutInflater inflater;
    private String username;
    private URL url = null;
    private Context context;

    public AddFriendAdapter(Context context, List<Record> records, String username) {
        this.records = records;
        this.username = username;
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return records.size();
    }

    @Override
    public Object getItem(int position) {
        return records.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Record record = records.get(getCount() - 1 - position);
        final ViewHolder viewHolder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.listview_item_addfriend, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.ivHead = (CircleImageView) convertView.findViewById(R.id.ivHead);
            viewHolder.tvMsg = (TextView) convertView.findViewById(R.id.tvMsg);
            viewHolder.tvResult = (TextView) convertView.findViewById(R.id.tvResult);
            viewHolder.tvName = (TextView) convertView.findViewById(R.id.tvName);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        switch (record.getResult()) {
            case "add":
                viewHolder.tvResult.setText("申请中");
                viewHolder.tvResult.setTextColor(Color.BLUE);
                break;
            case "accept":
                viewHolder.tvResult.setText("已接受");
                viewHolder.tvResult.setTextColor(Color.GREEN);
                break;
            default:
                viewHolder.tvResult.setText("已拒绝");
                viewHolder.tvResult.setTextColor(Color.RED);
                break;
        }
        if (record.getUsername().equals(username)) {
            viewHolder.tvMsg.setText("请求添加" + record.getTargetName() + "为好友");
            try {
                url = new URL(App.getInstance().getAddress() + record.getTargetImg());
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            viewHolder.tvName.setText(record.getTargetName());
        } else {
            viewHolder.tvMsg.setText(record.getName() + "请求添加我为好友");
            try {
                url = new URL(App.getInstance().getAddress() + record.getImg());
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            viewHolder.tvName.setText(record.getName());
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final Bitmap head = BitmapFactory.decodeStream(url.openStream());
                    ((Activity) context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            viewHolder.ivHead.setImageBitmap(head);
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
        return convertView;
    }

    private class ViewHolder {
        TextView tvMsg, tvResult, tvName;
        CircleImageView ivHead;
    }
}
