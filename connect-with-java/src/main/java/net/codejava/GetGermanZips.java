package net.codejava;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

public class GetGermanZips {
	
	Neo4jDBConnect neo4j;
	
	GetGermanZips(Neo4jDBConnect neo4j){
		this.neo4j = neo4j;
	}
	
	public List<String> getSomeZips(String city) {
		List<String> zipCodes = new LinkedList<String>();
		
	    try {
	        
	        URL url = new URL("https://gist.githubusercontent.com/jbspeakr/4565964/raw/4083f8b8933f0e9a64dafc943ecbae496f9d65d2/German-Zip-Codes.csv");
	         
	        // read text returned by server
	        BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
	         
	        String line;
	        int i = 0;
	        
	        
	        city.toLowerCase();
	        System.out.println("REEEEEEEEEEEEEEEEE" + city);
	        char[] cityTest = city.toCharArray();
	        while ((line = in.readLine()) != null) {
	        	line.toLowerCase();
	        	char[] lineTest = line.toCharArray();
	        	if(lineTest.length == 0) {
	        		continue;
	        	}
	        	
	        	while(cityTest.length > i) {
	        		if(cityTest[i] != lineTest[i]) {
	        			break;
	        		}
	        		if(cityTest.length - 1 == i) {
	        			zipCodes.add(retriveZip(lineTest, city));
	        		}
	        		i++;
	        	}
	        	
	            i = 0;
	        }
	        in.close();
	         
	    }
	    catch (MalformedURLException e) {
	        System.out.println("Malformed URL: " + e.getMessage());
	    }
	    catch (IOException e) {
	        System.out.println("I/O Error: " + e.getMessage());
	    }
	    
	    return zipCodes;
	}
	
	private String retriveZip(char[] line, String city) {
		int i = 0;
		String zipCode = "";
		for(char ch : line) {
			if(ch == ';') {
				i++;
				if(i == 2) {
					continue;
				}
				if(i == 3) {
					break;
				}
			}
			if(i == 2) {
				zipCode+= ch;
			}
		}
		System.out.println("Found a zip: " + zipCode);
		neo4j.addZipCityRelation(city, zipCode);
		
		return zipCode;
	}
}
