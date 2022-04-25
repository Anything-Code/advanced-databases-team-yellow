package net.codejava;

public final class EmergencyCodes {
	public boolean RequiresUniqeLocation = false;

	public EmergencyCodes(Neo4jDBConnect neoConnection, String[] neo4jDepartmentName, String codeName, String codeDescription, boolean uLoc) {
		RequiresUniqeLocation = uLoc;
		
		String name = codeName;
		String description = codeDescription;
		neoConnection.CreateEmergencyCodeNode(name, description, neo4jDepartmentName, 0);
	}
	
	public EmergencyCodes(Neo4jDBConnect neoConnection, String[] neo4jDepartmentName, String codeName, String codeDescription) {
		String name = codeName;
		String description = codeDescription;
		neoConnection.CreateEmergencyCodeNode(name, description, neo4jDepartmentName, 0);
		
	}
}
