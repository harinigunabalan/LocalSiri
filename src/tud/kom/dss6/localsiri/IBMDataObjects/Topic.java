package tud.kom.dss6.localsiri.IBMDataObjects;

/**
 * This is an IBM Data Object to persist the Topics 
 * on the IBM Mobile Data Cloud
 *
 * @author Harini Gunabalan	
 * @author Hariharan Gandhi
 */

import com.ibm.mobile.services.data.IBMDataObject;
import com.ibm.mobile.services.data.IBMDataObjectSpecialization;
import com.ibm.mobile.services.data.IBMDataRelation;

@IBMDataObjectSpecialization("Topic")
public class Topic extends IBMDataObject{
	public static final String CLASS_NAME 		= "Topic";	
	public static final String MAC 				= "DeviceMAC";
	public static final String TOPIC_TS 		= "TopicTimeStamp";
	public static final String LATITUDE 		= "Latitude";
	public static final String LONGITUDE 		= "Longitude";
	public static final String TOPIC 			= "TopicHeading";
	
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
	 * Gets the TopicTS 
	 * @return String TopicTS
	 */	
	public String getTopicTS(){
		return (String) getObject(TOPIC_TS);			
	}
	
	/*
	 * Sets the Topic TS
	 * @param TopicTS
	 */
	public void setTopicTS(String TopicTS){
		setObject(TOPIC_TS,(TopicTS != null) ? TopicTS : "" );
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

	/*
	 * Gets the Topic Heading
	 * @return String Topic Heading
	 */	
	public String getTopic(){
		return (String) getObject(TOPIC);
	}
	
	/*
	 * Sets the Topic Heading
	 * @param Topic Heading
	 */
	public void setTopic(String Topic){
		setObject(TOPIC,(Topic!= null) ? Topic : "" );
	}	

	@Override
	 public String toString() {
        return getTopic();
    }
	
}
