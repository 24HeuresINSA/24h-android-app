package com.insalyon.les24heures.DTO;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by remi on 27/12/14.
 */
public class AssomakerDTO {
    Map< Integer , ArrayList<DayResourceDTO>> animations;
    ArrayList<NightResourceDTO> artists;


    public Map<Integer, ArrayList<DayResourceDTO>> getAnimations() {
        return animations;
    }

    public ArrayList<NightResourceDTO> getArtists() {
        return artists;
    }
}
