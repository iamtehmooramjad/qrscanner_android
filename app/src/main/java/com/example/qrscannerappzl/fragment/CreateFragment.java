package com.example.qrscannerappzl.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.qrscannerappzl.activities.PermissionsActivity;
import com.example.qrscannerappzl.R;
import com.example.qrscannerappzl.create.QR;
import com.example.qrscannerappzl.create.QrAdapter;
import com.example.qrscannerappzl.databinding.FragmentCreateBinding;
import com.example.qrscannerappzl.util.CheckSettingPrefernces;
import com.google.android.gms.ads.AdListener;

import java.util.ArrayList;


public class CreateFragment extends Fragment {

    QrAdapter adapter;
    ArrayList<QR> list=new ArrayList<>();
    FragmentCreateBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        addData();
        binding = FragmentCreateBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        getActivity().setTitle("Create Qr Code");

        binding.adViewCurrent.loadAd(PermissionsActivity.admobbanneradrequest);
        binding.adViewCurrent.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {

                binding.adViewCurrent.setVisibility(View.VISIBLE);

            }

            @Override
            public void onAdClosed() {

                super.onAdClosed();

            }


        });


        binding.createQrRecyclerview.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter=new QrAdapter(getContext(),list);
        binding.createQrRecyclerview.setAdapter(adapter);

        adapter.setClickListener(new QrAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int pos) {
                if (list.get(pos).getText().equals("Text")){
                    callFragmet(new QrCreatorFragment(),"Text");
                }else if (list.get(pos).getText().equals("Clipboard")){
                    callFragmet(new QrCreatorFragment(),"Clipboard");
                }else if (list.get(pos).getText().equals("URL")){
                    callFragmet(new QrCreatorFragment(),"URL");
                }else if (list.get(pos).getText().equals("Phone")){
                    callFragmet(new QrCreatorFragment(),"Phone");
                }else if (list.get(pos).getText().equals("Contact/Vcard")){
                    callFragmet(new QrCreatorFragment(),"Contact/Vcard");
                }else if (list.get(pos).getText().equals("SMS")){
                    callFragmet(new QrCreatorFragment(),"SMS");
                }else if (list.get(pos).getText().equals("Email")){
                    callFragmet(new QrCreatorFragment(),"Email");
                }else if (list.get(pos).getText().equals("Barcode")){
                    callFragmet(new QrCreatorFragment(),"Barcode");
                }else if (list.get(pos).getText().equals("GeoLocation")){
                    callFragmet(new QrCreatorFragment(),"GeoLocation");
                }else if (list.get(pos).getText().equals("Application")){
                    callFragmet(new QrCreatorFragment(),"Application");
                }else if (list.get(pos).getText().equals("Wi-Fi")){
                    callFragmet(new QrCreatorFragment(),"Wi-Fi");
                }else if (list.get(pos).getText().equals("Event")){
                    callFragmet(new QrCreatorFragment(),"Event");
                }
            }
        });

        return view;

    }

    private void callFragmet(Fragment fragment,String which_creator)
    {

        Bundle arguments=new Bundle();
        arguments.putString("which_creator",which_creator);
        Fragment frag = fragment;
        frag.setArguments(arguments);
        FragmentManager fm = getParentFragmentManager();
        FragmentTransaction ft=fm.beginTransaction();
        ft.replace(R.id.fragmentView, frag).addToBackStack(null);
        ft.commit();

    }

    @Override
    public void onPause() {
        super.onPause();
        list.clear();
    }

    private void addData(){

        list.add(new QR(R.drawable.text_icon,"Text"));
        list.add(new QR(R.drawable.clipboard_icon,"Clipboard"));
        list.add(new QR(R.drawable.url_icon,"URL"));
        list.add(new QR(R.drawable.phone_icon,"Phone"));
        list.add(new QR(R.drawable.vcard_icon,"Contact/Vcard"));
        list.add(new QR(R.drawable.sms_icon,"SMS"));
        list.add(new QR(R.drawable.email_icon,"Email"));
        list.add(new QR(R.drawable.barcode_icon,"Barcode"));
        list.add(new QR(R.drawable.location_icon,"GeoLocation"));
        list.add(new QR(R.drawable.app_icon,"Application"));
        list.add(new QR(R.drawable.wifi_icon,"Wi-Fi"));
        list.add(new QR(R.drawable.event_icon,"Event"));


    }
}