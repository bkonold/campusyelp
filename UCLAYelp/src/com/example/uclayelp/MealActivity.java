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
	 
    private ExpandableListAdapter listAdapter;
    private ExpandableListView expListView;
    
    private List<String> listDataHeader;
    private HashMap<String, List<String>> listDataChild;
    
    private HashMap<String, List<Entree>> stationEntreeMap;

	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.meal_menu_layout);
		
		expListView = (ExpandableListView) findViewById(android.R.id.list);
		
		 //get the Intent and the meal, dining hall, and menu
		Intent intent = getIntent();
		diningHall = intent.getStringExtra(Constants.DINING_HALL);
		stationList = intent.getParcelableArrayListExtra(Constants.MENU);
		
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
		new GetReviewsTask().execute(29); // TODO: replace w/ eid
		return true;
	}
	
	private void populateList() {
		listDataHeader = new ArrayList<String>();
		listDataChild = new HashMap<String, List<String>>();
		stationEntreeMap = new HashMap<String, List<Entree>>();
		
		int size = 3;
		
		if (diningHall.equals(Constants.DE_NEVE)) {
			listDataHeader = Constants.DENEVE_KITCHENS;
		} else if (diningHall.equals(Constants.COVEL)) {
			listDataHeader = Constants.COVEL_KITCHENS;
		} else if (diningHall.equals(Constants.BRUIN_PLATE)) {
			listDataHeader = Constants.BP_KITCHENS;
			size+=1;
		} else {
			listDataHeader = Constants.FEAST_KITCHENS;
			size+=1;
		}
		
		for (int i = 0; i < size; i++) {
			ArrayList<String> menuList = new ArrayList<String>();
			ArrayList<Entree> entreeList = new ArrayList<Entree>();
			entreeList = stationList.get(i).getEntrees();
			for (int j = 0; j < entreeList.size(); j++) {
				menuList.add(entreeList.get(j).getTitle());
			}
			listDataChild.put(listDataHeader.get(i), menuList);
			stationEntreeMap.put(listDataHeader.get(i), entreeList);
		}
	}
	
	
	private class GetReviewsTask extends AsyncTask<Integer, Void, ArrayList<Review>> {
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
    	protected ArrayList<Review> doInBackground (Integer... params) {
    		Integer entree_id = params[0];
    		
    		JSONParser parser = new JSONParser();
    		return parser.getReviewsFromJson(entree_id);
    	}
    	
    	@Override
    	protected void onPostExecute(ArrayList<Review> result) {
    		Intent i = new Intent(getApplicationContext(), EntreeDetailsActivity.class);
    		i.putExtra(Constants.ENTREE,  selectedEntree.getTitle());
    		i.putExtra(Constants.RATING, selectedEntree.getRating());
    		i.putExtra(Constants.EID, selectedEntree.getId());
    		i.putExtra(Constants.REVIEWS, result);
    		
    		startActivity(i);
    		myProgressDialog.dismiss();
    	}
    }
}