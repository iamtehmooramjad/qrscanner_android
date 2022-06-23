package com.example.qrscannerappzl.fragment;

import android.content.Context;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.qrscannerappzl.databinding.FragmentScanBinding;
import com.example.qrscannerappzl.util.CheckSettingPrefernces;
import com.example.qrscannerappzl.R;




import com.example.qrscannerappzl.util.Methods;
import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdSize;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Reader;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;

import java.io.FileNotFoundException;
import java.io.InputStream;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

import static android.app.Activity.RESULT_OK;


public class ScanFragment extends Fragment implements ZXingScannerView.ResultHandler {


    SurfaceView surfaceView;
    FragmentScanBinding binding;
    String scan_result;
    private boolean flashState, Cameraorientation = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        binding = FragmentScanBinding.inflate(inflater, container, false);
        View v = binding.getRoot();

        getActivity().setTitle("Scan");

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

        binding.zxscan.setResultHandler(ScanFragment.this::handleResult);
        binding.zxscan.startCamera();

        binding.pickfromGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
                photoPickerIntent.setType("image/*");
                startActivityForResult(photoPickerIntent, 99);
            }
        });

        binding.lightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (flashState == false) {
                    binding.lightButton.setImageResource(R.drawable.flash_on);
                    Toast.makeText(getActivity(), "Flashlight turned on", Toast.LENGTH_SHORT).show();
                    binding.zxscan.setFlash(true);
                    flashState = true;

                } else if (flashState) {
                    binding.lightButton.setImageResource(R.drawable.flash_off);
                    Toast.makeText(getActivity(), "Flashlight turned off", Toast.LENGTH_SHORT).show();
                    binding.zxscan.setFlash(false);
                    flashState = false;
                }
            }
        });

        binding.CameraPosition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Cameraorientation == false) {
                    binding.CameraPosition.setImageResource(R.drawable.flip_camera);
                    binding.zxscan.stopCamera();
                    binding.zxscan.startCamera(1);
                    Cameraorientation = true;

                } else if (Cameraorientation) {
                    binding.CameraPosition.setImageResource(R.drawable.flip_camera);
                    binding.zxscan.stopCamera();
                    binding.zxscan.startCamera(0);
                    Cameraorientation = false;
                }
            }
        });

        return v;
    }


    @Override
    public void handleResult(Result rawResult) {
        binding.zxscan.stopCameraPreview();
        callFragment(new DisplayDetailFragment(), rawResult.getText(), rawResult.getBarcodeFormat().name());
        /////////////////////
        if (CheckSettingPrefernces.getBeepState(getContext())) {
            ToneGenerator toneGen1 = new ToneGenerator(AudioManager.STREAM_MUSIC, 100);
            toneGen1.startTone(ToneGenerator.TONE_PROP_BEEP, 150);
        }


        if (CheckSettingPrefernces.getVibrateState(getContext())){
            Vibrator v = (Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE);
            // Vibrate for 500 milliseconds
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                v.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE));
            } else {
                //deprecated in API 26
                v.vibrate(100);
            }
        }

    }



    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding.zxscan.stopCamera();
    }

    @Override
    public void onResume() {
        super.onResume();
        binding.zxscan.setResultHandler(this); // Register ourselves as a handler for scan results.
        binding.zxscan.startCamera();
    }

    @Override
    public void onPause() {
        super.onPause();
        binding.zxscan.stopCamera();
    }




    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

        if (requestCode == 99 && resultCode == RESULT_OK) {
            try {
                final Uri imageUri = imageReturnedIntent.getData();
                final InputStream imageStream = getActivity().getContentResolver().openInputStream(imageUri);
                final Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                try {
                    Bitmap bMap = selectedImage;
                    String contents = null;
                    int[] intArray = new int[bMap.getWidth() * bMap.getHeight()];
                    bMap.getPixels(intArray, 0, bMap.getWidth(), 0, 0, bMap.getWidth(), bMap.getHeight());
                    LuminanceSource source = new RGBLuminanceSource(bMap.getWidth(), bMap.getHeight(), intArray);
                    BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
                    Reader reader = new MultiFormatReader();
                    Result result = reader.decode(bitmap);
                    contents = result.getText();
                    callFragment(new DisplayDetailFragment(), contents, result.getBarcodeFormat().name());
                } catch (Exception e) {
                    e.printStackTrace();

                }

            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(getContext(), "Something went wrong", Toast.LENGTH_LONG).show();
            }

        } else {
            Toast.makeText(getContext(), "You haven't picked Image", Toast.LENGTH_LONG).show();
        }

    }

    //////////////Method to call Fragments
    public void callFragment(Fragment fragment1, String scan_result, String format) {
        Methods.insertIntoDatabase(scan_result,this);
        Fragment fragment = fragment1;
        Bundle arguments = new Bundle();
        arguments.putString("scanResult", "" + scan_result);
        arguments.putBoolean("inserttodatabase", true);
        arguments.putString("format", format);
        fragment.setArguments(arguments);
        FragmentManager fragmentManager=getActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.fragmentView, fragment, fragment.getClass().getSimpleName()).addToBackStack("display").commit();

    }


}