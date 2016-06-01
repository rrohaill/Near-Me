package com.rohail.apps.nearme.utilities;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Zaigham on 2/20/2016.
 */
public class PlaceJSONParser {
    /**
     * Receives a JSONObject and returns a list
     */
    public List<HashMap<String, String>> parse(JSONObject jObject) {

        JSONArray jPlaces = null;
        try {
            /** Retrieves all the elements in the 'places' array */
            jPlaces = jObject.getJSONArray("results");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        /** Invoking getPlaces with the array of json object
         * where each json object represent a place
         */
        return getPlaces(jPlaces);
    }

    private List<HashMap<String, String>> getPlaces(JSONArray jPlaces) {
        int placesCount = jPlaces.length();
        List<HashMap<String, String>> placesList = new ArrayList<HashMap<String, String>>();
        HashMap<String, String> place = null;

        /** Taking each place, parses and adds to list object */
        for (int i = 0; i < placesCount; i++) {
            try {
                /** Call getPlace with place JSON object to parse the place */
                place = getPlace((JSONObject) jPlaces.get(i));
                placesList.add(place);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return placesList;
    }

    /**
     * Parsing the Place JSON object
     */
    private HashMap<String, String> getPlace(JSONObject jPlace) {

        HashMap<String, String> place = new HashMap<String, String>();
        String placeId = "-NA-";
        String placeName = "-NA-";
        String placeIconUrl = "-NA-";
        String vicinity = "-NA-";
        String rating = "-NA-";
        String latitude = "";
        String longitude = "";

        try {

            if (!jPlace.isNull("place_id")) {
                placeId = jPlace.getString("place_id");
            }
            // Extracting Place name, if available
            if (!jPlace.isNull("name")) {
                placeName = jPlace.getString("name");
            }

            if (!jPlace.isNull("icon")) {
                placeIconUrl = jPlace.getString("icon");
            }
            // Extracting Place Vicinity, if available
            if (!jPlace.isNull("vicinity")) {
                vicinity = jPlace.getString("vicinity");
            }

            if (!jPlace.isNull("rating")) {
                rating = jPlace.getString("rating");
            }
            latitude = jPlace.getJSONObject("geometry").getJSONObject("location").getString("lat");
            longitude = jPlace.getJSONObject("geometry").getJSONObject("location").getString("lng");

            place.put("id", placeId);
            place.put("name", placeName);
            place.put("icon", placeIconUrl);
            place.put("vicinity", vicinity);
            place.put("rating", rating);
            place.put("lat", latitude);
            place.put("lng", longitude);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return place;
    }
}
