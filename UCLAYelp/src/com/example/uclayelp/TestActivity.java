package com.example.uclayelp;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class TestActivity extends Activity {
	TextView tv;
	Menu menu;
	ArrayList<Station> lunchMenu;
	ArrayList<Station> dinnerMenu;
	
	 @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test);
        
        tv = (TextView) findViewById(R.id.textView1);
        
     // Set the title based on the button pressed
 		Intent intent = getIntent();
 		menu = intent.getParcelableExtra(Constants.JSON_OBJ_MENU); 
 		lunchMenu = menu.getLunchMenu();
 		dinnerMenu = menu.getDinnerMenu();
 		if (lunchMenu!= null && dinnerMenu!=null) {
 			String blah = "";
 			for (int i = 0; i < lunchMenu.size(); i++) {
 				blah += lunchMenu.get(i).getStation();
 				blah+= ", i, ";
 			}
 			tv.setText(blah);
 		} else
 			tv.setText(menu.toString());
	 }

}
