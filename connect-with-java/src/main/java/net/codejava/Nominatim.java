package net.codejava;

import java.io.IOException;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import fr.dudie.nominatim.client.JsonNominatimClient;
import fr.dudie.nominatim.model.Address;
import fr.dudie.nominatim.model.Element;

public final class Nominatim {
	
	HttpClient client;
	JsonNominatimClient nominatimConnection;
	
	Nominatim (){
		client = HttpClientBuilder.create().build();
		
		nominatimConnection = new JsonNominatimClient(client, "emillinnebjerg@gmail.com");
		giveMeEmpireStateFromGPS();
	}
	
	public void giveMeEmpireStateFromGPS() {
		try {
			System.out.println("Finding empire state from gps");
			Address mup = nominatimConnection.getAddress(-73.985428, 40.748817);
			Element[] addressInfo = mup.getAddressElements();
			System.out.println("===================================" + addressInfo[0].getValue() + "===================================");
			System.out.println("===================================" + addressInfo[1].getValue() + "===================================");
			System.out.println("===================================" + addressInfo[2].getValue() + "===================================");
			System.out.println("===================================" + addressInfo[3].getValue() + "===================================");
			System.out.println("===================================" + addressInfo[4].getValue() + "===================================");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	
}
