package edu.cudenver.salimlakhani.phonebook;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DataManager {
    private SQLiteDatabase db;

    /**
     * DataManager constructor which initializes the database.
     * @param context Reference to the calling activity.
     */
    public DataManager (Context context) {
        MySQLiteOpenHelper helper = new MySQLiteOpenHelper (context);
        db = helper.getWritableDatabase();

    }

    /**
     * Executes select query sorted by name.
     * @return cursor to the result table.
     */
    public Cursor selectAll () {

        Cursor cursor = null;

        try {
            String query = "select * from contact order by name";
            cursor = db.rawQuery(query, null);
        }
        catch (Exception e) {
            Log.i ("info", "In DataManager selectAll method");
            Log.i ("info", e.getMessage());
        }

        //Log.i ("info", "Loaded data " + cursor.getCount());
        return cursor;
    }



    public Cursor selectAllByState (String state) {

        Cursor cursor = null;

        try {
            String query = "select * from contact where state = '" + state + "' order by name";
            cursor = db.rawQuery(query, null);
        }
        catch (Exception e) {
            Log.i ("info", "In DataManager selectAll method");
            Log.i ("info", e.getMessage());
        }

        //Log.i ("info", "Loaded data " + cursor.getCount());
        return cursor;
    }

    /**
     * Insert a new row into the database
     * @param name Name of the contact
     * @param phone Phone number of the contact
     * @param email Email address of the contact
     * @param street Street address of the contact
     * @param city City of the contact
     * @param state State of the contact
     * @param zip Zip code of the contact
     * @param type Contact type of the contact. Possible values are Business, Family, and Friend.
     */

    public void insert (String name, String phone, String email, String street,
                        String city, String state, String zip, String type) {

        try {
            String query = "insert into contact" +
                    "(name, phone, email, street, city, state, zip, contact_type) values " +
                    "( '" + name + "', '" + phone + "', '" + email + "', '" + street + "', '" + city +
                    "', '" + state + "', '" + zip + "', '" + type + "' )";
            db.execSQL(query);
        }
        catch (SQLException e) {
            Log.i ("info", "In DataManager insert method");
            Log.i ("info", e.getMessage());
        }
        Log.i ("info", "Added new contact " + name);
    }

    public void delete (int id) {
        try {
            String query = "delete from contact where _id =" + id;
            db.execSQL(query);
        }
        catch (SQLException e) {
            Log.i ("info", "In DataManager delete method");
            Log.i ("info", e.getMessage());
        }
    }

    public int search (Contact contact)
    {
        Cursor cursor = null;
        try{
            String query = "select * from contact where name='"+contact.getName()
                    +"' AND phone='"+contact.getPhone() +"' AND zip ='"+contact.getZip()
                    +"' AND email='" +contact.getEmail() + "' AND city= '"+ contact.getCity()+"'";
            cursor = db.rawQuery(query,null);
        }catch (SQLException e) {
            Log.i ("info", "In DataManager delete method");
            Log.i ("info", e.getMessage());
        }
//         if its not null then we do return the id
        if(cursor != null && cursor.moveToNext()){
            return cursor.getInt(0);
        }
        // if it is null then we
        else
        {
            return cursor.getColumnIndex("_id");
        }
    }

    public int getID(String name, String number) {
        /**
         * Get the id with the matching parameter.
         * @param name Name of the contact
         * @param phone Phone number of the contact
         * https://stackoverflow.com/questions/62169849/how-to-get-id-value-from-sqlite-database-android-studio-java
         */
        Cursor cursor = null;
        try {
            String query = "select * from contact where name ='" + name + "'and phone = '" + number +"'";
            cursor = db.rawQuery(query, null);
        } catch (SQLException e) {
            Log.i("info", e.getMessage());
        }
        if(cursor != null && cursor.moveToNext()){
            return cursor.getInt(0);
        }
        return cursor.getColumnIndex("_id");
    }

    /**
     * MySQLiteOpenHelper class extends the SQLiteOpenHelper class. It is used for
     * initializing and updating the database as needed.
     */
    private class MySQLiteOpenHelper extends SQLiteOpenHelper {

        public MySQLiteOpenHelper(Context context) {
            super(context, "address_book", null, 1);

        }

        /**
         * Called when database is accessed for the first time. It creates the structure of the database
         *
         * @param db Reference to the SQLiteDatabase object.
         */
        public void onCreate(SQLiteDatabase db) {

            try {
                String newTable = "create table contact  ("
                        + "_id integer primary key autoincrement not null, "
                        + "name text not null, "
                        + "phone text, "
                        + "email text, "
                        + "street text, "
                        + "city text, "
                        + "state text, "
                        + "zip text, "
                        + "contact_type)";

                db.execSQL(newTable);
            } catch (SQLException e) {
                Log.i("info", "In DataManager onCreate method");
                Log.i("info", e.getMessage());
            }

        }


        public void onUpgrade(SQLiteDatabase db, int oldVErsion, int newVersion) {

        }

    }
}
