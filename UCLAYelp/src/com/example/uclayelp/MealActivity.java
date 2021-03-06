package com.example.uclayelp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


import android.app.ExpandableListActivity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ExpandableListView;

public class MealActivity extends ExpandableListActivity {

	private String diningHall;
	private ArrayList<Station> stationList;
	private Entree selectedEntree;
	
	private boolean lunch;
	 
    private ExpandableListAdapter listAdapter;
    private ExpandableListView expListView;
    
    private List<String> listDataHeader;
    private HashMap<String, List<Entree>> listDataChild;
    
    private HashMap<String, List<Entree>> stationEntreeMap;

	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.meal_menu_layout);
		
		expListView = (ExpandableListView) findViewById(android.R.id.list);
		
		 //get the Intent and the meal, dining hall, and menu
		Intent intent = getIntent();
		diningHall = intent.getStringExtra(Constants.DINING_HALL);
		stationList = intent.getParcelableArrayListExtra(Constants.MENU);
		lunch = intent.getBooleanExtra("isLunch",  true);
		
		populateList();
		 
		listAdapter = new ExpandableListAdapter(this, listDataHeader, listDataChild);		
		expListView.setAdapter(listAdapter);
		int count = listAdapter.getGroupCount();
		for (int position = 1; position <= count; position++)
			expListView.expandGroup(position-1);
		 
		expListView.setOnChildClickListener(this);
	}
	
	public boolean onChildClick(ExpandableListView parent, View view, int groupPosition, int childPosition, long id) {
		//selected item
		List<Entree> entrees = stationEntreeMap.get(listDataHeader.get(groupPosition));
		selectedEntree = entrees.get(childPosition);
		
		
		//launch new activity
		new GetReviewsTask().execute(selectedEntree.getId()); // TODO: replace w/ eid
		return true;
	}
	
	private void populateList() {
		listDataHeader = new ArrayList<String>();
		listDataChild = new HashMap<String, List<Entree>>();
		stationEntreeMap = new HashMap<String, List<Entree>>();

		int size = stationList.size();
		for (int i = 0; i < size; i++) {
			String s = stationList.get(i).getStation();
			listDataHeader.add(s);
			ArrayList<Entree> menuList = new ArrayList<Entree>();
			ArrayList<Entree> entreeList = new ArrayList<Entree>();
			entreeList = stationList.get(i).getEntrees();
			for (int j = 0; j < entreeList.size(); j++) {
				menuList.add(entreeList.get(j));
			}
			listDataChild.put(listDataHeader.get(i), menuList);
			stationEntreeMap.put(listDataHeader.get(i), entreeList);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		String jdh;
		if (diningHall.equals(Constants.DE_NEVE))
			jdh = Constants.JSON_DE_NEVE;
		else if (diningHall.equals(Constants.COVEL))
			jdh = Constants.JSON_COVEL;
		else if (diningHall.equals(Constants.BRUIN_PLATE))
			jdh = Constants.JSON_BRUIN_PLATE;
		else 
			jdh = Constants.JSON_FEAST;
		

		
		new GetMenuTask().execute("http://54.186.3.129/app/menu", jdh);
		
	}
	
	
	private class GetReviewsTask extends AsyncTask<Integer, Void, RatingAndReviews> {
        // For loading message
    	ProgressDialog myProgressDialog;
    
    	@Override
        protected void onPreExecute()
        {
    		// Before anything runs, show loading message
    		// Respond to back button (cancel loading)
    		
            myProgressDialog= ProgressDialog.show(MealActivity.this, "Just a Second!", "Loading " + selectedEntree.getTitle(), 
            		true, true,  new DialogInterface.OnCancelListener(){
                        @Override
                        public void onCancel(DialogInterface dialog) {
                        	myProgressDialog.dismiss();
                        }
            }
            );
        }; 
    	
    	@Override
    	protected RatingAndReviews doInBackground (Integer... params) {
    		Integer entree_id = params[0];
    		
    		JSONParser parser = new JSONParser();
    		return parser.getRatingAndReviewsFromJson(entree_id);
    	}
    	
    	@Override
    	protected void onPostExecute(RatingAndReviews result) {
    		Intent i = new Intent(getApplicationContext(), EntreeDetailsActivity.class);
    		i.putExtra(Constants.ENTREE,  selectedEntree.getTitle());
    		i.putExtra(Constants.RATING, result.getRating());
    		i.putExtra(Constants.EID, selectedEntree.getId());
    		i.putExtra(Constants.REVIEWS, result.getReviews());
    		
    		startActivityForResult(i,1);
    		myProgressDialog.dismiss();
    	}
    }
	
	private class GetMenuTask extends AsyncTask<String, Void, Menu> {
        // For loading message
    	ProgressDialog myProgressDialog;
    
    	@Override
        protected void onPreExecute()
        {
    		// Before anything runs, show loading message
    		// Respond to back button (cancel loading)
            myProgressDialog= ProgressDialog.show(MealActivity.this, "Just a Second!", "Loading Menus...", 
            		true, true,  new DialogInterface.OnCancelListener(){
                        @Override
                        public void onCancel(DialogInterface dialog) {
                        	myProgressDialog.dismiss();
                        }
            }
            );
        }; 
    	
    	@Override
    	protected Menu doInBackground (String... params) {
    		String url = params[0];
    	    String diningHall = params[1];
    		
    		JSONParser parser = new JSONParser();
    		return parser.getMenuFromJson(url,  diningHall);
    	}
    	
    	@Override
    	protected void onPostExecute(Menu result) {
    		//displayMenus(result, diningHall);
    		if (lunch) stationList = result.getLunchMenu();
    		else
    			stationList = result.getDinnerMenu();
    		populateList();
    		listAdapter = new ExpandableListAdapter(MealActivity.this, listDataHeader, listDataChild);		
    		expListView.setAdapter(listAdapter);
    		int count = listAdapter.getGroupCount();
    		for (int position = 1; position <= count; position++)
    			expListView.expandGroup(position-1);
    		 
    		expListView.setOnChildClickListener(MealActivity.this);
    		myProgressDialog.dismiss();
    	}
    }
}