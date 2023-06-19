package com.shreesha.meetingmanager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

public class DataBaseConn extends SQLiteOpenHelper {
    public DataBaseConn(Context context) {
        super(context,"MeetingDB.db",null,1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create Table meetingTbl(date TEXT,time TEXT, agenda TEXT)");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop Table if exists meetingTbl");

    }
    public boolean insertvalue(String d, String t, String agd){
        SQLiteDatabase DB=this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("date",d);
        cv.put("time",t);
        cv.put("agenda",agd);
        long res=DB.insert("meetingTbl",null,cv);  //query to insert
        if(res==-1){
            return  false;
        }
        else
            return true;
    }
    public Cursor fetch(String d){

        SQLiteDatabase DB=this.getReadableDatabase();
        // String sqlquery="select name from MDTbl where date='19/3/21' AND time='Afternoon'";
        // Cursor c = DB.rawQuery(sqlquery,null);
        Cursor c = DB.rawQuery("Select time,agenda from meetingTbl where date='"+d+"' ",null);
        return c;
    }
    public Cursor fetchAllDatesWithMeetings() {
        SQLiteDatabase DB = this.getReadableDatabase();
        Cursor cursor = DB.rawQuery("SELECT DISTINCT date FROM meetingTbl", null);
        return cursor;
    }
}
