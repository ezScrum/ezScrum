package ntut.csie.ezScrum.web.action.plan;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ntut.csie.ezScrum.web.SecurityRequestProcessor;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.dataObject.SprintObject;
import ntut.csie.ezScrum.web.helper.SprintPlanHelper;
import ntut.csie.ezScrum.web.support.SessionManager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

public class AjaxGetSprintPlanListAction extends Action {
	private static Log log = LogFactory
			.getLog(AjaxGetSprintPlanListAction.class);

	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {
		ProjectObject project = SessionManager.getProject(request);

		String result = "";
		try {
			// Get sprint plan list
			SprintPlanHelper sprintPlanHelper = new SprintPlanHelper(project);
			ArrayList<SprintObject> sprints = sprintPlanHelper.getSprints();

			// write sprint plan to XML format
			StringBuilder stringBuilder = new StringBuilder();
			stringBuilder.append("<Sprints><Result>success</Result>");
			for (int i = 0; i < sprints.size(); i++) {
				stringBuilder.append("<Sprint>");
				stringBuilder.append("<Id>"
						+ String.valueOf(sprints.get(i).getId()) + "</Id>");
				stringBuilder.append("<Name>" + "Sprint "
						+ String.valueOf(sprints.get(i).getId()) + "</Name>");
				stringBuilder.append("</Sprint>");
			}
			// 多加一個all的選項 以抓取所有的 retrospective 這裡的 All 會被加進 combobox 的最下面
			stringBuilder.append("<Sprint>");
			stringBuilder.append("<Id>" + "All" + "</Id>");
			stringBuilder.append("<Name>" + "All" + "</Name>");
			stringBuilder.append("</Sprint>");
			stringBuilder.append("</Sprints>");
			response.setContentType("text/xml; charset=utf-8");
			response.getWriter().write(stringBuilder.toString());
			LogFactory.getLog(SecurityRequestProcessor.class).debug(
					"Current Time : " + new Date().toString());
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