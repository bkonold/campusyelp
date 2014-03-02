package com.example.uclayelp;

import java.util.ArrayList;
import java.util.Arrays;
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
	private String [] menu ;
	 
    private ExpandableListAdapter listAdapter;
    private ExpandableListView expListView;
    
    private List<String> listDataHeader;
    private HashMap<String, List<String>> listDataChild;

	public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.meal_menu_layout);
		
		expListView = (ExpandableListView) findViewById(android.R.id.list);
		
		 //get the Intent and the meal, dining hall, and menu
		Intent intent = getIntent();
		meal = intent.getStringExtra(Constants.MEAL);
		diningHall = intent.getStringExtra(Constants.DINING_HALL);
		menu = intent.getStringArrayExtra(Constants.MENU);

		listDataHeader = new ArrayList<String>();
		listDataChild = new HashMap<String, List<String>>();
		
		List<String> menuList = Arrays.asList(menu);
		
		listDataHeader.add("hi abby");
		listDataHeader.add("bye abby");
		listDataHeader.add("third");
		listDataChild.put(listDataHeader.get(0), menuList);
		listDataChild.put(listDataHeader.get(1), menuList);
		listDataChild.put(listDataHeader.get(2), menuList);
		 
		listAdapter = new ExpandableListAdapter(this, listDataHeader, listDataChild);		
		expListView.setAdapter(listAdapter);
		int count = listAdapter.getGroupCount();
		for (int position = 1; position <= count; position++)
			expListView.expandGroup(position-1);
		 
		expListView.setOnChildClickListener(this);
	}
	
	public boolean onChildClick(ExpandableListView parent, View view, int groupPosition, int childPosition, long id) {
		//selected item
		List<String> entrees = listDataChild.get(listDataHeader.get(groupPosition));
		String entree = entrees.get(childPosition);
		
		//launch new activity
		Intent i = new Intent(getApplicationContext(), EntreeDetailsActivity.class);
		i.putExtra(Constants.DINING_HALL, diningHall);
		i.putExtra(Constants.MEAL, meal);
		i.putExtra(Constants.ENTREE, entree);
		
		startActivity(i);
		return true;
	}
}