package com.example.uclayelp;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

public class JSONParser {

    static InputStream is = null;
    static JSONObject jObj = null;
    static String json = "";
    
    private static String REVIEWS_BASE_URL = "http://54.186.3.129/app/reviews/";

    // constructor
    public JSONParser() {}
    
   
    public Menu getMenuFromJson(String url, String json_diningHall) {
    	Menu menu = null;
        // Making HTTP request
        try {
            // defaultHttpClient
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpGet httpPost = new HttpGet(url);

            HttpResponse httpResponse = httpClient.execute(httpPost);
            HttpEntity httpEntity = httpResponse.getEntity();
            String s =  EntityUtils.toString(httpEntity);
            try {
				JSONObject jObj = new JSONObject(s);
		   		JSONObject menuObj = jObj.getJSONObject(json_diningHall);
	    		
	    		JSONArray lunchArray = menuObj.getJSONArray("lunch");
	    		JSONArray dinnerArray = menuObj.getJSONArray("dinner");
	    		
	    		ArrayList<Station> lunchStations = new ArrayList<Station>();
	    		ArrayList<Station> dinnerStations = new ArrayList<Station>();
	    		
	    		// lunch: get stations, and entrees for each station
	    		Log.w("blah", "lunch array length" + lunchArray.length());
	    		for (int i = 0; i < lunchArray.length(); i++) {
	    			
	    			JSONObject stationObj = lunchArray.getJSONObject(i);
	    			String stationName = stationObj.getString("station");
	    			
	    			ArrayList<Entree> stationEntrees = new ArrayList<Entree>();
	    			JSONArray entreeArray = stationObj.getJSONArray("items");
	    			for (int j = 0; j < entreeArray.length(); j++) {
	    				JSONObject entreeObj = entreeArray.getJSONObject(j);
	    				String entreeName = entreeObj.getString("title");
	    				int entreeId = entreeObj.getInt("id");
	    				float rating = (float) entreeObj.getDouble("rating");
	    				Entree e = new Entree(rating, entreeId, entreeName);
	    				stationEntrees.add(e);
	    				
	    			}

	    			Station station = new Station(json_diningHall, stationName, stationEntrees);
	    			lunchStations.add(station);
	    		}
	    		
	    		// dinner: get stations, and entrees for each station
	    		for (int i = 0; i < dinnerArray.length(); i++) {
	    			
	    			JSONObject stationObj = dinnerArray.getJSONObject(i);
	    			String stationName = stationObj.getString("station");
	    			
	    			ArrayList<Entree> stationEntrees = new ArrayList<Entree>();
	    			JSONArray entreeArray = stationObj.getJSONArray("items");
	    			
	    			for (int j = 0; j < entreeArray.length(); j++) {
	    				JSONObject entreeObj = entreeArray.getJSONObject(j);
	    				String entreeName = entreeObj.getString("title");
	    				int entreeId = entreeObj.getInt("id");
	    				float rating = (float) entreeObj.getDouble("rating");
	    				Entree e = new Entree(rating, entreeId, entreeName);
	    				stationEntrees.add(e);
	    			}
	    			
	    			Station station = new Station(json_diningHall, stationName, stationEntrees);
	    			dinnerStations.add(station);
	    		}
	    		
	    		
	    		menu = new Menu(json_diningHall, lunchStations, dinnerStations);
	    		return menu;
	    		
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				
			}
            return menu;


        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return menu;

    }

    public ArrayList<Review> getReviewsFromJson(int eid) {
    	String url = REVIEWS_BASE_URL + eid;
    	ArrayList<Review> reviews = new ArrayList<Review>();
    	
    	 // Making HTTP request
        try {
            // defaultHttpClient
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpGet httpPost = new HttpGet(url);

            HttpResponse httpResponse = httpClient.execute(httpPost);
            HttpEntity httpEntity = httpResponse.getEntity();
            String s =  EntityUtils.toString(httpEntity);
            JSONObject jObj = new JSONObject(s);
            JSONArray reviewsArray = jObj.getJSONArray("reviews");
            for (int i = 0; i < reviewsArray.length(); i++) {
            	JSONObject reviewObj = reviewsArray.getJSONObject(i);
            	String content = reviewObj.getString("content");
            	if (!content.equals("")) {
            		int id = reviewObj.getInt("food_id");
            		String title = reviewObj.getString("title");
            		float rating = (float) reviewObj.getDouble("rating");
            		Review review = new Review(id, title, content, rating);
            		reviews.add(review);
            	}
            	
            }


	    		
        } catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				
        }
	
    	return reviews;
    	
    }
    
}