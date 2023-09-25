package com.mylivestock.app.data.repository;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.mylivestock.app.data.database.SheepDatabase;
import com.mylivestock.app.data.database.dao.SheepMeasurementDao;
import com.mylivestock.app.data.database.entities.SheepMeasurement;

import java.util.List;

public class SheepRepository {
    private final SheepMeasurementDao sheepMeasurementDao;
    private final LiveData<List<SheepMeasurement>> allSheepMeasurements;

    public SheepRepository(Application application) {
        SheepDatabase db = SheepDatabase.getSheepDatabase(application);
        sheepMeasurementDao = db.sheepMeasurementDao();
        allSheepMeasurements = sheepMeasurementDao.getAllSheepMeasurements();
    }

    public LiveData<List<SheepMeasurement>> getAllSheepMeasurements() {
        return allSheepMeasurements;
    }

    public void insert(SheepMeasurement sheepMeasurement){
        SheepDatabase.dbExecutor.execute(() -> {
            sheepMeasurementDao.insertSheepMeasurement(sheepMeasurement);
        });
    }

    public void update(SheepMeasurement sheepMeasurement){
        SheepDatabase.dbExecutor.execute(() -> {
            sheepMeasurementDao.updateSheepMeasurement(sheepMeasurement);
        });
    }

    public void delete(SheepMeasurement sheepMeasurement){
        SheepDatabase.dbExecutor.execute(() -> {
            sheepMeasurementDao.deleteSheepMeasurement(sheepMeasurement);
        });
    }

    public void deleteAll(){
        sheepMeasurementDao.deleteAllSheepMeasurements();
    }

    public LiveData<List<SheepMeasurement>> getSheepMeasurementById(long SheepMeasurementId){
        return sheepMeasurementDao.getSheepMeasurementById(SheepMeasurementId);
    }
}
