package com.ibm.project_traffic.Packages.Utils;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Created by larryasante on 7/28/15.
 */
public class Geolocation {
    public static String reverseGeocoding(double latitude, double longitude, Context context){
        String city, state, country, locName;
        city = " ";
        state = " ";
        locName = " ";
        country = " ";
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        try{
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            city = addresses.get(0).getAddressLine(0);
            state = addresses.get(0).getAddressLine(1);
            country = addresses.get(0).getAddressLine(2);
            locName = city + "," + " " + state + "," + " " + country;
        }catch(IOException io){
            io.printStackTrace();
        }
        return locName;
    }
}
