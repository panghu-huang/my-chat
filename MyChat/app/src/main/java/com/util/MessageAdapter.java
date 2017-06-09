package com.util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.entity.Message;
import com.mychat.R;

import java.util.List;

/**
 * Created by Logan on 2016/10/20.
 */

public class MessageAdapter extends BaseAdapter {
    private List<Message> list;
    private LayoutInflater inflater;

    public MessageAdapter(Context context, List<Message> list) {
        this.list = list;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        if (list.get(position).getType() == Message.Type.MESSAGE_FROM) {
            return 0;
        } else {
            return 1;
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Message message = list.get(position);
        ViewHolder viewHolder;
        if (convertView == null) {
            if (getItemViewType(position) == 0) {
                convertView = inflater.inflate(R.layout.message_from, parent, false);
                viewHolder = new ViewHolder();
                viewHolder.textView = (TextView) convertView.findViewById(R.id.message_from);
            } else {
                convertView = inflater.inflate(R.layout.message_to, parent, false);
                viewHolder = new ViewHolder();
                viewHolder.textView = (TextView) convertView.findViewById(R.id.message_to);
            }
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.textView.setText(message.getText());
        return convertView;
    }

    private class ViewHolder {
        private TextView textView;
    }
}
