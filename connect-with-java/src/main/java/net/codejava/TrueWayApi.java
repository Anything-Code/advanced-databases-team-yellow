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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class TrueWayApi {
	
	public String makeTrueWayRequest(String landmark, String city) throws IOException, InterruptedException {
		String somePlace = landmark + "%20" + city;
		
		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create("https://trueway-places.p.rapidapi.com/FindPlaceByText?text="+somePlace+"&language=en"))
				.header("X-RapidAPI-Host", "trueway-places.p.rapidapi.com")
				.header("X-RapidAPI-Key", "f40b3989d8msh4d9450ac390ad2ap12ef24jsnff3c3c8b9fe2")
				.method("GET", HttpRequest.BodyPublishers.noBody())
				.build();
		HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
		System.out.println(response.body().toString());
		
		String lat = "";
		String lng = "";
		
		char[] responseChar = response.body().toCharArray();
		for(int i = 0; i < responseChar.length-1; i++) {
			if(responseChar[i] == '\"' &&
					responseChar[i+1] == 'l' &&
					responseChar[i+2] == 'a' &&
					responseChar[i+3] == 't' &&
					responseChar[i+4] == '\"' ) {
					i += 7;
					while(responseChar[i] != ',') {
						lat += responseChar[i];
						i++;
					}
					i += 17;
					while(responseChar[i] != '\n') {
						lng += responseChar[i];
						i++;
					}
			}
		}
		
		System.out.println("===================================" + lat + " " + lng);
		
		return lat + "," + lng;
	}
	


}
