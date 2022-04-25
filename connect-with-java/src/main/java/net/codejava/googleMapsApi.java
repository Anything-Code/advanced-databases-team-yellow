package net.codejava;

import java.io.IOException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.errors.ApiException;
import com.google.maps.model.GeocodingResult;

public final class googleMapsApi {
	private final String mapApiKey = "AIzaSyBA4P1_iM_Xd_3KjznuVcpRxInRhsKCpUA";
	
	public void test() throws ApiException, InterruptedException, IOException {
		GeoApiContext context = new GeoApiContext.Builder()
			    .apiKey(mapApiKey)
			    .build();
			GeocodingResult[] results =  GeocodingApi.geocode(context,
			    "1600 Amphitheatre Parkway Mountain View, CA 94043").await();
			Gson gson = new GsonBuilder().setPrettyPrinting().create();
			System.out.println(gson.toJson(results[0].addressComponents));

			// Invoke .shutdown() after your application is done making requests
			context.shutdown();
	}
}
