package net.codejava;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.bson.Document;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class TrueWayApi {
	
	MongoDBConnect mongoDB;
	Neo4jDBConnect neo4j;
	
	TrueWayApi(MongoDBConnect connect, Neo4jDBConnect neo4j){
		mongoDB = connect;
		this.neo4j = neo4j;
	}
	
	public void makeTrueWayRequest(String landmark, String city, String neo4jId, boolean knownZip) throws Exception {
		String somePlace = "Germany%20" + city + landmark;
		List<String> zipCodes = new LinkedList<String>();
		
		double[][] cordinateList;
		
		int i = 0;
		if(!knownZip) {
			GetGermanZips getZips = new GetGermanZips(neo4j);
			zipCodes = getZips.getSomeZips(city);
			
			cordinateList = new double[zipCodes.size() + 1][];
			
			for(String s : zipCodes) {
				System.out.println("====================Looking for costume zip");
				double[] temp = contactAPI(city + "%20" + s + "%20" + landmark, neo4jId);
				if(temp.length != 0) {
					cordinateList[i] = temp;
				}
				 
				i++;
			}
		}
		else {
			cordinateList = new double[1][];
		}
		
		System.out.println("====================Looking for def");
		cordinateList[i] = contactAPI(somePlace, neo4jId);
		
		double[] lng = new double[cordinateList.length];
		double[] lat = new double[cordinateList.length];
		i = 0;
		
		for(double[] a : cordinateList) {
			if(a == null || a.length == 0) {
				continue;
			}
			
			System.out.println(a[0] + " " + a[1]);
			lat[i] = a[0];
			lng[i] = a[1];
			
			i++;
		}
		mongoDB.createEmergencyZone(neo4jId, lat, lng, landmark);
	}
	
	private double[] contactAPI(String somePlace, String neo4jId) throws IOException, InterruptedException{
		if(MainClass.ExtensiveSearching) {
			TimeUnit.SECONDS.sleep(1);
			//gotta wait cus i use dat free api :)
		}
		
		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create("https://trueway-places.p.rapidapi.com/FindPlaceByText?text="+somePlace+"&language=en"))
				.header("X-RapidAPI-Host", "trueway-places.p.rapidapi.com")
				.header("X-RapidAPI-Key", "f40b3989d8msh4d9450ac390ad2ap12ef24jsnff3c3c8b9fe2")
				.method("GET", HttpRequest.BodyPublishers.noBody())
				.build();
		HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
		System.out.println(response.body().toString());
		System.out.println(response.toString());
		
		JSONObject myObj = new JSONObject(response.body());
		JSONArray responses;
		try {
			responses = myObj.getJSONArray("results");
		}catch(Exception e) {
			System.out.println("====================NAAAAAAAAA that shit was wack");
			return new double[0];
		}
		
		
		System.out.println(responses.length());
		
		double[] cordinates = new double[2];
		
		JSONObject JObj = responses.getJSONObject(0);
		JSONObject JObjLoc = JObj.getJSONObject("location");
		
		cordinates[0] = JObjLoc.getDouble("lat");
		cordinates[1] = JObjLoc.getDouble("lng");
		
		return cordinates;
		/*
		for(double[] latAlng : cordinates) {
			mongoDB.createEmergencyZone(neo4jId, latAlng[0]+"", latAlng[1]+"");
		}*/
		
	}


}
