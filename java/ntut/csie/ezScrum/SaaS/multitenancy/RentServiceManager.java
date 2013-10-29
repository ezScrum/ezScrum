package ntut.csie.ezScrum.SaaS.multitenancy;

public class RentServiceManager {

	public boolean isEnabled(RentService rs) {
		return false;
	}	
	
	public boolean isActivated(RentService rs) {
		return false;
	}
	
	public String getEndDate(RentService rs) {
		return "";
	}
	
}
