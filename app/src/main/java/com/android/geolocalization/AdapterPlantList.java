package com.android.geolocalization;

import android.app.Activity;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by jasc9 on 12/6/2016.
 */
public class AdapterPlantList extends ArrayAdapter<String> {

    private final Activity context;
    private final ArrayList<String> itemname;
    private final ArrayList<Bitmap> integers;
    private final ArrayList<Integer> id_plantas;
    private final ArrayList<Plant> item_plants;

    public AdapterPlantList(Activity context, ArrayList<String> itemname, ArrayList<Bitmap> integers, ArrayList<Integer> id_plantas, ArrayList<Plant> item_plants) {
        super(context, R.layout.fila_lista, itemname);
        // TODO Auto-generated constructor stub

        this.context = context;
        this.itemname = itemname;
        this.integers = integers;
        this.id_plantas = id_plantas;
        this.item_plants = item_plants;
    }

    public View getView(final int posicion, View view, ViewGroup parent){

        LayoutInflater inflater=context.getLayoutInflater();
        View rowView=inflater.inflate(R.layout.fila_lista,null,true);

        TextView txtTitle = (TextView) rowView.findViewById(R.id.nombre_principal);
        TextView txtSecundario = (TextView) rowView.findViewById(R.id.texto_secundario);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.icon);
        Button deletePlant = (Button) rowView.findViewById(R.id.delete_plant);

        txtTitle.setText(itemname.get(posicion));
        txtSecundario.setText(id_plantas.get(posicion) + "");
        imageView.setImageBitmap(integers.get(posicion));
        deletePlant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DataBase.deletePlant(item_plants.get(posicion));
                //item_plants.remove(posicion);
                MapsActivity.plants.remove(posicion);
                notifyDataSetChanged();
            }
        });

        return rowView;
    }
}
