package com.mylivestock.app;
import android.bluetooth.BluetoothClass;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.mylivestock.app.ui.bluetooth.DeviceInfoModel;

public class SharedViewModel extends ViewModel {

    //BluetoothFragment UI Data {
    private MutableLiveData<String> bluetoothStatus = new MutableLiveData<>();
    // }

    private final MutableLiveData<Boolean> tryingToConnectBT = new MutableLiveData<>(false);

    //MeasureFragment UI Data {
    private final MutableLiveData<String> measureText = new MutableLiveData<>();
    private final MutableLiveData<String> systemText = new MutableLiveData<>();
    private final MutableLiveData<Boolean> requestMeasure = new MutableLiveData<>(false);
    // }

    //BluetoothFragment UI Data
    private final MutableLiveData<DeviceInfoModel> deviceInfoModel = new MutableLiveData<>();

    /// Shared Objects func
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


    public void setTryingToConnectBT(boolean value) {
        tryingToConnectBT.setValue(value);
//        if (!value) {
//            deviceInfoModel = null;
//        }
    }
    public LiveData<Boolean> getTryingToConnectBT() {
        return tryingToConnectBT;
    }

    public void setRequestMeasure(boolean value) {
         requestMeasure.setValue(value);
    }
    public LiveData<Boolean> getRequestMeasure(boolean value) {
        return requestMeasure;
    }

    public void setDeviceInfoModel(DeviceInfoModel deviceInfoModel) {
        this.deviceInfoModel.setValue(deviceInfoModel);
    }
    public LiveData<DeviceInfoModel> getDeviceInfoModel(){
        return deviceInfoModel;
    }

}



