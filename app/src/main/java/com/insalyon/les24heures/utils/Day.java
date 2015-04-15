package com.insalyon.les24heures.utils;

/**
 * Created by remi on 30/01/15.
 */
public enum Day {
    MONDAY(1), TUESDAY(2), WEDNESDAY(3),
    THURSDAY(4), FRIDAY(5), SATURDAY(6), SUNDAY(7), NODAY(8);

    private final int rank;

    private Day(int rank) {
        this.rank = rank;
    }

    public int getRank() {
        return rank;
    }

}
