package com.example.uclayelp;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.NavUtils;
import android.util.Base64;
import android.util.Log;
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
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

public class EntreeDetailsActivity extends Activity implements OnClickListener {
	
	private static final int CAMERA_REQUEST_CODE = 1;
	
	private String entree;
	private int eid;

	private ListView lv;
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
       
        ImageButton imageButton = (ImageButton) findViewById(R.id.imageButton1);
        // TODO: get the right image?
        imageButton.setImageResource(R.drawable.breakfast);
        imageButton.setOnClickListener(this);        

        // Button to add a photo
        Button cameraButton = (Button) findViewById(R.id.camera_button);
        cameraButton.setOnClickListener(this);
        
        // Button to add a review
        Button addReviewButton = (Button) findViewById(R.id.review_button);
        addReviewButton.setOnClickListener(this);
        
        // Rating bar
        ratingBar = (RatingBar) findViewById(R.id.ratingBar1);
        float buttonRating = i.getFloatExtra(Constants.RATING, 0);
        ratingBar.setRating(buttonRating); 
        ratingBar.setOnTouchListener(new OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
        ratingBar.setFocusable(false);
        
        // List of reviews
        lv = (ListView) findViewById(R.id.listView1);  
        reviews = new ArrayList<Review>();
        reviews = i.getParcelableArrayListExtra(Constants.REVIEWS);
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
		// iv = (ImageView) findViewById(R.id.imageView1);
		 Bitmap imageBitmap = decodeSampledBitmapFromFile(file.getAbsolutePath(), 500, 250);
		 //iv.setImageBitmap(bitmap);
		 
		 //TODO: post to server
		 // POST TO SERVER
		 ByteArrayOutputStream baos = new ByteArrayOutputStream();  
		 imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos); //bm is the bitmap object   
		 byte[] byteArrayImage = baos.toByteArray(); 
		 
		 String encodedImage = Base64.encodeToString(byteArrayImage, Base64.DEFAULT);
		 String json = "{\"base64\":\"" + encodedImage + "\" }";
		 new PostImageTask().execute(json);
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
		for (int i = 0; i < reviews.size(); i++) {
			reviewContent.add(i, reviews.get(i).getContent());
		}
		
		CustomListAdapter listAdapter = new CustomListAdapter
				(EntreeDetailsActivity.this , R.layout.review_item_list , reviewContent);
		lv.setAdapter(listAdapter);
	}
	
	private class CustomListAdapter extends ArrayAdapter {

	    private Context mContext;
	    private int id;
	    private List <String>items ;

	    public CustomListAdapter(Context context, int textViewResourceId , List<String> list ) 
	    {
	        super(context, textViewResourceId, list);           
	        mContext = context;
	        id = textViewResourceId;
	        items = list ;
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
//	            text.setBackgroundColor(Color.BLACK);
	            text.setText(items.get(position));
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
    		
    	}
	} // end GetReviewsTask
}
    
