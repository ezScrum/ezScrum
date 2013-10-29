package ntut.csie.ezScrum.SaaS.multitenancy;

import java.util.List;

import ntut.csie.ezScrum.SaaS.database.TenantDataStore;
import ntut.csie.ezScrum.SaaS.interfaces.account.Account;
import ntut.csie.ezScrum.SaaS.util.ScrumEnum;
import ntut.csie.ezScrum.SaaS.util.ezScrumUtil;
import ntut.csie.jcis.account.core.internal.Permission;

import com.google.appengine.api.NamespaceManager;


public class TenantManager {
	private TenantMapper _tenantMpr;
	
	public TenantManager()
	{
		_tenantMpr = new TenantMapper();
	}
	
	public void addTenant(String tenantId, String name, String description, RentService rentService) {
		/*
		 * Tenant & Rent Service
		 */
		_tenantMpr.addTenant(tenantId, name, description, rentService);

		/*
		 * Account
		 */
		
		// fix later
		String adminAccountName = rentService.getAdminName();// + "@" + tenantId + ".com";
//		IAccountManager am = AccountFactory.getManager();
//		ExtensionFieldMapper extensionFieldMapper = new ExtensionFieldMapper();
		
		NamespaceManager.set(tenantId);
//		Account account = AccountFactory.createAccount(adminAccountName, adminAccountName, adminAccountName);
		
		Account account = new Account(adminAccountName, adminAccountName, ezScrumUtil.getMd5(adminAccountName), false);
		
		account.addPermission(new Permission(ScrumEnum.ADMINISTRATOR_PERMISSION, "system","admin"));
		account.addPermission(new Permission(ScrumEnum.CREATEPROJECT_PERMISSION, "system","createProject"));
		account.setEnable("true");
		account.setEmail("example@ezScrum.tw");
		
		_tenantMpr.addTenantAdmin(account);
				
//		am.addAccount(account);
		
//		extensionFieldMapper.newExtensionField(Configuration.TARGET_STRING[Configuration.TARGET_PROJECT]);
//		
//		extensionFieldMapper.newExtensionField(Configuration.TARGET_STRING[Configuration.TARGET_ACCOUNT]);
//		
//		extensionFieldMapper.newExtensionField(Configuration.TARGET_STRING[Configuration.TARGET_STORY]);
//		
//		extensionFieldMapper.newExtensionField(Configuration.TARGET_STRING[Configuration.TARGET_TASK]);
		
		NamespaceManager.set("");
	}

	public void stopTenant(String tenantId) {
		_tenantMpr.stopTenant(tenantId);
	}
	
	public void renewTenant(String tenantId) {
		_tenantMpr.renewTenant(tenantId);
	}

	public TenantDataStore getTenant(String tenantid) {
		return _tenantMpr.getTenant(tenantid);
	}

	public void updateTenant(String id, String name, String description,RentService rentService) {
		_tenantMpr.updateTenant(id, name, description, rentService);
	}

	public List<TenantDataStore> getTenantList() {
		return _tenantMpr.getTenantList();
	}

	public boolean isTenantExist(String id) {
		return _tenantMpr.isTenantExist(id);
	}	
}
