package com.example.qrscannerappzl.fragment;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Insets;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import android.os.Looper;
import android.provider.ContactsContract;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowInsets;
import android.view.WindowMetrics;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.qrscannerappzl.activities.MainActivity;
import com.example.qrscannerappzl.activities.PermissionsActivity;
import com.example.qrscannerappzl.create.Calender.CalenderClass;
import com.example.qrscannerappzl.create.Calender.EventAdapter;
import com.example.qrscannerappzl.create.Calender.EventModelClass;
import com.example.qrscannerappzl.create.CustomeAdapter.CustomeAdapter;
import com.example.qrscannerappzl.create.CustomeAdapter.ModelClass;
import com.example.qrscannerappzl.history.HistoryViewModel;
import com.example.qrscannerappzl.R;

import com.example.qrscannerappzl.RoomDatabase.ScanResultDetail;

import com.example.qrscannerappzl.databinding.FragmentQrCreatorBinding;
import com.example.qrscannerappzl.util.Methods;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.Writer;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.oned.Code128Writer;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Random;


import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;

import static android.content.Context.WIFI_SERVICE;


public class QrCreatorFragment extends Fragment {


    HistoryViewModel mHistoryViewModel;


    //
    double latitude, logitude;
    Location mLocation;
    String currentAddress;

    String dateString;
    String timeString;

    int mHour;
    int mMinute;
    int mDay;
    int mMonth;
    int mYear;

    //WiFi
    WifiManager wifiManager;
    private List<WifiConfiguration> results;
    private ArrayList<ModelClass> arrayList = new ArrayList<>();
    CustomeAdapter adapter;
    ///variables for permision
    public static final int PICK_CONTACT = 1;

    FragmentQrCreatorBinding binding;
    String WhichCreator = "";
    private int pa;
    private boolean canGetLocation;
    Bundle bundlecrash;
    //
    String event_startdate = null, event_enddate = null;
    private FirebaseAnalytics mfirebaseanlytics;


    public static AdRequest adRequest;
    public static InterstitialAd mInterstitialAd;
    public void InterstitialAdmob() {

        InterstitialAd.load(getActivity(),getString(R.string.Interstitial), adRequest, new InterstitialAdLoadCallback() {
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
                        createQR(WhichCreator);

                    }

                    @Override
                    public void onAdFailedToShowFullScreenContent(com.google.android.gms.ads.AdError adError) {
                        super.onAdFailedToShowFullScreenContent(adError);
                        mInterstitialAd = null;
                        /// perform your action here when ad will not load
                        createQR(WhichCreator);

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
    public void onStart() {

        super.onStart();
        adRequest = new AdRequest.Builder().build();
        InterstitialAdmob();

    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        binding = FragmentQrCreatorBinding.inflate(inflater, container, false);
        View v = binding.getRoot();
        wifiManager = (WifiManager) getActivity().getApplicationContext().getSystemService(WIFI_SERVICE);
        mfirebaseanlytics = FirebaseAnalytics.getInstance(getActivity());
        mfirebaseanlytics.setUserProperty("Qr Create Fragment", "QrCreatorFragment");

        // Get a new or existing ViewModel from the ViewModelProvider.
        mHistoryViewModel = new ViewModelProvider(this).get(HistoryViewModel.class);
        //spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.planets_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        binding.wifiSpinnerQrCreatorInput.setAdapter(adapter);

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            WhichCreator = bundle.getString("which_creator");
            viewHandling(WhichCreator);
        }


        binding.phoneQrCreatorInputBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addContact(1111);
            }
        });

        binding.vcardNameQrCreatorInputBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addContact(1112);
            }
        });

        binding.smsNumberQrCreatorInputBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addContact(1113);
            }
        });


        binding.applicationUrlQrCreatorInputBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List list = getPackages();
            }
        });

        binding.applicationUrlQrCreatorInputBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ArrayList<ModelClass> list = getPackages();

                CustomeAdapter adapter = new CustomeAdapter(getContext(), R.layout.item_view_for_custome_adapter, list);


                new MaterialAlertDialogBuilder(getContext()).
                        setTitle("Choose Application").
                        setAdapter(adapter, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                binding.applicationUrlQrCreatorInput.setText(list.get(i).getUrl());
                            }
                        }).show();
            }
        });


        binding.wifiSsidQrCreatorInputBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!wifiManager.isWifiEnabled()) {
                    Toast.makeText(getContext(), "WiFi is disabled ... We need to enable it", Toast.LENGTH_LONG).show();
                } else {
                    scanWifi();
                }

            }
        });


        binding.eventTitleQrCreatorInputBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ArrayList<EventModelClass> eventlist = CalenderClass.readCalendarEvent(getContext());
                EventAdapter eventAdapter = new EventAdapter(getContext(), R.layout.event_single_row, eventlist);
                new MaterialAlertDialogBuilder(getContext()).
                        setTitle("Choose Event").
                        setAdapter(eventAdapter, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                event_enddate = eventlist.get(i).getEnd_date();
                                event_startdate = eventlist.get(i).getStart_date();
                                binding.eventTitleQrCreatorInput.setText(eventlist.get(i).getName());
                                binding.eventStartdateQrCreatorInput.setText(Methods.extractDateString(eventlist.get(i).getStart_date()));
                                binding.eventEnddateQrCreatorInput.setText(Methods.extractDateString(eventlist.get(i).getEnd_date()));
                            }
                        }).show();

            }
        });


        binding.eventStartdateQrCreatorInputBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDate(binding.eventStartdateQrCreatorInput);

            }
        });

        binding.eventEnddateQrCreatorInputBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getDate(binding.eventEnddateQrCreatorInput);
            }
        });


        binding.geolocationAddressQrCreatorInputBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Methods.checkConnectivity(getContext()))
                    searchLocation();
            }
        });


        binding.geolocationAddressQrCreatorCurrentLocationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Methods.checkConnectivity(getContext())) {
                    getLocation();
                } else {
                    Toast.makeText(getContext(), "No internet Connection", Toast.LENGTH_SHORT).show();
                }


            }
        });

        //////Creating Qr Button/////
        binding.createQrButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bundlecrash = new Bundle();
                bundlecrash.putString(FirebaseAnalytics.Param.SCREEN_NAME, "QrCreatorFragment");
                bundlecrash.putString(FirebaseAnalytics.Param.SCREEN_CLASS, "Create Qr Button");

                if (mInterstitialAd!=null){

                    mInterstitialAd.show(getActivity());
                }

                else {

                    createQR(WhichCreator);

                }



            }
        });

        /////////


        return v;
    }

    public void getDate(TextInputEditText editText) {

        Calendar mcurrentTime = Calendar.getInstance();
        mHour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
        mMinute = mcurrentTime.get(Calendar.MINUTE);
        mDay = mcurrentTime.get(Calendar.DAY_OF_MONTH);
        mMonth = mcurrentTime.get(Calendar.MONTH);
        mYear = mcurrentTime.get(Calendar.YEAR);

        TimePickerDialog mTimePicker;
        mTimePicker = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {

            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                mcurrentTime.set(mYear, mMonth, mDay, selectedHour, selectedMinute);
                SimpleDateFormat formatter = new SimpleDateFormat(
                        "yyyyMMdd'T'HHmmss'Z'");
                dateString = formatter.format(mcurrentTime.getTime());
                editText.setText(Methods.extractDateString(dateString));

            }
        }, mMinute, mHour, true);//Yes 24 hour time
        mTimePicker.setTitle("Select Time");
        mTimePicker.show();

        DatePickerDialog datePickerDialog;
        datePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                mMonth = month;
                mYear = year;
                mDay = dayOfMonth;
            }
        }, mYear, mMonth, mDay);
        datePickerDialog.setTitle("Pick Date");
        datePickerDialog.show();

    }


    /////////////////

    public void createQR(String input_type) {

        String data = null;
        ////Variables start//////////////////////
        String text_text = null;
        String clipboard_text = null;
        String url_text = null;
        String phone = null;

        ////vcard variables
        String vcard_name = null, vcard_organization = null, vcard_email = null, vcard_phone = null, vcard_cell = null, vcard_fax = null,
                vcard_street = null, vcard_city = null, vcard_region = null, vcard_postcode = null, vcard_country = null, vcard_url = null;
        //vcard ends
        String sms_number = null, sms_body = null;
        //Email
        String email_message = null, email_to = null, email_subject = null;
        //barcode
        String barcode = null;
        //GeoLocation
        String geo_address = null, geo_longitude = null, geo_latitude = null;
        //Apllication
        String app_url = null;
        //Wifi
        String wifi_ssid = null, wifi_type = null, wifi_pass = null;
        //event
        String event_title = null, event_description = null, event_location = null;
        ////////////Variables end/////////////
        ScanResultDetail mScanResultDetail;
        if (input_type.equals("Text")) {
            if (!binding.textQrCreatorInput.getText().toString().isEmpty()) {
                text_text = binding.textQrCreatorInput.getText().toString();
                generateQR(input_type, text_text);
            } else {
                binding.textQrCreatorInputOutline.setErrorEnabled(true);
                binding.textQrCreatorInputOutline.setError("Required");
            }
        } else if (input_type.equals("Clipboard")) {
            if (!binding.clipboardQrCreatorInput.getText().toString().isEmpty()) {
                clipboard_text = binding.clipboardQrCreatorInput.getText().toString();
                generateQR(input_type, clipboard_text);
            } else {
                binding.clipboardQrCreatorInputOutline.setErrorEnabled(true);
                binding.clipboardQrCreatorInputOutline.setError("Required");
            }

        } else if (input_type.equals("URL")) {
            if (!binding.urlQrCreatorInput.getText().toString().isEmpty()) {
                url_text = binding.urlQrCreatorInput.getText().toString();
                generateQR(input_type, url_text);
            } else {
                binding.urlQrCreatorInputOutline.setErrorEnabled(true);
                binding.urlQrCreatorInputOutline.setError("Required");
            }
        } else if (input_type.equals("Phone")) {
            if (!binding.phoneQrCreatorInput.getText().toString().isEmpty()) {
                phone = "TEL:" + binding.phoneQrCreatorInput.getText().toString();
                generateQR(input_type, phone);
            } else {
                binding.phoneQrCreatorInputOutline.setErrorEnabled(true);
                binding.phoneQrCreatorInputOutline.setError("Required");
            }
        } else if (input_type.equals("Contact/Vcard")) {
            if (binding.vcardNameQrCreatorInput.getText().toString().isEmpty()) {
                binding.vcardNameQrCreatorInputOutline.setErrorEnabled(true);
                binding.vcardNameQrCreatorInputOutline.setError("Required");
            } else if (binding.vcardCompanyQrCreatorInput.getText().toString().isEmpty()) {
                binding.vcardCompanyQrCreatorInputOutline.setErrorEnabled(true);
                binding.vcardCompanyQrCreatorInputOutline.setError("Required");
            } else if (binding.vcardEmailQrCreatorInput.getText().toString().isEmpty()) {
                binding.vcardEmailQrCreatorInputOutline.setErrorEnabled(true);
                binding.vcardEmailQrCreatorInputOutline.setError("Required");
            } else if (binding.vcardPhoneQrCreatorInput.getText().toString().isEmpty()) {
                binding.vcardPhoneQrCreatorInputOutline.setErrorEnabled(true);
                binding.vcardPhoneQrCreatorInputOutline.setError("Required");
            } else if (binding.vcardCellQrCreatorInput.getText().toString().isEmpty()) {
                binding.vcardCellQrCreatorInputOutline.setErrorEnabled(true);
                binding.vcardCellQrCreatorInputOutline.setError("Required");
            } else if (binding.vcardFaxQrCreatorInput.getText().toString().isEmpty()) {
                binding.vcardFaxQrCreatorInputOutline.setErrorEnabled(true);
                binding.vcardFaxQrCreatorInputOutline.setError("Required");
            } else if (binding.vcardCityQrCreatorInput.getText().toString().isEmpty()) {
                binding.vcardCityQrCreatorInputOutline.setErrorEnabled(true);
                binding.vcardCityQrCreatorInputOutline.setError("Required");
            } else if (binding.vcardRegionQrCreatorInput.getText().toString().isEmpty()) {
                binding.vcardRegionQrCreatorInputOutline.setErrorEnabled(true);
                binding.vcardRegionQrCreatorInputOutline.setError("Required");
            } else if (binding.vcardPostQrCreatorInput.getText().toString().isEmpty()) {
                binding.vcardPostQrCreatorInputOutline.setErrorEnabled(true);
                binding.vcardPostQrCreatorInputOutline.setError("Required");
            } else if (binding.vcardCountryQrCreatorInput.getText().toString().isEmpty()) {
                binding.vcardCountryQrCreatorInputOutline.setErrorEnabled(true);
                binding.vcardCountryQrCreatorInputOutline.setError("Required");
            } else if (binding.vcardUrlQrCreatorInput.getText().toString().isEmpty()) {
                binding.vcardUrlQrCreatorInputOutline.setErrorEnabled(true);
                binding.vcardUrlQrCreatorInputOutline.setError("Required");
            } else {
                vcard_name = binding.vcardNameQrCreatorInput.getText().toString();
                vcard_organization = binding.vcardCompanyQrCreatorInput.getText().toString();
                vcard_email = binding.vcardEmailQrCreatorInput.getText().toString();
                vcard_phone = binding.vcardPhoneQrCreatorInput.getText().toString();
                vcard_street = binding.vcardStreetQrCreatorInput.getText().toString();
                vcard_cell = binding.vcardCellQrCreatorInput.getText().toString();
                vcard_fax = binding.vcardFaxQrCreatorInput.getText().toString();
                vcard_city = binding.vcardCityQrCreatorInput.getText().toString();
                vcard_region = binding.vcardRegionQrCreatorInput.getText().toString();
                vcard_postcode = binding.vcardPostQrCreatorInput.getText().toString();
                vcard_country = binding.vcardCountryQrCreatorInput.getText().toString();
                vcard_url = binding.vcardUrlQrCreatorInput.getText().toString();

                String comAddress = "ADD:" + vcard_street + "," + vcard_city + "," + vcard_region + "," + vcard_postcode + "," + vcard_country;
                data = "";
                data = "BEGIN:VCARD:\n" + "FN:" + vcard_name + "\n" + "ORG:" + vcard_organization + "\n" + "Email:" + vcard_email + "\n" +
                        "URL:" + vcard_url + "\n" +
                        "Phone" + ":" + vcard_phone + "\n" + "Cell:" + vcard_cell + "\n" + "Fax:" + vcard_fax + "\n" +
                        comAddress + "\n" + "END:VCARD";

                generateQR(input_type, data);
            }


        } else if (input_type.equals("SMS")) {


            if (binding.smsNumberQrCreatorInput.getText().toString().isEmpty()) {
                binding.smsNumberQrCreatorInputOutline.setErrorEnabled(true);
                binding.smsNumberQrCreatorInputOutline.setError("Required");
            } else if (binding.smsMessageQrCreatorInput.getText().toString().isEmpty()) {
                binding.smsMessageQrCreatorInputOutline.setErrorEnabled(true);
                binding.smsMessageQrCreatorInputOutline.setError("Required");
            } else {
                sms_number = binding.smsNumberQrCreatorInput.getText().toString();
                sms_body = binding.smsMessageQrCreatorInput.getText().toString();
                data = "";
                data = "SMSTO:" + sms_number + ":" + sms_body;
                generateQR(input_type, data);
            }


        } else if (input_type.equals("Email")) {
            if (binding.emailAddresQrCreatorInput.getText().toString().isEmpty()) {
                binding.emailAddresQrCreatorInputOutline.setErrorEnabled(true);
                binding.emailAddresQrCreatorInputOutline.setError("Required");
            } else if (binding.emailSubjectQrCreatorInput.getText().toString().isEmpty()) {
                binding.emailSubjectQrCreatorInputOutline.setErrorEnabled(true);
                binding.emailSubjectQrCreatorInputOutline.setError("Required");
            } else if (binding.emailTextQrCreatorInput.getText().toString().isEmpty()) {
                binding.emailTextQrCreatorInputOutline.setErrorEnabled(true);
                binding.emailTextQrCreatorInputOutline.setError("Required");
            } else {
                email_to = binding.emailAddresQrCreatorInput.getText().toString();
                email_subject = binding.emailSubjectQrCreatorInput.getText().toString();
                email_message = binding.emailTextQrCreatorInput.getText().toString();
                data = "";
                data = "MATMSG:" + "TO:" + email_to + ":" + "SUB:" + email_subject + ":" + "BODY:" + email_message;
                generateQR(input_type, data);
            }

        } else if (input_type.equals("Barcode")) {
            if (binding.barcodeNumberQrCreatorInput.getText().toString().isEmpty()) {
                binding.barcodeNumberQrCreatorInputOutline.setErrorEnabled(true);
                binding.barcodeNumberQrCreatorInputOutline.setError("Required");
            } else {
                barcode = binding.barcodeNumberQrCreatorInput.getText().toString();
                genrateBarcode(input_type, barcode);
            }

        } else if (input_type.equals("GeoLocation")) {
            if (binding.geolocationAddressQrCreatorInput.getText().toString().isEmpty()) {
                binding.geolocationAddressQrCreatorInputOutline.setErrorEnabled(true);
                binding.geolocationAddressQrCreatorInputOutline.setError("Required");
            } else if (binding.geolocationLatitudeQrCreatorInput.getText().toString().isEmpty()) {
                binding.geolocationLatitudeQrCreatorInputOutline.setErrorEnabled(true);
                binding.geolocationLatitudeQrCreatorInputOutline.setError("Required");
            } else if (binding.geolocationLongitudeQrCreatorInput.getText().toString().isEmpty()) {
                binding.geolocationLongitudeQrCreatorInputOutline.setErrorEnabled(true);
                binding.geolocationLongitudeQrCreatorInputOutline.setError("Required");
            } else {
                geo_address = binding.geolocationAddressQrCreatorInput.getText().toString();
                geo_latitude = binding.geolocationLatitudeQrCreatorInput.getText().toString();
                geo_longitude = binding.geolocationLongitudeQrCreatorInput.getText().toString();
                data = "GEO:" + geo_latitude + "," + geo_longitude;
                generateQR(input_type, data);

            }


        } else if (input_type.equals("Application")) {
            if (binding.applicationUrlQrCreatorInput.getText().toString().isEmpty()) {
                binding.applicationUrlQrCreatorInputOutline.setErrorEnabled(true);
                binding.applicationUrlQrCreatorInputOutline.setError("Required");
            } else {
                app_url = binding.applicationUrlQrCreatorInput.getText().toString();
                generateQR(input_type, app_url);
            }

        } else if (input_type.equals("Wi-Fi")) {
            if (binding.wifiPasswordQrCreatorInput.getText().toString().isEmpty()) {
                binding.wifiPasswordQrCreatorInputOutline.setErrorEnabled(true);
                binding.wifiPasswordQrCreatorInputOutline.setError("Required");
            } else if (binding.wifiSsidQrCreatorInput.getText().toString().isEmpty()) {
                binding.wifiSsidQrCreatorInputOutline.setErrorEnabled(true);
                binding.wifiSsidQrCreatorInputOutline.setError("Required");
            } else {
                wifi_pass = binding.wifiPasswordQrCreatorInput.getText().toString();
                wifi_ssid = binding.wifiSsidQrCreatorInput.getText().toString();
                wifi_type = binding.wifiSpinnerQrCreatorInput.getSelectedItem().toString();
                Toast.makeText(getContext(), "" + wifi_type, Toast.LENGTH_SHORT).show();
                data = "WIFI:" + "T:" + wifi_type + ":" + "S:" + wifi_ssid + ":" + "P:" + wifi_pass;
                generateQR(input_type, data);

            }

        } else if (input_type.equals("Event")) {
            if (binding.eventTitleQrCreatorInput.getText().toString().isEmpty()) {
                binding.eventTitleQrCreatorInputOutline.setErrorEnabled(true);
                binding.eventTitleQrCreatorInputOutline.setError("Required");
            } else if (binding.eventLocationQrCreatorInput.getText().toString().isEmpty()) {
                binding.eventLocationQrCreatorInputOutline.setErrorEnabled(true);
                binding.eventLocationQrCreatorInputOutline.setError("Required");
            } else if (binding.eventStartdateQrCreatorInput.getText().toString().isEmpty()) {
                binding.eventStartdateQrCreatorInputOutline.setErrorEnabled(true);
                binding.eventStartdateQrCreatorInputOutline.setError("Required");
            } else if (binding.eventEnddateQrCreatorInput.getText().toString().isEmpty()) {
                binding.eventEnddateQrCreatorInputOutline.setErrorEnabled(true);
                binding.eventEnddateQrCreatorInputOutline.setError("Required");
            } else {
                event_title = binding.eventTitleQrCreatorInput.getText().toString();
                event_location = binding.eventLocationQrCreatorInput.getText().toString();

                data = "";
                data = "BEGIN:VEVENT:\nSUMMARY:" + event_title + "\nLOCATION:" + event_location +
                        "\nDTSTART:" + event_startdate + "\nDTEND:" + event_enddate + "\nEND:VEVENT";

                generateQR(input_type, data);
            }
        }
    }


    //generating barcode
    public void genrateBarcode(String input_type, String data) {
        try {
            String productId = data;
            Hashtable<EncodeHintType, ErrorCorrectionLevel> hintMap = new Hashtable<EncodeHintType, ErrorCorrectionLevel>();
            hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);
            Writer codeWriter;
            codeWriter = new Code128Writer();
            BitMatrix byteMatrix = codeWriter.encode(productId, BarcodeFormat.CODE_128, 400, 200, hintMap);
            int width = byteMatrix.getWidth();
            int height = byteMatrix.getHeight();
            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            for (int i = 0; i < width; i++) {
                for (int j = 0; j < height; j++) {
                    bitmap.setPixel(i, j, byteMatrix.get(i, j) ? Color.BLACK : Color.WHITE);
                }
            }
            byte[] byteArray = convertBitmaptoArray(bitmap);

            ///inserting in database
            final int min = 20;
            final int max = 200000;
            final int random = new Random().nextInt((max - min) + 1) + min;
            ScanResultDetail mScanResultDetail;
            mScanResultDetail = new ScanResultDetail("" + input_type + random, false, "" + input_type, "created", getCurrentDate(), "" + data,false);
            mHistoryViewModel.insert(mScanResultDetail);
            tranferImage(bitmap, data, "Barcode");
        } catch (Exception e) {
            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }


    ///Genrating qr
    public void generateQR(String input_type, String data) {
        Bitmap bitmap;
        int width = 0;
        int height = 0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            WindowMetrics windowMetrics = getActivity().getWindowManager().getCurrentWindowMetrics();
            Insets insets = windowMetrics.getWindowInsets()
                    .getInsetsIgnoringVisibility(WindowInsets.Type.systemBars());
            width = windowMetrics.getBounds().width() - insets.left - insets.right;
            height = windowMetrics.getBounds().height() - insets.top - insets.bottom;
        } else {
            DisplayMetrics displayMetrics = new DisplayMetrics();
            getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            width = displayMetrics.widthPixels;
            height = displayMetrics.heightPixels;
        }
        ;
        int smallerDimension = width < height ? width : height;
        smallerDimension = smallerDimension * 3 / 4;

        QRGEncoder qrgEncoder;
        if (input_type.equalsIgnoreCase("EMAIL")) {
            qrgEncoder = new QRGEncoder(data, null, QRGContents.Type.EMAIL, smallerDimension);
        }
        if (input_type.equalsIgnoreCase("Phone")) {
            qrgEncoder = new QRGEncoder(data, null, QRGContents.Type.PHONE, smallerDimension);
        } else {
            qrgEncoder = new QRGEncoder(data, null, QRGContents.Type.TEXT, smallerDimension);
        }

        ///

        try {
            bitmap = qrgEncoder.encodeAsBitmap();
            tranferImage(bitmap, data, "QR");

            byte[] byteArray = convertBitmaptoArray(bitmap);
            ///inserting in database
            ScanResultDetail mScanResultDetail;
            final int min = 20;
            final int max = 200000;
            final int random = new Random().nextInt((max - min) + 1) + min;
            mScanResultDetail = new ScanResultDetail("" + input_type + random, false, "" + input_type, "created", getCurrentDate(), "" + data,false);
            mHistoryViewModel.insert(mScanResultDetail);

        } catch (WriterException e) {
            Toast.makeText(getActivity(), "Creation Failed", Toast.LENGTH_SHORT).show();
        }
    }


    //////

    byte[] convertBitmaptoArray(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        return byteArray;
    }


    //Transfer image to next fragment
    public void tranferImage(Bitmap bitmap, String input, String format) {
        //convert bitmap to array
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        /////
        Fragment frag = new DisplayDetailFragment();
        Bundle arguments = new Bundle();
        arguments.putByteArray("bitmap", byteArray);
        arguments.putString("scanResult", "" + input);
        arguments.putString("format", "" + format);
        arguments.putBoolean("inserttodatabase", true);
        frag.setArguments(arguments);
        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragmentView, frag).addToBackStack(null);
        ft.commit();
        ////////////////////////
    }

    ////////


    //
    public void getLocation() {
        Toast.makeText(getContext(), "Getting current location...", Toast.LENGTH_SHORT).show();
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(3000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

        }
        LocationServices.getFusedLocationProviderClient(getActivity()).requestLocationUpdates(
                locationRequest, new LocationCallback() {
                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        super.onLocationResult(locationResult);
                        LocationServices.getFusedLocationProviderClient(getActivity())
                                .removeLocationUpdates(this);
                        if (locationResult != null && locationResult.getLocations().size() > 0) {
                            int latestLocationIndex = locationResult.getLocations().size() - 1;
                            logitude = locationResult.getLocations().get(latestLocationIndex).getLongitude();
                            latitude = locationResult.getLocations().get(latestLocationIndex).getLatitude();

                            binding.geolocationLongitudeQrCreatorInput.setText("" + logitude);
                            binding.geolocationLatitudeQrCreatorInput.setText("" + latitude);
                            List<Address> addressList = null;
                            Geocoder geocoder = new Geocoder(getContext());
                            try {
                                addressList = geocoder.getFromLocation(latitude, logitude, 1);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            //
                            if (addressList != null) {
                                String add = addressList.get(0).getAddressLine(0);
                                binding.geolocationAddressQrCreatorInput.setText(add);
                            } else {
                                Toast.makeText(getContext(), "Enter location name...", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }, Looper.getMainLooper());

    }

    //////////
    public void searchLocation() {
        Toast.makeText(getContext(), "Searching place....", Toast.LENGTH_SHORT).show();

        String location = binding.geolocationAddressQrCreatorInput.getText().toString();
        if (location != null) {
            List<Address> addressList = null;

            if (location != null || !location.equals("")) {
                Geocoder geocoder = new Geocoder(getContext());
                try {

                    addressList = geocoder.getFromLocationName(location, 1);

                    if (addressList.size() != 0) {

                        Address address = addressList.get(0);
                        if (address != null) {
                            latitude = address.getLatitude();
                            logitude = address.getLongitude();

                            //
                            String add = addressList.get(0).getAddressLine(0);
                            binding.geolocationAddressQrCreatorInput.setText(add);
                            binding.geolocationLatitudeQrCreatorInput.setText("" + latitude);
                            binding.geolocationLongitudeQrCreatorInput.setText("" + logitude);
                        }
                    }
                    else{
                        Toast.makeText(getActivity(), "Please put the Valid Location", Toast.LENGTH_SHORT).show();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);
        switch (reqCode) {
            //phone and sms
            case (1111):
                if (resultCode == Activity.RESULT_OK) {
                    Uri contactData = data.getData();
                    ContentResolver contentResolver = getActivity().getContentResolver();
                    Cursor c = contentResolver.query(contactData, null, null, null, null);

                    if (c.moveToFirst()) {
                        String phone_num = c.getString(c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        binding.phoneQrCreatorInput.setText(phone_num);
                    }
                }
                break;
            //vcard
            case (1112):
                if (resultCode == Activity.RESULT_OK) {
                    Uri contactData = data.getData();
                    ContentResolver contentResolver = getActivity().getContentResolver();
                    Cursor c = contentResolver.query(contactData, null, null, null, null);

                    if (c.moveToFirst()) {
                        String phone_num = c.getString(c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        String name = c.getString(c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                        binding.vcardPhoneQrCreatorInput.setText(phone_num);
                        binding.vcardNameQrCreatorInput.setText(name);
                    }
                }
                break;
            case (1113):
                if (resultCode == Activity.RESULT_OK) {
                    Uri contactData = data.getData();
                    ContentResolver contentResolver = getActivity().getContentResolver();
                    Cursor c = contentResolver.query(contactData, null, null, null, null);

                    if (c.moveToFirst()) {
                        String phone_num = c.getString(c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        binding.smsNumberQrCreatorInput.setText(phone_num);
                        ;
                    }
                }
                break;

        }
    }

    public void viewHandling(String whichCreator) {
        if (whichCreator.equals("Text")) {
            binding.textCreatorView.setVisibility(View.VISIBLE);
        } else if (whichCreator.equals("Clipboard")) {
            binding.clipboardCreatorView.setVisibility(View.VISIBLE);
        } else if (whichCreator.equals("URL")) {
            binding.urlCreatorView.setVisibility(View.VISIBLE);
        } else if (whichCreator.equals("Phone")) {
            binding.phoneCreatorView.setVisibility(View.VISIBLE);
        } else if (whichCreator.equals("Contact/Vcard")) {
            binding.vcardCreatorView.setVisibility(View.VISIBLE);
        } else if (whichCreator.equals("SMS")) {
            binding.smsCreatorView.setVisibility(View.VISIBLE);
        } else if (whichCreator.equals("Email")) {
            binding.emailCreatorView.setVisibility(View.VISIBLE);
        } else if (whichCreator.equals("Barcode")) {
            binding.barcodeCreatorView.setVisibility(View.VISIBLE);
        } else if (whichCreator.equals("GeoLocation")) {
            binding.geolocationCreatorView.setVisibility(View.VISIBLE);
        } else if (whichCreator.equals("Application")) {
            binding.applicationCreatorView.setVisibility(View.VISIBLE);
        } else if (whichCreator.equals("Wi-Fi")) {
            binding.wifiCreatorView.setVisibility(View.VISIBLE);
        } else if (whichCreator.equals("Event")) {
            binding.eventCreatorView.setVisibility(View.VISIBLE);
        }

    }


    private void addContact(int code) {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
        startActivityForResult(intent, code);

    }

    private ArrayList<ModelClass> getPackages() {
        Toast.makeText(getContext(), "Fetching Installed apps....", Toast.LENGTH_SHORT).show();


        ArrayList<ModelClass> list = new ArrayList<>();
        PackageManager p = getActivity().getPackageManager();
        final List<PackageInfo> appinstall =
                p.getInstalledPackages(PackageManager.GET_PERMISSIONS |
                        PackageManager.GET_PROVIDERS);

        for (int i = 0; i < appinstall.size(); i++) {
            PackageInfo packageInfo = appinstall.get(i);
            String appName = packageInfo.applicationInfo.loadLabel(getActivity().getPackageManager()).toString();
            String url = "https://play.google.com/store/apps/" + packageInfo.packageName;
            appName = appName.replace("\n", "").replace("\r", "");
            list.add(new ModelClass(appName, packageInfo.applicationInfo.loadIcon(getActivity().getPackageManager()), "" + url));

        }

        hideProgress();
        return list;
    }


    private void scanWifi() {
        Toast.makeText(getContext(), "Fetching configured networks...", Toast.LENGTH_SHORT).show();
        arrayList.clear();
        getActivity().registerReceiver(wifiReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));

        wifiManager.startScan();
    }

    BroadcastReceiver wifiReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                return;
            }
            results = wifiManager.getConfiguredNetworks();
            getActivity().unregisterReceiver(this);

            Drawable drawable = getResources().getDrawable(R.drawable.wifi_icon);
            for (WifiConfiguration scanResult : results) {
                arrayList.add(new ModelClass(scanResult.SSID, drawable, ""));
            }
            adapter = new CustomeAdapter(getContext(), R.layout.item_view_for_custome_adapter, arrayList);
            adapter.notifyDataSetChanged();
            new MaterialAlertDialogBuilder(getContext()).
                    setTitle("Choose Wifi").
                    setAdapter(adapter, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            binding.wifiSsidQrCreatorInput.setText(arrayList.get(i).getName());
                        }
                    }).show();
        }


    };


    ///
    public static String getCurrentDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy-hh:mm aaa");
        return sdf.format(new Date());
    }


    //show Progress
    public void hideProgress() {
        binding.progress.setVisibility(View.GONE);
    }

    public void showProgress() {
        binding.progress.setVisibility(View.VISIBLE);
    }
}