package com.example.qrscannerappzl.util;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.provider.CalendarContract;
import android.provider.ContactsContract;
import android.widget.Toast;

import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;

import com.example.qrscannerappzl.history.HistoryViewModel;
import com.example.qrscannerappzl.RoomDatabase.ScanResultDetail;


import java.text.SimpleDateFormat;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Date;
import java.util.Locale;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Methods {

    /////compose email
    public static void composeEmail(String address, Context context) {
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{address});
        emailIntent.setType("text/plain");
        context.startActivity(emailIntent);
    }

    //Copy Text to clipboard
    public static void copyToClipboard(String text, Context context) {

        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("label", text);

        try {
            clipboard.setPrimaryClip(clip);
        }
        catch (Exception e) {
            e.printStackTrace();

        }

    }

    //Share qr text
    public static void share(String mesage, Context context) {

        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        String shareBody = mesage;
        sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Share Results");
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
        context.startActivity(Intent.createChooser(sharingIntent, "Share via"));

    }

    ///insert contact to phone
    public static void insertContactPhoneNumber(String phoneNumber, Context context) {

        Intent contactIntent = new Intent(Intent.ACTION_INSERT);
        contactIntent.setType(ContactsContract.RawContacts.CONTENT_TYPE);
        contactIntent.putExtra(ContactsContract.Intents.Insert.PHONE, phoneNumber);
        if (contactIntent.resolveActivity(context.getPackageManager()) != null) {
            context.startActivity(contactIntent);
        } else {
            Toast.makeText(context, "Failed to start intent", Toast.LENGTH_SHORT).show();
        }

    }

    //////////iNSERT vcard
    public static void insertContactPhoneNumber(String phoneNumber, String name, String email, String company, Context context) {
        Intent contactIntent = new Intent(Intent.ACTION_INSERT);
        contactIntent.setType(ContactsContract.RawContacts.CONTENT_TYPE);
        contactIntent.putExtra(ContactsContract.Intents.Insert.PHONE, phoneNumber);
        contactIntent.putExtra(ContactsContract.Intents.Insert.NAME, name);
        contactIntent.putExtra(ContactsContract.Intents.Insert.EMAIL, email);
        contactIntent.putExtra(ContactsContract.Intents.Insert.COMPANY, company);
        if (contactIntent.resolveActivity(context.getPackageManager()) != null) {
            context.startActivity(contactIntent);
        } else {
            Toast.makeText(context, "Failed to start intent", Toast.LENGTH_SHORT).show();
        }
    }

    //send sms
    public static void sendSMS(String number, String body, Context context) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("sms:" + number));
        intent.putExtra("sms_body", body);
        context.startActivity(intent);
    }
    ///

    //////////make Call
    public static void call(String number, Context context) {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:phoneNumber"));
        context.startActivity(intent);
    }

    //////Open Geo Location
    public static void openLocation(String url, Context context) {
        Intent intent = new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(url));
        context.startActivity(intent);
    }

    //oPEN URL
    public static void openUrl(String url, Context context) {
        Intent urlIntent = new Intent(Intent.ACTION_VIEW);
        urlIntent.setData(Uri.parse(url));
        context.startActivity(urlIntent);
    }

    //Show route or direction
    public static void showDirection(String location_name, Context context) {
        Uri gmmIntentUri = Uri.parse("google.navigation:q=" + location_name);
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        context.startActivity(mapIntent);
    }

    ///Show location with address
    public static void showLocation(String adress, Context context) {
        Uri gmmIntentUri = Uri.parse("geo:0,0?q=" + adress);
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        context.startActivity(mapIntent);
    }

    //Extract Email from text
    public static String extractEmail(String rawText) {
        String email = null;
        Pattern pattern = Pattern.compile("[\\w.]+@[\\w.]+");
        Matcher matcher = pattern.matcher(rawText);
        while (matcher.find()) {
            email = matcher.group();
        }
        return email;
    }

    //extract phone from text
    public static String extractPhone(String rawText) {
        String num = null;
        Pattern pattern = Pattern.compile("[\\+]?[0-9.-]+");
        Matcher matcher = pattern.matcher(rawText);
        while (matcher.find()) {
            num = matcher.group();
        }
        return num;
    }

    //extract URL from text
    public static String extractURL(String rawText) {
        String url = null;
        Pattern pattern = Pattern.compile("\\(?\\b(https?://|www[.]|ftp://)[-A-Za-z0-9+&@#/%?=~_()|!:,.;]*[-A-Za-z0-9+&@#/%=~_()|]");
        Matcher matcher = pattern.matcher(rawText);
        while (matcher.find()) {
            url = matcher.group();
        }
        if (url == null) {
            pattern = Pattern.compile("\\(?\\b(http://|www[.])[-A-Za-z0-9+&@#/%?=~_()|!:,.;]*[-A-Za-z0-9+&@#/%=~_()|]");
            matcher = pattern.matcher(rawText);
            while (matcher.find()) {
                url = matcher.group();
            }
        }
        return url;
    }

    //add event to phone
    public static void addEvent(String title, String location, String begin, String end, Context context) {
        Intent intent = new Intent(Intent.ACTION_INSERT)
                .setData(CalendarContract.Events.CONTENT_URI)
                .putExtra(CalendarContract.Events.TITLE, title)
                .putExtra(CalendarContract.Events.EVENT_LOCATION, location)
                .putExtra(CalendarContract.Events.DTSTART, begin)
                .putExtra(CalendarContract.Events.DTEND, end);
        if (intent.resolveActivity(context.getPackageManager()) != null) {
            context.startActivity(intent);
        }
    }

    //extract date from string
    public static String extractDateString(String rawData) {
        rawData = rawData.trim();
        if (rawData.length() != 16) {
            return extractPhone(rawData);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return OffsetDateTime
                    .parse(
                            rawData,
                            DateTimeFormatter.ofPattern("uuuuMMdd'T'HHmmssXXXXX")
                    )
                    .format(
                            DateTimeFormatter
                                    .ofLocalizedDateTime(FormatStyle.MEDIUM)
                                    .withLocale(Locale.UK)
                    );
        } else {
            return rawData;
        }
    }


    ///Check internet connectivity
    public static boolean checkConnectivity(Context mcontxt) {

        ConnectivityManager connectivityManager = (ConnectivityManager) mcontxt.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager != null) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.getActiveNetwork());
                if (capabilities != null) {
                    if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                        return true;
                    } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                        return true;
                    } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
                        return true;
                    }
                }
            } else {

                try {
                    NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
                    if (activeNetworkInfo != null && activeNetworkInfo.isConnected()) {

                        return true;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        return false;
    }
    public static String getCurrentDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy-hh:mm aaa");
        return sdf.format(new Date());
    }


    public static void insertIntoDatabase(String scan_result, ViewModelStoreOwner owner) {
        HistoryViewModel mHistoryViewModel;
        mHistoryViewModel = new ViewModelProvider(owner).get(HistoryViewModel.class);
        ////Email
        final int min = 20;
        final int max = 200000;
        final int random = new Random().nextInt((max - min) + 1) + min;


        if (scan_result.toUpperCase().startsWith("MATMSG")) {
            mHistoryViewModel.insert(new ScanResultDetail( "Email" + random, false, "" + "Email", "scanned", getCurrentDate(), "" + scan_result,false));
        }
        //SMS
        else if (scan_result.toUpperCase().startsWith("SMSTO")) {
            mHistoryViewModel.insert(new ScanResultDetail("SMS" + random, false, "" + "SMS", "scanned", getCurrentDate(), "" + scan_result,false));
        }
        //VCARD
        else if (scan_result.toUpperCase().startsWith("BEGIN:VCARD")) {
            mHistoryViewModel.insert(new ScanResultDetail( "Contact/Vcard" + random, false, "" + "Contact/Vcard", "scanned", getCurrentDate(), "" + scan_result,false));
        }
        //VEVENT
        else if (scan_result.toUpperCase().startsWith("BEGIN:VEVENT")||scan_result.toUpperCase().startsWith("BEGIN:VCALENDAR")) {
            mHistoryViewModel.insert(new ScanResultDetail( "Event" + random, false, "" + "event", "scanned", getCurrentDate(), "" + scan_result,false));
        }

        //wifi
        else if (scan_result.toUpperCase().startsWith("WIFI")) {
            mHistoryViewModel.insert(new ScanResultDetail( "Wi-Fi" + random, false, "" + "WiFi", "scanned", getCurrentDate(), "" + scan_result,false));
        }
        //GEO LOCATION
        else if (scan_result.toUpperCase().startsWith("GEO")) {
            mHistoryViewModel.insert(new ScanResultDetail( "GeoLocation" + random, false, "" + "GeoLocation", "scanned", getCurrentDate(), "" + scan_result,false));
        }

        //url
        else if (scan_result.toUpperCase().startsWith("HTTP")) {
            mHistoryViewModel.insert(new ScanResultDetail( "URL" + random, false, "" + "URL", "scanned", getCurrentDate(), "" + scan_result,false));
        }
        //tel
        else if (scan_result.toUpperCase().startsWith("TEL")) {
            mHistoryViewModel.insert(new ScanResultDetail( "Phone" + random, false, "" + "Phone", "scanned", getCurrentDate(), "" + scan_result,false));
        }
        //BARCODE
        else if (scan_result.matches("[0-9]+")) {
            mHistoryViewModel.insert(new ScanResultDetail( "Barcode" + random, false, "" + "Barcode", "scanned", getCurrentDate(), "" + scan_result,false));
        }
        //TEXT
        else {
            mHistoryViewModel.insert(new ScanResultDetail( "Text" + random, false, "" + "Text", "scanned", getCurrentDate(), "" + scan_result,false));
        }
    }

}
