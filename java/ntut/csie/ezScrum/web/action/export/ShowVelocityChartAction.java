package ntut.csie.ezScrum.web.action.export;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

public class ShowVelocityChartAction extends Action{
	private static Log log = LogFactory.getLog(ShowVelocityChartAction.class);
	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
	    request.setAttribute("releases", request.getParameter("releases"));
	    log.debug("\n\n\n\nasdfsdfsdfdf\n\n\n\n");
	    return mapping.findForward("success");
	}
}
