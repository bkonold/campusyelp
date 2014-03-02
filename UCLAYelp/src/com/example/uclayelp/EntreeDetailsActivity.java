package com.example.uclayelp;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RatingBar.OnRatingBarChangeListener;
import android.widget.Toast;

public class EntreeDetailsActivity extends Activity implements OnClickListener {
	
	private static final int CAMERA_REQUEST_CODE = 1;
	private static final int CAMERA_PIC_REQUEST = 0;
	private static final int CAMERA_CAPTURE_IMAGE_REQUEST = 100;
	private static final int MEDIA_TYPE_IMAGE = 1;
	private static final String IMAGE_DIRECTORY_NAME = "Test Camera";
	
	private Uri fileUri; // file url to store images
	
	private String diningHall;
	private String meal;
	private String entree;
	private float buttonRating;
	private ImageView iv;
	private Bitmap bitmap;
	

	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.entree_details);
        
        setupActionBar();
        
        ImageButton imageButton = (ImageButton) findViewById(R.id.imageButton1);
        imageButton.setImageResource(R.drawable.breakfast);
        imageButton.setOnClickListener(this);        
        
        Button submitButton = (Button) findViewById(R.id.submitButton);
        submitButton.setOnClickListener(this);
        
        Button cameraButton = (Button) findViewById(R.id.camera_button);
        cameraButton.setOnClickListener(this);
        
        iv = (ImageView) findViewById(R.id.imageView1);
        
        
        RatingBar ratingBar = (RatingBar) findViewById(R.id.ratingBar1);
        ratingBar.setOnRatingBarChangeListener(new OnRatingBarChangeListener() {	
			@Override
			public void onRatingChanged(RatingBar ratingBar, float rating,
					boolean fromUser) {
				// TODO Auto-generated method stub
					
				buttonRating = rating;
			}
		});

        //iv.setImageDrawable(null);
        
        Intent i = getIntent();
        diningHall = i.getStringExtra(Constants.DINING_HALL);
        meal = i.getStringExtra(Constants.MEAL);
        entree = i.getStringExtra(Constants.ENTREE);
        setTitle(entree);
        
        cameraButton.setText("Take a picture of " + entree + "!");
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
	
	@Override
    public void onClick(View v){
		Intent intent;
		
		switch (v.getId()){
		case R.id.imageButton1:
			intent = new Intent(this, ImageSwipeActivity.class);
			intent.putExtra(Constants.ENTREE, entree);
			startActivity(intent);
			break;
		case R.id.camera_button:
			//intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
	    	/*intent = new Intent();
	    	intent.setType("image/*");
	    	intent.setAction(Intent.ACTION_GET_CONTENT);
	    	intent.addCategory(Intent.CATEGORY_OPENABLE);
			if (intent.resolveActivity(getPackageManager()) != null){
	    		startActivityForResult(intent, CAMERA_REQUEST_CODE); // 1 = request_image_capture
	    	}*/
			takePicture();
		case R.id.ratingBar1:
			// I don't think you have to do anything here because
			// rating bar has it's own onClickListener action
			// The SUBMIT button should just use the ratingBar member value
    	    break;
		case R.id.submitButton:
			// Insert networking portion HERE:
			// Use ratingBar member value to get the food rating
			// Use iv member to check/submit the image
			
			// Check if user uploaded an image somehow
			// ^^ Not entirely sure how to do this
			break;
		
		default:
			intent = new Intent(this, AddReviewActivity.class);
			intent.putExtra(Constants.ENTREE, entree);
			startActivity(intent);
			break;
		}
    }
	
	private void takePicture() {
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
		
		startActivityForResult(intent, CAMERA_PIC_REQUEST);
		
	}
	
	
	/**
	 * Receiving activity result method will be called after closing the camera
	 * */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    // if the result is capturing Image
	    if (requestCode == CAMERA_PIC_REQUEST) {
	        if (resultCode == RESULT_OK) {
	            // successfully captured the image
	            // display it in image view
	            //previewCapturedImage(data);
	        	if(data != null)
	            {
	                Bitmap photo = (Bitmap) data.getExtras().get("data");
	                photo = Bitmap.createScaledBitmap(photo, 80, 80, false);
	                iv.setImageBitmap(photo);
	            }
	        } else if (resultCode == RESULT_CANCELED) {
	            // user cancelled Image capture
	            Toast.makeText(getApplicationContext(),
	                    "User cancelled image capture", Toast.LENGTH_SHORT)
	                    .show();
	        } else {
	            // failed to capture image
	            Toast.makeText(getApplicationContext(),
	                    "Sorry! Failed to capture image", Toast.LENGTH_SHORT)
	                    .show();
	        }
	    }
	}
	/*@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    super.onActivityResult(requestCode, resultCode, data);
	    switch(requestCode){
	    case 0:
	        if(resultCode==RESULT_OK){
	           Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
	           iv.setImageBitmap(thumbnail);
	            }
	    }
	}*/
	
	/*
     * Display image from a path to ImageView
     */
    private void previewCapturedImage(Intent data) {
        try {

 
            iv.setVisibility(View.VISIBLE);
 
            // bimatp factory
            BitmapFactory.Options options = new BitmapFactory.Options();
 
            // downsizing image as it throws OutOfMemory Exception for larger
            // images
            options.inSampleSize = 8;
 
            Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
	           iv.setImageBitmap(thumbnail);
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Creating file uri to store image/video
     */
    public Uri getOutputMediaFileUri(int type) {
        return Uri.fromFile(getOutputMediaFile(type));
    }
     
    /*
     * returning image / video
     */
    private static File getOutputMediaFile(int type) {
     
        // External sdcard location
        File mediaStorageDir = new File(
                Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                IMAGE_DIRECTORY_NAME);
     
        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d(IMAGE_DIRECTORY_NAME, "Oops! Failed create "
                        + IMAGE_DIRECTORY_NAME + " directory");
                return null;
            }
        }
     
        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                Locale.getDefault()).format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator
                    + "IMG_" + timeStamp + ".jpg");
        } else {
            return null;
        }
     
        return mediaFile;
    }
}