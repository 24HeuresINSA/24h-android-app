package com.insalyon.les24heures.DTO;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by remi on 27/12/14.
 */
public class AssomakerDTO {
    Map< Integer , ArrayList<ResourceDTO>> animations;

    public Map<Integer, ArrayList<ResourceDTO>> getAnimations() {
        return animations;
    }

    public void setAnimations(Map<Integer, ArrayList<ResourceDTO>> animations) {
        this.animations = animations;
    }
}
