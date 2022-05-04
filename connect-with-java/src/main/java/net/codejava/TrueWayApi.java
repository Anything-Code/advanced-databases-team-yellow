package net.codejava;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

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
	
	TrueWayApi(MongoDBConnect connect){
		mongoDB = connect;
	}
	
	public double[][] makeTrueWayRequest(String landmark, String city, String neo4jId) throws IOException, InterruptedException {
		String somePlace = landmark + "%20Germany%20" + city;
		

		
		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create("https://trueway-places.p.rapidapi.com/FindPlaceByText?text="+somePlace+"&language=en"))
				.header("X-RapidAPI-Host", "trueway-places.p.rapidapi.com")
				.header("X-RapidAPI-Key", "f40b3989d8msh4d9450ac390ad2ap12ef24jsnff3c3c8b9fe2")
				.method("GET", HttpRequest.BodyPublishers.noBody())
				.build();
		HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
		System.out.println(response.body().toString());
		
		JSONObject myObj = new JSONObject(response.body());
		JSONArray responses = myObj.getJSONArray("results");
		
		System.out.println(responses.length());
		
		double[][] cordinates = new double[responses.length()][2];
		
		for(int i = 0; i < responses.length(); i++) {
			JSONObject JObj = responses.getJSONObject(0);
			JSONObject JObjLoc = JObj.getJSONObject("location");
			
			cordinates[i][0] = JObjLoc.getDouble("lat");
			cordinates[i][1] = JObjLoc.getDouble("lng");
		}
		
		for(double[] latAlng : cordinates) {
			mongoDB.createEmergencyZone(neo4jId, latAlng[0]+"", latAlng[1]+"");
		}
		
		return cordinates;
	}
	


}
