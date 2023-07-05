package com.mylivestock.app;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SharedViewModel extends ViewModel {
    private MutableLiveData<String> measureText = new MutableLiveData<>();
    private MutableLiveData<String> systemText = new MutableLiveData<>();

    public void setMeasureText(String text) {
        measureText.setValue(text);
    }

    public LiveData<String> getMeasureText() {
        return measureText;
    }

    public void setSystemText(String text) {
        systemText.setValue(text);
    }

    public LiveData<String> getSystemText() {
        return systemText;
    }
}
