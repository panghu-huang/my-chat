package com.util;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.entity.User;
import com.mychat.App;
import com.mychat.R;
import com.view.CircleImageView;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

/**
 * Created by Logan on 2017/2/13.
 */

public class FriendAdapter extends BaseAdapter {
    private Context context;
    private List<User> users;
    private LayoutInflater inflater;
    private Bitmap bitmap;

    public FriendAdapter(Context context, List<User> users) {
        this.context = context;
        this.users = users;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return users.size();
    }

    @Override
    public Object getItem(int position) {
        return users.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = inflater.inflate(R.layout.listview_item_friends, parent, false);
            viewHolder.ivHead = (CircleImageView) convertView.findViewById(R.id.ivHead);
            viewHolder.tvName = (TextView) convertView.findViewById(R.id.tvName);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.tvName.setText(users.get(position).getName());
        try {
            final URL url = new URL(App.getInstance().getAddress() + users.get(position).getImage());
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        bitmap = BitmapFactory.decodeStream(url.openStream());
                        ((Activity) context).runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                viewHolder.ivHead.setImageBitmap(bitmap);
                            }
                        });
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return convertView;
    }

    private class ViewHolder {
        TextView tvName;
        CircleImageView ivHead;
    }
}
