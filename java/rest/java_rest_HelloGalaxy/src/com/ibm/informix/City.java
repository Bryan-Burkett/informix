package com.ibm.informix;

import javax.json.Json;
import javax.json.JsonObject;

public class City {
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
	
	public JsonObject toJson() {
		return Json.createObjectBuilder()
				.add("name", name)
				.add("population", population)
				.add("longitude", longitude)
				.add("latitude", latitude)
				.add("code", countryCode)
				.build();
	}

	public String toString(){
		return "name: " + this.name + "  \tpopulation: " + this.population + "\tlongitude: " + this.longitude + 
				"\tlatitude: " + this.latitude  +  "\tcode: " + this.countryCode;
	}
}
