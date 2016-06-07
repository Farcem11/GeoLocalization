package com.android.geolocalization;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback
{
    private GoogleMap mMap;
    static Context context;
    double latitude;
    double longitude;
    LocationManager mLocationManager;
    ArrayList<Plant> plants;
    private HashMap<Marker, Plant> markerMapPlant = new HashMap<>();
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER))
        {
            buildAlertMessage("Your GPS is disabled, please enabled it");
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        context = this;
    }

    private void buildAlertMessage(String pMessage) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(pMessage)
                .setCancelable(false)
                .setPositiveButton("Enable", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    public void showPlant(Plant plant)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.plant_info, null);
        ((TextView) dialogView.findViewById(R.id.textViewName)).setText(plant.get_Name());
        ((TextView) dialogView.findViewById(R.id.textViewPlanter)).setText(plant.get_Planter());
        ((TextView) dialogView.findViewById(R.id.textViewDonor)).setText(plant.get_Donor());
        ((ImageView) dialogView.findViewById(R.id.imageViewPlant)).setImageBitmap(plant.get_Image());

        builder.setView(dialogView)
        .setPositiveButton("Cerrar", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int id)
            {
                dialog.cancel();
            }
        });

        final AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener()
        {
            @Override
            public boolean onMarkerClick(Marker marker)
            {
                showPlant(markerMapPlant.get(marker));
                return false;
            }
        });
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            return;
        mMap.setMyLocationEnabled(true);

        final LocationListener locationListener = new LocationListener()
        {
            public void onLocationChanged(Location location)
            {
                updateLocation(getLastKnownLocation());
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
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 20, locationListener);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 20, locationListener);
/*
        Bitmap image = null;
        try
        {
            image = new getBitmapFromURL().execute("http://cdn.playbuzz.com/cdn/35df18d9-8369-4566-991a-c7379c53e34d/62abd3fa-f460-4d80-8ff6-f52300526cbb.jpg").get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        DataBase.addPlant(new Plant(0, "Planta 5", image, 9.9756239,-84.0180020, "Kevin", "Kevin"));
*/
        plants = DataBase.getPlants();
        markerMapPlant.clear();
        for (Plant plant : plants)
        {
            Marker marker = mMap.addMarker(new MarkerOptions().position(new LatLng(plant.get_Latitude(), plant.get_Longitude())));
            markerMapPlant.put(marker, plant);
        }
    }

    private void updateLocation(Location location)
    {
        longitude = location.getLongitude();
        latitude = location.getLatitude();
        LatLng myLocation = new LatLng(location.getLatitude(), location.getLongitude());
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myLocation,20));
    }

    private Location getLastKnownLocation()
    {
        mLocationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
        List<String> providers = mLocationManager.getProviders(true);
        Location bestLocation = null;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            for (String provider : providers) {
                Location l = mLocationManager.getLastKnownLocation(provider);
                if (l == null) {
                    continue;
                }
                if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                    bestLocation = l;
                }
            }
            return bestLocation;
        }
        else
            return null;
    }

    private class getBitmapFromURL extends AsyncTask<String, Void, Bitmap>
    {
        protected Bitmap doInBackground(String... pUrl)
        {
            try
            {
                URL url = new URL(pUrl[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                Bitmap myBitmap = BitmapFactory.decodeStream(input);
                return myBitmap;
            }
            catch (IOException e)
            {
                e.printStackTrace();
                return null;
            }
        }
    }
}
