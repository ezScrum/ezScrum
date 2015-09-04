package ntut.csie.ezScrum.web.action.retrospective;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ntut.csie.ezScrum.web.dataObject.AccountObject;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.logic.SprintBacklogLogic;
import ntut.csie.ezScrum.web.mapper.ProjectMapper;
import ntut.csie.ezScrum.web.mapper.SprintBacklogMapper;
import ntut.csie.ezScrum.web.support.SessionManager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

public class GetAddSprintTaskInfoAction extends Action {
	private static Log log = LogFactory.getLog(GetAddSprintTaskInfoAction.class);

	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {
		log.info("Get Add Sprint Task Information in GetAddSprintTaskInfoAction");
		
		ProjectObject project = SessionManager.getProjectObject(request);
		
		//所有 Sprint 封裝成 XML 給 Ext(ComboBox) 使用
		StringBuilder mStringBuilder = new StringBuilder();

		String mSprintId = request.getParameter("sprintId");

		if ( (mSprintId == null) || (mSprintId.length() <= 0 ) || (mSprintId.equals("")) || (mSprintId.equals("0")) || (mSprintId.equals("-1")) ) {
			// default data for empty sprint backlog information
			mStringBuilder.append("<Handlers><Partner></Partner><Handler></Handler></Handlers>");
		}
		
		SprintBacklogMapper sprintBacklogMapper = (new SprintBacklogLogic(project, Long.parseLong(mSprintId))).getSprintBacklogMapper();

		if ((sprintBacklogMapper != null) && (sprintBacklogMapper.getSprintId() > 0)) {
			//	MantisAccountMapper helper = new MantisAccountMapper(project, session);
			//	List<String> actorList = helper.getScrumWorkerList();
			ArrayList<AccountObject> projectWorkers = ProjectMapper.getProjectWorkers(project.getId());
			ArrayList<String> actors = new ArrayList<>();
			actors.add("");
			for (AccountObject worker : projectWorkers) {
				actors.add(worker.getUsername());
			}
			String defaultActor = "";
			if (actors != null) {
				for (int i = 0; i < actors.size(); i++) {
					//預設角色會有一個為null
					if (i > 1) {
						defaultActor += "; ";
					}
					defaultActor += actors.get(i);
				}
			}
			
			mStringBuilder.append("<Handlers><Partner><Name>" + defaultActor + "</Name></Partner>");
			for (String handler : actors) {
				mStringBuilder.append("<Handler>");
				mStringBuilder.append("<Name>" + handler + "</Name>");
				mStringBuilder.append("</Handler>");
			}
			mStringBuilder.append("</Handlers>");
		}
		
		try {
			response.setContentType("text/xml; charset=utf-8");
			response.getWriter().write(mStringBuilder.toString());
			response.getWriter().close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return null;
	}
}