package com.android.geolocalization;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

/**
 * Created by Farcem on 05-Jun-16.
 */
public class DataBase
{
    private static String getPlantsURL = "http://gramalab.esy.es/getPlants.php";
    public static ArrayList<Plant> getPlants()
    {
        ArrayList<Plant> plants = new ArrayList<>();
        try
        {
            JSONArray responseJSON = new Server().execute(getPlantsURL, "").get();
            JSONObject jsonObject;
            byte[] byteArray;
            Bitmap image;
            for (int i = 0; i < responseJSON.length(); i++)
            {
                jsonObject = responseJSON.getJSONObject(i);
                byteArray = jsonObject.getString("Image").getBytes();
                image = BitmapFactory.decodeByteArray(byteArray, 0 ,byteArray.length);
                plants.add(new Plant(jsonObject.getString("Name"), image, jsonObject.getDouble("Latitude"), jsonObject.getDouble("Longitude"), jsonObject.getString("Planter"), jsonObject.getString("Donor")));
            }
            return plants;
        }
        catch (InterruptedException e) {
            Toast.makeText(MapsActivity.context, "Error: " + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
        }
        catch (ExecutionException e) {
            Toast.makeText(MapsActivity.context, "Error: " + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
        } catch (JSONException e) {
            Toast.makeText(MapsActivity.context, "Error: " + e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
        }
        return null;
    }
}
