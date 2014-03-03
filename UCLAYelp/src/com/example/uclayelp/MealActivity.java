package com.example.uclayelp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.ExpandableListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ExpandableListView;

public class MealActivity extends ExpandableListActivity {

	private String meal;
	private String diningHall;
	private ArrayList<Station> stationList;
	 
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
		meal = intent.getStringExtra(Constants.MEAL);
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
		Entree entree = entrees.get(childPosition);
		
		//launch new activity
		Intent i = new Intent(getApplicationContext(), EntreeDetailsActivity.class);
		i.putExtra(Constants.DINING_HALL, diningHall);
		i.putExtra(Constants.MEAL, meal);
		i.putExtra(Constants.ENTREE,  entree.getTitle());
		i.putExtra(Constants.RATING, entree.getRating());
		i.putExtra(Constants.EID, entree.getId());
		//i.putExtra(Constants.ENTREE, entree);
		
		startActivity(i);
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
}