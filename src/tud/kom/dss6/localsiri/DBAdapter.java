package tud.kom.dss6.localsiri;

import android.content.ContentValues;
import android.content.Context;

import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Hariharan on 11/19/2014.
 */
public class DBAdapter extends SQLiteOpenHelper{

    private SQLiteDatabase db;
    private SQLiteOpenHelper dbHelper;

    public static final String TAG = "DBAdapter";
    private static final String DB_NAME = "dbLocation";
    private static final String DB_TABLE_NAME = "tblCoordinates";
    private static final String DB_TABLE_CREATE =
            "CREATE TABLE " + DB_TABLE_NAME +
                    " (date TEXT, latitude TEXT, longitude TEXT);";
    private static final String DB_TABLE_READ_ALL_GEOPOINTS =
            "SELECT * FROM " + DB_TABLE_NAME +
                    " ORDER BY 1 DESC";

    MainLocation geo;

    /* Constructor for SQLiteOpenHelper */
    public DBAdapter(Context applicationContext) {
        super(applicationContext, DB_NAME, null,1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            db.execSQL(DB_TABLE_CREATE);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int v_old, int v_new) {
        Log.w(TAG, "Database Upgrade From Version " + v_old + " to "
                + v_new + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + DB_TABLE_NAME);
        onCreate(db);
    }


    public void insertGeoPoints(HashMap<String, String> geoPointsSet){

        SQLiteDatabase DB_w = this.getWritableDatabase();

        ContentValues geoContents = new ContentValues();
        geoContents.put(geo.KEY_DATE,geoPointsSet.get(geo.KEY_DATE));
        geoContents.put(geo.KEY_LATITUDE,geoPointsSet.get(geo.KEY_LATITUDE));
        geoContents.put(geo.KEY_LONGITUDE,geoPointsSet.get(geo.KEY_LONGITUDE));

        DB_w.insert(DB_TABLE_NAME, null, geoContents);
        DB_w.close();

    }

    public ArrayList<HashMap<String, String>> getSavedGeoPoints(){
        ArrayList<HashMap<String, String>> geoArrayList = new ArrayList<HashMap<String, String>>();

        SQLiteDatabase DB_r = this.getReadableDatabase();

        Cursor cursor = DB_r.rawQuery(DB_TABLE_READ_ALL_GEOPOINTS, null);

        if(cursor.moveToFirst()) {

            do {
                HashMap<String, String> geoPoints = new HashMap<String, String>();

                geoPoints.put(geo.KEY_DATE,cursor.getString(0));
                geoPoints.put(geo.KEY_LATITUDE,cursor.getString(1));
                geoPoints.put(geo.KEY_LONGITUDE,cursor.getString(2));

                geoArrayList.add(geoPoints);
            }while(cursor.moveToNext());
        }
        return geoArrayList;
    }

}
