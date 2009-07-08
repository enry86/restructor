package sartorienrico.ais.restructor;

import java.sql.*;
/**
 * This class implements all the method needed to interact with the database
 * @author enry
 *
 */
public class Database {
	private Statement st;
	private Connection con;
	
	/**
	 * Constructor of the class, opens the connection to the database
	 */
	public Database(String username, String passwd){
		String url = "jdbc:mysql://localhost:3306/restructor";
		try {
			Class.forName("com.mysql.jdbc.Driver");
			con = DriverManager.getConnection(url, username, passwd);
			st = con.createStatement();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * this function strores an attribute and its information in the tables of the database
	 * @param name name of the attribute
	 * @param value value of the attribute
	 * @param entity id of the entity
	 * @param path path of the attribute
	 * @return outcome of the operation
	 */
	public boolean store_attribute(String name, String value, int entity, String path){
		boolean res = true;
		res = insert_pair(name, value);
		res = insert_attrs(name, path);
		String subsql = "select "+entity+", pair_id, attr_id  from pairs, attrs where pairs.name = attrs.name";
		String conds = "and pairs.value = '"+value+"' and pairs.name = '"+name+"' and attrs.path = '"+path+"'";
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
	
	/**
	 * inserts a new pair in the database
	 * @param name name of the attribute
	 * @param value value of the attribute
	 * @return outcome of the operation
	 */
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
	
	/**
	 * inserts a new entry in the attrs table in the database
	 * @param name name of the attribute 
	 * @param path path of the attribute
	 * @return outcome of the operation 
	 */
	public boolean insert_attrs(String name, String path){
		boolean res = true;
		String sql = "insert into attrs values (NULL,'"+name+"','"+path+"')";
		try {
			st.executeUpdate(sql);
		} catch (SQLException e) {
			if (e.getErrorCode() == 1062) res = true;
			else res = false;
		}
		return res;
	}
	
	/**
	 * Manages the execution of the query specified by the user for each pair
	 * name - value executes 4 queries which returns different families of results
	 * @param names array containing the names of the attribute required
	 * @param values array containing the values searched
	 * @return the resultset containing the final results of the query
	 */
	public ResultSet query_main(String[] names, String[] values){
		ResultSet res = null;
		try {
			st.executeUpdate("create temporary table results (entity_id integer, rank integer, num integer)");
		} catch (SQLException e) {
			e.printStackTrace();
		}
		for (int i = 0; i < names.length; i++){
			query_first(names[i], values[i], i);
			query_second(names[i], values[i], i);
			query_third(names[i], values[i], i);
			query_fourth(names[i], values[i], i);
		}
		String sub_sql = "select entity_id, max(rank) as ranking from results group by entity_id,num";
		
		String sql = "select entity_id, sum(ranking) as ranking, count(*) from ("+sub_sql+") as t1 group by entity_id having count(*) = "+names.length+"  order by ranking desc"; 
		try {
			res = st.executeQuery(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return res;
	}
	
	/**
	 * Drops the temporary table of the results
	 */
	public void clean_tmp(){
		String sql = "drop table results";
		try {
			st.executeUpdate(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * This query inserts in the results table all the entities which owns an attribute with 
	 * exact correspondence between name and value
	 * @param name name of the attribute searched
	 * @param value value of the attribute
	 * @param num number of the condition
	 * @return number of items
	 */
	public int query_first(String name, String value, int num){
		int res = -1;
		String sql = "select distinct entity_id, 100 as rank, "+num+" as num from associations, pairs where pairs.pair_id = associations.pair_id and value = '"+value+"' and name = '"+name+"'";
		try {
			res = st.executeUpdate("insert into results "+sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return res;
	}
	
	/**
	 * Inserts in the results table all the entities which owns an attribute as a child of the one passed
	 * @param name name of the attribute
	 * @param value value of the attribute
	 * @param num number of the condition
	 * @return item read
	 */
	public int query_second(String name, String value, int num){
		int res = -1;
		String con_sec = "pairs.name in (select name from attrs where attrs.path like '%/"+name+"/%' and attrs.name != '"+name+"')";
		String sql = "select distinct entity_id, 75 as rank, "+num+" as num from associations, pairs, attrs where associations.attr_id = attrs.attr_id and attrs.path like '%"+name+"/%' and pairs.pair_id = associations.pair_id and value = '"+value+"' and "+con_sec;
		try {
			res = st.executeUpdate("insert into results "+sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return res;
	}
	
	/**
	 * Inserts into the results table all the entities which owns an attribute which is known to appear
	 * in the dataset as parent of the one selected 
	 * @param name name of the attribute
	 * @param value value of the attribute
	 * @param num number of the query
	 * @return number of items
	 */
	public int query_third(String name, String value, int num){
		int res = -1;
		String sub_sql = "select t1.name from attrs as t1, ( select * from attrs where name = '"+name+"' ) as t2 where t2.path like  concat('%/', t1.name, '/%') and t1.name != '"+name+"'";
		String sql  = "select distinct entity_id, 50 as rank, "+num+" as num from pairs, associations where pairs.pair_id  = associations.pair_id and value = '"+value+"' and name in ("+sub_sql+")";
		try {
			res = st.executeUpdate("insert into results "+sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return res;
	}
	
	/**
	 * Inserts in the results table all the entities which have an attribute known
	 * to appear in the dataset as child of the one selected
	 * @param name name of the attribute
	 * @param value attribute value
	 * @param num number of the condition
	 * @return items read
	 */
	public int query_fourth(String name, String value, int num){
		int res = -1;
		String con_sec = "pairs.name in (select t1.name from attrs as t1, attrs as t2 where t2.path like '%/"+name+"/%' and locate(concat('/', t1.name, '/'), t2.path) > locate('/"+name+"/', t2.path))";
		String sql = "select distinct entity_id, 25 as rank, "+num+" as num from associations, pairs, attrs where associations.attr_id = attrs.attr_id and pairs.pair_id = associations.pair_id and value = '"+value+"' and "+con_sec;
		try {
			res = st.executeUpdate("insert into results "+sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return res;
	}

	/**
	 * Closes the connection with the DB and flushes the tables
	 */
	public void close_db(){
		clear_table("associations");
		clear_table("pairs");
		clear_table("attrs");
		try {
			con.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Clears the table passed as argument
	 * @param table name of the table to clear
	 */
	private void clear_table(String table){
		String cmd = "delete from "+table;
		try {
			st.executeUpdate(cmd);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
}
