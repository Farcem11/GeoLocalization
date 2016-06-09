package com.android.geolocalization;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    static final int REQUEST_IMAGE_CAPTURE = 1;
    //Take Picture
    private static final String TAG = "MainActivity";
    static Context context;
    double latitude;
    double longitude;
    LocationManager mLocationManager;
    ArrayList<Plant> plants;
    public static GoogleMap mMap;
    public static HashMap<Marker, Plant> markerMapPlant = new HashMap<>();
    private Bitmap mImageBitmap;
    private String mCurrentPhotoPath;

    //form
    private EditText inputPlantName;
    private EditText inputPersonWhoPlanted;
    private EditText inputPersonWhoDonated;

    private File storageDir;
    public static LocationManager manager;
    boolean isZoom = false;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER))
            buildEnableGPSMessage("Por favor habilita el GPS");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        context = this;
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.menu_item_new_plant);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dispatchTakePictureIntent();
            }
        });
    }

    private void dispatchTakePictureIntent() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                Log.e(TAG, "exception", ex);
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                startActivityForResult(cameraIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        storageDir = Environment.getExternalStoragePublicDirectory("GeoLocalization");
        storageDir.mkdirs();
        File image = File.createTempFile(
                imageFileName,  // prefix
                ".jpg",         // suffix
                storageDir      // directory
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = "file:" + image.getAbsolutePath();
        return image;
    }

    void deleteRecursive(File fileOrDirectory) {
        if (fileOrDirectory.isDirectory())
            for (File child : fileOrDirectory.listFiles())
                deleteRecursive(child);

        fileOrDirectory.delete();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            try
            {
                if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER))
                    buildEnableGPSMessage("Por favor habilita el GPS");
                else
                {
                    mImageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), Uri.parse(mCurrentPhotoPath));
                    //showPlant(new Plant(1,"Nueva",mImageBitmap,2,2,"loc","log2"));
                    createPlant();
                    //mImageView.setImageBitmap(mImageBitmap);
                    deleteRecursive(storageDir);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void buildEnableGPSMessage(String pMessage) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(pMessage)
                .setCancelable(false)
                .setPositiveButton("Habilitar", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    public void createPlant() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_create_plant, null);


        ((ImageView) dialogView.findViewById(R.id.imageViewNewPlant)).setImageBitmap(mImageBitmap);
        inputPlantName = (EditText) dialogView.findViewById(R.id.input_plant_name);
        inputPersonWhoPlanted = (EditText) dialogView.findViewById(R.id.input_person_who_planted);
        inputPersonWhoDonated = (EditText) dialogView.findViewById(R.id.input_person_who_donated);

        builder.setView(dialogView)
                .setNegativeButton("CANCELAR",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                .setPositiveButton("GUARDAR",
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {
                                String name = inputPlantName.getText().toString();
                                String planter = inputPersonWhoPlanted.getText().toString();
                                String donor = inputPersonWhoDonated.getText().toString();
                                Plant plant = new Plant(1, name, mImageBitmap, latitude, longitude, planter, donor);

                                DataBase.addPlant(plant);
                                dialog.dismiss();
                                Toast.makeText(context, "Guardado", Toast.LENGTH_SHORT).show();
                            }
                        });

        final AlertDialog alert = builder.create();
        alert.show();
    }

    public void showPlant(Plant plant) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.plant_info, null);
        ((TextView) dialogView.findViewById(R.id.textViewName)).setText(plant.get_Name());
        ((TextView) dialogView.findViewById(R.id.textViewPlanter)).setText(plant.get_Planter());
        ((TextView) dialogView.findViewById(R.id.textViewDonor)).setText(plant.get_Donor());
        ((ImageView) dialogView.findViewById(R.id.imageViewPlant)).setImageBitmap(plant.get_Image());

        builder.setView(dialogView)
                .setPositiveButton("Cerrar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

        final AlertDialog alert = builder.create();
        alert.setCanceledOnTouchOutside(false);
        alert.show();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                showPlant(markerMapPlant.get(marker));
                return false;
            }
        });
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            return;
        mMap.setMyLocationEnabled(true);
        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        final LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location location) 
            {
                if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) 
                    buildEnableGPSMessage("Por favor habilita el GPS");
                else
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
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
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
        LatLng myLocation = new LatLng(latitude, longitude);
        if (isZoom)
            mMap.animateCamera(CameraUpdateFactory.newLatLng(myLocation));
        else 
        {
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 15));
            isZoom = true;
        }
    }

    private Location getLastKnownLocation() {
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
        } else
            return null;
    }

    private class getBitmapFromURL extends AsyncTask<String, Void, Bitmap> {
        protected Bitmap doInBackground(String... pUrl) {
            try {
                URL url = new URL(pUrl[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                Bitmap myBitmap = BitmapFactory.decodeStream(input);
                return myBitmap;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
    }
}
