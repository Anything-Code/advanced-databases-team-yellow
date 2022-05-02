package net.codejava;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class TrueWayApi {
	
	public void makeTrueWayRequest(String landmark, String city) throws IOException, InterruptedException {
		String somePlace = landmark + "%20" + city;
		
		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create("https://trueway-places.p.rapidapi.com/FindPlaceByText?text="+somePlace+"&language=en"))
				.header("X-RapidAPI-Host", "trueway-places.p.rapidapi.com")
				.header("X-RapidAPI-Key", "f40b3989d8msh4d9450ac390ad2ap12ef24jsnff3c3c8b9fe2")
				.method("GET", HttpRequest.BodyPublishers.noBody())
				.build();
		HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
		System.out.println(response.body());
	}

}
