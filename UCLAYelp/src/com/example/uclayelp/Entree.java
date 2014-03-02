package com.example.uclayelp;

import android.os.Parcel;
import android.os.Parcelable;

public class Entree implements Parcelable {
	
	private float rating;
	private int id;
	private String title;
	
	// constructor
	public Entree() {}
	
	public Entree(float rating, int id, String title) {
		this.rating = rating;
		this.id = id;
		this.title = title;
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
	
	public void setRating(float rating) {
		this.rating = rating; 
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	// stuff for parcel
	@Override
	public void writeToParcel(Parcel out, int flags) {
		out.writeFloat(rating);
		out.writeInt(id);
		out.writeString(title);
	}
	
	private Entree(Parcel in) {
		rating = in.readFloat();
		id = in.readInt();
		title = in.readString();
	}
	
	@Override
	public int describeContents() {
		return 0;
	}
	
	public static final Parcelable.Creator<Entree> CREATOR =
			new Parcelable.Creator<Entree>() {
				public Entree createFromParcel(Parcel in) {
					return new Entree(in);
				}
				
				public Entree[] newArray(int size) {
					return new Entree[size];
				}
			};

}
