package hu.Edudroid.WhoIsThis;

import java.io.InputStream;
import java.util.ArrayList;

import android.net.Uri;
import android.provider.CallLog;

import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.ContactsContract;
import android.content.ContentResolver;

public class ContactAPI {

	private Cursor cur;
	private ContentResolver cr;
	
	
	public Cursor getCur() {
		return cur;
	}

	public void setCur(Cursor cur) {
		this.cur = cur;
	}

	public ContentResolver getCr() {
		return cr;
	}

	public void setCr(ContentResolver cr) {
		this.cr = cr;
	}

	public Intent getContactIntent() {
		return(new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI));
	}
	
	public Bitmap getContactPhoto(String id) {
		Uri uri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, Long.parseLong(id));
		InputStream input =	ContactsContract.Contacts.openContactPhotoInputStream(cr, uri);
		return BitmapFactory.decodeStream(input);
	}
	
	public ArrayList<CallHistoryItem> getCallHistory(ArrayList<Phone> phone) {
		ArrayList<CallHistoryItem> callHistory = new ArrayList<CallHistoryItem>(); 
		String[] projection = { CallLog.Calls._ID, CallLog.Calls.DATE, CallLog.Calls.TYPE, CallLog.Calls.DURATION  };

		for(int i=0; i < phone.size(); i++) {
		 this.cur = this.cr.query(CallLog.Calls.CONTENT_URI, projection, CallLog.Calls.NUMBER + "=?", new String[]{ phone.get(i).getNumber() }, null);
		 if (this.cur.getCount() > 0) {
			while (cur.moveToNext()) {
				long duration = cur.getLong(cur.getColumnIndex(CallLog.Calls.DURATION));
				int calltype = cur.getInt(cur.getColumnIndex(CallLog.Calls.TYPE));
				if(calltype == CallLog.Calls.OUTGOING_TYPE && duration == 0) calltype = 0;
				callHistory.add(0,new CallHistoryItem(
							phone.get(i).getNumber(),
							phone.get(i).getType(),
							cur.getLong(cur.getColumnIndex(CallLog.Calls.DATE)),
							duration,
							calltype));
			}
		 }
		}
		return callHistory;
	}
	
	public long[][] getCallHistoryStats(ArrayList<Phone> phones) {
		long[][] stats = new long[4][2];
		int[] calltype = { CallLog.Calls.INCOMING_TYPE, CallLog.Calls.OUTGOING_TYPE, CallLog.Calls.MISSED_TYPE };
		
		String[] projection = { CallLog.Calls._ID, CallLog.Calls.DATE, CallLog.Calls.TYPE, CallLog.Calls.DURATION  };
		for(int i=0; i < 3; i++)
		{
		long duration_sum = 0; int count = 0, count_failed = 0;
		  for(int j=0; j < phones.size(); j++) {
		  this.cur = this.cr.query(CallLog.Calls.CONTENT_URI, projection, CallLog.Calls.NUMBER + "=?" + " AND " + CallLog.Calls.TYPE + "=?",
																	new String[]{ phones.get(j).getNumber(), String.valueOf(calltype[i]) }, null);
			while (cur.moveToNext()) {
				if(calltype[i] != CallLog.Calls.MISSED_TYPE) {
				long duration = cur.getLong(cur.getColumnIndex(CallLog.Calls.DURATION));
					if(duration != 0)
					  count++;
				 	else count_failed++;
					  duration_sum += duration;
				}
				else count++;
			}
			stats[calltype[i]][0] = count;
			stats[calltype[i]][1] = duration_sum;
		  }
		  stats[0][0] += count_failed;
		  stats[0][1] = 0;
		}
		return(stats);
	}
	
	public ContactList newBasicContactList(String sortOrder, boolean onlyActive) {
		ContactList contacts = new ContactList();
		String id;
		String[] projection = { ContactsContract.Contacts._ID, ContactsContract.Contacts.DISPLAY_NAME, ContactsContract.Contacts.LAST_TIME_CONTACTED, 
								ContactsContract.Contacts.TIMES_CONTACTED, ContactsContract.Contacts.PHOTO_ID };
		
		this.cur = this.cr.query(ContactsContract.Contacts.CONTENT_URI, projection, null, null, sortOrder);
		if (this.cur.getCount() > 0) {
			while (cur.moveToNext()) {
				if(!onlyActive || (onlyActive && cur.getLong(cur.getColumnIndex(ContactsContract.Contacts.LAST_TIME_CONTACTED)) != 0)) {
				Contact c = new Contact();
				id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
				c.setId(id);
				c.setDisplayName(cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)));
				c.setLastContact(cur.getLong(cur.getColumnIndex(ContactsContract.Contacts.LAST_TIME_CONTACTED)));
				c.setContactCount(cur.getInt(cur.getColumnIndex(ContactsContract.Contacts.TIMES_CONTACTED)));
				c.setPhoto(getContactPhoto(id));
				contacts.addContact(c);
				}
			}
		}
		return(contacts);
	}
	
	public ContactList newInactiveContatcList() {
		ContactList contacts = new ContactList();
		String id;
		String[] projection = { ContactsContract.Contacts._ID, ContactsContract.Contacts.DISPLAY_NAME, ContactsContract.Contacts.LAST_TIME_CONTACTED, 
								ContactsContract.Contacts.HAS_PHONE_NUMBER, ContactsContract.Contacts.PHOTO_ID };
		String selection = ContactsContract.Contacts.HAS_PHONE_NUMBER + " = 0 OR " + ContactsContract.Contacts.LAST_TIME_CONTACTED + " = 0";
		this.cur = this.cr.query(ContactsContract.Contacts.CONTENT_URI, projection, selection, null, null);
		if (this.cur.getCount() > 0) {
			while (cur.moveToNext()) {
				Contact c = new Contact();
				id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
				c.setId(id);
				c.setDisplayName(cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)));
				c.setLastContact(cur.getLong(cur.getColumnIndex(ContactsContract.Contacts.LAST_TIME_CONTACTED)));
				contacts.addContact(c);
			}
		}
		return(contacts);
	}
	
	public ContactList newContactList() {
		ContactList contacts = new ContactList();
		String id;
		
		this.cur = this.cr.query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null);
		if (this.cur.getCount() > 0) {
			while (cur.moveToNext()) {
				Contact c = new Contact();
				id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
				c.setId(id);
				c.setDisplayName(cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)));
				if (Integer.parseInt(cur.getString(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
					c.setPhone(this.getPhoneNumbers(id));
				}
				c.setEmail(this.getEmailAddresses(id));
				c.setNotes(this.getContactNotes(id));
				c.setAddresses(this.getContactAddresses(id));
				c.setImAddresses(this.getIM(id));
				c.setOrganization(this.getContactOrg(id));
				contacts.addContact(c);
			}
		}
		return(contacts);
	}
	
	public ArrayList<String> getGroups(String id) {
		ArrayList<String> groups = new ArrayList<String>();
		
		//this.cur = this.cr.query(ContactsContract.CommonDataKinds.GroupMembership.CONTACT_ID, projection, selection, null, null);

		return(groups);
	}
	
	public ArrayList<Phone> getPhoneNumbers(String id) {
		ArrayList<Phone> phones = new ArrayList<Phone>();
		
		Cursor pCur = this.cr.query(
				ContactsContract.CommonDataKinds.Phone.CONTENT_URI, 
				null, 
				ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = ?", 
				new String[]{id}, null);
		while (pCur.moveToNext()) {
			String type = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));
			
			Phone phone = new Phone(
					pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)), type );
			if(Integer.valueOf(type) == ContactsContract.CommonDataKinds.Phone.TYPE_CUSTOM)
					phone.setTypeLabel(pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.LABEL)));
			phones.add(phone);
		} 
		pCur.close();
		return(phones);
	}
	
	public ArrayList<Email> getEmailAddresses(String id) {
		ArrayList<Email> emails = new ArrayList<Email>();
		
		Cursor emailCur = this.cr.query( 
				ContactsContract.CommonDataKinds.Email.CONTENT_URI, 
				null,
				ContactsContract.CommonDataKinds.Email.CONTACT_ID + " = ?", 
				new String[]{id}, null); 
		while (emailCur.moveToNext()) {
			String type = emailCur.getString(emailCur.getColumnIndex(ContactsContract.CommonDataKinds.Email.TYPE));
			
			Email email = new Email(emailCur.getString(emailCur.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA)), type );
			if(Integer.valueOf(type) == ContactsContract.CommonDataKinds.Email.TYPE_CUSTOM)
				email.setTypeLabel(emailCur.getString(emailCur.getColumnIndex(ContactsContract.CommonDataKinds.Email.LABEL)));
			emails.add(email);
		} 
		emailCur.close();
		return(emails);
	}
	
	public ArrayList<String> getContactNotes(String id) {
		ArrayList<String> notes = new ArrayList<String>();
		String where = ContactsContract.Data.CONTACT_ID + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ?"; 
		String[] whereParameters = new String[]{id, 
			ContactsContract.CommonDataKinds.Note.CONTENT_ITEM_TYPE}; 
		Cursor noteCur = this.cr.query(ContactsContract.Data.CONTENT_URI, null, where, whereParameters, null); 
		if (noteCur.moveToFirst()) { 
			String note = noteCur.getString(noteCur.getColumnIndex(ContactsContract.CommonDataKinds.Note.NOTE));
			if (note.length() > 0) {
				notes.add(note);
			}
		} 
		noteCur.close();
		return(notes);
	}
	
	public ArrayList<Address> getContactAddresses(String id) {
		ArrayList<Address> addrList = new ArrayList<Address>();
		
		String where = ContactsContract.Data.CONTACT_ID + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ?"; 
		String[] whereParameters = new String[]{id, 
				ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE}; 
		
		Cursor addrCur = this.cr.query(ContactsContract.Data.CONTENT_URI, null, where, whereParameters, null); 
		while(addrCur.moveToNext()) {
			String poBox = addrCur.getString(addrCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.POBOX));
			String street = addrCur.getString(addrCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.STREET));
			String city = addrCur.getString(addrCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.CITY));
			String state = addrCur.getString(addrCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.REGION));
			String postalCode = addrCur.getString(addrCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.POSTCODE));
			String country = addrCur.getString(addrCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.COUNTRY));
			String type = addrCur.getString(addrCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.TYPE));
			Address a = new Address(poBox, street, city, state, postalCode, country, type);
			if(Integer.valueOf(type) == ContactsContract.CommonDataKinds.StructuredPostal.TYPE_CUSTOM)
				a.setTypeLabel(addrCur.getString(addrCur.getColumnIndex(ContactsContract.CommonDataKinds.StructuredPostal.LABEL)));
			addrList.add(a);
		} 
		addrCur.close();
		return(addrList);
	}
	
	public ArrayList<IM> getIM(String id) {
		ArrayList<IM> imList = new ArrayList<IM>();
		String where = ContactsContract.Data.CONTACT_ID + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ?"; 
		String[] whereParameters = new String[]{id, 
				ContactsContract.CommonDataKinds.Im.CONTENT_ITEM_TYPE}; 
		
		Cursor imCur = this.cr.query(ContactsContract.Data.CONTENT_URI, null, where, whereParameters, null); 
		if (imCur.moveToFirst()) { 
			String imName = imCur.getString(imCur.getColumnIndex(ContactsContract.CommonDataKinds.Im.DATA));
			String imType = imCur.getString(imCur.getColumnIndex(ContactsContract.CommonDataKinds.Im.TYPE));
			String imProtocol = imCur.getString(imCur.getColumnIndex(ContactsContract.CommonDataKinds.Im.PROTOCOL));
			if (imName.length() > 0) {
				IM im = new IM(imName, imType, imProtocol);
				if(Integer.valueOf(imType) == ContactsContract.CommonDataKinds.Im.TYPE_CUSTOM)
					im.setTypeLabel(imCur.getString(imCur.getColumnIndex(ContactsContract.CommonDataKinds.Im.LABEL)));
				if(Integer.valueOf(imProtocol) == ContactsContract.CommonDataKinds.Im.PROTOCOL_CUSTOM)
					im.setProtocolLabel(imCur.getString(imCur.getColumnIndex(ContactsContract.CommonDataKinds.Im.CUSTOM_PROTOCOL)));				
				imList.add(im);
			}
		} 
		imCur.close();
		return(imList);
	}
	
	public Organization getContactOrg(String id) {
		Organization org = new Organization();
		String where = ContactsContract.Data.CONTACT_ID + " = ? AND " + ContactsContract.Data.MIMETYPE + " = ?"; 
		String[] whereParameters = new String[]{id, 
				ContactsContract.CommonDataKinds.Organization.CONTENT_ITEM_TYPE}; 
		
		Cursor orgCur = this.cr.query(ContactsContract.Data.CONTENT_URI, null, where, whereParameters, null);

		if (orgCur.moveToFirst()) { 
			String orgName = orgCur.getString(orgCur.getColumnIndex(ContactsContract.CommonDataKinds.Organization.DATA));
			String title = orgCur.getString(orgCur.getColumnIndex(ContactsContract.CommonDataKinds.Organization.TITLE));
			if (orgName.length() > 0) {
				org.setOrganization(orgName);
				org.setTitle(title);
			}
		} 
		orgCur.close();
		return(org);
	}
	
}