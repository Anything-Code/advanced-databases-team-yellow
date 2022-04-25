package net.codejava;

import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;
import org.neo4j.driver.Session;
import org.neo4j.driver.Result;
import org.neo4j.driver.Transaction;
import org.neo4j.driver.TransactionWork;
import org.neo4j.driver.types.Node;

import static org.neo4j.driver.Values.parameters;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
public final class Neo4jDBConnect implements AutoCloseable{
	private final Driver driver;
	public Neo4jDBConnect(String uri, String user, String password) {
		driver = GraphDatabase.driver(uri, AuthTokens.basic(user, password));
	}
	
	public void close() throws Exception{
		driver.close();
	}

	//===============================================LOOKUP SECTION=======================================
	public String LookUpDepartment(String name) {
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
	public void CreateEmergencyCodeNode(String name, String desc, String[] Dept, int i) {
		Map<String, Object> params = new HashMap<>();
		params.put("name", name);
		params.put("description", desc);
		params.put("deptName", Dept[i]);
		String insturction = "MATCH (m:Department{name: $deptName})"
				+ "MERGE (n:EmergencyCode{name: $name, description: $description}) "
				+ "CREATE (m)-[r:Handles]->(n) " 
				+ "SET n.name = $name, n.description = $description " 
				+ "Return id(n)";
		addNote(insturction, params);
		
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
		Map<String, Object> params = new HashMap<>();
		params.put("name", name);
		String insturction = "MERGE (n:Department{name: $name}) " + "SET n.name = $name " +
				"Return id(n)";
		addNote(insturction, params);
	}
	
	//===============================================CREATING THE EMERGENCY REPORT
	public String CreateEmergencyReport(String code) {//The most plain
		Map<String, Object> params = new HashMap<>();
		params.put("name", code);
		
		String insturction = "MATCH (n:EmergencyCode{name: $name})"
				+ "CREATE (m:Emergency)-[r:ReportsA]->(n)" 
				+ "MERGE (m)-[rr:LocatedAt]->(nm:Adresse{}) "
				+ "Return id(m)";
		
		return(addNoteGetId(insturction, params));
	}
	
	public String CreateEmergencyReport(final String code, String city, String zip, String street, String Nr) {//The most plain
		addAdressInfo(city, zip, street, Nr);
		
		Map<String, Object> params = new HashMap<>();
		params.put("name", code);
		params.put("City", city);
		params.put("Zip", zip);
		params.put("Street", street);
		
		String insturction = "MATCH (n:EmergencyCode{name: $name}), (nm:Adresse{City: $City, Zip: $Zip, Street: $Street}) "
				+ "CREATE (m:Emergency)-[r:ReportsA]->(n) " 
				+ "MERGE (m)-[rr:LocatedAt]->(nm) "
				+ "Return id(m)";
		
		return(addNoteGetId(insturction, params));
	}
	
	public void addText() {
		final String Text = "";
		
		Map<String, Object> params = new HashMap<>();
		params.put("Text", Text);
		
		String insturction = "CREATE (n:NLdata)" + "SET n.Text = $Text " + 
				"Return id(n)";
		
		addNote(insturction, params);
	}
	
	//===============================================CREATING THE ADRESS
	public void addAdressInfo(String City, String Zip, String Street, String Nr) {
		Map<String, Object> params = new HashMap<>();
		params.put("City", City);
		params.put("Zip", Zip);
		params.put("Street", Street);
		params.put("Nr", Nr);
		
		String insturction = "MERGE (n:Adreess{City: $City, Zip: $Zip, Street: $Street, Nr: $Nr})" 
				+ "SET n.City = $City, n.Zip = $Zip, n.Street = $Street, n.Nr = $Nr "
				+ "Return id(n)";
		
		addNote(insturction, params);
	}
	//===============================================EDIT THE ADRESS
	public void addAdressInfoZip(String zip, String id) {//we can consider if this could be done better but i can't think of a great way atm
		Map<String, Object> params = new HashMap<>();
		params.put("Zip", zip);
		params.put("NId", Integer.parseInt(id));
		System.out.println(id);
		
		String insturction = "MATCH (nm:Emergency) "
				+ "WHERE id(nm) = $NId "
				+ "MATCH (nm)-[rr:LocatedAt]->(n:Adresse) "
				+ "SET n.Zip = $Zip "
				+ "Return id(nm)";
		
		addNote(insturction, params);
	}
	
	//===============================================CREATING NODES=====================================	
	private void addNote(String instructions, Map<String, Object> params) {
		try(Session session = driver.session()){
			session.writeTransaction(new TransactionWork<String>() {
			@Override
			public String execute(Transaction tx) {
				Result result = tx.run(instructions,
						params);
				return result.single().get(0).toString();
				}
			});
		}
	}
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
	
}
