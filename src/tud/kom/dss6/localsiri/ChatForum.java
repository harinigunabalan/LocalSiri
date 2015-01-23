package tud.kom.dss6.localsiri;

import java.util.List;

import tud.kom.dss6.localsiri.IBMDataObjects.Post;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import bolts.Continuation;
import bolts.Task;

import com.ibm.mobile.services.data.IBMDataObject;
import com.ibm.mobile.services.data.IBMQuery;

public class ChatForum  extends ListActivity {
	
	//protected ListView chat;
	List<Post> postList;
	String topicID;
	LocalSiriApplication lsApplication;
	public static final String CLASS_NAME = "ChatForum";
	EditText sendText;
	String myMac;
	ChatAdapter chatAdapter;
	ProgressDialog progressDialog;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_forum);
        
        Intent intent = getIntent();
        
        String topic_heading = intent.getStringExtra("Topic_name"); 
        setTitle(topic_heading);
        //chat = (ListView) this.findViewById(R.id.chatForum);
        sendText = (EditText) this.findViewById(R.id.send_text);
               
        /* Use application class to maintain global state. */
		lsApplication = (LocalSiriApplication) getApplication();
		postList = lsApplication.getPostList(); 
        
        topicID = intent.getStringExtra(MyTopics.TOPIC_CHAT);
        System.out.println("Topic ID from received Intent is: " + topicID);
        
        
        
        //new ProgressDialog(this);
        progressDialog = ProgressDialog.show(this,
                "Please Wait",
                "Loading previous discussions", true, false);
        
        queryTopicForPosts();
    
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    public void queryTopicForPosts(){
    	try {
    		    
		/* Set the Device MAC address*/    	
		WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
		WifiInfo wInfo = wifiManager.getConnectionInfo();
		myMac = wInfo.getMacAddress();    		
		
    	Log.e(CLASS_NAME, "Entered queryTopicForPosts");
		IBMQuery<Post> query = IBMQuery.queryForClass(Post.class);		  	
    	query.whereKeyEqualsTo("TopicID", topicID);
		query.find().continueWith(new Continuation<List<Post>, Void>() {

			@Override
			public Void then(Task<List<Post>> task) throws Exception {
                final List<Post> objects = task.getResult();
                 // Log if the find was cancelled.
                if (task.isCancelled()){
                    Log.e(CLASS_NAME, "Exception : Task " + task.toString() + " was cancelled.");
                }
				 // Log error message, if the find task fails.
				else if (task.isFaulted()) {
					Log.e(CLASS_NAME, "Exception : " + task.getError().getMessage());
				}				
				 // If the result succeeds, load the list.
				else {
					Log.e(CLASS_NAME, "Data Fetched successfully");                                                                        
					postList.clear();
					for(IBMDataObject IBM_Post:objects) {
						
						Post post = (Post) IBM_Post;
						
						if(!(post.getMac() == null)){													
							if(post.getMac().equalsIgnoreCase(myMac)){
								post.setIsMine(true);
								System.out.println("MAC is: " +post.getMac() + " -- "+ post.getIsMine());
							}
						}
						else{
							post.setIsMine(false);
							System.out.println("MAC is: " +post.getMac() + " -- "+ post.getIsMine());							
						}						
                        postList.add(post);                        
                        Log.e(CLASS_NAME, "Inside for loop");
                    } 															
					listPosts();										
				}
				return null;
			}
		},Task.UI_THREAD_EXECUTOR);		       
		
    	} catch(Exception e){
    		
    	}
    }
    
    protected void listPosts() {
		
		int j = postList.size();
		System.out.println("No. of posts fetched:" + j);
		Log.e(CLASS_NAME, "No. of posts fetched: "+j);                					
		
    	 if(!postList.isEmpty()){
    		 progressDialog.dismiss();
	        	Log.e(CLASS_NAME, "Inside List");
	        	chatAdapter = new ChatAdapter(this, postList);			        	
	        	Log.e(CLASS_NAME, "After Chat Adapter");
	        	setListAdapter(chatAdapter);	        		        
	        	
	        	Log.e(CLASS_NAME, "It shud work!");			        	
	        }
	        else
	        {
	        	progressDialog.dismiss();
	        }					
	}
    
	public void sendMessage(View v)
	{
		String newMessage = sendText.getText().toString().trim(); 
		if(newMessage.length() > 0)
		{
			sendText.setText("");			
			Post post = new Post();
			
	    	/* Set the Device MAC address*/    	
	    	WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
	    	WifiInfo wInfo = wifiManager.getConnectionInfo();
	    	String macAddress = wInfo.getMacAddress();	    				
	    	
	    	/* Set the Post Content */
	    	post.setMac(macAddress);	    		    	
	    	post.setPostContent(newMessage); 	    	
	    	post.setTopic_ID(topicID);		               			    	
	    	post.save().continueWith(new Continuation<IBMDataObject, Void>() {         			    		
	    		
            	@Override 
            	public Void then(Task<IBMDataObject> task) throws Exception { 
            		// Log if the save was cancelled. 
            		if (task.isCancelled()){ 
            			Log.e(CLASS_NAME, "Post Exception : Task " + task.toString() + " was cancelled."); 
            		} 
            		// Log error message, if the save task fails. 
            		else if (task.isFaulted()) { 
            			Log.e(CLASS_NAME, "Post Exception : " + task.getError().getMessage()); 
            		} 
            		// If the result succeeds,  
            		else { 
	            		Log.i(CLASS_NAME, " Post Successfully saved in Mobile Cloud");	        	            			        	            		
	            		Toast.makeText(ChatForum.this, "Your Question is Posted", Toast.LENGTH_SHORT).show();	        	            			        	            			            			            			        	            			        	            			            			            		
            		} 
            		return null; 
            	} 
            });
	    	post.setIsMine(true);
	    	addNewPost(post);
		}		
	}
	
	void addNewPost(Post post)
	{
		post.setIsMine(true);
		postList.add(post);
		chatAdapter.notifyDataSetChanged();
		getListView().setSelection(postList.size()-1);
	}
	
}