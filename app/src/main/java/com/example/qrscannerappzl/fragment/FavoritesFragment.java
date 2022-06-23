package com.example.qrscannerappzl.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.qrscannerappzl.favorites.fadapter.FavAdapter;
import com.example.qrscannerappzl.history.HistoryViewModel;
import com.example.qrscannerappzl.R;
import com.example.qrscannerappzl.RoomDatabase.ScanResultDetail;
import com.example.qrscannerappzl.databinding.FragmentFavoritesBinding;
import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdOptionsView;
import com.facebook.ads.MediaView;
import com.facebook.ads.NativeAd;
import com.facebook.ads.NativeAdLayout;
import com.facebook.ads.NativeAdListener;


import java.util.ArrayList;
import java.util.List;


public class FavoritesFragment extends Fragment {

    HistoryViewModel mHistoryViewModel;
    FragmentFavoritesBinding binding;
    FavAdapter adapter;
    List<ScanResultDetail> list=new ArrayList<>();

    private NativeAdLayout nativeAdLayout;
    private LinearLayout adView;
    private NativeAd nativeAd;
    private void loadNativeAd(View  mview) {

        nativeAd = new NativeAd(getActivity(), getResources().getString(R.string.fb_placement_NativeAdvanced));
        NativeAdListener nativeAdListener = new NativeAdListener() {
            @Override
            public void onMediaDownloaded(Ad ad) {

            }

            @Override
            public void onError(Ad ad, AdError adError) {

            }

            @Override
            public void onAdLoaded(Ad ad) {
                // Race condition, load() called again before last ad was displayed
                if (nativeAd == null || nativeAd != ad) {
                    return;
                }
                // Inflate Native Ad into Container
                inflateAd(nativeAd,mview);
            }

            @Override
            public void onAdClicked(Ad ad) {

            }

            @Override
            public void onLoggingImpression(Ad ad) {

            }

        };

        // Request an ad
        nativeAd.loadAd(
                nativeAd.buildLoadAdConfig()
                        .withAdListener(nativeAdListener)
                        .build());
    }
    private void inflateAd(NativeAd nativeBannerAd,View mview) {
        // Unregister last ad
        nativeBannerAd.unregisterView();
        // Add the Ad view into the ad container.
        nativeAdLayout = mview.findViewById(R.id.native_ad_container);
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        // Inflate the Ad view.  The layout referenced is the one you created in the last step.
        adView = (LinearLayout) inflater.inflate(R.layout.facebook_native_ad, nativeAdLayout, false);
        nativeAdLayout.addView(adView);

        // Add the AdChoices icon
        RelativeLayout adChoicesContainer = adView.findViewById(R.id.ad_choices_container);
        AdOptionsView adOptionsView = new AdOptionsView(getActivity(), nativeBannerAd, nativeAdLayout);
        adChoicesContainer.removeAllViews();
        adChoicesContainer.addView(adOptionsView, 0);

        // Create native UI using the ad metadata.
        TextView nativeAdTitle = adView.findViewById(R.id.native_ad_title);
        TextView nativeAdSocialContext = adView.findViewById(R.id.native_ad_social_context);
        TextView sponsoredLabel = adView.findViewById(R.id.native_ad_sponsored_label);
        MediaView nativeAdIconView = adView.findViewById(R.id.native_icon_view);
        Button nativeAdCallToAction = adView.findViewById(R.id.native_ad_call_to_action);

        // Set the Text.
        nativeAdCallToAction.setText(nativeBannerAd.getAdCallToAction());
        nativeAdCallToAction.setVisibility(
                nativeBannerAd.hasCallToAction() ? View.VISIBLE : View.INVISIBLE);
        nativeAdTitle.setText(nativeBannerAd.getAdvertiserName());
        nativeAdSocialContext.setText(nativeBannerAd.getAdSocialContext());
        sponsoredLabel.setText(nativeBannerAd.getSponsoredTranslation());

        // Register the Title and CTA button to listen for clicks.
        List<View> clickableViews = new ArrayList<>();
        clickableViews.add(nativeAdTitle);
        clickableViews.add(nativeAdCallToAction);
        nativeBannerAd.registerViewForInteraction(adView, nativeAdIconView, clickableViews);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentFavoritesBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        loadNativeAd(view);
        getActivity().setTitle("Favorite");
        adapter=new FavAdapter(getActivity(),this);

        // init ViewModel
        mHistoryViewModel = new ViewModelProvider(this).get(HistoryViewModel.class);
        binding.favRecyclerview.setLayoutManager(new LinearLayoutManager(getContext()));
        mHistoryViewModel.getFavData().observe(getViewLifecycleOwner(), new Observer<List<ScanResultDetail>>() {
            @Override
            public void onChanged(List<ScanResultDetail> scanResultDetails) {
                adapter.setList(scanResultDetails);
                list=scanResultDetails;
                binding.favRecyclerview.setAdapter(adapter);
            }
        });

        adapter.setOnItemClicklLisnter(new FavAdapter.OnItemClickListener() {
            @Override
            public void onClick(int position) {
                callFragment(new DisplayDetailFragment(),list.get(position).getScan_results());
            }
        });

        return view;

    }

    public void callFragment(Fragment fragment1, String scan_result) {

        Fragment fragment = fragment1;
        Bundle arguments = new Bundle();
        arguments.putString("scanResult", "" + scan_result);
        arguments.putBoolean("inserttodatabase", false);    //a bool variable used to check weahter insert to database or not
        //
        fragment.setArguments(arguments);
        FragmentManager fragmentManager=getActivity().getSupportFragmentManager();
        // Replace fragmentCotainer with your container id
        Fragment currentFragment = fragmentManager.findFragmentById(R.id.fragmentView);
        // Return if the class are the same
        if(currentFragment.getClass().equals(fragment.getClass())) return;
        else
            fragmentManager.beginTransaction().replace(R.id.fragmentView, fragment, fragment.getClass().getSimpleName()).addToBackStack("Favorite").commit();

    }


}