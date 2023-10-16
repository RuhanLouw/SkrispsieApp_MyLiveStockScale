package com.mylivestock.app.ui.measure;

import static com.mylivestock.app.MainActivity.MESSAGE_SEND;
import static com.mylivestock.app.MainActivity.handlerMain;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;


import com.mylivestock.app.SharedViewModel;
import com.mylivestock.app.data.database.entities.SheepMeasurement;
import com.mylivestock.app.data.repository.SheepViewModel;
import com.mylivestock.app.databinding.FragmentMeasureBinding;
import com.mylivestock.app.ui.data.DataAlertDialog;

public class MeasureFragment extends Fragment {

    public static MeasureViewModel measureViewModel;

    private FragmentMeasureBinding binding;
    private SharedViewModel sharedViewModel;
    private SheepViewModel sheepViewModel;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Retrieve SharedViewModel
        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        sheepViewModel = new ViewModelProvider(requireActivity()).get(SheepViewModel.class);
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
         //measureViewModel = new ViewModelProvider(this).get(MeasureViewModel.class);
        binding = FragmentMeasureBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView textViewMeasure = binding.textViewMeasure;
        TextView textViewSystem = binding.textViewSystem;
        Button buttonMeasure = binding.buttonMeasure;
        SwitchCompat switchCalibrate = binding.switchCalibrate;
        Button buttonTare = binding.buttonTare;
        Button buttonSave = binding.buttonSave;
        Button buttonSleepWake = binding.buttonSleepWake;

        // Observe the measureText LiveData from the shared ViewModel &
        // Update the TextView with the new measureText value
        sharedViewModel.getMeasureText().observe(getViewLifecycleOwner(), textViewMeasure::setText);

        // Observe the systemText LiveData from the shared ViewModel &
        // Update the TextView with the new systemText value
        sharedViewModel.getSystemText().observe(getViewLifecycleOwner(), textViewSystem::setText);



        // Observe if connected or not and adjust btn click-ability
        sharedViewModel.getIsConnected().observe(getViewLifecycleOwner(), isConnected -> {
            buttonMeasure.setEnabled(isConnected);
            buttonTare.setEnabled(isConnected);
            buttonSleepWake.setEnabled(isConnected);
        });


        //set up the buttons
        buttonMeasure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                sharedViewModel.setSystemText("Requesting Measurement...");
                //sharedViewModel.setMeasureText("123.45");
                String messageToSend; //1 for measurement and 0 for calibration
                if (switchCalibrate.isChecked()) {
                    messageToSend = "11\n";
                } else {
                    messageToSend = "10\n";
                }
                handlerMain.obtainMessage(MESSAGE_SEND, messageToSend).sendToTarget();
            }
        });

        buttonTare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                sharedViewModel.setSystemText("Requesting Tare...");
                String messageToSend;
                if (switchCalibrate.isChecked()) {
                    messageToSend = "21\n";
                } else {
                    messageToSend = "20\n";
                }
                handlerMain.obtainMessage(MESSAGE_SEND, messageToSend).sendToTarget();
            }
        });

        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                sharedViewModel.setSystemText("Saving Data...");
                DataAlertDialog.showInsertDialog(requireContext(), sharedViewModel.getMeasureText().getValue(), new DataAlertDialog.InsertDataListener() {
                    @Override
                    public void onDataInserted(SheepMeasurement newSheepMeasurement) {
                        sheepViewModel.insert(newSheepMeasurement);
                    }

                });

            }
        });

        buttonSleepWake.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                if(sharedViewModel.getIsAwake()){
                    sharedViewModel.setSystemText("Sleeping...");
                    String messageToSend = "3\n";
                    handlerMain.obtainMessage(MESSAGE_SEND, messageToSend).sendToTarget();
                }else{
                    sharedViewModel.setSystemText("Waking...");
                    String messageToSend = "3\n";
                    handlerMain.obtainMessage(MESSAGE_SEND, messageToSend).sendToTarget();

                }
            }
        });

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}