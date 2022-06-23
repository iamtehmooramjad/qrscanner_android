package com.example.qrscannerappzl.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.example.qrscannerappzl.R;
import com.example.qrscannerappzl.fragment.CreateFragment;
import com.example.qrscannerappzl.fragment.FavoritesFragment;
import com.example.qrscannerappzl.fragment.HistoryFragment;
import com.example.qrscannerappzl.fragment.ScanFragment;

import com.example.qrscannerappzl.fragment.SettingFragment;
import com.example.qrscannerappzl.databinding.ActivityMainBinding;
import com.example.qrscannerappzl.util.CheckSettingPrefernces;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.analytics.FirebaseAnalytics;

public class MainActivity extends AppCompatActivity {


    private ActionBarDrawerToggle actionBarDrawerToggle;
    ActivityMainBinding binding;//for view binding
    private FirebaseAnalytics mfirebaseanlytics;
    public static AdRequest adRequest;
    public static InterstitialAd mInterstitialAd;

    public void InterstitialAdmob() {

        InterstitialAd.load(MainActivity.this,getString(R.string.Interstitial), adRequest, new InterstitialAdLoadCallback() {
            @Override
            public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {

                // The mInterstitialAd reference will be null until
                // an ad is loaded.

                mInterstitialAd = interstitialAd;
                mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback(){
                    @Override
                    public void onAdDismissedFullScreenContent() {
                        super.onAdDismissedFullScreenContent();
                        mInterstitialAd=null;
                        //// perform your code that you wants todo after ad dismissed or closed


                    }

                    @Override
                    public void onAdFailedToShowFullScreenContent(com.google.android.gms.ads.AdError adError) {
                        super.onAdFailedToShowFullScreenContent(adError);
                        mInterstitialAd = null;
                        /// perform your action here when ad will not load


                    }

                    @Override
                    public void onAdShowedFullScreenContent() {
                        super.onAdShowedFullScreenContent();
                        mInterstitialAd = null;

                    }
                });

            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                // Handle the error
                mInterstitialAd = null;

            }

        });

    }

    @Override
    protected void onStart() {

        super.onStart();
        adRequest = new AdRequest.Builder().build();
        InterstitialAdmob();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View v = binding.getRoot();
        setContentView(v);



        mfirebaseanlytics = FirebaseAnalytics.getInstance(MainActivity.this);
        mfirebaseanlytics.setUserProperty("MainActivity","Main Activity");


        if (savedInstanceState == null) {
            try {
                callFragment(new ScanFragment(), "scan");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        //applying theme
        if (CheckSettingPrefernces.getMode(this)) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }


        ///////
        setSupportActionBar(binding.toolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        actionBarDrawerToggle = new ActionBarDrawerToggle(MainActivity.this, binding.drawerLayout, R.string.drawer_open, R.string.drawer_close);
        binding.drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        ////Inflating navigation header view
        View view = binding.navigationView.inflateHeaderView(R.layout.navigation_header);
        binding.navigationView.setItemIconTintList(null);


        /////////////click listener over Nnavigation drawer
        binding.navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                UserItemSelected(item);
                return false;
            }
        });
    }
    ////////////////this method is overide to enable toogle button
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app icon touch event
        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // Handle your other action bar items...
        return super.onOptionsItemSelected(item);
    }
    Bundle bundle;
    /////navigation item selected method
    private void UserItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.scan:

                bundle = new Bundle();
                bundle.putString(FirebaseAnalytics.Param.SCREEN_NAME, "MainActivity");
                bundle.putString(FirebaseAnalytics.Param.SCREEN_CLASS, "Scan");
                mfirebaseanlytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, bundle);
                callFragment(new ScanFragment(), "scan");

                break;
            case R.id.create:

                bundle = new Bundle();
                bundle.putString(FirebaseAnalytics.Param.SCREEN_NAME, "MainActivity");
                bundle.putString(FirebaseAnalytics.Param.SCREEN_CLASS, "Create");
                callFragment(new CreateFragment(), "create");

                break;

            case R.id.history:

                bundle = new Bundle();
                bundle.putString(FirebaseAnalytics.Param.SCREEN_NAME, "MainActivity");
                bundle.putString(FirebaseAnalytics.Param.SCREEN_CLASS, "History");
                callFragment(new HistoryFragment(), "history");

                break;

            case R.id.favorites:

                bundle = new Bundle();
                bundle.putString(FirebaseAnalytics.Param.SCREEN_NAME, "MainActivity");
                bundle.putString(FirebaseAnalytics.Param.SCREEN_CLASS, "Favorite");
                callFragment(new FavoritesFragment(), "favorite");

                break;

            case R.id.setting:

                bundle = new Bundle();
                bundle.putString(FirebaseAnalytics.Param.SCREEN_NAME, "MainActivity");
                bundle.putString(FirebaseAnalytics.Param.SCREEN_CLASS, "Settings");
                callFragment(new SettingFragment(), "setting");

                break;
            case R.id.rateus:

                bundle = new Bundle();
                bundle.putString(FirebaseAnalytics.Param.SCREEN_NAME, "MainActivity");
                bundle.putString(FirebaseAnalytics.Param.SCREEN_CLASS, "Rate us");
                openAppInPlayStore();

                break;

            case R.id.share:

                bundle = new Bundle();
                bundle.putString(FirebaseAnalytics.Param.SCREEN_NAME, "MainActivity");
                bundle.putString(FirebaseAnalytics.Param.SCREEN_CLASS, "Share App");
                share_app();

                break;

            case R.id.privacy_policy:

                bundle = new Bundle();
                bundle.putString(FirebaseAnalytics.Param.SCREEN_NAME, "MainActivity");
                bundle.putString(FirebaseAnalytics.Param.SCREEN_CLASS, "Privacy Policy ");
                openPrivacyPolicy();

                break;
        }

    }
    //////////////Method to call Fragments
    public void callFragment(Fragment fragment1, String TAG) {

        setTitle(TAG);
        Fragment fragment = fragment1;
        FragmentManager fragmentManager = getSupportFragmentManager();
        if (fragmentManager.getBackStackEntryCount() > 0) {
            fragmentManager.popBackStack(TAG, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            if (fragment instanceof HistoryFragment){
                setTitle("History");
            }
            else if (fragment instanceof  FavoritesFragment){
                setTitle("Favorite");
            }
            else if (fragment instanceof  ScanFragment){
                setTitle("Scan");
            }
            else if (fragment instanceof  CreateFragment){
                setTitle("Create");
            }

        }
        if (!TAG.equalsIgnoreCase("scan")) {
            fragmentManager.beginTransaction().replace(R.id.fragmentView, fragment).addToBackStack(TAG).commit();
        }

        else {

            fragmentManager.beginTransaction().replace(R.id.fragmentView, fragment).commit();
        }

        binding.drawerLayout.closeDrawers();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }

    @Override
    public void onBackPressed() {

      if (mInterstitialAd !=null){

          mInterstitialAd.show(MainActivity.this);

      }

      else {

         binding.drawerLayout.closeDrawers();
         super.onBackPressed();

      }


    }
    private void share_app() {

        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, "https://play.google.com/store/apps/details?id=" + getPackageName());
        sendIntent.setType("text/plain");
        Intent shareIntent = Intent.createChooser(sendIntent, "Choose From");
        startActivity(shareIntent);

    }
    private void openAppInPlayStore() {

        Uri uri = Uri.parse("market://details?id=" + getPackageName());
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        try {
            startActivity(goToMarket);
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id=" + getPackageName())));
        }


    }
    private void openPrivacyPolicy() {

        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://sites.google.com/view/qr-code-privacy/home"));
        try {
            startActivity(browserIntent);
        }

        catch (Exception e) {

        }

    }

}