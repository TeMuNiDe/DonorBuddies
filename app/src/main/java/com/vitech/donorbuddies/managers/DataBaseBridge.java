package com.vitech.donorbuddies.managers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.vitech.donorbuddies.data.BloodRequest;

import java.util.ArrayList;
import java.util.List;

public class DataBaseBridge extends SQLiteOpenHelper {
    SQLiteDatabase database;
    public DataBaseBridge(Context context) {
        super(context,"BLOOD_REQUESTS",null,1);
        database = this.getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
db.execSQL("CREATE TABLE BLOOD_REQUESTS(SENDER TEXT,NAME TEXT,CONTACT TEXT,HOSPITAL TEXT,MESSAGE TEXT,PRIMARYBLOOD TEXT,SUBSTITUTE TEXT,TIMESTAMP TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
    void insertRequest(BloodRequest request){
        ContentValues values = new ContentValues();
        values.put("SENDER",request.sender);

        values.put("NAME",request.name);
        values.put("HOSPITAL",request.hospital);
        values.put("MESSAGE",request.message);
        values.put("CONTACT",request.contact);
        values.put("PRIMARYBLOOD",request.bloodGroupPrimary);
        values.put("SUBSTITUTE",request.bloodGroupSubstitute);
        values.put("TIMESTAMP",request.timeStamp);
        database.insert("BLOOD_REQUESTS",null,values);


    }
     public void deleteRequest(String timestamp){
         database.delete("BLOOD_REQUESTS"," TIMESTAMP LIKE ?",new String[]{timestamp});
     }
    public List<BloodRequest> getRequests(){
        List<BloodRequest> all = new ArrayList<>();
        Cursor cursor = database.rawQuery("SELECT * from BLOOD_REQUESTS WHERE 1 ORDER BY TIMESTAMP DESC",null);
        while (cursor.moveToNext()){

            all.add(new BloodRequest(cursor.getString(0),cursor.getString(1),cursor.getString(2),cursor.getString(3),cursor.getString(4),cursor.getString(5),cursor.getString(6),cursor.getString(7)));
        }
        cursor.close();
        return all;
    }
}
