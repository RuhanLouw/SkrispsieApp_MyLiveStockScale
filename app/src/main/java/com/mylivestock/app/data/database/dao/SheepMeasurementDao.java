package com.mylivestock.app.data.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.mylivestock.app.data.database.entities.SheepMeasurement;

import java.util.List;

@Dao
public interface SheepMeasurementDao {
    @Insert
    void insertSheepMeasurement(SheepMeasurement sheepMeasurement);

    @Query("SELECT * FROM SheepMeasurements_table ORDER BY timestamp DESC")
    LiveData<List<SheepMeasurement>> getAllSheepMeasurements();

    @Query("SELECT * FROM SheepMeasurements_table WHERE id = :SheepMeasurementId")
    LiveData<List<SheepMeasurement>> getSheepMeasurementById(long SheepMeasurementId);


    @Update
    void updateSheepMeasurement(SheepMeasurement sheepMeasurement);

    @Delete
    void deleteSheepMeasurement(SheepMeasurement sheepMeasurement);

    @Query("DELETE FROM SheepMeasurements_table")
    void deleteAllSheepMeasurements();

}
