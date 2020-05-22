package com.vakkendwarf;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.Calendar;

public class Database {
	
	public static Connection connect(){

		Connection con = null;

		try {

			File dbfile = new File("");
			String url = "jdbc:sqlite:"+ dbfile.getAbsolutePath() + "/touk_cinema.db";
			Debug.Print(url);
			con = DriverManager.getConnection(url);

			Debug.Print("Database connected.");

		} catch (SQLException e) {

			if(Debug.DebugMode) {
				Debug.Print(e.getMessage());
			} else {
				System.out.println("Database connection error.");
			}

		}

		return con;

	}

	public static Room[] LoadRooms(){

		Connection con = connect();

		int roomamt = 0;

		try {
			Statement sta = con.createStatement();
			ResultSet res = sta.executeQuery("SELECT COUNT(*) FROM ROOMS;");
			roomamt = res.getInt(1);
		} catch (SQLException e) {
			Debug.Print(e.getMessage());
		}

		Room[] rooms = new Room[roomamt];

		Debug.Print("Found " + roomamt + " rooms.");

		try {
			Statement sta = con.createStatement();
			ResultSet res = sta.executeQuery("SELECT * FROM ROOMS");

			int i = 0;
			while(res.next()){

				rooms[i] = new Room(res.getString("NAME"), res.getInt("ROWS"), res.getInt("COLS"));
				Debug.Print("Successfully created new Room object: " + rooms[i].toString() + " (" + rooms[i].GetName() + ")");

			i++;
			}
			con.close();
		} catch (SQLException e) {
			Debug.Print(e.getMessage());
		}

		return rooms;

	}

	// This function only exists because i can't add seats by hand to the database.
	public static void AddScreeningToDatabase(Room[] rooms, int movieid, int scrid, int roomid, LocalDateTime time){

		boolean[][] seats = new boolean[rooms[roomid].GetRows()][rooms[roomid].GetCols()];
		String strseats = Tools.Bool2DToString(seats);

		Connection con = connect();

		try {
			String sqlstr = "INSERT INTO SCREENINGS VALUES (?, ?, ?, ?, ?)";
			PreparedStatement sta = con.prepareStatement(sqlstr);
			sta.setInt(1, movieid);
			sta.setInt(2, scrid);
			sta.setInt(3, roomid);
			sta.setString(4, time.toString());
			sta.setString(5, strseats);
			sta.executeUpdate();
			con.commit();
			Debug.Print("Successfully updated the database.");
			con.close();
		} catch (SQLException e) {
			Debug.Print(e.getMessage());
		}

	}

	public static Movie[] LoadMovies(){

		Connection con = connect();

		int movamt = 0;

		try {
			Statement sta = con.createStatement();
			ResultSet res = sta.executeQuery("SELECT COUNT(*) FROM MOVIES;");
			movamt = res.getInt(1);
		} catch(SQLException e) {
			Debug.Print(e.getMessage());
		}

		Debug.Print("Found " + movamt + " movies.");
		Movie[] movies = new Movie[movamt];

		try {
			Statement sta = con.createStatement();
			ResultSet res = sta.executeQuery("SELECT * FROM MOVIES");

			int i = 0;
			while(res.next()){
				
				movies[i] = new Movie(res.getString("NAME"), res.getInt("YEAR"), res.getInt("RUNTIME"), res.getInt("RATING")); //This will need to be replaced, as the amount of movies grows beyond what a single array could handle.
				Debug.Print("Successfully created new Movie object: " + movies[i].toString() + " (" + movies[i].GetName() + ")" );

			i++;
			}

			con.close();

		} catch(SQLException e) {
			Debug.Print(e.getMessage());
		}

		return movies;

	}

	public static Screening[] GetScreeningsByDay( Movie[] movies, Room[] rooms, Calendar cal ){ // We could probably get away with having a global movie array. Let's just hope that java actually passes a pointer here, and doesn't clone the entire array.

		Connection con = connect();

		int scramt = 0;

		String month = "" + cal.get(Calendar.MONTH);
		if(month.length() == 1){ month = "0" + month;}
		String datestr = cal.get(Calendar.YEAR) + "-" + month + "-" + cal.get(Calendar.DAY_OF_MONTH);

		try {
			String com = "SELECT COUNT(*) FROM SCREENINGS WHERE DATE(?) = DATE(Time);";
			
			Debug.Print("Trying DB Query with " + datestr);
			PreparedStatement sta = con.prepareStatement(com);
			sta.setString(1, datestr);
			ResultSet res = sta.executeQuery();
			scramt = res.getInt(1);
		} catch (SQLException e) {
			Debug.Print(e.getMessage());
		}

		Screening[] screenings = new Screening[scramt];

		Debug.Print("Found " + scramt + " screenings in the database.");

		try {

			String com = "SELECT * FROM SCREENINGS WHERE DATE(?) = DATE(Time);";
			PreparedStatement sta = con.prepareStatement(com);

			sta.setString(1, datestr);

			ResultSet res = sta.executeQuery();

			int i = 0;
			while (res.next()){
				
				String seatString = res.getString("SEATS");

				if(seatString == null){
					screenings[i] = new Screening(movies[res.getInt("MovieID")], rooms[res.getInt("RoomID")], LocalDateTime.parse(res.getString("Time")));
				} else {

					boolean[][] seats = Tools.StringToBool2D(seatString);

					screenings[i] = new Screening(movies[res.getInt("MovieID")], rooms[res.getInt("RoomID")], LocalDateTime.parse(res.getString("Time")), seats);
					Debug.Print("Successfully created new screening: " + screenings[i].toString() + " (" + screenings[i].GetMovie().GetName() + ")");


				}
				i++;

			}


		} catch (SQLException e) {
			Debug.Print(e.getMessage());
		}

		return screenings;

	}
}