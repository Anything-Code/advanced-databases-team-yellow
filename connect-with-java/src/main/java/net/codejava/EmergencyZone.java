package net.codejava;

public final class EmergencyZone {
	final String name;
	final String lat;
	final String lng;
	final String radius;
	final String color;
	
	EmergencyZone(String name, String lat, String lng, String radius, String color){
		this.name = name;
		this.lat = lat;
		this.lng = lng;
		this.radius = radius;
		this.color = color;
	}
}
