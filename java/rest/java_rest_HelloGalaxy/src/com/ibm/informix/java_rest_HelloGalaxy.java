/*-
 * Java Sample Application: Connection to Informix with REST
 */

/*-
 * Topics
 * 1 Data Structures
 * 1.1 Create Collection
 * 1.2 Create Table
 * 2 Inserts
 * 2.1 Insert a single document into a collection
 * 2.2 insert multiple documents into a collection
 * 3 Queries
 * 3.1 Find one document in a collection
 * 3.2 Find all documents in a collection
 * 3.3 Count documents in a collection
 * 3.4 Order documents in a collection
 * 3.5 Find distinct fields in a collection
 * 3.6 Joins
 * 3.6a Collection-Collection join
 * 3.6b Table-Collection join
 * 3.6c Table-Table join
 * 3.7 Modifying batch size
 * 3.8 Find with projection clause
 * 4 Update documents in a collection
 * 5 Delete documents in a collection
 * 6 SQL passthrough
 * 7 Transactions
 * 8 Catalog
 * 8.1 Collections + relational tables
 * 8.2 Collections + relational tables + system tables
 * 9 Commands
 * 9.1 collStats
 * 9.2 dbStats
 * 10 List all collections in a database
 * 11 Drop a collection
 */

package com.ibm.informix;

import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.sql.Savepoint;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonReader;

import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;

public class java_rest_HelloGalaxy {
	
	public static String URL = "https://yourURL:port/";
	public static List<String> commands = new ArrayList<String>();
	public static List<JsonObject> itemsToPost = new ArrayList<JsonObject>();
	public static List<Query> queries = new ArrayList<Query>();
	
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
    	//if (args[0] != null)
		//URL = args[0];
		//else
		//parseVcap();
		
		doEverything();
		
		//print log
		for (String command : commands)
			System.out.println(command);

	}

	public static List<String> doEverything() {
		
		String reply = "";
		String db = "dbTest";
		String collection = "mycollection";
		String collectionJoin = "collectionjoin";
		String table = "mytable";
		String tableJoin = "tablejoin";
		//get access to get, post, put, delete methods
		REST restAPI = new REST("username", "password");
		
		
		commands.add("Connected to: " + URL);
		commands.add("\nTopics");
		
		//1 Data Structures
		commands.add("\nData Structures");
		
		//1.1 Create a collection
		commands.add("1 Create a collection");
		
		JsonObject createCollection = Json.createObjectBuilder()
				.add("name", collection).build();
		itemsToPost.clear();
		itemsToPost.add(createCollection);
		reply = restAPI.post(URL + db, itemsToPost);
		
		commands.add("\tCollection: " + reply);
		//<------------------------------------->
		
		//1.2 Create a table
		commands.add("\n1.2 Create a table");  
		
		JsonObject createTable = 
				Json.createObjectBuilder()
				.add("name", table)
				.add("columns", 
					Json.createArrayBuilder()
					.add(
						Json.createObjectBuilder()
						.add("name","name")
						.add("type","varchar(50)")
						.build())
					.add(
						Json.createObjectBuilder()
						.add("name","population")
						.add("type","int")
						.build())
					.add(
						Json.createObjectBuilder()
						.add("name","longitude")
						.add("type","Deicmal(8,4)")
						.build())
					.add(
						Json.createObjectBuilder()
						.add("name","latitude")
						.add("type","Deicmal(8,4)")
						.build())
					.add(
						Json.createObjectBuilder()
						.add("name","code")
						.add("type","int")
						.build())
					.build())
				.build();
		itemsToPost.clear();
		itemsToPost.add(createTable);
		reply = restAPI.post(URL + db, itemsToPost);
		
		commands.add("\tTable: " + reply);
		//<------------------------------------->
		
		//2 Inserts
		commands.add("\n2 Inserts");
		
		//2.1 Insert a single document into a collection
		commands.add("2.1 Insert a single document into a collection");
		
		itemsToPost.clear();
		itemsToPost.add(kansasCity.toJson());
		reply = restAPI.post(URL + db + "/" + collection, itemsToPost);
		reply = restAPI.post(URL + db + "/" + table, itemsToPost);
		
		commands.add("\tSingle Insert Document: "
				+ kansasCity.toJson());
		commands.add("\tCreate Single Document: "
				+ reply);
		//<------------------------------------->

		//2.2 Insert multiple documents into a collection
		commands.add("2.2 Insert multiple documents into a collection");
		
		itemsToPost.clear();
		itemsToPost.add(seattle.toJson());
		itemsToPost.add(newYork.toJson());
		itemsToPost.add(london.toJson());
		itemsToPost.add(tokyo.toJson());
		itemsToPost.add(madrid.toJson());
		itemsToPost.add(melbourne.toJson());
		reply = restAPI.post(URL + db + "/" + collection, itemsToPost);
		reply = restAPI.post(URL + db + "/" + table, itemsToPost);
		
		for (JsonObject city : itemsToPost) {
			commands.add("\tMultiple Insert Document -> " + city.toString());
		}
		commands.add("\tCreate Multiple Documents: "
				+ reply);
		//<------------------------------------->
		
		//3 Queries
		commands.add("\n3 Queries");
		
		//3.1 Find documents in a collection that match a query condition
		commands.add("3.1 Find documents in a collection that match a query condition");
		//url/dbTest/mycollection?query={"population":{"$gt":8000000},"code":1}&fields={_id:0}
		
		Query population = new Query();
		population.setQueryType("query"); //defaults to query
		JsonObject queryValue = 
				Json.createObjectBuilder()
				.add("population", 
					Json.createObjectBuilder()
					.add("$gt",8000000)
					.build())
				.add("code", 1)
				.build();
		population.setQueryValue(queryValue);
		queryValue = 
				Json.createObjectBuilder()
					.add("_id", 0)
					.build();
		Query fields = new Query("fields", queryValue);
		queries.clear();
		queries.add(population);
		queries.add(fields);
		reply = restAPI.get(URL + db + "/" + collection, queries);
		
		for (Query query : queries)
		commands.add("\tDocument Query -> " + query.toString());
		commands.add("\tList Document: "
				+ reply);
		//<------------------------------------->
		
		//3.2 Find all documents in a collection
		commands.add("3.2 Find all documents in a collection");
		//url/dbTest/mycollection
		
		reply = restAPI.get(URL + db + "/" + collection, null);
		
		commands.add("\tList all Documents: "
						+ reply);
		//<------------------------------------->
		
		//3.3 Count documents in a collection
		commands.add("\n3.3 Count documents in a collection");
		//url/dbTest/$cmd?query={"count":"mycollection",query:{"longitude":{"$lt":40.0}}}
		
		Query count = new Query();
		queryValue = Json.createObjectBuilder()
				.add("count", collection)
				.add("query", 
					Json.createObjectBuilder()
					.add("longitude", 
						Json.createObjectBuilder()
						.add("$lt", 40.0)
						.build())
					.build())
				.build();
		count.setQueryValue(queryValue);
		queries.clear();
		queries.add(count);
		reply = restAPI.get(URL + db + "/$cmd", queries);
		
		commands.add("\tNumber of documents: " + reply);
		//<------------------------------------->
		
		//3.4 Order documents in a collection
		commands.add("\n3.4 Order documents in a collection");
		//url/dbTest/mycollection?sort={"population":1}
		
		Query sort = new Query("sort", Json.createObjectBuilder().add("population",1).build());
		queries.clear();
		queries.add(sort);
		reply = restAPI.get(URL + db + "/" + collection, queries);
		
		commands.add("\tSorted Documents: " + reply);
		//<------------------------------------->
		
		//3.5 Find distinct fields in a collection
		commands.add("\n3.5 Find distinct fields in a collection");
		//url/dbTest/$cmd?query={"distinct":"mycollection","key":"code",query:{"longitude":{"$gt":40.0}}}
		
		Query distinct = new Query();
		queryValue = Json.createObjectBuilder()
				.add("distinct", collection)
				.add("key", "code")
				.add("query" , 
					Json.createObjectBuilder()
					.add("longitude", 
						Json.createObjectBuilder()
						.add("$gt", 40.0)
						.build())
					.build())
				.build();
		distinct.setQueryValue(queryValue);
		
		queries.clear();
		queries.add(distinct);
		reply = restAPI.get(URL + db + "/$cmd", queries);
		
		commands.add("\tDistinct Documents: " + reply);
		//<------------------------------------->
		
		//3.6 Join
		commands.add("\n3.6 Join");
		
		//create collection to join and add data
		JsonObject createCollectionJoin = Json.createObjectBuilder()
				.add("name", collectionJoin).build();
		itemsToPost.clear();
		itemsToPost.add(createCollectionJoin);
		reply = restAPI.post(URL + db, itemsToPost);
		itemsToPost.clear();
		itemsToPost.add(Json.createObjectBuilder().add("countryCode", 1).add("country", "United States of America").build());
		itemsToPost.add(Json.createObjectBuilder().add("countryCode", 44).add("country", "United Kingdom").build());
		itemsToPost.add(Json.createObjectBuilder().add("countryCode", 81).add("country", "Japan").build());
		itemsToPost.add(Json.createObjectBuilder().add("countryCode", 34).add("country", "Spain").build());
		itemsToPost.add(Json.createObjectBuilder().add("countryCode", 61).add("country", "Australia").build());
		reply = restAPI.post(URL + db + "/" + collectionJoin, itemsToPost);
		
		
		//create table to join and add data
		JsonObject createTableJoin = 
				Json.createObjectBuilder()
				.add("name", tableJoin)
				.add("columns", 
					Json.createArrayBuilder()
					.add(
						Json.createObjectBuilder()
						.add("name","countryCode")
						.add("type","int")
						.build())
					.add(
						Json.createObjectBuilder()
						.add("name","country")
						.add("type","varchar(50)")
						.build())
					.build())
				.build();
		itemsToPost.clear();
		itemsToPost.add(createTableJoin);
		reply = restAPI.post(URL + db, itemsToPost);
		itemsToPost.clear();
		itemsToPost.add(Json.createObjectBuilder().add("countryCode", 1).add("country", "United States of America").build());
		itemsToPost.add(Json.createObjectBuilder().add("countryCode", 44).add("country", "United Kingdom").build());
		itemsToPost.add(Json.createObjectBuilder().add("countryCode", 81).add("country", "Japan").build());
		itemsToPost.add(Json.createObjectBuilder().add("countryCode", 34).add("country", "Spain").build());
		itemsToPost.add(Json.createObjectBuilder().add("countryCode", 61).add("country", "Australia").build());
		reply = restAPI.post(URL + db + "/" + tableJoin, itemsToPost);
		
		//3.6a Collection-Collection join
		commands.add("3.6a Collection-Collection join");
		//url/dbTest/system.join?query={"$collections":{"mycollection":{"$project":{"name":1,"population":1,"longitude":1,"latitude":1}},
		Query joinCollections = new Query();
		queryValue = Json.createObjectBuilder()
				.add("$collections", 
					Json.createObjectBuilder()
					.add(collection, 
						Json.createObjectBuilder()
						.add("$project", 
							Json.createObjectBuilder()
							.add("name", 1)
							.add("population", 1)
							.add("longitude", 1)
							.add("latitude", 1)
							.build())
						.build())
					.add(collectionJoin, 
						Json.createObjectBuilder()
						.add("$project", 
							Json.createObjectBuilder()
							.add("countryCode", 1)
							.add("country", 1)
							.build())
						.build()))
				.add("$condition", 
					Json.createObjectBuilder()
					.add(collection + ".code", collectionJoin + ".countryCode")
					.build())
				.build();
		
		joinCollections.setQueryValue(queryValue);
		queries.clear();
		queries.add(joinCollections);
		reply = restAPI.get(URL + db + "/system.join" , queries);
		
		commands.add("\tJoin collections: " + collection + " and " + collectionJoin);
		commands.add("\tJoined Documents: " + reply);
		
		//3.6b Table-Collection join
		commands.add("\n3.6b Table-Collection join");
		//url/dbTest/system.join?query={"$collections":{"mytable":{"$project":{"name":1,"population":1,"longitude":1,"latitude":1}},"collectionjoin":{"$project":{"countryCode":1,"country":1}}},"$condition":{"mytable.code":"collectionjoin.countryCode"}}

		Query joinTableWithCol = new Query();
		queryValue = Json.createObjectBuilder()
				.add("$collections", 
					Json.createObjectBuilder()
					.add(table, 
						Json.createObjectBuilder()
						.add("$project", 
							Json.createObjectBuilder()
							.add("name", 1)
							.add("population", 1)
							.add("longitude", 1)
							.add("latitude", 1)
							.build())
						.build())
					.add(collectionJoin, 
						Json.createObjectBuilder()
						.add("$project", 
							Json.createObjectBuilder()
							.add("countryCode", 1)
							.add("country", 1)
							.build())
						.build()))
				.add("$condition", 
					Json.createObjectBuilder()
					.add(table + ".code", collectionJoin + ".countryCode")
					.build())
				.build();
		
		joinTableWithCol.setQueryValue(queryValue);
		queries.clear();
		queries.add(joinTableWithCol);
		reply = restAPI.get(URL + db + "/system.join" , queries);
		
		commands.add("\tJoin table with collection: " + table + " and " + collectionJoin);
		commands.add("\tJoined Documents: " + reply);
		
		//3.6c Table-Table join
		commands.add("\n3.6c Table-Table join");
		//url/dbTest/system.join?query={"$collections":{"mytable":{"$project":{"name":1,"population":1,"longitude":1,"latitude":1}},"tablejoin":{"$project":{"countryCode":1,"country":1}}},"$condition":{"mytable.code":"tablejoin.countryCode"}}
		
		Query joinTableWithTable = new Query();
		queryValue = Json.createObjectBuilder()
				.add("$collections", 
					Json.createObjectBuilder()
					.add(table, 
						Json.createObjectBuilder()
						.add("$project", 
							Json.createObjectBuilder()
							.add("name", 1)
							.add("population", 1)
							.add("longitude", 1)
							.add("latitude", 1)
							.build())
						.build())
					.add(tableJoin, 
						Json.createObjectBuilder()
						.add("$project", 
							Json.createObjectBuilder()
							.add("countryCode", 1)
							.add("country", 1)
							.build())
						.build()))
				.add("$condition", 
					Json.createObjectBuilder()
					.add(table + ".code", tableJoin + ".countryCode")
					.build())
				.build();
		
		joinTableWithTable.setQueryValue(queryValue);
		queries.clear();
		queries.add(joinTableWithTable);
		reply = restAPI.get(URL + db + "/system.join" , queries);
		
		commands.add("\tJoin tables: " + table + " and " + tableJoin);
		commands.add("\tJoined Documents: " + reply);
		//<------------------------------------->
		
		//3.7 Modifying batch size
		commands.add("\n3.7 Modifying batch size");
		//url/dbTest/mycollection?batchSize=2
		
		Query batchSize = new Query("batchSize", 2);
		queries.clear();
		queries.add(batchSize);
		reply = restAPI.get(URL + db + "/" + collection, queries);
		
		commands.add("\tChange batch size");
		commands.add("\tFound documents: " + reply);
		
		//<------------------------------------->
		
		//3.8 Find with projection clause
		commands.add("\n3.8 Find with projection clause");
		//url/dbTest/mycollection?fields={"name":1,"code":1,"_id":0}&query={"population":{"$gt":8000000}}
		
		queryValue = Json.createObjectBuilder().add("name", 1).add("code", 1).add("_id", 0).build();
		Query projection = new Query("fields", queryValue);
		queryValue = Json.createObjectBuilder().add("population", Json.createObjectBuilder().add("$gt",8000000).build()).build();
		Query projectionQuery = new Query("query", queryValue);
		queries.clear();
		queries.add(projection);
		queries.add(projectionQuery);
		reply = restAPI.get(URL + db + "/" + collection, queries);
		
		commands.add("\tFind with projection: " + projection.toString());
		commands.add("\tFound Document: " + reply);
		
		//<------------------------------------->
		
		//4 Update documents in a collection
		commands.add("\n4 Update documents in a collection");
		
		JsonObject updateDocument = 
				Json.createObjectBuilder()
				.add("name", "Seattle")
				.add("population", 652405)
				.add("longitude", 47.6097)
				.add("latitude", 122.3331)
				.add("code", 999)
				.build();
		Query nameQuery = new Query("query", Json.createObjectBuilder().add("name", "Seattle").build());
		queries.clear();
		queries.add(nameQuery);
		reply = restAPI.put(URL + db + "/" + collection, updateDocument, queries);
		
		for (Query query : queries)
		commands.add("\tDocument Query -> " + query.toString());
		commands.add("\tUpdate Document: " 
				+ updateDocument.toString());
		commands.add("\tUpdate Document: "
				+ reply);
		//<------------------------------------->
		
		//5 Delete documents in a collection
		commands.add("\n5 Delete documents in a collection");
		
		nameQuery.setQueryValue(Json.createObjectBuilder().add("name", "Tokyo").build());		
		queries.clear();
		queries.add(nameQuery);
		reply = restAPI.delete(URL + db + "/" + collection, queries);
		
		for (Query query : queries)
		commands.add("\tDocument Query: " + query.toString());
		commands.add("\tDelete Document: "+ reply);
		//<------------------------------------->
		
		//6 SQL passthrough
		commands.add("\n6 SQL passthrough");

		Query sqlCreate = new Query();
		Query sqlInsert = new Query();
		Query sqlSelect = new Query();
		Query sqlDrop = new Query();
		queryValue = Json.createObjectBuilder().add("$sql", "create table if not exists town (name varchar(50),countryCode int)").build();
		sqlCreate.setQueryValue(queryValue);
		commands.add(sqlCreate.toString());
		queryValue = Json.createObjectBuilder().add("$sql", "insert into town values ('Manhattan', 1)").build();
		sqlInsert.setQueryValue(queryValue);
		queryValue = Json.createObjectBuilder().add("$sql", "select * from town").build();
		sqlSelect.setQueryValue(queryValue);
		queryValue = Json.createObjectBuilder().add("$sql", "drop table town").build();
		sqlDrop.setQueryValue(queryValue);
		
		//must urlencode without spaces
		//REST.java line 43... sketchy
		//url/dbTest/system.sql?query={"$sql":"create table if not exists town (name varchar(255), countryCode int)"}
		queries.clear();
		queries.add(sqlCreate);
		reply = restAPI.get(URL + db + "/system.sql", queries);
		commands.add("\tSQL Create: " + reply);
		
		//url/dbTest/system.sql?query={"$sql":"insert into town values ('Manhattan', 1)"}
		queries.clear();
		queries.add(sqlInsert);
		reply = restAPI.get(URL + db + "/system.sql", queries);
		commands.add("\tSQL Insert: " + reply);
		
		//url/dbTest/system.sql?query={"$sql":"select * from town"}
		queries.clear();
		queries.add(sqlSelect);
		reply = restAPI.get(URL + db + "/system.sql", queries);
		commands.add("\tSQL Select: " + reply);
		
		//url/dbTest/system.sql?query={"$sql":"drop table town"}
		queries.clear();
		queries.add(sqlDrop);
		reply = restAPI.get(URL + db + "/system.sql", queries);
		commands.add("\tSQL Drop: " + reply);
		//<------------------------------------->
		
		//7 Transactions
		commands.add("\n7 Transactions");

		//6 Transactions
		
		//transaction start
		//url/dbTest/$cmd?query={transaction:"enable"}
		Query transactionStart = new Query("query", Json.createObjectBuilder().add("transaction", "enable").build());
		queries.clear();
		queries.add(transactionStart);
		reply = restAPI.get(URL + db +"/$cmd?", queries);
		
		commands.add("\tStart tranaction... " + reply);
		
		//transaction insert
		itemsToPost.clear();
		itemsToPost.add(sydney.toJson());
		reply = restAPI.post(URL + db + "/" + collection, itemsToPost);
		
		commands.add("\tInsert Document: " + reply);
		
		//transaction update
		JsonObject transactionUpdate = 
				Json.createObjectBuilder()
				.add("name", "Seattle")
				.add("population", 652405)
				.add("longitude", 47.6097)
				.add("latitude", 122.3331)
				.add("code", 998)
				.build();
		nameQuery.setQueryValue(Json.createObjectBuilder().add("name", "Seattle").build());
		queries.clear();
		queries.add(nameQuery);
		reply = restAPI.put(URL + db + "/" + collection, transactionUpdate, queries);
		commands.add("\tUpdate Document: " + reply);
		
		//transaction commit
		//url/dbTest/$cmd?query={transaction:"commit"}
		Query transactionCommit = new Query("query", Json.createObjectBuilder().add("transaction", "commit").build());
		queries.clear();
		queries.add(transactionCommit);
		reply = restAPI.get(URL + db +"/$cmd?", queries);
		commands.add("\tCommit Changes... " + reply);
		
		//transaction delete
		nameQuery.setQueryValue(Json.createObjectBuilder().add("name", "Sydney").build());		
		queries.clear();
		queries.add(nameQuery);
		reply = restAPI.delete(URL + db + "/" + collection, queries);
		commands.add("\tDelete Document: " + reply);
		
		//transaction rollback
		//url/dbTest/$cmd?query={transaction:"rollback"}
		Query transactionRollback = new Query("query", Json.createObjectBuilder().add("transaction", "rollback").build());
		queries.clear();
		queries.add(transactionRollback);
		reply = restAPI.get(URL + db +"/$cmd?", queries);
		commands.add("\tRollback Changes... " + reply);
		
		//transaction status
		//url/dbTest/$cmd?query={transaction:"status"}
		Query transactionStatus = new Query("query", Json.createObjectBuilder().add("transaction", "status").build());
		queries.clear();
		queries.add(transactionStatus);
		reply = restAPI.get(URL + db +"/$cmd?", queries);
		commands.add("\tTransaction Status: " + reply);
		
		//transaction end
		//url/dbTest/$cmd?query={transaction:"disable"}
		Query transactionEnd = new Query("query", Json.createObjectBuilder().add("transaction", "disable").build());
		queries.clear();
		queries.add(transactionEnd);
		reply = restAPI.get(URL + db +"/$cmd?", queries);
		commands.add("\tTransaction End: " + reply);
		// <------------------------------------->
		
		//8 Catalog
		commands.add("\n8 Catalog");
		//8.1 Collections + relational tables
		commands.add("\n8.1 Collections + relational tables");
		//url/dbTest?options={includeRelational:true}
		
		Query catalog = new Query("options", Json.createObjectBuilder().add("includeRelational", true).build());
		queries.clear();
		queries.add(catalog);
		reply = restAPI.get(URL + db, queries);
		
		commands.add("\tCatalog: " + reply);
		//<------------------------------------->
		
		//8.2 Collections + relational tables + system tables
		commands.add("\n8.2 Collections + relational tables + system tables");
		//url/dbTest?options={includeRelational:true, includeSystem:true}
		
		catalog.setQueryValue(Json.createObjectBuilder().add("includeRelational", "true").add("includeSystem", true).build());
		queries.clear();
		queries.add(catalog);
		reply = restAPI.get(URL + db, queries);
		
		commands.add("\tCatalog: " + reply);
		//<------------------------------------->
		
		//9 Commands
		commands.add("\n9 Commands");
		//9.1 collStats
		commands.add("\n9.1 collStats");
		//url/dbTest/$cmd?query={collStats:"mycollection"}
		Query commandQuery = new Query("query", Json.createObjectBuilder().add("collStats", collection).build());
		queries.clear();
		queries.add(commandQuery);
		reply = restAPI.get(URL + db +"/$cmd?", queries);
		commands.add("\tcollStats: " + reply);
		//<------------------------------------->
		
		//9.2 dbStats
		commands.add("\n9.2 dbStats");
		//url/dbTest/$cmd?query={dbStats:1}
		
		commandQuery.setQueryValue(Json.createObjectBuilder().add("dbStats", 1).build());
		queries.clear();
		queries.add(commandQuery);
		reply = restAPI.get(URL + db +"/$cmd?", queries);
		commands.add("\tdbStats: " + reply);
		//<------------------------------------->
				
		//10 List all collections in a database
		commands.add("\n10 List all collections in a database");
		
		reply = restAPI.get(URL + db, null);
		
		commands.add("\tList all Collections: " + reply);
		//<------------------------------------->
		
		//11 Drop a collection
		commands.add("\n11 Drop a collection");
		
		reply = restAPI.delete(URL + db + "/" + collection, null);
		
		commands.add("\tDelete Collection: " + reply);
		
		reply = restAPI.delete(URL + db + "/" + collectionJoin, null);
		
		commands.add("\tDelete Collection: " + reply);
		
		reply = restAPI.delete(URL + db + "/" + table, null);
				
		commands.add("\tDelete table: " + reply);
		
		reply = restAPI.delete(URL + db + "/" + tableJoin, null);
		
		commands.add("\tDelete table: " + reply);
		//<------------------------------------->
		
		return commands;
	}
	
	public static void parseVcap() {

		StringReader stringReader = new StringReader(
				System.getenv("VCAP_SERVICES"));
		JsonReader jsonReader = Json.createReader(stringReader);
		JsonObject vcap = jsonReader.readObject();
		System.out.println("vcap: " + vcap);
		boolean ssl = false;
		if (ssl)
			URL = vcap.getJsonArray("clouddb-dev").getJsonObject(0)
					.getJsonObject("credentials").getString("ssl_rest_url");
		else
			URL = vcap.getJsonArray("clouddb-dev").getJsonObject(0)
					.getJsonObject("credentials").getString("rest_url");
		System.out.println(URL);

	}

}