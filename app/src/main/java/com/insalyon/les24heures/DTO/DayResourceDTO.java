package com.insalyon.les24heures.DTO;

import java.util.ArrayList;

/**
 * Created by remi on 27/12/14.
 */
public class DayResourceDTO {
    String nom;
    String description;
    String locX;
    String locY;
    ArrayList<ScheduleDTO> horaires;

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

    public ArrayList<ScheduleDTO> getHoraires() {
        return horaires;
    }

    public void setHoraires(ArrayList<ScheduleDTO> horaires) {
        this.horaires = horaires;
    }
}
