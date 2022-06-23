package com.example.qrscannerappzl.create;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.qrscannerappzl.R;
import com.example.qrscannerappzl.util.CheckSettingPrefernces;

import java.util.ArrayList;

public class QrAdapter extends RecyclerView.Adapter<QrAdapter.MyViewHolder> {

    Context context;
    ArrayList<QR> qrlist=new ArrayList<>();
    OnItemClickListener mListener;
    public interface OnItemClickListener{
        void onItemClick(int pos);
    }
    public void setClickListener(OnItemClickListener listener){
        this.mListener=listener;
    }
    public QrAdapter(Context context, ArrayList<QR> list) {
        this.context = context;
        this.qrlist = list;
    }

    @NonNull
    @Override
    public QrAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater=LayoutInflater.from(parent.getContext());
        View v=inflater.inflate(R.layout.single_row_for_createqr,parent,false);
        return  new MyViewHolder(v,mListener);
    }


    @Override
    public void onBindViewHolder(@NonNull QrAdapter.MyViewHolder holder, int position) {
        holder.image.setImageResource(qrlist.get(position).getImage());
        holder.text.setText(qrlist.get(position).getText());
        if (!CheckSettingPrefernces.getMode(context)){
            holder.image.setColorFilter(ContextCompat.getColor(context, R.color.blue), android.graphics.PorterDuff.Mode.SRC_IN);
        }
    }


    @Override
    public int getItemCount() {
        return qrlist.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView text;
        public MyViewHolder(@NonNull View itemView,OnItemClickListener listener) {
            super(itemView);
            image=itemView.findViewById(R.id.create_qr_image);
            text=itemView.findViewById(R.id.qr_text);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener!=null){
                        int pos=getAdapterPosition();
                        if (pos!=RecyclerView.NO_POSITION){
                            listener.onItemClick(pos);
                        }
                    }
                }
            });
        }
    }
}
