package com.example.android.sunshine.app;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.sunshine.app.data.WeatherContract;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

    private static final String LOG_TAG = DetailFragment.class.getSimpleName();

    private static final String FORECAST_SHARE_HASHTAG = " #SunshineApp";
    private String mForecastUri;
    private String mForecastString;
    private Uri mUri;

    static final String DETAIL_URI = "Uri";


    private ShareActionProvider mShareActionProvider;
    private String mForecast;

    private static final int DETAIL_LOADER = 0;

    private static final String[] FORECAST_COLUMNS = {
            WeatherContract.WeatherEntry.TABLE_NAME + "." + WeatherContract.WeatherEntry._ID,
            WeatherContract.WeatherEntry.COLUMN_DATE,
            WeatherContract.WeatherEntry.COLUMN_SHORT_DESC,
            WeatherContract.WeatherEntry.COLUMN_MAX_TEMP,
            WeatherContract.WeatherEntry.COLUMN_MIN_TEMP,
            WeatherContract.WeatherEntry.COLUMN_HUMIDITY,
            WeatherContract.WeatherEntry.COLUMN_WIND_SPEED,
            WeatherContract.WeatherEntry.COLUMN_DEGREES,
            WeatherContract.WeatherEntry.COLUMN_PRESSURE,
            WeatherContract.WeatherEntry.COLUMN_WEATHER_ID
    };

    // these constants correspond to the projection defined above, and must change if the
    // projection changes
    private static final int COL_WEATHER_ID = 0;
    private static final int COL_WEATHER_DATE = 1;
    private static final int COL_WEATHER_DESC = 2;
    private static final int COL_WEATHER_MAX_TEMP = 3;
    private static final int COL_WEATHER_MIN_TEMP = 4;
    private static final int COL_WEATHER_HUMIDITY = 5;
    private static final int COL_WEATHER_WIND_SPEED = 6;
    private static final int COL_WEATHER_DEGREES = 7;
    private static final int COL_WEATHER_PRESSURE = 8;
    private static final int COL_WEATHER_CONDITION_ID = 9;
    
    public ImageView iconView;
    public TextView dayView;
    public TextView dateView;
    public TextView highView;
    public TextView lowView;
    public TextView humidityView;
    public TextView windView;
    public TextView pressureView;
    public TextView forecastView;
    
    public DetailFragment() {
        setHasOptionsMenu(true);
    }

    void onLocationChanged( String newLocation ) {
        // replace the uri, since the location has changed
        Uri uri = mUri;
        if (null != uri) {
            long date = WeatherContract.WeatherEntry.getDateFromUri(uri);
            Uri updatedUri = WeatherContract.WeatherEntry.buildWeatherLocationWithDate(newLocation, date);
            mUri = updatedUri;
            getLoaderManager().restartLoader(DETAIL_LOADER, null, this);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Bundle arguments = getArguments();
        if (arguments != null) {
            mUri = arguments.getParcelable(DetailFragment.DETAIL_URI);
        }
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        iconView = (ImageView)rootView.findViewById(R.id.detail_icon);
        dayView = (TextView)rootView.findViewById(R.id.detail_day_textview);
        dateView = (TextView)rootView.findViewById(R.id.detail_date_textview);
        highView = (TextView)rootView.findViewById(R.id.detail_high_textview);
        lowView = (TextView)rootView.findViewById(R.id.detail_low_textview);
        humidityView = (TextView)rootView.findViewById(R.id.detail_humidity_textview);
        windView = (TextView)rootView.findViewById(R.id.detail_wind_textview);
        pressureView = (TextView)rootView.findViewById(R.id.detail_pressure_textview);
        forecastView = (TextView)rootView.findViewById(R.id.detail_forecast_textview);
        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.detailfragment, menu);

        // Retrieve the share menu item
        MenuItem menuItem = menu.findItem(R.id.action_share);

        // Get the provider and hold onto it to set/change the share intent.
        ShareActionProvider mShareActionProvider =
                (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);

        // Attach an intent to this ShareActionProvider.  You can update this at any time,
        // like when the user selects a new piece of data they might like to share.
        if (mShareActionProvider != null ) {
            mShareActionProvider.setShareIntent(createShareForecastIntent());
        } else {
            Log.d(LOG_TAG, "Share Action Provider is null?");
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(0, null, this);
    }


    private Intent createShareForecastIntent() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT,
                mForecast + FORECAST_SHARE_HASHTAG);
        return shareIntent;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

//        // The detail Activity called via intent.  Inspect the intent for forecast data.
//        Intent intent = getActivity().getIntent();
//        if (intent == null || intent.getData() == null){
//            return null;
//        }

        if (null != mUri) {

            return new CursorLoader(
                      getActivity()
                    , mUri
                    , FORECAST_COLUMNS
                    , null
                    , null
                    , null
            );
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.v(LOG_TAG, "In onLoadFinished");
        if (!data.moveToFirst()) {
            return;
        }

        String dayName = Utility.getDayName(
                getActivity(),
                data.getLong(COL_WEATHER_DATE));

        String monthDayName = Utility.getFormattedMonthDay(
                getActivity(),
                data.getLong(COL_WEATHER_DATE)
        );

        String humidity = getString(R.string.format_humidity, data.getFloat(COL_WEATHER_HUMIDITY));

        String wind = Utility.getFormattedWind(
                  getActivity()
                , data.getFloat(COL_WEATHER_WIND_SPEED)
                , data.getFloat(COL_WEATHER_DEGREES)
        );

        String pressure = getString(R.string.format_pressure, data.getFloat(COL_WEATHER_PRESSURE));

        String weatherDescription =
                data.getString(COL_WEATHER_DESC);

        boolean isMetric = Utility.isMetric(getActivity());

        String high = Utility.formatTemperature(
                getActivity(), data.getDouble(COL_WEATHER_MAX_TEMP), isMetric);

        String low = Utility.formatTemperature(
                getActivity(), data.getDouble(COL_WEATHER_MIN_TEMP), isMetric);

        int weatherCodeId = data.getInt(COL_WEATHER_CONDITION_ID);
        int imageId = Utility.getArtResourceForWeatherCondition(weatherCodeId);
        iconView.setImageResource(imageId);
        dayView.setText(dayName);
        dateView.setText(monthDayName);
        humidityView.setText(humidity);
        windView.setText(wind);
        pressureView.setText(pressure);
        forecastView.setText(weatherDescription);
        highView.setText(high);
        lowView.setText(low);

        // If onCreateOptionsMenu has already happened, we need to update the share intent now.
        if (mShareActionProvider != null) {
            mShareActionProvider.setShareIntent(createShareForecastIntent());
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

//    public static class ViewHolder{
//        public final ImageView iconView;
//        public final TextView dayView;
//        public final TextView dateView;
//        public final TextView highView;
//        public final TextView lowView;
//        public final TextView humidityView;
//        public final TextView windView;
//        public final TextView pressureView;
//        public final TextView forecastView;
//
//        public ViewHolder(View view){
//            iconView = (ImageView)view.findViewById(R.id.detail_icon);
//            dayView = (TextView)view.findViewById(R.id.detail_day_textview);
//            dateView = (TextView)view.findViewById(R.id.detail_date_textview);
//            highView = (TextView)view.findViewById(R.id.detail_high_textview);
//            lowView = (TextView)view.findViewById(R.id.detail_low_textview);
//            humidityView = (TextView)view.findViewById(R.id.detail_humidity_textview);
//            windView = (TextView)view.findViewById(R.id.detail_wind_textview);
//            pressureView = (TextView)view.findViewById(R.id.detail_pressure_textview);
//            forecastView = (TextView)view.findViewById(R.id.detail_forecast_textview);
//        }
//    }
}