package com.insadelyon.les24heures.DTO;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by remi on 27/12/14.
 */
public class AssomakerDTO {
    Map<Integer, ArrayList<DayResourceDTO>> animations;
    ArrayList<DayResourceDTO> resources;
    ArrayList<NightResourceDTO> artists;
    ArrayList<CategoryDTO> categories;
    String version;



    public Map<Integer, ArrayList<DayResourceDTO>> getAnimations() {
        return animations;
    }

    public ArrayList<NightResourceDTO> getArtists() {
        return artists;
    }

    public ArrayList<CategoryDTO> getCategories() {
        return categories;
    }

    public String getVersion() {
        return version;
    }

    public ArrayList<DayResourceDTO> getResources() {
        return resources;
    }
}
