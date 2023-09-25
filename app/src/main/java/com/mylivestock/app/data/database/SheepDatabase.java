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
//                            .addCallback(roomCallback)
                            .build();
                }
            }
        }
        return _INSTANCE;
    }

// use only when a pre populated db is needed
//    // below line is to create a callback for our room database.
//    private static RoomDatabase.Callback roomCallback = new RoomDatabase.Callback() {
//        @Override
//        public void onCreate(@NonNull SupportSQLiteDatabase db) {
//            super.onCreate(db);
//            // this method is called when database is created
//            // and below line is to populate our data.
//            dbExecutor.execute(new Runnable() {
//                @Override
//                public void run() {
//
//                }
//            });
//        }
//    };
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
