package com.example.uclayelp;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.NavUtils;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

public class EntreeDetailsActivity extends Activity implements OnClickListener {
	
	private static final int CAMERA_REQUEST_CODE = 1;
	
	private String entree;
	private int eid;
	private int max_images = 0;
    private float buttonRating;
	private RatingBar ratingBar;
	private ArrayList<Review> reviews;
	
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.entree_details);
        
        setupActionBar();
        
        Intent i = getIntent();
        entree = i.getStringExtra(Constants.ENTREE);
        eid = i.getIntExtra(Constants.EID, 1);
        
        // set title of Activity
        setTitle(entree);
       
        ImageButton imageButton = (ImageButton) findViewById(R.id.image_swipe_display);
        imageButton.setImageResource(R.drawable.loading);      

        // Button to add a photo
        Button cameraButton = (Button) findViewById(R.id.camera_button);
        cameraButton.setOnClickListener(this);
        
        // Button to add a review
        Button addReviewButton = (Button) findViewById(R.id.review_button);
        addReviewButton.setOnClickListener(this);
        
        // Rating bar
        ratingBar = (RatingBar) findViewById(R.id.ratingBar1);
        buttonRating = i.getFloatExtra(Constants.RATING, 0);
        ratingBar.setRating(buttonRating); 
        ratingBar.setOnTouchListener(new OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        ratingBar.setFocusable(false);
        
        TextView averageText = (TextView) findViewById(R.id.average_rating);
        averageText.setText(String.format("%.02f", buttonRating) + " overall");
        
        // List of reviews
        reviews = new ArrayList<Review>();
        reviews = i.getParcelableArrayListExtra(Constants.REVIEWS);
        setListView(reviews);
        
        
        //Start getting all the user-submitted photos
        GetAllPhotosTask fetchPhotos = new GetAllPhotosTask();
        fetchPhotos.execute(eid);
        
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

			Intent i = getIntent();
			i.putExtra("rating", buttonRating);
			setResult(RESULT_OK, i);
			finish();
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	
	}
	
	@Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
			Intent i = getIntent();
			i.putExtra("rating", buttonRating);
			setResult(RESULT_OK, i);
			finish();

            //Here put your code i.e start new activity but first finish current activity

            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
	
	@Override
    public void onClick(View v){
		Intent intent;
		
		switch (v.getId()){
		case R.id.image_swipe_display:
			intent = new Intent(this, ImageSwipeActivity.class);
			intent.putExtra(Constants.ENTREE, entree);
	        intent.putExtra("numImages", max_images);
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
		}
    }
	
	
	/** Called after add review button pressed. */
	private void getReviewPopupDialog() {
		LayoutInflater li = LayoutInflater.from(this);
		View v = li.inflate(R.layout.add_review, null);
		
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		
		builder.setView(v);
		builder.setTitle("Add your review");
		
		final EditText userInput = (EditText) v.findViewById(R.id.editText1);
		final RatingBar ratingBar = (RatingBar) v.findViewById(R.id.ratingBar1);
		
		builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				//TODO: delete title from json string later
			    String content = userInput.getText().toString();
			    Float rating = ratingBar.getRating();
			    String json = "{\"content\":\"" + content + "\", \"rating\":" + rating.toString() + "}";
			    new PostReviewTask().execute(json);
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
		 Bitmap imageBitmap = decodeSampledBitmapFromFile(file.getAbsolutePath(), 500, 250);
		 
		 //TODO: post to server
		 // POST TO SERVER
		 ByteArrayOutputStream baos = new ByteArrayOutputStream();  
		 imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos); //bm is the bitmap object   
		 byte[] byteArrayImage = baos.toByteArray(); 
		 
		 String encodedImage = Base64.encodeToString(byteArrayImage, Base64.DEFAULT);
		 
		 max_images++;
		 String fileName = "image" + max_images + ".jpg";
		 File finalFile = new File(Environment.getExternalStorageDirectory()+File.separator + fileName);
		 file.renameTo(finalFile);
		 
 		if (max_images == 1) {
			ImageButton display = (ImageButton) findViewById(R.id.image_swipe_display);
			display.setImageBitmap(imageBitmap);
    		display.setOnClickListener(EntreeDetailsActivity.this);
		}
		 
		 new PostImageTask().execute(encodedImage);
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
	
	/** Populate list of reviews .*/
	
	private void setListView(ArrayList<Review> reviews) {
		List<String> reviewContent = new ArrayList<String>();
		List<Float> reviewRatings = new ArrayList<Float>();
		for (int i = 0; i < reviews.size(); i++) {
			reviewContent.add(i, reviews.get(i).getContent());
			reviewRatings.add(i, reviews.get(i).getRating());
		}
		
		CustomListAdapter listAdapter = new CustomListAdapter
				(EntreeDetailsActivity.this , R.layout.review_item_list , 
						reviewContent, reviewRatings);
		
		LinearLayout ll = (LinearLayout) findViewById(R.id.ll);
		
		ll.removeAllViews();
		
		for (int i = 0; i < listAdapter.getCount(); i++){
			View item = listAdapter.getView(i, null, null);
			ll.addView(item);
		}
	}
	
	private class CustomListAdapter extends ArrayAdapter {

	    private Context mContext;
	    private int id;
	    private List <String>items ;
	    private List <Float> ratings;

	    public CustomListAdapter(Context context, int textViewResourceId , 
	    		List<String> list, List<Float> ratings ) 
	    {
	        super(context, textViewResourceId, list);           
	        mContext = context;
	        id = textViewResourceId;
	        items = list ;
	        this.ratings = ratings;
	    }

	    @Override
	    public View getView(int position, View v, ViewGroup parent)
	    {
	        View mView = v ;
	        if(mView == null){
	            LayoutInflater vi = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	            mView = vi.inflate(id, null);
	        }

	        TextView text = (TextView) mView.findViewById(R.id.reviewListItem);
	        if(items.get(position) != null)
	        {
	        	text.setTextColor(Color.BLACK);
	            text.setTypeface(null, Typeface.ITALIC);
	            text.setText(items.get(position));
	        }
	        
	        RatingBar reviewScore = (RatingBar) mView.findViewById(R.id.reviewRatingDisplay);
	        if(ratings.get(position) != null)
	        {
	            reviewScore.setRating(ratings.get(position));
	        }
	        
	        return mView;
	    }
	}
	 
	private class PostReviewTask extends AsyncTask<String, Void, Integer> {
        // For loading message
    	ProgressDialog myProgressDialog;
    
    	@Override
        protected void onPreExecute()
        {// Before anything runs, show loading message
    		// Respond to back button (cancel loading)
            myProgressDialog= ProgressDialog.show(EntreeDetailsActivity.this, "Just a Second!", "Submitting review...", 
            		true, true,  new DialogInterface.OnCancelListener(){
                        @Override
                        public void onCancel(DialogInterface dialog) {
                        	myProgressDialog.dismiss();
                        }
            }
            );
        }; 
    	
    	@Override
    	protected Integer doInBackground (String... params) {
    		String url = Constants.REVIEWS_BASE_URL + eid;
            Integer success = 0; // set to 1 when post succeeds
            String json = params[0];
            Log.w("tag", json);
            DefaultHttpClient httpClient = new DefaultHttpClient();
            
            try {

                HttpPost httppost = new HttpPost(url);
                httppost.setHeader("Content-type", "application/json");

                StringEntity se = new StringEntity(json); 
                se.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
                httppost.setEntity(se); 

                HttpResponse response = httpClient.execute(httppost);
                String temp = EntityUtils.toString(response.getEntity());
                Log.w("tag", temp);

                new GetReviewsTask().execute(eid);
                success = 1;

            } catch (Exception e) {
            	Log.w("postreview", e.getMessage());
            }
            return success;
    	}
    	
    	@Override
    	protected void onPostExecute(Integer result) {
    		myProgressDialog.dismiss();
    		AlertDialog.Builder builder = new AlertDialog.Builder(EntreeDetailsActivity.this);
    		if (result == 1) { 			
    			builder.setMessage("Review submitted.");
    		} else {
    			builder.setMessage("There was an error submitting your review. Please try again.");
    		}
    	}
    }
	// End PostReviewTask
	
	private class PostImageTask extends AsyncTask<String, Void, Integer> {
        // For loading message
    	ProgressDialog myProgressDialog;
    
    	@Override
        protected void onPreExecute()
        {// Before anything runs, show loading message
    		// Respond to back button (cancel loading)
            myProgressDialog= ProgressDialog.show(EntreeDetailsActivity.this, "Just a Second!", "Sending image...", 
            		true, true,  new DialogInterface.OnCancelListener(){
                        @Override
                        public void onCancel(DialogInterface dialog) {
                        	myProgressDialog.dismiss();
                        }
            }
            );
        }; 
    	
        @Override
    	protected Integer doInBackground (String... params) {
    		String url = Constants.POST_IMG_URL + eid;
            Integer success = 0; // set to 1 when post succeeds
            String json = params[0];
            Log.w("tag", json);
            
            JSONObject obj = new JSONObject();
            DefaultHttpClient httpClient = new DefaultHttpClient();
            
            try {
            	obj.put("base64", json);
            	StringEntity se = new StringEntity(obj.toString());
            	se.setContentType("application/json");
                HttpPost httppost = new HttpPost(url);
                httppost.setEntity(se);
                /*
                HttpPost httppost = new HttpPost(url);
                httppost.setHeader("Content-type", "application/json");

                StringEntity se = new StringEntity(json); 
                se.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
                httppost.setEntity(se); 
*/
                HttpResponse response = httpClient.execute(httppost);
                String temp = EntityUtils.toString(response.getEntity());
                Log.w("tag", temp);
                success = 1;

            } catch (Exception e) {
            	Log.w("postreview", e.getMessage());
            }
            return success;
    	}

    	
    	@Override
    	protected void onPostExecute(Integer result) {
    		myProgressDialog.dismiss();
    		AlertDialog.Builder builder = new AlertDialog.Builder(EntreeDetailsActivity.this);
    		if (result == 1) { 			
    			builder.setMessage("Image submitted.");
    		} else {
    			builder.setMessage("There was an error submitting your image. Please try again.");
    		}
    	}
	}

	private class GetReviewsTask extends AsyncTask<Integer, Void, RatingAndReviews> {
    	
    	@Override
    	protected RatingAndReviews doInBackground (Integer... params) {
    		Integer entree_id = params[0];
    		
    		JSONParser parser = new JSONParser();
    		return parser.getRatingAndReviewsFromJson(entree_id);
    	}
    	
    	@Override
    	protected void onPostExecute(RatingAndReviews result) {
    		ratingBar.setRating(result.getRating());
    		reviews = result.getReviews();
    		setListView(reviews);
    		
    		TextView averageText = (TextView) findViewById(R.id.average_rating);
            averageText.setText(String.format("%.02f", result.getRating()) + " overall");
    	}
	} // end GetReviewsTask

	private class GetPhotosTask extends AsyncTask<Integer, Void, String> {
    	
		private int picture_id;
		
    	@Override
    	protected String doInBackground (Integer... params) {
    		Integer entree_id = params[0];
    		picture_id = params[1];
    		Log.w("pictureID", "" + picture_id);
    		
    		JSONParser parser = new JSONParser();
    		return parser.getImageStrFromJson(entree_id, picture_id);
    	}
    	
    	@Override
    	protected void onPostExecute(String result) {
    		// imageData = bitmap of imageData
    		byte[] imgBytes = Base64.decode(result, 0);
			Bitmap imageData = BitmapFactory.decodeByteArray(imgBytes, 0, imgBytes.length);
    		
    		// Save to sdCard
    		String fileName = "image" + picture_id + ".jpg";

    		try {
    			FileOutputStream fileOutputStream = 
    					new FileOutputStream(Environment.getExternalStorageDirectory()+File.separator + fileName);

    			BufferedOutputStream bos = new BufferedOutputStream(fileOutputStream);

    			imageData.compress(CompressFormat.JPEG, 100, bos);

    			bos.flush();
    			bos.close();

    		} catch (FileNotFoundException e) {
    			Log.w("TAG", "Error saving image file: " + e.getMessage());
    		} catch (IOException e) {
    			Log.w("TAG", "Error saving image file: " + e.getMessage());
    		}
    		
    		ImageButton display = (ImageButton) findViewById(R.id.image_swipe_display);
    		
    		if (picture_id == 1) 
    			display.setImageBitmap(imageData);

    		if (picture_id == max_images)
    			display.setOnClickListener(EntreeDetailsActivity.this);
    	}
	} // end GetPhotosTask
	
	private class GetAllPhotosTask extends AsyncTask<Integer, Void, Integer> {
    
		private int entree_id;
		
    	@Override
    	protected Integer doInBackground (Integer... params) {
    		 entree_id = params[0];

    		JSONParser parser = new JSONParser();
    		return parser.getMaxIdFromJson(Constants.POST_IMG_URL, entree_id);
    	}
    	
    	@Override
    	protected void onPostExecute(Integer result) {
    		max_images = result;
    		
    		if (result == 0) {
    	        ImageButton imageButton = (ImageButton) findViewById(R.id.image_swipe_display);
    	        imageButton.setImageResource(R.drawable.no_image_available);
    		}
    		
    		for (int i = 1; i < result+1; i++){
    			GetPhotosTask picture = new GetPhotosTask();
    			picture.execute(entree_id, i);
    		}	
    	}
	} // end GetPhotosTask
	

}
    




