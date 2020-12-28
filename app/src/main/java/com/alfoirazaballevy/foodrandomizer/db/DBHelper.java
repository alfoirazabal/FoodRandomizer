package com.alfoirazaballevy.foodrandomizer.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.alfoirazaballevy.foodrandomizer.domains.Food;
import com.alfoirazaballevy.foodrandomizer.domains.Food;

import java.nio.file.Paths;
import java.util.ArrayList;

public class DBHelper extends SQLiteOpenHelper {

    SQLiteDatabase db;

    private static final String DB_NAME = "foods.db";
    private static final int DB_VERSION = 1;

    private static final String TABLE_FOODS = "_foods";

    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_DESCRIPTION = "description";
    private static final String KEY_SINTACC = "sintacc"; //1 True or 0 False
    private static final String KEY_FOODTYPE = "foodtype";  //1 CAR, 2 VEGE, 3 VEGAN

    public DBHelper(Context context) {
        super(
                context,
                context.getApplicationContext().getExternalFilesDir("dbData").getAbsolutePath() + "/" +
                        DB_NAME,
                null,
                DB_VERSION
        );
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String Query_Table = "CREATE TABLE " + TABLE_FOODS + "(" +
                KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                KEY_NAME + " TEXT, " +
                KEY_DESCRIPTION + " TEXT, " +
                KEY_SINTACC + " TINYINT, " +
                KEY_FOODTYPE + " TINYINT);";
        db.execSQL(Query_Table);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //Not yet...
    }

    public long insertFood(String name, byte tipo, byte sinTACC, String descripcion) {
        db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(KEY_NAME, name);
        cv.put(KEY_FOODTYPE, tipo);
        cv.put(KEY_SINTACC, sinTACC);
        cv.put(KEY_DESCRIPTION, descripcion);
        return db.insert(TABLE_FOODS, null, cv);
    }

    public ArrayList<Food> getFoods() {
        db = this.getReadableDatabase();
        String[] columns = new String[]{
                KEY_ID,
                KEY_NAME,
                KEY_DESCRIPTION,
                KEY_SINTACC,
                KEY_FOODTYPE
        };
        Cursor cursor = db.query(
                TABLE_FOODS, columns, null, null, null,
                null, null
        );
        int iId = cursor.getColumnIndex(KEY_ID);
        int iName = cursor.getColumnIndex(KEY_NAME);
        int iDescription = cursor.getColumnIndex(KEY_DESCRIPTION);
        int iSinTACC = cursor.getColumnIndex(KEY_SINTACC);
        int iFoodType = cursor.getColumnIndex(KEY_FOODTYPE);

        ArrayList<Food> foods = new ArrayList<>();

        for(cursor.moveToLast() ; !cursor.isBeforeFirst() ; cursor.moveToPrevious()) {
            long id = cursor.getLong(iId);
            String name = cursor.getString(iName);
            String description = cursor.getString(iDescription);
            byte sinTACC = (byte)cursor.getInt(iSinTACC);
            byte foodType = (byte)cursor.getInt(iFoodType);
            foods.add(new Food(
                    id, name, description, sinTACC, foodType
            ));
        }
        db.close();
        return foods;
    }

    public Food getFood(long foodId) {
        Food food = null;
        db = this.getReadableDatabase();
        String[] columns = new String[]{
                KEY_ID,
                KEY_NAME,
                KEY_DESCRIPTION,
                KEY_SINTACC,
                KEY_FOODTYPE
        };
        Cursor cursor = db.query(
                TABLE_FOODS, columns, KEY_ID + " = " + foodId,
                null, null,null, null
        );
        if(cursor != null) {
            cursor.moveToFirst();
            long id = cursor.getLong(0);
            String name = cursor.getString(1);
            String description = cursor.getString(2);
            byte sinTACC = (byte)cursor.getInt(3);
            byte foodType = (byte)cursor.getInt(4);
            food = new Food(
                    id, name, description, sinTACC, foodType
            );
        }
        return food;
    }

    public void updateFood(Food food) {
        ContentValues cv = new ContentValues();
        cv.put(KEY_NAME, food.getName());
        cv.put(KEY_DESCRIPTION, food.getDescription());
        cv.put(KEY_SINTACC, food.getSinTACC());
        cv.put(KEY_FOODTYPE, food.getFoodType());
        db.update(TABLE_FOODS, cv, KEY_ID + " = " + food.getId(), null);
    }

    public void deleteFood(long foodId) {
        db = this.getWritableDatabase();
        db.delete(TABLE_FOODS, KEY_ID + " = " + foodId, null);
    }

}
