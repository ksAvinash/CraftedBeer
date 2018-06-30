package it.justdevelop.craftedbeer.helpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class SQLiteDatabaseHelper  extends SQLiteOpenHelper {
    private static final String LOG = "SQLiteHelper :";
    private static final String APP_LOG_TAG = "CrafterBeer: ";


    private static final String DATABASE_NAME = "cb_v1";
    private static final String TABLE_BEERS = "beers";

    private static final String BEER_ID = "beer_id";
    private static final String BEER_NAME = "beer_name";
    private static final String BEER_ABV = "beer_abv";
    private static final String BEER_IBU = "beer_ibu";
    private static final String BEER_STYLE = "beer_style";
    private static final String BEER_OUNCES = "beer_ounces";
    private static final String BEER_CART_STATUS = "beer_cart_status";







    public SQLiteDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.i(APP_LOG_TAG, LOG+"Creating tables!");

        String create_table_beers = "create table "+TABLE_BEERS+" ("+BEER_ID+" integer primary key, "+BEER_NAME+" text, "+BEER_STYLE
                +" text, "+BEER_ABV+" number, "+BEER_IBU+" integer, "+BEER_OUNCES+" number);";

        db.execSQL(create_table_beers);

    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists "+TABLE_BEERS);
        onCreate(db);
    }

    public void deleteTables(){
        SQLiteDatabase db = this.getWritableDatabase();
        onUpgrade(db,0,1);
    }







    //inserts the new beer into the table or updates the contents if beer_id exists
    public void insertIntoBeers(int id, String name, String style, double abv, int ibu, double ounces){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("select count(*) from "+TABLE_BEERS+" where "+BEER_ID+" = "+id+";",null);
        cursor.moveToNext();
        int count = cursor.getInt(0);
        cursor.close();
        if(count > 0){
           // Log.i(APP_LOG_TAG, LOG+"updating beer_table : "+id);

            ContentValues contentValues = new ContentValues();
            contentValues.put(BEER_NAME, name);
            contentValues.put(BEER_STYLE, style);
            contentValues.put(BEER_ABV, abv);
            contentValues.put(BEER_IBU, ibu);
            contentValues.put(BEER_OUNCES, ounces);

            db.update(TABLE_BEERS, contentValues, BEER_ID+" = "+id, null);
        }else{
            //Log.i(APP_LOG_TAG, LOG+"inserting into beer_table : "+id);
            ContentValues contentValues = new ContentValues();
            contentValues.put(BEER_ID, id);
            contentValues.put(BEER_NAME, name);
            contentValues.put(BEER_STYLE, style);
            contentValues.put(BEER_ABV, abv);
            contentValues.put(BEER_IBU, ibu);
            contentValues.put(BEER_OUNCES, ounces);

            db.insert(TABLE_BEERS, null, contentValues);
        }
        db.close();
    }



    //fetches all beers by beer name in asc order
    public Cursor fetchAllBeers(){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery("select * from "+TABLE_BEERS+" order by "+BEER_NAME+" asc;",null);
    }

    public Cursor fetchBeersByName(String search_name){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery("select * from "+TABLE_BEERS+" where "+BEER_NAME+" like '%"+search_name+"%' order by "+BEER_NAME+" ;",null);
    }



    public Cursor fetchBeersByAlcoholAscending(){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery("select * from "+TABLE_BEERS+" order by "+BEER_ABV+" desc;",null);
    }

    public Cursor fetchBeersByAlcoholDescending(){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery("select * from "+TABLE_BEERS+" order by "+BEER_ABV+" asc;",null);
    }

    public Cursor fetchBeerById(int beer_id){
        SQLiteDatabase db = this.getWritableDatabase();
        return db.rawQuery("select * from "+TABLE_BEERS+" where "+BEER_ID+" = "+beer_id+" ;",null);
    }



}
