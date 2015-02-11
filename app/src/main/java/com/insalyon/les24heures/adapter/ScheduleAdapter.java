package com.insalyon.les24heures.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.insalyon.les24heures.R;
import com.insalyon.les24heures.model.Schedule;

import java.util.ArrayList;

/**
 * Created by remi on 09/02/15.
 */
public class ScheduleAdapter extends ArrayAdapter<Schedule> {

    private final LayoutInflater vi;
    ArrayList<Schedule> schedules;
    int schedule_grid_item;


    public ScheduleAdapter(Context applicationContext, int schedule_grid_item, ArrayList<Schedule> schedules) {
        super(applicationContext,schedule_grid_item,schedules);
        this.schedules = schedules;
        this.schedule_grid_item = schedule_grid_item;

        this.vi = (LayoutInflater) applicationContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    private class ViewHolder{
        TextView day;
        TextView hours;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if(convertView == null){
            convertView = vi.inflate(schedule_grid_item,null);

            holder = new ViewHolder();

            holder.day = (TextView) convertView.findViewById(R.id.schedule_item_day);
            holder.hours = (TextView) convertView.findViewById(R.id.schedule_item_hours);

            convertView.setTag(holder);
        }else {
            holder = (ViewHolder) convertView.getTag();
        }

        Schedule schedule = schedules.get(position);

        holder.day.setText(schedule.toString());

        return convertView;
    }



}
