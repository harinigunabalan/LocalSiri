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

@IBMDataObjectSpecialization("Post")
public class Post extends IBMDataObject{
	public static final String CLASS_NAME 		= "Post";	
	public static final String TOPIC_ID			= "TopicID";
	public static final String MAC 				= "DeviceMAC";
	public static final String POST_CONTENT 	= "PostContent";
	
	public boolean isMine = false;

	/*
	 * Gets the Topic ID
	 * @return String Topic ID
	 */	
	public String getTopic_ID(){
		return (String) getObject(TOPIC_ID);
	}	
	
	/*
	 * Sets the Topic ID
	 * @param Topic ID
	 */
	public void setTopic_ID(String Topic_id){
		setObject(TOPIC_ID,(Topic_id!= null) ? Topic_id : "" );
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
	 * Gets the Post Content
	 * @return String Post Content
	 */	
	public String getPostContent(){
		return (String) getObject(POST_CONTENT);
	}
	
	/*
	 * Sets the Post Content
	 * @param Post Content
	 */
	public void setPostContent(String PostContent){
		setObject(POST_CONTENT,(PostContent != null) ? PostContent : "" );
	}		
	
	/*
	 * Gets the isMine
	 * @return boolean isMine
	 */	
	public boolean getIsMine(){
		return isMine;
	}
	
	/*
	 * Sets the isMine
	 * @param Boolean mine
	 */
	public void setIsMine(boolean mine){
		this.isMine = mine;
	}	
	
}
