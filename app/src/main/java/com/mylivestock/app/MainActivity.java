package com.mylivestock.app;


import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.mylivestock.app.data.repository.SheepViewModel;
import com.mylivestock.app.databinding.ActivityMainBinding;
import com.mylivestock.app.ui.bluetooth.DeviceInfoModel;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding bindingMain;
    private SharedViewModel sharedViewModel;
    public SheepViewModel sheepViewModel;
    public static Handler handlerMain;
    public static BluetoothSocket mmSocket;
    public static ConnectedThread connectedThread;
    public CreateConnectThread createConnectThread;
    //Connection status in handlerMain: Public variables for classes -> ConnectionInit, CreateConnectThread, ConnectThread
    public final static int CONNECTING_STATUS = 1; // used in bluetooth handler to identify message status
    public final static int MESSAGE_READ = 2; // used in bluetooth handler to identify message update
    public final static int MESSAGE_SEND = 3; // used in handlerMain to identify connection error

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //MainBinding
        bindingMain = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(bindingMain.getRoot());
        setSupportActionBar(bindingMain.appBarMain.toolbar);

        //Message fab
        bindingMain.appBarMain.fab.setOnClickListener(view ->
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        );

        //Navigation setup
        DrawerLayout drawer = bindingMain.drawerLayout;
        NavigationView navigationView = bindingMain.navView;
            // Passing each menu ID as a set of Ids because each
            // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_measure, R.id.nav_data, R.id.nav_bluetooth)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        ProgressBar progressBarBtConnection = bindingMain.appBarMain.progressBarBtConnection;

        //SharedViewModel
        sharedViewModel = new ViewModelProvider(this).get(SharedViewModel.class);
        sheepViewModel = new ViewModelProvider(this).get(SheepViewModel.class);
        sharedViewModel.setMeasureText("---");
        sharedViewModel.setSystemText("Connect to a Bluetooth Device");

        //CreateConnectThread and ConnectedThread
            //retrieve from (boolean) sharedViewModel.tryingToConnectBT
        sharedViewModel.getTryingToConnectBT().observe(this, shouldConnect -> {
            if (shouldConnect) {
                //Start connection process and Initialise the ConnectedThread
                progressBarBtConnection.setVisibility(View.VISIBLE);
                sharedViewModel.setSystemText("Connecting...");
                BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                DeviceInfoModel deviceInfoModel = sharedViewModel.getDeviceInfoModel().getValue();
                String deviceHardwareAddress = null;
                if (deviceInfoModel != null) {
                    deviceHardwareAddress = deviceInfoModel.getDeviceHardwareAddress();
                    createConnectThread = new CreateConnectThread(bluetoothAdapter, deviceHardwareAddress);
                    createConnectThread.start();
                }
            } else {
                progressBarBtConnection.setVisibility(View.GONE);
            }
        });




        /*
        Second most important piece of Code. GUI Handler
         */
        //require test
        handlerMain = new Handler(Looper.getMainLooper()){
            public void handleMessage(@NonNull Message msg){
                switch (msg.what){
                    case CONNECTING_STATUS:
                        //update UI
                        switch (msg.arg1){
                            case 0:
                                sharedViewModel.setSystemText("Connecting to device");
                                break;
                            case 1:
                                sharedViewModel.setSystemText("Connected to device");
                                sharedViewModel.setIsConnected(true);
                                sharedViewModel.setTryingToConnectBT(false);

                                break;
                            case -1:
                                sharedViewModel.setSystemText("Error connecting to device");
                                sharedViewModel.setIsConnected(false);
                                sharedViewModel.setTryingToConnectBT(false);

                                break;
                        }
                        break;

                    case MESSAGE_READ:
                        String measurement = msg.obj.toString();
                        switch (msg.arg1){
                            case 0:
                                Log.d("ESP32", "0");
                                sharedViewModel.setSystemText("Error from Scale");
                                break;
                            case 1:
                                Log.d("ESP32", "1");
                                sharedViewModel.setMeasureText(measurement);
                                sharedViewModel.setSystemText("Measurement Received:");
                                break;
                            case 2:
                                Log.d("ESP32", "2");
                                sharedViewModel.setSystemText("Scale Tared");
                                break;
                            case 3:
                                Log.d("ESP32", "3");
                                sharedViewModel.setSystemText("Continuous Measurement");
                        }
                        break;

                    case MESSAGE_SEND:
                        if (connectedThread != null) {
                            String messageToSend = msg.obj.toString();
                            connectedThread.write(messageToSend);
                        }else {
                            sharedViewModel.setSystemText("Error Not Connected");
                        }
	                    break;
                }
            }
        };

    }//onCreate

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }



    /////\
    /* ============================ Thread to Create Bluetooth Connection =================================== */
    public static class CreateConnectThread extends Thread {

        @SuppressLint("MissingPermission")
        public CreateConnectThread(BluetoothAdapter bluetoothAdapter, String address) {
            /*
            Use a temporary object that is later assigned to mmSocket
            because mmSocket is final.
             */
            BluetoothDevice bluetoothDevice = bluetoothAdapter.getRemoteDevice(address);
            BluetoothSocket tmp = null;
            @SuppressLint("MissingPermission") UUID uuid = bluetoothDevice.getUuids()[0].getUuid();

            try {
                /*
                Get a BluetoothSocket to connect with the given BluetoothDevice.
                Due to Android device varieties,the method below may not work fo different devices.
                You should try using other methods i.e. :
                tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
                 */
                tmp = bluetoothDevice.createInsecureRfcommSocketToServiceRecord(uuid);

            } catch (IOException e) {
                Log.e(TAG, "Socket's create() method failed", e);

            }
            mmSocket = tmp;
        }

        @SuppressLint("MissingPermission")
        public void run() {
            // Cancel discovery because it otherwise slows down the connection.
            BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            //bluetoothAdapter.cancelDiscovery();
            try {
                // Connect to the remote device through the socket. This call blocks
                // until it succeeds or throws an exception.
                mmSocket.connect();
                Log.e("Status", "Device connected");
                handlerMain.obtainMessage(CONNECTING_STATUS, 1, -1).sendToTarget();
            } catch (IOException connectException) {
                // Unable to connect; close the socket and return.
                try {
                    mmSocket.close();
                    Log.e("Status", "Cannot connect to device");
                    handlerMain.obtainMessage(CONNECTING_STATUS, -1, -1).sendToTarget();
                } catch (IOException closeException) {
                    Log.e(TAG, "Could not close the client socket", closeException);
                }
                return;
            }

            // The connection attempt succeeded. Perform work associated with
            // the connection in a separate thread.
            connectedThread = new ConnectedThread(mmSocket);
            connectedThread.start();
        }

        // Closes the client socket and causes the thread to finish.
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "Could not close the client socket", e);
            }
        }
    }



    /* =============================== Thread for Data Transfer =========================================== */
    public static class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket) {
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the input and output streams, using temp objects because
            // member streams are final
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
                Log.e("IOException", "Error occurred when creating Input or Output Stream", e);
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            byte[] buffer = new byte[1024];  // buffer store for the stream
            int bytes = 0; // bytes returned from read()
            String fullMessage;
            String readMessage;
            // Keep listening to the InputStream until an exception occurs
            while (true) {
                try {
                    /*
                    Read from the InputStream from ESP until termination character is reached.
                    Then send the whole String message to GUI Handler. !! use args as instructions for message received
                     INCOMING MESSAGE STRUCTURE:  "CODE":"MESSAGE"\n -> 2 bytes before message and one stop byte after message ('\n')
                                                    1/0 : 123.45 \n
                     */
                    buffer[bytes] = (byte) mmInStream.read();
                    if (buffer[bytes] == '\n'){
                        int code = buffer[0] - '0';
                        fullMessage = new String(buffer,0,bytes);
                        Log.e("ESP32 Message", "fullMessage: " + fullMessage);
                        readMessage = fullMessage.substring(2); //-> offset 2 bytes ('code' & ':') before message and one stop byte after message ('\n')
                        Log.e("ESP32", readMessage);
                        handlerMain.obtainMessage(MESSAGE_READ,code,0,readMessage).sendToTarget(); // arg2: dummy
                        bytes = 0;
//                        byte lastByte = (byte) mmInStream.read();//last bit to receive
                    } else {
                        bytes++;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    break;
                }
            }
        }

        /* Call this from the main activity to send data to the remote Bluetooth Device */
        public void write(String input) {
            byte[] bytes = (input).getBytes(); //adds stop byte and converts entered String into bytes
            try {
                mmOutStream.write(bytes);
            } catch (IOException e) {
                Log.e("Send Error","Unable to send message",e);
            }
        }

        /* Call this from the main activity to shutdown the connection */
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) { }
        }
    }

    /* ============================ Terminate Connection at BackPress ====================== */
    @Override
    public void onBackPressed() {
        // Terminate Bluetooth Connection and close app
        if (createConnectThread != null){
            createConnectThread.cancel();
        }
        Intent a = new Intent(Intent.ACTION_MAIN);
        a.addCategory(Intent.CATEGORY_HOME);
        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(a);
    }

}