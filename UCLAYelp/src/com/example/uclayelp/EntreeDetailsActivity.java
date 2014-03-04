package com.example.uclayelp;

import java.io.File;
import java.util.ArrayList;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.NavUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.RatingBar.OnRatingBarChangeListener;

public class EntreeDetailsActivity extends Activity implements OnClickListener {
	
	private static final int CAMERA_REQUEST_CODE = 1;
	
	private String diningHall;
	private String meal;
	
	private String entree;
	private int eid;
	private float buttonRating;

	private ImageView iv;
	private ListView lv;
	

	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.entree_details);
        
        setupActionBar();
        
        ImageButton imageButton = (ImageButton) findViewById(R.id.imageButton1);
        imageButton.setImageResource(R.drawable.breakfast);
        imageButton.setOnClickListener(this);        

        
        Button cameraButton = (Button) findViewById(R.id.camera_button);
        cameraButton.setOnClickListener(this);
        
        Button addReviewButton = (Button) findViewById(R.id.review_button);
        addReviewButton.setOnClickListener(this);
        
        iv = (ImageView) findViewById(R.id.imageView1);
        
        lv = (ListView) findViewById(R.id.listView1);
        
        
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
        eid = i.getIntExtra(Constants.EID, 1);
        buttonRating = i.getFloatExtra(Constants.RATING, 0);
        ArrayList<Review> reviews = new ArrayList<Review>();
        reviews = i.getParcelableArrayListExtra(Constants.REVIEWS);
        
        
        setTitle(entree);
        setListView(reviews);
        
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
			 intent = new Intent("android.media.action.IMAGE_CAPTURE");
			 File file = new File(Environment.getExternalStorageDirectory()+File.separator + "image.jpg");
			 intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
			 startActivityForResult(intent, CAMERA_REQUEST_CODE);
			 break;
		case R.id.review_button:
			getReviewPopupDialog();
		case R.id.ratingBar1:
			// I don't think you have to do anything here because
			// rating bar has it's own onClickListener action
			// The SUBMIT button should just use the ratingBar member value
    	    break;

		
		default:
			intent = new Intent(this, AddReviewActivity.class);
			intent.putExtra(Constants.ENTREE, entree);
			startActivity(intent);
			break;
		}
    }
	
	
	/** Called after add review button pressed. */
	private void getReviewPopupDialog() {
		/*AlertDialog.Builder alert = new AlertDialog.Builder(this);

		alert.setTitle("Add your review");

		// Set an EditText view to get user input 
		final EditText input = new EditText(this);
		alert.setView(input);

		alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
		public void onClick(DialogInterface dialog, int whichButton) {
		  String value = input.toString();
		  // Do something with value!
		  }
		});

		alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
		  public void onClick(DialogInterface dialog, int whichButton) {
		    // Canceled
		  }
		});

		alert.show();*/
		LayoutInflater li = LayoutInflater.from(this);
		View v = li.inflate(R.layout.add_review, null);
		
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		
		builder.setView(v);
		builder.setTitle("Add your review");
		
		final EditText userInput = (EditText) v.findViewById(R.id.editText1);
		
		builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
			  String value = userInput.toString();
			  // Do something with value!
			  }
			});

			builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
			  public void onClick(DialogInterface dialog, int whichButton) {
			    // Canceled
			  }
			});

			builder.show();
	}
	
	/**
	 * Receiving activity result method will be called after closing the camera
	 * */
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		 File file = new File(Environment.getExternalStorageDirectory()+File.separator + "image.jpg");
		// iv = (ImageView) findViewById(R.id.imageView1);
		 Bitmap bitmap = decodeSampledBitmapFromFile(file.getAbsolutePath(), 500, 250);
		 //iv.setImageBitmap(bitmap);
	}
	
	public static Bitmap decodeSampledBitmapFromFile(String path,
		        int reqWidth, int reqHeight) { // BEST QUALITY MATCH

        // First decode with inJustDecodeBounds=true to check dimensions
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(path, options);

		// Calculate inSampleSize
		// Raw height and width of image
		final int height = options.outHeight;
		final int width = options.outWidth;
		options.inPreferredConfig = Bitmap.Config.RGB_565;
		int inSampleSize = 1;

	if (height > reqHeight) {
	    inSampleSize = Math.round((float)height / (float)reqHeight);
	}

	        int expectedWidth = width / inSampleSize;

	        if (expectedWidth > reqWidth) {
	            //if(Math.round((float)width / (float)reqWidth) > inSampleSize) // If bigger SampSize..
	            inSampleSize = Math.round((float)width / (float)reqWidth);
	        }


	    options.inSampleSize = inSampleSize;

	    // Decode bitmap with inSampleSize set
	    options.inJustDecodeBounds = false;

	    return BitmapFactory.decodeFile(path, options);
	  }
	
	private void setListView(ArrayList<Review> reviews) {
		String [] reviewContent = new String [reviews.size()];
		for (int i = 0; i < reviews.size(); i++) {
			reviewContent[i] = reviews.get(i).getContent();
		}
		
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, reviewContent);
		lv.setAdapter(adapter);
	}

	 
    
}