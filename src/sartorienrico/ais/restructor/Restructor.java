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
		cons = new Console(this);
		cons.input();
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
	
	public void manage_query(String[] pairs){
		ResultSet res;
		String[] tmp;
		String[] values = new String[pairs.length];
		String[] keys = new String[pairs.length];
		for (int i = 0; i < pairs.length; i++){
			tmp = pairs[i].split(":");
			keys[i] = tmp[0];
			values[i] = tmp[1];
		}
		res = db.query_main(keys, values);
		output_res(res, pairs.length);
		db.clean_tmp();
	}
	
	private void output_res(ResultSet res, int key_num){
		System.out.println("Entity | Relevance");
		try {
			while (res.next()){
				System.out.println(res.getInt("entity_id")+" | "+(res.getInt("ranking") / (float) key_num));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void close(){
		db.close_db();
	}
	
}
