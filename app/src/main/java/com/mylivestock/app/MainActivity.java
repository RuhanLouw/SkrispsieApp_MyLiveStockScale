package com.mylivestock.app;


import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.Menu;
import android.view.View;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.mylivestock.app.databinding.ActivityMainBinding;
import com.mylivestock.app.databinding.FragmentBluetoothBinding;
import com.mylivestock.app.databinding.FragmentMeasureBinding;
import com.mylivestock.app.ui.bluetooth.DeviceInfoModel;
import com.mylivestock.app.ui.measure.MeasureFragment;
import com.mylivestock.app.ui.measure.MeasureViewModel;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding bindingMain;
    private SharedViewModel sharedViewModel;
    private String deviceName = null;
    private String deviceHardwareAddress;
    public static Handler handlerMain;
    public static BluetoothSocket mmSocket;

    public ConnectedThread connectedThread;
    public CreateConnectThread createConnectThread;
    public ConnectionInit connectionInit;


    //Connection status in handlerMain: Public variables for classes -> ConnectionInit, CreateConnectThread, ConnectThread
    public final static int CONNECTING_STATUS = 1; // used in bluetooth handler to identify message status
    public final static int MESSAGE_READ = 2; // used in bluetooth handler to identify message update

    public final static int CONNECTION_ERROR = 3; // used in handlerMain to identify connection error


    private DeviceInfoModel deviceInfoModel;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //MainBinding
        bindingMain = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(bindingMain.getRoot());
        setSupportActionBar(bindingMain.appBarMain.toolbar);

        //Message fab
        bindingMain.appBarMain.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

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

        //Todo: Initialise UI
        //SharedViewModel
        sharedViewModel = new ViewModelProvider(this).get(SharedViewModel.class);
        sharedViewModel.setMeasureText("---");
        sharedViewModel.setSystemText("no error");


        //CreateConnectThread and ConnectedThread
            //retrieve from (boolean) sharedViewModel.tryingToConnectBT
        sharedViewModel.getTryingToConnectBT().observe(this, shouldConnect -> {
            if (shouldConnect) {
                //Start connection process and Initialise the ConnectedThread
                progressBarBtConnection.setVisibility(View.VISIBLE);
                BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                DeviceInfoModel deviceInfoModel = sharedViewModel.getDeviceInfoModel().getValue();
                String deviceHardwareAddress = null;
                if (deviceInfoModel != null) {
                    deviceHardwareAddress = deviceInfoModel.getDeviceHardwareAddress();
                    createConnectThread = new CreateConnectThread(bluetoothAdapter, deviceHardwareAddress);
                    createConnectThread.start();
                    connectedThread = createConnectThread.getConnectedThread();
                }
            } else {
                progressBarBtConnection.setVisibility(View.GONE);
            }
        });


        /*
        Second most important piece of Code. GUI Handler
         */
        //Todo: Should handler for all UI be placed here?
        //require test
        handlerMain = new Handler(Looper.getMainLooper()){
            public void handleMessage(Message msg){
                switch (msg.what){
                    case CONNECTING_STATUS:
                        //Todo: update UI
                        switch (msg.arg1){
                            case 0:
                                sharedViewModel.setSystemText("Connecting...");
                                break;
                            case 1:
                                sharedViewModel.setSystemText("Connected to device");
                                sharedViewModel.setTryingToConnectBT(false);
                                break;
                            case -1:
                                sharedViewModel.setSystemText("Error connecting to device");
                                sharedViewModel.setTryingToConnectBT(false);
                                break;
                        }
                        break;
                    case MESSAGE_READ:
                        String measurement = msg.obj.toString();
                        sharedViewModel.setMeasureText(measurement);
                        break;
                }
            }
        };
        //Todo: add buttons from pages
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

}