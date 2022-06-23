package com.example.qrscannerappzl.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.example.qrscannerappzl.R;


public class PrivacyPolicy extends AppCompatActivity {


    SharedPreferences sharedPreferences;
    SharedPreferences.Editor  editor;

    @Override
    protected void onStart() {
        super.onStart();
        sharedPreferences=getSharedPreferences("mobiWomenworkout",MODE_PRIVATE);
        boolean privacychecked =sharedPreferences.getBoolean("Privacy_checked",false);

        if (privacychecked){

            Intent mprvacyintent = new Intent(PrivacyPolicy.this,PermissionsActivity.class);
            startActivity(mprvacyintent);
            finish();
        }



    }
    Animation btnAnim ;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.privacy);

        sharedPreferences=getSharedPreferences("mobiWomenworkout",MODE_PRIVATE);

        AppCompatButton grantedPerm = (AppCompatButton) findViewById(R.id.granted);

        btnAnim = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.button_animation);

        grantedPerm.setAnimation(btnAnim);

        //Proceed button that goes to mainactivity only if the desired permissions are given
        grantedPerm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                editor=sharedPreferences.edit();
                editor.putBoolean("Privacy_checked",true);
                editor.apply();

                startActivity(new Intent(PrivacyPolicy.this, PermissionsActivity.class));
                finish();

            }
        });



    }








}