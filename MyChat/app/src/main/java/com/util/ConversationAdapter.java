package com.util;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.entity.Conversation;
import com.entity.Information;
import com.mychat.App;
import com.mychat.R;
import com.view.CircleImageView;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Logan on 2017/2/15.
 */

public class ConversationAdapter extends BaseAdapter {
    private Context context;
    private List<Conversation> conversations = null;
    private LayoutInflater inflater;

    public ConversationAdapter(Context context, List<Conversation> conversations) {
        notifyDataSetChanged();
        this.context = context;
        inflater = LayoutInflater.from(context);
        if (this.conversations == null) {
            this.conversations = new ArrayList<>();
        } else {
            this.conversations.clear();
        }
        this.conversations = new ArrayList<>();
        for (int i = 0; i < conversations.size(); i++) {
            if (conversations.get(i).getType() == Information.PRIVATE) {
                this.conversations.add(conversations.get(i));
                Log.e("Adapter", conversations.get(i).toString());
            }
        }

    }

    @Override
    public int getCount() {
        return conversations.size();
    }

    @Override
    public Object getItem(int position) {
        return conversations.get(getCount() - 1 - position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Conversation conversation = conversations.get(getCount() - 1 - position);
        final ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = inflater.inflate(R.layout.listview_item_message, parent, false);
            viewHolder.tvCount = (TextView) convertView.findViewById(R.id.tvUnReadCount);
            viewHolder.ivHead = (CircleImageView) convertView.findViewById(R.id.ivHead);
            viewHolder.tvSender = (TextView) convertView.findViewById(R.id.tvSender);
            viewHolder.tvTime = (TextView) convertView.findViewById(R.id.tvTime);
            viewHolder.tvMessage = (TextView) convertView.findViewById(R.id.tvMessage);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.tvSender.setText(conversation.getSenderName());
        viewHolder.tvTime.setText(conversation.getTime());
        viewHolder.tvMessage.setText(conversation.getLastMessage());
        if (conversation.getUnReadCount() != 0) {
            viewHolder.tvCount.setText(conversation.getUnReadCount() + "");
            viewHolder.tvCount.setVisibility(View.VISIBLE);
        } else {
            viewHolder.tvCount.setVisibility(View.GONE);
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL(App.getInstance().getAddress() + conversation.getSenderImg());
                    final Bitmap bitmap = BitmapFactory.decodeStream(url.openStream());
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
        return convertView;
    }

    private class ViewHolder {
        TextView tvSender, tvTime, tvMessage, tvCount;
        CircleImageView ivHead;
    }
}
