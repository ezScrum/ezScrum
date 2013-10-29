package ntut.csie.ezScrum.SaaS.action.Tenant;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ntut.csie.ezScrum.SaaS.multitenancy.TenantManager;
import ntut.csie.ezScrum.web.action.PermissionAction;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

public class CreateTenantAction extends PermissionAction {
	@Override
	public boolean isValidAction() {
		return super.getScrumRole().getAccessProductBacklog();
	}

	@Override
	public boolean isXML() {
		// XML
		return true;
	}

	@Override
	public StringBuilder getResponse(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {
		
		System.out.println(">>>>> CreateTenantAction <<<<<");
		
		// get parameter info
		String id = request.getParameter("id");
		String name = request.getParameter("name");
		
		String description = request.getParameter("description");
		
		TenantManager tm = new TenantManager();
		tm.addTenant(id, name, description, null);	// alex
		
//		List<Tenant> tenantList = new LinkedList<Tenant>();
//		tenantList.add(tm.getTenant(name));
//		TenantXmlTranslation tr = new TenantXmlTranslation(tenantList);
		
		return new StringBuilder("");
	}
}

