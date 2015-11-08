package com.example.mary.customlists;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;

import java.util.List;


public class MessageAdapter extends BaseAdapter implements ListAdapter {
    private List<Message> messages;
    private Context context;

    public MessageAdapter(Context context, List<Message> messages) {
        this.messages = messages;
        this.context = context;
    }

    @Override
    public int getCount() {
        return messages.size();
    }

    @Override
    public Object getItem(int position) {
        return messages.get(position);
    }

    @Override
    public long getItemId(int position) {
        return messages.get(position).getId();
    }

    //getView will return the formatting of a particular list item. We need to write some XML here.
    // Generate new android XML file in the res/layout folder (see list_message_item.xml)
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(R.layout.list_message_item, null);

        Message message = messages.get(position);
        String title = message.getTitle();
        String sender = message.getSender();

        TextView titleView = (TextView) view.findViewById(R.id.list_message_title);
        TextView senderView = (TextView) view.findViewById(R.id.list_message_sender);

        titleView.setText(title);
        senderView.setText(sender);

        return view;
    }
}
