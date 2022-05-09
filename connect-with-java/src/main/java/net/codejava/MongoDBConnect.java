package net.codejava;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.bson.Document;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoIterable;
import com.mongodb.client.model.geojson.Point;
import com.mongodb.client.model.geojson.Position;

public class MongoDBConnect implements AutoCloseable{
	
	MongoDatabase db;
	
	public void mongoConnect() {
		//this should work for all instanaces of locally run mongoDB
		String uri = "mongodb://localhost";
		MongoClient mongoClient = MongoClients.create(uri);
		
		MongoIterable<String> dbNames = mongoClient.listDatabaseNames();
		System.out.println("Showing local mongo databeses");
		for(String dbName : dbNames) {
			System.out.println(dbName);
		}
		
		db = mongoClient.getDatabase("EmergencyApp");
		
		/*
		MongoCollection<Document> collection = db.getCollection("inventory");
		
		Document document = new Document("name", "test");
		collection.insertOne(document);
		//If this works for you expect a new database when using "show databases" that is called codejava
		 * 
		 */
	}
	
	public void createEmergency(String codeT, String neo4jId) {
		MongoCollection<Document> collection = db.getCollection("Emergency");
		
		Document document = new Document();
		document.put("EmergencyCode", codeT);
		document.put("Neo4jId", neo4jId);
		collection.insertOne(document);
	}
	
	public void createEmergencyZone(String neo4jId, double[] lat, double[] lng, String type, String color) {
		MongoCollection<Document> collection = db.getCollection("EmergencyZone");
		List<Document> documentList = new LinkedList<Document>();
		
		int i = 0;
		for(double a : lat) {
			
			Document document = new Document();
			document.put("location", new Point(new Position(lng[i], a)));
			document.put("Radius", 10);
			document.put("category", type);
			document.put("MapId", type + "_" + i);
			document.put("NeoId", neo4jId);
			document.put("Color", color);
			
			documentList.add(document);
			i++;
		}
		
		collection.insertMany(documentList);
		
		collection.createIndex(new Document("location", "2dsphere"));
	}
	
	public List<EmergencyZone> giveAllZones() {
		List<EmergencyZone> emergencyZones = new LinkedList<EmergencyZone>();
		
		MongoCollection<Document> collection = db.getCollection("EmergencyZone");
		for(Document doc : collection.find()) {
			Document CDPoint = (Document) doc.get("location");
			ArrayList<Double> Cordi = (ArrayList<Double>) CDPoint.get("coordinates");
			
			String name = doc.get("MapId").toString();
			double lat = Cordi.get(1);
			double lng = Cordi.get(0);
			String rad = doc.get("Radius").toString();
			String color = doc.get("Color").toString();
			
			emergencyZones.add(new EmergencyZone(name, lat+"", lng+"", rad, color));
		}
		
		return (emergencyZones);
	}
	
	public ArrayList<ArrayList<Double>> giveCordinatesOfLoc(String name) {
		ArrayList<ArrayList<Double>> result = new ArrayList<ArrayList<Double>>();
		
		BasicDBObject fileds = new BasicDBObject("category", name);
		
		MongoCollection<Document> collection = db.getCollection("EmergencyZone");
		for(Document doc : collection.find(fileds)) {
			Document CDPoint = (Document) doc.get("location");
			ArrayList<Double> Cordi = (ArrayList<Double>) CDPoint.get("coordinates");
			
			result.add(Cordi);
		}
		
		
		return result;
	}
	
	public ArrayList<ArrayList<Double>> findNearest(String category, double lat, double lng) {
		MongoCollection<Document> collection = db.getCollection("EmergencyZone");
		
		Document document = new Document("$geoNear", 
			    new Document("near", 
			    	    new Document("type", "Point")
			    	                .append("coordinates", Arrays.asList(lng, lat)))
			    	            .append("distanceField", "dist.calculated")
			    	            .append("maxDistance", 1000L)//this is in meters
			    	            .append("query", 
			    	    new Document("category", category))
			    	            .append("includeLocs", "dist.location")
			    	            .append("spherical", true));
		
		AggregateIterable<Document> results = collection.aggregate(List.of(document));
		
		ArrayList<ArrayList<Double>> returnMid = new ArrayList<ArrayList<Double>>();
		for(Document result: results) {
			
			Document doc = (Document) result.get("dist");
			
			Document CDPoint = (Document) doc.get("location");
			ArrayList<Double> Cordi = (ArrayList<Double>) CDPoint.get("coordinates");
			
			ArrayList<Double> theCordi = new ArrayList<Double>();
			
			System.out.println("First: " + Cordi.get(1));
			if(lat > Cordi.get(1)) {
				theCordi.add(Cordi.get(1) + (lat - Cordi.get(1))/2);
			}
			else {
				theCordi.add(lat + (Cordi.get(1) - lat)/2);
			}
			
			System.out.println("Second: " + Cordi.get(0));
			if(lng > Cordi.get(0)) {
				theCordi.add(Cordi.get(0) + (lng - Cordi.get(0))/2);
			}
			else {
				theCordi.add(lng + (Cordi.get(0) - lng)/2);
			}
			
			System.out.println("=================================================");
			System.out.println(lat + " new " + theCordi.get(0) + " " + lng + " new " + theCordi.get(1));
			returnMid.add(theCordi);
		}
		
		
		return returnMid;
	}
	
	@Override
	public void close() throws Exception {
		// TODO Auto-generated method stub
		
	}

}
