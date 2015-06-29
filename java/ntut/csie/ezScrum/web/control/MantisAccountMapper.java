package ntut.csie.ezScrum.web.control;

import ntut.csie.ezScrum.issue.core.ITSEnum;
import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.issue.sql.service.core.IITSService;
import ntut.csie.ezScrum.issue.sql.service.core.ITSServiceFactory;
import ntut.csie.ezScrum.pic.core.IUserSession;
import ntut.csie.jcis.account.core.AccountFactory;
import ntut.csie.jcis.account.core.IAccount;
import ntut.csie.jcis.account.core.IAccountManager;
import ntut.csie.jcis.resource.core.IProject;

public class MantisAccountMapper {
	private IProject m_project;
	private ITSServiceFactory m_itsFactory;
	private Configuration m_config;
	private IUserSession m_userSession;
	
	public MantisAccountMapper( IProject project,IUserSession userSession){
		m_project = project;
		m_userSession = userSession;
		
		//初始ITS的設定
		m_itsFactory = ITSServiceFactory.getInstance();
		//m_itsPrefs = new ITSPrefsStorage(m_project, m_userSession);	
		m_config = new Configuration(m_userSession);
	}
	
	//新增使用者
	public void addUser(String name,String password,String email, String realName,String access_Level,String cookie_string,String createDate,String lastVisitDate) throws Exception {
		IITSService itsService = m_itsFactory.getService(
				ITSEnum.MANTIS_SERVICE_ID, m_config);
		itsService.openConnect();
		itsService.addUser(name, password, email, realName,access_Level,cookie_string,createDate,lastVisitDate);
		itsService.closeConnect();
	}
	
	//新增使用者跟Project之間的關係
	public void addUserProjectRelation(String name,String access_Level)throws Exception{
		IITSService itsService = m_itsFactory.getService(
				ITSEnum.MANTIS_SERVICE_ID, m_config);
		itsService.openConnect();
		itsService.addUserProjectRelation(m_project.getName(),name,access_Level);
		itsService.closeConnect();
	}
	
	//刪除使用者
	public void deleteUser(String userName) throws Exception {
		IITSService itsService = m_itsFactory.getService(
				ITSEnum.MANTIS_SERVICE_ID, m_config);
		itsService.openConnect();
		itsService.deleteUser(userName);
		itsService.closeConnect();
	}
	
	
	//刪除使用者跟Project之間的關係
	public void deleteUserProjectRelation(String userName,String projectName) throws Exception {
		IITSService itsService = m_itsFactory.getService(
				ITSEnum.MANTIS_SERVICE_ID, m_config);
		itsService.openConnect();
		itsService.deleteUserProjectRelation(userName,projectName);
		itsService.closeConnect();
	}

	//更新user的資料
	public void updateUserProfile(String userID,String realName,String password,String email,String enable)throws Exception{
		IITSService itsService = m_itsFactory.getService(
				ITSEnum.MANTIS_SERVICE_ID, m_config);
		itsService.openConnect();
		itsService.updateUserProfile(userID,realName,password,email,enable);
		itsService.closeConnect();
	}
	
	//回傳使用者是否存在於資料庫中
	public boolean existUser(String userID){
		IITSService itsService = m_itsFactory.getService(ITSEnum.MANTIS_SERVICE_ID, m_config);
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
		IITSService itsService = m_itsFactory.getService(m_config);
		itsService.openConnect();
		String[] actors = itsService.getActors(this.m_project.getName(), ITSEnum.VIEWER_ACCESS_LEVEL);		
		itsService.closeConnect();
		return actors;
	}
	
	public String[] getActorListByUpdaterAccessLevel(){
		IITSService itsService = m_itsFactory.getService(m_config);
		itsService.openConnect();
		String[] actors = itsService.getActors(m_project.getName(), ITSEnum.UPDATER_ACCESS_LEVEL);
		itsService.closeConnect();
		return actors;
	}
}
