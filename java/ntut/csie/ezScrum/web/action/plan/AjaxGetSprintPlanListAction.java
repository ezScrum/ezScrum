package ntut.csie.ezScrum.web.action.plan;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ntut.csie.ezScrum.iteration.core.ISprintPlanDesc;
import ntut.csie.ezScrum.web.SecurityRequestProcessor;
import ntut.csie.ezScrum.web.helper.SprintPlanHelper;
import ntut.csie.jcis.resource.core.IProject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

public class AjaxGetSprintPlanListAction extends Action {
	private static Log log = LogFactory.getLog(AjaxGetSprintPlanListAction.class);
	
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {
		IProject project = (IProject) request.getSession().getAttribute(
				"Project");

		String result = "";
		try
		{
	    	// Get sprint plan list
	    	SprintPlanHelper spHelper=new SprintPlanHelper(project);
	    	List<ISprintPlanDesc> plans = spHelper.loadListPlans();

	    	// write sprint plan to XML format
			StringBuilder sb = new StringBuilder();
			sb.append("<Sprints><Result>success</Result>");
			for(int i = 0; i < plans.size(); i++)
			{
				sb.append("<Sprint>");
				sb.append("<Id>" + plans.get(i).getID() + "</Id>");
				sb.append("<Name>" + "Sprint " + plans.get(i).getID() + "</Name>");
				sb.append("</Sprint>");
			}
				//多加一個all的選項 以抓取所有的retrospective 這裡的All會被加進combobox的最下面
				sb.append("<Sprint>");
				sb.append("<Id>" + "All" + "</Id>");
				sb.append("<Name>" + "All" + "</Name>");
				sb.append("</Sprint>");
			sb.append("</Sprints>");
			response.setContentType("text/xml; charset=utf-8");
			response.getWriter().write(sb.toString());
			LogFactory.getLog(SecurityRequestProcessor.class).debug("Current Time : " + new Date().toString());
			response.getWriter().close();
		} catch (IOException e) {
			result = "<Sprints><Result>false</Result></Sprints>";
		}
		
		try {
			response.setContentType("text/xml; charset=utf-8");
			response.getWriter().write(result);
			response.getWriter().close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
}
