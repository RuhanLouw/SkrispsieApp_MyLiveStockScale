package com.mylivestock.app.ui.measure;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;


import com.mylivestock.app.SharedViewModel;
import com.mylivestock.app.databinding.FragmentMeasureBinding;

public class MeasureFragment extends Fragment {

    public static MeasureViewModel measureViewModel;

    private FragmentMeasureBinding binding;
    private SharedViewModel sharedViewModel;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Retrieve SharedViewModel
        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
         //measureViewModel = new ViewModelProvider(this).get(MeasureViewModel.class);

        binding = FragmentMeasureBinding.inflate(inflater, container, false);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button buttonMeasure = binding.buttonMeasure;
        TextView textViewMeasure = binding.textViewMeasure;
        TextView textViewSystem = binding.textViewSystem;



        // Observe the measureText LiveData from the shared ViewModel &
        // Update the TextView with the new measureText value
        sharedViewModel.getMeasureText().observe(getViewLifecycleOwner(), textViewMeasure::setText);

        // Observe the systemText LiveData from the shared ViewModel &
        // Update the TextView with the new systemText value
        sharedViewModel.getSystemText().observe(getViewLifecycleOwner(), textViewSystem::setText);

        //if BtConnection not trying to connect?

        buttonMeasure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                sharedViewModel.setMeasureText("128.63");
                //connectedThread().write("requestMeasure");
                sharedViewModel.setRequestMeasure(true);
            }
        });

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}