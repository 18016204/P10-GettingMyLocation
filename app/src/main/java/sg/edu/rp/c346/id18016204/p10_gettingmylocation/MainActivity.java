package sg.edu.rp.c346.id18016204.p10_gettingmylocation;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.PermissionChecker;
import androidx.fragment.app.FragmentManager;

import android.Manifest;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    Button btnStart, btnStop, btnCheckRecord;
    TextView tvLat, tvLong;

    GoogleMap map;

    // SG Coordinate
    LatLng singaporeCoords = new LatLng(1.3521, 103.8198);
    Marker currentLocationMarker;


    FusedLocationProviderClient client;
    LocationRequest mLocationRequest;
    LocationCallback mLocationCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnStart = findViewById(R.id.btnStart);
        btnStop = findViewById(R.id.btnstop);
        btnCheckRecord = findViewById(R.id.btnCheckRecords);
        tvLat = findViewById(R.id.tvLat);
        tvLong = findViewById(R.id.tvLong);

        client = LocationServices.getFusedLocationProviderClient(this);


        FragmentManager fragmentManager = getSupportFragmentManager();
        SupportMapFragment mapFragment = (SupportMapFragment) fragmentManager.findFragmentById(R.id.mapView);

        mapFragment.getMapAsync(gMap -> {
            map = gMap;
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(singaporeCoords, 10));
            UiSettings uiSettings = map.getUiSettings();
            uiSettings.setZoomControlsEnabled(true);
        });


        if(checkPermission() == true){
            Task<Location> task = client.getLastLocation();
            task.addOnSuccessListener(MainActivity.this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    //Get last known location
                    if (location != null){
                        String lat = "Latitude : " + location.getLatitude();
                        String longt = "Longtitude: " + location.getLongitude();
                        tvLat.setText(lat);
                        tvLong.setText(longt);
                        Toast.makeText(MainActivity.this, lat + longt, Toast.LENGTH_SHORT).show();
                    } else {
                        String msg = "No Last Known Location found";
                        Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //configurations, how soon, how often

                Intent i = new Intent(MainActivity.this, MyService.class);
                startService(i);
                Toast.makeText(MainActivity.this, "Service Running", Toast.LENGTH_SHORT).show();
            }
        });

        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainActivity.this, MyService.class);
                stopService(i);
                Toast.makeText(MainActivity.this, "Service stopped Running", Toast.LENGTH_SHORT).show();

            }
        });

        btnCheckRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent i = new Intent(MainActivity.this, RecordActivity.class);
//                startActivity(i);

                String folderLocation = Environment.getExternalStorageDirectory().getAbsolutePath() + "/P09";
                File targetFile = new File(folderLocation, "data.txt");

                if(targetFile.exists()){
                    String data = "";
                    try{
                        FileReader reader = new FileReader(targetFile);
                        BufferedReader br = new BufferedReader(reader);
                        String line = br.readLine();
                        while(line != null){
                            data += line + "\n";
                            line = br.readLine();
                        }
                        br.close();
                        reader.close();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Toast.makeText(MainActivity.this, data + "\n", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private boolean checkPermission(){
        int permissionCheck_Course = ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION);
        int permissionCheck_Fine = ContextCompat.checkSelfPermission(MainActivity.this,Manifest.permission.ACCESS_FINE_LOCATION);

        if (permissionCheck_Course == PermissionChecker.PERMISSION_GRANTED ||
                permissionCheck_Fine == PermissionChecker.PERMISSION_GRANTED){
            return true;
        }else {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[] {Manifest.permission.ACCESS_FINE_LOCATION},0);
            return false;
        }
    }
}