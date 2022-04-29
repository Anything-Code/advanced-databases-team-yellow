package net.codejava;

import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;
import org.neo4j.driver.Session;
import org.neo4j.driver.Result;
import org.neo4j.driver.Transaction;
import org.neo4j.driver.TransactionWork;

import static org.neo4j.driver.Values.parameters;

import java.util.Map;
public final class Neo4jDBConnect implements AutoCloseable{
	private final Driver driver;
	public Neo4jDBConnect(String uri, String user, String password) {
		driver = GraphDatabase.driver(uri, AuthTokens.basic(user, password));
	}
	
	public void close() throws Exception{
		driver.close();
	}
	//===============================================TRIGGER INSTRUCTIONS=======================================
	
	public void createGPSTrigger() {
		Map<String, Object> params = Map.of();
		String instructions = "CALL apoc.trigger.add('setAllConnectedNodes','UNWIND apoc.trigger.propertiesByKey($assignedNodeProperties,\"City\") AS prop"
				+ "WITH prop.node as n"
				+ "MATCH(n)-[]-(a)"
				+ "SET a.surname = n.surname', {phase:'after'});";
		
		addNoteGetId(instructions, params);
	}
	
	//===============================================LOOKUP INSTRUCTIONS=======================================
	public String fetchKnowAdresse(String id) {
		Map<String, Object> params = Map.of("NId", Integer.parseInt(id));
		
		String insturctions = "MATCH (nm:Emergency) "
				+ "WHERE id(nm) = $NId "
				+ "MATCH (nm)-[r:LocatedAt]->(s) "
				+ "MATCH (s)-[r2:LocatedIn]->(z) "
				+ "MATCH (z)-[r3:LocatedIn]->(c) "
				+ "Return c.City + ' ' + z.Nr + ' ' + s.Street + ' ' + s.Nr ";
		
		return (addNoteGetId(insturctions, params));
	}
	
	public String fetchGPSfromKnowAdresse(String id) {//IK IK copy paste but Readability tho
		Map<String, Object> params = Map.of("NId", Integer.parseInt(id));
		
		String insturctions = "MATCH (nm:Emergency) "
				+ "WHERE id(nm) = $NId "
				+ "MATCH (nm)-[r1:LocatedAt]->(s) "
				+ "MATCH (s)-[r3:LocatedIn]->(z) "
				+ "MATCH (z)-[r4:LocatedIn]->(c) "
				+ "CALL apoc.spatial.geocodeOnce(s.Nr + ' ' + s.Street + ' ' + z.Nr + ' ' + c.City + ' GERMANY') "
				+ "YIELD location "
				+ "Return location.latitude + ' ' + location.longitude";
		
		String GPS = addNoteGetId(insturctions, params);;/*
		try{
			GPS = addNoteGetId(insturctions, params);
		}catch(Exception e){
			GPS = "";
		}*/
		
		
		return (GPS);
	}

	//===============================================LOOKUP SECTION=======================================
	
	
	
	
	public String LookUpDepartment(String name, String instructions) {//TODO kill this bad boy
		String id;
		try(Session session = driver.session()){
			id = session.writeTransaction(new TransactionWork<String>() {
			@Override
			public String execute(Transaction tx) {
				Result result = tx.run("MATCH (n:Department{name: $name})" + "Return id(n)",
						parameters("name", name));
				return result.single().get(0).toString();
				}
			});
		}
		catch(Exception e){
			throw e;
		}
		return id;
	}
	
	
	//===============================================CREATING INSTRUCTIONS=====================================
	public void addText() {
		final String Text = "";
		
		Map<String, Object> params = Map.of("Text", Text);
		
		String insturction = "CREATE (n:NLdata)" + "SET n.Text = $Text " + 
				"Return id(n)";
		
		addNoteGetId(insturction, params);
	}
	
	public void CreateEmergencyCodeNode(String name, String desc, String[] Dept, int i) {
		Map<String, Object> params = Map.of("name", name, "description", desc, "deptName", Dept[i]);

		String insturction = "MATCH (m:Department{name: $deptName})"
				+ "MERGE (n:EmergencyCode{name: $name, description: $description}) "
				+ "CREATE (m)-[r:Handles]->(n) " 
				+ "SET n.name = $name, n.description = $description " 
				+ "Return id(n)";
		addNoteGetId(insturction, params);
		
		if(i < Dept.length - 1) {
			i++;
			CreateEmergencyCodeNode(name, desc, Dept, i);
		}
		else {
			return;
		}
	}
	
	public void SetupNeo4j() {//this funk is kept to sperate all neo4j stuff from main
		createDepartment("Police");
		createDepartment("Medical");
		createDepartment("Fire");
	}
	
	private void createDepartment(String name) {
		Map<String, Object> params = Map.of("name", name);
		
		String insturction = "MERGE (n:Department{name: $name}) " + "SET n.name = $name " +
				"Return id(n)";
		
		addNoteGetId(insturction, params);
	}
	
	//===============================================CREATING THE EMERGENCY REPORT
	private String CreateEmergencyReport(String code) {//The most plain
		Map<String, Object> params = Map.of("name", code);
		
		String insturction = "MATCH (n:EmergencyCode{name: $name})"
				+ "CREATE (m:Emergency)-[r:ReportsA]->(n)" 
				+ "Return id(m)";
		
		return(addNoteGetId(insturction, params));
	}
	
	public String CreateEmergencyReport(final String code, String city, String zip, String street, String Nr) {//The most plain
		String id = CreateEmergencyReport(code);
		addAdressInfo(city, zip, street, Nr, id);
		return(id);
	}
	
	//===============================================CREATING THE ADRESS
	public void addAdressInfo(String city, String zip, String street, String nr, String id) {
		addCity(city, addZip(zip, addStreet(street, nr, id)));
	}

	private String addStreet(String street, String nr, String id) {
		Map<String, Object> params = Map.of("Street", street, "Nr", nr, "NId", Integer.parseInt(id));
		
		String insturction = "MATCH (nm:Emergency) "
				+ "WHERE id(nm) = $NId "
				+ "MERGE (n:Street{Street: $Street, Nr: $Nr}) "
				+ "MERGE (nm)-[r:LocatedAt]->(n) "
				+ "Return id(n)";
		
		return addNoteGetId(insturction, params);
	}

	private String addZip(String zip, String streetId) {
		Map<String, Object> params = Map.of("Zip", zip, "StreetId", Integer.parseInt(streetId));
		
		String insturction = "MATCH (nm:Street) "
				+ "WHERE id(nm) = $StreetId "
				+ "MERGE (n:Zip{Nr: $Zip}) "
				+ "MERGE (nm)-[r:LocatedIn]->(n) "
				+ "Return id(n)";
		
		return addNoteGetId(insturction, params);
	}
	
	private String addCity(String city, String zipId) {
		Map<String, Object> params = Map.of("City", city, "ZipId", Integer.parseInt(zipId));
		
		String insturction = "MATCH (nm:Zip) "
				+ "WHERE id(nm) = $ZipId "
				+ "MERGE (n:City{City: $City}) "
				+ "MERGE (nm)-[r:LocatedIn]->(n) "
				+ "Return id(n)";
		
		return addNoteGetId(insturction, params);
	}
	//===============================================CREATING THE VERSION
	public boolean checkVersion(int version) {
		Map<String, Object> params = Map.of("V", version);
		
		String insturction = "MERGE (n:Version{Version: $V})"

				+ "Return id(n)";
		
		addNoteGetId(insturction, params);
		
		return false;
	}

	//===============================================CREATING NODES=====================================	
	private String addNoteGetId(String instructions, Map<String, Object> params) {
		String id;
		try(Session session = driver.session()){
			id = session.writeTransaction(new TransactionWork<String>() {
			@Override
			public String execute(Transaction tx) {
				Result result = tx.run(instructions,
						params);
				return result.single().get(0).toString();
				}
			});
		}
		
		return id;
	}
	
	private String addNoteGetArray(String instructions, Map<String, Object> params) {
		String id;
		try(Session session = driver.session()){
			id = session.writeTransaction(new TransactionWork<String>() {
			@Override
			public String execute(Transaction tx) {
				Result result = tx.run(instructions,
						params);
				return result.single().get(0).toString();
				}
			});
		}
		
		return id;
	}
	
}
