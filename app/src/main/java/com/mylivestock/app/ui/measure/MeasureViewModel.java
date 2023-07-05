package com.mylivestock.app.ui.measure;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class MeasureViewModel extends ViewModel {

    private final MutableLiveData<String> mText;
    private final MutableLiveData<String> connectionStatus;

    public MeasureViewModel() {
        mText = new MutableLiveData<>();
        connectionStatus = new MutableLiveData<>();
        mText.setValue("This is measure fragment");
        connectionStatus.setValue("noErr");
    }

    public LiveData<String> getText_measure() {
        return mText;
    }
    public LiveData<String> getText_system() {
        return connectionStatus;
    }

    public void setText_mText(String text) { mText.setValue(text); }

    public void setText_connectionStatus(String text) {
        connectionStatus.setValue(text);
    }
}