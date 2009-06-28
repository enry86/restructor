package sartorienrico.ais.restructor;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Restructor {
	private Database db;
	private Parser pars;
	private Console cons;
		
	public Restructor(String dataset){
		db = new Database();
		pars = new Parser(dataset,db);
		pars.parse();
		String[] names = {"city"};
		String[] values = {"molveno"};
		ResultSet r = db.query_main(names, values);
		try {
			while (r.next()){
				System.out.println(r.getInt("entity_id") + " " + r.getInt("ranking"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		db.close_db();
	}
	
	public static void main(String[] args) {
		int err = 0;
		Restructor r;
		String dataset;
		if (args.length != 1) err = 1;
		else {
			dataset = args[0];
			r = new Restructor(dataset);
		}
		System.exit(err);
	}
}
