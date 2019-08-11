package com.asu.cse535.group_number_420.Assignment2;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import static android.content.ContentValues.TAG;

public class DBHelper {

    private Context context;
    private PersonInfo person;
    private String tableName;
    public static final int db_version = 1;
    private static final String DB_NAME = "420";
    private static final String KEY_ID = "id";
    private static final String TIME_STAMP = "timestamp";
    private static final String XVALUES = "xvalues";
    private static final String YVALUES = "yvalues";
    private static final String ZVALUES = "zvalues";


    private SQLiteDatabase sqlDB;

    public DBHelper(Context context, PersonInfo person){
        this.context = context;
        this.person = person;
        setTableName();
        CreateTable();

    }

    public DBHelper(){

    }

    public SQLiteDatabase getDBInfo(String folder_path){
        SQLiteDatabase sqlDB = SQLiteDatabase.openOrCreateDatabase(Environment.getExternalStorageDirectory()+folder_path, null);

        return sqlDB;

    }

    private void setTableName(){
        tableName = person.getName() + "_" + person.getId() + "_" + person.getAge() + "_" + person.getSex();
    }

    public void CreateTable(){

        try{
            sqlDB = SQLiteDatabase.openOrCreateDatabase(Environment.getExternalStorageDirectory()+"/Android/Data/CSE535_ASSIGNMENT2", null);

            sqlDB.beginTransaction();
            try {
                String CREATE_TABLE = "CREATE TABLE " + tableName + "("
                        + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                        + TIME_STAMP + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP,"
                        + XVALUES + " TEXT,"
                        + YVALUES + " TEXT,"
                        + ZVALUES + " TEXT);";
                sqlDB.execSQL(CREATE_TABLE);



                sqlDB.setTransactionSuccessful(); //commit your changes
                Toast.makeText(context, "created db", Toast.LENGTH_LONG).show();

            }
            catch (SQLiteException e) {
                //report problem
            }
            finally {
                sqlDB.endTransaction();
            }
        }catch (SQLException e){

            Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
        }

    }



    public void insertNewData(String xvalue, String yvalue, String zvalue){

        sqlDB = SQLiteDatabase.openOrCreateDatabase(Environment.getExternalStorageDirectory()+"/Android/Data/CSE535_ASSIGNMENT2", null);
        File sd = Environment.getExternalStorageDirectory();
        String path = sd.getAbsolutePath();
        try {
            ContentValues contentValues = new ContentValues();

            //contentValues.put(TIME_STAMP, timestamp);
            contentValues.put(XVALUES, xvalue);
            contentValues.put(YVALUES, yvalue);
            contentValues.put(ZVALUES, zvalue);

            long result = sqlDB.insertOrThrow(tableName, null, contentValues);

            //Toast.makeText(context,"Data added", Toast.LENGTH_SHORT).show();

        }
        catch (SQLiteException e) {
            //report problem
        }
        finally {
            //db.endTransaction();
        }

        //db.close();
    }


    public ArrayList<HashMap<String, String>> GetDataFromCurrentPatient() {
        sqlDB = SQLiteDatabase.openOrCreateDatabase(Environment.getExternalStorageDirectory().getPath()+"/Android/Data/CSE535_ASSIGNMENT2", null);

        ArrayList<HashMap<String, String>> userList = new ArrayList<>();

        String query = "SELECT xvalues, yvalues, zvalues FROM " + tableName + " ORDER BY TIMESTAMP DESC";
        Log.d(TAG,"Query: " + query);
        try {
            Cursor cursor = sqlDB.rawQuery(query, null);
            //Log.i(TAG, "before cursor " + cursor.getCount());

            while (cursor.moveToNext()) {
                HashMap<String, String> user = new HashMap<>();
                user.put("xvalues", cursor.getString(cursor.getColumnIndexOrThrow(XVALUES)));
                //Log.i(TAG,"User 1 data ");
                user.put("yvalues", cursor.getString(cursor.getColumnIndexOrThrow(YVALUES)));
                //Log.i(TAG,"User 1 data ");
                user.put("zvalues", cursor.getString(cursor.getColumnIndexOrThrow(ZVALUES)));
                //Log.i(TAG,"User 1 data ");

                userList.add(user);
            }
            //Toast.makeText(context, "Data returned", Toast.LENGTH_SHORT).show();
            return userList;
        }catch (SQLiteException e) {
            //System.out.println("No such table: " + tableName);
            Toast.makeText(context,  "No such table: " + tableName, Toast.LENGTH_SHORT).show();
        }
        return null;
    }

    public ArrayList<HashMap<String, String>> GetDataFromPatientInfo(String table_name) {
        sqlDB = SQLiteDatabase.openOrCreateDatabase(Environment.getExternalStorageDirectory()+"/Android/Data/CSE535_ASSIGNMENT2", null);

        ArrayList<HashMap<String, String>> userList = new ArrayList<>();
        String query = "SELECT xvalues, yvalues, zvalues FROM " + table_name;
        Cursor cursor = sqlDB.rawQuery(query,null);
        Log.i(TAG,"before cursor ");

        if (cursor.moveToNext()) {
            HashMap<String, String> user = new HashMap<>();
            user.put("xvalues", cursor.getString(cursor.getColumnIndexOrThrow(XVALUES)));
            Log.i(TAG,"User 1 data ");
            user.put("yvalues", cursor.getString(cursor.getColumnIndexOrThrow(YVALUES)));
            Log.i(TAG,"User 1 data ");
            user.put("zvalues", cursor.getString(cursor.getColumnIndexOrThrow(ZVALUES)));
            Log.i(TAG,"User 1 data ");

            userList.add(user);
        }
        //Toast.makeText(context,"Data returned", Toast.LENGTH_SHORT).show();

        return userList;
    }
}