package com.vakkendwarf;

public class Room {

	private String Name;
	private boolean[][] Seats;
	private int Rows;
	private int Columns;

	public Room(String name, int Rows, int Columns){

		this.Name = name;
		this.Seats = new boolean[Rows][Columns];
		this.Rows = Rows;
		this.Columns = Columns;

	}

	public String GetName(){
		return this.Name;
	}

	public boolean[][] GetSeats(){
		return this.Seats;
	}
	
	public int GetRows(){
		return this.Rows;
	}

	public int GetCols(){
		return this.Columns;
	}
	
}