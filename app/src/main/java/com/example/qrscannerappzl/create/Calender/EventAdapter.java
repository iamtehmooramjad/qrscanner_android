package com.example.qrscannerappzl.create.Calender;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.qrscannerappzl.R;

import java.util.ArrayList;

public class EventAdapter extends ArrayAdapter<EventModelClass> {
    ArrayList<EventModelClass> list = new ArrayList<>();

    public EventAdapter(Context context, int textViewResourceId, ArrayList<EventModelClass> objects) {
        super(context, textViewResourceId, objects);
        list = objects;
    }

    @Override
    public int getCount() {
        return super.getCount();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        v = inflater.inflate(R.layout.event_single_row, null);
        TextView name = (TextView) v.findViewById(R.id.event_name);
        TextView start_date = (TextView) v.findViewById(R.id.event_start_date);
        TextView end_date = (TextView) v.findViewById(R.id.event_end_date);

        name.setText(list.get(position).getName());
        start_date.setText(list.get(position).getStart_date());
        end_date.setText(list.get(position).getEnd_date());
        return v;

    }
}
