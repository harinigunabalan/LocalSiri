package tud.kom.dss6.localsiri.IBMDataObjects;

/**
 * This is an IBM Data Object to persist the location 
 * on the IBM Mobile Data Cloud
 *
 * @author Harini Gunabalan	
 * @author Hariharan Gandhi
 */

public class GeoPointLocation{
	
	double latitude;
	double longitude;
	/*
	 * Gets the Latitude 
	 * @return double Latitude
	 */	
	public double getLatitude(){
		return latitude;
	}
	
	/*
	 * Sets the Latitude
	 * @param Latitude
	 */
	public void setLatitude(double Latitude){
		this.latitude = Latitude;
	}
	
	/*
	 * Gets the Longitude 
	 * @return double Longitude
	 */	
	public double getLongitude(){
		return longitude;
	}
	
	/*
	 * Sets the Longitude
	 * @param double Longitude
	 */
	public void setLongitude(double Longitude){
		this.longitude = Longitude;
	}	
}
