package net.codejava;

import org.neo4j.driver.AuthTokens;
import org.neo4j.driver.Driver;
import org.neo4j.driver.GraphDatabase;
import org.neo4j.driver.Session;
import org.neo4j.driver.Result;
import org.neo4j.driver.Transaction;
import org.neo4j.driver.TransactionWork;

import static org.neo4j.driver.Values.parameters;
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
}
