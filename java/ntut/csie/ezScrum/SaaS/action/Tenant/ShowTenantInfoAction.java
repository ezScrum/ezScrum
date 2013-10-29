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

public class ShowTenantInfoAction extends Action {
	private String id = "";

	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		// 取得要取得帳號資訊的id
		this.id = request.getParameter("id");

		TenantManager tenantManager = new TenantManager();
		TenantDataStore tenantDS = tenantManager.getTenant(this.id);
		
		// write account to XML format
		List<TenantDataStore> tenantList = new ArrayList<TenantDataStore>();
		tenantList.add(tenantDS);
		TenantXmlTranslation tr = new TenantXmlTranslation(tenantList);
	    
	    try {
			response.setContentType("text/xml; charset=utf-8");
			response.getWriter().write(tr.getXmlstring());
			response.getWriter().close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
}
