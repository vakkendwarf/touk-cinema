package com.vakkendwarf;

public class Movie {

	private String Name; // Some may call it the Title.
	private int Year;
	private int Runtime;
	private float Rating;

	public Movie(String name, int year, int runtime, float rating) {
		this.Name = name;
		this.Year = year;
		this.Runtime = runtime;
		this.Rating = rating;
	}

	public String GetName(){
		return this.Name;
	}

	public int GetYear(){
		return this.Year;
	}

	public int GetRuntime(){
		return this.Runtime;
	}

	public float GetRating(){
		return this.Rating;
	}
	
}