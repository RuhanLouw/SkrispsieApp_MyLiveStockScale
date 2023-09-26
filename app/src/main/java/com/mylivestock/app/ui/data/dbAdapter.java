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
    private final SheepViewModel sheepViewModel;

    public static class ViewHolder extends RecyclerView.ViewHolder{

        LinearLayout linearLayout_db;
        TextView textViewDBid;
        TextView textViewDBname;
        TextView textViewDBdate;
        TextView textViewDBweight;
        TextView textViewDBnote;

        public ViewHolder(@NonNull View v) {
            super(v);
            linearLayout_db = v.findViewById(R.id.linearLayout_db);
            textViewDBid = v.findViewById(R.id.textViewDBid);
            textViewDBname = v.findViewById(R.id.textViewDBname);
            textViewDBdate = v.findViewById(R.id.textViewDBdate);
            textViewDBweight = v.findViewById(R.id.textViewDBweight);
            textViewDBnote = v.findViewById(R.id.textViewDBnote);
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
        dbEntityHolder.textViewDBdate.setText(sheepMeasurement.getTimestamp());
        dbEntityHolder.textViewDBid.setText(sheepMeasurement.getSheepId());
        dbEntityHolder.textViewDBname.setText(sheepMeasurement.getSheepName());
        dbEntityHolder.textViewDBweight.setText(sheepMeasurement.getSheepWeight_string());
        dbEntityHolder.textViewDBnote.setText(sheepMeasurement.getUserNote());


        //click listener
            //functions to return data, that has been selected, to fragment for editDataDialog
        dbEntityHolder.linearLayout_db.setOnClickListener((v) -> {
            //alertDialog
                //edit data -> sheepMeasurement
            //update all measurements if data changed
            DataAlertDialog.showEditDialog(context, sheepMeasurement, new DataAlertDialog.EditDataListener() {
                @Override
                public void onDataEdited(SheepMeasurement editedsheepMeasurement) {
                    sheepViewModel.update(editedsheepMeasurement);
            }
                @Override
                public void onDataDeleted(SheepMeasurement deletedSheepMeasurement) {
                    AlertDialog.Builder deleteBuilder = new AlertDialog.Builder(context);
                    deleteBuilder.setTitle("Delete SheepMeasurement");
                    deleteBuilder.setMessage("Are you sure you want to delete this SheepMeasurement Instance?");

                    deleteBuilder.setPositiveButton("YES", (dialog1, which1) -> {
                        sheepViewModel.delete(deletedSheepMeasurement);
                        dialog1.dismiss();
                    });

                    deleteBuilder.setNegativeButton("NO", (dialog1, which1) -> dialog1.dismiss());
                    AlertDialog deleteDialog = deleteBuilder.create();
                    deleteDialog.show();
                }
            });
        });
    }



    @Override
    public int getItemCount() {
        return allMeasurmentsList.size();
    }

}
