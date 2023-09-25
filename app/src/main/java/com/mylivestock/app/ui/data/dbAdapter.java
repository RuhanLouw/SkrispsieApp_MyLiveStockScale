package com.mylivestock.app.ui.data;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.LiveData;
import androidx.recyclerview.widget.RecyclerView;

import com.mylivestock.app.R;
import com.mylivestock.app.SharedViewModel;
import com.mylivestock.app.data.database.entities.SheepMeasurement;
import com.mylivestock.app.data.repository.SheepViewModel;

import java.util.List;

public class dbAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final Context context;
    private List<SheepMeasurement> allMeasurmentsList;
    SheepViewModel sheepViewModel;

    public static class ViewHolder extends RecyclerView.ViewHolder{

        LinearLayout linearLayout_db;
        TextView textView_db_sheepId;
        TextView textView_db_sheepName;
        TextView textView_db_sheepDate;
        TextView textView_db_sheepNote;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            linearLayout_db = itemView.findViewById(R.id.linearLayout_db);
            textView_db_sheepId = itemView.findViewById(R.id.textView_db_id);
            textView_db_sheepName = itemView.findViewById(R.id.textView_db_name);
            textView_db_sheepDate = itemView.findViewById(R.id.textView_db_date);
            textView_db_sheepNote = itemView.findViewById(R.id.textView_db_note);
        }
    }
    public dbAdapter(Context context, List<SheepMeasurement> allMeasurementsList, SheepViewModel _sheepViewModel){
        this.context = context;
        this.allMeasurmentsList = allMeasurementsList;
        this.sheepViewModel = _sheepViewModel;
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.db_entity_layout, parent, false);
        return new ViewHolder(v);
    }
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ViewHolder dbEntityHolder = (ViewHolder) holder;
        SheepMeasurement sheepMeasurement = allMeasurmentsList.get(position);
        dbEntityHolder.textView_db_sheepId.setText(sheepMeasurement.getId());
        dbEntityHolder.textView_db_sheepName.setText(sheepMeasurement.getSheepName());
        dbEntityHolder.textView_db_sheepDate.setText(sheepMeasurement.getTimestamp());
        dbEntityHolder.textView_db_sheepNote.setText(sheepMeasurement.getUserNote());

        //TODO: Add click listener
            //TODO: functions to return data, that has been selected, to fragment for editDataDialog
        dbEntityHolder.linearLayout_db.setOnClickListener((v) -> {
            //alertDialog
                //edit data
            //update all measurements if data changed
            DataAlertDialog.showEditDialog(context, sheepMeasurement, editedsheepMeasurement -> {
                //TODO: check that it actually updates!!!!
                sheepViewModel.update(editedsheepMeasurement);

            });

        });
    }



    @Override
    public int getItemCount() {
        return allMeasurmentsList.size();
    }

}
