package com.android.geolocalization;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
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
    static void addPlant(Plant plant)
    {
        try
        {
            String query = String.format("Name=%s&Image=%s&Latitude=%s&Longitude=%s&Planter=%s&Donor=%s",
                    URLEncoder.encode(plant.get_Name(), charset),
                    URLEncoder.encode(BitMapToString(plant.get_Image()), charset),
                    URLEncoder.encode(String.valueOf(plant.get_Latitude()), charset),
                    URLEncoder.encode(String.valueOf(plant.get_Longitude()), charset),
                    URLEncoder.encode(plant.get_Planter(), charset),
                    URLEncoder.encode(plant.get_Donor(), charset));
            new Server().execute(addPlantURL, query);

        } catch (UnsupportedEncodingException e) {
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
}
