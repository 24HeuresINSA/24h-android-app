package com.insalyon.les24heures.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.insalyon.les24heures.R;
import com.insalyon.les24heures.model.LiveUpdate;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;


public class LiveUpdateAdapter extends BaseAdapter {

    private final LayoutInflater mInflater;
    private List<LiveUpdate> liveUpdates;

    public LiveUpdateAdapter(Context context, List<LiveUpdate> liveUpdates) {
        mInflater = LayoutInflater.from(context);
        this.liveUpdates=liveUpdates;
    }

    @Override
    public int getCount() {
        return liveUpdates.size();
    }

    @Override
    public Object getItem(int position) {
        return liveUpdates.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        ViewHolder holder;
        if(convertView == null) {
            view = mInflater.inflate(R.layout.live_update_list_item, parent, false);
            holder = new ViewHolder();
            holder.title = ButterKnife.findById(view, R.id.list_item_live_update_title_text);
            holder.message = ButterKnife.findById(view, R.id.list_item_live_update_message_text);
            view.setTag(holder);

        } else {
            view = convertView;
            holder = (ViewHolder)view.getTag();
        }

        LiveUpdate liveUpdate = liveUpdates.get(position);
        holder.title.setText(liveUpdate.getTitle());
        holder.message.setText(liveUpdate.getMessage());

        return view;
    }

    private class ViewHolder {
        public TextView title, message;
    }
}