package ntut.csie.ezScrum.web.action.plan;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ntut.csie.ezScrum.web.action.PermissionAction;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.dataObject.SprintObject;
import ntut.csie.ezScrum.web.helper.SprintPlanHelper;
import ntut.csie.ezScrum.web.support.SessionManager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;

public class ShowEditSprintInfoAction extends PermissionAction {
	private static Log log = LogFactory.getLog(ShowEditSprintInfoAction.class);

	@Override
	public boolean isValidAction() {
		return super.getScrumRole().getAccessSprintPlan();
	}

	@Override
	public boolean isXML() {
		// XML
		return true;
	}
	
	@Override
	public StringBuilder getResponse(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {
		
		// get session info
		ProjectObject project = SessionManager.getProjectObject(request);
		
		// get parameter info
		String sprintId = request.getParameter("SprintID");
		
		/*-----------------------------------------------------------
		*	變數宣告區
		-------------------------------------------------------------*/
		SprintPlanHelper sprintPlanHelper = new SprintPlanHelper(project);
		ArrayList<SprintObject> sprints = null;
		sprints = sprintPlanHelper.getSprints();
		
		/*-----------------------------------------------------------
		 *	將Sprint 封裝成 XML回傳至前端
		-------------------------------------------------------------*/
		// Sprint 封裝成 XML 給 Ext 使用
		StringBuilder result = new StringBuilder();
		result.append("<SprintBacklog>");
		if(sprints != null)
		{
			for (SprintObject sprint : sprints) {
				String currentSprintId = String.valueOf(sprint.getId());
				if(currentSprintId.equals(sprintId))
				{
					// 將所有的Sprint資訊寫入XML的TAG內的Value中
					result.append("<Sprint>");
					result.append("<Id>" + currentSprintId + "</Id>");
					result.append("<Goal>" + sprint.getSprintGoal() + "</Goal>");
					result.append("<StartDate>" + sprint.getStartDateString()
							+ "</StartDate>");
					result.append("<Interval>" + sprint.getInterval()
							+ " </Interval>");
					result.append("<Members>" + sprint.getMembersAmount()
							+ " </Members>");
					result.append("<AvaliableDays>" + sprint.getHoursCanCommit()
							+ " hours</AvaliableDays>");
					result.append("<FocusFactor>" + sprint.getFocusFactor()
							+ " </FocusFactor>");
					result.append("<DailyScrum>" + sprint.getDailyInfo() + "</DailyScrum>");
					result.append("<DemoDate>" + sprint.getDemoDateString() + "</DemoDate>");
					result.append("<DemoPlace>" + sprint.getDemoPlace()
							+ "</DemoPlace>");
					result.append("</Sprint>");
					break;
				}
			}
		}
		result.append("</SprintBacklog>");

		return result;
	}
}
