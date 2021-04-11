package com.example.licenta.util.dateUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateConverter {

    // Format
    public static String FORMAT_DATE = "dd-MM-yyyy";
    public static final SimpleDateFormat formatter = new SimpleDateFormat(FORMAT_DATE, Locale.US);


    // Metode
    // to date
    public static Date toDate(String value) {
        try {
            return formatter.parse(value);
        } catch (ParseException e) {
            return null;
        }
    }

    // to string
    public static String toString(Date value) {
        if (value == null) {
            return null;
        }
        return formatter.format(value);
    }


    // from milis to date
    public static Date toDateFromLong(long millis){
        try {
            return new Date(millis);
        } catch (Exception e) {
            return null;
        }
    }
}
