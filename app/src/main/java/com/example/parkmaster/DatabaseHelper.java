package com.example.parkmaster;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.Cursor;
import android.util.Log;
import android.util.Pair;

import androidx.annotation.Nullable;

import java.util.ArrayList;

// if you want to get all one column data from a cursor. Use:
//        ArrayList<WhateverTypeYouWant> mArrayList = new ArrayList<WhateverTypeYouWant>();
//        for(mCursor.moveToFirst(); !mCursor.isAfterLast(); mCursor.moveToNext()) {
//
//              mArrayList.add(mCursor.getWhateverTypeYouWant(WHATEVER_COLUMN_INDEX_YOU_WANT));
//        }


public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "data.db";
    private static final String TABLE_NAME = "app_table";
    private static final String COL_1 = "ID";
    private static final String COL_2 = "Name";
    private static final String COL_3 = "Longitude";
    private static final String COL_4 = "Latitude";
    private static final String COL_5 = "S_time"; // Starting time
    private static final String COL_6 = "E_time"; // Ending time written in format YYYYMMDDHHmm
    private static final String COL_7 = "Price";
    private static final String COL_8 = "QR";


    public DatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, 13); // If you want to clean database, just increment the version number and re-run app like from 9 to 10
        SQLiteDatabase db = this.getWritableDatabase();
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_NAME + " (ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "Name TEXT, Longitude REAL, Latitude REAL, S_time REAL, E_time REAL, Price REAL, QR TEXT)");
    }

    // deletes data if higher version is detected
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);

    }

    // deletes table manually
    public void deleteDB() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
    }


    // insert a row into table. Returns true if success, else false.
    public boolean insertData(String name, double Longitude, double Latitude, double S_time, double E_time, double Price, String QR) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_2, name);
        contentValues.put(COL_3, Longitude);
        contentValues.put(COL_4, Latitude);
        contentValues.put(COL_5, S_time);
        contentValues.put(COL_6, E_time);
        contentValues.put(COL_7, Price);
        contentValues.put(COL_8, QR);
        long result = db.insert(TABLE_NAME, null, contentValues);
        if (result == -1)
            return false;
        else
            return true;
    }

    // gets all data, returns a cursor. If empty returns null.
    public Cursor getAllData() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from " + TABLE_NAME, null);
        if (res != null) {
            res.moveToFirst(); // VERY IMPORTANT
            if (!res.isAfterLast())
                return res;
        }

        return null;
    }

    // get all unique locations
    // this returns a cursor type object. To access the longitude and latitude, use cursor.getDouble() function.
    // cursor.getDouble(0) should return the longitude. cursor.getDouble(1) should return the latitude
    public Cursor getLoca() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select distinct Longitude, Latitude from " + TABLE_NAME, null);
        if (res != null) {
            res.moveToFirst(); // VERY IMPORTANT
            if (!res.isAfterLast())
                return res;
        }

        return null;
    }


    // get all unique locations. same as above method
    // this method returns an arraylist rather than a cursor type
    // method returns null if nothing is found
    public ArrayList<Pair<Double, Double>> getLocArray() {
        ArrayList<Pair<Double, Double>> arr = new ArrayList<Pair<Double, Double>>();
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select distinct Longitude, Latitude from " + TABLE_NAME, null);
        if (res != null) {
            res.moveToFirst(); // VERY IMPORTANT
            if (res.isAfterLast())
                return null;
        } else return null;


        Pair<Double, Double> temp;
        Double d1, d2;
        for (res.moveToFirst(); !res.isAfterLast(); res.moveToNext()) {
            d1 = res.getDouble(0);
            d2 = res.getDouble(1);
            temp = new Pair(d1, d2);
            arr.add(temp);
        }

        return arr;
    }

    // get Starting time given longitude and latitude
    public Cursor getS_time(double Lon, double Lat) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select S_time from app_table where Longitude =  ? AND Latitude = ? ", new String[]{Lon + "", Lat + ""});

        if (res != null) {
            res.moveToFirst(); // VERY IMPORTANT
            if (!res.isAfterLast())
                return res;
        }

        return null;
    }


    // get cursor pointing to list of Ending time with given longitude and latitude
    public Cursor getE_time(double Lon, double Lat) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select E_time from app_table where Longitude =  ? AND Latitude = ? ", new String[]{Lon + "", Lat + ""});
        if (res != null) {
            res.moveToFirst(); // VERY IMPORTANT
            if (!res.isAfterLast())
                return res;
        }
        return null;
    }

    // gets cursor pointing to list IDs with given longitude and latitude. If empty returns null.
    public Cursor getID(double Lon, double Lat) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select ID from app_table where Longitude =  ? AND Latitude = ? ", new String[]{Lon + "", Lat + ""});
        if (res != null) {
            res.moveToFirst(); // VERY IMPORTANT
            if (!res.isAfterLast())
                return res;
        }
        return null;
    }

    // gets Cursor pointing to list of QR Codes with given name
    public Cursor getQRwName(String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select QR from app_table where Name = ? ", new String[]{name});
        if (res != null) {
            res.moveToFirst(); // VERY IMPORTANT
            if (!res.isAfterLast())
                return res;
        }
        return res;
    }

    // gets Cursor pointing to list of QR Codes with given Location and Time
    public Cursor getQRwLT(double Lon, double Lat, double S, double E) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select QR from app_table where Longitude =  ? AND Latitude = ? AND S_time = ? AND E_time = ? ",
                new String[]{Lon + "", Lat + "", S + "", E + ""});
        if (res != null) {
            res.moveToFirst(); // VERY IMPORTANT
            if (!res.isAfterLast())
                return res;
        }
        return res;
    }


    // gets Cursor pointing to list of data with given QR
    public Cursor getDatawQR(String qr) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select Name, Longitude, Latitude, S_time, E_time, Price from app_table where QR = ? ",
                new String[]{qr});
        if (res != null) {
            res.moveToFirst(); // VERY IMPORTANT
            if (!res.isAfterLast())
                return res;
        }
        return res;
    }


    // gets cursor pointing to list of Name with given QR
    public Cursor getNamewQR(String qr) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select Name from app_table where qr = ? ", new String[]{qr});
        if (res != null) {
            res.moveToFirst(); // VERY IMPORTANT
            if (!res.isAfterLast())
                return res;
        }
        return res;
    }


    // gets Cursor pointing to list of data with given name
    public Cursor getDatawName(String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select Name, Longitude, Latitude, S_time, E_time, Price, QR from app_table where name = ? ",
                new String[]{name});
        if (res != null) {
            res.moveToFirst(); // VERY IMPORTANT
            if (!res.isAfterLast())
                return res;
        }
        return res;
    }


    // returns cursor pointing to list Prices with given longitude and latitude. If empty returns null.
    public Cursor getPrice(double Lon, double Lat) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select Price from app_table where Longitude =  ? AND Latitude = ? ", new String[]{Lon + "", Lat + ""});
        if (res != null) {
            res.moveToFirst(); // VERY IMPORTANT
            if (!res.isAfterLast())
                return res;
        }
        return null;
    }

    // returns cursor pointing to list of id with given longitude and latitude also within a period
    public Cursor getID_withPrd(double Lon, double Lat, double Start, double End) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery
                ("select ID from app_table where Longitude =  ? AND Latitude = ? AND E_time > ? AND E_time < ? " +
                                "UNION" +
                                " select ID from app_table where Longitude =  ? AND Latitude = ? AND S_time < ? AND S_time > ?",
                        new String[]{Lon + "", Lat + "", Start + "", End + "", Lon + "", Lat + "", End + "", Start + ""});
        if (res != null) {
            res.moveToFirst(); // VERY IMPORTANT
            if (!res.isAfterLast())
                return res;
        }
        return null;
    }


    // get location using id
    public Pair<Double, Double> getLoca_ID(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select Longitude, Latitude from " + TABLE_NAME + " where ID = ? ", new String[]{id + ""});
        if (res != null) {
            res.moveToFirst(); // VERY IMPORTANT
            if (res.isAfterLast())
                return null;
        }
        return new Pair(res.getDouble(0), res.getDouble(1));
    }

    public Cursor getCLoca_ID(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select Longitude, Latitude from " + TABLE_NAME + " where ID = ? ", new String[]{id + ""});
        if (res != null) {
            res.moveToFirst(); // VERY IMPORTANT
            if (res.isAfterLast())
                return null;
        }
        return res;
    }

    // get starting and ending time using id
    public Pair<Double, Double> getTime_ID(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select S_time, E_time from " + TABLE_NAME + " where ID = ? ", new String[]{id + ""});
        if (res != null) {
            res.moveToFirst(); // VERY IMPORTANT
            if (res.isAfterLast())
                return null;
        }
        return new Pair(res.getDouble(0), res.getDouble(1));
    }


    // Checks availability of a parking spot during a time
    public boolean CheckAvail(double Lon, double Lat, double S_time, double E_time) {
        Cursor sC = this.getS_time(Lon, Lat);
        Cursor eC = this.getE_time(Lon, Lat);

        long sT;
        long eT;

        while (!sC.isAfterLast()) {
            sT = sC.getLong(0);
            eT = eC.getLong(0);

            if (sT < S_time) {
                if (eT >= S_time) {
                    Log.d("1", "1");
                    return false;

                }

            } else if (sT == S_time) {
                Log.d("2", "2");
                return false;


            } else {  //sT > S_Time
                if (sT <= E_time) {
                    Log.d("3", "3");
                    return false;

                }

            }

            sC.move(1);
            eC.move(1);

        }

        return true;
    }

    // delete a row with that particular id, returns 1 if succeeds
    public Integer deleteData(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_NAME, "ID = ?", new String[]{id});
    }

}


