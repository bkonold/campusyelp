package com.example.uclayelp;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.TabActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainActivity extends Activity implements OnClickListener {
	
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

    
    @Override
    public void onClick(View v){
    	// Check network connection
    	ConnectivityManager connMgr = (ConnectivityManager) 
    	        getSystemService(Context.CONNECTIVITY_SERVICE);
    	NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
    	if (networkInfo != null && networkInfo.isConnected()) {
    		displayMenus(v.getId());
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
    public void displayMenus(int buttonId) {
 
    	// Show menus
        Intent intent = new Intent(this, DisplayMenusActivity.class);
        
        switch(buttonId) {
            case R.id.button1:
            	intent.putExtra(Constants.DINING_HALL, Constants.DE_NEVE);
        	    break;
            case R.id.button2:
            	intent.putExtra(Constants.DINING_HALL, Constants.COVEL);
        	    break;
            case R.id.button3:
            	intent.putExtra(Constants.DINING_HALL, Constants.BRUIN_PLATE);
        	    break;
            case R.id.button4:
            	intent.putExtra(Constants.DINING_HALL, Constants.FEAST);
        	    break;
        }
        
        startActivity(intent);
    }
    

    
}
