package com.example.tourmate.helper;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class EventUtils {
    public static long getDefferentBetweenTwoDate(String currentDate,String selectDate)
    {
        long diffDays=0;

        String dateStart = currentDate; //"25/02/2012 ";
        String dateStop = selectDate;//"02/03/2013";

        //HH converts hour in 24 hours format (0-23), day calculation
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");

        Date d1 = null;
        Date d2 = null;

        try {
            d1 = format.parse(dateStart);
            d2 = format.parse(dateStop);

            //in milliseconds
            long diff = d2.getTime() - d1.getTime();

           diffDays = diff / (24 * 60 * 60 * 1000);



        } catch (Exception e) {
            e.printStackTrace();
        }

        return (diffDays);
    }

    public static String getCrrentDate() {
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Date date = new Date();
        return dateFormat.format(date);
    }

    public static String getDateWithTime() {
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm aa");
        Date date = new Date();
        return dateFormat.format(date);
    }

    public static String getCurrentDateTime(){
        return new SimpleDateFormat("dd/MM/yyyy HH:mm:ss")
                .format(new Date());
    }

    public static class WeatherUtils{
        public static final String UNIT_CELCIUS = "metric";
        public static final String UNIT_FAHRENHEIT = "imperial";
        public static final String UNIT_CELCIUS_SYMBOL = "°C";
        public static final String UNIT_FAHRENHEIT_SYMBOL = "°F";
        public static final String UNIT_DEGREE = "\u00B0";
        public static final String ICON_URL_PREFIX = "https://openweathermap.org/img/wn/";
    }

    public static String getFormattedDate(long dt){
        Date date = new Date(dt * 1000);
        return new SimpleDateFormat("hh:mm a MMM dd, yyyy")
                .format(date);
    }

    public static String getFormattedTime(long dt){
        Date date = new Date(dt * 1000);
        return new SimpleDateFormat("hh:mm a")
                .format(date);
    }


    public static String getFormattedDay(long dt){
        Date date = new Date(dt * 1000);
        return new SimpleDateFormat("EEEE").format(date);
    }
}
