package tud.kom.dss6.localsiri.localservice;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import tud.kom.dss6.localsiri.R;
import android.app.ListActivity;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.SimpleAdapter;
import android.widget.Toast;

public class MainLocation extends ListActivity {

    private static final long MINIMUM_DISTANCE_CHANGE_FOR_UPDATES = 1; // in Meters
    private static final long MINIMUM_TIME_BETWEEN_UPDATES = 5000; // in Milliseconds

    protected LocationManager locationManager;

    protected Button recordCurrentLocation;
    protected Button displaySavedLocations;

    public static final String KEY_DATE = "date";
    public static final String KEY_LATITUDE = "latitude";
    public static final String KEY_LONGITUDE = "longitude";

    DBAdapter DB;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_location);

        recordCurrentLocation = (Button) findViewById(R.id.add_geoPoints);
        displaySavedLocations = (Button) findViewById(R.id.display_geoPint);
        DB = new DBAdapter(this);

        /*locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                MINIMUM_TIME_BETWEEN_UPDATES,
                MINIMUM_DISTANCE_CHANGE_FOR_UPDATES,
                new MyLocationListener()
        );*/

        recordCurrentLocation.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                addGeoLocation();
            }
        });

        displaySavedLocations.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                displayGeoLocation();
            }
        });
    }
    
    
    protected void addGeoLocation() {

        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        if (location != null) {

            HashMap<String, String> geoPointsSet = new HashMap<String, String>();

            geoPointsSet.put(KEY_DATE,getDateTime());
            geoPointsSet.put(KEY_LATITUDE, String.valueOf(location.getLatitude()));
            geoPointsSet.put(KEY_LONGITUDE, String.valueOf(location.getLongitude()));

            String message = "adding to DB. Latitude: " + String.valueOf(location.getLatitude())+ "\n" +
                    "Longitude: " + String.valueOf(location.getLongitude());

            DB.insertGeoPoints(geoPointsSet);
            Toast.makeText(MainLocation.this, message,
                    Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(MainLocation.this, R.string.noInfo,
                    Toast.LENGTH_SHORT).show();
        }

    }

    protected void displayGeoLocation() {

        ArrayList<HashMap<String, String>> geoPointList = DB.getSavedGeoPoints();

        ListAdapter adapter = new SimpleAdapter(MainLocation.this,geoPointList,R.layout.geo_points_list,
                new String[] {KEY_DATE, KEY_LATITUDE, KEY_LONGITUDE},
                new int[]{R.id.date,R.id.latitude,R.id.longitude});

        setListAdapter(adapter);

    }

    private class MyLocationListener implements LocationListener {

        public void onLocationChanged(Location location) {
            Toast.makeText(MainLocation.this, "New Location Updated",
                    Toast.LENGTH_SHORT).show();
        }

        public void onStatusChanged(String s, int i, Bundle b) {
            Toast.makeText(MainLocation.this, "Provider status changed",
                    Toast.LENGTH_SHORT).show();
        }

        public void onProviderDisabled(String s) {
            Toast.makeText(MainLocation.this,
                    "Provider disabled by the user. GPS turned off",
                    Toast.LENGTH_SHORT).show();
        }

        public void onProviderEnabled(String s) {
            Toast.makeText(MainLocation.this,
                    "Provider enabled by the user. GPS turned on",
                    Toast.LENGTH_SHORT).show();
        }

    }

    private String getDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }

}