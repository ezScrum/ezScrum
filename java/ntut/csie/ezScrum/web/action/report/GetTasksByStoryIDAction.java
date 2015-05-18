package ntut.csie.ezScrum.web.action.report;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.dataObject.StoryObject;
import ntut.csie.ezScrum.web.dataObject.TaskObject;
import ntut.csie.ezScrum.web.logic.SprintBacklogLogic;
import ntut.csie.ezScrum.web.mapper.SprintBacklogMapper;
import ntut.csie.ezScrum.web.support.SessionManager;
import ntut.csie.ezScrum.web.support.TranslateSpecialChar;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

public class GetTasksByStoryIDAction extends Action {
	private static Log log = LogFactory.getLog(GetTasksByStoryIDAction.class);

	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {
		ProjectObject project = SessionManager.getProjectObject(request);

		long storyId = Long.parseLong(request.getParameter("storyID"));
		long sprintId = Long.parseLong(request.getParameter("sprintID"));
		SprintBacklogMapper sprintBacklogMapper = (new SprintBacklogLogic(
				project, sprintId)).getSprintBacklogMapper();

		// 封裝 Task 成 XML
		StringBuilder stringBuilder = new StringBuilder();
		TranslateSpecialChar tsc = new TranslateSpecialChar();
		if (sprintBacklogMapper != null) {
			stringBuilder.append("<Tasks>");
			// 取出 指定 Story 底下的 Tasks
			StoryObject story = sprintBacklogMapper.getStory(storyId);
			ArrayList<TaskObject> tasks = story.getTasks();
			if (tasks != null) {
				for (TaskObject task : tasks) {
					String handlerUsername = task.getHandler() != null ? task
							.getHandler().getUsername() : "";

					stringBuilder.append("<Task>");
					stringBuilder.append("<Id>").append(task.getId())
							.append("</Id>");
					stringBuilder.append("<Link>").append("").append("</Link>");
					stringBuilder.append("<Name>")
							.append(tsc.TranslateXMLChar(task.getName()))
							.append("</Name>");
					stringBuilder.append("<Estimate>")
							.append(task.getEstimate()).append("</Estimate>");
					stringBuilder.append("<Actual>").append(task.getActual())
							.append("</Actual>");
					stringBuilder.append("<Handler>").append(handlerUsername)
							.append("</Handler>");
					stringBuilder
							.append("<Partners>")
							.append(tsc.TranslateXMLChar(task
									.getPartnersUsername()))
							.append("</Partners>");
					stringBuilder.append("<Notes>")
							.append(tsc.TranslateXMLChar(task.getNotes()))
							.append("</Notes>");
					stringBuilder.append("</Task>");
				}
			}
			stringBuilder.append("</Tasks>");
		} else {
			stringBuilder.append("<Tasks></Tasks>");
		}

		try {
			response.setContentType("text/xml; charset=utf-8");
			response.getWriter().write(stringBuilder.toString());
			response.getWriter().close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}