package com.example.uclayelp;


import java.util.ArrayList;

import android.os.Parcel;
import android.os.Parcelable;

public class Menu implements Parcelable{
	private String diningHall;
	private ArrayList<Station> lunchMenu;
	private ArrayList<Station> dinnerMenu;
	
	public Menu(String diningHall) {
		this.diningHall = diningHall;
		lunchMenu = new ArrayList<Station>();
		dinnerMenu = new ArrayList<Station>();
	}
	
	public Menu(String diningHall, ArrayList<Station> lunchMenu, ArrayList<Station> dinnerMenu) {
		this.diningHall = diningHall;
		this.lunchMenu = lunchMenu;
		this.dinnerMenu = dinnerMenu;
	}
	
	public ArrayList<Station> getLunchMenu() {
		return lunchMenu;
	}
	
	public ArrayList<Station> getDinnerMenu() {
		return dinnerMenu;
	}
	
	//stuff for Parcel
	@Override
	public void writeToParcel(Parcel out, int flags) {
		out.writeString(diningHall);
		out.writeTypedList(lunchMenu);
		out.writeTypedList(dinnerMenu);
	}
	
	private Menu(Parcel in) {
		lunchMenu = new ArrayList<Station>();
		dinnerMenu = new ArrayList<Station>();
		diningHall = in.readString();
		in.readTypedList(lunchMenu, Station.CREATOR);
		in.readTypedList(dinnerMenu, Station.CREATOR);
		
	}
	
	@Override
	public int describeContents() {
        return 0;
    }

    public static final Parcelable.Creator<Menu> CREATOR = 
            new Parcelable.Creator<Menu>() {
        public Menu createFromParcel(Parcel in) {
            return new Menu(in);
        }

        public Menu[] newArray(int size) {
            return new Menu[size];
        }
    };

}
