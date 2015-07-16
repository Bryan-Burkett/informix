package com.ibm.informix;
/*-
 * Java Sample Application: Connection to Informix with REST
 */

/*-
 * Topics
 * 1 Create a collection
 * 2 Inserts
 * 2.1 Insert a single document into a collection
 * 2.2 Insert multiple documents into a collection
 * 3 Queries
 * 3.1 Find documents in a collection that match a query condition
 * 3.2 Find all documents in a collection
 * 4 Update documents in a collection
 * 5 Delete documents in a collection
 * 6 List all collections in a database
 * 7 Drop a collection
 */

import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
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


public class java_rest_HelloWorld {
	
	public static String URL;// = "http://qkkamrhs:xplswenfpqbe@184.173.63.222:80/s8873642";
	public static List<String> commands = new ArrayList<String>();
	
	public static void main(String[] args) {
		
//    	if (args[0] != null)
//		URL = args[0];
//		else
//		parseVcap();

		doEverything();
		
		//print log
		for (String command : commands)
			System.out.println(command);

	}

	public static List<String> doEverything() {
		parseVcap();
		String reply = "";
		//get access to get,post,put,delete methods
		REST restAPI = new REST("username", "password");
		
		
		commands.add("Connected to: " + URL);
		commands.add("\nTopics");
		
		
		//1 Create a collection
		commands.add("\n1 Create a collection");
		
		JsonObject createCollection = Json.createObjectBuilder()
				.add("name", "mycollection").build();
		reply = restAPI.post(URL + "dbTest/", createCollection);
		
		commands.add("\tCollection: "
				+ reply);
		//<------------------------------------->
		
		//2 Inserts
		commands.add("\n2 Inserts");
		
		//2.1 Insert a single document into a collection
		commands.add("2.1 Insert a single document into a collection");

		JsonObject createSingleInsert = Json.createObjectBuilder()
				.add("firstname", "user1").add("lastname", "name1")
				.add("number", 1).build();
		reply = restAPI.post(URL + "dbTest/mycollection", createSingleInsert);
		
		commands.add("\tSingle Insert Document: "
				+ createSingleInsert.toString());
		commands.add("\tCreate Single Document: "
				+ reply);
		//<------------------------------------->

		//2.2 Insert multiple documents into a collection
		commands.add("2.2 Insert multiple documents into a collection");
		
		JsonObject createMultipleInsertOne = Json.createObjectBuilder()
				.add("firstname", "user2").add("lastname", "name2")
				.add("number", 2).build();
		JsonObject createMultipleInsertTwo = Json.createObjectBuilder()
				.add("firstname", "user3").add("lastname", "name3")
				.add("number", 3).build();
		reply = restAPI.post(URL + "dbTest/mycollection",
				createMultipleInsertOne, createMultipleInsertTwo);
		
		commands.add("\tMultiple Insert Document: "
				+ createMultipleInsertOne.toString());
		commands.add("\tMultiple Insert Document: "
				+ createMultipleInsertTwo.toString());
		commands.add("\tCreate Multiple Documents: "
				+ reply);
		//<------------------------------------->
		
		//3 Queries
		commands.add("\n3 Queries");
		
		//3.1 Find documents in a collection that match a query condition
		commands.add("3.1 Find documents in a collection that match a query condition");
		
		JsonObject listDocumentQuery = Json.createObjectBuilder()
				.add("number", 3).build();
		reply = restAPI.get(URL + "dbTest/mycollection", listDocumentQuery);
		
		commands.add("\tDocument Query: " 
				+ listDocumentQuery.toString());
		commands.add("\tList Document: "
				+ reply);
		//<------------------------------------->
		
		//3.2 Find all documents in a collection
		commands.add("3.2 Find all documents in a collection");
		
		reply = restAPI.get(URL + "dbTest/mycollection", null);
		
		commands.add("\tList all Documents: "
						+ reply);
		//<------------------------------------->
		
		//4 Update documents in a collection
		commands.add("\n4 Update documents in a collection");
		
		JsonObject updateDocument = Json.createObjectBuilder()
				.add("firstname", "newuser").add("lastname", "newname")
				.add("number", 4).build();
		JsonObject updateDocumentQuery = Json.createObjectBuilder()
				.add("firstname", "user1").build();
		reply = restAPI.put(URL + "dbTest/mycollection", updateDocument,
				updateDocumentQuery);
		
		commands.add("\tDocument Query: " 
				+ updateDocumentQuery.toString());
		commands.add("\tUpdate Document: " 
				+ updateDocument.toString());
		commands.add("\tUpdate Document: "
				+ reply);
		//<------------------------------------->
		
		//5 Delete documents in a collection
		commands.add("\n5 Delete documents in a collection");
		
		JsonObject deleteDocumentQuery = Json.createObjectBuilder()
				.add("number", 2).build();	
		reply = restAPI.delete(URL + "dbTest/mycollection",deleteDocumentQuery);
		
		commands.add("\tDocument Query: " 
				+ deleteDocumentQuery.toString());
		commands.add("\tDelete Document: "
				+ reply);
		//<------------------------------------->
		
		//6 List all collections in a database
		commands.add("\n6 List all collections in a database");
		
		reply = restAPI.get(URL + "dbTest", null);
		
		commands.add("\tList all Collections: " + reply);
		//<------------------------------------->
		
		//7 Drop a collection
		commands.add("\n7 Drop a collection");
		
		reply = restAPI.delete(URL + "dbTest/mycollection", null);
				
		commands.add("\tDelete Collection: " + reply);
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
			URL = vcap.getJsonArray("altadb-dev").getJsonObject(0)
					.getJsonObject("credentials").getString("ssl_rest_url");
		else
			URL = vcap.getJsonArray("altadb-dev").getJsonObject(0)
					.getJsonObject("credentials").getString("rest_url");
		System.out.println(URL);

	}

}

class REST {

	public final Client client = ClientBuilder.newClient();
	public final Map<String,Cookie> cookies = new HashMap<String,Cookie>();
	
	public REST(String username, String password) {
		HttpAuthenticationFeature feature = HttpAuthenticationFeature.basic(
				username, password);
		client.register(feature);
	}

	//GET
	public String get(String uri, JsonObject query) {
		WebTarget target = client.target(uri);
		
		//add query to target
		if (query != null)
			target = target.queryParam("query", query.toString());
		Invocation.Builder invocationBuilder = target
				.request(MediaType.APPLICATION_JSON_TYPE);
		for(Entry<String,Cookie> cookie : this.cookies.entrySet())
        	invocationBuilder.cookie(cookie.getValue());
		//get and retrieve response
		Response response = invocationBuilder.get();
		String entity = response.readEntity(String.class);
		cookies.putAll(response.getCookies());
		response.close();
		return entity;
	}
	//POST
	public String post(String uri, JsonObject... itemsToPost) {
		WebTarget target = client.target(uri);
		
		//insert multiple
		if (itemsToPost.length > 1) {
			Invocation.Builder invocationBuilder = target
					.request(MediaType.APPLICATION_JSON_TYPE);
			
			//build a json array
			JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
			for (JsonObject itemToPost : itemsToPost) {
				arrayBuilder.add(itemToPost);
			}
			JsonArray arrayOfPosts = arrayBuilder.build();
			for(Entry<String,Cookie> cookie : this.cookies.entrySet())
	        	invocationBuilder.cookie(cookie.getValue());
			//post json array and retrieve response
			Response response = invocationBuilder.post(Entity.json(arrayOfPosts
					.toString()));
			String entity = response.readEntity(String.class);
			cookies.putAll(response.getCookies());
			response.close();
			return entity;
		}
		else {
		//insert single
		Invocation.Builder invocationBuilder = target
				.request(MediaType.APPLICATION_JSON_TYPE);
		for(Entry<String,Cookie> cookie : this.cookies.entrySet())
        	invocationBuilder.cookie(cookie.getValue());
		//post json object and retrieve response
		Response response = invocationBuilder.post(Entity.json(itemsToPost[0]
				.toString()));
		String entity = response.readEntity(String.class);
		cookies.putAll(response.getCookies());
		response.close();
		return entity;
		}
	}

	//PUT
	public String put(String uri, JsonObject itemToPost, JsonObject query) {
		WebTarget target = client.target(uri);
		
		//add query to target
		if (query != null) {
			try {
				target = target.queryParam("query",
						URLEncoder.encode(query.toString(), "UTF-8"));
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		Invocation.Builder invocationBuilder = target
				.request(MediaType.APPLICATION_JSON);
		for(Entry<String,Cookie> cookie : this.cookies.entrySet())
        	invocationBuilder.cookie(cookie.getValue());
		//put json object and retrieve response
		Response response = invocationBuilder.put(Entity.json(itemToPost
				.toString()));
		String entity = response.readEntity(String.class);
		cookies.putAll(response.getCookies());
		response.close();
		return entity;
	}

	//DELETE
	public String delete(String uri, JsonObject query) {
		WebTarget target = client.target(uri);
		
		//add query to target
		if (query != null) {
			try {
				target = target.queryParam("query",
						URLEncoder.encode(query.toString(), "UTF-8"));
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		Invocation.Builder invocationBuilder = target
				.request(MediaType.APPLICATION_JSON_TYPE);
		for(Entry<String,Cookie> cookie : this.cookies.entrySet())
        	invocationBuilder.cookie(cookie.getValue());
		//delete and retrieve response
		Response response = invocationBuilder.delete();
		String entity = response.readEntity(String.class);
		cookies.putAll(response.getCookies());
		response.close();
		return entity;
	}
}
