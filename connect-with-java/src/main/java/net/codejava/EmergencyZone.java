package net.codejava;

public final class EmergencyZone {
	final String name;
	final String lat;
	final String lng;
	final String radius;
	
	EmergencyZone(String name, String lat, String lng, String radius){
		this.name = name;
		this.lat = lat;
		this.lng = lng;
		this.radius = radius;
	}
}
