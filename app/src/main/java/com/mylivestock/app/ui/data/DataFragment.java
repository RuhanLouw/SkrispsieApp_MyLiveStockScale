package com.mylivestock.app.ui.data;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mylivestock.app.SharedViewModel;
import com.mylivestock.app.data.database.entities.SheepMeasurement;
import com.mylivestock.app.data.repository.SheepViewModel;
import com.mylivestock.app.databinding.FragmentDataBinding;

import java.util.List;

public class DataFragment extends Fragment {

    private FragmentDataBinding binding;
    public SharedViewModel sharedViewModel;
    public SheepViewModel sheepViewModel;
    private DataViewModel dataViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        DataViewModel dataViewModel = new ViewModelProvider(requireActivity()).get(DataViewModel.class);
        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        sheepViewModel = new ViewModelProvider(requireActivity()).get(SheepViewModel.class);


        binding = FragmentDataBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        //--
        RecyclerView recyclerViewDB = binding.recyclerViewDB;
        recyclerViewDB.setLayoutManager(new LinearLayoutManager(requireContext()));

        sheepViewModel.getAllSheepMeasurements().observe(getViewLifecycleOwner(), allSheepMeasurmentsList -> {
            recyclerViewDB.setAdapter(new dbAdapter(requireContext(), allSheepMeasurmentsList, sheepViewModel));
            recyclerViewDB.setItemAnimator(new DefaultItemAnimator());
        });

        //--
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}