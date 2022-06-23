package com.example.qrscannerappzl.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Insets;
import android.os.Build;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.os.Environment;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowInsets;
import android.view.WindowMetrics;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.qrscannerappzl.R;

import com.example.qrscannerappzl.databinding.FragmentDisplayDetailBinding;
import com.example.qrscannerappzl.util.CheckSettingPrefernces;
import com.example.qrscannerappzl.history.HistoryViewModel;

import com.example.qrscannerappzl.util.Methods;

import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdOptionsView;
import com.facebook.ads.MediaView;
import com.facebook.ads.NativeAd;
import com.facebook.ads.NativeAdLayout;
import com.facebook.ads.NativeAdListener;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.Writer;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.oned.Code128Writer;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;

import java.text.SimpleDateFormat;

import java.util.ArrayList;
import java.util.Date;

import java.util.Hashtable;
import java.util.List;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;

public class DisplayDetailFragment extends Fragment {
    ////////////////////////Fileds variables

    String online_store_url=null;
    //String variables for fields
    String type_of_qr = null;
    String email = null;
    String subject = null;
    String body = null;
    String sms_phone = null;
    String sms_body = null;

    String wifi = null;
    String wifi_type = null;
    String wifi_pass = null;

    String geoLocation = null;
    String latitude = null;
    String longitude = null;
    String geo_url = null;
    //

    String simple_url = null;
    String event_location = null;
    String event_summary = null;
    String eventstart_date = null;
    String eventend_date = null;
    String eventEndResult = null;
    String vcard_name = null;
    String vcard_organization = null;
    String vcard_cell = null;
    String vcard_phone = null;
    String vcard_email = null;
    String vcard_fax = null;
    String vcard_url = null;
    String vcard_address = null;
    String vcardEndResult = null;
    ///Barcode
    String barcode = null;
    //tEXT
    String text = null;
    /////////////////////////////////////Fileds variables end

    FragmentDisplayDetailBinding binding;
    String scan_result_string, scan_format;
    Bitmap scannedImageBitmap;
    HistoryViewModel mHistoryViewModel;

    //////////////////

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
        binding = FragmentDisplayDetailBinding.inflate(inflater, container, false);
        View v = binding.getRoot();
        mHistoryViewModel = new ViewModelProvider(this).get(HistoryViewModel.class);


        loadNativeAd(v);

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            scan_result_string = bundle.getString("scanResult");
            scan_format = bundle.getString("format");
        }
        scannedImageBitmap = generateQR(scan_result_string);


        binding.scanFormat.setText(scan_format);


        binding.saveQrBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveToGallery(scannedImageBitmap);
            }
        });

        binding.shareQr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                share(scan_result_string);
            }
        });

        ValidateScanResult(scan_result_string);
        if (type_of_qr.equalsIgnoreCase("barcode")){
            binding.qrImage.setImageBitmap(genrateBarcode(scan_result_string));
        }else {
            binding.qrImage.setImageBitmap(scannedImageBitmap);
        }

        ////////////////////////////////////////////Click Listners for Horizontals items////////////////////

        //copy
        binding.copyHorizontalLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CheckSettingPrefernces.getClipboardState(getContext())) {
                    if (type_of_qr.equals("Email"))
                        Methods.copyToClipboard(email + "\n" + subject + "\n" + body, getActivity());
                    else if (type_of_qr.equals("SMS"))
                        Methods.copyToClipboard(sms_phone + "\n" + sms_body, getActivity());
                    else if (type_of_qr.equals("GeoLocation"))
                        Methods.copyToClipboard(geo_url, getContext());
                    else if (type_of_qr.equals("URL"))
                        Methods.copyToClipboard(simple_url, getContext());
                    else if (type_of_qr.equals("Barcode"))
                        Methods.copyToClipboard(barcode, getContext());
                    else if (type_of_qr.equals("Text"))
                        Methods.copyToClipboard(text, getContext());
                    else if (type_of_qr.equals("Vcard"))
                        Methods.copyToClipboard(vcardEndResult, getContext());
                    Toast.makeText(getContext(), "Coppied", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //Share
        binding.shareHorizontalLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (type_of_qr.equals("Email"))
                    Methods.share(email + "\n" + subject + "\n" + body, getActivity());
                else if (type_of_qr.equals("SMS"))
                    Methods.share(sms_phone + "\n" + sms_body, getActivity());
                else if (type_of_qr.equals("GeoLocation"))
                    Methods.share(geo_url, getContext());
                else if (type_of_qr.equals("URL"))
                    Methods.share(simple_url, getContext());
                else if (type_of_qr.equals("Barcode"))
                    Methods.share(barcode, getContext());
                else if (type_of_qr.equals("Text"))
                    Methods.share(text, getContext());
                else if (type_of_qr.equals("Vcard"))
                    Methods.share(vcardEndResult, getContext());
            }
        });
        //Email
        binding.emailHorizontalLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (email != null) {
                    Methods.composeEmail("" + email, getContext());
                } else if (vcard_email != null) {
                    Methods.composeEmail("" + vcard_email, getContext());
                }
            }
        });
        ///SMS
        binding.addContactHorizontalLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sms_phone != null) {
                    Methods.insertContactPhoneNumber(sms_phone, getContext());
                } else if (vcard_phone != null || vcard_cell != null) {
                    if (vcard_cell != null)
                        Methods.insertContactPhoneNumber("" + vcard_phone, vcard_name, "" + vcard_email,
                                "" + vcard_organization, getContext());
                    else if (vcard_phone != null)
                        Methods.insertContactPhoneNumber("" + vcard_phone, vcard_name, "" + vcard_email,
                                "" + vcard_organization, getContext());
                }

            }
        });
        binding.sendSmsHorizontalLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sms_phone != null)
                    Methods.sendSMS("" + sms_phone, sms_body, getContext());
                else if (vcard_phone != null) {
                    Methods.sendSMS("" + vcard_phone, "", getContext());
                }
            }
        });
        binding.smsCallHorizontalLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sms_phone != null)
                    Methods.call(sms_phone, getContext());
                else if (vcard_phone != null) {
                    Methods.call(vcard_phone, getContext());
                }
            }
        });
        ////////////GEO LOCATION
        binding.geoLocationHorizontalLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Methods.openLocation(geo_url, getContext());
            }
        });

        ///////////URL
        binding.urlHorizontalLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (simple_url != null)
                    Methods.openUrl(simple_url, getContext());
                if (vcard_url != null) {
                    Methods.openUrl(vcard_url, getContext());
                }
            }
        });

        ////Event
        binding.directionHorizontalLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (event_location != null)
                    Methods.showDirection(event_location, getContext());
                else if (vcard_address != null)
                    Methods.showDirection(vcard_address, getContext());
            }
        });
        binding.showLocationHorizontalLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (event_location != null)
                    Methods.showLocation(event_location, getContext());
                else if (vcard_address != null)
                    Methods.showLocation(vcard_address, getContext());
            }
        });
        //


        ////EVENT
        binding.addEventHorizontalLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Methods.addEvent("" + event_summary, event_location, Methods.extractDateString(eventstart_date), Methods.extractDateString(eventend_date), getContext());
            }
        });

        //amazon
        binding.amazonHorizontalLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (barcode!=null)
                    Methods.openUrl("https://www.amazon.com/s?k="+barcode,getContext());
                else if (simple_url!=null)
                    Methods.openUrl(""+simple_url,getContext());
            }
        });

        //walmart
        binding.walmartHorizontalLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (barcode!=null)
                    Methods.openUrl("https://www.flipkart.com/search/?query="+barcode,getContext());
                else if (simple_url!=null)
                    Methods.openUrl(""+simple_url,getContext());
            }
        });

        //ebay
        binding.ebayHorizontalLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (barcode!=null)
                    Methods.openUrl("https://www.ebay.com/sch/i.html?_nkw="+barcode,getContext());
                else if (simple_url!=null)
                    Methods.openUrl(""+simple_url,getContext());
            }
        });



        ///////////Horizontal scroolview code
        setIconWithRespectToTheme();

        return v;
    }



    //bitmap conversion method
    byte[] convertBitmaptoArray(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        return byteArray;
    }


    public void ValidateScanResult(String scan_result) {

        //checking auto url option is enable or not
        if (CheckSettingPrefernces.getAutoUrlOpenState(getContext())) {
            if (scan_result.contains("http://maps.google.com/")) {
                type_of_qr = "Location";
                Methods.openLocation(scan_result, getContext());
            } else if (scan_result.startsWith("http")) {
                type_of_qr = "URL";
                Methods.openUrl(scan_result, getContext());
            }
        }
        ////Email
        if (scan_result.startsWith("MATMSG") && type_of_qr == null) {
            type_of_qr = "Email";
            String[] typeAndValue = scan_result.split("[:;]", 10);
            email = typeAndValue[2];
            subject = typeAndValue[4];
            body = typeAndValue[6];
            binding.scanningResult.setText("Email To :" + email + "\n" + "Subject : " + subject + "\nMessage Body : " + body);
            binding.scanResultType.setText("EMAIL");
        }
        //SMS
        else if (scan_result.startsWith("SMSTO")) {
            type_of_qr = "SMS";
            String[] typeAndValue = scan_result.split("[:;]", 10);
            sms_phone = typeAndValue[1];
            sms_body = typeAndValue[2];
            binding.scanningResult.setText("Phone Number :" + sms_phone + "\n" + "Message : " + sms_body);
            binding.scanResultType.setText("SMS");
        }
        //VCARD
        else if (scan_result.toUpperCase().startsWith("BEGIN:VCARD")) {

            type_of_qr = "Vcard";

            String[] tokens = scan_result.split("\n");
            String data = "";

            for (int i = 0; i < tokens.length; i++) {

                if (tokens[i].startsWith("N")) {
                    vcard_name = tokens[i].substring("N:".length());
                }
                if (tokens[i].startsWith("FN")) {
                    vcard_name = tokens[i].substring("FN:".length());
                }
                if (tokens[i].startsWith("URL")) {
                    vcard_url = Methods.extractURL(tokens[i]);
                }
                if (tokens[i].startsWith("ORG")) {
                    vcard_organization = tokens[i].substring("ORG:".length());
                }
                if (tokens[i].toUpperCase().startsWith("EMAIL")) {
                    vcard_email = Methods.extractEmail(tokens[i]);
                }
                if (tokens[i].toUpperCase().startsWith("TEL")) {

                    if (tokens[i].toUpperCase().startsWith("TEL")) {
                        vcard_phone = Methods.extractPhone(tokens[i]);
                    }
                    if (tokens[i].contains("work")) {
                        vcard_phone = Methods.extractPhone(tokens[i]);
                    }
                    if (tokens[i].toUpperCase().contains("CELL")) {
                        vcard_cell = Methods.extractPhone(tokens[i]);
                    } else if (tokens[i].toUpperCase().contains("CELL")) {
                        vcard_cell = Methods.extractPhone(tokens[i]);
                    } else if (tokens[i].toUpperCase().contains("CELL")) {
                        vcard_cell = Methods.extractPhone(tokens[i]);
                    } else if (tokens[i].toUpperCase().contains("CELL")) {
                        vcard_cell = Methods.extractPhone(tokens[i]);
                    }

                    ////////////////
                    if (tokens[i].startsWith("TEL;WORK;VOICE:")) {
                        vcard_phone = Methods.extractPhone(tokens[i]);
                    } else if (tokens[i].startsWith("TEL;HOME;VOICE:")) {
                        vcard_phone = Methods.extractPhone(tokens[i]);
                    } else if (tokens[i].startsWith("TEL;TYPE=WORK,VOICE:")) {
                        vcard_phone = Methods.extractPhone(tokens[i]);
                    } else if (tokens[i].startsWith("TEL;TYPE=HOME,VOICE:")) {
                        vcard_phone = Methods.extractPhone(tokens[i]);
                    } else if (tokens[i].startsWith("TEL;TYPE=work,voice;VALUE=uri:")) {
                        vcard_phone = Methods.extractPhone(tokens[i]);
                    } else if (tokens[i].startsWith("TEL;TYPE=home,voice;VALUE=uri:")) {
                        vcard_phone = Methods.extractPhone(tokens[i]);
                    }
                    //////////

                    ////////////////
                    if (tokens[i].toUpperCase().contains("FAX")) {
                        vcard_fax = Methods.extractPhone(tokens[i]);
                    } else if (tokens[i].toUpperCase().contains("FAX")) {
                        vcard_fax = Methods.extractPhone(tokens[i]);
                    } else if (tokens[i].toUpperCase().contains("FAX")) {
                        vcard_fax = Methods.extractPhone(tokens[i]);
                    } else if (tokens[i].toUpperCase().contains("FAX")) {
                        vcard_fax = Methods.extractPhone(tokens[i]);
                    }
                }
                if (tokens[i].toUpperCase().startsWith("PHONE")) {
                    vcard_phone = tokens[i].substring("PHONE:".length());
                }

                if (tokens[i].toUpperCase().contains("ADR") || tokens[i].toUpperCase().contains("ADD")) {
                    vcard_address = tokens[i].substring("ADD:".length());
                }

                /////////////////Merging vcard result
                vcardEndResult = "";
                if (vcard_name != null) {
                    vcardEndResult = vcardEndResult + "Name : " + vcard_name + "\n";
                }
                if (vcard_phone != null) {
                    vcardEndResult = vcardEndResult + "Phone : " + vcard_phone + "\n";
                }
                if (vcard_organization != null) {
                    vcardEndResult = vcardEndResult + "Organization : " + vcard_organization + "\n";
                }
                if (vcard_email != null) {
                    vcardEndResult = vcardEndResult + "Email : " + vcard_email + "\n";
                }
                if (vcard_url != null) {
                    vcardEndResult = vcardEndResult + "URL : " + vcard_url + "\n";
                }
                if (vcard_fax != null) {
                    vcardEndResult = vcardEndResult + "Fax : " + vcard_fax + "\n";
                }
                if (vcard_cell != null) {
                    vcardEndResult = vcardEndResult + "Cell : " + vcard_cell + "\n";
                }
                if (vcard_address != null) {
                    vcardEndResult = vcardEndResult + "Address : " + vcard_address + "\n";
                }


            }
            binding.scanResultType.setText("Vcard");
            binding.scanningResult.setText(vcardEndResult);
        }

        //VEVENT
        else if (scan_result.toUpperCase().startsWith("BEGIN:VEVENT") || scan_result.toUpperCase().startsWith("BEGIN:VCALENDAR")) {
            type_of_qr = "Vevent";
            String[] Tokens = scan_result.split("\n");
            for (int k = 0; k < Tokens.length; k++) {
                if (Tokens[k].startsWith("SUMMARY")) {
                    event_summary = Tokens[k].substring("SUMMARY:".length());
                }
                if (Tokens[k].startsWith("LOCATION")) {
                    event_location = Tokens[k].substring("LOCATION:".length());
                }
                if (Tokens[k].startsWith("DTSTART")) {
                    eventstart_date = Tokens[k].substring("DTSTART:".length());
                }
                if (Tokens[k].startsWith("DTEND")) {
                    eventend_date = Tokens[k].substring("DTEND:".length());
                }
                eventEndResult = "";
                if (event_summary != null) {
                    eventEndResult = eventEndResult + "Event Name: " + event_summary + "\n";
                }
                if (event_location != null) {
                    eventEndResult = eventEndResult + "Event Location: " + event_location + "\n";
                }
                if (eventstart_date != null) {
                    eventEndResult = eventEndResult + "Event Start Date: " + Methods.extractDateString(eventstart_date) + "\n";
                }
                if (eventend_date != null) {
                    eventEndResult = eventEndResult + "Event End Date: " + Methods.extractDateString(eventend_date) + "\n";
                }

                binding.scanResultType.setText("VEVENT");
                binding.scanningResult.setText(eventEndResult);

            }
        }

        //wifi
        else if (scan_result.startsWith("WIFI")) {

            type_of_qr = "WIFI";
            String[] typeAndValue = scan_result.split("[:;]", 20);
            for (int i = 0; i < typeAndValue.length; i++) {
                if (typeAndValue[i].equalsIgnoreCase("S")) {
                    wifi = typeAndValue[i + 1];
                } else if (typeAndValue[i].equalsIgnoreCase("T")) {
                    wifi_type = typeAndValue[i + 1];
                } else if (typeAndValue[i].equalsIgnoreCase("P")) {
                    wifi_pass = typeAndValue[i + 1];
                }
            }
            binding.scanningResult.setText("SSID : " + wifi + "\nTYPE : " + wifi_type + "\nPassword : " + wifi_pass);
            binding.scanResultType.setText("WiFi");
        }
        //GEO LOCATION
        else if (scan_result.toUpperCase().

                startsWith("GEO") || scan_result.startsWith("http://maps.google.com")) {

            type_of_qr = "GeoLocation";
            if (scan_result.toUpperCase().startsWith("GEO")) {
                String[] typeAndValue = scan_result.split("[:;,]", 20);
                latitude = typeAndValue[1];
                longitude = typeAndValue[2];
                geo_url = "http://maps.google.com/maps?q=" + latitude + "," + longitude;
                binding.scanningResult.setText("Latitude : " + latitude + "\nLongitude : " + longitude);
                binding.scanResultType.setText("GEO LOCATION");
            } else if (scan_result.startsWith("http://maps.google.com")) {
                geo_url = scan_result;
                binding.scanningResult.setText(geo_url);
                binding.scanResultType.setText("GEO LOCATION");
            }

        }

        //url
        else if (scan_result.startsWith("http") && !scan_result.startsWith("http://maps.google.com")) {
            type_of_qr = "URL";
            simple_url = scan_result;
            if (simple_url.toLowerCase().contains("www.walmart.com")){
                binding.walmartHorizontalLayout.setVisibility(View.VISIBLE);
            }
            else if (simple_url.toLowerCase().contains("www.amazon.com")){
                binding.amazonHorizontalLayout.setVisibility(View.VISIBLE);
            }else if (simple_url.toLowerCase().contains("www.ebay.com")){
                binding.ebayHorizontalLayout.setVisibility(View.VISIBLE);
            }
            binding.scanningResult.setText("" + scan_result);
            binding.scanResultType.setText("URL");
        }
        //tel
        else if (scan_result.toUpperCase().

                startsWith("TEL")) {
            type_of_qr = "Phone";
            sms_phone = scan_result;
            binding.scanningResult.setText(scan_result);
            binding.scanResultType.setText("Phone Number");
        }
        //BARCODE
        else if (scan_result.matches("[0-9]+")) {
            type_of_qr = "Barcode";
            barcode = scan_result;
            binding.scanningResult.setText("" + scan_result);
            binding.scanResultType.setText("BAR CODE");
        }
        //TEXT
        else {
            type_of_qr = "Text";
            text = scan_result;
            binding.scanningResult.setText(scan_result);
            binding.scanResultType.setText("TEXT");
        }

        setHorizontalLayoutItemVisibility(type_of_qr);


    }


    public Bitmap generateQR(String data) {
        Bitmap bitmap = null;
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
        int smallerDimension = width < height ? width : height;
        smallerDimension = smallerDimension * 3 / 4;

        QRGEncoder qrgEncoder;

        qrgEncoder = new QRGEncoder(data, null, QRGContents.Type.TEXT, smallerDimension);
        ///
        try {
            bitmap = qrgEncoder.encodeAsBitmap();
        } catch (WriterException e) {
            Toast.makeText(getActivity(), "Creation Failed", Toast.LENGTH_SHORT).show();
        }
        return bitmap;
    }


    //generating barcode
    public Bitmap genrateBarcode(String data) {
        Bitmap bitmap = null;
        try {

            String productId = data;
            Hashtable<EncodeHintType, ErrorCorrectionLevel> hintMap = new Hashtable<EncodeHintType, ErrorCorrectionLevel>();
            hintMap.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);
            Writer codeWriter;
            codeWriter = new Code128Writer();
            BitMatrix byteMatrix = codeWriter.encode(productId, BarcodeFormat.CODE_128, 400, 200, hintMap);
            int width = byteMatrix.getWidth();
            int height = byteMatrix.getHeight();
            bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            for (int i = 0; i < width; i++) {
                for (int j = 0; j < height; j++) {
                    bitmap.setPixel(i, j, byteMatrix.get(i, j) ? Color.BLACK : Color.WHITE);
                }
            }
            byte[] byteArray = convertBitmaptoArray(bitmap);
        } catch (Exception e) {
            Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_LONG).show();
        }
        return bitmap;
    }
    //
    public static String getCurrentDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd G 'at' HH:mm:ss");
        return sdf.format(new Date());
    }



    ////save image
    private void saveToGallery(Bitmap bitmap) {

        String app_folder_path;
        if (android.os.Build.VERSION.SDK_INT >= 29) {
            app_folder_path = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES).toString();
        } else {
            app_folder_path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).toString() + "/Camera";
        }

        FileOutputStream outputStream = null;
        File dir = new File(app_folder_path);
        dir.mkdirs();

        String filename = String.format("%d.png", System.currentTimeMillis());
        File outFile = new File(dir, filename);
        try {
            outputStream = new FileOutputStream(outFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
        try {
            outputStream.flush();
            Toast.makeText(getContext(), "Image Saved", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void share(String mesage) {
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        String shareBody = mesage;
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Share Results");
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
        startActivity(Intent.createChooser(sharingIntent, "Share via"));
    }


    private void setHorizontalLayoutItemVisibility(String type) {

        if (type.equals("Email")) {
            binding.emailHorizontalLayout.setVisibility(View.VISIBLE);
        } else if (type.equals("SMS")) {
            binding.addContactHorizontalLayout.setVisibility(View.VISIBLE);
            binding.sendSmsHorizontalLayout.setVisibility(View.VISIBLE);
            binding.smsCallHorizontalLayout.setVisibility(View.VISIBLE);
        } else if (type_of_qr.equals("GeoLocation")) {
            binding.geoLocationHorizontalLayout.setVisibility(View.VISIBLE);
        } else if (type.equals("URL")) {
            binding.urlHorizontalLayout.setVisibility(View.VISIBLE);
        } else if (type.equals("Phone")) {


            binding.addContactHorizontalLayout.setVisibility(View.VISIBLE);
            binding.smsCallHorizontalLayout.setVisibility(View.VISIBLE);
        } else if (type.equalsIgnoreCase("Vevent")) {
            binding.directionHorizontalLayout.setVisibility(View.VISIBLE);
            binding.showLocationHorizontalLayout.setVisibility(View.VISIBLE);
            binding.addEventHorizontalLayout.setVisibility(View.VISIBLE);
        } else if (type.equalsIgnoreCase("Vcard")) {
            if (vcard_address != null) {
                binding.showLocationHorizontalLayout.setVisibility(View.VISIBLE);
                binding.directionHorizontalLayout.setVisibility(View.VISIBLE);
            }
            if (vcard_phone != null || vcard_cell != null) {
                binding.smsCallHorizontalLayout.setVisibility(View.VISIBLE);
                binding.addContactHorizontalLayout.setVisibility(View.VISIBLE);
                binding.sendSmsHorizontalLayout.setVisibility(View.VISIBLE);
            }
            if (vcard_url != null) {
                binding.urlHorizontalLayout.setVisibility(View.VISIBLE);
            }
            if (vcard_email != null) {
                binding.emailHorizontalLayout.setVisibility(View.VISIBLE);
            }
        }

        if (type.equalsIgnoreCase("Barcode")){
            binding.ebayHorizontalLayout.setVisibility(View.VISIBLE);
            binding.walmartHorizontalLayout.setVisibility(View.VISIBLE);
            binding.amazonHorizontalLayout.setVisibility(View.VISIBLE);
        }
    }



    public void setIconWithRespectToTheme(){
        if (!CheckSettingPrefernces.getMode(getContext())){
            binding.copyHorizontalItemImage.setColorFilter(ContextCompat.getColor(getContext(), R.color.blue), android.graphics.PorterDuff.Mode.SRC_IN);
            binding.shareHorizontalItemImage.setColorFilter(ContextCompat.getColor(getContext(), R.color.blue), android.graphics.PorterDuff.Mode.SRC_IN);
            binding.sendSmsHorizontalItemImage.setColorFilter(ContextCompat.getColor(getContext(), R.color.blue), android.graphics.PorterDuff.Mode.SRC_IN);
            binding.emailHorizontalItemImage.setColorFilter(ContextCompat.getColor(getContext(), R.color.blue), android.graphics.PorterDuff.Mode.SRC_IN);
            binding.addEventHorizontalItemImage.setColorFilter(ContextCompat.getColor(getContext(), R.color.blue), android.graphics.PorterDuff.Mode.SRC_IN);
            binding.smsCallHorizontalItemImage.setColorFilter(ContextCompat.getColor(getContext(), R.color.blue), android.graphics.PorterDuff.Mode.SRC_IN);
            binding.geoLocationHorizontalItemImage.setColorFilter(ContextCompat.getColor(getContext(), R.color.blue), android.graphics.PorterDuff.Mode.SRC_IN);
            binding.showLocationHorizontalItemImage.setColorFilter(ContextCompat.getColor(getContext(), R.color.blue), android.graphics.PorterDuff.Mode.SRC_IN);
            binding.directionHorizontalItemImage.setColorFilter(ContextCompat.getColor(getContext(), R.color.blue), android.graphics.PorterDuff.Mode.SRC_IN);
            binding.ebayHorizontalItemImage.setImageResource(R.drawable.ebay_blue_icon);
            binding.amazonHorizontalItemImage.setImageResource(R.drawable.amazon_blue_icon);
            binding.walmartHorizontalItemImage.setImageResource(R.drawable.flipkart_blue_icon);
            binding.addContactHorizontalItemImage.setColorFilter(ContextCompat.getColor(getContext(), R.color.blue), android.graphics.PorterDuff.Mode.SRC_IN);
            binding.saveQrBtn.setColorFilter(ContextCompat.getColor(getContext(), R.color.blue), android.graphics.PorterDuff.Mode.SRC_IN);
            binding.shareQr.setColorFilter(ContextCompat.getColor(getContext(), R.color.blue), android.graphics.PorterDuff.Mode.SRC_IN);
            binding.urlHorizontalItemImage.setColorFilter(ContextCompat.getColor(getContext(), R.color.blue), android.graphics.PorterDuff.Mode.SRC_IN);
        }
    }



}