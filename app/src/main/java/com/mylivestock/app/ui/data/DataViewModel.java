package com.mylivestock.app.ui.data;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class DataViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public DataViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("this is the data page");
    }

    public LiveData<String> getText() {
        return mText;
    }
}