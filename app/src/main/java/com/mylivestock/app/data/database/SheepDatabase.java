package com.mylivestock.app.data.database;

import android.content.Context;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.mylivestock.app.data.database.dao.SheepMeasurementDao;
import com.mylivestock.app.data.database.entities.SheepMeasurement;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {SheepMeasurement.class}, version = 1)
public abstract class SheepDatabase extends RoomDatabase {

    public abstract SheepMeasurementDao sheepMeasurementDao();
    //singleton instance of SheepDatabase
    private static volatile SheepDatabase _INSTANCE;
    private static final int NUM_THREADS = 2;
    public static final ExecutorService dbExecutor = Executors.newFixedThreadPool(NUM_THREADS);

    public static SheepDatabase getSheepDatabase(final Context context) {
        if (_INSTANCE == null) {
            synchronized (SheepDatabase.class) {
                if (_INSTANCE == null) {
                    _INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            SheepDatabase.class, "sheep_database")
                            .fallbackToDestructiveMigration()
                            .addCallback(prePopRoomCallback)
                            .build();
                }
            }
        }
        return _INSTANCE;
    }

    //use only when a pre populated db is needed
    // below line is to create a callback for our room database.
    private static final RoomDatabase.Callback prePopRoomCallback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            // this method is called when database is created
            // and below line is to populate our data.
            dbExecutor.execute(() -> {
                SheepMeasurementDao dao = _INSTANCE.sheepMeasurementDao();
                SheepMeasurement sheepMeasurement1 = new SheepMeasurement(1632514800000L, "12345", "Fluffy", 45.22f, "Healthy and active");
                SheepMeasurement sheepMeasurement2 = new SheepMeasurement(1641379200000L, "67890", "Buddy", 55.86f, "Needs more exercise");
                SheepMeasurement sheepMeasurement3 = new SheepMeasurement(1627353600000L, "54321", "Woolly", 40.54f, "Eating well, no issues");
                SheepMeasurement sheepMeasurement4 = new SheepMeasurement(1639986000000L, "98765", "Sunny", 47.31f, "Recent vaccination");
                SheepMeasurement sheepMeasurement5 = new SheepMeasurement(1646212800000L, "13579", "Cotton", 51.11f, "Frequent grazing");

                dao.insertSheepMeasurement(sheepMeasurement1);
                dao.insertSheepMeasurement(sheepMeasurement2);
                dao.insertSheepMeasurement(sheepMeasurement3);
                dao.insertSheepMeasurement(sheepMeasurement4);
                dao.insertSheepMeasurement(sheepMeasurement5);

            });
        };
    };
//
//    // we are creating an task class using Executors (asynctask deprecated) to perform task in background.
//    private static class PopulateDbAsyncTask extends AsyncTask<Void, Void, Void> {
//        PopulateDbAsyncTask(SheepDatabase instance) {
//            SheepMeasurementDao dao = instance.sheepMeasurementDao();
//        }
//        @Override
//        protected Void doInBackground(Void... voids) {
//            return null;
//        }
//
//    }



}
