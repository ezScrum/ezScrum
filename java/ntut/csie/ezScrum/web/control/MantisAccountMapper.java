package ntut.csie.ezScrum.web.control;

import ntut.csie.ezScrum.issue.core.ITSEnum;
import ntut.csie.ezScrum.issue.sql.service.core.IITSService;
import ntut.csie.ezScrum.issue.sql.service.core.ITSPrefsStorage;
import ntut.csie.ezScrum.issue.sql.service.core.ITSServiceFactory;
import ntut.csie.ezScrum.pic.core.IUserSession;
import ntut.csie.jcis.account.core.AccountFactory;
import ntut.csie.jcis.account.core.IAccount;
import ntut.csie.jcis.account.core.IAccountManager;
import ntut.csie.jcis.resource.core.IProject;

public class MantisAccountMapper {
	private IProject m_project;
	private ITSServiceFactory m_itsFactory;
	private ITSPrefsStorage m_itsPrefs;
	private IUserSession m_userSession;
	
	public MantisAccountMapper( IProject project,IUserSession userSession){
		m_project = project;
		m_userSession = userSession;
		
		//初始ITS的設定
		m_itsFactory = ITSServiceFactory.getInstance();
		m_itsPrefs = new ITSPrefsStorage(m_project, m_userSession);	
	}
	
	//新增使用者
	public void addUser(String name,String password,String email, String realName,String access_Level,String cookie_string,String createDate,String lastVisitDate) throws Exception {
		IITSService itsService = m_itsFactory.getService(
				ITSEnum.MANTIS_SERVICE_ID, m_itsPrefs);
		itsService.openConnect();
		itsService.addUser(name, password, email, realName,access_Level,cookie_string,createDate,lastVisitDate);
		itsService.closeConnect();
	}
	
	//新增使用者跟Project之間的關係
	public void addUserProjectRelation(String name,String access_Level)throws Exception{
		IITSService itsService = m_itsFactory.getService(
				ITSEnum.MANTIS_SERVICE_ID, m_itsPrefs);
		itsService.openConnect();
		itsService.addUserProjectRelation(m_project.getName(),name,access_Level);
		itsService.closeConnect();
	}
	
	//刪除使用者
	public void deleteUser(String userName) throws Exception {
		IITSService itsService = m_itsFactory.getService(
				ITSEnum.MANTIS_SERVICE_ID, m_itsPrefs);
		itsService.openConnect();
		itsService.deleteUser(userName);
		itsService.closeConnect();
	}
	
	
	//刪除使用者跟Project之間的關係
	public void deleteUserProjectRelation(String userName,String projectName) throws Exception {
		IITSService itsService = m_itsFactory.getService(
				ITSEnum.MANTIS_SERVICE_ID, m_itsPrefs);
		itsService.openConnect();
		itsService.deleteUserProjectRelation(userName,projectName);
		itsService.closeConnect();
	}

	//更新user的資料
	public void updateUserProfile(String userID,String realName,String password,String email,String enable)throws Exception{
		IITSService itsService = m_itsFactory.getService(
				ITSEnum.MANTIS_SERVICE_ID, m_itsPrefs);
		itsService.openConnect();
		itsService.updateUserProfile(userID,realName,password,email,enable);
		itsService.closeConnect();
	}
	
	//回傳使用者是否存在於資料庫中
	public boolean existUser(String userID){
		IITSService itsService = m_itsFactory.getService(ITSEnum.MANTIS_SERVICE_ID, m_itsPrefs);
		itsService.openConnect();
		boolean exist = itsService.existUser(userID);
		itsService.closeConnect();
		
		IAccountManager manager = AccountFactory.getManager();
		IAccount acc = manager.getAccount(userID);
		boolean isInRoleBase = false;
		if ( (acc!= null) && acc.getEnable().equals("true")) {
			isInRoleBase = true;
		}
		
		return (exist && isInRoleBase);
	}
	
	/**
	 * 回傳此專案所有成員
	 */
	public String[] getActorListByViewerAccessLevel() {
		IITSService itsService = m_itsFactory.getService(m_itsPrefs);
		itsService.openConnect();
		String[] actors = itsService.getActors(this.m_project.getName(), ITSEnum.VIEWER_ACCESS_LEVEL);		
		itsService.closeConnect();
		return actors;
	}
	
	public String[] getActorListByUpdaterAccessLevel(){
		IITSService itsService = m_itsFactory.getService(m_itsPrefs);
		itsService.openConnect();
		String[] actors = itsService.getActors(m_project.getName(), ITSEnum.UPDATER_ACCESS_LEVEL);
		itsService.closeConnect();
		return actors;
	}
//	public List<IAccount> getProjectMemberList() {
//		IITSService itsService = m_itsFactory.getService(m_itsPrefs);
//		itsService.openConnect();
//		String[] actors = itsService.getActors(this.m_project.getName(), ITSEnum.VIEWER_ACCESS_LEVEL);		
//		itsService.closeConnect();
//		
//		IAccountManager am = AccountFactory.getManager();
//		
//		List<IAccount> Accountlist = new LinkedList<IAccount>();
//		for (String actor : actors) {
//			if (actor.equalsIgnoreCase("administrator") || actor.equalsIgnoreCase("admin")) {
//				continue;
//			}
//			
//			if (existUser(actor)) {
//				Accountlist.add(am.getAccount(actor));
//			}
//		}
//		return Accountlist;
//	}
	
	//	move to AccountMapper
	/**
	 * 回傳此 Scrum Team 內可以存取 TaskBoard 權限的人，代表可以領取工作者
	 */
//	public List<String> getScrumWorkerList() {
//		IITSService itsService = m_itsFactory.getService(m_itsPrefs);
//		itsService.openConnect();
//		String[] actors = itsService.getActors(this.m_project.getName(), ITSEnum.UPDATER_ACCESS_LEVEL);
//		List<String> list = new ArrayList<String>();
//		
//		list.add("");
//		for (String actor : actors) {
//			if (actor.equalsIgnoreCase("administrator") || actor.equalsIgnoreCase("admin")) {
//				continue;
//			}
//			
//			if (existUser(actor) && isScrumTeamWorker(actor)) {
//				list.add(actor);
//			}
//		}
//		itsService.closeConnect();
//		return list;
//	}

	//	move to AccountMapper
	// 驗證此 user 是否可以存取 TaskBoard，因為使用此 worker list 回傳為在 scrum 內會領取工作者
//	private boolean isScrumTeamWorker(String userID) {
//		IAccountManager manager = AccountFactory.getManager();
//		IAccount acc = manager.getAccount(userID);
//		
//		if (acc != null) {
//			String projectName = this.m_project.getName();
//			
////			ScrumRoleManager srmanager = new ScrumRoleManager();
////			Map<String, ScrumRole> ScrumRoleMap = srmanager.getScrumRoles(acc);
//			Map<String, ScrumRole> ScrumRoleMap = (new ScrumRoleLogic()).getScrumRoles(acc);
//			ScrumRole sr = ScrumRoleMap.get(projectName);
//			
//			// 判斷此角色對於此專案是否有存取 TaskBoard 的權限
//			if (sr != null && sr.getAccessTaskBoard()) {
//				return true;
//			}
//		}
//		
//		return false;
//	}
	
	/**
	 * never use
	 */
//	public List<String> getActorList() {
//		IITSService itsService = m_itsFactory.getService(m_itsPrefs);
//		itsService.openConnect();
//		String[] actors = itsService.getActors(this.m_project.getName(),
//				ITSEnum.UPDATER_ACCESS_LEVEL);
//
//		List<String> list = new ArrayList<String>();
//		MantisAccountMapper helper = new MantisAccountMapper(this.m_project, this.m_userSession);
//		
//		list.add("");
//		for (String actor : actors) {
//			if (actor.equalsIgnoreCase("administrator")
//					|| actor.equalsIgnoreCase("admin"))
//				continue;
//			if (helper.existUser(actor)) {
//				list.add(actor);
//			}
//		}
//		itsService.closeConnect();
//		return list;
//	}
	
	//刪除Project所有屬於access_level的使用者
//	public void deleteUserProjectRelationByAccessLevel(String projectName,String access_level) throws Exception {
//		IITSService itsService = m_itsFactory.getService(
//				ITSEnum.MANTIS_SERVICE_ID, m_itsPrefs);
//		itsService.openConnect();
//		itsService.deleteUserProjectRelationByAccessLevel(projectName,access_level);
//		itsService.closeConnect();
//	}	
	
	//回傳是否User和任何一個Project有關聯
//	public boolean isUserHasRelationByAnyProject(String userName)throws Exception{
//		IITSService itsService = m_itsFactory.getService(
//				ITSEnum.MANTIS_SERVICE_ID, m_itsPrefs);
//		itsService.openConnect();
//		boolean exist = itsService.isUserHasRelationByAnyProject(userName);
//		itsService.closeConnect();
//		return exist;
//	}
	
	
	/**
	 * move to AccountLogic
	 */
	
	/**
	 * 判斷帳號對於專案的權限
	 * @return
	 */
//	public boolean CheckAccount( HttpServletRequest request ) {
//		return new AccountLogic().CheckAccount(request);
//		// 判斷使用者是否為被啟用狀態
//	}
	
//	public boolean CheckAccount( HttpServletRequest request ) {
//		// 判斷使用者是否為被啟用狀態
//		IAccount acc = m_userSession.getAccount();
//		if ( ! acc.getEnable().equals("true")) {
//			return false;
//		}
//		ScrumRole sr = SessionManager.getScrumRole(request, m_project, acc);
//		
//		if( sr == null ){
//			return false;
//		}
//		
//		// 判斷使用者是否為 guest 使用者
//		if ( sr.isGuest() ) {
//			return false;
//		}
//		
//		// 判斷使用者是否為 admin 使用者
//		if ( sr.isAdmin() ) {
//			return true;
//		}
//		
//		// 判斷使用者是否為存在於資料庫的使用者
//		if ( ! existUser(acc.getID())) {
//			return false;
//		}
//		
//		return true;
//	}
}
