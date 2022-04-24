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
public class Neo4jDBConnect implements AutoCloseable{
	private final Driver driver;
	public Neo4jDBConnect(String uri, String user, String password) {
		driver = GraphDatabase.driver(uri, AuthTokens.basic(user, password));
	}
	public void close() throws Exception{
		driver.close();
	}
	public void printGreeting (final String message) {
		try(Session session = driver.session()){
			String greeting = session.writeTransaction(new TransactionWork<String>() {
			@Override
			public String execute(Transaction tx) {
				Result result = tx.run("CREATE (a:Greating)" + "SET a.message = $message " + "Return a.message +', from node ' + id(a)",
						parameters("message", message));
				return result.single().get(0).asString();
				}
			});
			System.out.println(greeting);
		}
	}
	public static void mainConnector() throws Exception{
		try(Neo4jDBConnect greeter = new Neo4jDBConnect("bolt://localhost:7687","neo4j","123")){
			greeter.printGreeting("Hello world");
		}
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
	
	
	//===============================================CREATION SECTION=====================================
	public void CreateEmergencyCodeNode(String name, String desc, String[] Dept, int i) {
		Map<String, Object> params = new HashMap<>();
		params.put("name", name);
		params.put("description", desc);
		params.put("deptName", Dept[i]);
		String insturction = "MATCH (m:Department{name: $deptName})"
				+ "MERGE (n:EmergencyCode{name: $name, description: $description}) "
				+ "CREATE (m)-[r:Handles]->(n) " 
				+ "SET n.name = $name, n.description = $description " 
				+ "Return ', from node ' + id(n)";
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
				"Return ', from node ' + id(n)";
		addNote(insturction, params);
	}
	
	public void CreateEmergencyReport() {
		final String EmergencyCode = "Code Blue";
		Map<String, Object> params = new HashMap<>();
		params.put("EmergencyCode", EmergencyCode);
		
		String insturction = "CREATE (n:Emergency)" + "SET n.EmergencyCode = $EmergencyCode " + 
				"Return ', from node ' + id(n)";
		
		addNote(insturction, params);
		
		System.out.println();
	}
	
	public void addText() {
		final String Text = "";
		
		Map<String, Object> params = new HashMap<>();
		params.put("Text", Text);
		
		String insturction = "CREATE (n:NLdata)" + "SET n.Text = $Text " + 
				"Return ', from node ' + id(n)";
		
		addNote(insturction, params);
	}
	
	public void addAdressInfo() {
		final String City = "Heidelberg";
		final String Zip = "69123";
		final String Street = "MPS: 3";
		Map<String, Object> params = new HashMap<>();
		params.put("City", City);
		params.put("Zip", Zip);
		params.put("Street", Street);
		
		String insturction = "CREATE (n:Adreess)" + "SET n.City = $City, n.Zip = $Zip, n.Street = $Street " + 
				"Return ', from node ' + id(n)";
		
		addNote(insturction, params);
	}
	
	private void addNote(String instructions, Map<String, Object> params) {
		try(Session session = driver.session()){
			session.writeTransaction(new TransactionWork<String>() {
			@Override
			public String execute(Transaction tx) {
				Result result = tx.run(instructions,
						params);
				return result.single().get(0).asString();
				}
			});
		}
	}
	
}
