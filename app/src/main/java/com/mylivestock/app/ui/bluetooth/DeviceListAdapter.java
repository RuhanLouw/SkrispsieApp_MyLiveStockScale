package com.mylivestock.app.ui.bluetooth;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.RecyclerView;

import com.mylivestock.app.ConnectionInit;
import com.mylivestock.app.R;
import com.mylivestock.app.SharedViewModel;

import java.util.List;

public class DeviceListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private List<Object> deviceList;
    private Fragment fragmentMove;
    public ConnectionInit connectionInit;

    private SharedViewModel sharedViewModel;


    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textName, textAddress;
        LinearLayout linearLayout;

        public ViewHolder(View v) {
            super(v);
            textName = v.findViewById(R.id.textViewDeviceName);
            textAddress = v.findViewById(R.id.textViewDeviceHardwareAddress);
            linearLayout = v.findViewById(R.id.linearLayoutDeviceInfo);
        }
    }

    public DeviceListAdapter(Context context, Fragment fragment, List<Object> deviceList) {
        this.context = context;
        this.deviceList = deviceList;
        fragmentMove = fragment;
        //just use the fragment(to)Move to get parent activity
        sharedViewModel = new ViewModelProvider(fragment.requireActivity()).get(SharedViewModel .class);
    }

    @NonNull
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.device_info_layout, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, final int position) {
        ViewHolder itemHolder = (ViewHolder) holder;
        final DeviceInfoModel deviceInfoModel = (DeviceInfoModel) deviceList.get(position);
        itemHolder.textName.setText(deviceInfoModel.getDeviceName());
        itemHolder.textAddress.setText(deviceInfoModel.getDeviceHardwareAddress());

        itemHolder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                        //Start connection process
                        //connectionInit = new ConnectionInit(deviceInfoModel.getDeviceName(), deviceInfoModel.getDeviceHardwareAddress());
                //Notify of connection initiated and pass selected device to sharedViewModel
                sharedViewModel.setDeviceInfoModel(deviceInfoModel);
                sharedViewModel.setTryingToConnectBT(true);
                NavHostFragment.findNavController(fragmentMove).navigate(R.id.nav_measure);


                //Has a connection already been initiated?
                //Possible bug, need to check redundancy
            }
        });
    }

    @Override
    public int getItemCount() {
        return deviceList.size();
    }

}
