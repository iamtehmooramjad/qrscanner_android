package com.example.qrscannerappzl.RoomDatabase;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class ScanResultDetail {
    @PrimaryKey(autoGenerate = true)
    int id;



    String  title;
    boolean favorite;

    public int getId() {
        return id;
    }

    String result_type;
    String category;//scan or created
    String date;
    String scan_results;
    boolean checkbox_status;

    public ScanResultDetail( String title, boolean favorite, String result_type, String category, String date, String scan_results, boolean checkbox_status) {
        this.title = title;
        this.favorite = favorite;
        this.result_type = result_type;
        this.category = category;
        this.date = date;
        this.scan_results = scan_results;
        this.checkbox_status = checkbox_status;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }

    public String getResult_type() {
        return result_type;
    }

    public void setResult_type(String result_type) {
        this.result_type = result_type;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getScan_results() {
        return scan_results;
    }

    public void setScan_results(String scan_results) {
        this.scan_results = scan_results;
    }

    public boolean isCheckbox_status() {
        return checkbox_status;
    }

    public void setCheckbox_status(boolean checkbox_status) {
        this.checkbox_status = checkbox_status;
    }
}




