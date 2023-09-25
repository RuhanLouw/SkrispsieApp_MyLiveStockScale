package com.mylivestock.app.data.repository;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.mylivestock.app.data.database.entities.SheepMeasurement;

import java.util.List;

public class SheepViewModel extends AndroidViewModel {

    private final SheepRepository sheepRepository;
    public MutableLiveData<Boolean> wantToEditData;
    public SheepMeasurement editData;
    private LiveData<List<SheepMeasurement>> allSheepMeasurements;

    public SheepViewModel(Application application) {
        super(application);
        sheepRepository = new SheepRepository(application);
        allSheepMeasurements = sheepRepository.getAllSheepMeasurements();
    }

    public LiveData<List<SheepMeasurement>> getAllSheepMeasurements() {
        return allSheepMeasurements;
    }

    public void insert(SheepMeasurement sheepMeasurement){
        sheepRepository.insert(sheepMeasurement);
    }
    public void update(SheepMeasurement sheepMeasurement){
        sheepRepository.update(sheepMeasurement);
    }
    public void delete(SheepMeasurement sheepMeasurement){
        sheepRepository.delete(sheepMeasurement);
    }
    public void deleteAll(){
        sheepRepository.deleteAll();
    }

    public LiveData<List<SheepMeasurement>> getSheepMeasurementById(long SheepMeasurementId){
        return sheepRepository.getSheepMeasurementById(SheepMeasurementId);
    }

    //DataFragment: Chosen entity to edit
    public SheepMeasurement getEditData(){
        return editData;
    }
    public void setEditData(SheepMeasurement editData){
        this.editData =  editData;
    }

    public LiveData<Boolean> getWantToEditData(){
        return wantToEditData;
    }


    public void wantToEditData(boolean value) {
        wantToEditData.setValue(value);
    }
}
