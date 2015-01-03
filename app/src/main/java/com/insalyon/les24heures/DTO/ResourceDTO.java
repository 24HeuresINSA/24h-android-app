package com.insalyon.les24heures.DTO;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by remi on 27/12/14.
 */
public class ResourceDTO {
    String nom;
    String description;
    String locX;
    String locY;
    //ArrayList<Map<String,String>> horaires;

    @Override
    public String toString() {
        return nom;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocX() {
        return locX;
    }

    public void setLocX(String locX) {
        this.locX = locX;
    }

    public String getLocY() {
        return locY;
    }

    public void setLocY(String locY) {
        this.locY = locY;
    }
}
