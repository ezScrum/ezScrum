package ntut.csie.ezScrum.restful.mobile.service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import ntut.csie.ezScrum.restful.mobile.support.ConvertProject;
import ntut.csie.ezScrum.web.dataObject.AccountObject;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.helper.ProjectHelper;
import ntut.csie.ezScrum.web.logic.ProjectLogic;
import ntut.csie.jcis.account.core.LogonException;

import org.codehaus.jettison.json.JSONException;

public class ProjectWebService extends LoginWebService {
	private ArrayList<ProjectObject> mProjects = new ArrayList<ProjectObject>();

	public ProjectWebService(String username, String password)
			throws LogonException {
		super(username, password);
		mProjects = getUserProject(super.getAccount());
	}

	public ProjectWebService(String username, String password,
			String projectName) throws LogonException {
		super(username, password);
		mProjects.add(getProject(projectName));
	}

	public ProjectWebService(AccountObject user, String projectName)
			throws LogonException {
		super(user.getUsername(), user.getPassword());
		mProjects.add(getProject(projectName));
	}

	public ArrayList<ProjectObject> getAllProjects() {
		return mProjects;
	}

	private ArrayList<ProjectObject> getUserProject(AccountObject account) {
		ArrayList<ProjectObject> projects = new ArrayList<ProjectObject>();
		ProjectLogic helper = new ProjectLogic();

		// get the user and projects permission mapping
		Map<String, Boolean> map = helper.getProjectPermissionMap(account);
		Set<Entry<String, Boolean>> set = map.entrySet();
		Iterator<Entry<String, Boolean>> iter = set.iterator();

		while (iter.hasNext()) {
			Map.Entry<String, Boolean> entry = (Entry<String, Boolean>) iter
					.next();
			if (entry.getValue() == Boolean.TRUE) {
				String projectName = (String) entry.getKey();
				projects.add(getProject(projectName));
			}
		}

		return projects;
	}

	private ProjectObject getProject(String projectName) {
		ProjectObject project = null;

		if (projectName != null) {
			project = (new ProjectHelper()).getProjectByName(projectName);
		}

		return project;
	}

	public String getRESTFulResponseString() throws JSONException {
		ConvertProject convert = new ConvertProject();
		for (ProjectObject project : mProjects) {
			convert.addProject(project);
		}
		return convert.getJSONString();
	}
}