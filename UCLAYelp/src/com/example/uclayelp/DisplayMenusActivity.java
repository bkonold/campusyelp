package com.example.uclayelp;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;

public class DisplayMenusActivity extends TabActivity {
	
	private TabHost tabHost;
	private Menu menu;
	
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_display_menus);
		// Show the Up button in the action bar.
		setupActionBar();
		
		// Set the title based on the button pressed
		Intent intent = getIntent();
		String diningHall = intent.getStringExtra(Constants.DINING_HALL);
		menu = intent.getParcelableExtra(Constants.JSON_OBJ_MENU);  
        
		setTitle("Food for " + diningHall);
			
		tabHost = getTabHost();
		// adding tabspec to tabhost
		tabHost.addTab(createTab(diningHall, Constants.LUNCH));
		tabHost.addTab(createTab(diningHall, Constants.DINNER));


	}

	/**
	 * Set up the {@link android.app.ActionBar}, if the API is available.
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	private void setupActionBar() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			getActionBar().setDisplayHomeAsUpEnabled(true);
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	
	}
	
	/**
	 * 
	 * @param meal_index
	 * @param diningHall
	 * @return
	 */
	private TabSpec createTab(String diningHall, String meal) {
		TabSpec tab = tabHost.newTabSpec(meal);
		tab.setIndicator(meal); //, getResources().getDrawable(R.drawable.icon_breakfast_tab));
		Intent newIntent = new Intent(this, MealActivity.class);
		
		tab.setIndicator(meal, this.getResources().getDrawable(android.R.drawable.star_on));
    	tab.setIndicator(getTabIndicator(tabHost.getContext(), meal)); // new function to inject our own tab layout
		
		
		newIntent.putExtra(Constants.DINING_HALL, diningHall);
		if (meal.equals(Constants.LUNCH))
				newIntent.putExtra(Constants.MENU,  menu.getLunchMenu());
		else
			newIntent.putExtra(Constants.MENU,  menu.getDinnerMenu());

		
		tab.setContent(newIntent);
		return tab;
	}
	
    private View getTabIndicator(Context context, String title) {
        View view = LayoutInflater.from(context).inflate(R.layout.tab_layout, null);
        TextView tv = (TextView) view.findViewById(R.id.textView);
        tv.setText(title);
        return view;
    }
   

	
	
}
