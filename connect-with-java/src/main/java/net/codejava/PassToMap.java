package net.codejava;

import java.io.Console;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;


public class PassToMap {
		final MongoDBConnect mongoDB;
	
		PassToMap(MongoDBConnect mongoDB){
			this.mongoDB = mongoDB;
		}
		
	  public void makeData() {
		  
		  List<EmergencyZone> emergencyZones = mongoDB.giveAllZones();
		  
		  String content = "const citymap = {\r\n";
		  
		  for(EmergencyZone zone : emergencyZones) {
			  content += makeLocationBlock(zone.lat, zone.lng, zone.radius, zone.name, zone.color);  
		  }
		  
		  content += "};";
		  
		    try {
		        FileWriter myWriter = new FileWriter("C:\\Users\\Emil\\eclipse-workspace\\ConnectDBUsingJava\\src\\main\\java\\web\\reader.js");
		        
		        myWriter.write(content);
		        myWriter.close();
		        System.out.println("Successfully wrote to the file.");
		      } catch (IOException e) {
		        System.out.println("An error occurred.");
		        e.printStackTrace();
		      }
		  }
	  
	  private String makeLocationBlock(String lat, String lng, String radius, String name, String color) {
		  String toReturn = "";
		  toReturn += name + ": {\r\n";
		  toReturn += "center: { lat: " + lat + ", lng: " + lng + " },\r\n";
		  toReturn += "population: " + radius + ",\r\n";
		  toReturn += "color: \"" + color + "\",\r\n";
		  toReturn += "},\r\n";
		  
		  return toReturn;
	  }
	  
}

/**
 * 
 *Why do it like this you ask?
 *Well google maps was costing money and my trial with JxBrowser expired...
 *Anyways just to visualize hope its ok haha
 */