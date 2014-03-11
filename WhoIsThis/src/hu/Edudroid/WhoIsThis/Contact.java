package hu.Edudroid.WhoIsThis;

import java.sql.Date;
import java.util.ArrayList;

import android.graphics.Bitmap;
import android.provider.CallLog;

public class Contact {
	private String id;
 	private String displayName;
	private Bitmap photo;
 	private long lastContact;
 	private int contactCount;
	private ArrayList<Phone> phone;
 	private ArrayList<Email> email;
 	private ArrayList<String> notes;
 	private ArrayList<Address> addresses = new ArrayList<Address>();
 	private ArrayList<IM> imAddresses;
 	private Organization organization;
	private ArrayList<CallHistoryItem> callhistory;
	private long[][] callhistoryStats;
 	
 	public Organization getOrganization() {
 		return organization;
 	}
 	public void setOrganization(Organization organization) {
 		this.organization = organization;
 	}
 	public ArrayList<IM> getImAddresses() {
 		return imAddresses;
 	}
 	public void setImAddresses(ArrayList<IM> imAddresses) {
 		this.imAddresses = imAddresses;
  	}
 	public void addImAddresses(IM imAddr) {
 		this.imAddresses.add(imAddr);
 	}
 	public ArrayList<String> getNotes() {
 		return notes;
 	}
 	public void setNotes(ArrayList<String> notes) {
 		this.notes = notes;
 	}
 	public void addNote(String note) {
 		this.notes.add(note);
 	}
 	public ArrayList<Address> getAddresses() {
 		return addresses;
 	}
 	public void setAddresses(ArrayList<Address> addresses) {
 		this.addresses = addresses;
 	}
 	public void addAddress(Address address) {
 		this.addresses.add(address);
 	}
 	public ArrayList<Email> getEmail() {
 		return email;
 	}
 	public void setEmail(ArrayList<Email> email) {
 		this.email = email;
 	}
 	public void addEmail(Email e) {
 		this.email.add(e);
 	}	
 	public String getId() {
 		return id;
 	}
 	public void setId(String id) {
  		this.id = id;
 	}
 	public String getDisplayName() {
 		return displayName;
 	}
 	public void setDisplayName(String dName) {
 		this.displayName = dName;
 	}
 	public ArrayList<Phone> getPhone() {
 		return phone;
 	}
 	public void setPhone(ArrayList<Phone> phone) {
 		this.phone = phone;
 	}
 	public void addPhone(Phone phone) {
 		this.phone.add(phone);
 	}
 	public void setPhoto(Bitmap photo) {
 		this.photo = photo;
 	}
 	public Bitmap getPhoto() {
 		return photo;
 	}
 	public void setCallHistoryStats(long[][] stats) {
 		this.callhistoryStats = stats;
 	}
 	public ArrayList<CallHistoryItem> getCallHistory() {
 		return callhistory;
 	}
 	public void setCallHistory(ArrayList<CallHistoryItem> callhistory) {
 		this.callhistory = callhistory;
 	} 		
 	public int getIncomingCount() {
		return (int)callhistoryStats[CallLog.Calls.INCOMING_TYPE][0]; 		
 	}
 	public int getOutgoingCount() {
		return (int)callhistoryStats[CallLog.Calls.OUTGOING_TYPE][0]; 		
 	}
 	public int getMissedCount() {
		return (int)callhistoryStats[CallLog.Calls.MISSED_TYPE][0];	
 	}
 	public int getFailedOutgoingCount() {
 		return (int) callhistoryStats[0][0];
 	}
 	public long getIncomingDuration() {
 		return callhistoryStats[CallLog.Calls.INCOMING_TYPE][1];
 	}
 	public long getOutgoingDuration() {
 		return callhistoryStats[CallLog.Calls.OUTGOING_TYPE][1];
 	}
 	public long getIncomingDurationAverage() {
 		return this.getIncomingDuration() / this.getIncomingCount();
 	}
 	public long getOutgoingDurationAverage() {
 		return this.getOutgoingDuration() / this.getOutgoingCount();
 	}
 	public int getContactCount() {
 		return contactCount;
 	}
 	public void setContactCount(int contactCount) {
 		this.contactCount = contactCount;
 	}
 	public void setLastContact(long lastcontact) {
 		this.lastContact = lastcontact;
 	}
 	public long getLastContact() {
 		return lastContact;
 	}
 	public String getLastContactString() {
 	    String output = "(?)";
 	    
 		if(lastContact != 0)
 		{
 		Date lastdate = new Date(this.lastContact);
 		Date currdate = new Date(System.currentTimeMillis());
 	    long diffInSeconds = (currdate.getTime() - lastdate.getTime()) / 1000;

 	    long diff[] = new long[] { 0, 0, 0, 0, 0 };
 	    /* sec */	diff[4] = (diffInSeconds >= 60 ? diffInSeconds % 60 : diffInSeconds);
 	    /* min */	diff[3] = (diffInSeconds = (diffInSeconds / 60)) >= 60 ? diffInSeconds % 60 : diffInSeconds;
 	    /* hours */	diff[2] = (diffInSeconds = (diffInSeconds / 60)) >= 24 ? diffInSeconds % 24 : diffInSeconds;
 	    /* days */	diff[1] = (diffInSeconds = (diffInSeconds / 24));
 	    /* weeks */	diff[0] = (diffInSeconds = (diffInSeconds / 7));
 	    
 	    //String[] timeunits = R.array.timeunit;
 	    if(diff[1]>0) output = String.valueOf(diff[1]) + " day" + (diff[1] > 1 ? "s" : "");
 	    else if(diff[2]>0) output = String.valueOf(diff[2]) + " hour" + (diff[2] > 1 ? "s" : "");
 	    else if(diff[3]>0) output = String.valueOf(diff[3]) + " minute" + (diff[3] > 1 ? "s" : "");
 		}
 		else output = "";
 		
 		return output;
 	}
 }
