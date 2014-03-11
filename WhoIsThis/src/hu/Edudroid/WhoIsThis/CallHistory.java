package hu.Edudroid.WhoIsThis;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class CallHistory extends ListActivity {
	private static Cursor cursor;
	private Contact contact = new Contact();
	private CallHistoryAdapter adapter;
	private static ContactAPI contactapi = new ContactAPI();
    private Handler handler = new Handler() {
        	public void handleMessage(Message msg) {
        		setListAdapter(adapter);
        	}};
        	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        contactapi.setCr(getContentResolver());
        contactapi.setCur(cursor);
        
        Bundle in = super.getIntent().getExtras();

    	contact.setId(in.getString("id"));

    	Thread CollectingThread = new Thread(new Runnable() {
        	public void run() {
        		contact.setPhone(contactapi.getPhoneNumbers(contact.getId()));
        		contact.setCallHistory(contactapi.getCallHistory(contact.getPhone()));
        		adapter = new CallHistoryAdapter(CallHistory.this, R.layout.callhistory_list_item, R.id.CallHistory_Number, contact.getCallHistory());
   				handler.sendEmptyMessage(0);
         	}
        }); CollectingThread.start();

    }
	
	@Override
	public void onAttachedToWindow() {
		super.onAttachedToWindow();
	    getWindow().setFormat(PixelFormat.RGBA_8888);
	}
	
	private String createDateString(long time) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date(time));

		return String.valueOf(calendar.get(Calendar.YEAR)).substring(2) +
				String.format("/%02d/%02d %02d:%02d", 		
				calendar.get(Calendar.MONTH)+1,
				calendar.get(Calendar.DAY_OF_MONTH),
				calendar.get(calendar.HOUR_OF_DAY),
				calendar.get(Calendar.MINUTE));
	}
	
	private String createDurationString(long duration) {
		int seconds = (int) (duration >= 60 ? duration % 60 : duration);
		int minutes = (int) ((duration = (duration / 60)) >= 60 ? duration % 60 : duration);	
		int hours = (int) (duration = (duration / 60));

		return ((hours == 0) ? "" : (String.valueOf(hours) + ":")) + 
			String.format("%02d:%02d", minutes, seconds);
	}
	
	private class CallHistoryAdapter extends ArrayAdapter<CallHistoryItem> {
		private LayoutInflater mInflater;
		private String[] callTypes = getResources().getStringArray(R.array.call_types);
		private int[] callTypeImages = { R.drawable.call_failedoutgoing, R.drawable.call_incoming, R.drawable.call_outgoing, R.drawable.call_missed };
		
		private CallHistoryAdapter(Context context, int resource, int textViewResourceId, ArrayList<CallHistoryItem> arrayList) {
            super(context, resource, textViewResourceId, arrayList);
              mInflater = LayoutInflater.from(context);
		}
		
		public View getView(int position, View convertView, ViewGroup parent) {
			if (convertView == null) { convertView = mInflater.inflate(R.layout.callhistory_list_item, parent, false); }
			((ImageView) convertView.findViewById(R.id.CallHistory_Type)).setImageResource(callTypeImages[Integer.valueOf(getItem(position).getCallType())]);
			//((TextView) convertView.findViewById(R.id.CallHistory_Type)).setText(callTypes[Integer.valueOf(getItem(position).getCallType())]);
			((TextView) convertView.findViewById(R.id.CallHistory_Number)).setText(getItem(position).getNumber());
			((TextView) convertView.findViewById(R.id.CallHistory_Date)).setText(createDateString(getItem(position).getDate()));
			((TextView) convertView.findViewById(R.id.CallHistory_Duration)).setText(createDurationString(getItem(position).getDuration()));
			return convertView;
		}
	}
}
