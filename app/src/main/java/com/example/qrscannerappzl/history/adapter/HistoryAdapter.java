package com.example.qrscannerappzl.history.adapter;


import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.recyclerview.widget.RecyclerView;

import com.example.qrscannerappzl.history.HistoryViewModel;
import com.example.qrscannerappzl.R;
import com.example.qrscannerappzl.RoomDatabase.ScanResultDetail;
import com.example.qrscannerappzl.fragment.DisplayDetailFragment;
import com.example.qrscannerappzl.util.CheckSettingPrefernces;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.MyViewHolder> {

    boolean isFav, multiselection = false;
    List<ScanResultDetail> list;
    List<ScanResultDetail> selected_items_list = new ArrayList<>();
    Activity context;
    HistoryViewModel mHistoryViewModel;
    private OnItemClickListner mListner;
    ActionMode actionMode;
    int selection_counter = 0;


    public boolean multiSelect = false;
    private List<ScanResultDetail> selectedItems;


    public interface OnItemClickListner {
        void onItemClick(int pos);
    }

    public void setOnItemClickListner(OnItemClickListner listner) {
        this.mListner = listner;
    }

    public void setList(List<ScanResultDetail> list) {
        this.list = list;
    }

    public HistoryAdapter(Activity context, ViewModelStoreOwner owner) {
        this.context = context;
        mHistoryViewModel = new ViewModelProvider(owner).get(HistoryViewModel.class);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View v = inflater.inflate(R.layout.itemview_for_history, parent, false);
        return new MyViewHolder(v, mListner);

    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.name.setText(list.get(position).getTitle());
        holder.date.setText(list.get(position).getDate());
        if (list.get(position).isCheckbox_status()) {
            holder.mCheckBox.setVisibility(View.VISIBLE);
        } else {

            holder.mCheckBox.setVisibility(View.GONE);
        }

        //////For  Local Theme//////////////////////////////////////////////////////////////////////////////////////

        if (list.get(position).getResult_type().equalsIgnoreCase("Barcode"))
            holder.imageView.setImageResource(R.drawable.barcode_icon);
        if (list.get(position).getResult_type().equalsIgnoreCase("Clipboard"))
            holder.imageView.setImageResource(R.drawable.clipboard_icon);
            //SMS
        else if (list.get(position).getResult_type().equalsIgnoreCase("SMS"))
            holder.imageView.setImageResource(R.drawable.sms_icon);
        else if (list.get(position).getResult_type().equalsIgnoreCase("Contact/Vcard"))
            holder.imageView.setImageResource(R.drawable.vcard_icon);
        else if (list.get(position).getResult_type().equalsIgnoreCase("event"))
            holder.imageView.setImageResource(R.drawable.event_icon);

        else if (list.get(position).getResult_type().equalsIgnoreCase("WiFi"))
            holder.imageView.setImageResource(R.drawable.wifi_icon);
            //GEO LOCATION
        else if (list.get(position).getResult_type().equalsIgnoreCase("GeoLocation"))
            holder.imageView.setImageResource(R.drawable.location_icon);

            //url
        else if (list.get(position).getResult_type().equalsIgnoreCase("URL"))
            holder.imageView.setImageResource(R.drawable.url_icon);
            //tel
        else if (list.get(position).getResult_type().equalsIgnoreCase("Phone"))
            holder.imageView.setImageResource(R.drawable.phone_icon);

        else {

            holder.imageView.setImageResource(R.drawable.text_icon);
        }

        if (!CheckSettingPrefernces.getMode(context)) {

            holder.imageView.setColorFilter(ContextCompat.getColor(context, R.color.blue), android.graphics.PorterDuff.Mode.SRC_IN);
        }

        ////////////////////////////////////////////////////////////////////////////

        if (!CheckSettingPrefernces.getMode(context)) {

            holder.more_btn.setColorFilter(ContextCompat.getColor(context, R.color.blue), android.graphics.PorterDuff.Mode.SRC_IN);

        }

        holder.more_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextInputEditText edit_name;
                View add_to_fav;
                View delete;
                View view_detail;
                Button save;
                ImageView add_to_fav_image;
                BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(context, R.style.BottomSheetDialogTheme);
                View bottomSheetView = LayoutInflater.from(context)
                        .inflate(R.layout.bottom_sheet_for_history, (LinearLayout) v.findViewById(R.id.bottom_sheet));
                bottomSheetDialog.setContentView(bottomSheetView);
                bottomSheetDialog.show();

                edit_name = bottomSheetDialog.findViewById(R.id.edit_name);
                add_to_fav = bottomSheetDialog.findViewById(R.id.add_to_fav);
                delete = bottomSheetDialog.findViewById(R.id.delete);
                save = bottomSheetDialog.findViewById(R.id.update_history_item);
                add_to_fav_image = bottomSheetDialog.findViewById(R.id.add_to_fav_image);
                view_detail = bottomSheetView.findViewById(R.id.view_fav);

                edit_name.setText(list.get(position).getTitle());

                isFav = list.get(position).isFavorite();

                isFav = list.get(position).isFavorite();

                if (isFav) {
                    add_to_fav_image.setImageResource(R.drawable.fav_check_icon);
                } else if (!isFav) {
                    add_to_fav_image.setImageResource(R.drawable.fav_uncheck_icon);
                }

                add_to_fav.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (isFav) {
                            add_to_fav_image.setImageResource(R.drawable.fav_uncheck_icon);
                            isFav = false;
                        } else if (!isFav) {
                            add_to_fav_image.setImageResource(R.drawable.fav_check_icon);
                            isFav = true;
                        }
                    }

                });
                delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new MaterialAlertDialogBuilder(context).
                                setTitle("Delete").setMessage("Are you sure you want to delete this item?")
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        mHistoryViewModel.deleteSpecific(list.get(position).getTitle());
                                        notifyDataSetChanged();
                                        bottomSheetDialog.dismiss();
                                    }
                                })
                                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        bottomSheetDialog.dismiss();
                                    }
                                }).show();
                        //

                    }
                });
                save.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!edit_name.getText().toString().equalsIgnoreCase("")) {

                            mHistoryViewModel.updateFav(list.get(position).getId(), isFav);
                            if (!list.get(position).getTitle().equals(edit_name.getText().toString())) {
                                mHistoryViewModel.updateTitle(list.get(position).getTitle(), edit_name.getText().toString());
                            }
                            bottomSheetDialog.dismiss();
                            notifyDataSetChanged();
                        } else {
                            Toast.makeText(context, "Enter title", Toast.LENGTH_SHORT).show();
                        }

                    }
                });
                ///////
                view_detail.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Bundle arguments = new Bundle();
                        ;
                        AppCompatActivity activity = (AppCompatActivity) v.getContext();
                        Fragment fragment = new DisplayDetailFragment();
                        arguments.putString("scanResult", "" + list.get(position).getScan_results());
                        arguments.putBoolean("inserttodatabase", false);    //a bool variable used to check weahter insert to database or not
                        fragment.setArguments(arguments);
                        activity.getSupportFragmentManager().beginTransaction().replace(R.id.fragmentView, fragment).addToBackStack(null).commit();
                        bottomSheetDialog.dismiss();
                    }
                });
            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (multiselection) {
                    if (!list.get(position).isCheckbox_status()) {
                        holder.mCheckBox.setVisibility(View.VISIBLE);
                        holder.mCheckBox.setChecked(true);
                        list.get(position).setCheckbox_status(true);
                        selection_counter++;
                    } else {
                        holder.mCheckBox.setVisibility(View.GONE);
                        list.get(position).setCheckbox_status(false);
                        selection_counter--;
                    }
                    if (selection_counter == 0) {
                        actionMode.finish();
                        multiselection = false;
                    }


                }

            }
        });
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {


                if (multiselection) {
                    if (!list.get(position).isCheckbox_status()) {
                        holder.mCheckBox.setVisibility(View.VISIBLE);
                        holder.mCheckBox.setChecked(true);
                        list.get(position).setCheckbox_status(true);
                        selection_counter++;

                    } else {
                        holder.mCheckBox.setVisibility(View.GONE);
                        list.get(position).setCheckbox_status(false);
                        selection_counter--;
                    }
                    if (selection_counter == 0) {

                        actionMode.finish();
                        multiselection = false;
                    }
                } else {
                    multiselection = true;
                    holder.mCheckBox.setVisibility(View.VISIBLE);
                    holder.mCheckBox.setChecked(true);
                    selection_counter++;
                    list.get(position).setCheckbox_status(true);
                    actionMode = context.startActionMode(new ActionMode.Callback() {
                        @Override
                        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                            context.getMenuInflater().inflate(R.menu.action_mode, menu);
                            return true;
                        }

                        @Override
                        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                            return false;
                        }

                        @Override
                        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                            switch (item.getItemId()) {
                                case R.id.delete:
                                    selected_items_list.clear();
                                    getSelectedList();
                                    new MaterialAlertDialogBuilder(context).
                                            setTitle("Delete").setMessage("Are you sure you want to delete selected items?")
                                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    deleteAll();
                                                }
                                            })
                                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {

                                                }
                                            }).show();
                                    //
                            }
                            return false;
                        }

                        @Override
                        public void onDestroyActionMode(ActionMode mode) {

                        }
                    });
                }


                return true;
            }
        });


    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView imageView;
        TextView name, date;
        ImageView more_btn;
        CheckBox mCheckBox;


        public MyViewHolder(@NonNull View itemView, OnItemClickListner listner) {
            super(itemView);

            more_btn = itemView.findViewById(R.id.more_btn);
            name = itemView.findViewById(R.id.history_item_name);
            date = itemView.findViewById(R.id.history_item_time);
            imageView = itemView.findViewById(R.id.history_item_image);
            mCheckBox = itemView.findViewById(R.id.checkbox);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (listner != null) {
                        int pos = getAdapterPosition();
                        if (pos != RecyclerView.NO_POSITION) {
                            listner.onItemClick(pos);
                        }
                    }
                }
            });


        }
    }

    private void getSelectedList() {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).isCheckbox_status()) {
                selected_items_list.add(list.get(i));
            }
        }
    }
    private void deleteAll() {
        for (int i = 0; i < selected_items_list.size(); i++) {
            mHistoryViewModel.deleteSpecific(list.get(i).getTitle());
        }
    }

}
