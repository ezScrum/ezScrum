package ntut.csie.ezScrum.web.action.report;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ntut.csie.ezScrum.issue.core.IIssue;
import ntut.csie.ezScrum.pic.core.IUserSession;
import ntut.csie.ezScrum.web.dataObject.AttachFileObject;
import ntut.csie.ezScrum.web.logic.SprintBacklogLogic;
import ntut.csie.ezScrum.web.support.Translation;
import ntut.csie.jcis.resource.core.IProject;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.google.gson.Gson;

public class AjaxGetTaskBoardStoryTaskListByGuest extends Action {
	public ActionForward execute(ActionMapping mapping, ActionForm form,
	        HttpServletRequest request, HttpServletResponse response) {

		IProject project = (IProject) request.getSession().getAttribute("Project");
		IUserSession userSession = (IUserSession) request.getSession().getAttribute("UserSession");
		String sprintID = request.getParameter("sprintID");

		SprintBacklogLogic sprintBacklogLogic = new SprintBacklogLogic(project, userSession, sprintID);
		List<IIssue> stories = sprintBacklogLogic.getStoriesByImp();

		ArrayList<TaskBoard_Story> storyList = new ArrayList<TaskBoard_Story>();

		for (IIssue story : stories) {
			storyList.add(new TaskBoard_Story(story));
		}

		Gson gson = new Gson();

		HashMap<String, Object> jsonMap = new HashMap<String, Object>();
		jsonMap.put("success", "true");
		jsonMap.put("Total", "" + stories.size());
		jsonMap.put("Stories", storyList);

		try {
			response.setContentType("text/html; charset=utf-8");
			response.getWriter().write(gson.toJson(jsonMap));
			response.getWriter().close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return null;
	}

	// if partner of assignto is equals usename, return it
	public boolean checkParent(String name, String partners, String assignto) {
		String[] parents = partners.split(";");
		for (String p : parents) {
			if (name.compareTo(p) == 0)
				return true;
		}

		if (name.compareTo(assignto) == 0)
			return true;

		return false;
	}
	
	private class TaskBoard_Story {
		String Id;
		String Name;
		String Value;
		String Estimate;
		String Importance;
		String Tag;
		String Status;
		String Notes;
		String HowToDemo;
		String Link;
		String Release;
		String Sprint;
		String Attach;
		List<TaskBoard_AttachFile> AttachFileList;

		public TaskBoard_Story(IIssue story) {
			Id = Long.toString(story.getIssueID());
			Name = HandleSpecialChar(story.getSummary());
			Value = story.getValue();
			Estimate = story.getEstimated();
			Importance = story.getImportance();
			Tag = Translation.Join(story.getTags(), ",");
			Status = story.getStatus();
			Notes = HandleSpecialChar(story.getNotes());
			HowToDemo = HandleSpecialChar(story.getHowToDemo());
			Release = story.getReleaseID();
			Sprint = story.getSprintID();

			Link = story.getIssueLink();
			AttachFileList = getAttachFilePath(story, story.getAttachFiles());

			if (!AttachFileList.isEmpty())
				Attach = "true";
			else
				Attach = "false";

		}
	}

	private class TaskBoard_AttachFile {
		long FileId;
		String FileName;
		String FilePath;
		Date UploadDate;

		public TaskBoard_AttachFile(long id, String name, String path, Date date) {
			FileId = id;
			FileName = name;
			FilePath = path;
			UploadDate = date;
		}

		public Date getUploadDate() {
			return UploadDate;
		}
	}

	// 嚙諄抬蕭嚙緻IIssue嚙踝蕭AttachFile嚙踝蕭Path
	private ArrayList<TaskBoard_AttachFile> getAttachFilePath(IIssue story, ArrayList<AttachFileObject> list) {

		ArrayList<TaskBoard_AttachFile> array = new ArrayList<TaskBoard_AttachFile>();
		for (AttachFileObject attachFile : list) {
			array.add(new TaskBoard_AttachFile(attachFile.getId(), attachFile.getName(), "fileDownload.do?projectName="
			        + story.getProjectName() + "&fileId=" + attachFile.getId() + "&fileName=" + attachFile.getName()
			        , new Date(attachFile.getCreateTime())));
		}
		return array;
	}

	private String HandleSpecialChar(String str) {
		if (str.contains("\n")) {
			str = str.replaceAll("\n", "<br/>");
		}

		return str;
	}
}
