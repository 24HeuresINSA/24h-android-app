package com.insalyon.les24heures.model;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

/**
 * Created by remi on 27/12/14.
 */
public class ResourceDTO {
    String nom;
    String description;
    String locX;
    String locY;

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
