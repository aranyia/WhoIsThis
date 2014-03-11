package hu.Edudroid.WhoIsThis;

public class Email {
 	private String address;
 	private String type;
 	private String type_label;
 	
 	public String getAddress() {
 		return address;
 	}
 	public void setAddress(String address) {
 		this.address = address;
 	}
 	public String getType() {
 		return type;
 	}
 	public void setType(String t) {
 		this.type = t;
 	} 	
	public String getTypeLabel() {
		return type_label;
	}	
	public void setTypeLabel(String type_label) {
		this.type_label = type_label;
	} 	
 	public Email(String a, String t) {
 		this.address = a;
 		this.type = t;
 	}
 }
