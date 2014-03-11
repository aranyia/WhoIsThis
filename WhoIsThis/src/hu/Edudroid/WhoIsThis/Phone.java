package hu.Edudroid.WhoIsThis;

public class Phone {
	private String number;
	private String type;
	private String type_label;
	
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

	public String getTypeLabel() {
		return type_label;
	}
	
	public void setTypeLabel(String type_label) {
		this.type_label = type_label;
	}
	
	public Phone(String n, String t) {
		this.number = n;
		this.type = t;
	}
}
