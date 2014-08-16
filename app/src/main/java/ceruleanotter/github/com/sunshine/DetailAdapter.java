package ceruleanotter.github.com.sunshine;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ListView;

/**
 * {@link ForecastAdapter} exposes a list of weather forecasts
 * from a {@link Cursor} to a {@link ListView}.
 */

public class DetailAdapter extends CursorAdapter {




    /**
     * Cache of the children views for a forecast list item.
     */
    public static class DetailViewHolder {
        public final ImageView iconView;
        public final TextView dateView;
        public final TextView descriptionView;
        public final TextView highTempView;
        public final TextView lowTempView;
        public final TextView humidityView;
        public final TextView pressureView;
        public final TextView windView;
        public final ViewtasticView faceView;
        public DetailViewHolder(View view) {
            iconView = (ImageView) view.findViewById(R.id.detail_icon);
            dateView = (TextView) view.findViewById(R.id.detail_date_textview);
            descriptionView = (TextView) view.findViewById(R.id.detail_forecast_textview);
            highTempView = (TextView) view.findViewById(R.id.detail_high_textview);
            lowTempView = (TextView) view.findViewById(R.id.detail_low_textview);
            humidityView = (TextView) view.findViewById(R.id.detail_humidity_textview);
            pressureView = (TextView) view.findViewById(R.id.detail_pressure_textview);
            windView = (TextView) view.findViewById(R.id.detail_wind_textview);
            faceView = (ViewtasticView) view.findViewById(R.id.faceView);
        }
    }

    public DetailAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        int viewType = getItemViewType(cursor.getPosition());
        int layoutId = R.layout.fragment_detail;

        View root = LayoutInflater.from(context).inflate(layoutId, parent, false);
        DetailViewHolder v = new DetailViewHolder(root);
        root.setTag(v); //a little tag that can be used to store any data

        return root;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        DetailViewHolder v = (DetailViewHolder)view.getTag();

        // Read weather icon ID from cursor
        int weatherId = cursor.getInt(DetailFragment.COL_WEATHER_ID);


        // set the read image here
        v.iconView.setImageResource(Utility.getIconResourceForWeatherCondition(weatherId));

        // Read date from cursor
        String dateString = cursor.getString(DetailFragment.COL_WEATHER_DATE);
        // Find TextView and set formatted date on it

        v.dateView.setText(Utility.getFriendlyDayString(context, dateString));

        // Read weather forecast from cursor
        String description = cursor.getString(DetailFragment.COL_WEATHER_DESC);
        // Find TextView and set weather forecast on it

        v.descriptionView.setText(description);

        // Read user preference for metric or imperial temperature units
        boolean isMetric = Utility.isMetric(context);

        // Read high temperature from cursor
        float high = cursor.getFloat(DetailFragment.COL_WEATHER_MAX_TEMP);

        v.highTempView.setText(Utility.formatTemperature(context,high, isMetric));

        // Read low temperature from cursor
        float low = cursor.getFloat(DetailFragment.COL_WEATHER_MIN_TEMP);
        v.lowTempView.setText(Utility.formatTemperature(context, low, isMetric));


        float pressure = cursor.getFloat(DetailFragment.COL_WEATHER_PRESSURE);
        v.pressureView.setText(context.getString(R.string.format_pressure, pressure));

        float humidity = cursor.getFloat(DetailFragment.COL_WEATHER_HUMIDITY);
        v.pressureView.setText(context.getString(R.string.format_humidity, humidity));

        float wind = cursor.getFloat(DetailFragment.COL_WEATHER_WINDSPEED);
        float degrees = cursor.getFloat(DetailFragment.COL_WEATHER_DEGREES);
        v.pressureView.setText(Utility.getFormattedWind(context, wind, degrees));


        v.faceView.setEmotion( (Math.random() > 0.5) ? true : false);
    }
}