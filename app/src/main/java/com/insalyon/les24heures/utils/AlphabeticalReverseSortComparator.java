package com.insalyon.les24heures.utils;

import com.insalyon.les24heures.model.Resource;

import java.util.Comparator;

/**
 * Created by nicolas on 08/03/15.
 */
public class AlphabeticalReverseSortComparator implements Comparator<Resource> {


    @Override
    public int compare(Resource res1, Resource res2) {
        return -res1.getTitle().compareTo(res2.getTitle());
    }
}
