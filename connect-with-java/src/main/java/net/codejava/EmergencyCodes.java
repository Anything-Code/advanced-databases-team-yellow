package net.codejava;

public class EmergencyCodes {
	public EmergencyCodes(Neo4jDBConnect neoConnection, String[] neo4jDepartmentName, String codeName,
			String codeDescription) {
		String name = codeName;
		String description = codeDescription;
		neoConnection.CreateEmergencyCodeNode(name, description, neo4jDepartmentName, 0);
		
	}
}
