package com.mylivestock.app;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.mylivestock.app.ui.bluetooth.DeviceInfoModel;

public class SharedViewModel extends ViewModel {

    private final MutableLiveData<String> bluetoothStatus = new MutableLiveData<>();
    private final MutableLiveData<Boolean> tryingToConnectBT = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> isConnected = new MutableLiveData<>(false);
    private Boolean isAwake = true;
    private final MutableLiveData<String> measureText = new MutableLiveData<>();
    private final MutableLiveData<String> systemText = new MutableLiveData<>();
    private final MutableLiveData<Boolean> requestMeasure = new MutableLiveData<>(false);
    private final MutableLiveData<DeviceInfoModel> deviceInfoModel = new MutableLiveData<>();

    /// Shared Objects func
    public void setMeasureText(String text) {
        measureText.setValue(text);
    }
    public LiveData<String> getMeasureText() {
        return measureText;
    }
    //
    //
    public void setSystemText(String text) {
        systemText.setValue(text);
    }
    public LiveData<String> getSystemText() {
        return systemText;
    }
    //
    //
    public void setTryingToConnectBT(boolean value) {
        tryingToConnectBT.setValue(value);
//        if (!value) {
//            deviceInfoModel = null;
//        }
    }
    public LiveData<Boolean> getTryingToConnectBT() {
        return tryingToConnectBT;
    }
    //
    //
    public LiveData<Boolean> getIsConnected(){return isConnected;}
    public void setIsConnected(boolean value){isConnected.setValue(value);}
    //
    public void setRequestMeasure(boolean value) {
         requestMeasure.setValue(value);
    }
    public LiveData<Boolean> getRequestMeasure(boolean value) {
        return requestMeasure;
    }
    //
    //
    public void setDeviceInfoModel(DeviceInfoModel deviceInfoModel) {
        this.deviceInfoModel.setValue(deviceInfoModel);
    }
    public MutableLiveData<DeviceInfoModel> getDeviceInfoModel(){
        return deviceInfoModel;
    }

    public Boolean getIsAwake() {
        return isAwake;
    }
    public void setIsAwake(boolean value) {
        isAwake = value;

    }
    //
}



