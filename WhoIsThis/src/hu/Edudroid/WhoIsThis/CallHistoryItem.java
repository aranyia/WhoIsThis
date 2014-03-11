package hu.Edudroid.WhoIsThis;

public class CallHistoryItem {
	private String number;
	private String type;
	private long date;
	private long duration;
	private int calltype;

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public long getDate() {
		return date;
	}

	public void setDate(long date) {
		this.date = date;
	}
	
	public long getDuration() {
		return duration;
	}

	public void setDuration(long duration) {
		this.duration = duration;
	}
	
	public int getCallType() {
		return calltype;
	}

	public void setCallType(int calltype) {
		this.calltype = calltype;
	}
	
	public CallHistoryItem(String n, String t, long dt, long du, int ct) {
		this.number = n;
		this.type = t;
		this.date = dt;
		this.duration = du;
		this.calltype = ct;
	}
}
