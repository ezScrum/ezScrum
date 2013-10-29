package ntut.csie.ezScrum.SaaS.interfaces.account;

import ntut.csie.ezScrum.web.dataObject.Person;

public class TenantAdmin extends Person{

	@Override
	public String getForwardName() {
		return "Tenant_ManagementView";
	}
	
}
