package com.ibm.informix;
/*-
 * Java Sample Application: Connection to Informix using Mongo
 */

//Topics
//1 Inserts
//1.1 Insert a single document into a collection
//1.2 Insert multiple documents into a collection
//2 Queries
//2.1 Find one document in a collection that matches a query condition  
//2.2 Find documents in a collection that match a query condition
//2.3 Find all documents in a collection
//3 Update documents in a collection
//4 Delete documents in a collection
//5 Drop a collection

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.MongoException;


public class java_mongo_HelloWorld {
	public static String MONGOURL;
	public static String databaseName;
	public static String host;
	public static String username;
	public static String password;
	public static String port;
	public static final String collectionName = "javaTest";
	public static final DataFormat user1 = new DataFormat("test1", 1);
	public static final DataFormat user2 = new DataFormat("test2", 2);
	public static final DataFormat user3 = new DataFormat("test3", 3);
	
	public static List<String> commands = new ArrayList<String>();

	public static void main(final String[] args){
		
//    	if (args != null)
//			MONGOURL = args[0];
//		else

//		parseVcap();
		doEverything();
		
		//print log

		for (String command : commands){
			System.out.print(command + "\n");
		}
	}
	
	public static void doEverything() throws MongoException {
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
		
		// This is the collection that you will be working with throughout the program.
		DB db = conn.getDB(databaseName);
		DBCollection collection = db.getCollection(collectionName);
		commands.add("Connected to: " + MONGOURL);
		
		commands.add("\nTopics");
		
		//1 Inserts
		commands.add("\n1 Inserts");
		//1.1 Insert a single document into a collection
		commands.add("1.1 Insert a single document into a collection");
		
		BasicDBObject insertSample = new BasicDBObject();
		insertSample.put("name", user1.name);
		insertSample.put("value", user1.value);
		collection.insert(insertSample);
		
		commands.add("\tInserting document: " + insertSample);
		
		//1.2 Insert multiple documents into a collection
		commands.add("1.2 Insert multiple documents into a collection");
		
		List<DBObject> multiDocs = new ArrayList<>();
		BasicDBObject insertSamples1 = new BasicDBObject();
		insertSamples1.put("name", user1.name);
		insertSamples1.put("value", user1.value);
		BasicDBObject insertSamples2 = new BasicDBObject();
		insertSamples2.put("name", user2.name);
		insertSamples2.put("value", user2.value);
		BasicDBObject insertSamples3 = new BasicDBObject();
		insertSamples3.put("name", user3.name);
		insertSamples3.put("value", user3.value);
		multiDocs.add(insertSamples1);
		multiDocs.add(insertSamples2);
		multiDocs.add(insertSamples3);
		collection.insert(multiDocs);
		
		commands.add("\tInserting document: -> " + insertSamples1);
		commands.add("\tInserting document: -> " + insertSamples2);
		commands.add("\tInserting document: -> " + insertSamples3);
		
		commands.add("\n2 Queries");

		//2.1 Find one document in a collection that matches a query condition
		commands.add("2.1 Find one document in a collection that matches a query condition");
		
		DBObject doc3 = new BasicDBObject();
		BasicDBObject searchOneQuery = new BasicDBObject();
		searchOneQuery.put("name", user1.name);
		doc3 = collection.findOne(searchOneQuery);
		
		commands.add("\tFinding one with name " + user1.name);
		commands.add("\tFound: " + doc3);
		
		//2.2 Find documents in a collection that match a query condition
		commands.add("2.2 Find documents in a collection that match a query condition");
		List<DBObject> docs = new ArrayList<DBObject>();
		BasicDBObject searchAllQuery = new BasicDBObject();
		searchAllQuery.put("name", user1.name);
		DBCursor findCursor = collection.find(searchAllQuery);
		while (findCursor.hasNext()) {
			docs.add(findCursor.next());
		}
		findCursor.close();
		
		commands.add("\tFinding all with name: " + user1.name);
		for (DBObject doc : docs)
			commands.add("\tFound -> " + doc);
		
		
		//2.3 Find all documents in a collection
		commands.add("2.3 Find all documents in a collection");
		
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
		
		//3 Update documents in a collection
		commands.add("\n3 Update documents in a collection");
		
		BasicDBObject doc2 = new BasicDBObject();
		int newValue = 5;
		doc2.put("value", newValue);
		BasicDBObject update = new BasicDBObject();
		// Using '$set' allows you to keep the rest of the document the same and only update the attribute desired
		update.put("$set", doc2);
		BasicDBObject searchQuery = new BasicDBObject();
		searchQuery.put("name", user2.name);
		collection.update(searchQuery, update);
		
		commands.add("\tUpdating: " + searchQuery + " with value: " + newValue);
		
		//4 Remove documents in a collection
		commands.add("\n4 Remove documents in a collection");
		
		BasicDBObject removeQuery = new BasicDBObject();
		String removeName = "test2";
		removeQuery.put("name", removeName);
		collection.remove(removeQuery);
		
		commands.add("\tRemoving documents with the name: " + removeName);
		commands.add("\tDocuments removed");
		
		//5 Drop a collection
		commands.add("\n5 Drop a collection");
		
		collection.drop();
		
		commands.add("\tDropping collection " + collectionName);
		commands.add("\tCollection dropped");
		
		// close connection
		commands.add("\nComplete!");
		conn.close();
		
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

//		StringReader stringReader = new StringReader(
//				System.getenv("VCAP_SERVICES"));
//		JsonReader jsonReader = Json.createReader(stringReader);
//		JsonObject vcap = jsonReader.readObject();
//		System.out.println("vcap: " + vcap);
//		boolean ssl = true;
//		
//		if (ssl) {
//			MONGOURL = vcap.getJsonArray("altadb-dev").getJsonObject(0)
//					.getJsonObject("credentials").getString("ssl_json_url");
//			databaseName = vcap.getJsonArray("altadb-dev").getJsonObject(0)
//				.getJsonObject("credentials").getString("db");
//		}
//		else {
//			MONGOURL = vcap.getJsonArray("altadb-dev").getJsonObject(0)
//					.getJsonObject("credentials").getString("json_url");
//			databaseName = vcap.getJsonArray("altadb-dev").getJsonObject(0)
//					.getJsonObject("credentials").getString("db");
//			System.out.println(MONGOURL);
//		}
			

	}

}

class DataFormat {
	public final String name;
	public final int value;
	
	public DataFormat (String name, int value) {
		this.name = name;
		this.value = value;
	}
	
	public String toString(){
		return "name: " + this.name + " value: " + this.value + " ";
	}
}
