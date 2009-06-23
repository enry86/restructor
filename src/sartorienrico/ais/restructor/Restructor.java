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
//		pars.parse();
		ResultSet r = db.query_main("address", "molveno");
		try {
			while (r.next()){
				System.out.println(r.getInt("entity_id") + " " + r.getInt("rank"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		ResultSet s = db.query_second("address", "molveno");
		try {
			while (s.next()){
				System.out.println(s.getInt("entity_id") + " " + s.getInt("rank"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		ResultSet t = db.query_third("address", "molveno");
		try {
			while (t.next()){
				System.out.println(t.getInt("entity_id") + " " + t.getInt("rank"));
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
