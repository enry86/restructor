package sartorienrico.ais.restructor;

import java.sql.*;

public class Database {
	private Statement st;
	private Connection con;
	
	public Database(){
		String url = "jdbc:mysql://localhost:3306/restructor";
		try {
			Class.forName("com.mysql.jdbc.Driver");
			con = DriverManager.getConnection(url, "enry", "miyukidb");
			st = con.createStatement();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public boolean store_attribute(String name, String value, int entity, boolean has_child, String path){
		boolean res = true;
		res = insert_pair(name, value);
		res = insert_attrs(name, has_child, path);
		String subsql = "select "+entity+", pair_id, attr_id  from pairs, attrs where pairs.name = attrs.name";
		String conds = "and pairs.value = '"+value+"' and pairs.name = '"+name+"' and attrs.has_child = "+has_child+" and attrs.path = '"+path+"'";
		String sql = "insert into associations "+subsql+" "+conds;
		if (res){
			try {
				st.executeUpdate(sql);
			} catch (SQLException e) {
				if (e.getErrorCode() == 1062) res = true;
				else res = false;
			}
		}
		return res;
	}
	
	public boolean insert_pair(String name, String value){
		boolean res = true;
		String sql = "insert into `pairs` values (NULL,'"+name+"','"+value+"')";
		try {
			st.executeUpdate(sql);
		} catch (SQLException e) {
			if (e.getErrorCode() == 1062) res = true;
			else res = false;
		} 
		return res;
	}
	
	public boolean insert_attrs(String name, boolean has_child, String path){
		boolean res = true;
		String sql = "insert into attrs values (NULL,'"+name+"',"+has_child+",'"+path+"')";
		try {
			st.executeUpdate(sql);
		} catch (SQLException e) {
			if (e.getErrorCode() == 1062) res = true;
			else res = false;
		}
		return res;
	}
	
	
	
	public void close_db(){
		//clear_table("associations");
		//clear_table("pairs");
		//clear_table("attrs");
		try {
			con.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	private void clear_table(String table){
		String cmd = "delete from "+table;
		try {
			st.executeUpdate(cmd);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
}
