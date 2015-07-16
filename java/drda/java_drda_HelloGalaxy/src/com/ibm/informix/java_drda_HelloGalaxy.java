/*-
 * Java Sample Application: Connection to Informix using DRDA
 */

/*-
 * Topics
 * 1 Create table 
 * 2 Inserts
 * 2.1 Insert a single document into a table
 * 2.2 Insert multiple documents into a table
 * 3 Queries
 * 3.1 Find one document in a table that matches a query condition
 * 3.2 Find documents in a table that match a query condition
 * 3.3 Find all documents in a table
 * 3.4 Count documents with query
 * 3.5 Order documents in a table
 * 3.6 Join tables
 * 3.7 Find distinct fields in a table
 * 3.8 Find with projection clause
 * 4 Update documents in a table
 * 5 Delete documents in a table
 * 6 Transactions
 * 7 Commands
 * 7.1 Count
 * 7.2 Distinct
 * 8 Drop a table
 */

package com.ibm.informix;
import java.io.StringReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;

import com.ibm.db2.jcc.DB2Driver;

public class java_drda_HelloGalaxy {

	public static String DRDAURL;// = "jdbc:ids://yourURL:port/db:user=username;password=password;";
	public static List<String> everything = new ArrayList<String>();
	
	public static final City kansasCity = new City("Kansas City", 467007, 39.0997, 94.5783, 1);
	public static final City seattle = new City("Seattle", 652405, 47.6097, 122.3331, 1);
	public static final City newYork = new City("New York", 8406000, 40.7127, 74.0059, 1);
	public static final City london = new City("London", 8308000, 51.5072, 0.1275, 44);
	public static final City tokyo = new City("Tokyo", 13350000, 35.6833, -139.6833, 81);
	public static final City madrid = new City("Madrid", 3165000, 40.4001, 3.7167, 34);
	public static final City melbourne = new City("Melbourne", 4087000, -37.8136, -144.9631, 61);
	public static final City sydney = new City("Sydney", 4293000, -33.8651, -151.2094, 61);
		
	public static void main(String[] args) {
		// connect to bluemix
		// if (args[0] != null)
		// DRDAURL = args[0];
		// else
		// parseVcap();

		doEverything();

		// print log
		for (String s : everything)
			System.out.println(s);

	}

	public static List<String> doEverything() {
		parseVcap();
		try {

			// initialize some variables
			String tableName = "cities";
			String sql = "";
			List<String> output = new ArrayList<String>();
			PreparedStatement statement = null;
			Properties prop = new Properties();
			Connection conn;
			// <------------------------------------->

			// connect to database
			conn = new DB2Driver().connect(DRDAURL, prop);
			if (conn != null)
				everything.add("Connected to: " + DRDAURL);
			// <------------------------------------->

			everything.add("\nTopics");

			// 1 Create table
			everything.add("\n1 Create table");
			
			sql = "create table if not exists " + tableName + " (City VARCHAR(255),Population INTEGER,Longitude DECIMAL(8,4),Latitude DECIMAL(8,4),Code INTEGER)";
			statement = conn.prepareStatement(sql);
			statement.executeUpdate();

			everything.add("\tCreate a table named: " + tableName);
			everything.add("\tCreate Table SQL: " + sql);
			// <------------------------------------->

			// 2 Inserts
			everything.add("\n2 Inserts");

			// 2.1 Insert a single document into a table
			everything.add("2.1 Insert a single document into a table");

			sql = "insert into " + tableName + " values (?,?,?,?,?)";
			statement = conn.prepareStatement(sql);
			statement.setString(1, kansasCity.name);
			statement.setInt(2, kansasCity.population);
			statement.setDouble(3, kansasCity.longitude);
			statement.setDouble(4, kansasCity.latitude);
			statement.setInt(5, kansasCity.countryCode);
			statement.executeUpdate();
			statement.close();

			everything.add("\tCreate document -> " + kansasCity.toString());
			everything.add("\tSingle Insert SQL: " + sql);
			// <------------------------------------->

			// 2.2 Insert multiple documents into a table
			everything.add("\n2.2 Insert multiple documents into a table");

			List<City> cities = new ArrayList<City>();
			cities.add(seattle);
			cities.add(newYork);
			cities.add(london);
			cities.add(tokyo);
			cities.add(madrid);
			cities.add(melbourne);
			sql = "insert into " + tableName + " values(?,?,?,?,?)";
			statement = conn.prepareStatement(sql);
			for (City city : cities) {
				statement.setString(1, city.name);
				statement.setInt(2, city.population);
				statement.setDouble(3, city.longitude);
				statement.setDouble(4, city.latitude);
				statement.setInt(5, city.countryCode);
				statement.addBatch();
			}
			statement.executeBatch();
			statement.close();

			for (City city : cities)
				everything
						.add("\tCreate Document -> " + city.toString());
			everything.add("\tMultiple Insert SQL: " + sql
					+ " (executed as batch)");
			// <------------------------------------->

			// 3 Queries
			everything.add("\n3 Queries");

			// 3.1 Find one document in a table that matches a query condition
			everything
					.add("3.1 Find one document in a table that matches a query condition");

			output.clear();
			String condition = "population > 8000000 and code = 1";
			sql = "select * from " + tableName + " where " + condition;
			statement = conn.prepareStatement(sql);
			ResultSet rs = statement.executeQuery();
			ResultSetMetaData rsmd = rs.getMetaData();
			StringBuilder stringBuilder = new StringBuilder();
			while (rs.next()){
				for (int i=1;i<=rsmd.getColumnCount();i++){
					stringBuilder.append(rsmd.getColumnName(i) + " : " + rs.getString(i) + "  \t");
				}
				output.add(stringBuilder.toString());
				stringBuilder.delete(0, stringBuilder.toString().length());
			}
			String firstDocument = output.get(0);
			rs.close();
			statement.close();

			everything.add("\tFind document with: " + condition);
			everything.add("\tFirst document -> "
					+ firstDocument.toString());
			everything.add("\tQuery SQL: " + sql);
			// <------------------------------------->

			// 3.2 Find documents in a table that match a query condition
			everything
					.add("\n3.2 Find documents in a table that match a query condition");

			output.clear();
			condition = "population > 8000000 and longitude > 40.0";
			sql = "select * from " + tableName + " where " + condition;
			statement = conn.prepareStatement(sql);
			rs = statement.executeQuery();
			rsmd = rs.getMetaData();
			stringBuilder = new StringBuilder();
			while (rs.next()){
				for (int i=1;i<=rsmd.getColumnCount();i++){
					stringBuilder.append(rsmd.getColumnName(i) + " : " + rs.getString(i) + "  \t");
				}
				output.add(stringBuilder.toString());
				stringBuilder.delete(0, stringBuilder.toString().length());
			}
			rs.close();
			statement.close();

			everything.add("\tFind all documents with: " + condition);
			for (String city : output)
				everything.add("\tFound Document -> " + city.toString());

			everything.add("\tQuery All SQL: " + sql);
			// <------------------------------------->

			// 3.3 Find all documents in a table
			everything.add("\n3.3 Find all documents in a table");

			output.clear();
			sql = "select * from " + tableName;
			statement = conn.prepareStatement(sql);
			rs = statement.executeQuery();
			rsmd = rs.getMetaData();
			stringBuilder = new StringBuilder();
			while (rs.next()){
				for (int i=1;i<=rsmd.getColumnCount();i++){
					stringBuilder.append(rsmd.getColumnName(i) + " : " + rs.getString(i) + "  \t");
				}
				output.add(stringBuilder.toString());
				stringBuilder.delete(0, stringBuilder.toString().length());
			}
			rs.close();
			statement.close();

			everything.add("\tFind all documents in table: " + tableName);
			for (String city : output)
				everything.add("\tFound document -> "
						+ city.toString());
			everything.add("\tFind All Documents SQL: " + sql);
			// <------------------------------------->
			
			//3.4 Count documents in a table
			everything.add("\n3.4 Count documents with query");
			
			condition = "longitude < 40.0";
			sql = "select count(*) from " + tableName + " where " + condition;
			statement = conn.prepareStatement(sql);
			rs = statement.executeQuery();
			int numberInTable = 0;
			while (rs.next())
				numberInTable = rs.getInt(1);
			rs.close();
			statement.close();
			
			everything.add("\tCount documents with: " + condition);
			everything.add("\tNumber of documents: " + numberInTable);
			everything.add("\tCount Documents SQL: " + sql);
			// <------------------------------------->
			
			//3.5 Order documents in a table
			everything.add("\n3.5 Order documents in a table");
			
			output.clear();
			condition = "population";
			sql = "select * from " + tableName + " order by " + condition;
			statement = conn.prepareStatement(sql);
			rs = statement.executeQuery();
			rsmd = rs.getMetaData();
			stringBuilder = new StringBuilder();
			while (rs.next()){
				for (int i=1;i<=rsmd.getColumnCount();i++){
					stringBuilder.append(rsmd.getColumnName(i) + " : " + rs.getString(i) + "  \t");
				}
				output.add(stringBuilder.toString());
				stringBuilder.delete(0, stringBuilder.toString().length());
			}
			rs.close();
			statement.close();
			
			everything.add("\tSort documents by: " + condition);
			for (String city : output)
				everything.add("\tSorted Document -> "
						+ city.toString());
			everything.add("\tOrder By SQL: " + sql);
			// <------------------------------------->
			
			//3.6 Joins
			everything.add("\n3.6 Joins");
			
			//create another table with data
			String tableJoin = "country";
			sql = "create table if not exists " + tableJoin + " (countryCode INTEGER, countryName VARCHAR(255))";
			statement = conn.prepareStatement(sql);
			statement.executeUpdate();
			sql = "insert into " + tableJoin + " values (1,\"United States of America\")";
			statement = conn.prepareStatement(sql);
			statement.executeUpdate();
			statement.close();
			sql = "insert into " + tableJoin + " values (44,\"United Kingdom\")";
			statement = conn.prepareStatement(sql);
			statement.executeUpdate();
			statement.close();
			sql = "insert into " + tableJoin + " values (81,\"Japan\")";
			statement = conn.prepareStatement(sql);
			statement.executeUpdate();
			statement.close();
			sql = "insert into " + tableJoin + " values (34,\"Spain\")";
			statement = conn.prepareStatement(sql);
			statement.executeUpdate();
			statement.close();
			sql = "insert into " + tableJoin + " values (61,\"Australia\")";
			statement = conn.prepareStatement(sql);
			statement.executeUpdate();
			statement.close();
			
			//join tables
			output.clear();
			sql = "select table1.city, table1.population, table1.longitude, table1.latitude, table1.code, table2.countryName from " + tableName + 
					" table1 inner join " + tableJoin + " table2 on table1.code=table2.countryCode"; 
			statement = conn.prepareStatement(sql);
			rs = statement.executeQuery();
			rsmd = rs.getMetaData();
			stringBuilder = new StringBuilder();
			while (rs.next()){
				for (int i=1;i<=rsmd.getColumnCount();i++){
					stringBuilder.append(rsmd.getColumnName(i) + " : " + rs.getString(i) + "  \t");
				}
				output.add(stringBuilder.toString());
				stringBuilder.delete(0, stringBuilder.toString().length());
			}
			rs.close();
			statement.close();
			
			everything.add("\tJoin tables: " + tableName + " and " + tableJoin);
			for (String city : output)
				everything.add("\tJoined Document -> " + city.toString());
			everything.add("\tJoin SQL: " + sql);
			// <------------------------------------->
			
			//3.7 Find distinct fields in a table
			everything.add("\n3.7 Find distinct fields in a table");
			
			output.clear();
			condition = "longitude > 40.0";
			sql = "select distinct code from " + tableName + " where " + condition;
			statement = conn.prepareStatement(sql);
			rs = statement.executeQuery();
			rsmd = rs.getMetaData();
			stringBuilder = new StringBuilder();
			while (rs.next()){
				for (int i=1;i<=rsmd.getColumnCount();i++){
					stringBuilder.append(rsmd.getColumnName(i) + " : " + rs.getString(i) + "  \t");
				}
				output.add(stringBuilder.toString());
				stringBuilder.delete(0, stringBuilder.toString().length());
			}
			rs.close();
			statement.close();
			
			everything.add("\tFind distinct with: " + condition);
			for (String city : output)
				everything.add("\tDocument Found -> " + city.toString());
			everything.add("\tDistinct SQL: " + sql);
			// <------------------------------------->
			
			//3.8 Find with projection clause
			everything.add("\n3.8 Find with projection clause");
			
			output.clear();
			condition = "population > 8000000";
			String projection = "city, code";
			sql = "select distinct " + projection + " from " + tableName + " where " + condition;
			statement = conn.prepareStatement(sql);
			rs = statement.executeQuery();
			rsmd = rs.getMetaData();
			stringBuilder = new StringBuilder();
			while (rs.next()) {
				for (int i=1;i<=rsmd.getColumnCount();i++){
					stringBuilder.append(rsmd.getColumnName(i) + " : " + rs.getString(i) + "  \t");
				}
				output.add(stringBuilder.toString());
				stringBuilder.delete(0, stringBuilder.toString().length());
			}
			rs.close();
			statement.close();
			
			everything.add("\tFind: " + projection + " with: " + condition);
			for (String city : output)
				everything.add("\tDocument Found -> " + city.toString());
			everything.add("\tProjection SQL: " + sql);
			// <------------------------------------->
			
			// 4 Update documents in a table
			everything.add("\n4 Update documents in a table");
			
			String nameToUpdate = "Seattle";
			int updatedValue = 999;
			statement.close();
			sql = "update " + tableName + " set code = " + updatedValue + " where city  = '" + nameToUpdate + "'";
			statement = conn.prepareStatement(sql);
			statement.executeUpdate();
			statement.close();

			everything.add("\tDocument to update: " + nameToUpdate);
			everything.add("\tUpdate By Name SQL: " + sql);
			// <------------------------------------->

			// 5 Delete documents in a table
			everything.add("\n5 Delete documents in a table");

			String nameToDelete = "Tokyo";
			sql = "delete from " + tableName + " where city like '"
					+ nameToDelete + "'";
			statement = conn.prepareStatement(sql);
			statement.executeUpdate();
			statement.close();

			everything.add("\tDelete documents with ID: " + nameToDelete);
			everything.add("\tDelete By ID SQL: " + sql);
			// <------------------------------------->
			
			//6 Transactions
			everything.add("\n6 Transactions");
			
			//transaction start
			conn.setAutoCommit(false);
			
			everything.add("\tStart Transaction...");
			
			//transaction insert
			sql = "insert into " + tableName + " values (?,?,?,?,?)";
			statement = conn.prepareStatement(sql);
			statement.setString(1, sydney.name);
			statement.setInt(2, sydney.population);
			statement.setDouble(3, sydney.longitude);
			statement.setDouble(4, sydney.latitude);
			statement.setInt(5, sydney.countryCode);
			statement.executeUpdate();
			
			everything.add("\tInsert Document");
			
			//transaction update
			nameToUpdate = "Seattle";
			updatedValue = 998;
			sql = "update " + tableName + " set code = " + updatedValue + " where city  = '" + nameToUpdate + "'";
			statement = conn.prepareStatement(sql);
			statement.executeUpdate();
			
			everything.add("\tUpdate Document");
			
			//transaction savepoint
			conn.commit();
			Savepoint savepoint = conn.setSavepoint();
			
			everything.add("\tCreate Savepoint...");
			
			//transaction delete
			nameToDelete = "Sydney";
			sql = "delete from " + tableName + " where city like '"
					+ nameToDelete + "'";
			statement = conn.prepareStatement(sql);
			statement.executeUpdate();
			
			everything.add("\tDelete Document");
			
			//transaction rollback
			conn.rollback(savepoint);
			conn.commit();
			
			everything.add("\tRoll back to savepoint...");
			
			//transaction end
			conn.setAutoCommit(true);
			
			everything.add("\tTransaction Complete");
			// <------------------------------------->
			
			//7 Commands
			everything.add("\n7 Commands");
			
			//7.1 Count
			everything.add("7.1 Count");
			
			sql = "select count(*) from " + tableName;
			statement = conn.prepareStatement(sql);
			rs = statement.executeQuery();
			numberInTable = 0;
			while (rs.next())
				numberInTable = rs.getInt(1);
			rs.close();
			statement.close();
			
			everything.add("\tCount documents in table: " + tableName);
			everything.add("\tNumber of documents: " + numberInTable);
			everything.add("\tCount SQL: " + sql);
			// <------------------------------------->
			
			//7.2 Distinct
			everything.add("\n7.2 Distinct");
			
			output.clear();
			sql = "select distinct code from " + tableName;
			statement = conn.prepareStatement(sql);
			rs = statement.executeQuery();
			rsmd = rs.getMetaData();
			stringBuilder = new StringBuilder();
			while (rs.next()){
				for (int i=1;i<=rsmd.getColumnCount();i++){
					stringBuilder.append(rsmd.getColumnName(i) + " : " + rs.getString(i) + "  \t");
				}
				output.add(stringBuilder.toString());
				stringBuilder.delete(0, stringBuilder.toString().length());
			}
			rs.close();
			statement.close();
			
			everything.add("\tFind distinct code in: " + tableName);
			for (String city : output)
				everything.add("\tDocument Found -> " + city.toString());
			everything.add("\tDistinct SQL: " + sql);
			// <------------------------------------->

			// 8 Drop a table
			everything.add("\n8 Drop a table");
	
			sql = "drop table " + tableName;
			statement = conn.prepareStatement(sql);
			statement.executeUpdate();
			statement.close();
			sql = "drop table " + tableJoin;
			statement = conn.prepareStatement(sql);
			statement.executeUpdate();
			statement.close();
	
			everything.add("\tDrop table: " + tableName);
			everything.add("\tDrop table: " + tableJoin);
			everything.add("\tDrop Table SQL: " + sql);
			// <------------------------------------->

			// Complete
			everything.add("\nComplete!");

		} catch (Exception e) {
			System.err.println("[ERROR] "
					+ (e instanceof SQLException ? " Error Code : "
							+ ((SQLException) e).getErrorCode() : "")
					+ " Message : " + e.getMessage());
			e.printStackTrace();
		}
		return everything;
	}

	public static void parseVcap() {

		StringReader stringReader = new StringReader(
				System.getenv("VCAP_SERVICES"));
		JsonReader jsonReader = Json.createReader(stringReader);
		JsonObject vcap = jsonReader.readObject();
		System.out.println("vcap: " + vcap);
		boolean ssl = false;
		if (ssl)
			DRDAURL = vcap.getJsonArray("clouddb-dev").getJsonObject(0)
					.getJsonObject("credentials").getString("ssl_drda_url");
		else
			DRDAURL = vcap.getJsonArray("clouddb-dev").getJsonObject(0)
					.getJsonObject("credentials").getString("drda_url");
		System.out.println(DRDAURL);

	}

}

class City {
	public final String name;
	public final int population;
	public final double longitude;
	public final double latitude;
	public final int countryCode;

	public City(String name, int population, double longitude, double latitude, int countryCode) {
		this.name = name;
		this.population = population;
		this.longitude = longitude;
		this.latitude = latitude;
		this.countryCode = countryCode;
	}

	public String toString(){
		return "city: " + this.name + "  \tpopulation: " + this.population + "\tlongitude: " + this.longitude + 
				"\tlatitude: " + this.latitude  +  "\tcode: " + this.countryCode;
	}
}