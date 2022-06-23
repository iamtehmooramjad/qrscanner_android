package com.example.qrscannerappzl.create.Calender;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class CalenderClass {
    public static ArrayList<EventModelClass> eventList=new ArrayList<>();


    public static ArrayList<EventModelClass> readCalendarEvent(Context context) {
        Cursor cursor = context.getContentResolver()
                .query(
                        Uri.parse("content://com.android.calendar/events"),
                        new String[] { "calendar_id", "title", "description",
                                "dtstart", "dtend", "eventLocation" }, null,
                        null, null);
        cursor.moveToFirst();
        // fetching calendars name
        String CNames[] = new String[cursor.getCount()];

        // fetching calendars id
        eventList.clear();
        for (int i = 0; i < CNames.length; i++) {


            eventList.add(new EventModelClass(""+cursor.getString(1),
                    ""+getDate(Long.parseLong(cursor.getString(3))),
                    ""+getDate(Long.parseLong(cursor.getString(4)))));

            CNames[i] = cursor.getString(1);
            cursor.moveToNext();

        }
        return eventList;
    }

    public static String getDate(long milliSeconds) {
        SimpleDateFormat formatter = new SimpleDateFormat(
                "yyyyMMdd'T'HHmmss'Z'");
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);
        return formatter.format(calendar.getTime());
    }
}
