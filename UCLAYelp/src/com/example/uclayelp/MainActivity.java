package com.example.uclayelp;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;


public class MainActivity extends Activity implements OnClickListener {
	
	private static String menuUrl = "http://54.186.3.129/app/menu";
	private String diningHall;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        
        Button deNeveButton = (Button) findViewById(R.id.button1);
        Button covelButton = (Button) findViewById(R.id.button2);
        Button bruinButton = (Button) findViewById(R.id.button3);
        Button feastButton = (Button) findViewById(R.id.button4);
        deNeveButton.setOnClickListener(this);
        covelButton.setOnClickListener(this);
        bruinButton.setOnClickListener(this);
        feastButton.setOnClickListener(this);
    }

    // Use this to set the size of the buttons to the longest one (Feast at Reiber)
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        // Call here getWidth() and getHeight()
        List<Button> allButtons = new ArrayList<Button>();
        allButtons.add( (Button) findViewById(R.id.button1));
        allButtons.add( (Button) findViewById(R.id.button2));
        allButtons.add( (Button) findViewById(R.id.button3));
        allButtons.add( (Button) findViewById(R.id.button4));
        
        int maxWidth = 0;
        for (Button button : allButtons){
        	int curButtonWidth = button.getWidth();
        	if (curButtonWidth > maxWidth)
        		maxWidth = curButtonWidth;
        }
        for (Button button : allButtons){
        	button.setWidth(maxWidth);
        }

     }
    
    @Override
    public void onClick(View v){
    	// Check network connection
    	ConnectivityManager connMgr = (ConnectivityManager) 
    	        getSystemService(Context.CONNECTIVITY_SERVICE);
    	NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
    	if (networkInfo != null && networkInfo.isConnected()) {
    		getMenu(v.getId());
    	} else {
    	    AlertDialog.Builder builder = new AlertDialog.Builder(this);
    	    builder.setMessage(Constants.NETWORK_ERR_MSG)
    	           .setPositiveButton("OK", new DialogInterface.OnClickListener() {
    	        	   public void onClick(DialogInterface dialog,int id) {
   					       // if this button is clicked, just close
   						   // the dialog box and do nothing
   						   dialog.cancel();
   					   }
		    });
    	    
    	    AlertDialog alertDialog = builder.create();
    	    alertDialog.show();
    	}
    }
    
    /** Called when the user clicks one of the dining hall buttons. */
    private void getMenu(int buttonId) {
    	String json_diningHall = "";
    	switch(buttonId) {
	        case R.id.button1:
	        	diningHall = Constants.DE_NEVE;
	        	json_diningHall = Constants.JSON_DE_NEVE;
	    	    break;
	        case R.id.button2:
	        	diningHall = Constants.COVEL;
	        	json_diningHall = Constants.JSON_COVEL;
	    	    break;
	        case R.id.button3:
	        	diningHall = Constants.BRUIN_PLATE;
	        	json_diningHall = Constants.JSON_BRUIN_PLATE;
	    	    break;
	        case R.id.button4:
	        	diningHall = Constants.FEAST;
	        	json_diningHall = Constants.JSON_FEAST;
	    	    break;
    	}
    	
    	new GetMenuTask().execute(menuUrl, json_diningHall);
    }
    
    /** Called when the async task completes */
    public void displayMenus(Menu menu, String diningHall) {

    	// Show menus
        Intent intent = new Intent(this, DisplayMenusActivity.class);
        intent.putExtra(Constants.DINING_HALL, diningHall);
        intent.putExtra(Constants.JSON_OBJ_MENU, menu);
        try { 
	        if (menu.getLunchMenu() != null)
	        	startActivity(intent);
        } catch (NullPointerException e) {
        	 AlertDialog.Builder builder = new AlertDialog.Builder(this);
     	    builder.setMessage("Can't establish a connection to the server.  Please try again!")
     	           .setPositiveButton("OK", new DialogInterface.OnClickListener() {
     	        	   public void onClick(DialogInterface dialog,int id) {
    					       // if this button is clicked, just close
    						   // the dialog box and do nothing
    						   dialog.cancel();
    					   }
 		    });
     	    
     	    AlertDialog alertDialog = builder.create();
     	    alertDialog.show();
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
            myProgressDialog= ProgressDialog.show(MainActivity.this, "Just a Second!", "Loading Menus...", 
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
    		displayMenus(result, diningHall);
    		myProgressDialog.dismiss();
    	}
    }
    
    

}
