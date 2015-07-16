package com.ibm.informix;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;

public class REST {

	public final Client client = ClientBuilder.newClient();
	public final Map<String,Cookie> cookies = new HashMap<String,Cookie>();
	
	public REST(String username, String password) {
		HttpAuthenticationFeature feature = HttpAuthenticationFeature.basic(
				username, password);
		client.register(feature);
	}
	
	//GET
	public String get (String uri, List<Query> queries){
		WebTarget target = client.target(uri);	
		if (queries != null){
		for (Query query : queries) {
			if (query != null) {
				try {
					String s = URLEncoder.encode(query.queryValue.toString(), "UTF-8").replace('+', ' ');
					target = target.queryParam(query.queryType, s);
				} catch (UnsupportedEncodingException uee) {
					uee.printStackTrace();
				}
			}
		}
		}
		
		Invocation.Builder invocationBuilder = target.request(MediaType.APPLICATION_JSON_TYPE);
		for(Entry<String,Cookie> cookie : this.cookies.entrySet())
			invocationBuilder.cookie(cookie.getValue());
		Response response = invocationBuilder.get();
		String entity = response.readEntity(String.class);
		cookies.putAll(response.getCookies());
		response.close();
		return entity;		
	}
	
//	//GET
//	public String get(String uri, Query query) {
//		WebTarget target = client.target(uri);
//		
//		//add query to target
//		if (query != null) {
//			try {
//				target = target.queryParam(query.queryType,
//						URLEncoder.encode(query.queryValue.toString(), "UTF-8"));
//			} catch (UnsupportedEncodingException e) {
//				e.printStackTrace();
//			}
//		}
//		Invocation.Builder invocationBuilder = target
//				.request(MediaType.APPLICATION_JSON_TYPE);
//		for(Entry<String,Cookie> cookie : this.cookies.entrySet())
//        	invocationBuilder.cookie(cookie.getValue());
//		//get and retrieve response
//		Response response = invocationBuilder.get();
//		String entity = response.readEntity(String.class);
//		cookies.putAll(response.getCookies());
//		response.close();
//		return entity;
//	}
	
	//POST
	public String post(String uri, List<JsonObject> itemsToPost) {
		WebTarget target = client.target(uri);
		Object postData = new Object();
		
		if (itemsToPost.size() > 1) {
			JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
			for (JsonObject itemToPost : itemsToPost) {
				arrayBuilder.add(itemToPost);
			}
			postData = arrayBuilder.build();
		} else 
			postData = itemsToPost.get(0);
		
			Invocation.Builder invocationBuilder = target
					.request(MediaType.APPLICATION_JSON_TYPE);
			for (Entry<String, Cookie> cookie : this.cookies.entrySet())
				invocationBuilder.cookie(cookie.getValue());
			Response response = invocationBuilder.post(Entity.json(postData.toString()));
			String entity = response.readEntity(String.class);
			cookies.putAll(response.getCookies());
			response.close();
			return entity;
	}
	
//	//POST
//	public String post(String uri, JsonObject... itemsToPost) {
//		WebTarget target = client.target(uri);
//		
//		//insert multiple
//		if (itemsToPost.length > 1) {
//			Invocation.Builder invocationBuilder = target
//					.request(MediaType.APPLICATION_JSON_TYPE);
//			
//			//build a json array
//			JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
//			for (JsonObject itemToPost : itemsToPost) {
//				arrayBuilder.add(itemToPost);
//			}
//			JsonArray arrayOfPosts = arrayBuilder.build();
//			for(Entry<String,Cookie> cookie : this.cookies.entrySet())
//	        	invocationBuilder.cookie(cookie.getValue());
//			//post json array and retrieve response
//			Response response = invocationBuilder.post(Entity.json(arrayOfPosts
//					.toString()));
//			String entity = response.readEntity(String.class);
//			cookies.putAll(response.getCookies());
//			response.close();
//			return entity;
//		}
//		else {
//		//insert single
//		Invocation.Builder invocationBuilder = target
//				.request(MediaType.APPLICATION_JSON_TYPE);
//		for(Entry<String,Cookie> cookie : this.cookies.entrySet())
//        	invocationBuilder.cookie(cookie.getValue());
//		//post json object and retrieve response
//		Response response = invocationBuilder.post(Entity.json(itemsToPost[0]
//				.toString()));
//		String entity = response.readEntity(String.class);
//		cookies.putAll(response.getCookies());
//		response.close();
//		return entity;
//		}
//	}
	
	//PUT
	public String put(String uri, JsonObject itemToPost, List<Query> queries) {
		WebTarget target = client.target(uri);
		if (queries != null) {
		for (Query query : queries) {
			if (query != null) {
				try {
					target = target.queryParam(query.queryType, URLEncoder
							.encode(query.queryValue.toString(), "UTF-8"));
				} catch (UnsupportedEncodingException uee) {
					uee.printStackTrace();
				}
			}
		}
		}
		Invocation.Builder invocationBuilder = target.request(MediaType.APPLICATION_JSON);
		for(Entry<String,Cookie> cookie : this.cookies.entrySet())
        	invocationBuilder.cookie(cookie.getValue());
		Response response = invocationBuilder.put(Entity.json(itemToPost.toString()));
		String entity = response.readEntity(String.class);
		cookies.putAll(response.getCookies());
		response.close();
		return entity;
	}

//	//PUT
//	public String put(String uri, JsonObject itemToPost, JsonObject query) {
//		WebTarget target = client.target(uri);
//		
//		//add query to target
//		if (query != null) {
//			try {
//				target = target.queryParam("query",
//						URLEncoder.encode(query.toString(), "UTF-8"));
//			} catch (UnsupportedEncodingException e) {
//				e.printStackTrace();
//			}
//		}
//		Invocation.Builder invocationBuilder = target
//				.request(MediaType.APPLICATION_JSON);
//		for(Entry<String,Cookie> cookie : this.cookies.entrySet())
//        	invocationBuilder.cookie(cookie.getValue());
//		//put json object and retrieve response
//		Response response = invocationBuilder.put(Entity.json(itemToPost
//				.toString()));
//		String entity = response.readEntity(String.class);
//		cookies.putAll(response.getCookies());
//		response.close();
//		return entity;
//	}
	
	//DELETE
	public String delete(String uri, List<Query> queries) {
		WebTarget target = client.target(uri);	
		
		if (queries != null) {
		for (Query query : queries) {
			if (query != null) {
				try {
					target = target.queryParam(query.queryType, URLEncoder
							.encode(query.queryValue.toString(), "UTF-8"));
				} catch (UnsupportedEncodingException uee) {
					uee.printStackTrace();
				}
			}
		}
		}
			
		Invocation.Builder invocationBuilder = target.request(MediaType.APPLICATION_JSON_TYPE);
		for(Entry<String,Cookie> cookie : this.cookies.entrySet())
			invocationBuilder.cookie(cookie.getValue());
		Response response = invocationBuilder.delete();
		String entity = response.readEntity(String.class);
		cookies.putAll(response.getCookies());
		response.close();
		return entity;
	}

//	//DELETE
//	public String delete(String uri, JsonObject query) {
//		WebTarget target = client.target(uri);
//		
//		//add query to target
//		if (query != null) {
//			try {
//				target = target.queryParam("query",
//						URLEncoder.encode(query.toString(), "UTF-8"));
//			} catch (UnsupportedEncodingException e) {
//				e.printStackTrace();
//			}
//		}
//		Invocation.Builder invocationBuilder = target
//				.request(MediaType.APPLICATION_JSON_TYPE);
//		for(Entry<String,Cookie> cookie : this.cookies.entrySet())
//        	invocationBuilder.cookie(cookie.getValue());
//		//delete and retrieve response
//		Response response = invocationBuilder.delete();
//		String entity = response.readEntity(String.class);
//		cookies.putAll(response.getCookies());
//		response.close();
//		return entity;
//	}
}
