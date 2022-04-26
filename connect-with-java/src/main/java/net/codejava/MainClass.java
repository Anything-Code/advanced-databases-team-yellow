package net.codejava;

import java.util.LinkedList;
import java.util.List;

public class MainClass {
	private static int version = 1;
	static Neo4jDBConnect neo4jClient;
	
	public static List<EmergencyCodes> emergencyCodeList = new LinkedList<>();
	
	public static void main(String...args) throws Exception{
		System.out.println("started...");
		
		System.out.println("connecting Neo4j");
		try{
			neo4jClient = new Neo4jDBConnect("bolt://localhost:7687","neo4j","123");
		}catch(Exception e){
			throw e;
		}
		
		Nominatim nominatimClient = new Nominatim();
		
		/*
		try(Neo4jDBConnect neo4jClient = new Neo4jDBConnect("bolt://localhost:7687","neo4j","123")){
			neo4jClient.SetupNeo4j();
			System.out.println(neo4jClient.LookUpDepartment("Hospital"));
			//EmergencyCodeList.add(new EmergencyCodes(neo4jClient, "Hospital"));
		}
		
		/*
		System.out.println("connecting MongoDB");
		try(MongoDBConnect mongoClient = new MongoDBConnect()){
			mongoClient.mongoConnect();
		}
		
		System.out.println("connecting Redis");
		try(RedisDBConnect redisClient = new RedisDBConnect()){
		}
		*/
		
		System.out.println("Checking Up To Date");
		checkBaiscIsUpToDate();
		
		//googleMapsApi mapApi = new googleMapsApi();
		//mapApi.test();
		
		//scenario
		EmergencyReport emergencyTest = new EmergencyReport(neo4jClient, "");
		//emergencyTest.updateZip("69123");
		
		System.out.println("Done");
	}
	
	private static void checkBaiscIsUpToDate() {//takes care of updates and such crap
		if (neo4jClient.checkVersion(version)) {
			return;
		}
		
		neo4jClient.SetupNeo4j();
		
		//i used the American code names
		emergencyCodeList = List.of(
				(new EmergencyCodes(neo4jClient, new String[]{"Police"}, "Code Adam", "Child abduction")),
				(new EmergencyCodes(neo4jClient, new String[]{"Medical"}, "Code Blue", "Heart or respiration stops")),
				(new EmergencyCodes(neo4jClient, new String[]{"Fire"}, "Code Brown", "Severe weather", true)),
				(new EmergencyCodes(neo4jClient, new String[]{"Police","Medical","Fire"}, "Code Clear", "Emergency is over")),
				(new EmergencyCodes(neo4jClient, new String[]{"Police","Medical"}, "Code Gray", "Combative Person")),
				(new EmergencyCodes(neo4jClient, new String[]{"Police","Medical","Fire"}, "Code Orange", "Hazardous spills",true)),
				(new EmergencyCodes(neo4jClient, new String[]{"Police","Medical"}, "Code Pink", "Infant abduction, pediatric emergency and/or obstetrical emergency")),
				(new EmergencyCodes(neo4jClient, new String[]{"Fire"}, "Code Red", "Fire", true)),
				(new EmergencyCodes(neo4jClient, new String[]{"Police"}, "Code Silver", "Weapon or hostage situation")),
				(new EmergencyCodes(neo4jClient, new String[]{"Medical"}, "Code White", "Neonatal emergency")),
				(new EmergencyCodes(neo4jClient, new String[]{"Police"}, "Code Violet", "Aggressive person in hospitals", true)),
				(new EmergencyCodes(neo4jClient, new String[]{"Police","Medical","Fire"}, "Code Green", "Emergency activation")),
				(new EmergencyCodes(neo4jClient, new String[]{"Police"}, "Code Black", "Bomb threat", true)),
				(new EmergencyCodes(neo4jClient, new String[]{"Police","Medical","Fire"}, "External triage", "External disaster", true)),
				(new EmergencyCodes(neo4jClient, new String[]{"Police","Medical","Fire"}, "Internal triage", "Internal disaster", true)),
				(new EmergencyCodes(neo4jClient, new String[]{"Medical"}, "Rapid response team", "Medical team needed,  prior to heart or respiration stopping")));
	}

}
