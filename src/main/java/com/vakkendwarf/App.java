package com.vakkendwarf;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class App 
{

    public static void main( String[] args )
    {

        //Debug.Enable();
        // Uncomment the above line if you wish to see the inner workings of my program :)
/*
		Room[] rooms = Database.LoadRooms();

        Database.AddScreeningToDatabase(rooms, 0, 1, 0, LocalDateTime.of(2020, 05, 21, 20, 00, 00));
        Database.AddScreeningToDatabase(rooms, 0, 2, 1, LocalDateTime.of(2020, 05, 21, 22, 00, 00));
        Database.AddScreeningToDatabase(rooms, 1, 3, 1, LocalDateTime.of(2020, 05, 22, 00, 00, 00));
        Database.AddScreeningToDatabase(rooms, 1, 4, 1, LocalDateTime.of(2020, 05, 22, 20, 00, 00));
        Database.AddScreeningToDatabase(rooms, 2, 5, 0, LocalDateTime.of(2020, 05, 22, 22, 00, 00));
        Database.AddScreeningToDatabase(rooms, 2, 6, 0, LocalDateTime.of(2020, 05, 22, 23, 00, 00));
        Database.AddScreeningToDatabase(rooms, 2, 7, 2, LocalDateTime.of(2020, 05, 21, 20, 00, 00));
        Database.AddScreeningToDatabase(rooms, 0, 8, 2, LocalDateTime.of(2020, 05, 21, 22, 00, 00));
        Database.AddScreeningToDatabase(rooms, 1, 9, 2, LocalDateTime.of(2020, 05, 22, 20, 00, 00));
*/

        // I can already imagine writing an admin panel to avoid things like ^

        LocalDateTime movietime = Reservation.DecideMovies();
        if(movietime.toString().hashCode() == LocalDateTime.of(0,1,1,0,0,0).toString().hashCode()){ //For some very odd reason these strings are not equal to each other but their hashcodes are. weird.
            return;
        }
        
        ArrayList<Screening> availablescreen = Reservation.GetAvailableScreenings(movietime);

        System.out.println("Found " + availablescreen.size() + " screenings.");

        if(availablescreen.size() == 0){
            System.out.println("No screenings were found for this day, please try again on a different date."); // I'm thinking we should have a repertoire
            return;
        }

        Screening chosenScr = Reservation.PickScreening(availablescreen);

        if(chosenScr == null){
            return;
        }
        
        Reservation currentRes = new Reservation(chosenScr);

        boolean check = currentRes.SelectTickets();
        
        if(!check){
            return;
        }

        currentRes.SelectSeats();

        check = currentRes.EnterName();

        if(!check){
            return;
        }

        currentRes.ConfirmReservation();

    }
}
