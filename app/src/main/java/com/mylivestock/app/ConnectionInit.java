package com.mylivestock.app;

import android.bluetooth.BluetoothAdapter;

public class ConnectionInit {

    boolean connectionStart = false;
    public ConnectionInit(String deviceName, String deviceHardwareAddress) {

        //if a bluetooth device has been selected from BluetoothFragment, then connect to it

        if (deviceName != null){
            /*
            This is the most important piece of code. When "deviceName" is found
            the code will call a new thread to create a bluetooth connection to the
            selected device (see the thread code below)
             */
            BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            CreateConnectThread createConnectThread = new CreateConnectThread(bluetoothAdapter, deviceHardwareAddress);
            createConnectThread.start();
            connectionStart = true;
        }
    }

    public boolean isConnectionStart(){
        return connectionStart;
    }
}
