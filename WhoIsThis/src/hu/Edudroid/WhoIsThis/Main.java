package hu.Edudroid.WhoIsThis;

import java.util.ArrayList;
import java.util.List;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class Main extends ListActivity {
		private static Cursor cursor;
		private ContactList contactlist;
		private ContactsAdapter adapter;
		private static ContactAPI contactapi = new ContactAPI();
	    private ProgressDialog collectingdialog;
	    private Handler handler = new Handler() {
	        	public void handleMessage(Message msg) {
	        		collectingdialog.dismiss();
	        		setListAdapter(adapter);
	        	}};
		
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

		contactapi.setCr(getContentResolver());
		contactapi.setCur(cursor);
		
        collectingdialog = ProgressDialog.show(this, "", 
                getResources().getString(R.string.Msg_CollecingContacts), true);
        Thread CollectingThread = new Thread(new Runnable() {
        	public void run() {
        		contactlist = contactapi.newBasicContactList(ContactsContract.Contacts.LAST_TIME_CONTACTED+" DESC", false);
        		adapter = new ContactsAdapter(Main.this, R.layout.list_item, R.id.text1, contactlist.getContacts());
   				handler.sendEmptyMessage(0);
         	}
        }); CollectingThread.start();

    }
    
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		Contact selected = (Contact) l.getItemAtPosition(position);
		Intent contactdetails = new Intent();

		contactdetails.putExtra("id", selected.getId());
		contactdetails.putExtra("name", selected.getDisplayName());
		contactdetails.putExtra("lastcontact", selected.getLastContact());

		contactdetails.setClassName("hu.Edudroid.WhoIsThis","hu.Edudroid.WhoIsThis." + ContactDetails.class.getSimpleName());
 		startActivity(contactdetails);
	}
	
	@Override
	public void onAttachedToWindow() {
		super.onAttachedToWindow();
	    getWindow().setFormat(PixelFormat.RGBA_8888);
	}
	
	private class ContactsAdapter extends ArrayAdapter<Contact> {
		private LayoutInflater mInflater;
		
		private ContactsAdapter(Context context, int resource, int textViewResourceId, ArrayList<Contact> arrayList) {
            super(context, resource, textViewResourceId, arrayList);
              mInflater = LayoutInflater.from(context);
		}
		
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) { convertView = mInflater.inflate(R.layout.list_item, parent, false); }
			if(getItem(position).getPhoto() != null)
			((ImageView) convertView.findViewById(R.id.ContactPic)).setImageBitmap(getItem(position).getPhoto());
			else
			((ImageView) convertView.findViewById(R.id.ContactPic)).setImageResource(R.drawable.no_contact_picture);
			((TextView) convertView.findViewById(R.id.text1)).setText(getItem(position).getDisplayName());
			((TextView) convertView.findViewById(R.id.text2)).setText(getItem(position).getLastContactString());
			//((TextView) convertView.findViewById(R.id.text2)).setText(String.valueOf(contactlist.get(position).getLastContact()));
			/*((ImageView) convertView.findViewById(R.id.icon)).setImageBitmap(
			(position & 1) == 1 ? mIcon1 : mIcon2);*/
			return convertView;
		}
	}
    
}