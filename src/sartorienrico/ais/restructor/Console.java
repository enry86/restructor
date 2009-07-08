package sartorienrico.ais.restructor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * This class is the command line interface to the user
 * @author enry
 *
 */
public class Console {
	BufferedReader console;
	Restructor rest;
	
	/**
	 * initilizes the console
	 * @param r instance of the class Restructor
	 */
	public Console(Restructor r){
		console = new BufferedReader(new InputStreamReader(System.in));
		rest = r;
	}
	
	/**
	 * Reads the line in input
	 */
	public void input(){
		String line = "";
		while (line.compareTo("quit") != 0) {
			System.out.print("> ");
			try {
				line = console.readLine();
				parse_line(line);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} 
	}
	
	/**
	 * Checks if the command follows the syntax rules
	 * @param line the command typed by the user
	 * @return result of the check
	 */
	private boolean check_line(String line){
		String base = "[a-zA-Z1-9][a-zA-Z1-9]* *: *[a-zA-Z1-9][a-zA-Z1-9]*";
		return line.matches(base+"( *, *"+base+")*");	
	}
	
	/**
	 * Parse the line and eventually launches the query, or closes the program
	 * @param line
	 */
	private void parse_line(String line){
		String[] pairs;
		if (line.compareTo("quit") == 0){
			System.out.println("Closing...");
			rest.close();
		}
		else if (check_line(line)){
			line = line.replaceAll(" ", "");
			pairs = line.split(",");
			rest.manage_query(pairs);
		}
		else System.out.println("Command error!");
	}
}
