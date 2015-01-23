package tud.kom.dss6.localsiri;

import java.util.List;

import tud.kom.dss6.localsiri.IBMDataObjects.Post;
import bolts.Continuation;
import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

public class ChatAdapter extends BaseAdapter{

	public static final String CLASS_NAME = "Chat Adapter";
	
	private Context mContext;
	List<Post> mPosts;
	
	public ChatAdapter(Context context, List<Post>  postList) {
		super();
		this.mContext = context;
		this.mPosts = postList;
	
	}	

	@Override
	public int getCount() {
		return mPosts.size();
	}	
	
	@Override
	public Object getItem(int position) {
		Log.e(CLASS_NAME, "Inside get Item method of chat adapter");
		return mPosts.get(position);		
	}
	
	@Override
	public View getView(int position, View view, ViewGroup viewGroup) {
		Log.e(CLASS_NAME, "Inside get View method of chat adapter");
		Post post = (Post) this.getItem(position);
		
		ViewContainer vc;
		
		if(view == null){
			vc = new ViewContainer();
			view = LayoutInflater.from(mContext).inflate(R.layout.chat_forum_list_view, viewGroup, false);
			vc.msg = (TextView) view.findViewById(R.id.post);
			System.out.println("Message: "+vc.msg);
			view.setTag(vc);
		}
		else{
			vc = (ViewContainer) view.getTag();
		}		
		
		vc.msg.setText(post.getPostContent());
		
		LayoutParams lp = (LayoutParams) vc.msg.getLayoutParams();
		
		if(post.getIsMine() == false){
			
			System.out.println("NOT MINE");
			
			vc.msg.setBackgroundResource(R.drawable.chat_patch1);
			lp.gravity = Gravity.START;
			
		}else{
			System.out.println("MINE");
			vc.msg.setBackgroundResource(R.drawable.chat_patch2);
			lp.gravity = Gravity.END;
		}				
		
		vc.msg.setLayoutParams(lp);		
						
		return view;
		
	}
	
	class ViewContainer
	{
		TextView msg;
	}
	
	@Override
	public long getItemId(int position) {
		//Unimplemented, because we aren't using Sqlite.
		return position;
	}
	
}
