package project.csci.geocaching;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

//This contains the functions for interacting with the users Database.
//passwords are encrypted using a free library found online with the BCrypt algorithm.


public class UserDBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "Users.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_NAME = "Users";

    public UserDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    //Create the table
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "(userId INTEGER PRIMARY KEY,name TEXT, password TEXT, caches INTEGER)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public void addEntry(String username, String password){
        String hashedPass = BCrypt.hashpw(password, BCrypt.gensalt());

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("name", username);
        values.put("password", hashedPass);
        values.put("caches", 0);
        db.insert("Users", null, values);
    }

    public boolean validatePass(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        boolean match = false;
        Cursor cursor = db.rawQuery("SELECT  * FROM " + TABLE_NAME + " WHERE name = ?",
                new String[]{username});

        if (cursor.moveToFirst()){
            if(BCrypt.checkpw(password,cursor.getString(2))){
                match = true;
            }
        }

        cursor.close();
        return match;
    }

    public boolean checkUserDupes(String username){
        SQLiteDatabase db = this.getReadableDatabase();
        boolean match = false;
        Cursor cursor = db.rawQuery("SELECT  * FROM " + TABLE_NAME + " WHERE name = ?",
                new String[]{username});

        if (cursor.getCount() > 0){
                match = true;
        }

        cursor.close();
        return match;
    }

    public int getUserCaches(String username){
        SQLiteDatabase db = this.getReadableDatabase();
        int caches = 0;
        Cursor cursor = db.rawQuery("SELECT  * FROM " + TABLE_NAME + " WHERE name = ?",
                new String[]{username});

        if (cursor.moveToFirst()){
            if (cursor.getCount() > 0){
                caches = cursor.getInt(3);
            }
        }

        cursor.close();
        return caches;
    }

    //Update the DB when a user claims a cache
    public void updateUserCache(String username, int caches){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues newValues = new ContentValues();
        newValues.put("caches", caches);

        String[] args = new String[]{username};

        db.update(TABLE_NAME, newValues,"name=?" , args);
    }
}