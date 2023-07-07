package com.mylivestock.app;

import static android.content.ContentValues.TAG;

import static com.mylivestock.app.MainActivity.CONNECTING_STATUS;
import static com.mylivestock.app.MainActivity.handlerMain;
import static com.mylivestock.app.MainActivity.mmSocket;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.util.UUID;

public class CreateConnectThread extends Thread{

    ConnectedThread connectedThread = null;
    @SuppressLint("MissingPermission")
    public CreateConnectThread(BluetoothAdapter bluetoothAdapter, String deviceHardwareAddress){

        BluetoothDevice bluetoothDevice = bluetoothAdapter.getRemoteDevice(deviceHardwareAddress);
        BluetoothSocket tmp = null; //temp var for socket

        @SuppressLint("MissingPermission")
        UUID uuid = bluetoothDevice.getUuids()[0].getUuid();

        try{
                /*
                Get a BluetoothSocket to connect with the given BluetoothDevice.
                Due to Android device varieties,the method below may not work fo different devices.
                You should try using other methods i.e. :
                tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
                 */
            tmp = bluetoothDevice.createInsecureRfcommSocketToServiceRecord(uuid);

        }catch(Exception e){
            Log.e(TAG, "Socket's create method failed", e);
        }
        mmSocket = tmp;

    }
    @SuppressLint("MissingPermission")
    public void run(){
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
       //bluetoothAdapter.cancelDiscovery();

        try{
            mmSocket.connect();
            Log.e("Status", "DeviceConnected");
            handlerMain.obtainMessage(CONNECTING_STATUS,1,-1).sendToTarget();
        }catch (IOException connectException) {
            // Unable to connect; close the socket and return.
            try {
                mmSocket.close();
                Log.e("Status", "Cannot connect to device");
                handlerMain.obtainMessage(CONNECTING_STATUS, -1, -1).sendToTarget();
            } catch (IOException closeException) {
                Log.e(TAG, "Could not close the client socket", closeException);
            }


            //sharedViewModel.setTryingToConnectBT(false);
            return;
        }
        //connection successful
        connectedThread = new ConnectedThread(mmSocket);
        connectedThread.runStream();
    }

    public ConnectedThread getConnectedThread(){
        if (connectedThread != null){
            return connectedThread;
        }else{
            return null;
        }
    }

    public void cancel(){
        try {
            mmSocket.close();
        } catch (IOException e) {
            Log.e(TAG, "Could not close the client socket", e);
        }
    }
}

