package net.codejava;

import org.bson.Document;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoIterable;

public class MongoDBConnect implements AutoCloseable{

	public void mongoConnect() {
		//this should work for all instanaces of locally run mongoDB
		String uri = "mongodb://localhost";
		MongoClient mongoClient = MongoClients.create(uri);
		
		MongoIterable<String> dbNames = mongoClient.listDatabaseNames();
		System.out.println("Showing local mongo databeses");
		for(String dbName : dbNames) {
			System.out.println(dbName);
		}
		
		MongoDatabase db = mongoClient.getDatabase("codejava");
		MongoCollection<Document> collection = db.getCollection("inventory");
		
		Document document = new Document("name", "test");
		collection.insertOne(document);
		//If this works for you expect a new database when using "show databases" that is called codejava
	}

	@Override
	public void close() throws Exception {
		// TODO Auto-generated method stub
		
	}

}
