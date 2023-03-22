package com.zerobase.dividend.model.constants;

public enum Month {
    JAN("Jan", 1),
    FEB("Feb", 2),
    MAR("Mar", 3),
    APR("Apr", 4),
    MAY("May", 5),
    JUN("Jun", 6),
    JUL("Jul", 7),
    AUG("Aug", 8),
    SEP("Sep", 9),
    OCT("Oct", 10),
    NOV("Nov", 11),
    DEC("Dec", 12);

    private String month;
    private int monthNum;

    Month(String month, int monthNum) {
        this.month = month;
        this.monthNum = monthNum;
    }

    public static int strToNumber(String s) {
        for (var m : Month.values()) {
            if (m.month.equals(s)) {
                return m.monthNum;
            }
        }

        return -1;
    }
}
