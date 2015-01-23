package tud.kom.dss6.localsiri.IBMDataObjects;

/**
 * This is an IBM Data Object to persist the location 
 * on the IBM Mobile Data Cloud
 *
 * @author Harini Gunabalan	
 * @author Hariharan Gandhi
 */

import com.ibm.mobile.services.data.IBMDataObject;
import com.ibm.mobile.services.data.IBMDataObjectSpecialization;

@IBMDataObjectSpecialization("CurrentLocation")
public class CurrentPosition extends IBMDataObject{
	public static final String CLASS_NAME = "Location";
	public static final String DATE = "Date";
	public static final String MAC  = "DeviceMAC";
	public static final String LATITUDE = "Latitude";
	public static final String LONGITUDE = "Longitude";
	
	/*
	 * Gets the Date 
	 * @return String Date
	 */	
	public String getDate(){
		return (String) getObject(DATE);
	}
	
	/*
	 * Sets the Date
	 * @param Date
	 */
	public void setDate(String Date){
		setObject(DATE,(Date != null) ? Date : "" );
	}
	
	/*
	 * Gets the Mac 
	 * @return String Mac
	 */	
	public String getMac(){
		return (String) getObject(MAC);
	}
	
	/*
	 * Sets the MAC
	 * @param Mac
	 */
	public void setMac(String Mac){		
		setObject(MAC,(Mac != null) ? Mac : "" );
	}
	
	/*
	 * Gets the Latitude 
	 * @return String Latitude
	 */	
	public String getLatitude(){
		return (String) getObject(LATITUDE);
	}
	
	/*
	 * Sets the Latitude
	 * @param Latitude
	 */
	public void setLatitude(String Latitude){
		setObject(LATITUDE,(Latitude != null) ? Latitude : "" );
	}
	
	/*
	 * Gets the Longitude 
	 * @return String Longitude
	 */	
	public String getLongitude(){
		return (String) getObject(LONGITUDE);
	}
	
	/*
	 * Sets the Longitude
	 * @param String Longitude
	 */
	public void setLongitude(String Longitude){
		setObject(LONGITUDE,(Longitude != null) ? Longitude : "" );
	}	
}
