package ntut.csie.ezScrum.SaaS.action.support;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import ntut.csie.ezScrum.SaaS.database.TenantDataStore;


public class TenantXmlTranslation{
	
	private List<TenantDataStore> tenants = null;
	
	public TenantXmlTranslation(List<TenantDataStore> tenants) {
		if( tenants != null ){
			this.tenants = tenants;
		}else{
			this.tenants = new ArrayList<TenantDataStore>();
		}
		
	}
	
	public String getXmlstring() {
		Iterator<TenantDataStore> iter = this.tenants.iterator();
		// write projects to XML format
		StringBuilder sb = new StringBuilder();
		sb.append("<Tenants>");
		while (iter.hasNext()) {
			TenantDataStore tenant = (TenantDataStore) iter.next();
			sb.append("<Tenant>");
			// Tenant Information
			sb.append("<ID>" + tenant.getTenantId() + "</ID>");	// alex
			sb.append("<Name>" + tenant.getTenantName() + "</Name>");	// alex
			sb.append("<Description>" + tenant.getDescription() + "</Description>");
			// Rent Service
			sb.append("<AdminName>" + tenant.getAdminName() + "</AdminName>");				
//			sb.append("<ActivativeDate>" + tenant.getActivateDate() + "</ActivativeDate>");			
//			sb.append("<Period>" + tenant.getPeriod() + "</Period>");
			sb.append("<Enable>" + tenant.getEnable() + "</Enable>");
			//
			sb.append("</Tenant>");
		}
	    sb.append("</Tenants>");
	    
		return sb.toString();
	}
}
