package ntut.csie.ezScrum.web.mapper;

import java.io.File;

import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.iteration.core.ScrumEnum;
import ntut.csie.ezScrum.pic.core.ScrumRole;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.databasEnum.RoleEnum;
import ntut.csie.ezScrum.web.sqlService.MySQLService;
import ntut.csie.jcis.core.ISystemPropertyEnum;
import ntut.csie.jcis.core.util.XmlFileUtil;

import org.jdom.Document;
import org.jdom.Element;

public class ScrumRoleMapper {
//	private Map<String, ScrumRole> scrumRoles;
	private MySQLService mService;
	
	public ScrumRoleMapper() {
		mService = new MySQLService(new Configuration());
	}
	
	/**
	 * update permission by ScrumRole 
	 */
	public void update(ScrumRole role) {
		String workspaceRoot = System.getProperty(ISystemPropertyEnum.WORKSPACE_PATH);
		String path = workspaceRoot + File.separator + role.getProjectName() + File.separator + "_metadata" + File.separator + ScrumEnum.SCRUMROLE_FILE;
		String defaultpath = workspaceRoot + File.separator + "_metadata" + File.separator + "ScrumRole.xml";
		
		Document doc = XmlFileUtil.LoadXmlFile(path);
		if (doc == null) {
			// load default file
			doc = XmlFileUtil.LoadXmlFile(defaultpath);
		}
		
		// root
		Element root = doc.getRootElement();
		Element s_role = root.getChild(role.getRoleName());
		if (s_role != null) {
			s_role.removeContent();
		} else {
			s_role = new Element(role.getRoleName());
			root.addContent(s_role);
		}
		//if access product backlog equal true, set the text in the ScrumRole.xml
		if(role.getAccessProductBacklog()){
			Element element = new Element(ScrumEnum.SCRUMROLE_FILE_TAG_PERMISSION);
			element.setText(ScrumEnum.ACCESS_PRODUCTBACKLOG);
			s_role.addContent(element);
		}
		//if access release plan equal true, set the text in the ScrumRole.xml
		if(role.getAccessReleasePlan()){
			Element element = new Element(ScrumEnum.SCRUMROLE_FILE_TAG_PERMISSION);
			element.setText(ScrumEnum.ACCESS_RELEASEPLAN);
			s_role.addContent(element);
		}
		//if access sprint plan equal true, set the text in the ScrumRole.xml
		if(role.getAccessSprintPlan()){
			Element element = new Element(ScrumEnum.SCRUMROLE_FILE_TAG_PERMISSION);
			element.setText(ScrumEnum.ACCESS_SPRINTPLAN);
			s_role.addContent(element);
		}
		//if access sprint backlog equal true, set the text in the ScrumRole.xml
		if(role.getAccessSprintBacklog()){
			Element element = new Element(ScrumEnum.SCRUMROLE_FILE_TAG_PERMISSION);
			element.setText(ScrumEnum.ACCESS_SPRINTBACKLOG);
			s_role.addContent(element);
		}
		//if access taskboard equal true, set the text in the ScrumRole.xml
		if(role.getAccessTaskBoard()){
			Element element = new Element(ScrumEnum.SCRUMROLE_FILE_TAG_PERMISSION);
			element.setText(ScrumEnum.ACCESS_TASKBOARD);
			s_role.addContent(element);
		}
		//if access unplanned equal true, set the text in the ScrumRole.xml
		if(role.getAccessUnplannedItem()){
			Element element = new Element(ScrumEnum.SCRUMROLE_FILE_TAG_PERMISSION);
			element.setText(ScrumEnum.ACCESS_UNPLANNED);
			s_role.addContent(element);
		}
		//if access retrospective equal true, set the text in the ScrumRole.xml
		if(role.getAccessRetrospective()){
			Element element = new Element(ScrumEnum.SCRUMROLE_FILE_TAG_PERMISSION);
			element.setText(ScrumEnum.ACCESS_RETROSPECTIVE);
			s_role.addContent(element);
		}
		//if access read report equal true, set the text in the ScrumRole.xml
		if(role.getReadReport()){
			Element element = new Element(ScrumEnum.SCRUMROLE_FILE_TAG_PERMISSION);
			element.setText(ScrumEnum.ACCESS_REPORT);
			s_role.addContent(element);
		}
		//if access edit project equal true, set the text in the ScrumRole.xml
		if(role.getEditProject()){
			Element element = new Element(ScrumEnum.SCRUMROLE_FILE_TAG_PERMISSION);
			element.setText(ScrumEnum.ACCESS_EDITPROJECT);
			s_role.addContent(element);
		}

		//save file
		XmlFileUtil.SaveXmlFile(path, doc);
	}
	
	// ezScrum v1.8
	public void updateScrumRole(long projectId, ScrumRole scrumRole) {
		update(scrumRole);	// 過度期，更改Rolebase.xml, DB全數改為即可刪掉
		ProjectObject project = ProjectObject.get(projectId);
		project.updateScrumRole(scrumRole);
	}
	
	// ezScrum v1.8
	public ScrumRole getScrumRole(long projectId, String projectName, String roleName) {
		ProjectObject project = ProjectObject.get(projectId);
		ScrumRole scrumRole = project.getScrumRole(RoleEnum.valueOf(roleName));
		return scrumRole;
	}
	
//	// return 某個專案角色的權限
//	public ScrumRole getPermission(String resource, String rolename) {
//		String workspaceRoot = System.getProperty(ISystemPropertyEnum.WORKSPACE_PATH);
//		String path = workspaceRoot + File.separator + resource + File.separator + "_metadata" + File.separator + "ScrumRole.xml";
//		String defaultpath = workspaceRoot + File.separator + "_metadata" + File.separator + "ScrumRole.xml";
//		Document doc = XmlFileUtil.LoadXmlFile(path);
//		
//		if (doc == null) {
//			// load default file
//			doc = XmlFileUtil.LoadXmlFile(defaultpath);
//		}
//		Element root = doc.getRootElement();
//		Element role = root.getChild(rolename);
//		List<Element> role_Permissions = role.getChildren(ScrumEnum.SCRUMROLE_FILE_TAG_PERMISSION); 
//		ScrumRole s_Role = new ScrumRole(resource, rolename);
//		setAttribute(s_Role, role_Permissions);
//		return s_Role;
//	}
//	
//	// set ScrumRole permission attribute 
//	private void setAttribute(ScrumRole role, List<Element> attributes){
//		for (Element attribute: attributes) {
//			String value = attribute.getValue();
//			
//			if(value.equals(ScrumEnum.ACCESS_PRODUCTBACKLOG))
//				role.setAccessProductBacklog(Boolean.TRUE);
//			else if(value.equals(ScrumEnum.ACCESS_RELEASEPLAN))
//				role.setAccessReleasePlan(Boolean.TRUE);
//			else if(value.equals(ScrumEnum.ACCESS_SPRINTPLAN))
//				role.setAccessSprintPlan(Boolean.TRUE);
//			else if(value.equals(ScrumEnum.ACCESS_SPRINTBACKLOG))
//				role.setAccessSprintBacklog(Boolean.TRUE);
//			else if(value.equals(ScrumEnum.ACCESS_TASKBOARD))
//				role.setAccessTaskBoard(Boolean.TRUE);
//			else if(value.equals(ScrumEnum.ACCESS_UNPLANNED))
//				role.setAccessUnplannedItem(Boolean.TRUE);
//			else if(value.equals(ScrumEnum.ACCESS_RETROSPECTIVE))
//				role.setAccessRetrospective(Boolean.TRUE);
//			else if(value.equals(ScrumEnum.ACCESS_REPORT))
//				role.setReadReport(Boolean.TRUE);
//			else if(value.equals(ScrumEnum.ACCESS_EDITPROJECT))
//				role.setEditProject(Boolean.TRUE);
//		}
//	}
	
/*	public ScrumRole getScrumRole(IProject project, IAccount account){
		synchronized(this) {
			scrumRoles = this.getScrumRoles(account);
			ScrumRole sr = scrumRoles.get(project.getName());
			return sr;
		}
	}
	
	//透過account 得到相對專案的權限
	public Map<String, ScrumRole> getScrumRoles(IAccount account){
		synchronized(this) {
			this.setScrumRoles( account );
		    return scrumRoles;
		}
	}
	
	public void setScrumRoles( IAccount account ){
		scrumRoles = new HashMap<String, ScrumRole>();
		
		IAccountManager accountManager = AccountFactory.getManager();
		IPermission permAdmin = accountManager.getPermission("system_admin");
		
		//如果帳號為admin的角色，則給他所有的權限
		if(account.checkPermission(permAdmin)){
			synchronized(this) {
				IProject[] projects = ProjectControler.getAllProjects();//get projects information from workspace
				for(IProject project: projects){
					ScrumRole s_role = new ScrumRole(project.getName(), ScrumEnum.SCRUMROLE_ADMINISTRATOR);
					s_role.setAccessProductBacklog(Boolean.TRUE);
					s_role.setAccessReleasePlan(Boolean.TRUE);
					s_role.setAccessSprintPlan(Boolean.TRUE);
					s_role.setAccessSprintBacklog(Boolean.TRUE);
					s_role.setAccessTaskBoard(Boolean.TRUE);
					s_role.setAccessUnplannedItem(Boolean.TRUE);
					s_role.setAccessRetrospective(Boolean.TRUE);
					s_role.setReadReport(Boolean.TRUE);
					s_role.setEditProject(Boolean.TRUE);		// edit project set
					s_role.setisAdmin(Boolean.TRUE);			// admin set
					scrumRoles.put(project.getName(), s_role);
				}
			}
		}else{
			//不是admin的角色, 只是一般的使用者
			IRole[] roles = account.getRoles();
			for(IRole role: roles){
				IPermission[] permissions = role.getPermisions();
				for(IPermission per: permissions){//get permission from account
					String resource = per.getResourceName();
					String permission = per.getOperation();
					ScrumRole s_role = null;
					
					// check system actor
					if( ! resource.equals("system")) {
						s_role = getPermission(resource, permission);
					}
					
					// check guest actor
					if (permission.equalsIgnoreCase("Guest")) {
						s_role.setisGuest(true);
					}
					
					// add to map
					if(s_role!=null) {
						scrumRoles.put(resource, s_role);
					}
				}
			}
		}
	}*/	
	
}
