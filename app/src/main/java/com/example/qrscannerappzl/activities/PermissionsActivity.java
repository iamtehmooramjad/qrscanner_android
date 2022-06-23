package com.example.qrscannerappzl.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.qrscannerappzl.R;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

import java.util.ArrayList;
import java.util.List;


@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
public class PermissionsActivity extends AppCompatActivity {

    public static final int PERMISSION_ALL = 1;
    String[] PERMISSIONS = {

            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.CAMERA,
            Manifest.permission.READ_CALENDAR,
            Manifest.permission.WRITE_CALENDAR,

       };

    public static AdRequest admobbanneradrequest;
    @Override
    protected void onStart() {
        admobbanneradrequest = new AdRequest.Builder().build();
        if (checkPermissions()){

            gotoActivity();
        }
        else {

            if(!hasPermissions(PermissionsActivity.this, PERMISSIONS)){
                ActivityCompat.requestPermissions(PermissionsActivity.this, PERMISSIONS, PERMISSION_ALL);
            }

        }
        super.onStart();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.permisison);

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String permissionsList[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_ALL:{
                if (grantResults.length > 0) {
                    String permissionsDenied = "";
                    for (String per : permissionsList) {
                        if(grantResults[0] == PackageManager.PERMISSION_DENIED){
                            permissionsDenied += "\n" + per;

                        }

                    }
                    // Show permissionsDenied
                    gotoActivity();
                }
                return;
            }
        }
    }



    private  boolean checkPermissions() {
        int result;
        List<String> listPermissionsNeeded = new ArrayList<>();
        for (String p:PERMISSIONS) {
            result = ContextCompat.checkSelfPermission(PermissionsActivity.this,p);
            if (result != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(p);
            }
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]),PERMISSION_ALL );
            return false;
        }
        return true;
    }

    public static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    private void gotoActivity() {

        Intent intent = new Intent(PermissionsActivity.this, MainActivity.class);
        startActivity(intent);
        finish();


    }


}