package com.example.qrscannerappzl.RoomDatabase;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import java.util.List;


@Dao
public interface ScanResulstDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertData(ScanResultDetail scanResultDetail);

    @Query("SELECT * FROM ScanResultDetail")
    LiveData<List<ScanResultDetail>> getAllScanResults();

    @Query("DELETE FROM ScanResultDetail")
    void deleteAll();


    @Query("SELECT * FROM ScanResultDetail WHERE category = :scanned  ")
    LiveData<List<ScanResultDetail>> getAllScannedData(String scanned);


    @Query("SELECT * FROM ScanResultDetail WHERE category = :created  ")
    LiveData<List<ScanResultDetail>> getAllCreatedData(String created);


    @Query("Delete from ScanResultDetail where title = :title")
    void deleteSingleRecord(String title);

    @Query("update ScanResultDetail set title = :newTitle Where title = :oldTitle")
    void updateRecord(String oldTitle, String newTitle);


    @Query("update ScanResultDetail set favorite = :fav Where id = :id")
    void updateFav(int id, boolean fav);

    @Query("SELECT * FROM ScanResultDetail WHERE favorite = :isFav  ")
    LiveData<List<ScanResultDetail>> getAllFavData(boolean isFav);


    @Query("DELETE FROM ScanResultDetail WHERE category = :created  ")
    void deleteCreated(String created);


    @Query("DELETE FROM ScanResultDetail WHERE category = :scanned  ")
    void deleteScanned(String scanned);


}
