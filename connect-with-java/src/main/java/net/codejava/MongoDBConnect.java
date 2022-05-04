package net.codejava;

import java.util.LinkedList;
import java.util.List;

import org.bson.Document;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoIterable;

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
	
	public void createEmergencyZone(String neo4jId, String lat, String lng) {
		MongoCollection<Document> collection = db.getCollection("EmergencyZone");
		Document Cdocument = new Document();
		Cdocument.put("Lat", lat);
		Cdocument.put("Lng", lng);
		
		Document document = new Document();
		document.put("Center", Cdocument);
		document.put("Radius", 10);
		document.put("NeoId", neo4jId);
		collection.insertOne(document);
	}
	
	public List<EmergencyZone> giveAllZones() {
		List<EmergencyZone> emergencyZones = new LinkedList<EmergencyZone>();
		
		MongoCollection<Document> collection = db.getCollection("EmergencyZone");
		for(Document doc : collection.find()) {
			Document Cdocument = (Document) doc.get("Center");
			
			
			String name = doc.get("NeoId").toString();
			String lat = Cdocument.get("Lat").toString();
			String lng = Cdocument.get("Lng").toString();
			String rad = doc.get("Radius").toString();
			
			emergencyZones.add(new EmergencyZone(name, lat, lng, rad));
		}
		
		return (emergencyZones);
	}
	
	@Override
	public void close() throws Exception {
		// TODO Auto-generated method stub
		
	}

}
