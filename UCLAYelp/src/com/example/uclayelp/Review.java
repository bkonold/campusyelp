package com.example.uclayelp;

import android.os.Parcel;
import android.os.Parcelable;

public class Review implements Parcelable {
	
	private int id;
	private String title;
	private String content;
	private float rating; 
	
	// constructor
	public Review() {}
	
	public Review(int id, String title, String content, float rating) {
		this.id = id;
		this.title = title;
		this.content = content;
		this.rating = rating;
	}
	
	public float getRating() {
		return rating;
	}
	
	public int getId() {
		return id;
	}
	
	public String getTitle() {
		return title;
	}
	
	public String getContent() {
		return content;
	}

	// stuff for parcel
	@Override
	public void writeToParcel(Parcel out, int flags) {
		out.writeInt(id);
		out.writeString(title);
		out.writeString(content);
		out.writeFloat(rating);
	}
	
	private Review(Parcel in) {
		id = in.readInt();
		title = in.readString();
		content = in.readString();
		rating = in.readFloat();
	}
	
	@Override
	public int describeContents() {
		return 0;
	}
	
	public static final Parcelable.Creator<Review> CREATOR =
			new Parcelable.Creator<Review>() {
				public Review createFromParcel(Parcel in) {
					return new Review(in);
				}
				
				public Review[] newArray(int size) {
					return new Review[size];
				}
			};

}
