package ceruleanotter.github.com.sunshine;

import android.app.AlarmManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.android.debug.hv.ViewServer;




public class MainActivity extends ActionBarActivity implements Callback {
    private final String LOG_TAG = MainActivity.class.getSimpleName();
    private boolean mTwoPane;

    @Override
    public void onItemSelected(String date) {
        if (mTwoPane) {
            //tablet
            DetailFragment dt = new DetailFragment();
            Bundle dateargs = new Bundle();
            dateargs.putString(ForecastFragment.WEATHER_DATE_ARG, date);
            dt.setArguments(dateargs);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.weather_detail_container, dt)
                    .commit();
        } else {
            //phone
            Intent startDetailView = new Intent(this, DetailActivity.class);
            startDetailView.putExtra(ForecastFragment.WEATHER_DATE_ARG, date);
            startActivity(startDetailView);
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v(LOG_TAG, "on Create Called");
        setContentView(R.layout.activity_main);
        if(this.findViewById(R.id.weather_detail_container) != null) {
            mTwoPane = true;
            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.weather_detail_container, new DetailFragment())
                        .commit();
            }
        } else {
            mTwoPane = false;
        }
        ForecastFragment f = ((ForecastFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_forecast));
        f.mForecastAdapter.setmUseTodayLayout(!mTwoPane);

        ViewServer.get(this).addWindow(this);

        




    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.v(LOG_TAG, "on Start Called");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.v(LOG_TAG, "on Stop Called");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.v(LOG_TAG, "on Pause Called");
    }

    @Override
    protected void onResume() {
        super.onResume();
        ViewServer.get(this).setFocusedWindow(this);
        Log.v(LOG_TAG, "on Resume Called");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ViewServer.get(this).removeWindow(this);
        Log.v(LOG_TAG, "on Destory Called");
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            //DO SOMETHING
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
