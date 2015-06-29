package ntut.csie.ezScrum.web.action.rbac;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ntut.csie.ezScrum.web.helper.AccountHelper;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

public class GetAssignedProjectAction extends Action {
	public ActionForward execute(ActionMapping mapping, ActionForm form,
	        HttpServletRequest request, HttpServletResponse response)
	        throws Exception {

		long id;
		try {
			id = Long.parseLong(request.getParameter("accountID"));
		} catch (NumberFormatException e) {
			id = 0;
		}
		
		try {
			response.setContentType("text/xml; charset=utf-8");
			response.getWriter().write(new AccountHelper().getAssignedProject(id));
			response.getWriter().close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}
