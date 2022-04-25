package net.codejava;

public final class EmergencyReport {
	final Neo4jDBConnect neo4j;
	final boolean missingAdress = false;
	final String myId;
	
	EmergencyReport(Neo4jDBConnect neo4jClient){
		neo4j = neo4jClient;
		myId = neo4jClient.CreateEmergencyReport("Code Adam");
	}
	
	EmergencyReport(Neo4jDBConnect neo4jClient, String City) throws Exception{
		neo4j = neo4jClient;
		myId = neo4jClient.CreateEmergencyReport("Code Adam", "Heidelberg", "69123", "MPS", "3");
		throw new Exception("Not Implemented");
	}
	
	
	//=============================================FINDING THE ADRESSE======================================================
	public void updateZip(String zip) {
		neo4j.addAdressInfoZip(zip, myId);
	}
	public void updateCity() {
		
	}
	public void updateStreet() {
		
	}
	public void updateNr() {
		
	}
	
	
	
	private void addGPSLocation() {
		
	}	
	
	//public void addPartialAdress
}
