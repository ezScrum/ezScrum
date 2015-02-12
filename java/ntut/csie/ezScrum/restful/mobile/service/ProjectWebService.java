package ntut.csie.ezScrum.restful.mobile.service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import ntut.csie.ezScrum.restful.mobile.support.ConvertIProject;
import ntut.csie.ezScrum.web.dataObject.AccountObject;
import ntut.csie.ezScrum.web.logic.ProjectLogic;
import ntut.csie.ezScrum.web.mapper.ProjectMapper;
import ntut.csie.jcis.account.core.IAccount;
import ntut.csie.jcis.account.core.LogonException;
import ntut.csie.jcis.resource.core.IProject;

import org.codehaus.jettison.json.JSONException;

public class ProjectWebService extends LoginWebService {
	private List<IProject> Project = new ArrayList<IProject>();
	
	public ProjectWebService(String username, String userpwd) throws LogonException {
		super(username, userpwd);
		this.Project = getUserProject(super.getAccount());
	}
	
	public ProjectWebService(String username, String userpwd, String projectID) throws LogonException {
		super(username, userpwd);
		
		this.Project.add(getProject(projectID));
	}

	public ProjectWebService(AccountObject user, String projectID) throws LogonException {
		super(user.getUsername(), user.getPassword());
		
		this.Project.add(getProject(projectID));
	}
	
	public List<IProject> getProjectList() {
		return this.Project;
	}
	
	private List<IProject> getUserProject(AccountObject acc) {
		List<IProject> projectlist = new ArrayList<IProject>();
		ProjectLogic helper = new ProjectLogic();
		
        // get the user and projects permission mapping
        Map<String, Boolean> map = helper.getProjectPermissionMap(acc);
        Set<Entry<String, Boolean>> set = map.entrySet();
        Iterator<Entry<String, Boolean>> iter = set.iterator();
        
        while (iter.hasNext()) {
        	Map.Entry<String, Boolean> entry = (Entry<String, Boolean>) iter.next();
        	if (entry.getValue() == Boolean.TRUE) {
        		String projectID = (String) entry.getKey();
        		projectlist.add(getProject(projectID));
        	}
        }
        
		return projectlist;
	}
	
	private IProject getProject(String projectID) {
        IProject project = null;
        
        if (projectID != null) {
//        	project = ResourceFacade.getProject(projectID);
        	project = (new ProjectMapper()).getProjectByID(projectID);
        }
        
        return project;
	}
	
	public String getRESTFulResponseString() throws JSONException{
		ConvertIProject convert = new ConvertIProject();
		for(IProject p : this.Project){
			convert.addProject(p);
		}
		return convert.getJSONString();
	}
}