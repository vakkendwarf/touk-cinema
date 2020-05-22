package com.vakkendwarf;

import java.time.LocalDateTime;

public class Screening {

	private Movie Movie;
	private Room Room;
	private LocalDateTime Time;
	private boolean[][] Seats;

	public Screening(Movie movie, Room room, LocalDateTime time) {

		this.Movie = movie;
		this.Room = room;
		this.Time = time;
		this.Seats = room.GetSeats();

	}

	public Screening(Movie movie, Room room, LocalDateTime time, boolean[][] seats){

		this.Movie = movie;
		this.Room = room;
		this.Time = time;
		this.Seats = seats;

	}

	public LocalDateTime GetTime() {
		return this.Time;
	}

	public Movie GetMovie() {
		return this.Movie;
	}

	public Room GetRoom(){
		return this.Room;
	}

	// !!! WARNING !!! ONLY FOR DEBUGGING !!!
	public void SetSeats(boolean[][] seats){
		this.Seats = seats;
	}

	public boolean[][] GetSeats() {
		return this.Seats;
	}

	public void ShowSeats() { // This entire thing will be replaced with a proper frontend eventually.

		int rowno = 0;

		System.out.print("       [");
		for(int i = 0; i < Math.floor(this.GetRoom().GetCols() * 4 / 2 - 4); i++) {
			System.out.print("-");
		}
		System.out.print("SCREEN");
		for(int i = 0; i < Math.floor(this.GetRoom().GetCols() * 4 / 2 - 4); i++){
			System.out.print("-");
		}
		System.out.println("]");
		System.out.print("SEAT   [");
		for(int i = 1; i <= this.GetRoom().GetCols(); i++){
			if(i > 1){
				System.out.print("  ");
			}
			if(i < 10) {
				System.out.print("0");
			}
			System.out.print(i);
		}
		System.out.println("]");
		for(boolean[] row : Seats){
			
			rowno += 1;
			if(rowno < 10){
				System.out.print("ROW 0" + rowno + " ");
			} else {
			System.out.print("ROW " + rowno + " ");
			}

			for(boolean seat : row){
				if(seat){
					System.out.print("[XX]");
				} else {
					System.out.print("[  ]");
				}
			}

			System.out.println("");
		}

	}

	public void Reserve(int row, int col){
		this.Seats[row][col] = true;
		// TODO: Save to database?
	}
}