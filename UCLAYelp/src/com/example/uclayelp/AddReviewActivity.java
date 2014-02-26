package com.example.uclayelp;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.NavUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class AddReviewActivity extends Activity implements OnClickListener {
	public static final int CAMERA_REQUEST_CODE = 1;
	
	private ImageView iv;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.add_review);
        setupActionBar();
        
        iv = (ImageView) findViewById(R.id.imageView1);
        
        Intent i = getIntent();
        TextView text = (TextView) findViewById(R.id.text_field);
        String str = i.getStringExtra(Constants.ENTREE);
        text.setText(str);
        
        Button cameraButton = (Button) findViewById(R.id.camera_button);
        cameraButton.setOnClickListener(this);
        
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
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.display_menus, menu);
		return true;
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
	
	@Override
    public void onClick(View v){
    	Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
    	if (intent.resolveActivity(getPackageManager()) != null){
    		startActivityForResult(intent, CAMERA_REQUEST_CODE); // 1 = request_image_capture
    	}
    }
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK) {
	        Bundle extras = data.getExtras();
	        Bitmap imageBitmap = (Bitmap) extras.get("data");
	        iv.setImageBitmap(imageBitmap);
	    }
	}

}
