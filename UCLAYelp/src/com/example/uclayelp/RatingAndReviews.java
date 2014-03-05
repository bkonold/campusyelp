package com.example.uclayelp;

import java.util.ArrayList;

import android.os.Parcel;
import android.os.Parcelable;

public class RatingAndReviews implements Parcelable{
	private float rating;
	private ArrayList<Review> reviews;
	
	public RatingAndReviews(float rating, ArrayList<Review> reviews) {
		this.rating = rating;
		this.reviews = reviews;
	}
	
	public float getRating() {
		return rating;
	}
	
	public ArrayList<Review> getReviews() {
		return reviews;
	}
	
	// stuff for parcel
	@Override
	public void writeToParcel(Parcel out, int flags) {
		out.writeFloat(rating);
		out.writeTypedList(reviews);
	}
	
	private RatingAndReviews(Parcel in) {
		reviews = new ArrayList<Review> ();
		rating = (float) in.readDouble();
		in.readTypedList(reviews, Review.CREATOR);
	}
	
	@Override
	public int describeContents() {
		return 0;
	}
	
	public static final Parcelable.Creator<RatingAndReviews> CREATOR =
			new Parcelable.Creator<RatingAndReviews>() {
		public RatingAndReviews createFromParcel(Parcel in) {
			return new RatingAndReviews(in);
		}
		
		public RatingAndReviews[] newArray(int size) {
			return new RatingAndReviews[size];
		}
	};

}
