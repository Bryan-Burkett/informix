package com.ibm.informix;

import javax.json.JsonObject;

public class Query {
	
	public String queryType;
	public Object queryValue;
	
	public Query() {
		queryType = "query";
		queryValue = null;
	}
	
	public Query(String queryType, Object queryValue) {
		this.queryType = queryType;
		this.queryValue = queryValue;
	}
	
	public void setQueryType(String queryType) {
		this.queryType = queryType;
	}
	
	public void setQueryValue(Object queryValue){
		this.queryValue = queryValue;
	}
	
	public String toString() {
		return queryType + "=" + queryValue;
	}

}
