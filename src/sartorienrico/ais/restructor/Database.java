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
	
	public ResultSet query_main(String name, String value){
		ResultSet res = null;
		String sql = "select entity_id, 100 as rank from associations, pairs where pairs.pair_id = associations.pair_id and value = '"+value+"' and name = '"+name+"'";
		try {
			res = st.executeQuery(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return res;
	}
	
	public ResultSet query_second(String name, String value){
		ResultSet res = null;
		String con_sec = "attrs.path like '%"+name+"/%' and attrs.name != '"+name+"'";
		String sql = "select entity_id, 75 as rank from associations, pairs, attrs where associations.attr_id = attrs.attr_id and pairs.pair_id = associations.pair_id and value = '"+value+"' and "+con_sec;
		try {
			res = st.executeQuery(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return res;
	}
	
	public ResultSet query_third(String name, String value){
		ResultSet res = null;
		String sub_sql = "select t1.attr_id from attrs as t1, ( select * from attrs where name = '"+name+"' ) as t2 where t2.path like  concat('%/', t1.name, '/%') and t1.name != '"+name+"'";
		String sql  = "select entity_id, 50 as rank from pairs, associations where pairs.pair_id  = associations.pair_id and value = '"+value+"' and associations.attr_id in ("+sub_sql+")";
		try {
			res = st.executeQuery(sql);
		} catch (SQLException e){
			e.printStackTrace();
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
