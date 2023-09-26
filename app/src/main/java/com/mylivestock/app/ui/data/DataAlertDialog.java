package com.mylivestock.app.ui.data;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import java.text.SimpleDateFormat;

import com.mylivestock.app.R;
import com.mylivestock.app.data.database.entities.SheepMeasurement;

public class DataAlertDialog {

    private static final String TAG = "DataAlertDialog";

    //Interface for dataAlertDialog on completion
    public interface EditDataListener {
        void onDataEdited(SheepMeasurement editedsheepMeasurement);
        void onDataDeleted(SheepMeasurement deletedSheepMeasurement);
    }
    public interface InsertDataListener {
        void onDataInserted(SheepMeasurement newSheepMeasurement);
    }

    //dataFragment recyclerView listAdapter -> edit SheepMeasurement Entity instance inside dataBase
    public static void showEditDialog(Context context, SheepMeasurement editSheepMeasurement, EditDataListener listener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_edit_data, null);
        builder.setView(dialogView);
        //Find
        TextView dataAlertDialog_title = dialogView.findViewById(R.id.dataAlertDialog_title);
        EditText editSheepID = dialogView.findViewById(R.id.editSheepID);
        EditText editSheepName = dialogView.findViewById(R.id.editSheepName);
        EditText editSheepWeight = dialogView.findViewById(R.id.editSheepWeight);
        EditText editSheepNote = dialogView.findViewById(R.id.editSheepNote);
        //Set
        dataAlertDialog_title.setText(R.string.dataAlertDialog_title_edit_data);
        editSheepID.setText(editSheepMeasurement.getSheepId());
        editSheepName.setText(editSheepMeasurement.getSheepName());
        editSheepWeight.setText(editSheepMeasurement.getSheepWeight_string());
        editSheepNote.setText(editSheepMeasurement.getUserNote());
        //-- BTNs---
        builder.setPositiveButton("Update", (dialog, which) -> {
            SheepMeasurement editedSheepMeasurement = new SheepMeasurement(
                    editSheepMeasurement.timestamp,
                    editSheepID.getText().toString(),
                    editSheepName.getText().toString(),
                    Float.parseFloat(editSheepWeight.getText().toString()),
                    editSheepNote.getText().toString()
            );
            //Throw to interface listener
            if (listener != null) {
                listener.onDataEdited(editedSheepMeasurement);
            } else {
                Log.e(TAG, "showEditDialog: listener is null");
            }
            dialog.dismiss();
        });

        builder.setNeutralButton("Cancel", (dialog, which) -> dialog.dismiss());

        builder.setNegativeButton("Delete", (dialog, which) -> {
            //Throw to interface listener
            if (listener != null) {
                listener.onDataDeleted(editSheepMeasurement);
            } else {
                Log.e(TAG, "showEditDialog: listener is null");
            }
        });
        //--BTNS---

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    //measurementFragment save a sheepMeasurement data instance to database
    public static void showInsertDialog(Context context, String MeasurementWeight_string, InsertDataListener listener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_edit_data, null);
        builder.setView(dialogView);

        TextView dataAlertDialog_title = dialogView.findViewById(R.id.dataAlertDialog_title);
        EditText editSheepID = dialogView.findViewById(R.id.editSheepID);
        EditText editSheepName = dialogView.findViewById(R.id.editSheepName);
        EditText editSheepWeight = dialogView.findViewById(R.id.editSheepWeight);
        EditText editSheepNote = dialogView.findViewById(R.id.editSheepNote);

        dataAlertDialog_title.setText(R.string.dataAlertDialog_title_insert_data);
        editSheepWeight.setText(MeasurementWeight_string);

        builder.setPositiveButton("Save", (dialog, which) -> {

            SheepMeasurement newSheepMeasurement = new SheepMeasurement(
                    System.currentTimeMillis(),
                    editSheepID.getText().toString(),
                    editSheepName.getText().toString(),
                    Float.parseFloat(editSheepWeight.getText().toString()),
                    editSheepNote.getText().toString()
            );

            if (listener != null) {
                listener.onDataInserted(newSheepMeasurement);
            }

            dialog.dismiss();
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


}
