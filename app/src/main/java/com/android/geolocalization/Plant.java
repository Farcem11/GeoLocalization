package com.android.geolocalization;

import android.graphics.Bitmap;

/**
 * Created by Farcem on 05-Jun-16.
 */
public class Plant
{
    private int _Id;
    private String _Name;
    private Bitmap _Image;
    private double _Latitude;
    private double _Longitude;
    private String _Planter;
    private String _Donor;

    public Plant(int _Id, String _Name, Bitmap _Image, double _Latitude, double _Longitude, String _Planter, String _Donor)
    {
        this._Id = _Id;
        this._Name = _Name;
        this._Image = _Image;
        this._Latitude = _Latitude;
        this._Longitude = _Longitude;
        this._Planter = _Planter;
        this._Donor = _Donor;
    }
    public int get_Id() {
        return _Id;
    }

    public void set_Id(int _Id) {
        this._Id = _Id;
    }

    public String get_Name() {
        return _Name;
    }

    public void set_Name(String _Name) {
        this._Name = _Name;
    }

    public Bitmap get_Image() {
        return _Image;
    }

    public void set_Image(Bitmap _Image) {
        this._Image = _Image;
    }

    public double get_Latitude() {
        return _Latitude;
    }

    public void set_Latitude(double _Latitude) {
        this._Latitude = _Latitude;
    }

    public double get_Longitude() {
        return _Longitude;
    }

    public void set_Longitude(double _Longitude) {
        this._Longitude = _Longitude;
    }

    public String get_Planter() {
        return _Planter;
    }

    public void set_Planter(String _Planter) {
        this._Planter = _Planter;
    }

    public String get_Donor() {
        return _Donor;
    }

    public void set_Donor(String _Donor) {
        this._Donor = _Donor;
    }
}
