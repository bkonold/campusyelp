package com.example.uclayelp;

import java.util.ArrayList;

import android.os.Parcel;
import android.os.Parcelable;

public class Station implements Parcelable{
	private String diningHall;
	private String stationName;
	private ArrayList<Entree> entrees;
	
	public Station(String diningHall, String stationName, ArrayList<Entree> entrees) {
		this.diningHall = diningHall;
		this.stationName = stationName;
		this.entrees = entrees;
	}
	
	public void addEntry (Entree e) {
		if (entrees == null) {
			entrees = new ArrayList<Entree>();
			entrees.add(e);
		}
	}
	
	public String getDiningHall() {
		return diningHall;
	}
	
	public String getStation() {
		return stationName;
	}
	
	public ArrayList<Entree> getEntrees() {
		return entrees;
	}
	
	// stuff for parcel
	@Override
	public void writeToParcel(Parcel out, int flags) {
		out.writeString(diningHall);
		out.writeString(stationName);
		out.writeTypedList(entrees);
	}
	
	private Station(Parcel in) {
		entrees = new ArrayList<Entree> ();
		diningHall = in.readString();
		stationName = in.readString();
		in.readTypedList(entrees, Entree.CREATOR);
	}
	
	@Override
	public int describeContents() {
		return 0;
	}
	
	public static final Parcelable.Creator<Station> CREATOR =
			new Parcelable.Creator<Station>() {
		public Station createFromParcel(Parcel in) {
			return new Station(in);
		}
		
		public Station[] newArray(int size) {
			return new Station[size];
		}
	};

}
