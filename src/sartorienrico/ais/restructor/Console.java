package sartorienrico.ais.restructor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Console {
	BufferedReader console;
	Restructor rest;
	
	public Console(Restructor r){
		console = new BufferedReader(new InputStreamReader(System.in));
		rest = r;
	}
	
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
	
	private boolean check_line(String line){
		String base = "[a-zA-Z1-9][a-zA-Z1-9]* *: *[a-zA-Z1-9][a-zA-Z1-9]*";
		return line.matches(base+"( *, *"+base+")*");	
	}
	
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
