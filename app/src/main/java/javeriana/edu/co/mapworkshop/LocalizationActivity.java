package javeriana.edu.co.mapworkshop;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import javeriana.edu.co.mapworkshop.dto.MyLocation;

public class LocalizationActivity extends AppCompatActivity {

  private final static int CONTACTS_PERMISSION = 0;
  private final static int LOCATION_PERMISSION = 1;
  private static final double RADIUS_OF_EARTH_KM = 6371;
  private static final double AIRPORT_LATITUDE = 4.6889695;
  private static final double AIRPORT_LONGITUDE = -74.1398821;

  private TextView latitude, longitude, altitude, distanceToAirport;

  private ListView locationList;

  //Location
  private FusedLocationProviderClient fusedLocationClient;
  private LocationRequest locationRequest;

  private LocationCallback locationCallback;

  private ArrayAdapter<String> arrayAdapter;

  List<String> locationListValues;

  @Override
  protected void onResume() {
    super.onResume();
    startLocationUpdates();
  }

  @Override
  protected void onPause() {
    super.onPause();
    stopLocationUpdates();
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_localization);

    latitude = findViewById(R.id.latitudeTextView);
    longitude = findViewById(R.id.longitudeTextView);
    altitude = findViewById(R.id.altitudeTextView);
    distanceToAirport = findViewById(R.id.airportDistanceView);

    locationListValues = new ArrayList<>();
    locationList = findViewById(R.id.locationListView);
    arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,
        locationListValues);
    locationList.setAdapter(arrayAdapter);
    locationRequest = createLocationRequest();

    requestPermission(this, Manifest.permission.READ_CONTACTS, "Contact access needed.",
        CONTACTS_PERMISSION);
    requestPermission(this, Manifest.permission.ACCESS_FINE_LOCATION, "Location access needed.",
        LOCATION_PERMISSION);

    fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

    locationCallback = new LocationCallback() {
      @Override
      public void onLocationResult(LocationResult locationResult) {
        Location location = locationResult.getLastLocation();
        Log.i("Location", "location callback update: " + location);
        if (location != null) {
          latitude.setText("Latitude: " + String.valueOf(location.getLatitude()));
          longitude.setText("Longitude: " + String.valueOf(location.getLongitude()));
          altitude.setText("Altitude: " + String.valueOf(location.getAltitude()));

          double distance = distance(AIRPORT_LATITUDE, AIRPORT_LONGITUDE, location.getLatitude(),
              location.getLongitude());

          distanceToAirport.setText("Distance to airport: " + String.valueOf(distance));

        }
      }
    };

  }


  @Override
  public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
      @NonNull int[] grantResults) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    switch (requestCode) {
      case CONTACTS_PERMISSION: {
        initView();
        break;
      }
    }
  }

  private LocationRequest createLocationRequest() {
    LocationRequest locationRequest = new LocationRequest();
    locationRequest.setInterval(10000);
    locationRequest.setFastestInterval(5000);
    locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    return locationRequest;
  }

  private void requestPermission(Activity context, String permission, String explanation,
      int requestId) {
    if (ContextCompat.checkSelfPermission(context, permission)
        != PackageManager.PERMISSION_GRANTED) {
      // Should we show an explanation?
      if (ActivityCompat.shouldShowRequestPermissionRationale(context, permission)) {
        Toast.makeText(context, explanation, Toast.LENGTH_LONG).show();
      }
      ActivityCompat.requestPermissions(context, new String[]{permission}, requestId);
    }
  }

  public void addLocationsToLocationsList(View view) {
    MyLocation myLocation = new MyLocation(latitude.getText().toString(),
        longitude.getText().toString());
    locationListValues.add(myLocation.toString());
    arrayAdapter.notifyDataSetChanged();
    writeJSONObject(myLocation);

  }

  private void writeJSONObject(MyLocation myLocation) {
    Writer output = null;
    String filename = "locations.json";
    try {
      File file = new File(getBaseContext().getFilesDir(), filename);
      Log.i("LOCATION", "File location: " + file);
      output = new BufferedWriter(new FileWriter(file));
      output.write(myLocation.toJSON().toString());
      output.close();
      Toast.makeText(getApplicationContext(), "Location saved",
          Toast.LENGTH_LONG).show();
    } catch (Exception e) {
      Toast.makeText(getBaseContext(), e.getMessage(), Toast.LENGTH_LONG).show();
    }
  }

  /*Contacts shit.*/
  private void initView() {
    if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)
        == PackageManager.PERMISSION_GRANTED) {
      Log.i("INITVIEW", "pasé por acá");

    } else {

    }
  }

  private void startLocationUpdates() {
    if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
        == PackageManager.PERMISSION_GRANTED) {
      fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
    }
  }

  private void stopLocationUpdates() {
    fusedLocationClient.removeLocationUpdates(locationCallback);
  }

  public double distance(double lat1, double long1, double lat2, double long2) {
    double latDistance = Math.toRadians(lat1 - lat2);
    double lngDistance = Math.toRadians(long1 - long2);
    double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
        + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
        * Math.sin(lngDistance / 2) * Math.sin(lngDistance / 2);
    double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    double result = RADIUS_OF_EARTH_KM * c;
    return Math.round(result * 100.0) / 100.0;
  }

}
