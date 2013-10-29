package ntut.csie.ezScrum.SaaS.action.Tenant;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ntut.csie.ezScrum.SaaS.action.support.TenantXmlTranslation;
import ntut.csie.ezScrum.SaaS.database.TenantDataStore;
import ntut.csie.ezScrum.SaaS.multitenancy.RentService;
import ntut.csie.ezScrum.SaaS.multitenancy.TenantManager;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

public class ModifyTenantAction extends Action {
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {
		// 取得要新增租戶的資料
		String id = request.getParameter("id");
		String name = request.getParameter("name");
		String description = request.getParameter("description");
		//
		String adminName = request.getParameter("adminname");		
//		String activativedate = request.getParameter("activativedate");
//		String period = request.getParameter("period");
		String isEdit = request.getParameter("isEdit");
		
		System.out.println(">>>>> ModifyTenantAction <<<<<");
		System.out.println("\t id = " + id);
		System.out.println("\t name = " + name);
		
		boolean enable = true;
		if (request.getParameter("enable") != null && request.getParameter("enable").equals("false")) {
			enable = false;
		}

		//String roles = "user";

		RentService rentService = new RentService(id, adminName, enable);
		TenantDataStore newTenant = null;

		if (Boolean.valueOf(isEdit)) {
			newTenant = updateTenant(id, name, description, rentService);
		} else {
			newTenant = createTenant(id, name, description, rentService);
		}

		StringBuilder result = new StringBuilder();
		if (newTenant != null) {
			// translate to xml string
			List<TenantDataStore> tenants = new ArrayList<TenantDataStore>();
			tenants.add(newTenant);

			TenantXmlTranslation txt = new TenantXmlTranslation(tenants);
			result.append(txt.getXmlstring());
		} else {
			result.append("{\"success\": false}");
		}

		response.setContentType("text/xml; charset=utf-8");

		try {
			response.getWriter().write(result.toString());
			response.getWriter().close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * 進行Tenant建立的動作, 並且將RentService Assign至Tenant
	 */
	private TenantDataStore createTenant(String id, String name, String description, RentService rentService) {
		TenantManager tm = new TenantManager();
		tm.addTenant(id, name, description, rentService);
		return tm.getTenant(id);

	}

	/**
	 * 進行編輯帳號的動作，並且將帳號更新角色於 RoleBase
	 * 
	 * @return
	 */
	private TenantDataStore updateTenant(String id, String name, String description, RentService rentservice) {
		TenantManager tm = new TenantManager();
		tm.updateTenant(id, name, description, rentservice);

		return tm.getTenant(id);
	}
}
