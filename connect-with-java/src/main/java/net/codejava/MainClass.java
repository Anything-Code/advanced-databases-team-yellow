package net.codejava;

import java.util.LinkedList;
import java.util.List;

public class MainClass {
	private static int version = 1;
	static Neo4jDBConnect neo4jClient;

	public static List<EmergencyCodes> emergencyCodeList = new LinkedList<>();

	public static void main(String... args) throws Exception {
		System.out.println("started...");

		System.out.println("connecting Neo4j");
		try {
			neo4jClient = new Neo4jDBConnect("bolt://localhost:7687", "neo4j", "123");
		} catch (Exception e) {
			throw e;
		}

		/*
		 * try(Neo4jDBConnect neo4jClient = new
		 * Neo4jDBConnect("bolt://localhost:7687","neo4j","123")){
		 * neo4jClient.SetupNeo4j();
		 * System.out.println(neo4jClient.LookUpDepartment("Hospital"));
		 * //EmergencyCodeList.add(new EmergencyCodes(neo4jClient, "Hospital"));
		 * }
		 * 
		 * /*
		 * System.out.println("connecting MongoDB");
		 * try(MongoDBConnect mongoClient = new MongoDBConnect()){
		 * mongoClient.mongoConnect();
		 * }
		 * 
		 * System.out.println("connecting Redis");
		 * try(RedisDBConnect redisClient = new RedisDBConnect()){
		 * }
		 */

		System.out.println("Checking Up To Date");
		checkBaiscIsUpToDate();

		System.out.println("Done");
	}

	private static void checkBaiscIsUpToDate() {// takes care of updates and such crap
		neo4jClient.SetupNeo4j();
		// i used the American code names
		emergencyCodeList
				.add(new EmergencyCodes(neo4jClient, new String[] { "Police" }, "Code Adam", "Child abduction"));
		emergencyCodeList.add(
				new EmergencyCodes(neo4jClient, new String[] { "Medical" }, "Code Blue", "Heart or respiration stops"));
		emergencyCodeList.add(new EmergencyCodes(neo4jClient, new String[] { "Fire" }, "Code Brown", "Severe weather"));
		emergencyCodeList.add(new EmergencyCodes(neo4jClient, new String[] { "Police", "Medical", "Fire" },
				"Code Clear", "Emergency is over"));
		emergencyCodeList.add(
				new EmergencyCodes(neo4jClient, new String[] { "Police", "Medical" }, "Code Gray", "Combative Person"));
		emergencyCodeList.add(new EmergencyCodes(neo4jClient, new String[] { "Police", "Medical", "Fire" },
				"Code Orange", "Hazardous spills"));
		emergencyCodeList.add(new EmergencyCodes(neo4jClient, new String[] { "Police", "Medical" }, "Code Pink",
				"Infant abduction, pediatric emergency and/or obstetrical emergency"));
		emergencyCodeList.add(new EmergencyCodes(neo4jClient, new String[] { "Fire" }, "Code Red", "Fire"));
		emergencyCodeList.add(new EmergencyCodes(neo4jClient, new String[] { "Police" }, "Code Silver",
				"Weapon or hostage situation"));
		emergencyCodeList.add(new EmergencyCodes(neo4jClient, new String[] { "Police", "Medical", "Fire" },
				"Code White", "Neonatal emergency"));
		emergencyCodeList.add(new EmergencyCodes(neo4jClient, new String[] { "Police" }, "Code Violet",
				"Aggressive person in hospitals"));
		emergencyCodeList.add(new EmergencyCodes(neo4jClient, new String[] { "Police", "Medical", "Fire" },
				"Code Green", "Emergency activation"));
		emergencyCodeList.add(new EmergencyCodes(neo4jClient, new String[] { "Police" }, "Code Black", "Bomb threat"));
		emergencyCodeList.add(new EmergencyCodes(neo4jClient, new String[] { "Police", "Medical", "Fire" },
				"External triage", "External disaster"));
		emergencyCodeList.add(new EmergencyCodes(neo4jClient, new String[] { "Police", "Medical", "Fire" },
				"Internal triage", "Internal disaster"));
		emergencyCodeList.add(new EmergencyCodes(neo4jClient, new String[] { "Medical" }, "Rapid response team",
				"Medical team needed,  prior to heart or respiration stopping"));
	}

}
