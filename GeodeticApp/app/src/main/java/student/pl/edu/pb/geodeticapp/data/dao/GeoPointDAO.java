package student.pl.edu.pb.geodeticapp.data.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import student.pl.edu.pb.geodeticapp.data.GeoDBHelper;
import student.pl.edu.pb.geodeticapp.data.entities.GeoPoint;


public class GeoPointDAO implements GenericDAO<GeoPoint> {
    private static final String TABLE_NAME = "POINTS";
    private static final String[] ALL_COLUMNS = new String[]{"ID", "NAME", "LONGITUDE", "LATITUDE", "XGK", "YGK"};

    private GeoDBHelper dbHelper;
    private SQLiteDatabase database;

    public GeoPointDAO(GeoDBHelper dbHelper) {
        this.dbHelper = dbHelper;
        this.database = dbHelper.getWritableDatabase();
    }

    @Override
    public GeoPoint getByID(long id) {
        Cursor result = database.query(TABLE_NAME, ALL_COLUMNS, "ID = ?", new String[]{Long.toString(id)}, null, null, null);
        if (result.moveToNext()) {
            GeoPoint resultPoint = extractGeoPointFromCursor(result);
            result.close();
            return resultPoint;
        }
        return null;
    }

    @Override
    public List<GeoPoint> getAll() {
        Cursor result = database.query(TABLE_NAME, ALL_COLUMNS, null, null, null, null, null);
        List<GeoPoint> resultList = new ArrayList<>();
        while (result.moveToNext()) {
            resultList.add(extractGeoPointFromCursor(result));
        }
        result.close();
        return resultList;
    }

    @Override
    public long save(GeoPoint entity) {
        ContentValues values = new ContentValues();
        values.put(ALL_COLUMNS[1], entity.getName());
        values.put(ALL_COLUMNS[2], entity.getLatitude());
        values.put(ALL_COLUMNS[3], entity.getLongitude());
        values.put(ALL_COLUMNS[4], entity.getxGK());
        values.put(ALL_COLUMNS[5], entity.getyGK());
        return database.insert(TABLE_NAME, null, values);
    }

    @Override
    public void update(GeoPoint entity) {
        ContentValues values = new ContentValues();
        values.put(ALL_COLUMNS[1], entity.getName());
        values.put(ALL_COLUMNS[2], entity.getLatitude());
        values.put(ALL_COLUMNS[3], entity.getLongitude());
        values.put(ALL_COLUMNS[4], entity.getxGK());
        values.put(ALL_COLUMNS[5], entity.getyGK());
        database.update(TABLE_NAME, values, "ID = ?", new String[]{Long.toString(entity.getId())});
    }

    @Override
    public void deleteByID(long id) {
        database.delete(TABLE_NAME, "ID = ?", new String[]{Long.toString(id)});
    }

    private GeoPoint extractGeoPointFromCursor(Cursor cursor) {
        return new GeoPoint(cursor.getLong(0), cursor.getString(1), cursor.getDouble(2), cursor.getDouble(3), cursor.getDouble(4), cursor.getDouble(5));
    }
}
