package com.example.qrscannerappzl.create.CustomeAdapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.qrscannerappzl.R;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class CustomeAdapter extends ArrayAdapter<ModelClass> {

    ArrayList<ModelClass> list = new ArrayList<>();

    public CustomeAdapter(Context context, int textViewResourceId, ArrayList<ModelClass> objects) {
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
        v = inflater.inflate(R.layout.item_view_for_custome_adapter, null);
        TextView text = (TextView) v.findViewById(R.id.text_customeadapter);
        CircleImageView imageView = (CircleImageView) v.findViewById(R.id.image_customeadapter);

        text.setText(list.get(position).getName());
        imageView.setImageDrawable(list.get(position).getImage());
        return v;

    }

}
