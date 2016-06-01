package com.rohail.apps.nearme.utilities;

/**
 * Created by Zaigham on 2/21/2016.
 */

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.rohail.apps.nearme.activities.MapActivity;
import com.rohail.apps.nearme.models.LocationModel;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * A class to parse the Google Places in JSON format
 */
public class ParsePlacesDataTask extends AsyncTask<String, Integer, List<HashMap<String, String>>> {


    JSONObject jObject;
    Context context;

    ParsePlacesDataTask(Context context) {
        this.context = context;
    }

    // Invoked by execute() method of this object
    @Override
    protected List<HashMap<String, String>> doInBackground(String... jsonData) {

        List<HashMap<String, String>> places = null;
        PlaceJSONParser placeJsonParser = new PlaceJSONParser();

        try {
            jObject = new JSONObject(jsonData[0]);

            Log.i("Json Data:", jObject.toString());
            /** Getting the parsed data as a List construct */
            places = placeJsonParser.parse(jObject);

        } catch (Exception e) {
            Log.d("Exception", e.toString());
        }
        return places;
    }

    // Executed after the complete execution of doInBackground() method
    @Override
    protected void onPostExecute(List<HashMap<String, String>> list) {
        StaticData.listLocations = new ArrayList<>();

        for (int i = 0; i < list.size(); i++) {

            // Getting a place from the places list
            HashMap<String, String> hmPlace = list.get(i);

            LocationModel location = new LocationModel();
            location.setId(hmPlace.get("place_id"));
            location.setName(hmPlace.get("name"));
            location.setIconUrl(hmPlace.get("icon"));
            location.setVicinity(hmPlace.get("vicinity"));
            location.setRating(hmPlace.get("rating"));
            location.setLatitude(hmPlace.get("lat"));
            location.setLongitude(hmPlace.get("lng"));
            StaticData.listLocations.add(location);

        }

        MapActivity activity = (MapActivity) context;
        activity.populatePlaces();
    }
}
