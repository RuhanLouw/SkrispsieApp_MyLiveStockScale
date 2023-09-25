package com.mylivestock.app.data.database.entities;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

@Entity(tableName = "SheepMeasurements_table")
public class SheepMeasurement {
    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "timestamp")
    public long timestamp;
    @ColumnInfo(name = "sheepId")
    public String sheepId;
    @ColumnInfo(name = "sheepName")
    public String sheepName;
    @ColumnInfo(name = "sheepWeight_kg")
    public float sheepWeight;
    @ColumnInfo(name = "userNote")
    public String userNote;

    public SheepMeasurement(long timestamp, String sheepId, String sheepName, float sheepWeight, String userNote) {
        this.timestamp = timestamp;
        this.sheepId = sheepId;
        this.sheepName = sheepName;
        this.sheepWeight = sheepWeight;
        this.userNote = userNote;
    }

    //Getter and Setter
    public void setId(int id) {
        this.id = id;
    }
    public int getId() {
        return id;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
    public String getTimestamp() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date date = new Date(timestamp);
        return sdf.format(date);
    }

    public void setSheepId(String sheepId) {
        this.sheepId = sheepId;
    }
    public String getSheepId() {
        return sheepId;
    }

    public void setSheepName(String sheepName) {
        this.sheepName = sheepName;
    }
    public String getSheepName() {
        return sheepName;
    }

    public void setSheepWeight(float sheepWeight) { this.sheepWeight = sheepWeight;}
    public float getSheepWeight() {
        return sheepWeight;
    }
    public String getSheepWeight_string() {
        return String.valueOf(sheepWeight);
    }

    public void setUserNote(String userNote) {
        this.userNote = userNote;
    }
    public String getUserNote() {
        return userNote;
    }
}



