package com.orange.oy.util;

import com.orange.oy.info.MyteamNewfdInfo;

import java.util.Comparator;

public class PinyinComparatorForMyteam implements Comparator<MyteamNewfdInfo> {

    public int compare(MyteamNewfdInfo o1, MyteamNewfdInfo o2) {
        if (o1.getSortLetters().equals("@")
                || o2.getSortLetters().equals("#")) {
            return -1;
        } else if (o1.getSortLetters().equals("#")
                || o2.getSortLetters().equals("@")) {
            return 1;
        } else {
            return o1.getSortLetters().compareTo(o2.getSortLetters());
        }
    }

}
