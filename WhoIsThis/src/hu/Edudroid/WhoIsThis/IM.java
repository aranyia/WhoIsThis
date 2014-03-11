package hu.Edudroid.WhoIsThis;

public class IM {
 	private String name;
 	private String type;
 	private String type_label;
 	private String protocol;
 	private String protocol_label;
 	public String getName() {
 		return name;
 	}
 	public void setName(String name) {
 		this.name = name;
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
 	public String getProtocol() {
 		return protocol;
 	}
 	public void setProtocol(String protocol) {
 		this.protocol = protocol;
 	}
	public String getProtocolLabel() {
		return protocol_label;
	}	
	public void setProtocolLabel(String protocol_label) {
		this.protocol_label = protocol_label;
	}
 	
 	public IM(String name, String type, String protocol) {
 		this.name = name;
 		this.type = type;
 		this.protocol = protocol;
 	}
 }