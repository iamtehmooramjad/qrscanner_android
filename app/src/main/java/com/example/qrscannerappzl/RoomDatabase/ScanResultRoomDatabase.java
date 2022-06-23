package com.example.qrscannerappzl.RoomDatabase;

import android.content.Context;
import android.net.wifi.ScanResult;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Insert;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {ScanResultDetail.class},version = 1,exportSchema = false)
public abstract class ScanResultRoomDatabase extends RoomDatabase{

    public abstract ScanResulstDao scanResulstDao();

    private static volatile ScanResultRoomDatabase INSTANCE;
    private static final int NUMBER_OF_THREADS = 4;
    static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public static ScanResultRoomDatabase getInstance(final Context context) {
        if (INSTANCE == null) {
            synchronized (ScanResultRoomDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            ScanResultRoomDatabase.class, "scan_results_database")
                            .addCallback(sRoomDatabaseCallback)
                            .build();
                }
            }
        }
        return INSTANCE;
    }


     /*Override the onCreate method to populate the database.
     For this sample, we clear the database every time it is created.*/
    private static RoomDatabase.Callback sRoomDatabaseCallback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);

            databaseWriteExecutor.execute(() -> {
                // Populate the database in the background.
                ScanResulstDao dao = INSTANCE.scanResulstDao();
                dao.deleteAll();
                ScanResultDetail scanResultDetail=new ScanResultDetail("",false,"","","","",false);
                dao.insertData(scanResultDetail);

            });
        }
    };
}
