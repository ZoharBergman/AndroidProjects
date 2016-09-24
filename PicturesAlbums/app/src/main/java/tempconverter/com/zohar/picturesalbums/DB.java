package tempconverter.com.zohar.picturesalbums;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Zohar on 13/08/2016.
 */
public class DB extends SQLiteOpenHelper{
    final static String DB_NAME = "DB";
    final static String DB_TABLE_IMAGE_DATA = "image_data";

    public DB(Context c){
        super(c, DB_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            // Create a scores table in the Database.
            db.execSQL("CREATE TABLE IF NOT EXISTS "
                    + DB_TABLE_IMAGE_DATA
                    + " (image_name TEXT, location TEXT, comment TEXT);");
        }
        catch (SQLException ex){
            Log.e("DB", ex.getMessage());
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        try {
            // Drop older table if existed
            db.execSQL("DROP TABLE IF EXISTS " + DB_TABLE_IMAGE_DATA);

            // Create tables again
            onCreate(db);
        }
        catch (SQLException ex){
            Log.e("DB", ex.getMessage());
        }
    }

    public void insertOrUpdateRow(ImageData data){
        try{
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("image_name", data.getImage_name());
            values.put("location", data.getLocation());
            values.put("comment", data.getComment());
            if(select(data.getImage_name()) == null) {
                db.insert(DB_TABLE_IMAGE_DATA, null, values);
            }
            else {
                db.update(DB_TABLE_IMAGE_DATA, values, "image_name = \"" +data.getImage_name() + "\"", null);
            }
        }
        catch (SQLException ex){
            Log.e("DB", ex.getMessage());
        }
    }

    public ImageData select(String image_name){
        try {
            // Getting the DB
            SQLiteDatabase db = this.getReadableDatabase();
            // Creating the query and execute it
            String query = "SELECT location, comment FROM " + DB_TABLE_IMAGE_DATA + " WHERE image_name = \"" + image_name + "\"";
            Cursor cursor = db.rawQuery(query, null);
            // Going over the selected rows and adding to ArrayList
            if (cursor.moveToFirst())
                    return new ImageData(image_name, cursor.getString(0), cursor.getString(1));

        }
        catch (SQLException ex){
            Log.e("DB", ex.getMessage());
        }

        return null;
    }
}
