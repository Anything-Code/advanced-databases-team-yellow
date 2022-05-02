package net.codejava;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
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
		
		//Nominatim nominatimClient = new Nominatim();
		
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
		
		
		interfaceBasic();
		
		
		//googleMapsApi mapApi = new googleMapsApi();
		//mapApi.test();
		
		//scenario

		
		
		//emergencyTest.updateZip("69123");
		
		System.out.println("Done");
	}
	
	public static void interfaceBasic() throws Exception {
        BufferedReader reader = new BufferedReader(
            new InputStreamReader(System.in));
        
        System.out.println("=========================================================================");
        System.out.println("Case 1: find location based on partial addresse");
        System.out.println("Type '1' to run this case");
        System.out.println("=========================================================================");
        
        String input = reader.readLine();
        int num;
        try {
        	num = Integer.parseInt(input);
        }catch(Exception e){
        	throw e;
        }
        
        
        if(num == 1) {
        	runCaseOne();
        }
	}
	
	public static void runCaseOne() throws Exception {
		
		String eCode = "Code Adam";
		String city;
		String zip;
		String street;
		String nr;
            
            System.out.println("=========================================================================");
            System.out.println("What is the emergency type?");
            System.out.println("Type 'show' to see all or type the emergency");
            System.out.println("=========================================================================");
            
            String input = getAInput();
            if(input.equals("show")) {
            	
            }
            else {
            	eCode = input;
            }
            
            System.out.println("=========================================================================");
            System.out.println("Do you know your town?");
            System.out.println("Type 'no' if not");
            System.out.println("=========================================================================");
            
            input = getAInput();
            if(input.equals("no")) {
            	city = "";
            }
            else {
            	city = input;
            }
            
            System.out.println("=========================================================================");
            System.out.println("Do you know your zip?");
            System.out.println("Type 'no' if not");
            System.out.println("=========================================================================");
            
            input = getAInput();
            if(input.equals("no")) {
            	zip = "";
            }
            else {
            	zip = input;
            }
            
            System.out.println("=========================================================================");
            System.out.println("Do you know your street?");
            System.out.println("Type 'no' if not");
            System.out.println("=========================================================================");
            
            input = getAInput();
            if(input.equals("no")) {
            	street = "";
            }
            else {
            	street = input;
            }
            
            System.out.println("=========================================================================");
            System.out.println("Do you know your Nr?");
            System.out.println("Type 'no' if not");
            System.out.println("=========================================================================");
            
            input = getAInput();
            if(input.equals("no")) {
            	nr = "";
            }
            else {
            	nr = input;
            }
            
    		EmergencyReport emergencyTest = new EmergencyReport(neo4jClient, eCode, city, zip, street, nr);
    		
    		String Location = emergencyTest.myId;
    		if(nr == null || nr.length() == 0) {
    			System.out.println("Could not find location");
    			
                System.out.println("=========================================================================");
                System.out.println("Are you near a store or landmark?");
                System.out.println("Type 'no' if not");
                System.out.println("=========================================================================");
                
                input = getAInput();
                if(input.equals("no")) {
                	//nr = null;
                }
                else {
                	TrueWayApi trueWayApi = new TrueWayApi();
            		System.out.println("your near...");
            		trueWayApi.makeTrueWayRequest(input, city);
                }
    			
    		}
    		else {
    			System.out.println("I am at");
        		System.out.println(neo4jClient.fetchGPSfromKnowAdresse(emergencyTest.myId));
    		}
    		
	}
	
	public static String getAInput() throws IOException {
		BufferedReader reader = new BufferedReader(
                new InputStreamReader(System.in));
		
		String input = reader.readLine();
		
		return input;
	}
	
	
	private static void checkBaiscIsUpToDate() throws Exception {//takes care of updates and such crap
		if (neo4jClient.checkVersion(version)) {
			return;
		}
		
		neo4jClient.createGPSTrigger();
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
