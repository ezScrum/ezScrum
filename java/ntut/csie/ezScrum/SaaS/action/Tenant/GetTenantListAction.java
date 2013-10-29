package ntut.csie.ezScrum.SaaS.action.Tenant;

import java.io.IOException;
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

public class GetTenantListAction extends Action {

	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response){
		
//		IUserSession session = (IUserSession) request.getSession().getAttribute("UserSession");

		try {
			
			// 取得帳號列表
			TenantManager tenant = new TenantManager();
			List<TenantDataStore> tenantList = tenant.getTenantList();
			TenantXmlTranslation tr = new TenantXmlTranslation(tenantList);
			response.setContentType("text/xml; charset=utf-8");
			response.getWriter().write(tr.getXmlstring());
//			LogFactory.getLog(SecurityRequestProcessor.class).debug("Current Time : " + DateUtil.getNowDate().toString());
			response.getWriter().close();
						
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return null;
	
	}
}
