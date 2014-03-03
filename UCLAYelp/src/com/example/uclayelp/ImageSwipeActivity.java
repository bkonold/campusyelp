package com.example.uclayelp;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class ImageSwipeActivity extends Activity {
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.image_swipe);
        
        setupActionBar();
        
        ViewPager viewPager = (ViewPager) findViewById(R.id.view_pager);
        ImageSwiper adapter = new ImageSwiper();
        viewPager.setAdapter(adapter);
        
        Intent i = getIntent();
        String entree = i.getStringExtra(Constants.ENTREE);
 
        setTitle("Photos of " + entree);
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
	
	
	/**
	 * private class for image swiper stuff
	 */
	
	private class ImageSwiper extends PagerAdapter {
		private int[] images = new int [] {
				R.drawable.breakfast,
				R.drawable.lunch,
				R.drawable.dinner
		};
		
		@Override
		public int getCount() {
			return images.length;
		}
		
		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view == ((ImageView) object) ;
		}
		
		@Override
		public Object instantiateItem(ViewGroup container, int pos) {
			Context context = ImageSwipeActivity.this;
			ImageView imageView = new ImageView(context);
			int padding = context.getResources().getDimensionPixelSize(R.dimen.padding_medium); // TODO: don't use fixed constant here
			imageView.setPadding(padding, padding, padding, padding);
			imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
			imageView.setImageResource(images[pos]);
			((ViewPager) container).addView(imageView, 0);
			return imageView;
		}
		
		@Override
		public void destroyItem(ViewGroup container, int pos, Object object) {
			((ViewPager) container).removeView((ImageView) object);
		}
	} // end image swiper class def

}
