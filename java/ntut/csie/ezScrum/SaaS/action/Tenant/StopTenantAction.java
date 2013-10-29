package ntut.csie.ezScrum.SaaS.action.Tenant;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ntut.csie.ezScrum.SaaS.action.support.TenantXmlTranslation;
import ntut.csie.ezScrum.SaaS.database.TenantDataStore;
import ntut.csie.ezScrum.SaaS.multitenancy.TenantManager;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

public class StopTenantAction extends Action {
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {

		// get parameter info
		String name = request.getParameter("id");
		
		TenantManager tenant = new TenantManager();
		tenant.stopTenant(name);
		
		StringBuilder result = new StringBuilder();
		
		
		List<TenantDataStore> tenants = new ArrayList<TenantDataStore>();
		tenants.add(tenant.getTenant(name));
		
		TenantXmlTranslation txt = new TenantXmlTranslation(tenants);
		result.append(txt.getXmlstring());
		
		
		response.setContentType("text/xml; charset=utf-8");
		
		try {
			
			response.getWriter().write(result.toString());
			response.getWriter().close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
		return null;
	}
}