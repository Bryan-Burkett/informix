package com.ibm.informix;

/*-
 * Java Sample Application: Connection to Informix using Mongo
 */

/*
Topics
1 Data Structures
1.1 Create collection
1.2 Create table
2 Inserts
2.1 Insert a single document into a collection 
2.2 Insert multiple documents into a collection 
3 Queries
3.1 Find one document in a collection 
3.2 Find documents in a collection 
3.3 Find all documents in a collection 
3.4 Count documents in a collection 
3.5 Order documents in a collection 
3.6 Find distinct fields in a collection 
3.7 Joins
3.7a Collection-Collection join
3.7b Table-Collection join
3.7c Table-Table join 
3.8 Modifying batch size 
3.9 Find with projection clause 
4 Update documents in a collection 
5 Delete documents in a collection 
6 SQL passthrough 
7 Transactions
8 Commands
8.1 Count  
8.2 Distinct 
8.3 CollStats 
8.4 DBStats 
9 Drop a collection
*/

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;

import com.mongodb.BasicDBObject;
import com.mongodb.CommandResult;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.MongoException;


public class java_mongo_HelloGalaxy {

	public static String MONGOURL;
	public static String databaseName;
	public static String host;
	public static String username;
	public static String password;
	public static String port;
	public static final String collectionName1 = "cities";
	public static final String collectionName2 = "countries";
	public static final String tableName1 = "cityTable";
	public static final String tableName2 = "codeTable";
	
	public static final City kansasCity = new City("Kansas City", 467007, 39.0997, 94.5783, 1);
	public static final City seattle = new City("Seattle", 652405, 47.6097, 122.3331, 1);
	public static final City newYork = new City("New York", 8406000, 40.7127, 74.0059, 1);
	public static final City london = new City("London", 8308000, 51.5072, 0.1275, 44);
	public static final City tokyo = new City("Tokyo", 13350000, 35.6833, -139.6833, 81);
	public static final City madrid = new City("Madrid", 3165000, 40.4000, 3.7167, 34);
	public static final City melbourne = new City("Melbourne", 4087000, -37.8136, -144.9631, 61);
	public static final City sydney = new City("Sydney", 4293000, -33.8650, -151.2094, 61);
	
	public static List<String> commands = new ArrayList<String>();
	
	public static void main(final String[] args){
		
//    	if (args != null)
//			MONGOURL = args[0];
//		else
//		parseVcap();
		
		doEverything();
		for (String command : commands){
			System.out.print(command + "\n");
		}
	}
	
	public static void doEverything() throws MongoException{
		
		parseVcap();
		MongoClient conn = null;
		// This is the commands list that is printed out to the screen after all of the commands have been processed.
		// Here the Mongo client opens the connection to the server at the URL that you provide
		MongoClientURI uri = new MongoClientURI(MONGOURL);
		try {
		conn = new MongoClient(uri);
		
		} catch (Exception ex){
			ex.getMessage();
		}
		
		//1 Create collection
		commands.add("# 1.1 Create a collection");
		DB db = conn.getDB(databaseName);
		DBCollection collection = db.getCollection(collectionName1);
		DBCollection collectionJoin = db.getCollection(collectionName2);
		DBCollection sys = db.getCollection("system.join");
		
		//2 Create table
		commands.add("# 1.2 Create a table\n");
//		DBCollection cityTable = db.getCollection(tableName1);
		DBCollection cityTable = db.createCollection(tableName1, new BasicDBObject("columns", Arrays.asList(
				new BasicDBObject("name", "name").append("type", "varchar(50)"), 
				new BasicDBObject("name", "population").append("type", "int"),
				new BasicDBObject("name", "longitude").append("type", "decimal(8,4)"),
				new BasicDBObject("name", "latitude").append("type", "decimal(8,4)"),
				new BasicDBObject("name", "countryCode").append("type", "int")
				))); 
		
//		DBCollection codeTable = db.getCollection(tableName2);
		DBCollection codeTable = db.createCollection(tableName2, new BasicDBObject("columns", Arrays.asList(
				new BasicDBObject("name", "countryCode").append("type", "int"), 
				new BasicDBObject("name", "countryName").append("type", "varchar(50)")))); 

		//3 Inserts
		commands.add("# 2 Inserts");
		//3.1 Insert a single document
		commands.add("# 2.1 Insert a single document into a collection");
		// Creates the database object you will be inserting into the collection
		BasicDBObject insertSample = new BasicDBObject();
		insertSample.put("name", kansasCity.name);
		insertSample.put("population", kansasCity.population);
		insertSample.put("longitude", kansasCity.longitude);
		insertSample.put("latitude", kansasCity.latitude);
		insertSample.put("countryCode", kansasCity.countryCode);
		commands.add("\tInserting document: " + insertSample);
		collection.insert(insertSample);
		
		commands.add("\n");
		
		//3.2 Insert multiple documents
		commands.add("# 2.2 Insert multiple documents into a collection");
		// Creates the list of database objects you will be inserting into the collection
		List<DBObject> multiDocs = new ArrayList<>();
		List<DBObject> joinDocs = new ArrayList<>();
		
		BasicDBObject insertSamples1 = new BasicDBObject();
		insertSamples1.put("name", seattle.name);
		insertSamples1.put("population", seattle.population);
		insertSamples1.put("longitude", seattle.longitude);
		insertSamples1.put("latitude", seattle.latitude);
		insertSamples1.put("countryCode", seattle.countryCode);
		
		BasicDBObject insertSamples2 = new BasicDBObject();
		insertSamples2.put("name", newYork.name);
		insertSamples2.put("population", newYork.population);
		insertSamples2.put("longitude", newYork.longitude);
		insertSamples2.put("latitude", newYork.latitude);
		insertSamples2.put("countryCode", newYork.countryCode);
		
		BasicDBObject insertSamples3 = new BasicDBObject();
		insertSamples3.put("name", london.name);
		insertSamples3.put("population", london.population);
		insertSamples3.put("longitude", london.longitude);
		insertSamples3.put("latitude", london.latitude);
		insertSamples3.put("countryCode", london.countryCode);
		
		BasicDBObject insertSamples4 = new BasicDBObject();
		insertSamples4.put("name", tokyo.name);
		insertSamples4.put("population", tokyo.population);
		insertSamples4.put("longitude", tokyo.longitude);
		insertSamples4.put("latitude", tokyo.latitude);
		insertSamples4.put("countryCode", tokyo.countryCode);
		
		BasicDBObject insertSamples5 = new BasicDBObject();
		insertSamples5.put("name", madrid.name);
		insertSamples5.put("population", madrid.population);
		insertSamples5.put("longitude", madrid.longitude);
		insertSamples5.put("latitude", madrid.latitude);
		insertSamples5.put("countryCode", madrid.countryCode);
		
		BasicDBObject insertSamples6 = new BasicDBObject();
		insertSamples6.put("name", melbourne.name);
		insertSamples6.put("population", melbourne.population);
		insertSamples6.put("longitude", melbourne.longitude);
		insertSamples6.put("latitude", melbourne.latitude);
		insertSamples6.put("countryCode", melbourne.countryCode);
		
		BasicDBObject insertSamples7 = new BasicDBObject();
		insertSamples7.put("name", sydney.name);
		insertSamples7.put("population", sydney.population);
		insertSamples7.put("longitude", sydney.longitude);
		insertSamples7.put("latitude", sydney.latitude);
		insertSamples7.put("countryCode", sydney.countryCode);
		
		BasicDBObject joinSamples1 = new BasicDBObject();
		joinSamples1.put("countryCode", 1);
		joinSamples1.put("countryName", "United States of America");
		
		BasicDBObject joinSamples2 = new BasicDBObject();
		joinSamples2.put("countryCode", 44);
		joinSamples2.put("countryName", "United Kingdom");
		
		BasicDBObject joinSamples3 = new BasicDBObject();
		joinSamples3.put("countryCode", 81);
		joinSamples3.put("countryName", "Japan");
		
		BasicDBObject joinSamples4 = new BasicDBObject();
		joinSamples4.put("countryCode", 34);
		joinSamples4.put("countryName", "Spain");
		
		BasicDBObject joinSamples5 = new BasicDBObject();
		joinSamples5.put("countryCode", 61);
		joinSamples5.put("countryName", "Australia");

		commands.add("\tInserting documents: \n" );
		commands.add("\t" + insertSamples1);
		commands.add("\t" + insertSamples2);
		commands.add("\t" + insertSamples3);
		commands.add("\t" + insertSamples4);
		commands.add("\t" + insertSamples5);
		commands.add("\t" + insertSamples6);
		commands.add("\t" + insertSamples7);
		
		multiDocs.add(insertSamples1);
		multiDocs.add(insertSamples2);
		multiDocs.add(insertSamples3);
		multiDocs.add(insertSamples4);
		multiDocs.add(insertSamples5);
		
		joinDocs.add(joinSamples1);
		joinDocs.add(joinSamples2);
		joinDocs.add(joinSamples3);
		joinDocs.add(joinSamples4);
		joinDocs.add(joinSamples5);
		
		collection.insert(multiDocs);
		collectionJoin.insert(joinDocs);
		cityTable.insert(multiDocs);
		codeTable.insert(joinDocs);
		
		
		commands.add("\n");
		//3 Queries
		commands.add("# 3 Queries");
		//3.1 Find one document in a collection that matches query conditions
		// db.collection.find()
		// Finds the first instance of a document that satisfies the query specifications
		commands.add("# 3.1 Find one document in a collection that matches query conditions");
		DBObject doc3 = new BasicDBObject();
		BasicDBObject searchOneQuery = new BasicDBObject();
		searchOneQuery.put("population", new BasicDBObject("$lt",8000000));
		searchOneQuery.put("countryCode",1);
		commands.add("\tFinding one");
		doc3 = collection.findOne(searchOneQuery);
		commands.add("\tFound: " + doc3);
		commands.add("\n");
		
		//3.2 Find doucments in a coolection that match query conditions
		// db.collection.find()
		// Finds all of the documents that satisfy the query specifications
		commands.add("# 3.2 Find doucments in a coolection that match query conditions");
		List<DBObject> docs = new ArrayList<DBObject>();
		BasicDBObject searchAllQuery = new BasicDBObject();
		searchAllQuery.put("population", new BasicDBObject("$gt",8000000));
		searchAllQuery.put("longitude", new BasicDBObject("$gt", 40.0));
		commands.add("\tFinding all");
		DBCursor findCursor = collection.find(searchAllQuery);
		
		while (findCursor.hasNext()) {
			docs.add(findCursor.next());
		}
		findCursor.close();
		commands.add("\tFound: ");
		for (DBObject doc : docs){
			commands.add("\t" + doc);
		}
		commands.add("\n");
		
		//3.3 Find all documents in a collection
		commands.add("# 3.3 Find all documents in a collection");
		List<DBObject> allDocs = new ArrayList<DBObject>();
		DBCursor allCursor = collection.find();
		while (allCursor.hasNext()) {
			allDocs.add(allCursor.next());
		}
		allCursor.close();
		commands.add("\tDisplaying all documents: ");
		for (DBObject doc : allDocs){
			commands.add("\t" + doc);
		}
		commands.add("\n");
		
		//3.4 Count documents based off query
		commands.add("# 3.4 Count documents in a collection");
		BasicDBObject countQuery = new BasicDBObject();
		countQuery.put("longitude", new BasicDBObject("$lt", 40.0));
		commands.add("\tCounting users with longitude less than 40 N");
		long count = collection.getCount(countQuery);
		commands.add("\tDocuments in collection: " + count);
		commands.add("\n");
		
		//3.5 Orders documents
		commands.add("# 3.5 Order documents in a collection");
		BasicDBObject query = new BasicDBObject();
	    query.put("countryCode", 1);
	    DBCursor cursor = collection.find(query).sort(new BasicDBObject("population",-1));
	    commands.add("\tDocuments ordered by increasing population");
	    for (DBObject dbObject : cursor) {
	        commands.add("\t" + dbObject);
	    }
	    commands.add("\n");
	    
	    //3.6 Find distinct 
	    commands.add("# 3.6 Finding distinct ");
		BasicDBObject distinct = new BasicDBObject();
	    distinct.put("longitude", new BasicDBObject("$gt", 40.0));
	    //List type must match distinct return type
	    List<Integer> distinctFound = collection.distinct("countryCode", distinct);
	    for (Integer city : distinctFound) {
	        commands.add("\t" + city);
	    }
	    commands.add("\n");
	    
	    //3.7 Joins
	    commands.add("# 3.7a Join collection-collection");
	    BasicDBObject joinCollectionCollection = new BasicDBObject();
	    BasicDBObject joinProjection1 = new BasicDBObject();
	    joinProjection1.put("name", 1);
	    joinProjection1.put("population", 1);
	    joinProjection1.put("longitude", 1);
	    joinProjection1.put("latitude", 1);
	    joinCollectionCollection.put(collectionName1, new BasicDBObject("$project", joinProjection1));
	    
	    BasicDBObject joinProjection2 = new BasicDBObject();
	    joinProjection2.put("countryCode", 1);
	    joinProjection2.put("countryName", 1);
	    joinCollectionCollection.put(collectionName2, new BasicDBObject("$project",joinProjection2));
	    
		BasicDBObject join1 = new BasicDBObject("$collections",  joinCollectionCollection);
		BasicDBObject conditions = new BasicDBObject("cities.countryCode", "countries.countryCode");
		join1.put("$condition", conditions);
		
	
	    DBCursor joinCursor1 = sys.find(join1);
	    List<DBObject> joinDocs1 = new ArrayList<DBObject>();
	    while (joinCursor1.hasNext()) {
			joinDocs1.add(joinCursor1.next());
		}
		joinCursor1.close();
		
		commands.add("\tDisplaying joined collections: ");
		for (DBObject doc : joinDocs1){
			commands.add("\t" + doc);
		}
		
		commands.add("# 3.7b Join collection-table");
	    BasicDBObject joinCollectionTable = new BasicDBObject();
	    joinCollectionTable.put(tableName1, new BasicDBObject("$project", joinProjection1));
	    
	    joinCollectionTable.put(collectionName2, new BasicDBObject("$project",joinProjection2));
	    
		BasicDBObject join2 = new BasicDBObject("$collections",  joinCollectionTable);
		BasicDBObject conditions2 = new BasicDBObject("cityTable.countryCode", "countries.countryCode");
		join2.put("$condition", conditions2);
		
		commands.add("" + join2);
	    DBCursor joinCursor2 = sys.find(join2);
	    List<DBObject> joinDocs2 = new ArrayList<DBObject>();
	    while (joinCursor2.hasNext()) {
			joinDocs2.add(joinCursor2.next());
		}
		joinCursor2.close();
		
		commands.add("\tDisplaying joined collection-table: ");
		for (DBObject doc : joinDocs2){
			commands.add("\t" + doc);
		}
		
		commands.add("# 3.7c Join table-table");
	    BasicDBObject joinTableTable = new BasicDBObject();
	    joinTableTable.put(tableName2, new BasicDBObject("$project", joinProjection2));
	    joinTableTable.put(tableName1, new BasicDBObject("$project",joinProjection1));
	    
		BasicDBObject join3 = new BasicDBObject("$collections",  joinTableTable);
		BasicDBObject conditions3 = new BasicDBObject("cityTable.countryCode", "codeTable.countryCode");
		join3.put("$condition", conditions3);
		
	    DBCursor joinCursor3 = sys.find(join3);
	    List<DBObject> joinDocs3 = new ArrayList<DBObject>();
	    while (joinCursor3.hasNext()) {
			joinDocs3.add(joinCursor3.next());
		}
		joinCursor3.close();
		
		commands.add("\tDisplaying joined table-table: ");
		for (DBObject doc : joinDocs3){
			commands.add("\t" + doc);
		}
		commands.add("\n");
		
		//3.8 Change batch size
	    commands.add("# 3.8 Change batch size ");
	    List<DBObject> batchDocs = new ArrayList<DBObject>();
		DBCursor batchCursor = collection.find().batchSize(2);
		while (batchCursor.hasNext()) {
			batchDocs.add(batchCursor.next());
		}
		batchCursor.close();
		commands.add("\tDisplaying all documents with new batch size of 2: ");
		for (DBObject doc : batchDocs){
			commands.add("\t" + doc);
		}
		commands.add("\n");
		
	    //3.9 Projection clause
	    commands.add("# 3.9 Projection clause");
		BasicDBObject projectionQuery = new BasicDBObject();
	    projectionQuery.put("countryCode", 1);
	    BasicDBObject projectionFields = new BasicDBObject();
	    projectionFields.put("longitude", 0);
	    projectionFields.put("latitude", 0);
	    
	    List<DBObject> proj = new ArrayList<DBObject>();
	    DBCursor projectionCursor = collection.find(projectionQuery, projectionFields);
	    while (projectionCursor.hasNext()) {
			proj.add(projectionCursor.next());
		}
		projectionCursor.close();
		commands.add("\tDisplaying results without longitude and latitude: ");
		for (DBObject doc : proj){
			commands.add("\t" + doc);
		}
		commands.add("\n");
		
		// 4 Update a document
		// Allows you to modify documents in your collection based.
		// The search query is how you specify what to change and the new attribute is what you are modifying it to be 
		commands.add("# 4 Update documents in a collection");
		BasicDBObject doc2 = new BasicDBObject();
		int newValue = 999;
		doc2.put("countryCode", newValue);
		BasicDBObject update = new BasicDBObject();
		// Using '$set' allows you to keep the rest of the document the same and only update the attribute desired
		update.put("$set", doc2);
		BasicDBObject searchQuery = new BasicDBObject();
		searchQuery.put("name", seattle.name);
		commands.add("\tUpdating: " + searchQuery + " with countryCode " + newValue);
		collection.update(searchQuery, update);
		commands.add("\n");
	    
		// 5 Remove a document
		commands.add("# 5 Remove documents in a collection");
		BasicDBObject removeQuery = new BasicDBObject();
		String removeName = "Tokyo";
		removeQuery.put("name", removeName);
		commands.add("\tRemoving documents with the name " + removeName);
		collection.remove(removeQuery);
		commands.add("\tDocuments removed");
		
		//6 SQL Passthrough create
		commands.add("# 6 SQL Passthrough");
		DBCollection sql = db.getCollection("system.sql");
		DBObject sqlQuery = new BasicDBObject();
		sqlQuery.put("$sql", "create table town (name varchar(255), countryCode int)");
		DBCursor sqlCreate = sql.find(sqlQuery);
		List<DBObject> sqlCreateDocs = new ArrayList<DBObject>();
		while (sqlCreate.hasNext()) {
			sqlCreateDocs.add(sqlCreate.next());
		}
		sqlCreate.close();
		commands.add("\tTesting sql passthrough");
		for (DBObject doc : sqlCreateDocs){
			commands.add("\t" + doc);
		}
		
		// SQL Passthrough insert
		DBObject sqlQueryInsert = new BasicDBObject();
		sqlQueryInsert.put("$sql", "insert into town values('Lawrence', 1)");
		DBCursor sqlInsert = sql.find(sqlQueryInsert);
		List<DBObject> sqlInsertDocs = new ArrayList<DBObject>();
		while (sqlInsert.hasNext()) {
			sqlInsertDocs.add(sqlInsert.next());
		}
		sqlInsert.close();
		commands.add("\tDisplaying all documents: ");
		for (DBObject doc : sqlInsertDocs){
			commands.add("\t" + doc);
		}
		
		DBObject sqlQueryFind = new BasicDBObject();
		sqlQueryFind.put("$sql", "select * from town");
		DBCursor sqlFind = sql.find(sqlQueryFind);
		List<DBObject> sqlFindDocs = new ArrayList<DBObject>();
		while (sqlFind.hasNext()) {
			sqlFindDocs.add(sqlFind.next());
		}
		sqlFind.close();
		commands.add("\tTesting sql passthrough find");
		for (DBObject doc : sqlFindDocs){
			commands.add("\t" + doc);
		}
		
		// SQL Passthrough drop
		DBObject sqlQueryDrop = new BasicDBObject();
		sqlQueryDrop.put("$sql", "drop table town");
		DBCursor sqlDrop = sql.find(sqlQueryDrop);
		List<DBObject> sqlDropDocs = new ArrayList<DBObject>();
		while (sqlDrop.hasNext()) {
			sqlDropDocs.add(sqlDrop.next());
		}
		sqlDrop.close();
		commands.add("\tTesting sql passthrough drop");
		for (DBObject doc : sqlDropDocs){
			commands.add("\t" + doc);
		}
		
//		// Transactions
//		commands.add("\n#7 Testing Transactions");
//		
//		db.command(new BasicDBObject("transaction", "enable"));
//		collection.insert(insertSamples6);
//		
//		commands.add("Inserting " + insertSamples6);
//		List<DBObject> transactionDocs1 = new ArrayList<DBObject>();
//		DBCursor transactionCursor1 = collection.find();
//		while (transactionCursor1.hasNext()) {
//			transactionDocs1.add(transactionCursor1.next());
//		}
//		transactionCursor1.close();
//		commands.add("\tDisplaying all documents: ");
//		for (DBObject doc : transactionDocs1){
//			commands.add("\t" + doc);
//		}
//		commands.add("\n");
//		db.command(new BasicDBObject("transaction", "commit"));
//		
//		commands.add("Inserting " + insertSamples7);
//		collection.insert(insertSamples7);
//		commands.add("Transaction rollback before commit");
//		db.command(new BasicDBObject("transaction", "rollback"));
//
//		List<DBObject> transactionDocs2 = new ArrayList<DBObject>();
//		DBCursor transactionCursor2 = collection.find();
//		while (transactionCursor2.hasNext()) {
//			transactionDocs2.add(transactionCursor2.next());
//		}
//		transactionCursor2.close();
//		commands.add("\tDisplaying all documents: ");
//		for (DBObject doc : transactionDocs2){
//			commands.add("\t" + doc);
//		}
//
//		db.command(new BasicDBObject("transaction", "disable"));
//		commands.add("\n");
		
		
		commands.add("#8 Commands");
		// Document count
		commands.add("#8.1 Count documents in a collection ");
		long countCommand = collection.count();
		commands.add("\tDocuments in collection: " + countCommand);
		commands.add("\n"); 
		
	    // Find distinct 
	    commands.add("#8.2 Finding distinct ");
	    //List type must match distinct return type
	    List<Integer> anyDistinctFound = collection.distinct("countryCode");
	    for (Integer city : anyDistinctFound) {
	        commands.add("\t" + city);
	    }
	    commands.add("\n"); 
	    
	    // Collection stats
	    commands.add("#8.3 Collection stats:");
	    CommandResult collstats = collection.getStats();
	    commands.add("\t" + collstats);
	    commands.add("\n"); 
	    
	    //Db stats
	    commands.add("#8.4 Database stats:");
	    CommandResult dbstats = db.getStats();
	    Set<String> colls = db.getCollectionNames();
	    commands.add("\t" + colls);
	    commands.add("\t" + dbstats); 
	    commands.add("\n"); 
	    
		// Drop collection
		// Deletes the whole collection and everything in it
		commands.add("# 9 Drop a collection");
		commands.add("\tDropping collections " + collectionName1 + ", " + collectionName2 + ", " + tableName1 + ", " + tableName2);
		
		collection.drop();
		collectionJoin.drop();
		codeTable.drop();
		cityTable.drop();
		
		commands.add("\tCollection dropped");
		
		// close connection
		conn.close();
		
		return;
	}
	
	public static void parseVcap() {

		StringReader stringReader = new StringReader(System.getenv("VCAP_SERVICES"));
		JsonReader jsonReader = Json.createReader(stringReader);
		JsonObject vcap = jsonReader.readObject();
		JsonObject credentials = vcap.getJsonArray("altadb-dev").getJsonObject(0).getJsonObject("credentials");
		
		databaseName = credentials.getString("db");
		host = credentials.getString("host");
		username = credentials.getString("username");
		password = credentials.getString("password");
		boolean ssl = false;
		if (ssl) {
			MONGOURL = credentials.getString("ssl_json_url");
			port = credentials.getString("ssl_json_port");
		} else {
			MONGOURL = credentials.getString("json_url");
			port = credentials.getString("json_port");
		}
		
		System.out.println("URL -> " + MONGOURL);
		System.out.println("DB -> " + databaseName);
		System.out.println("host -> " + host);
		System.out.println("username -> " + username);
		System.out.println("password -> " + password);
		System.out.println("port -> " + port);

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
		return "Name: " + this.name + "   \tPopulation: " + this.population + "\tLongitude: " + this.longitude + 
				"\tLatitude: " + this.latitude  +  "\tCountry Code: " + this.countryCode;
	} 
}

