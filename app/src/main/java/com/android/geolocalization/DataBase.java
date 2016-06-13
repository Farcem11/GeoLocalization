package com.android.geolocalization;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.util.Base64;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

/**
 * Created by Farcem on 05-Jun-16.
 */
public class DataBase
{
    private static String getPlantsURL = "http://gramalab.esy.es/getPlants.php";
    private static String deletePlantsURL = "http://gramalab.esy.es/deletePlant.php";
    private static String addPlantURL = "http://gramalab.esy.es/addPlant.php";
    private static String charset = "UTF-8";

    public static ArrayList<Plant> getPlants()
    {
        ArrayList<Plant> plants = new ArrayList<>();
        try
        {
            JSONArray responseJSON = new Server().execute(getPlantsURL, "").get();
            JSONObject jsonObject;
            byte[] byteArray;
            Bitmap image;
            for (int i = 0; responseJSON != null && i < responseJSON.length(); i++)
            {
                jsonObject = responseJSON.getJSONObject(i);
                byteArray = Base64.decode(jsonObject.getString("Image"), Base64.DEFAULT);
                image = BitmapFactory.decodeByteArray(byteArray, 0 ,byteArray.length);
                plants.add(new Plant(jsonObject.getInt("idPlant"),jsonObject.getString("Name"), image, jsonObject.getDouble("Latitude"), jsonObject.getDouble("Longitude"), jsonObject.getString("Planter"), jsonObject.getString("Donor")));
            }
            return plants;
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    static void deletePlant(int pId) {
        try {

            String query = String.format("Id=%s",
                    URLEncoder.encode(String.valueOf(pId), charset));
            new Server().execute(deletePlantsURL, query).get();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }
    static void addPlant(Plant plant)
    {
        try
        {
            plant.set_Image(getResizedBitmap(plant.get_Image(), 800, 600));
            String query = String.format("Name=%s&Image=%s&Latitude=%s&Longitude=%s&Planter=%s&Donor=%s",
                    URLEncoder.encode(plant.get_Name(), charset),
                    URLEncoder.encode(BitMapToString(plant.get_Image()), charset),
                    URLEncoder.encode(String.valueOf(plant.get_Latitude()), charset),
                    URLEncoder.encode(String.valueOf(plant.get_Longitude()), charset),
                    URLEncoder.encode(plant.get_Planter(), charset),
                    URLEncoder.encode(plant.get_Donor(), charset));

            new Server().execute(addPlantURL, query).get();
            Marker marker = MapsActivity.mMap.addMarker(new MarkerOptions().position(new LatLng(plant.get_Latitude(), plant.get_Longitude())));
                MapsActivity.markerMapPlant.put(marker, plant);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    public static String BitMapToString(Bitmap bitmap)
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] b = baos.toByteArray();
        return Base64.encodeToString(b, Base64.DEFAULT);
    }

    public static Bitmap getResizedBitmap(Bitmap bm, int newWidth, int newHeight) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // CREATE A MATRIX FOR THE MANIPULATION
        Matrix matrix = new Matrix();
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight);

        // "RECREATE" THE NEW BITMAP
        Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, false);
        bm.recycle();
        return resizedBitmap;
    }
}
