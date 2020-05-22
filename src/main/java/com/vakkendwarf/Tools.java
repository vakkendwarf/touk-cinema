package com.vakkendwarf;

import java.time.LocalDateTime;

public class Tools {

	// Frontend could supply a DateTime instead of a string, probably.
	public static LocalDateTime StringDateToRealDate(String unformatteddate){ // lets assume we all operate on one timezone.

		char[] charudate = unformatteddate.toCharArray();

		// Check date format (YYYY-MM-DD HH:MM) for compliance.
		if(charudate[4] != '-' || charudate[7] != '-' || charudate[10] != ' ' || charudate[13] != ':'){
			System.out.println("The date you have entered is incorrect.");
			return LocalDateTime.of(0,1,1,0,0,0);
		}

		String[] splitdate = unformatteddate.split(" ");
		String[] date = splitdate[0].split("-");
		String[] time = splitdate[1].split(":");

		int year = 0;
		int month = 0;
		int day = 0;

		int hour = 0;
		int minute = 0;

		try {

			year = Integer.parseInt(date[0]);
			month = Integer.parseInt(date[1]);
			day = Integer.parseInt(date[2]);

			hour = Integer.parseInt(time[0]);
			minute = Integer.parseInt(time[1]);

		} catch (NumberFormatException e) {
			System.out.println("The date you have entered is incorrect.");
			return LocalDateTime.of(0,1,1,0,0,0);
		}

		// Protect against intentionally entered wrong input.
		if(hour > 23 || minute > 59 || hour < 0 || minute < 0){
			System.out.println("The date you have entered is incorrect.");
			return LocalDateTime.of(0,1,1,0,0,0);
		}

		LocalDateTime formatteddate = LocalDateTime.of(year, month, day, hour, minute, 0);

		return formatteddate;

	}

	public static boolean DateWithinRange(LocalDateTime testDate, LocalDateTime startDate, LocalDateTime endDate) {
		return !(testDate.isBefore(startDate) || testDate.isAfter(endDate));
	}

	public static String Bool2DToString(boolean[][] arr){

		String result = "";

		for(int i = 0; i < arr.length; i++){
			if(i > 0){result += ":";}
			for(int j = 0; j < arr[i].length; j++){
				if(arr[i][j]){
					result += "1";
				} else {
					result += "0";
				}
			}
		}

		return result;

	}

	public static boolean[][] StringToBool2D(String str){

		String[] splitstr = str.split(":");

		boolean[][] result = new boolean[splitstr.length][splitstr[0].length()];

		for(int i = 0; i < splitstr.length; i++){
			char[] a = splitstr[i].toCharArray();
			for(int j = 0; j < a.length; j++){
				if(a[j] == '1'){
					result[i][j] = true;
				} else {
					result[i][j] = false;
				}
			}
		}

		return result;

	}

	public static int[] StringToSeatCoords(String str){

		int[] result = new int[2];
		String[] resarr = str.split(",");

		int row = 0;
		int col = 0;

		int errors = 0; // Dirty fix

		if (resarr.length != 2) {
			System.out.println("You have entered an incorrect amount of numbers.");
			return null;
		}

		try{

			row = Integer.parseInt(resarr[0]) - 1;
			col = Integer.parseInt(resarr[1]) - 1;

		} catch (NumberFormatException e) {
			if(errors > 0) {}
			errors += 1;
			System.out.println("Error! The data you have supplied is incorrect!");
			return null;
		}

		result[0] = row;
		result[1] = col;

		return result;

	}

}