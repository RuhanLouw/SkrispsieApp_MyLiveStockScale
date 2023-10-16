package com.mylivestock.app.ui.data;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.mylivestock.app.data.database.entities.SheepMeasurement;
import com.mylivestock.app.data.repository.SheepRepository;

import java.io.Closeable;
import java.util.List;


public class DataViewModel extends ViewModel {

//    private final MutableLiveData<String> mText;
//    private final LiveData<List<SheepMeasurement>> allSheepMeasurements;
//    private SheepRepository sheepRepository;
//
//    public DataViewModel(Application application) {
//        super((Closeable) application);
//        sheepRepository = new SheepRepository(application);
//        allSheepMeasurements = sheepRepository.getAllSheepMeasurements();
//        mText = new MutableLiveData<>();
//        mText.setValue("this is the data page");
//    }
//
//    public LiveData<String> getText() {
//        return mText;
//    }
//
//    public LiveData<List<SheepMeasurement>> getAllSheepMeasurements() {
//        return allSheepMeasurements;
//    }
//    public void insert(SheepMeasurement sheepMeasurement){
//        sheepRepository.insert(sheepMeasurement);
//    }
}