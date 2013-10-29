/*
 * [AspectJ 語法參考]
 * 
 * 1.取代原有函式的執行(代入與回傳的參數型態保持一致)
 *   pointcut 切點函式名稱(代入參數型態 代入參數名稱)
 *   : execution(回傳參數型態 欲取代的函式名稱(代入參數型態)) && arg(代入參數名稱);
 *   	 
 *   回傳參數型態 around(代入參數型態 代入參數名稱)
 *   : 切點函式名稱(代入參數名稱) {
 *   	// replaced code
 *   }
 *   
 *   ex: 
 *   pointcut replaceFunc(String arg0, String arg1)
 *   : execution(String className.replacedFunc(String, String)) && arg(arg0, arg1);
 *   
 *   String around(String arg0, String arg1)
 *   : replaceFunc(arg0, arg1) {
 *   	System.out.println("我要取代妳的功能: " + thisJoinPoint);
 *   }
 *   
 *   2.
 */
package ntut.csie.ezScrum.SaaS.aspect;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import ntut.csie.ezScrum.SaaS.PMF;
import ntut.csie.ezScrum.SaaS.database.ProjectDataStore;
import ntut.csie.ezScrum.SaaS.database.AccountDataStore;
import ntut.csie.ezScrum.SaaS.database.ScrumRoleDataStore;
import ntut.csie.ezScrum.SaaS.interfaces.account.Account;
import ntut.csie.ezScrum.SaaS.interfaces.pic.internal.Project;

import ntut.csie.ezScrum.SaaS.util.DateUtil;
import ntut.csie.ezScrum.SaaS.util.ScrumEnum;
import ntut.csie.ezScrum.SaaS.util.ezScrumUtil;
import ntut.csie.ezScrum.pic.core.ScrumRole;
import ntut.csie.ezScrum.pic.core.IUserSession;
import ntut.csie.ezScrum.web.action.CreateDatabaseAction;
import ntut.csie.ezScrum.web.action.TestConnAction;
import ntut.csie.ezScrum.web.dataObject.ITSInformation;
import ntut.csie.ezScrum.web.form.ProjectInfoForm;
import ntut.csie.ezScrum.web.logic.ProjectLogic;
import ntut.csie.ezScrum.web.mapper.ProjectMapper;
import ntut.csie.jcis.account.core.IAccount;
import ntut.csie.jcis.account.core.IPermission;
import ntut.csie.jcis.account.core.IRole;
import ntut.csie.jcis.account.core.internal.Permission;
import ntut.csie.jcis.account.core.internal.Role;
import ntut.csie.jcis.project.core.IProjectDescription;
import ntut.csie.jcis.project.core.internal.ProjectDescription;
import ntut.csie.jcis.resource.core.IProject;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;


// 有關資料庫物件存取的程式碼 (DataStore)必須做 data nucleus enhance 的動作
public aspect CPA_Project {

	/*
	 * ProjectMapper
	 */
	
//	private ProjectInfoForm convertProjectInfo(ProjectInformation projectInformation) {
//		String name = projectInformation.getName();
//		String displayName = projectInformation.getDisplayName();
//		String comment = projectInformation.getComment();
//		String manager = projectInformation.getManager(); 
//		String attachFileSize = projectInformation.getAttachFileSize();
//		
//		ProjectInfoForm projectInfoForm = new ProjectInfoForm();
//		// 塞入假資料
//		projectInfoForm.setServerType("SVN");
//		projectInfoForm.setCvsConnectionType("pserver");
//		projectInfoForm.setSvnHook("Close");
//		projectInfoForm.setOutputPath("/");
//		projectInfoForm.setSourcePathString("/");
//		// 塞入使用者輸入的資料
//		projectInfoForm.setName(name);
//		projectInfoForm.setDisplayName(displayName);
//		projectInfoForm.setComment(comment);
//		projectInfoForm.setProjectManager(manager);
//		projectInfoForm.setAttachFileSize(attachFileSize);
//
//		return projectInfoForm;	
//	}	
	
	private Project convertFormToProject(ProjectInfoForm saveProjectInfoForm) {
		Project project = new Project(saveProjectInfoForm.getName());

		project.setDisplayName(saveProjectInfoForm.getDisplayName());
		project.setComment(saveProjectInfoForm.getComment());
		project.setCreateDate(DateUtil.getNowDate());
		project.setManager(saveProjectInfoForm.getProjectManager());		
		
		// 上傳檔案待實作
		// String fileSize = saveProjectInfoForm.getAttachFileSize();
		// if(fileSize.compareTo("")==0) //如果fileSize沒有填值的話，則自動填入2
		// desc.setAttachFileSize("2");
		// else
		// desc.setAttachFileSize(fileSize);
		
		// follow ori.
		project.setDescription(saveProjectInfoForm);

		/*
		 * ScrumRole: 設定各個角色的初始值,參考Web版本(與原本GAE版本不太一樣)
		 */
		String projectId = project.getProjectId();
		
		// PO
		ScrumRole sr = new ScrumRole(projectId, ScrumEnum.SCRUMROLE_PRODUCTOWNER);
		sr.setAccessProductBacklog(true);
		sr.setAccessReleasePlan(true);
		sr.setAccessSprintPlan(true);
		sr.setAccessSprintBacklog(true);		
		sr.setAccessTaskBoard(true);
		sr.setAccessRetrospective(true);		
		sr.setAccessUnplannedItem(true);
		sr.setEditProject(true);
		sr.setReadReport(true);
		project.setScrumRole(ScrumEnum.SCRUMROLE_PRODUCTOWNER, sr);
		
		// ScrumMaster: all except Edit Project
		sr = new ScrumRole(projectId, ScrumEnum.SCRUMROLE_SCRUMMASTER);
		sr.setAccessProductBacklog(true);
		sr.setAccessReleasePlan(true);
		sr.setAccessSprintPlan(true);
		sr.setAccessSprintBacklog(true);		
		sr.setAccessTaskBoard(true);
		sr.setAccessRetrospective(true);		
		sr.setAccessUnplannedItem(true);
		sr.setReadReport(true);
		project.setScrumRole(ScrumEnum.SCRUMROLE_SCRUMMASTER, sr);
		
		// ScrumTeam: the same as ScrumMaster
		sr = new ScrumRole(projectId, ScrumEnum.SCRUMROLE_SCRUMTEAM);
		sr.setAccessProductBacklog(true);
		sr.setAccessReleasePlan(true);
		sr.setAccessSprintPlan(true);
		sr.setAccessSprintBacklog(true);		
		sr.setAccessTaskBoard(true);
		sr.setAccessRetrospective(true);		
		sr.setAccessUnplannedItem(true);
		sr.setReadReport(true);
		project.setScrumRole(ScrumEnum.SCRUMROLE_SCRUMTEAM, sr);
		
		// Stakeholder: view Report only
		sr = new ScrumRole(projectId, ScrumEnum.SCRUMROLE_STAKEHOLDER);
		sr.setReadReport(true);
		project.setScrumRole(ScrumEnum.SCRUMROLE_STAKEHOLDER, sr);
		
		// Guest: none
		sr = new ScrumRole(projectId, ScrumEnum.SCRUMROLE_GUEST);
		project.setScrumRole(ScrumEnum.SCRUMROLE_GUEST, sr);			
		
		return project;
	}	
	
	// replace: constructor of ProjectMapper.new()
	pointcut ProjectMapperPC() 
	: execution(ProjectMapper.new());

	void around() 
	: ProjectMapperPC() {
		System.out.println("replaced by AOP...ProjectMapperPC: " + thisJoinPoint);
	}
	
	// replace: public IProject createProject(IUserSession userSession, ITSInformation itsInformation, ProjectInfoForm projectInfoForm) throws Exception
	pointcut createProjectPC(IUserSession userSession, ITSInformation itsInformation, ProjectInfoForm projectInfoForm)
	: execution(IProject ProjectMapper.createProject(IUserSession, ITSInformation, ProjectInfoForm)) && args(userSession, itsInformation, projectInfoForm);

	IProject around(IUserSession userSession, ITSInformation itsInformation, ProjectInfoForm projectInfoForm) throws Exception
	: createProjectPC(userSession, itsInformation, projectInfoForm) {	
		System.out.println("replaced by AOP...createProjectPC: " + thisJoinPoint);		
		
		// convert 同時也預設了各個ScrumRole的存取權限
		Project project = this.convertFormToProject(projectInfoForm);

		PersistenceManager persistenceManager = PMF.get().getPersistenceManager();
		
		// 以ProjecId為 key存入資料庫
		Key key = KeyFactory.createKey(ProjectDataStore.class.getSimpleName(), project.getProjectId());

		ProjectDataStore projectDS = new ProjectDataStore(key);

		projectDS.setName(project.getProjectId());
		projectDS.setDisplayName(project.getDisplayName());
		projectDS.setComment(project.getComment());
		projectDS.setManager(project.getProjectManager());
		projectDS.setCreateDate(project.getCreateDate());
		// ?
		projectDS.setNewIssueID(ScrumEnum.PROJECT_DEFAULT_ISSUE);
		projectDS.setNewTagID(ScrumEnum.PROJECT_DEFAULT_ISSUE);

		/*
		 * Scrum Role: 從convertFormToProject()預設的存取權限取出並存入資料庫
		 */
		String projectId = project.getProjectId();
		
		// PO
		ScrumRole sr = project.getScrumRole(ScrumEnum.SCRUMROLE_PRODUCTOWNER);
		ScrumRoleDataStore srds = new ScrumRoleDataStore(sr);
		projectDS.getScrumRoles().add(srds);

		// Scrum Master
		sr = project.getScrumRole(ScrumEnum.SCRUMROLE_SCRUMMASTER);
		srds = new ScrumRoleDataStore(sr);
		projectDS.getScrumRoles().add(srds);

		// Scrum Team
		sr = project.getScrumRole(ScrumEnum.SCRUMROLE_SCRUMTEAM);
		srds = new ScrumRoleDataStore(sr);
		projectDS.getScrumRoles().add(srds);

		// Stakeholder
		sr = project.getScrumRole(ScrumEnum.SCRUMROLE_STAKEHOLDER);
		srds = new ScrumRoleDataStore(sr);
		projectDS.getScrumRoles().add(srds);

		// Guest
		sr = project.getScrumRole(ScrumEnum.SCRUMROLE_GUEST);
		srds = new ScrumRoleDataStore(sr);
		projectDS.getScrumRoles().add(srds);

		// no need to save ScrumRoleDS?
		try {
			persistenceManager.makePersistent(projectDS);
		} finally {
			persistenceManager.close();
		}
		
		return project;
	}
	
	// replace: IProject ProjectMapper.updateProject(ProjectInfoForm saveProjectInfoForm)
	pointcut updateProjectPC(ProjectInfoForm saveProjectInfoForm)
	: execution(IProject ProjectMapper.updateProject(ProjectInfoForm)) && args(saveProjectInfoForm);

	IProject around(ProjectInfoForm saveProjectInfoForm)
	: updateProjectPC(saveProjectInfoForm) {	
		System.out.println("replaced by AOP...updateProjectPC: " + thisJoinPoint);		

		// 不包含 ScrumRole permission 的更新		
		Project project = this.convertFormToProject(saveProjectInfoForm);
		PersistenceManager pm = PMF.get().getPersistenceManager();

		try {
			Key key = KeyFactory.createKey(ProjectDataStore.class.getSimpleName(), project.getProjectId());
			
			ProjectDataStore p = pm.getObjectById(ProjectDataStore.class, key);
			p.setDisplayName(project.getDisplayName());
			p.setComment(project.getComment());
			p.setManager(project.getProjectManager());
			
			pm.makePersistent(p);
		} finally {
			pm.close();
		}		
		
		return project;				
	}	

	// replace: List<IProject> ProjectMapper.getAllProjectList()
	pointcut getAllProjectListPC() 
	: execution(List<IProject> ProjectMapper.getAllProjectList());

	List<IProject> around()
		: getAllProjectListPC() {
		System.out.println("replaced by AOP...getAllProjectListPC: " + thisJoinPoint);

		List<IProject> projectlist = new ArrayList<IProject>();
		PersistenceManager pm = PMF.get().getPersistenceManager();
		
		// 以 Project JDO 為取出的條件
		Query query = pm.newQuery(ProjectDataStore.class);
		@SuppressWarnings("unchecked")
		List<ProjectDataStore> result = (List<ProjectDataStore>) query
				.execute();

		System.out.println(">>>>>>>>>>>>>>> Project JDO get: " + result.size());
		
		for (int i = 0; i < result.size(); i++) {
			ProjectDataStore p = result.get(i);

			Project project = new Project(p.getName());	// ID = NAME
			project.setDisplayName(p.getDisplayName());
			
			project.setComment(p.getComment());
			project.setManager(p.getManager());
			project.setCreateDate(p.getCreateDate());

			// follow ori.
			IProjectDescription aProjDesc = new ProjectDescription(p.getName());
			
			aProjDesc.setName(p.getName());	// ID
			aProjDesc.setDisplayName(project.getDisplayName());
			aProjDesc.setComment(project.getComment());		
			aProjDesc.setProjectManager(project.getProjectManager());
			aProjDesc.setCreateDate(project.getCreateDate());
			
			project.setProjectDesc(aProjDesc);			
			
			// Scrum Role part
			List<ScrumRoleDataStore> scrumRolesDS = p.getScrumRoles();
			for (int j = 0; j < scrumRolesDS.size(); j++) {
				ScrumRoleDataStore srDS = scrumRolesDS.get(j);
				ScrumRole sr = new ScrumRole(p.getName(), srDS.getRoleName());

				sr.setAccessProductBacklog(srDS.getAccessProductBacklog());
				sr.setAccessReleasePlan(srDS.getAccessReleasePlan());
				sr.setAccessRetrospective(srDS.getAccessRetrospective());
				sr.setAccessSprintBacklog(srDS.getAccessSprintBacklog());
				sr.setAccessSprintPlan(srDS.getAccessSprintPlan());
				sr.setAccessTaskBoard(srDS.getAccessTaskBoard());
				sr.setAccessUnplannedItem(srDS.getAccessUnplannedItem());
				sr.setEditProject(srDS.getEditProject());
				sr.setReadReport(srDS.getReadReport());

				project.setScrumRole(srDS.getRoleName(), sr);
			}
			projectlist.add(project);
		}

		pm.close();
		return projectlist;
	}

	// replace: IProject ProjectMapper.getProjectByID(String projectID)
	pointcut getProjectByIdPC(String projectID) 
	: execution(IProject ProjectMapper.getProjectByID(String)) && args(projectID);
	
	IProject around(String projectID)
	: getProjectByIdPC(projectID) {
		System.out.println("replaced by AOP...getProjectByIdPC: " + thisJoinPoint);

		PersistenceManager pm = PMF.get().getPersistenceManager();
		Project project = null; // fix later

		try {
			// 以 projectID 為 key 來取出相對應的 Project data
			Key key = KeyFactory.createKey(ProjectDataStore.class.getSimpleName(), projectID);
			ProjectDataStore p = pm.getObjectById(ProjectDataStore.class, key);

			// Project data 從 DB 轉存到 Memory
			project = new Project(p.getName());

			project.setDisplayName(p.getDisplayName());
			project.setComment(p.getComment());
			project.setManager(p.getManager());
			project.setCreateDate(p.getCreateDate());

			// follow ori.
			IProjectDescription aProjDesc = new ProjectDescription(projectID);
			
			aProjDesc.setName(projectID);	// ID
			aProjDesc.setDisplayName(project.getDisplayName());
			aProjDesc.setComment(project.getComment());		
			aProjDesc.setProjectManager(project.getProjectManager());
			aProjDesc.setCreateDate(project.getCreateDate());
			
			project.setProjectDesc(aProjDesc);
			
			// 有關 Scrum Role 權限設定的資料
			List<ScrumRoleDataStore> scrumRolesDS = p.getScrumRoles();
			for (int i = 0; i < scrumRolesDS.size(); i++) {
				ScrumRoleDataStore srDS = scrumRolesDS.get(i);
				ScrumRole sr = new ScrumRole(p.getName(), srDS.getRoleName());

				sr.setAccessProductBacklog(srDS.getAccessProductBacklog());
				sr.setAccessReleasePlan(srDS.getAccessReleasePlan());
				sr.setAccessRetrospective(srDS.getAccessRetrospective());
				sr.setAccessSprintBacklog(srDS.getAccessSprintBacklog());
				sr.setAccessSprintPlan(srDS.getAccessSprintPlan());
				sr.setAccessTaskBoard(srDS.getAccessTaskBoard());
				sr.setAccessUnplannedItem(srDS.getAccessUnplannedItem());
				sr.setEditProject(srDS.getEditProject());
				sr.setReadReport(srDS.getReadReport());

				project.setScrumRole(srDS.getRoleName(), sr);
			}

		} finally {
			pm.close();
		}
		
		return project;
	}

	// replace: public List<IAccount> getProjectMemberList(IUserSession userSession, IProject project)

	pointcut getProjectMemberListPC(IUserSession userSession, IProject project)
	: execution(List<IAccount> ProjectMapper.getProjectMemberList(IUserSession, IProject)) && args(userSession, project);
	
	List<IAccount> around(IUserSession userSession, IProject project) 
	: getProjectMemberListPC(userSession, project) {
		System.out.println("replaced by AOP...: getProjectMemberListPC" + thisJoinPoint);		
		
		String projectName = project.getName();
		ArrayList<IAccount> accountList = new ArrayList<IAccount>();
		PersistenceManager pm = PMF.get().getPersistenceManager();
		
		Query query = pm.newQuery(AccountDataStore.class);
		@SuppressWarnings("unchecked")
		List<AccountDataStore> result = (List<AccountDataStore>) query.execute();
		
		for(int i=0; i < result.size(); i++)
		{
			AccountDataStore accoutDataDS = result.get(i);
			boolean isInProject = false;
			
			IAccount account = new Account(accoutDataDS.getId(), accoutDataDS.getName(), ezScrumUtil.getMd5(accoutDataDS.getPassword()), false);
			account.setPassword(accoutDataDS.getPassword());
	    	account.setEmail(accoutDataDS.getEmail());
	    	account.setEnable(accoutDataDS.getEnable());	// add
	    	
	    	List<String> permList = accoutDataDS.getPermissions();
	    	for (int j=0; j < permList.size(); j++) {
	    		String permName = permList.get(j);
	    		String theProjectName = permName.substring(0, permName.lastIndexOf('_'));
	    		if (theProjectName.equals(projectName))
	    			isInProject = true;
	    		
	    		String roleName = permName.substring(permName.lastIndexOf('_')+1, permName.length());
	    		// convert
//	    		account.addPermission(AccountFactory.createPermission(permName, theProjectName, roleName));
	    		IPermission permission = new Permission(permName, theProjectName, roleName);
	    		IRole role = new Role(permName, roleName);

	    		role.addPermission(permission);
	    		account.addRole(role);
	    	}
	    	
	    	if (isInProject)
	    		accountList.add(account);				
		}

		pm.close();
		
		return accountList;
	}	
	
	
	/**
	 * 取得該專案中，使用者是否有存取Taskboard的權限。
	 * @param userSession
	 * @param project
	 */
	// replace: public List<String> getProjectScrumWorkerList(IUserSession userSession, IProject project)
	pointcut getProjectScrumWorkerListPC(IUserSession userSession, IProject project)
	: execution(List<String> ProjectMapper.getProjectScrumWorkerList(IUserSession, IProject)) && args(userSession, project);
	
	List<String> around(IUserSession userSession, IProject project) 
	: getProjectScrumWorkerListPC(userSession, project) {
		System.out.println("replaced by AOP...: getProjectMemberListPC" + thisJoinPoint);		
		
		String projectName = project.getName();
		PersistenceManager pm = PMF.get().getPersistenceManager();
		
		// 以 projectID 為 key 來取出相對應的 Project data
		Key key = KeyFactory.createKey(ProjectDataStore.class.getSimpleName(), projectName);
		ProjectDataStore projectDataStore = pm.getObjectById(ProjectDataStore.class, key);
		List<ScrumRoleDataStore> scrumDataStoreList = projectDataStore.getScrumRoles();
		boolean isAccessTaskBoard = false;
		for(ScrumRoleDataStore scrumRoleDataStore:scrumDataStoreList){
			if(scrumRoleDataStore.getAccessTaskBoard()){
				isAccessTaskBoard = true;
			}
		}
		
		Query query = pm.newQuery(AccountDataStore.class);
		@SuppressWarnings("unchecked")
		List<AccountDataStore> result = (List<AccountDataStore>) query.execute();
		List<String> accountList = new ArrayList<String>();
//		for(int i=0; i < result.size(); i++){
		for(AccountDataStore accoutDataDS:result){
//			AccountDataStore accoutDataDS = result.get(i);
			boolean isInProject = false;
	    	
	    	List<String> permList = accoutDataDS.getPermissions();
//	    	for (int j=0; j < permList.size(); j++) {
	    	for(String permName:permList){
//	    		String permName = permList.get(j);
	    		String theProjectName = permName.substring(0, permName.lastIndexOf('_'));
	    		if (theProjectName.equals(projectName))
	    			isInProject = true;
	    		
	    		String roleName = permName.substring(permName.lastIndexOf('_')+1, permName.length());
	    		// convert
	    		IPermission permission = new Permission(permName, theProjectName, roleName);
	    		IRole role = new Role(permName, roleName);

	    		role.addPermission(permission);
	    	}
	    	
	    	if (isInProject && isAccessTaskBoard)
	    		accountList.add(accoutDataDS.getName());				
		}

		pm.close();
		
		return accountList;
	}
	
	/*
	 * DB 檢查與建立的部分
	 */

	// [Action]
	
	// replace: public ActionForward TestConnAction.execute
	//	(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) 
	pointcut TestConnActionPC(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
	: execution(ActionForward TestConnAction.execute(ActionMapping, ActionForm, HttpServletRequest, HttpServletResponse)) && args(mapping, form, request, response);
	
	ActionForward around(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
	: TestConnActionPC(mapping, form, request, response)  {
		System.out.println("replaced by AOP...TestConnActionPC: " + thisJoinPoint);
		
		try {
			response.getWriter().write("success");
			response.getWriter().close();
    	} catch (IOException e) {
    		System.out.println("TestConnAction.java : response occur IOException. " );
			e.printStackTrace();
		}

		return null;
	}
	
	// replace: public ActionForward CreateDatabaseAction.execute
	//	(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
	pointcut CreateDatabaseActionPC(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
	: execution(ActionForward CreateDatabaseAction.execute(ActionMapping, ActionForm, HttpServletRequest, HttpServletResponse)) && args(mapping, form, request, response);
	
	ActionForward around(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
	: CreateDatabaseActionPC(mapping, form, request, response)  {
		System.out.println("replaced by AOP...: CreateDatabaseActionPC" + thisJoinPoint);
		
		try {
			response.setContentType("text/html; charset=utf-8");
			response.getWriter().write("{\"success\":true}");
			response.getWriter().close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}	
	
	// SprintPlanHelper 尚未重構 fix later
	// replace: public SprintPlanHelper(IProject project)
//	pointcut getProjectListPC() 
//	: within(ProjectHelper)
//	 	&& withincode(String getProjectListXML(IAccount)) 
//		&& call(String SprintPlanHelper.getNextDemoDate());
//
//	String around() 
//	: getProjectListPC() {
//		System.out.println("replaced by AOP...getProjectListPC: " + thisJoinPoint);
//		return null;	// demo date
//	}			
	
	// [Logic] 在 InitialPlugIn.init() 等程式會做基本檔案複製動作,將這些複製動作取代
	
	// replace: public void ProjectLogic.cloneDefaultFile()
	pointcut cloneDefaultFilePC() 
	: execution(void ProjectLogic.cloneDefaultFile());

	void around() 
	: cloneDefaultFilePC() {
		System.out.println("replaced by AOP...cloneDefaultFilePC: " + thisJoinPoint);
	}	
	
	// replace: public void check_default_role()
	pointcut check_default_rolePC() 
	: execution(void ProjectLogic.check_default_role());

	void around() 
	: check_default_rolePC() {
		System.out.println("replaced by AOP...check_default_rolePC: " + thisJoinPoint);
	}		
	
	// replace:	public void check_role_guest()
	pointcut check_role_guestPC() 
	: execution(void ProjectLogic.check_role_guest());

	void around() 
	: check_role_guestPC() {
		System.out.println("replaced by AOP...check_role_guestPC: " + thisJoinPoint);
	}	
	
	/*
	 * Others
	 */	
	
	// PIC: 有呼叫AccountFactory.getManager(), but AccountFactory is static function 待改良 fix later
	// replace: public IUserSession ProjectInfoCenter.login(String id, String Password)
//	pointcut loginPC(String id, String Password)
//	: call(IUserSession ProjectInfoCenter.login(String, String)) && args(id, Password);
//	
//	IUserSession around(String id, String Password) throws LogonException
//	: loginPC(id, Password) {
//		System.out.println("replaced by AOP...loginPC: " + thisJoinPoint);
//		
//		IAccount theAccount = null;		
//		IAccountManager manager = AccountFactory.getManager();
//		
//		manager.confirmAccount(id, Password);		
//		theAccount = manager.getAccount(id);		
//		IUserSession theUserSession = new UserSession(theAccount);
//		
//		return theUserSession;
//	}		
	
}