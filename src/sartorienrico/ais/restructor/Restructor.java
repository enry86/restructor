package sartorienrico.ais.restructor;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * the main class of the RESTRUCTOR program, manages all the activities
 * of the system
 * @author enry
 *
 */
public class Restructor {
	private Database db;
	private Parser pars;
	private Console cons;
	
	/**
	 * initializes the parser, the database and the command line
	 * @param dataset filename of the dataset
	 * @param user username for the database
	 * @param passwd password for the database
	 */
	public Restructor(String dataset, String user, String passwd){
		db = new Database(user, passwd);
		System.out.println("RESTRUCTOR - type quit to exit\n");
		pars = new Parser(dataset,db);
		long time = pars.parse();
		System.out.println("Dataset parsed in "+time/1000.0+" sec.");
		cons = new Console(this);
		cons.input();
	}
	
	/**
	 * main method, checks the argument passed
	 * @param args
	 */
	public static void main(String[] args) {
		int err = 0;
		Restructor r;
		String dataset;
		String user;
		String passwd;
		if (args.length != 3) {
			err = 1;
			System.out.println("RESTRUCTOR - usage: restructor <dataset> <username> <password>");
		}
		else {
			dataset = args[0];
			user = args[1];
			passwd = args[2];
			r = new Restructor(dataset, user, passwd);
		}
		System.exit(err);
	}
	
	/**
	 * Prepares the command received for executing the query
 	 * @param pairs array containing the pairs read from command line
	 */
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
	
	/**
	 * outputs the results obtained
	 * @param res resultset containing the answers to the query
	 * @param key_num number of conditions used in the query
	 */
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
	
	/**
	 * closes the database
	 */
	public void close(){
		db.close_db();
	}
	
}
