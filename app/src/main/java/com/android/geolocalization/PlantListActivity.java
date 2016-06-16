package com.android.geolocalization;

import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import java.util.ArrayList;

public class PlantListActivity extends AppCompatActivity {

    ArrayList<String> item_names = new ArrayList<>();
    ArrayList<Bitmap> item_images = new ArrayList<>();
    ArrayList<Integer> item_id = new ArrayList<>();

    ArrayList<Plant> plants;
    private ListView lista;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plant_list);

        llenarListas();

        AdapterPlantList adapterPlants = new AdapterPlantList(this, item_names, item_images, item_id, plants);
        lista = (ListView)findViewById(R.id.plant_list);
        lista.setAdapter(adapterPlants);
    }

    private void llenarListas() {
        plants = MapsActivity.plants;
        for (Plant plant : plants) {
            item_names.add(plant.get_Name());
            item_images.add(plant.get_Image());
            item_id.add(plant.get_Id());
        }
    }
}
