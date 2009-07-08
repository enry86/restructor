package sartorienrico.ais.restructor;

import java.io.IOException;
import java.util.Date;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class Parser extends DefaultHandler{
	
	private SAXParserFactory spf;
	private SAXParser sax;
	private String filename;
	private StringBuffer chars;
	
	private Database db;
	private int entity;
	
	private String path;
	private String last;
	
	public Parser(String filename, Database db){
		spf = SAXParserFactory.newInstance();
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
	
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException{
		if (qName.compareTo("entity") != 0 && qName.compareTo("entities") != 0){
			last = qName;
			path += qName + "/"; 
		}
		else {
			if (qName.compareTo("entity") == 0){
				entity = Integer.parseInt(attributes.getValue("id"));
			}
			path = "/";
			
		}
	}
	
	public void endElement(String uri, String localName, String qName) throws SAXException{
		if (qName.compareTo(last) == 0){
			String tmp = getChars();
			tmp = tmp.replaceAll("\n", " ");
			tmp = tmp.replaceAll("  *", " ");
			String[] values = tmp.split("[  *]");
			for (int i = 0; i < values.length; i++){
				if (values[i].compareTo("") != 0){
					db.store_attribute(last, values[i], entity, path);
				}
			}
			path = path.replaceAll(last+"/$", "");
			
		}
	}
	
	public void characters(char[] ch, int start, int length) throws SAXException {
		chars.append(ch,start,length);
	}
	
	private String getChars(){
		String res = chars.toString();
		chars.setLength(0);
		return res;
	}
}
