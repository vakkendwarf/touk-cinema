package com.vakkendwarf;

import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.InputMismatchException;
import java.util.Scanner;
import java.util.regex.*;

// REST!

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("/reservation")
public class Reservation {

	static Scanner in = new Scanner(new InputStreamReader(System.in, StandardCharsets.ISO_8859_1));

	private Screening Screening;
	private int SeatAmt;
	private float ToPay;
	private String Firstname;
	private String Lastname;

	public Reservation(Screening scrn){

		this.Screening = scrn;

	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public boolean VerifyName(String first, String last){

		char[] firstc = first.toCharArray();
		char[] lastc = last.toCharArray();

		Pattern p1 = Pattern.compile("([A-z]+-[A-z]+)|([A-z]+)");
		Pattern p2 = Pattern.compile("[A-z]+");

		if(first.length() < 3 || last.length() < 3){
			System.out.println("Error: first or last name is too short.");
			return false;
		}

		else if(Character.toUpperCase(firstc[0]) != firstc[0] || Character.toUpperCase(lastc[0]) != lastc[0]){
			System.out.println("Error: first character of first name and last name must be uppercase.");
			return false;
		}

		else if(!p2.matcher(first).matches()){
			System.out.println("Error: the first name you have entered is incorrect.");
			return false;
		}

		else if(!p1.matcher(last).matches()){
			System.out.println("Error: the last name you have entered is incorrect.");
			return false;
		}

		else {
			this.Firstname = first;
			this.Lastname = last;
			return true;
		}

	}

	// For the demo.
	public boolean EnterName(){

		String first = null;
		String last = null;

		try {

			System.out.println("Please enter your name to confirm this reservation. If your last name has two parts, use a dash to separate them.");

			in.nextLine(); // Dirty fix for the program immediately jumping to next step without accepting input.
			String name = in.nextLine();

			if(name.split(" ").length != 2){
				System.out.println("Error: the data you have entered is incorrect.");				
				return false;
			}

			String[] splitname = name.split(" ");

			first = splitname[0];
			last = splitname[1];

			Debug.Print("Firstname: '" + first + "'");
			Debug.Print("Lastname: '" + last + "'");

		}

		catch (Exception e) {

			System.out.println("Error: the data you have entered is incorrect.");
			return false;

		}
		
		System.out.println("Your name is: " + first + " " + last);

		if(!VerifyName(first, last)){
			return false;
		}

		return true;

	}

	// For demonstration purposes.
	public static LocalDateTime DecideMovies(){

		// I thought that we'd need this, but as it turns out, if you're picking a movie for when you return from vacation, you'll pick using local time of the cinema, and doing otherwise would be illogical.
		/*
		Calendar cal = Calendar.getInstance();
		TimeZone tz = TimeZone.getTimeZone("CET");
		cal.setTimeZone(tz);
		System.out.println("Please type in your timezone. (or press enter for CET)");
		String pickedtimezone = in.nextLine();
		if(pickedtimezone != ""){
			tz = TimeZone.getTimeZone(pickedtimezone);
			System.out.println("You have set your timezone to: " + tz.getDisplayName());
		}
		*/

		System.out.println("At what time do you want to see a movie? Please use YYYY-MM-DD HH:MM");
		String unformatteddate = in.nextLine();

		try{
			return Tools.StringDateToRealDate(unformatteddate);
		} catch(ArrayIndexOutOfBoundsException e){
			System.out.println("The date you have specified is incorrect.");
			return LocalDateTime.of(0,1,1,0,0,0);
		}

	}

	public static Screening PickScreening(ArrayList<Screening> scr){ // Once again, dummy frontend just for the demo.

		System.out.println("The following screenings are available for you in the picked time:");

		int i = 0;
		for(Screening scrn: scr){
			System.out.println("[" + i + "] : '" + scrn.GetMovie().GetName() + "' (" + scrn.GetMovie().GetYear() + ") In room " + scrn.GetRoom().GetName() + " at " + scrn.GetTime().toString());
			i++;
		}

		System.out.println("Please choose one of these screenings by typing its number shown in the square brackets.");

		int chosenScr = 0;

		try {
			chosenScr = Math.abs(in.nextInt());
		}

		catch (Exception e) {
			System.out.println("The data you have entered is incorrect.");
			return null;
		}

		Screening chosen = null;

		try {
			chosen = scr.get(chosenScr);
		}

		catch (IndexOutOfBoundsException e) {
			System.out.println("The data you have entered is incorrect.");
			return null;
		}

		return chosen;

	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public static ArrayList<Screening> GetAvailableScreenings(LocalDateTime when) { // We consider everything half an hour before and 3 hours after the specified time.
		
		Calendar cal = Calendar.getInstance();
		cal.set(when.getYear(), when.getMonthValue(), when.getDayOfMonth());

		Room[] allrooms = Database.LoadRooms(); // These might get very huge. Need a futureproof workaround.
		Movie[] allmovies = Database.LoadMovies();
		Screening[] screeningsToday = Database.GetScreeningsByDay(allmovies, allrooms, cal);

		ArrayList<Screening> screeningsFound = new ArrayList<Screening>();
		

		for(Screening screening: screeningsToday){
			if(Tools.DateWithinRange(screening.GetTime(), when.minusMinutes(30), when.plusMinutes(180))){
				screeningsFound.add(screening);
			}
		}

		return screeningsFound;

	}

	public boolean SelectTickets(){ // Demo purposes.
		
		int adultticks = 0;
		int studentticks = 0;
		int childticks = 0;

		try {
		System.out.println("How many adult tickets do you want to reserve?");
		adultticks = Math.abs(in.nextInt());
		System.out.println("How many student tickets do you want to reserve?");
		studentticks = Math.abs(in.nextInt());
		System.out.println("How many children tickets do you want to reserve?");
		childticks = Math.abs(in.nextInt());

		} catch (InputMismatchException e) {
			System.out.println("The data you have entered is incorrect. Only use integer numbers."); // Non-numbers would be normally blocked by frontend, 
																									 //so for the sake of demonstration, we're just going to brick the transaction here.
			return false;
		}

		SelectTicketsInstantly(adultticks, studentticks, childticks);

		return true;
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public void SelectTicketsInstantly(int adultticks, int studentticks, int childticks){
		this.SeatAmt = adultticks + studentticks + childticks;
		this.ToPay = adultticks*25 + studentticks*19 + childticks*12.5f;// For the sake of this demonstration i presume that the price is constant, but
																		// it would be very easy to replace these constant prices with variable prices.

	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	public boolean VerifySeatCoordinates(String coords){

			int[] coordinates = Tools.StringToSeatCoords(coords);

			int row = coordinates[0];
			int col = coordinates[1];

			boolean[][] s = this.Screening.GetSeats(); // shortcut variable

			Debug.Print("Row: " + row);
			Debug.Print("Col: " + col);

			Debug.Print("MaxRows: " + (this.Screening.GetRoom().GetRows() - 1));
			Debug.Print("MaxCols: " + (this.Screening.GetRoom().GetCols() - 1));

			if(row < 0 || row > this.Screening.GetRoom().GetRows()-1 || col < 0 || col > this.Screening.GetRoom().GetCols()-1){
				System.out.println("Error! The seat you picked is out of bounds.");
				return false;
			} 

			if(s[row][col]){
				System.out.println("Error! You can not reserve a seat that is already reserved.");
				return false;
			}

			int rb = this.Screening.GetRoom().GetCols()-1;

			if(col < 2 || col > rb-2){ // this is split into more"ifs" to improve readability.
				if((col == 0 && !s[row][1] && s[row][2]) || (col == rb && !s[row][rb-1] && s[row][rb-2])) {
					System.out.println("Error! There must not be one empty seat next to the reserved seat.");
					return false;
				}
			} else {
				if((!(s[row][col-1]) && s[row][col-2]) || (!(s[row][col+1]) && s[row][col+2])) {
					System.out.println("Error! There must not be one empty seat between reserved ones.");
					return false;
				}
			}

			return true;

	}

	public void SelectSeats(){ // Demo purposes.
		System.out.println("You have " + this.SeatAmt + " seats to reserve.");
		System.out.println("This is your screening room (" + this.Screening.GetRoom().GetName() + ")");
		System.out.println("Please type in desired seats in the following format: ROW,SEAT");
		System.out.println("Example: 6,5");

		for(int i = 0; i < this.SeatAmt; i++){
			this.Screening.ShowSeats();
			System.out.println("You have " + Integer.toString(this.SeatAmt - i) + " seats left to reserve.");
			System.out.println("Please specify seat location: ");

			String response = in.next();

			boolean check = this.VerifySeatCoordinates(response);

			int[] coordinates = Tools.StringToSeatCoords(response);

			int row = coordinates[0];
			int col = coordinates[1];

			if(check){
				Screening.Reserve(row, col);
			} 
			
			else {
				i-=1;
				continue;
			}

		}
		this.Screening.ShowSeats();

	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public void ConfirmReservation(){

		this.Screening.ShowSeats();
		System.out.println(this.Firstname + " " + this.Lastname + "! Your reservation for '" + this.Screening.GetMovie().GetName() + "' was successful");
		System.out.println("The total is: " + this.ToPay);
		System.out.println("Your reservation is valid until " + this.Screening.GetTime().minusMinutes(15) + " (15 minutes before screening time). Pick up your tickets from our front desk before then.");

		//TODO: Save to database?

	}
	
}