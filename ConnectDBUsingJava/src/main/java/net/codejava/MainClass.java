package net.codejava;

public class MainClass {
	public static void main(String...args) throws Exception{
		System.out.println("started...");
		
		System.out.println("connecting Neo4j");
		try(Neo4jDBConnect neo4jClient = new Neo4jDBConnect("bolt://localhost:7687","neo4j","123")){
			neo4jClient.printGreeting("Neo4j says hello");
		}
		
		System.out.println("connecting MongoDB");
		try(MongoDBConnect mongoClient = new MongoDBConnect()){
			mongoClient.mongoConnect();
		}
		
		System.out.println("connecting Redis");
		try(RedisDBConnect redisClient = new RedisDBConnect()){
		}
		
		System.out.println("Done");
	}
}
