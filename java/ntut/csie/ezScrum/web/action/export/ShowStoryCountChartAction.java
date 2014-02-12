package ntut.csie.ezScrum.web.action.export;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

/**
 * 當Story Count Chart頁面按下Export時，此頁面會發出action forward產生一個
 * iframe的新頁面(Story Count chart)
 */
public class ShowStoryCountChartAction extends Action{
	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
	    request.setAttribute("releases", request.getParameter("releases"));
	    return mapping.findForward("success");
	}
}
