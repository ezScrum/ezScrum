package ntut.csie.ezScrum.web.action.plan;

import java.io.IOException;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ntut.csie.ezScrum.iteration.core.IReleasePlanDesc;
import ntut.csie.ezScrum.iteration.core.ISprintPlanDesc;
import ntut.csie.ezScrum.web.SecurityRequestProcessor;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.helper.ReleasePlanHelper;
import ntut.csie.ezScrum.web.helper.SprintPlanHelper;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

public class ShowExistedSprintAction extends Action {
	// --------------------------------------------------------- Instance
	private static Log log = LogFactory.getLog(ShowExistedSprintAction.class);
	private ReleasePlanHelper RPhelper;
	// --------------------------------------------------------- Methods
	public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
		ProjectObject project = (ProjectObject) request.getSession().getAttribute("Project");
	
		log.info("project=" + project.getName());

		String release_ID = request.getParameter("releaseID");
		RPhelper = new ReleasePlanHelper(project);
		IReleasePlanDesc reDesc = RPhelper.getReleasePlan(release_ID);
		
		if (reDesc == null) {		// 錯誤的 release ID 除錯
			return null;
		}
		
		Hashtable<String, String> ht = new Hashtable<String, String>();
		ht = getSprintsID(reDesc);
		
		//取出所有的Sprint
		SprintPlanHelper SPhelper = new SprintPlanHelper(project);
		List<ISprintPlanDesc> descs = SPhelper.loadListPlans();	
		
		//Sprint 封裝成 XML 給 Ext 使用
		StringBuilder sb = new StringBuilder();
		sb.append("<SprintBacklog>");
		
		for (ISprintPlanDesc SprintDesc : descs) {
			if (ht.get(SprintDesc.getID()) == null) {	
				sb.append("<Sprint>");
				sb.append("<Id>" + SprintDesc.getID() + "</Id>");
				sb.append("<Goal>" + SprintDesc.getGoal() + "</Goal>");
				sb.append("<StartDate>" + SprintDesc.getStartDate() + "</StartDate>");
				sb.append("<Interval>" + SprintDesc.getInterval() + " week(s)</Interval>");
				sb.append("<Members>" + SprintDesc.getMemberNumber() + " person(s)</Members>");
				sb.append("<AvaliableDays>" + SprintDesc.getAvailableDays() + " days</AvaliableDays>");
				sb.append("<FocusFactor>" + SprintDesc.getFocusFactor() + " %</FocusFactor>");
				sb.append("<DailyScrum>" + SprintDesc.getNotes() + "</DailyScrum>");
				sb.append("<DemoDate>" + SprintDesc.getDemoDate() + "</DemoDate>");
				sb.append("<DemoPlace>" + SprintDesc.getDemoPlace() + "</DemoPlace>");
				sb.append("</Sprint>");
			}
		}
		
		sb.append("</SprintBacklog>");
		response.setContentType("text/xml; charset=utf-8");
		try {
			response.getWriter().write(sb.toString());
			LogFactory.getLog(SecurityRequestProcessor.class).debug("Current Time : " + new Date().toString());
			response.getWriter().close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	private Hashtable<String, String> getSprintsID(IReleasePlanDesc desc){
		Hashtable<String, String> ht = new Hashtable<String, String>();
		
		// 讀取 release 的所有 sprints
		List<ISprintPlanDesc> sprints = desc.getSprintDescList();		
		
		if (sprints != null) {
			for (ISprintPlanDesc s : sprints) {
				if (ht.get(s) == null) {
					ht.put(s.getID(), "true");
				}
			}
			
			return ht;
		} else {
			return null;
		}	
	}
}
