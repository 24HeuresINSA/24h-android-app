package com.insadelyon.les24heures.utils;

import com.insadelyon.les24heures.model.NightResource;

import java.util.Comparator;

/**
 * Created by nicolas on 08/03/15.
 */
public class PositionSortComparator implements Comparator<NightResource> {


    @Override
    public int compare(NightResource res1, NightResource res2) {
        return res1.getPosition().compareTo(res2.getPosition());
    }
}
