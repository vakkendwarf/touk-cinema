package com.vakkendwarf;

public class Debug {

	public static boolean DebugMode = false;

	public static void Enable(){
		DebugMode = true;
	}

	public static void Disable(){
		DebugMode = false;
	}
	
	public static void Print(String msg){
		if (DebugMode){
			System.out.println("[DEBUG] " + msg);
		}
	}

	public static void Print(boolean msg){
		if (DebugMode){
			if(msg){
				System.out.println("True");
			} else {
				System.out.println("False");
			}
		}
	}
	
}