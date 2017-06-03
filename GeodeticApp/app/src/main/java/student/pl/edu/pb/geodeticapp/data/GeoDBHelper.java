package student.pl.edu.pb.geodeticapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class GeoDBHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "geoApp.db";
    private static final String DB_TAG = "Database";
    private static final int DB_VERSION = 1;

    private static final String CREATE_POINTS_TABLE_SQL = "CREATE TABLE POINTS (\n" +
            "  ID INTEGER PRIMARY KEY,\n" +
            "  NAME TEXT,\n" +
            "  LATITUDE REAL,\n" +
            "  LONGITUDE REAL,\n" +
            "  XGK REAL,\n" +
            "  YGK REAL\n" +
            ");";

    public GeoDBHelper(Context context){
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_POINTS_TABLE_SQL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(DB_TAG, String.format("Database upgraded from %d to %d", oldVersion, newVersion));
    }
}
