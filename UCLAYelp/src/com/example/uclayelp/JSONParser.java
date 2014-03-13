package com.example.uclayelp;

import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import android.util.Log;

public class JSONParser {


    // constructor
    public JSONParser() {}
    
    public int getMaxIdFromJson(String url, int eid) {
    	int maxId = 0;
    	// Making HTTP request
    	try {
    		//defaultHttpClient
    		DefaultHttpClient httpClient = new DefaultHttpClient();
    		HttpGet httpGet = new HttpGet(url+eid);
    		
    		HttpResponse httpResponse = httpClient.execute(httpGet);
    		HttpEntity httpEntity = httpResponse.getEntity();
    		String s = EntityUtils.toString(httpEntity);
    		Log.w("max_id", "" + maxId);
    		
    		JSONObject jObj = new JSONObject (s);
    		maxId = jObj.getInt("max_id");
    		Log.w("max_id_e", "" + maxId);
    		
    	} catch (Exception e) {
    		Log.w("max_id_e", "" + maxId);
    		e.printStackTrace();
    	}
    	
    	
    	return maxId;
    }
    
    public String getImageStrFromJson(int eid, int imgId) {
    	String str = "";
    	String url = Constants.GET_IMG_URL + eid + "/" + imgId;
    	try {
    		//defaultHttpClient
    		DefaultHttpClient httpClient = new DefaultHttpClient();
    		HttpGet httpGet = new HttpGet(url);
    		
    		HttpResponse httpResponse = httpClient.execute(httpGet);
    		HttpEntity httpEntity = httpResponse.getEntity();
    		String s = EntityUtils.toString(httpEntity);
    		Log.w("httpEntity", s);
    		
    		JSONObject jObj = new JSONObject (s);
    		str = jObj.getString("base64");
    		Log.w("dlImage", str);
    		
    	} catch (Exception e) {
    		Log.w("ImageError", e.getMessage());
    		e.printStackTrace();
    	}
    	
    	return str;
    	
    }
    
   
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
            
			JSONObject jObj = new JSONObject(s);
	   		JSONObject menuObj = jObj.getJSONObject(json_diningHall);
			
			JSONArray lunchArray = menuObj.getJSONArray("lunch");
			JSONArray dinnerArray = menuObj.getJSONArray("dinner");
			
			ArrayList<Station> lunchStations = new ArrayList<Station>();
			ArrayList<Station> dinnerStations = new ArrayList<Station>();
			
			// lunch: get stations, and entrees for each station
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
			
	    		
        } catch (Exception e) {
        	e.printStackTrace();
        }
        
        return menu;
    }

    public RatingAndReviews getRatingAndReviewsFromJson(int eid) {
    	String url = Constants.REVIEWS_BASE_URL + eid;
    	float averageRating;
    	ArrayList<Review> reviews = new ArrayList<Review>();
    	RatingAndReviews r = null;
    	
    	 // Making HTTP request
        try {
            // defaultHttpClient
            DefaultHttpClient httpClient = new DefaultHttpClient();
            HttpGet httpPost = new HttpGet(url);

            HttpResponse httpResponse = httpClient.execute(httpPost);
            HttpEntity httpEntity = httpResponse.getEntity();
            String s =  EntityUtils.toString(httpEntity);
            JSONObject jObj = new JSONObject(s);
            averageRating = (float) jObj.getDouble("rating");
            JSONArray reviewsArray = jObj.getJSONArray("reviews");
            for (int i = 0; i < reviewsArray.length(); i++) {
            	JSONObject reviewObj = reviewsArray.getJSONObject(i);
            	String content = reviewObj.getString("content");
            	if (!content.equals("")) {
            		float reviewRating = (float) reviewObj.getDouble("rating");
            		Review review = new Review(content, reviewRating);
            		reviews.add(review);
            	}
            	
            }

            r = new RatingAndReviews(averageRating, reviews);

	    		
        } catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				
        }
	
    	return r;
    	
    }
    
}