package com.mylivestock.app;


import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.Menu;
import android.view.View;

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
import com.mylivestock.app.ui.measure.MeasureFragment;
import com.mylivestock.app.ui.measure.MeasureViewModel;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding bindingMain;
    private SharedViewModel sharedViewModel;
    private String deviceName = null;
    private String deviceHardwareAddress;
    public static Handler handlerMain;
    public static BluetoothSocket mmSocket;
    public final static int CONNECTING_STATUS = 1; // used in bluetooth handler to identify message status
    public final static int MESSAGE_READ = 2; // used in bluetooth handler to identify message update



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

        //Todo: Initialise UI
        //SharedViewModel
        sharedViewModel = new ViewModelProvider(this).get(SharedViewModel.class);
        sharedViewModel.setMeasureText("---");
        sharedViewModel.setSystemText("no error");



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
                            case 1:

                                break;
                            case -1:
                                sharedViewModel.setSystemText("Error connecting to device");
                                break;
                        }
                        break;
                    case MESSAGE_READ:
                        //Todo: update UI
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