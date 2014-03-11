package hu.Edudroid.WhoIsThis;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.facebook.android.*;
import com.facebook.android.Facebook.DialogListener;

public class FacebookFeed extends Activity implements DialogListener, OnClickListener
 {
	private static final String FB_APP_ID = "183030021739249";
    private static final String[] FB_AUTHORIZATIONS = { "friends_about_me", "friends_birthday", "friends_hometown", 
    													"friends_location", "friends_photos", "	friends_website" };
	private String fb_jsonString, fb_id, contact_name;
    private Facebook facebook = new Facebook(FB_APP_ID);


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.facebook);
        facebook.getAccessToken();
        Bundle in = super.getIntent().getExtras();
        fb_id = in.getString("fb_id");
        contact_name = in.getString("name");

        facebook.authorize(this, FB_AUTHORIZATIONS, this);
    }
    
    @Override
    public void onComplete(Bundle values) {
		TextView t1 = (TextView) findViewById(R.id.FBTextView1);
		TextView t2 = (TextView) findViewById(R.id.FBTextView2);
		TextView t3 = (TextView) findViewById(R.id.FBTextView3);
		TextView t4 = (TextView) findViewById(R.id.FBTextView4);
		TextView t5 = (TextView) findViewById(R.id.FBTextView5);
		
        try {
        	if(fb_id == null || fb_id.compareTo("") == 0 )
        		 fb_id = searchForId(contact_name, getFriendList());
        	fb_jsonString = facebook.request(fb_id);
		} catch (MalformedURLException e) {
		} catch (IOException e) {}
		
	    try {	    	
	        JSONObject json = new JSONObject(fb_jsonString);
	        t1.setText(json.getString("first_name"));
	        t2.setText(json.getString("last_name"));
	        t3.setText(json.getString("birthday"));
	        t4.setText(json.getString("website"));	        
	        JSONObject location = json.getJSONObject("location");
			t5.setText(location.getString("name"));

	    } catch (JSONException e) {}
    }

    @Override
    public void onFacebookError(FacebookError error) {
    	finish();
    }

    @Override
    public void onError(DialogError e) {
    	finish();
    }

    @Override
    public void onCancel() {
    	finish();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
    	super.onActivityResult(requestCode, resultCode, data);
    	facebook.authorizeCallback(requestCode, resultCode, data);
    }

	@Override
	public void onClick(View v) {
	
	}
	
	private ArrayList<HashMap<String, String>> getFriendList() {
		ArrayList<HashMap<String, String>> friendlist = new ArrayList<HashMap<String, String>>();
	    try {
	        JSONObject json = new JSONObject(facebook.request("me/friends"));
	        JSONArray data = json.getJSONArray("data");

	        for (int i = 0; i < data.length(); i++) {
	            JSONObject data_item = data.getJSONObject(i);
	            HashMap<String, String> friend = new HashMap<String, String>(data.length());
	            friend.put("name", data_item.getString("name"));
	            friend.put("id", data_item.getString("id"));

	            friendlist.add(friend);
	        }
		}
	    catch (MalformedURLException e) {}
		catch (IOException e) {}
	    catch (JSONException e) {}
	    
	    return friendlist;
	}
	
	private String searchForId(String name, ArrayList<HashMap<String, String>> friendlist) {
		String id = null;
		for(int i=0; i < friendlist.size(); i++) {
			HashMap<String, String> friend = (HashMap<String, String>) friendlist.get(i);
			if(friend.get("name").compareToIgnoreCase(name) == 0) id = friend.get("id");
		}
		return id;
	}

}