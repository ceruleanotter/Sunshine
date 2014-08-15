package ceruleanotter.github.com.sunshine;

/**
 * Created by lyla on 7/13/14.
 */

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Date;

import ceruleanotter.github.com.sunshine.data.WeatherContract;
import ceruleanotter.github.com.sunshine.data.WeatherContract.LocationEntry;
import ceruleanotter.github.com.sunshine.data.WeatherContract.WeatherEntry;

/**
 * A placeholder fragment containing a simple view.
 */
public class ForecastFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private final String LOG_TAG = ForecastFragment.class.getSimpleName();
    //private final String ZIPCODE_INTENT_EXTRA = "77096";
    public static final String WEATHER_DATE_ARG = "weatherData";
    public static final String SAVED_POSITION_ARG = "savedPosition";
    public static final String ZIPCODE_INTENT_EXTRA = "zipcode";
    public ArrayList<String> _forecastEntries;
    ListView _ls;
    ForecastAdapter mForecastAdapter;



    private static final int FORECAST_LOADER = 0;
    private String mLocation;
    private int mPosition;




    // For the forecast view we're showing only a small subset of the stored data.
    // Specify the columns we need.
    private static final String[] FORECAST_COLUMNS = {
            // In this case the id needs to be fully qualified with a table name, since
            // the content provider joins the location & weather tables in the background
            // (both have an _id column)
            // On the one hand, that's annoying.  On the other, you can search the weather table
            // using the location set by the user, which is only in the Location table.
            // So the convenience is worth it.
            WeatherEntry.TABLE_NAME + "." + WeatherEntry._ID,
            WeatherEntry.COLUMN_DATETEXT,
            WeatherEntry.COLUMN_SHORT_DESC,
            WeatherEntry.COLUMN_MAX_TEMP,
            WeatherEntry.COLUMN_MIN_TEMP,
            LocationEntry.COLUMN_LOCATION_SETTING,
            WeatherEntry.COLUMN_WEATHER_ID
    };

    // These indices are tied to FORECAST_COLUMNS.  If FORECAST_COLUMNS changes, these
    // must change.
    public static final int COL_WEATHER_ID = 0;
    public static final int COL_WEATHER_DATE = 1;
    public static final int COL_WEATHER_DESC = 2;
    public static final int COL_WEATHER_MAX_TEMP = 3;
    public static final int COL_WEATHER_MIN_TEMP = 4;
    public static final int COL_LOCATION_SETTING = 5;
    public static final int COL_WEATHER_ICON_ID = 6;


    public ForecastFragment() {
    }

    @Override
    public void onCreate(Bundle savedState){
        super.onCreate(savedState);
        setHasOptionsMenu(true);


    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.forecast_fragment_menu, menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        if (item.getItemId() == R.id.action_refresh){
            this.updateWeather();
        }

        if (item.getItemId() == R.id.action_map){
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this.getActivity().getApplicationContext());
            String zipcodeLocation = prefs.getString(getString(R.string.pref_key_location), getString(R.string.pref_default_location));


            Intent mapIntent = new Intent(Intent.ACTION_VIEW);
            mapIntent.setData(Uri.parse("geo:0,0?q=" + zipcodeLocation));
            if (mapIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                startActivity(mapIntent);
            } else {
                Log.e(LOG_TAG, "The intent has no activity to start!");
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {



        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        _ls = (ListView) (rootView.findViewById(R.id.listview_forecast));

        _ls.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Callback parentactivity = (Callback)getActivity();

                ForecastAdapter adap = (ForecastAdapter)adapterView.getAdapter();
                Cursor cursor = adap.getCursor();
                mPosition = position;



                if (cursor != null && cursor.moveToPosition(position)) {

                    String date = cursor.getString(COL_WEATHER_DATE);
                    parentactivity.onItemSelected(cursor.getString(COL_WEATHER_DATE));


                }

            }


        });



        // The SimpleCursorAdapter will take data from the database through the
        // Loader and use it to populate the ListView it's attached to.

        mForecastAdapter = new ForecastAdapter(
                getActivity(),
                null,
                0

        );




        _ls.setAdapter(mForecastAdapter);


        if (savedInstanceState != null && savedInstanceState.containsKey(ForecastFragment.SAVED_POSITION_ARG)) {
            mPosition = savedInstanceState.getInt(ForecastFragment.SAVED_POSITION_ARG);
        }

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onResume() {
        super.onResume();
        if (mLocation != null && !mLocation.equals(Utility.getPreferredLocation(getActivity()))) {
            getLoaderManager().restartLoader(FORECAST_LOADER, null, this);
        }
    }


    public void updateWeather() {

        FetchWeatherTask ft = new FetchWeatherTask(getActivity());
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this.getActivity().getApplicationContext());
        //String zip = prefs.getString(getString(R.string.pref_key_location), getString(R.string.pref_default_location));

        ft.execute(Utility.getPreferredLocation(getActivity()));
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // This is called when a new Loader needs to be created.  This
        // fragment only uses one loader, so we don't care about checking the id.

        // To only show current and future dates, get the String representation for today,
        // and filter the query to return weather only for dates after or including today.
        // Only return data after today.
        String startDate = WeatherContract.getDbDateString(new Date());

        // Sort order:  Ascending, by date.
        String sortOrder = WeatherEntry.COLUMN_DATETEXT + " ASC";

        mLocation = Utility.getPreferredLocation(getActivity());
        Uri weatherForLocationUri = WeatherEntry.buildWeatherLocationWithStartDate(
                mLocation, startDate);

        // Now create and return a CursorLoader that will take care of
        // creating a Cursor for the data being displayed.
        return new CursorLoader(
                getActivity(),
                weatherForLocationUri,
                FORECAST_COLUMNS,
                null,
                null,
                sortOrder
        );
    }

    @Override
    public void onLoadFinished(Loader loader, Cursor data) {



        if (null != mForecastAdapter) mForecastAdapter.swapCursor(data);
        if ( mLocation != null && !mLocation.equals(Utility.getPreferredLocation(getActivity())) ) {
            getLoaderManager().restartLoader(FORECAST_LOADER, null, this);
        }



        if (mPosition != ListView.INVALID_POSITION ) {
            //_ls.smoothScrollToPosition(mPosition);
            _ls.setSelection(mPosition);
            _ls.setItemChecked(mPosition, true);

        }

    }

    @Override
    public void onLoaderReset(Loader loader) {
        if (null != mForecastAdapter) mForecastAdapter.swapCursor(null);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(FORECAST_LOADER, null, this);
        this.updateWeather();
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(ForecastFragment.SAVED_POSITION_ARG,mPosition);
        super.onSaveInstanceState(outState);
    }
}
