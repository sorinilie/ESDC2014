package ro.ucv.ids.smarthotel.utils;

import java.io.Serializable;

public class Room implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7866576069192490229L;
	private String galileoIP;
	private Integer galileoPort;
	private boolean hasBathroom;
	private boolean hasAC;
	private Integer roomID;
	
	public Room(Integer roomID, String galileoIP, Integer galileoPort, boolean hasBathroom, boolean hasAC) {
		this.galileoIP = galileoIP;
		this.hasBathroom = hasBathroom;
		this.roomID = roomID;
		this.hasAC = hasAC;
		this.galileoPort = galileoPort;
	}

	public String getGalileoIP() {
		return galileoIP;
	}
	
	public Integer getGalileoPort() {
		return galileoPort;
	}

	public boolean isHasBathroom() {
		return hasBathroom;
	}

	public boolean isHasAC() {
		return hasAC;
	}

	public Integer getRoomID() {
		return roomID;
	}
	
}
