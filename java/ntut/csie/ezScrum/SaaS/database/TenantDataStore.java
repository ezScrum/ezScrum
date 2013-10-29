package ntut.csie.ezScrum.SaaS.database;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import ntut.csie.ezScrum.SaaS.multitenancy.RentService;

import com.google.appengine.api.datastore.Key;

@PersistenceCapable
public class TenantDataStore {
	@PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    private Key key;
	
	@Persistent
    private String tenantId = "";
	
	public String getTenantId() {
		return tenantId;
	}

	public void setTenantId(String tenantId) {
		this.tenantId = tenantId;
		id = tenantId;
	}

	@Persistent
    private String tenantName = "";
	
	@Persistent
    private String adminName = "";
	
	public String getAdminName() {
		return adminName;
	}

	public void setAdminName(String adminName) {
		this.adminName = adminName;
	}
	
	@Persistent
	private String Description;
	
	// Rent Service part

	@Persistent
	private String id = "";
	
//	@Persistent
//	private String activateDate = "";
//	
//	@Persistent
//	private String period = "";
	
	@Persistent
	private boolean enable = false;	
	

	public TenantDataStore(Key key) {
		this.key = key;
		
	}
	
	public Key getKey() {
		return key;
	}
	
	public void setDescription(String description) {
		Description = description;
	}

	public String getDescription() {
		return Description;
	}

	public void setTenantname(String tenantname) {
		tenantName = tenantname;
	}

	public String getTenantName() {
		return tenantName;
	}

	// Rent Service
	public String getId() {
		return id;
	}
	
//	public String getPeriod() {
//		return period;
//	}
//
//	public void setPeriod(String period) {
//		this.period = period;
//	}

	public boolean getEnable() {
		return enable;
	}

	public void setEnable(boolean enable) {
		this.enable = enable;
	}
	
//	public String getActivateDate() {
//		return activateDate;
//	}
//	
//	public void setActivateDate(String activateDate) {
//		this.activateDate = activateDate;
//	}

	public void setRentService(RentService rentService) {
		this.id = rentService.getId();
		this.adminName = rentService.getAdminName();
//		this.period = rentService.getPeriod();		
//		this.activateDate = rentService.getActivateDate();
		this.enable = rentService.isEnable();
	}
	
	public RentService getRentService() {
		RentService rs = new RentService(this.id, this.adminName, this.enable);
		return rs;
	}
	
}
