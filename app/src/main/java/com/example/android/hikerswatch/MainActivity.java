package com.example.android.hikerswatch;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    /** General variables */
    LocationManager mLocationManager;
    LocationListener mLocationListener;

    TextView mLatitudeTextView;
    TextView mLongitudeTextView;
    TextView mAccuracyTextView;
    TextView mAltitudeTextView;
    TextView mAddressTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize Views from Layout
        mLatitudeTextView = (TextView) findViewById(R.id.latitudeTextView);
        mLongitudeTextView = (TextView) findViewById(R.id.longitudeTextView);
        mAccuracyTextView = (TextView) findViewById(R.id.accuracyTextView);
        mAltitudeTextView = (TextView) findViewById(R.id.altitudeTextView);
        mAddressTextView = (TextView) findViewById(R.id.addressTextView);

        // Initialize LocationManager
        mLocationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        // Initialize and create a new location listener
        mLocationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                setInfo(location);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };

        // If device is running SDK < 23
        if (Build.VERSION.SDK_INT < 23) {
            startListening();
        } else {
            // Check permission
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                // Ask permission
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);

            } else {
                // We have permission!
                mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, mLocationListener);
                // Get the last known location, and check if isn't null before calling our method
                Location location = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if(location != null) {
                    setInfo(location);
                }

            }
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // Check the permission result, if the user agreed then call our method to start listening
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startListening();
        }
    }

    // Method for setting up the TextView information
    private void setInfo(Location location) {

        // Starter string for Address TextView
        String address = "Address:";

        // Getting information about the current location
        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
        mLatitudeTextView.setText("Latitude: " + location.getLatitude());
        mLongitudeTextView.setText("Longitude: " + location.getLongitude());
        mAltitudeTextView.setText("Altitude: " +location.getAltitude());
        mAccuracyTextView.setText("Accuracy: "+ location.getAccuracy());

        try {
            List<Address> addressList = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
            // Check if the list isn't null or empty, if it is, then set a default text in the else
            // statement.
            if(addressList != null && addressList.size() > 0) {
                // Check if we have and address and set the text for every line
                if(addressList.get(0).getAddressLine(0) != null) {
                    // Get all the lines of Address (streets)
                    for (int i = 0; i < addressList.get(0).getMaxAddressLineIndex(); i++) {
                        address += "\n" + addressList.get(0).getAddressLine(i);
                    }
                    if(addressList.get(0).getCountryName() != null) {
                        address += "\n"+ addressList.get(0).getCountryName();
                    }
                    mAddressTextView.setText(address);
                }
            } else { mAddressTextView.setText("Could not find address"); }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void startListening() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
            // If we got the permission, then get the last location and update the TextViews with
            // our private method.
            Location location = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            setInfo(location);
        }
    }
}
