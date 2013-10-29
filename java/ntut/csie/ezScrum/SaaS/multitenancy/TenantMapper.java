package ntut.csie.ezScrum.SaaS.multitenancy;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.jdo.Extent;
import javax.jdo.JDOObjectNotFoundException;
import javax.jdo.PersistenceManager;

import org.datanucleus.exceptions.NucleusObjectNotFoundException;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

import ntut.csie.ezScrum.SaaS.PMF;
import ntut.csie.ezScrum.SaaS.database.AccountDataStore;
import ntut.csie.ezScrum.SaaS.database.TenantDataStore;
import ntut.csie.ezScrum.SaaS.interfaces.account.Account;


public class TenantMapper {
	public TenantMapper() {
	}
	
	public void addTenantAdmin(Account account){
		PersistenceManager pm = PMF.get().getPersistenceManager();
		Key key = KeyFactory.createKey(AccountDataStore.class.getSimpleName(), account.getID());
		
		AccountDataStore accountData = new AccountDataStore(key, account.getID(), account.getPassword());
		accountData.setName(account.getName());
		accountData.setEmail(account.getEmail());
		accountData.setEnable(account.getEnable());
		
		// ?
		List<String> permissions = new ArrayList<String>();
		for (int i=0; i<account.getPermissionList().size(); i++) {
			permissions.add(account.getPermissionList().get(i).getPermissionName());
		}
		accountData.setPermissions(permissions);

		try {
			pm.makePersistent(accountData);
		} finally {
			pm.close();
		}	
	}
	
	public void addTenant(String tenantId, String name, String description, RentService rentService) {
		PersistenceManager pm = PMF.get().getPersistenceManager();

		Key key = KeyFactory.createKey(TenantDataStore.class.getSimpleName(), tenantId);
		TenantDataStore tenant = new TenantDataStore(key);
		tenant.setTenantname(name);
		tenant.setTenantId(tenantId);
		tenant.setDescription(description);	
		tenant.setRentService(rentService);
		
		try {
			pm.makePersistent(tenant);
		} finally {
			pm.close();
		}	
	}

	public void stopTenant(String tenantId) {
		PersistenceManager pm = PMF.get().getPersistenceManager();

		Key key = KeyFactory.createKey(TenantDataStore.class.getSimpleName(), tenantId);
		TenantDataStore tenant = pm.getObjectById(TenantDataStore.class, key);
		
		tenant.setEnable(false);		
		try {
			pm.makePersistent(tenant);
		} finally {
			pm.close();
		}
	}
	
	public void renewTenant(String tenantId) {
		PersistenceManager pm = PMF.get().getPersistenceManager();

		Key key = KeyFactory.createKey(TenantDataStore.class.getSimpleName(), tenantId);
		TenantDataStore tenant = pm.getObjectById(TenantDataStore.class, key);
		
		tenant.setEnable(true);		
		try {
			pm.makePersistent(tenant);
		} finally {
			pm.close();
		}
	}

	public TenantDataStore getTenant(String tenantId) {
		PersistenceManager pm = PMF.get().getPersistenceManager();

		Key key = KeyFactory.createKey(TenantDataStore.class.getSimpleName(), tenantId);
		TenantDataStore tenant;
		try{
			tenant = pm.getObjectById(TenantDataStore.class, key);
		}catch (NucleusObjectNotFoundException ex) {
			tenant = null;
		}catch (JDOObjectNotFoundException ex) {
			tenant = null;
		}finally{
			pm.close();
		}
		return tenant;
	}

	public void updateTenant(String tenantId, String name, String description,RentService rentService) {
		PersistenceManager pm = PMF.get().getPersistenceManager();

		Key key = KeyFactory.createKey(TenantDataStore.class.getSimpleName(), tenantId);

		TenantDataStore tenant = pm.getObjectById(TenantDataStore.class, key);
		tenant.setTenantId(tenantId);
		tenant.setTenantname(name);
		tenant.setDescription(description);
		tenant.setRentService(rentService);
		try {
			pm.makePersistent(tenant);
		} finally {
			pm.close();
		}
	}

	public List<TenantDataStore> getTenantList() {
		List<TenantDataStore> list = new ArrayList<TenantDataStore>();
		PersistenceManager pm = PMF.get().getPersistenceManager();
		Extent<TenantDataStore> extent = pm.getExtent(TenantDataStore.class, false);
		Iterator<TenantDataStore> ir = extent.iterator();

		while (ir.hasNext()) {
			TenantDataStore tenants = (TenantDataStore) ir.next();
			list.add(tenants);

		}
		extent.closeAll();
		pm.close();

		return list;
	}

	public boolean isTenantExist(String tenantId) {
		boolean Exist = false;
		PersistenceManager pm = PMF.get().getPersistenceManager();
		Extent<TenantDataStore> extent = pm.getExtent(TenantDataStore.class, false);
		Iterator<TenantDataStore> ir = extent.iterator();

		while (ir.hasNext()) {
			TenantDataStore tenants = (TenantDataStore) ir.next();
			if (tenants.getTenantId().equalsIgnoreCase(tenantId))
				Exist = true;

		}
		extent.closeAll();
		pm.close();
		return Exist;
	}	
}
