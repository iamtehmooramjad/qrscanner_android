package com.example.qrscannerappzl.fragment;

import android.content.DialogInterface;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.example.qrscannerappzl.R;
import com.example.qrscannerappzl.RoomDatabase.ScanResultDetail;
import com.example.qrscannerappzl.history.HistoryViewModel;
import com.example.qrscannerappzl.history.adapter.HistoryAdapter;
import com.example.qrscannerappzl.databinding.FragmentHistoryBinding;
import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdSize;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.tabs.TabLayout;
import java.util.ArrayList;
import java.util.List;


public class HistoryFragment extends Fragment {


    FragmentHistoryBinding binding;
    HistoryViewModel mHistoryViewModel;
    List<ScanResultDetail> scanned_list=new ArrayList<>();
    List<ScanResultDetail> created_list=new ArrayList<>();
    HistoryAdapter scanned_adapter;
    HistoryAdapter created_adapter;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        binding = FragmentHistoryBinding.inflate(inflater, container, false);
        View v = binding.getRoot();

        com.facebook.ads.AdView adView = new com.facebook.ads.AdView(getActivity(), getString(R.string.fb_placement_banner), AdSize.BANNER_HEIGHT_50);
        binding.bannerContainer.addView(adView);
        adView.loadAd(adView.buildLoadAdConfig().withAdListener(new com.facebook.ads.AdListener() {
            @Override
            public void onError(Ad ad, AdError adError) {


            }

            @Override
            public void onAdLoaded(Ad ad) {


            }

            @Override
            public void onAdClicked(Ad ad) {

            }

            @Override
            public void onLoggingImpression(Ad ad) {


            }
        }).build());



        binding.dataRecyclerview.setLayoutManager(new LinearLayoutManager(getContext()));
        scanned_adapter=new HistoryAdapter(getActivity(),this);
        created_adapter=new HistoryAdapter(getActivity(),this);
        // init ViewModel
        mHistoryViewModel = new ViewModelProvider(this).get(HistoryViewModel.class);

        if (binding.tabLayout.getSelectedTabPosition()==0){
            mHistoryViewModel.getScannedData().observe(getViewLifecycleOwner(), new Observer<List<ScanResultDetail>>() {
                @Override
                public void onChanged(List<ScanResultDetail> scanResultDetails) {
                    scanned_list=scanResultDetails;

                    if (scanned_list.isEmpty()){
                        binding.emptyTextview.setVisibility(View.VISIBLE);
                    }
                    else {
                        binding.emptyTextview.setVisibility(View.GONE);
                        scanned_adapter.setList(scanned_list);
                        binding.dataRecyclerview.setAdapter(scanned_adapter);
                        binding.dataRecyclerview.scheduleLayoutAnimation();
                    }

                }
            });
        }

        binding.deleteAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (binding.tabLayout.getSelectedTabPosition()==0){
                    new MaterialAlertDialogBuilder(getContext()).
                            setTitle("Delete").setMessage("Are you sure you want to delete all scanned items?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    mHistoryViewModel.deleteScanned("scanned");
                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            }).show();
                    //

                }else if (binding.tabLayout.getSelectedTabPosition()==1){
                    new MaterialAlertDialogBuilder(getContext()).
                            setTitle("Delete").setMessage("Are you sure you want to delete all created items?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    mHistoryViewModel.deleteCreated("created");
                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            }).show();
                }
            }
        });






        binding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition()==0){
                    mHistoryViewModel.getScannedData().observe(getViewLifecycleOwner(), new Observer<List<ScanResultDetail>>() {
                        @Override
                        public void onChanged(List<ScanResultDetail> scanResultDetails) {
                            scanned_list=scanResultDetails;
                            if (scanned_list.isEmpty()){
                                binding.emptyTextview.setVisibility(View.VISIBLE);
                            }
                            else {
                                binding.emptyTextview.setVisibility(View.GONE);
                                scanned_adapter.setList(scanned_list);
                                binding.dataRecyclerview.setAdapter(scanned_adapter);
                                binding.dataRecyclerview.scheduleLayoutAnimation();
                            }
                        }
                    });

                }else if (tab.getPosition()==1){
                    mHistoryViewModel.getCreatedData().observe(getViewLifecycleOwner(), new Observer<List<ScanResultDetail>>() {
                        @Override
                        public void onChanged(List<ScanResultDetail> scanResultDetails) {
                            scanned_list.clear();
                            created_list=scanResultDetails;
                            if (created_list.isEmpty()){
                                binding.emptyTextview.setVisibility(View.VISIBLE);
                            }else{
                                binding.emptyTextview.setVisibility(View.GONE);
                                created_adapter.setList(created_list);
                                binding.dataRecyclerview.setAdapter(created_adapter);
                            }

                        }
                    });
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        scanned_adapter.setOnItemClickListner(new HistoryAdapter.OnItemClickListner() {
            @Override
            public void onItemClick(int pos) {
                //callFragment(new DisplayDetailFragment(),scanned_list.get(pos).getScan_results());
            }
        });
        created_adapter.setOnItemClickListner(new HistoryAdapter.OnItemClickListner() {
            @Override
            public void onItemClick(int pos) {
                //callFragment(new DisplayDetailFragment(),created_list.get(pos).getScan_results());
            }
        });


        return v;
    }


    public void callFragment(Fragment fragment1, String scan_result) {

        Fragment fragment = fragment1;
        Bundle arguments = new Bundle();
        arguments.putString("scanResult", "" + scan_result);
        arguments.putBoolean("inserttodatabase", false);    //a bool variable used to check weahter insert to database or not
        fragment.setArguments(arguments);
        FragmentManager fragmentManager=getActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.fragmentView, fragment, fragment.getClass().getSimpleName()).addToBackStack("history").commit();
    }
}