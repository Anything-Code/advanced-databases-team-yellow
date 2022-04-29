package net.codejava;

public final class EmergencyReport {
	final Neo4jDBConnect neo4j;
	final boolean missingAdress = false;
	final String myId;
	
	EmergencyReport(Neo4jDBConnect neo4jClient){
		neo4j = neo4jClient;
		myId = neo4jClient.CreateEmergencyReport("Code Adam", null, null, null, null);
	}
	
	EmergencyReport(Neo4jDBConnect neo4jClient, String City) throws Exception{
		neo4j = neo4jClient;
		myId = neo4jClient.CreateEmergencyReport("Code Adam", "BERLIN", "10785", "Potsdamer str", "61");
		//myId = neo4jClient.CreateEmergencyReport("Code Adam", "HEIDELBERG", "69124", "Maria Probst strasse", "3");
		//throw new Exception("Not Implemented");
	}
	
	
	//=============================================FINDING THE ADRESSE======================================================
	public void updateZip(String zip) {
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
