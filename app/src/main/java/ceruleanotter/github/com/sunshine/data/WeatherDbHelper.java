package ceruleanotter.github.com.sunshine.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


/**
 * Created by lyla on 7/23/14.
 */
public class WeatherDbHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "weather.db";
    private static final int VERSION = 1;
    public WeatherDbHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        //Create a table to hold locs.

        final String SQL_CREATE_WEATHER_TABLE = "CREATE TABLE " + WeatherContract.WeatherEntry.TABLE_NAME + " ("
                + WeatherContract.WeatherEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "

                //ID location associated with this weather data
                + WeatherContract.WeatherEntry.COLUMN_LOC_KEY + " INTEGER NOT NULL, "
                + WeatherContract.WeatherEntry.COLUMN_DATETEXT + " TEXT NOT NULL, "
                + WeatherContract.WeatherEntry.COLUMN_SHORT_DESC + " TEXT NOT NULL, "

                + WeatherContract.WeatherEntry.COLUMN_WEATHER_ID + " INTEGER NOT NULL, "

                + WeatherContract.WeatherEntry.COLUMN_MAX_TEMP + " REAL NOT NULL, "
                + WeatherContract.WeatherEntry.COLUMN_MIN_TEMP + " REAL NOT NULL, "
                + WeatherContract.WeatherEntry.COLUMN_HUMIDITY + " REAL NOT NULL, "
                + WeatherContract.WeatherEntry.COLUMN_PRESSURE + " REAL NOT NULL, "
                + WeatherContract.WeatherEntry.COLUMN_WIND_SPEED + " REAL NOT NULL, "
                + WeatherContract.WeatherEntry.COLUMN_DEGREES + " REAL NOT NULL, "

                + " FOREIGN KEY ( " + WeatherContract.WeatherEntry.COLUMN_LOC_KEY + " ) REFERENCES "
                + WeatherContract.LocationEntry.TABLE_NAME + " (" + WeatherContract.LocationEntry._ID + "), "

                + "UNIQUE (" + WeatherContract.WeatherEntry.COLUMN_DATETEXT + ", "
                + WeatherContract.WeatherEntry.COLUMN_LOC_KEY + ") ON CONFLICT REPLACE);";

        final String SQL_CREATE_LOCATION_TABLE = "CREATE TABLE " + WeatherContract.LocationEntry.TABLE_NAME + " ("
                + WeatherContract.LocationEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "

                + WeatherContract.LocationEntry.COLUMN_CITY_NAME + " TEXT NOT NULL, "
                + WeatherContract.LocationEntry.COLUMN_COORD_LAT + " REAL NOT NULL, "
                + WeatherContract.LocationEntry.COLUMN_COORD_LONG + " REAL NOT NULL, "
                + WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING + " TEXT UNIQUE NOT NULL,"
                + "UNIQUE (" + WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING+ ") ON CONFLICT IGNORE);";
        sqLiteDatabase.execSQL(SQL_CREATE_LOCATION_TABLE);
        sqLiteDatabase.execSQL(SQL_CREATE_WEATHER_TABLE);


    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i2) {
        //this is if the db version changes - this deletes the tables, which in this case is okay because they are just a
        //local cache - it would NOT be good for something that actually stores important data on the phone

        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + WeatherContract.LocationEntry.TABLE_NAME);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + WeatherContract.WeatherEntry.TABLE_NAME);

        this.onCreate(sqLiteDatabase);
    }
}
