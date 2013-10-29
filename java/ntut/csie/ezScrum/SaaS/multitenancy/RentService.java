package ntut.csie.ezScrum.SaaS.multitenancy;


public class RentService {

	private String _id = "";
	private String _adminName = "";
//	private String _activateDate = "";
//	private String _period = "";
	private boolean _enable = false;
	
	public RentService(String id, String adminName, boolean enable) {
		_id = id;
		_adminName = adminName;
//		_activateDate = activateDate; 
//		_period = period;
		_enable = enable;
	}

	public String getId() {
		return _id;
	}
	
	public String getAdminName() {
		return _adminName;
	}
	
//	public String getActivateDate() {
//		return _activateDate;
//	}
//	
//	public String getPeriod() {
//		return _period;
//	}

	public boolean isEnable() {
		return _enable;
	}
	
}
