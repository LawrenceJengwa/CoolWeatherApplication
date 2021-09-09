package com.lawrence.coolweatherapplication.utils;


import android.widget.ImageView;

import com.squareup.picasso.Picasso;

public class WeatherUtil {

    public static String dayImageUrl = "https://images.unsplash.com/photo-1542709111240-e9df0dd813b4?ixid=MnwxMjA3fDB8MHxzZWFyY2h8Mnx8ZGF5fGVufDB8fDB8fA%3D%3D&ixlib=rb-1.2.1&auto=format&fit=crop&w=500&q=60";
    public static String nightImageUrl = "https://images.unsplash.com/photo-1475274047050-1d0c0975c63e?ixid=MnwxMjA3fDB8MHxzZWFyY2h8MXx8bmlnaHQlMjBza3l8ZW58MHx8MHx8&ixlib=rb-1.2.1&auto=format&fit=crop&w=500&q=60";
    public static String OUTPUT_PATTERN = "yyyy-MM-dd hh:mm";
    public static String INPUT_PATTERN = "hh:mm aa";
    public static String BASE_URL = "http://api.weatherapi.com/v1/forecast.json?key";
    public static String API_KEY = "=89c6e74682074bc3a8182444210809&q=";
    public static String DAYS_SUFFIX = "&days=1&aqi=no&alerts=no";

    public static void loadImage(String imageUrl, ImageView view){
        Picasso.get().load(imageUrl).into(view);
    }

}
