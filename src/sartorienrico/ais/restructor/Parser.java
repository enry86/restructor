package sartorienrico.ais.restructor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * This class parses the xml file and stores the informations uses the class Database to store the data
 * @author enry
 *
 */
public class Parser extends DefaultHandler{
	
	private SAXParserFactory spf;
	private SAXParser sax;
	private String filename;
	private StringBuffer chars;
	
	private Database db;
	private int entity;
	
	private String path;
	private ArrayList<String> last;
	
	
	/**
	 * Initialize the class and the parser
	 * @param filename name of the file containing the dataset
	 * @param db instance of Database class
	 */
	public Parser(String filename, Database db){
		spf = SAXParserFactory.newInstance();
		last = new ArrayList<String>();
		this.filename = filename;
		this.db = db;
		chars = new StringBuffer();
		
		try {
			sax = spf.newSAXParser();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * starts the parsing task
	 * @return the time used for parsing
	 */
	public long parse(){
		long time = 0;
		time = System.currentTimeMillis();
		try {
			sax.parse(filename, this);
		} catch (SAXException e) {
			e.printStackTrace();
			return 0;
		} catch (IOException e) {
			e.printStackTrace();
			return 0;
		}
		return System.currentTimeMillis() - time;
	}
	
	/**
	 * When a new node is encountered if it's part of the data, the path value is updated and 
	 * the arraylist last keep track of the node traversed 
	 */
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException{
		if (qName.compareTo("entity") != 0 && qName.compareTo("entities") != 0){
			last.add(0, qName);
			path += qName + "/"; 
		}
		else {
			if (qName.compareTo("entity") == 0){
				entity = Integer.parseInt(attributes.getValue("id"));
			}
			path = "/";
			
		}
	}
	
	/**
	 * Called when an element is closed, saves the collected value in the database
	 */
	public void endElement(String uri, String localName, String qName) throws SAXException{
		if (qName.compareTo("entity") != 0 && qName.compareTo("entities") != 0){
			if (qName.compareTo(last.get(0)) == 0){
				String tmp = getChars();
				tmp = tmp.replaceAll("\n", " ");
				tmp = tmp.replaceAll("  *", " ");
				String[] values = tmp.split("[  *]");
				for (int i = 0; i < values.length; i++){
					if (values[i].compareTo("") != 0){
						db.store_attribute(last.get(0), values[i], entity, path);
					}
				}
				path = path.replaceAll(last.get(0)+"/$", "");
				last.remove(0);
			}
		}
	}
	
	/**
	 * Adds the characters to the buffer
	 */
	public void characters(char[] ch, int start, int length) throws SAXException {
		chars.append(ch,start,length);
	}
	
	/**
	 * reads and flushes the buffer
	 * @return
	 */
	private String getChars(){
		String res = chars.toString();
		chars.setLength(0);
		return res;
	}
}
