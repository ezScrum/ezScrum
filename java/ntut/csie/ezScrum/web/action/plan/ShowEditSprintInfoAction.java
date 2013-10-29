package ntut.csie.ezScrum.web.action.plan;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ntut.csie.ezScrum.iteration.core.ISprintPlanDesc;
import ntut.csie.ezScrum.pic.core.IUserSession;
import ntut.csie.ezScrum.web.action.PermissionAction;
import ntut.csie.ezScrum.web.helper.SprintPlanHelper;
import ntut.csie.jcis.core.util.DateUtil;
import ntut.csie.jcis.resource.core.IProject;

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
		IProject project = (IProject) request.getSession().getAttribute("Project");
		IUserSession session = (IUserSession) request.getSession().getAttribute("UserSession");
		
		// get parameter info
		String SprintID = request.getParameter("SprintID");
		
		/*-----------------------------------------------------------
		*	變數宣告區
		-------------------------------------------------------------*/
		SprintPlanHelper SPhelper = new SprintPlanHelper(project);
		List<ISprintPlanDesc> descs = null;
		List<String> totalSprintID = new ArrayList<String>();
	
		descs = SPhelper.loadListPlans();
		
		/*-----------------------------------------------------------
		 *	將Sprint 封裝成 XML回傳至前端
		-------------------------------------------------------------*/
		// Sprint 封裝成 XML 給 Ext 使用
		StringBuilder result = new StringBuilder();
		result.append("<SprintBacklog>");
		if(descs != null)
		{
			for (ISprintPlanDesc SprintDesc : descs) {
				if(SprintDesc.getID().equals(SprintID))
				{
					Date sprintStartDate = DateUtil
							.dayFilter(SprintDesc.getStartDate());
					Date sprintEndDate = DateUtil.dayFilter(SprintDesc.getEndDate());
					// 將所有的Sprint資訊寫入XML的TAG內的Value中
					result.append("<Sprint>");
					result.append("<Id>" + SprintDesc.getID() + "</Id>");
					result.append("<Goal>" + SprintDesc.getGoal() + "</Goal>");
					result.append("<StartDate>" + SprintDesc.getStartDate()
							+ "</StartDate>");
					result.append("<Interval>" + SprintDesc.getInterval()
							+ " </Interval>");
					result.append("<Members>" + SprintDesc.getMemberNumber()
							+ " </Members>");
					result.append("<AvaliableDays>" + SprintDesc.getAvailableDays()
							+ " hours</AvaliableDays>");
					result.append("<FocusFactor>" + SprintDesc.getFocusFactor()
							+ " </FocusFactor>");
					result.append("<DailyScrum>" + SprintDesc.getNotes() + "</DailyScrum>");
					result.append("<DemoDate>" + SprintDesc.getDemoDate() + "</DemoDate>");
					result.append("<DemoPlace>" + SprintDesc.getDemoPlace()
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
