package com.example.qrscannerappzl.history;

import android.app.Application;

import androidx.annotation.Nullable;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.qrscannerappzl.RoomDatabase.ScanResulstDao;
import com.example.qrscannerappzl.RoomDatabase.ScanResultDetail;
import com.example.qrscannerappzl.RoomDatabase.ScanResultRoomDatabase;


import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HistoryViewModel extends AndroidViewModel {

    ScanResulstDao scanResulstDao;
    private ExecutorService executorService;
    public HistoryViewModel(@Nullable Application application) {

        super(application);
        scanResulstDao = ScanResultRoomDatabase.getInstance(application).scanResulstDao();
        executorService = Executors.newSingleThreadExecutor();

    }

    public LiveData<List<ScanResultDetail>> getAllData() {
        return scanResulstDao.getAllScanResults();
    }

    public LiveData<List<ScanResultDetail>> getScannedData() {
        return scanResulstDao.getAllScannedData("scanned");
    }


    public LiveData<List<ScanResultDetail>> getCreatedData() {
        return scanResulstDao.getAllCreatedData("created");
    }

    public void insert(ScanResultDetail scanResultDetail) {
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                scanResulstDao.insertData(scanResultDetail);
            }
        });
    }
    public void deleteSpecific(String title) {
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                scanResulstDao.deleteSingleRecord(title);
            }
        });
    }
    public void updateTitle(String oldTitle,String newTitle) {
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                scanResulstDao.updateRecord(oldTitle,newTitle);
            }
        });
    }
    public void updateFav(int id,boolean fav) {

        executorService.execute(new Runnable() {
            @Override
            public void run() {

                scanResulstDao.updateFav(id,fav);

            }
        });

    }
    public LiveData<List<ScanResultDetail>> getFavData() {

                return scanResulstDao.getAllFavData(true);
    }

    public void deleteScanned(String scanned) {
        executorService.execute(new Runnable() {
            @Override
            public void run() {

                scanResulstDao.deleteScanned(scanned);
            }
        });
    }
    public void deleteCreated(String created) {
        executorService.execute(new Runnable() {
            @Override
            public void run() {

                scanResulstDao.deleteScanned(created);
            }
        });
    }

}
