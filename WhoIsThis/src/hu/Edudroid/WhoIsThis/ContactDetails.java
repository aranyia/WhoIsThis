package hu.Edudroid.WhoIsThis;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

public class ContactDetails extends Activity implements OnClickListener {
	private Cursor cur;
	private Contact contact = new Contact();
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contact_details);
    	
        ContactAPI contactapi = new ContactAPI();
        contactapi.setCr(getContentResolver());
        contactapi.setCur(cur);
        
        Bundle in = super.getIntent().getExtras();

    	contact.setId(in.getString("id"));
    	contact.setDisplayName(in.getString("name"));
    	contact.setLastContact(in.getLong("lastcontact"));
    	contact.setPhoto(contactapi.getContactPhoto(contact.getId()));
    	contact.setPhone(contactapi.getPhoneNumbers(contact.getId()));
    	contact.setEmail(contactapi.getEmailAddresses(contact.getId()));
    	contact.setImAddresses(contactapi.getIM(contact.getId()));
    	contact.setNotes(contactapi.getContactNotes(contact.getId()));
    	contact.setAddresses(contactapi.getContactAddresses(contact.getId()));
    	contact.setCallHistoryStats(contactapi.getCallHistoryStats(contact.getPhone()));
        
    	TextView ContactName = (TextView) findViewById(R.id.ContactName);
    	ImageView Photo = (ImageView) findViewById(R.id.ContactPicture);
        
        ContactName.setText(contact.getDisplayName());
        if(contact.getPhoto() != null) 
        	Photo.setImageBitmap(contact.getPhoto());
        else
        	Photo.setImageResource(R.drawable.no_contact_picture);

		TableLayout table = (TableLayout) findViewById(R.id.ContactTable);

		createDataCategoryRows(contact.getPhone(), table, R.string.Category_Phone);
		createDataCategoryRows(contact.getEmail(), table, R.string.Category_Email);
		createDataCategoryRows(contact.getImAddresses(), table, R.string.Category_IM);
		createDataCategoryRows(contact.getNotes(), table, R.string.Category_Note);
		createDataCategoryRows(contact.getAddresses(), table, R.string.Category_Address);
		
		Button callhistoryButton = (Button) findViewById(R.id.Button_CallHistory);
		callhistoryButton.setTag(CallHistory.class.getSimpleName());
		callhistoryButton.setOnClickListener(this);
		
		Button callstatisticsButton = (Button) findViewById(R.id.Button_CallStatistics);
		callstatisticsButton.setTag(ShowStatistics.class.getSimpleName());
		callstatisticsButton.setOnClickListener(this);

		Button facebookButton = (Button) findViewById(R.id.Button_Facebook);
		facebookButton.setTag(FacebookFeed.class.getSimpleName());
		facebookButton.setOnClickListener(this);
	}
	
	@Override
	public void onClick(View v) {
		Intent intent = new Intent();
		String tag = (String) v.getTag();

		if(tag == "phone") {
			intent = new Intent(android.content.Intent.ACTION_DIAL, Uri.parse("tel:" + ((TextView) v).getText()));
			Toast.makeText(this, getResources().getString(R.string.Msg_Dial) + contact.getDisplayName(), Toast.LENGTH_LONG).show(); }
		else if(tag == "email") {
			intent = new Intent(android.content.Intent.ACTION_SENDTO, Uri.parse("mailto:" + ((TextView) v).getText()));
			Toast.makeText(this, getResources().getString(R.string.Msg_ComposeEmail) + contact.getDisplayName(), Toast.LENGTH_LONG).show(); }
		else {
			intent.putExtra("id", contact.getId());
			if(tag.compareTo(FacebookFeed.class.getSimpleName()) == 0) {
				intent.putExtra("fb_id", "");
				intent.putExtra("name", contact.getDisplayName());
			}
			intent.setClassName("hu.Edudroid.WhoIsThis", "hu.Edudroid.WhoIsThis." + tag);
		}
		startActivity(intent);
	}
	
	@Override
	public void onAttachedToWindow() {
		super.onAttachedToWindow();
	    getWindow().setFormat(PixelFormat.RGBA_8888);
	}
	
	private void createDataCategoryRows(ArrayList<?> items, TableLayout table, int titleResourceId) {
			TableRow rowCat = new TableRow(this);			
			rowCat.setLayoutParams( new TableRow.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
			
			TextView category = new TextView(this);
			category.setTextColor(R.color.text_color);
			category.setText(titleResourceId);

			rowCat.addView(category);
			table.addView(rowCat);
        
		for(int i=0; i < items.size(); i++) {
	        View rowView = getLayoutInflater().inflate(R.layout.contact_content_item, null);

	        TextView type = ((TextView)rowView.findViewById(R.id.contact_content_type));
	        TextView data = ((TextView)rowView.findViewById(R.id.contact_content_data));
			
			if(items.get(i).getClass() == Phone.class) {
				Phone phone = (Phone) items.get(i);
		        type.setText((Integer.valueOf(phone.getType()) != 0)? 
		        		getResources().getStringArray(R.array.phone_types)[Integer.valueOf(phone.getType())] : phone.getTypeLabel() );
		        data.setText(phone.getNumber()); data.setTag("phone"); data.setOnClickListener(this);
			}
			else if(items.get(i).getClass() == Email.class) {
				Email email = (Email) items.get(i);
		        type.setText((Integer.valueOf(email.getType()) != 0)?
		        		getResources().getStringArray(R.array.email_types)[Integer.valueOf(email.getType())] : email.getTypeLabel() );
		        data.setText(email.getAddress()); data.setTag("email"); data.setOnClickListener(this);
			}
			else if(items.get(i).getClass() == IM.class) {
				IM im = (IM) items.get(i);
				type.setText((Integer.valueOf(im.getType()) != 0)? 
						getResources().getStringArray(R.array.address_and_im_types)[Integer.valueOf(im.getType())] : im.getTypeLabel() );                                                              
				String imProtocol = (Integer.valueOf(im.getProtocol()) != 0)?
						getResources().getStringArray(R.array.im_protocol)[Integer.valueOf(im.getType())] : im.getProtocolLabel();
				data.setText(imProtocol + ": " + im.getName());
			}
			else if(items.get(i).getClass() == String.class) {
				String note = (String) items.get(i);
				type.setVisibility(View.GONE);
				data.setText(note);
			}
			else if(items.get(i).getClass() == Address.class) {
				Address address = (Address) items.get(i);
				type.setText((Integer.valueOf(address.getType()) != 0)? 
						getResources().getStringArray(R.array.address_and_im_types)[Integer.valueOf(address.getType())] : address.getTypeLabel() );
				data.setText(address.toString());
			}

			TableRow row = new TableRow(this);
	        row.addView(rowView);
	        table.addView(row);
		}
	}

}

