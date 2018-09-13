package com.orange.oy.util;

import com.orange.oy.info.CityInfo;

import java.util.Comparator;

public class PinyinComparator implements Comparator<CityInfo> {

    public int compare(CityInfo o1, CityInfo o2) {
        if (o1.getSortLetters().equals("@") || o2.getSortLetters().equals("#")) {
            return -1;
        } else if (o1.getSortLetters().equals("#") || o2.getSortLetters().equals("@")) {
            return 1;
        } else {
            return o1.getSortLetters().compareTo(o2.getSortLetters());
        }
    }

}
