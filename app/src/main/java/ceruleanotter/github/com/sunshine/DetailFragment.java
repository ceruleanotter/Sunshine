package ceruleanotter.github.com.sunshine;

/**
 * Created by lyla on 8/4/14.
 */

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import ceruleanotter.github.com.sunshine.data.WeatherContract;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>  {
    private static final int DETAIL_LOADER = 0;
    private String mLocation;
    private String mForecastShareString;
    public static final String LOCATION_KEY = "locationkey";
    private DetailAdapter.DetailViewHolder _viewHolder;
    //TextView _weatherDataView;
    // For the forecast view we're showing only a small subset of the stored data.
    // Specify the columns we need.
    private static final String[] FORECAST_COLUMNS = {
            // In this case the id needs to be fully qualified with a table name, since
            // the content provider joins the location & weather tables in the background
            // (both have an _id column)
            // On the one hand, that's annoying.  On the other, you can search the weather table
            // using the location set by the user, which is only in the Location table.
            // So the convenience is worth it.
            WeatherContract.WeatherEntry.TABLE_NAME + "." + WeatherContract.WeatherEntry._ID,
            WeatherContract.WeatherEntry.COLUMN_DATETEXT,
            WeatherContract.WeatherEntry.COLUMN_SHORT_DESC,
            WeatherContract.WeatherEntry.COLUMN_MAX_TEMP,
            WeatherContract.WeatherEntry.COLUMN_MIN_TEMP,
            WeatherContract.LocationEntry.COLUMN_LOCATION_SETTING,
            WeatherContract.WeatherEntry.COLUMN_HUMIDITY,
            WeatherContract.WeatherEntry.COLUMN_PRESSURE,
            WeatherContract.WeatherEntry.COLUMN_WIND_SPEED,
            WeatherContract.WeatherEntry.COLUMN_DEGREES,

    };

    // These indices are tied to FORECAST_COLUMNS.  If FORECAST_COLUMNS changes, these
    // must change.
    public static final int COL_WEATHER_ID = 0;
    public static final int COL_WEATHER_DATE = 1;
    public static final int COL_WEATHER_DESC = 2;
    public static final int COL_WEATHER_MAX_TEMP = 3;
    public static final int COL_WEATHER_MIN_TEMP = 4;
    public static final int COL_LOCATION_SETTING = 5;
    public static final int COL_WEATHER_HUMIDITY = 6;
    public static final int COL_WEATHER_PRESSURE = 7;
    public static final int COL_WEATHER_WINDSPEED = 8;
    public static final int COL_WEATHER_DEGREES = 9;




    private String _date;
    private final String LOG_TAG = DetailActivity.class.getSimpleName();
    public DetailFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.detail_fragment_menu, menu);


        MenuItem shareItem = menu.findItem(R.id.action_share);
        ShareActionProvider shareProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(shareItem);

        if(shareProvider != null) shareProvider.setShareIntent(createShareIntent());
        else Log.e(LOG_TAG, "Share action provider is null");

    }

    private Intent createShareIntent() {
        Intent share = new Intent(Intent.ACTION_SEND);
        share.putExtra(Intent.EXTRA_TEXT, mForecastShareString);
        share.setFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        share.setType("text/plain");
        return share;
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        // _weatherDataView = (TextView)rootView.findViewById(R.id.weatherData);

        _viewHolder = new DetailAdapter.DetailViewHolder(rootView);


        return rootView;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Intent intent = getActivity().getIntent();
        boolean l = intent.hasExtra(ForecastFragment.WEATHER_DATA_INTENT_EXTRA);

        if(intent != null && intent.hasExtra(ForecastFragment.WEATHER_DATA_INTENT_EXTRA)) {
            _date = intent.getStringExtra(ForecastFragment.WEATHER_DATA_INTENT_EXTRA);
        }
        //_date = args.getString(ForecastFragment.WEATHER_DATA_INTENT_EXTRA);

        if(_date != null) {

            mLocation = Utility.getPreferredLocation(getActivity());
            Uri uri = WeatherContract.WeatherEntry.buildWeatherLocationWithDate(mLocation,_date);

            // Now create and return a CursorLoader that will take care of
            // creating a Cursor for the data being displayed.
            return new CursorLoader(
                    getActivity(),
                    uri,
                    FORECAST_COLUMNS,
                    null,
                    null,
                    null
            );

        } else {
            Log.e(LOG_TAG, "date was null, life sucks");

        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if(data.moveToFirst()) {
            //String desc = data.getString(data.getColumnIndex(WeatherContract.WeatherEntry.COLUMN_SHORT_DESC));

            View rootView = getView();
            //hope this has the right tag...
            //DetailAdapter.DetailViewHolder v = (DetailAdapter.DetailViewHolder) rootView.getTag();

            boolean isMetric = Utility.isMetric(getActivity());
            /*
            TextView dateText = (TextView)(getView().findViewById(R.id.detail_date_textview));
            TextView forecastText = (TextView)(getView().findViewById(R.id.detail_forecast_textview));
            TextView highText = (TextView)(getView().findViewById(R.id.detail_high_textview));
            TextView lowText = (TextView)(getView().findViewById(R.id.detail_low_textview));*/
            Context context = getActivity();

            _viewHolder.dateView.setText(Utility.formatDate(data.getString(COL_WEATHER_DATE)));
            _viewHolder.descriptionView.setText(data.getString(COL_WEATHER_DESC));
            _viewHolder.highTempView.setText(Utility.formatTemperature(context, data.getDouble(COL_WEATHER_MAX_TEMP), isMetric));
            _viewHolder.lowTempView.setText(Utility.formatTemperature(context, data.getDouble(COL_WEATHER_MIN_TEMP), isMetric));
            _viewHolder.pressureView.setText(context.getString(R.string.format_pressure, data.getFloat(COL_WEATHER_PRESSURE)));
            _viewHolder.humidityView.setText(context.getString(R.string.format_humidity, data.getFloat(COL_WEATHER_HUMIDITY)));
            _viewHolder.windView.setText(Utility.getFormattedWind(context,data.getFloat(COL_WEATHER_WINDSPEED),data.getFloat(COL_WEATHER_DEGREES)));
            mForecastShareString =  String.format("%s - %s - %s/%s", _viewHolder.dateView.getText(),
                    _viewHolder.descriptionView.getText(), _viewHolder.highTempView.getText(), _viewHolder.lowTempView.getText()) + " #SunshineApp";

                /*String informationFromData = String.format("%s - %s - %s/%s", ,
                        data.getString(COL_WEATHER_DESC), data.getString(COL_WEATHER_MAX_TEMP),
                        data.getString(COL_WEATHER_MIN_TEMP));*/





            //if (_weatherDataView != null) _weatherDataView.setText(informationFromData);
            //else Log.e(LOG_TAG, "weatherdataview was null, life also sucks");
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        //not sure what to do here...



    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if(savedInstanceState != null && savedInstanceState.containsKey(LOCATION_KEY)) {
            getLoaderManager().initLoader(DETAIL_LOADER, savedInstanceState, this);

        } else {
            getLoaderManager().initLoader(DETAIL_LOADER, null, this);
        }



    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(LOCATION_KEY, mLocation);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mLocation != null && mLocation != Utility.getPreferredLocation(getActivity())) {
            getLoaderManager().restartLoader(DETAIL_LOADER,null,this);

        }
    }
}