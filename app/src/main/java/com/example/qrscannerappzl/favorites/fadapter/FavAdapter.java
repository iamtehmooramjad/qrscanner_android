package com.example.qrscannerappzl.favorites.fadapter;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.recyclerview.widget.RecyclerView;

import com.example.qrscannerappzl.history.HistoryViewModel;
import com.example.qrscannerappzl.R;
import com.example.qrscannerappzl.RoomDatabase.ScanResultDetail;
import com.example.qrscannerappzl.util.CheckSettingPrefernces;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.List;

public class FavAdapter extends RecyclerView.Adapter<FavAdapter.OurViewHolder> {

    List<ScanResultDetail> list;
    Context context;
    HistoryViewModel mHistoryViewModel;
    OnItemClickListener mListener;

    ////////////interface
    public interface OnItemClickListener{
        void onClick(int position);
    }

    public void setOnItemClicklLisnter(OnItemClickListener lisnter){
        this.mListener=lisnter;
    }



    public FavAdapter(Context context, ViewModelStoreOwner owner) {
        this.context = context;
        mHistoryViewModel= new ViewModelProvider(owner).get(HistoryViewModel.class);
    }

    public void setList(List<ScanResultDetail> list){
        this.list=list;
    }

    @NonNull
    @Override
    public OurViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater=LayoutInflater.from(parent.getContext());
        View v=inflater.inflate(R.layout.single_row_for_fav,parent,false);
        return new OurViewHolder(v,mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull OurViewHolder holder, int position) {
        holder.name.setText(list.get(position).getTitle());
        holder.date.setText(list.get(position).getDate());

                holder.image_type.setImageResource(R.drawable.barcode_icon);
            if (list.get(position).getResult_type().equalsIgnoreCase("Clipboard"))
                holder.image_type.setImageResource(R.drawable.clipboard_icon);
                //SMS
            else if (list.get(position).getResult_type().equalsIgnoreCase("SMS"))
                holder.image_type.setImageResource(R.drawable.sms_icon);
            else if (list.get(position).getResult_type().equalsIgnoreCase("Contact/Vcard"))
                holder.image_type.setImageResource(R.drawable.vcard_icon);
            else if (list.get(position).getResult_type().equalsIgnoreCase("Event"))
                holder.image_type.setImageResource(R.drawable.event_icon);

            else if (list.get(position).getResult_type().equalsIgnoreCase("Wi-Fi"))
                holder.image_type.setImageResource(R.drawable.wifi_icon);
                //GEO LOCATION
            else if (list.get(position).getResult_type().equalsIgnoreCase("GeoLocation"))
                holder.image_type.setImageResource(R.drawable.location_icon);

                //url
            else if (list.get(position).getResult_type().equalsIgnoreCase("URL"))
                holder.image_type.setImageResource(R.drawable.url_icon);
                //tel
            else if (list.get(position).getResult_type().equalsIgnoreCase("Phone"))
                holder.image_type.setImageResource(R.drawable.phone_icon);
            else {
                holder.image_type.setImageResource(R.drawable.text_icon);
            }
        ////////////////////////////////////////////////////////////////////////////
        if (!CheckSettingPrefernces.getMode(context)) {
            holder.image_type.setColorFilter(ContextCompat.getColor(context, R.color.blue), android.graphics.PorterDuff.Mode.SRC_IN);
        }


        holder.heart_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new MaterialAlertDialogBuilder(context).
                        setTitle("Delete").setMessage("Are you sure you want to remove this item?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mHistoryViewModel.updateFav(list.get(position).getId(),false);
                                notifyDataSetChanged();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        }).show();

            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class OurViewHolder extends RecyclerView.ViewHolder {
        TextView name,date;
        ImageView heart_image,image_type;
        public OurViewHolder(@NonNull View itemView,OnItemClickListener listener) {
            super(itemView);
            heart_image=itemView.findViewById(R.id.fav_btn);
            name=itemView.findViewById(R.id.fav_item_name);
            date=itemView.findViewById(R.id.fav_item_time);
            image_type=itemView.findViewById(R.id.fav_item_image);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener!=null){
                        int pos=getAdapterPosition();
                        if (pos!=RecyclerView.NO_POSITION){
                            listener.onClick(pos);
                        }
                    }
                }
            });
        }
    }


}
