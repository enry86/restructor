package sartorienrico.ais.restructor;

public class Restructor {
	private Database db;
	private Parser pars;
	private Console cons;
		
	public Restructor(String dataset){
		db = new Database();
		pars = new Parser(dataset,db);
		db.store_attribute("city", "trento", 0, true, "/");
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
