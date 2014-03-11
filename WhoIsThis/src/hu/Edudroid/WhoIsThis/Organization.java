package hu.Edudroid.WhoIsThis;

public class Organization {
 	private String organization = "";
 	private String title = "";
 	public String getOrganization() {
 		return organization;
 	}
 	public void setOrganization(String organization) {
 		this.organization = organization;
 	}
 	public String getTitle() {
 		return title;
 	}
 	public void setTitle(String title) {
 		this.title = title;
 	}
 	
 	public Organization() {
 		
 	}
 	public Organization(String org, String title) {
 		this.organization = org;
 		this.title = title;
 	}
}
