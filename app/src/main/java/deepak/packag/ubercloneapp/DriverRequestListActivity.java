package deepak.packag.ubercloneapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.FindCallback;
import com.parse.LogOutCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class DriverRequestListActivity extends AppCompatActivity implements  View.OnClickListener , AdapterView.OnItemClickListener {

    private Button btnGetRequest;
    private ListView listView;
    private ArrayList<String> nearByDriveRequest;
    private ArrayAdapter adapter;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private ArrayList<Double> passengerLatitudes;
    private ArrayList<Double> passengerLongitudes;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_request_list);

        btnGetRequest = findViewById(R.id.btnGetRequest);
        btnGetRequest.setOnClickListener(this);
        listView = findViewById(R.id.requestListView);

        nearByDriveRequest = new ArrayList<>();
        passengerLatitudes = new ArrayList<>();
        passengerLongitudes = new ArrayList<>();
        adapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1,nearByDriveRequest);

        listView.setAdapter(adapter);
        nearByDriveRequest.clear();

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);


        if (Build.VERSION.SDK_INT < 23 || ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){

            locationListener = new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {

                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,locationListener);
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

        }

        listView.setOnItemClickListener(this);




    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.driver_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId() == R.id.driverLogout){

            ParseUser.logOutInBackground(new LogOutCallback() {
                @Override
                public void done(ParseException e) {

                    if (e==null){

                        finish();
                    }
                }
            });

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {




        if (Build.VERSION.SDK_INT < 23){

            Location currentDriverLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            updateRequestListView(currentDriverLocation);


        }else if (Build.VERSION.SDK_INT >= 23){

            if (ContextCompat.checkSelfPermission(DriverRequestListActivity.this,Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){

                ActivityCompat.requestPermissions(DriverRequestListActivity.this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1000);
            }else {

            //    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,locationListener);
                Location currentDriverLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                updateRequestListView(currentDriverLocation);

            }
        }

    }

    private void updateRequestListView(Location driverLocation){

        if (driverLocation!=null){


            final ParseGeoPoint driverCurrentLocation = new ParseGeoPoint(driverLocation.getLatitude(),driverLocation.getLongitude());

            ParseQuery<ParseObject> requestCarQuery= ParseQuery.getQuery("RequestCar");

            requestCarQuery.whereNear("passengerLocation" , driverCurrentLocation);
            requestCarQuery.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> objects, ParseException e) {

                    if (e==null){
                    if (objects.size()>0){

                        if (nearByDriveRequest.size() > 0 ){
                            nearByDriveRequest.clear();
                        }
                        if (passengerLatitudes.size() > 0){
                            passengerLatitudes.clear();
                        }
                        if (passengerLongitudes.size()>0){
                            passengerLongitudes.clear();
                        }

                        for (ParseObject nearRequest : objects){

                            ParseGeoPoint pLocation = (ParseGeoPoint) nearRequest.get("passengerLocation");

                            Double milesDistanceToPassenger = driverCurrentLocation.distanceInMilesTo(pLocation);

                            float roundedDistanceValue = Math.round(milesDistanceToPassenger * 10) / 10;

                            nearByDriveRequest.add(" There are " + roundedDistanceValue + " miles to " + nearRequest.get("username"));
                            passengerLatitudes.add(pLocation.getLatitude());
                            passengerLongitudes.add(pLocation.getLongitude());


                        }


                    }else {

                        Toast.makeText(DriverRequestListActivity.this, "Sorry no request" , Toast.LENGTH_SHORT).show();
                    }
                        adapter.notifyDataSetChanged();

                    }

                }
            });


        }

   }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode==1000 && grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){

            if (ContextCompat.checkSelfPermission(DriverRequestListActivity.this,Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){

                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,locationListener);
                Location currentDriverLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                updateRequestListView(currentDriverLocation);
            }
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        Toast.makeText(this,"Clicked" , Toast.LENGTH_SHORT).show();

    }
}
