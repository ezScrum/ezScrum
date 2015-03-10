package ntut.csie.ezScrum.web.control;

import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import ntut.csie.ezScrum.iteration.core.ScrumEnum;
import ntut.csie.ezScrum.pic.core.IUserSession;
import ntut.csie.ezScrum.pic.core.ScrumRole;
import ntut.csie.ezScrum.web.logic.ScrumRoleLogic;
import ntut.csie.jcis.account.core.AccountEnum;
import ntut.csie.jcis.account.core.AccountFactory;
import ntut.csie.jcis.account.core.IAccount;
import ntut.csie.jcis.account.core.IAccountManager;
import ntut.csie.jcis.account.core.IPermission;
import ntut.csie.jcis.account.core.IRole;
import ntut.csie.jcis.resource.core.IProject;
import ntut.csie.jcis.resource.core.ResourceFacade;

public class MantisAccountManager {
	private IUserSession session;
	
	public MantisAccountManager(IUserSession session){
		this.session = session;
	}
	
	private void createAccount(IProject project, IAccount account) throws Exception{
		String realName = account.getName();
		String name = account.getID();
		String email = account.getEmail();
		String password = account.getPassword();
		
		//the information of mantis user have create date
		Date   dateTime   =   new   Date();   
        SimpleDateFormat   format   =   new   SimpleDateFormat("yyyy-MM-dd HH:mm:ss");   
        String   createDate   =   format.format(dateTime);
        String   lastVisitDate   =   format.format(dateTime);
		
		//the information cookie_string is for mantis_user on DB 
		String cookie_string="";
		cookie_string=md5RandomString()+md5Time();
		//透過AccountHelper新增專案
		MantisAccountMapper ah=new MantisAccountMapper(project,session);
		ah.addUser(name,password,email,realName,AccountEnum.ACCESS_LEVEL_REPORTER,cookie_string,createDate,lastVisitDate);
	}

	public void addReleation(IAccount account, String resource, String operation) throws Exception{
		String name = account.getID();
		//如果專案名稱不是system的話, 才進行mantis的更新
		if(!resource.equals(AccountEnum.ACCESS_RESOURCE_SYSTEM)){
			IProject project = ResourceFacade.getWorkspace().getRoot().getProject(resource);		
			MantisAccountMapper ah = new MantisAccountMapper(project,session);
			if(!ah.existUser(name)){
				createAccount(project, account);
			}
			if(operation.equals(ScrumEnum.SCRUMROLE_STAKEHOLDER))
				ah.addUserProjectRelation(name,AccountEnum.ACCESS_LEVEL_VIEWER);
			else
				ah.addUserProjectRelation(name,AccountEnum.ACCESS_LEVEL_MANAGER);
		}
	}
	
	public void removeReleation(IAccount account, String resource) throws Exception{
		String userName = account.getID();
		//如果專案名稱不是system的話, 才進行mantis的更新
		if(!resource.equals(AccountEnum.ACCESS_RESOURCE_SYSTEM)){
			IProject project = ResourceFacade.getWorkspace().getRoot().getProject(resource);		
			MantisAccountMapper ah = new MantisAccountMapper(project,session);
			ah.deleteUserProjectRelation(userName, resource);
		}
	}
	
	public void deleteAccount(IAccountManager am, String id) throws Exception{
		//刪除mantis部分
		IAccount account = am.getAccount(id);
		IRole[] roles=account.getRoles();
		if(roles!=null){
			for(IRole role:roles){
				IPermission[] permissions=role.getPermisions();
				if(permissions!=null){
					for(IPermission permission:permissions){
						String resource=permission.getResourceName();
						if(!resource.equals(AccountEnum.ACCESS_RESOURCE_SYSTEM)){
							IProject project=ResourceFacade.getWorkspace().getRoot().getProject(resource);
							if(resource.compareTo("")!=0&&project!=null){
								MantisAccountMapper ah=new MantisAccountMapper(project,session);
								//先刪除user跟Project之間的關係，才不會造成找不到user的情況發生
								ah.deleteUserProjectRelation(id,project.getName());
								ah.deleteUser(id);
							}
						}
					}
				}
			}
		}
	}
	
	public void updateUserProfile(IAccount account) throws Exception{
		String userID = account.getID();
		String realName = account.getName();
		String password = account.getPassword();
		String email = account.getEmail();
		String enable = account.getEnable();
		String mantisEnable;
		if(enable.equals("true")){
			mantisEnable="1";
		} else {
			mantisEnable="0";
		}
		IRole[] roles = account.getRoles();
		if(roles!=null){
			for(IRole role: roles){
				IPermission[] permissions = role.getPermisions();
				if(permissions!=null){
					for(IPermission permission: permissions){
						String resource=permission.getResourceName();
						if(!resource.equals(AccountEnum.ACCESS_RESOURCE_SYSTEM)){
							IProject project=ResourceFacade.getWorkspace().getRoot().getProject(resource);
							if(resource.compareTo("")!=0&&project!=null){
								MantisAccountMapper ah=new MantisAccountMapper(project,session);
								//更新使用者的專案資訊
								ah.updateUserProfile(userID, realName, password, email, mantisEnable);
							}	
						}
					}
				}
					
			}
		}
	}
	
	public List<IAccount> getProjectMemberList(IProject project) {
		MantisAccountMapper mantisAccountMapper = new MantisAccountMapper(project, session);
		String[] projectActorList = mantisAccountMapper.getActorListByViewerAccessLevel();
		
		IAccountManager am = AccountFactory.getManager();
		List<IAccount> accountlist = new LinkedList<IAccount>();
		for (String actor : projectActorList) {
			if (actor.equalsIgnoreCase("administrator") || actor.equalsIgnoreCase("admin")) {
				continue;
			}
			
			if (mantisAccountMapper.existUser(actor)) {
				accountlist.add(am.getAccount(actor));
			}
		}
		return accountlist;
	}
	
	/**
	 * 回傳此 Scrum Team 內可以存取 TaskBoard 權限的人，代表可以領取工作者
	 */
	public List<String> getScrumWorkers(IProject project){
		MantisAccountMapper mantisAccountMapper = new MantisAccountMapper(project, session);
		String[] projectScrumWorkerList = mantisAccountMapper.getActorListByUpdaterAccessLevel();
		
		List<String> actorList = new ArrayList<String>();
		actorList.add("");
		for (String actor : projectScrumWorkerList) {
			if (actor.equalsIgnoreCase("administrator") || actor.equalsIgnoreCase("admin")) {
				continue;
			}
			
			if (mantisAccountMapper.existUser(actor) && this.isScrumTeamWorker(project, actor)) {
				actorList.add(actor);
			}
		}
		return actorList;
	}
	
	/**
	 *  驗證此 user 是否可以存取 TaskBoard，因為使用此 worker list 回傳為在 scrum team內會領取工作者
	 * @param project
	 * @param userID
	 * @return
	 */
	private boolean isScrumTeamWorker(IProject project, String userID) {
//		IAccountManager manager = AccountFactory.getManager();
//		IAccount acc = manager.getAccount(userID);
//		
//		if (acc != null) {
//			String projectName = project.getName();
//			
//			ScrumRoleManager srmanager = new ScrumRoleManager();
//			Map<String, ScrumRole> ScrumRoleMap = srmanager.getScrumRoles(acc);
//			
//			ScrumRole sr = ScrumRoleMap.get(projectName);
//			
//			// 判斷此角色對於此專案是否有存取 TaskBoard 的權限
//			if (sr != null && sr.getAccessTaskBoard()) {
//				return true;
//			}
//		}
//		
		return false;
	}
	
//以下為史上最偉大的演算法	author: py2k
//	public void createAccount(IAccount account) throws Exception{
//		String realName = account.getName();
//		String name = account.getID();
//		String email = account.getEmail();
//		String password = account.getPassword();
//		
//		//the information of mantis user have create date
//		Date   dateTime   =   new   Date();   
//        SimpleDateFormat   format   =   new   SimpleDateFormat("yyyy-MM-dd   HH:mm:ss");   
//        String   createDate   =   format.format(dateTime);
//        String   lastVisitDate   =   format.format(dateTime);
//		
//        
//		IRole[] roles=account.getRoles();
//		//the information cookie_string is for mantis_user on DB 
//		String cookie_string="";
//		cookie_string=md5RandomString()+md5Time();
//		for(IRole role:roles){
//			IPermission[] permissions=role.getPermisions();
//			for(IPermission permission:permissions){
//				String resource=permission.getResourceName();
//				String operation=permission.getOperation();
//				IProject project=ResourceFacade.getWorkspace().getRoot().getProject(resource);
//				if(resource.compareTo("")!=0&&project!=null){
//					AccountHelper ah=new AccountHelper(project,session);
//					if(resource.compareToIgnoreCase(AccountEnum.ACCESS_RESOURCE_SYSTEM)!=0){
//						try{
//							if(operation.compareToIgnoreCase(AccountEnum.ACCOUNT_ACTOR_ADMIN)==0){
//								//新增使用者到MySQL裡
//								ah.addUser(name,password,email,realName,AccountEnum.ACCESS_LEVEL_REPORTER,cookie_string,createDate,lastVisitDate);
//								//新增使用者跟Project之間的關係 到MySQL裡
//								ah.addUserProjectRelation(name,AccountEnum.ACCESS_LEVEL_MANAGER);
//							}
//							else if(operation.compareToIgnoreCase(AccountEnum.ACCOUNT_ACTOR_READ)==0){
//								//新增使用者到MySQL裡
//								ah.addUser(name,password,email,realName,AccountEnum.ACCESS_LEVEL_VIEWER,cookie_string,createDate,lastVisitDate);
//								//新增使用者跟Project之間的關係 到MySQL裡
//								ah.addUserProjectRelation(name,AccountEnum.ACCESS_LEVEL_VIEWER);
//							}
//							else{
//								//還有一個 create project ，還不知道要幹嘛的
//							}
//						}
//						catch(Exception e){
//							throw new Exception("Mantis 帳號加入失敗!!");
//						}
//					}
//				}
//			}
//		}
//	}
	
//	//update Mantis上面的資料
//	public void updateMantis(IAccount account, IAccountManager am, IRole[] oldRoles,List<String> newRoleList,String mantisEnable) throws Exception{
//		String id = account.getID();
//		String password = account.getPassword();
//		String email = account.getEmail();
//		String name = account.getName();
//		/*
//		 * 先算出交集，在得到應該刪除哪個role 以及該新增哪個role
//		 */
//		ArrayList<String> interSectionRoles = new ArrayList<String>();
//		ArrayList<String> addRoles = new ArrayList<String>();
//		ArrayList<String> eliminateRoles = new ArrayList<String>();
//		//得到交集的部分
//		for(IRole oldRole:oldRoles){
//			for(String newRole:newRoleList){
//				if(oldRole.getRoleName().compareTo(newRole)==0){
//					interSectionRoles.add(oldRole.getRoleName());
//				}
//			}
//		}
//		/* 
//		 * 數學式子:  新 - (新interSection舊) 
//		 *         = 新 - interSectionRoles 
//		 *         = mantis 需要新增的部分 (addRoles)
//		 */   
//		for(String newRole:newRoleList){
//			boolean exist = false;
//			for(String interSectionRole : interSectionRoles){
//				if(newRole.compareTo(interSectionRole)==0)
//					exist = true;
//			}
//			if(exist==false)
//				addRoles.add(newRole);
//		}
//		/* 
//		 * 數學式子:  舊 - (新interSection舊) 
//		 *         = 舊 - interSectionRoles 
//		 *         = mantis 需要刪除的部分 (eliminateRoles)
//		 */		
//		for(IRole oldRole:oldRoles){
//			boolean exist = false;
//			for(String interSectionRole : interSectionRoles){
//				if(oldRole.getRoleName().compareTo(interSectionRole)==0)
//					exist = true;
//			}
//			if(exist==false)
//				eliminateRoles.add(oldRole.getRoleName());
//		}		
//		
//		//得到Date
//		HashMap<String, String> date = getDate();
//		String createDate = date.get("createDate");
//		String lastVisitDate = date.get("lastVisitDate");
//		
//		//得到cookie_string
//		String cookie_string="";
//		cookie_string=md5RandomString()+md5Time();
//		
//		//新增mantis的Role
//		for(String addRole : addRoles){
//			IRole role = am.getRole(addRole);
//			if(role==null)
//				continue;
//			IPermission[] permissions = role.getPermisions();
//			for(IPermission permission:permissions){
//				String resource = permission.getResourceName();
//				String operation = permission.getOperation();
//				IProject project=ResourceFacade.getWorkspace().getRoot().getProject(resource);
//				if(resource.compareToIgnoreCase(AccountEnum.ACCESS_RESOURCE_SYSTEM)!=0&&project!=null){
//					AccountHelper ah=new AccountHelper(project,session);
//						try{
//							if(operation.compareToIgnoreCase(AccountEnum.ACCOUNT_ACTOR_ADMIN)==0){
//								//新增使用者到MySQL裡
//									ah.addUser(id,password,email,name,AccountEnum.ACCESS_LEVEL_REPORTER,cookie_string,createDate,lastVisitDate);
//								//新增使用者跟Project之間的關係 到MySQL裡
//								ah.addUserProjectRelation(id,AccountEnum.ACCESS_LEVEL_MANAGER);
//							}
//							else if(operation.compareToIgnoreCase(AccountEnum.ACCOUNT_ACTOR_READ)==0){
//								//新增使用者到MySQL裡
//									ah.addUser(id,password,email,name,AccountEnum.ACCESS_LEVEL_VIEWER,cookie_string,createDate,lastVisitDate);
//								//新增使用者跟Project之間的關係 到MySQL裡
//								ah.addUserProjectRelation(id,AccountEnum.ACCESS_LEVEL_VIEWER);
//							}
//							else{
//								//還有一個 create project ，還不知道要幹嘛的
//							}
//						}
//						catch(Exception e){
//							throw new Exception("Mantis 帳號加入失敗!!");
//						}
//				}
//			}
//		}
//		
//		//刪除mantis的Role
//		for(String eliminateRole : eliminateRoles){
//			IRole role = am.getRole(eliminateRole);
//			if(role==null)
//				continue;
//			IPermission[] permissions=role.getPermisions();
//			for(IPermission permission:permissions){
//				String resource=permission.getResourceName();
//				IProject project=ResourceFacade.getWorkspace().getRoot().getProject(resource);
//				if(resource.compareToIgnoreCase(AccountEnum.ACCESS_RESOURCE_SYSTEM)!=0&&project!=null){
//					AccountHelper ah=new AccountHelper(project,session);
//					//先刪除user跟Project之間的關係，才不會造成找不到user的情況發生
//					ah.deleteUserProjectRelation(id,project.getName());
////					//如果user和其他project有關聯則不刪除user，反之則刪除使用者帳號
////					但後來發現可能會造成mantis 的資料 欄位 會不正確
////					if(!ah.isUserHasRelationByAnyProject(id))
////						ah.deleteUser(id);
//				}
//			}
//		}
//		
//		//更新帳號資訊
//		for(String newRole:newRoleList){
//			IRole role = am.getRole(newRole);
//			if (role != null) {
//				IPermission[] permissions = role.getPermisions();
//				for(IPermission permission:permissions){
//					String resource = permission.getResourceName();
//					IProject project=ResourceFacade.getWorkspace().getRoot().getProject(resource);
//					if(!resource.equals(AccountEnum.ACCESS_RESOURCE_SYSTEM)&project!=null){
//						AccountHelper ah=new AccountHelper(project,session);
//						//更新user的資料
//						ah.updateUserProfile(id,name,password,email,mantisEnable);
//					}
//				}
//			}
//		}
//		
//	}
	
//	public void deleteAccount(IAccountManager am, String id) throws Exception{
//		//刪除mantis部分
//		IAccount account = am.getAccount(id);
//		IRole[] roles=account.getRoles();
//		for(IRole role:roles){
//			IPermission[] permissions=role.getPermisions();
//			for(IPermission permission:permissions){
//				String resource=permission.getResourceName();
//				if(resource.compareToIgnoreCase("system")!=0){
//					IProject project=ResourceFacade.getWorkspace().getRoot().getProject(resource);
//					if(resource.compareTo("")!=0&&project!=null){
//						AccountHelper ah=new AccountHelper(project,session);
//						//先刪除user跟Project之間的關係，才不會造成找不到user的情況發生
//						ah.deleteUserProjectRelation(id,project.getName());
//						ah.deleteUser(id);
//					}
//				}
//			}
//		}
//	}
	
//	public void deletePermission(IAccountManager am, String name) throws Exception{
//		//對mantis這部分進行刪除
//		IPermission permission = am.getPermission(name);
//		String resource = permission.getResourceName();
//		String access_level = permission.getOperation();
//		
//		IProject project=ResourceFacade.getWorkspace().getRoot().getProject(resource);
//		if(resource.compareToIgnoreCase(AccountEnum.ACCESS_RESOURCE_SYSTEM)!=0&&project!=null){
//			AccountHelper ah=new AccountHelper(project,session);
//			ah.deleteUserProjectRelationByAccessLevel(resource, access_level);
//		}
//	}
	
//	//刪除mantis Project 跟 user之間的list 
//	public void deleteChildRole(IRole role,IPermission[] permissions) throws Exception {
//		if(role!=null){
//			//先delete child role裡的account關係
//			IRole[] childRoles = role.getChildrenRoles();
//			for(IRole childRole:childRoles){
//				deleteChildRole(childRole,permissions);
//			}
//			IAccount[] accounts = role.getAccounts();
//			for(IAccount account:accounts){
//				for(IPermission permission:permissions){
//					String resource=permission.getResourceName();
//					IProject project=ResourceFacade.getWorkspace().getRoot().getProject(resource);
//					if(resource.compareToIgnoreCase(AccountEnum.ACCESS_RESOURCE_SYSTEM)!=0&&project!=null){
//						AccountHelper ah=new AccountHelper(project,session);
//						//先刪除user跟Project之間的關係，才不會造成找不到user的情況發生
//						ah.deleteUserProjectRelation(account.getID(),project.getName());
////						如果user和其他project有關聯則不刪除user，反之則刪除使用者帳號
////						但後來發現可能會造成mantis 的資料 欄位 會不正確
////						if(!ah.isUserHasRelationByAnyProject(account.getID()))
////							ah.deleteUser(account.getID());
//					}	
//				}
//			}
//		}
//	}
	
	//random字串 使用md5加密
	private String md5RandomString() throws Exception {    
	    String    chars     = "abcdefghijklmonpqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"; 
	    Random r = new Random(); 
	    char[] buf = new char[r.nextInt(chars.length())];
	    for (int i = 0; i < buf.length; i++) {    
	    	buf[i] = chars.charAt(r.nextInt(chars.length()));   
	    }
	    //使用md5加密
	    String md5Random = getMd5(new String(buf));
	    return md5Random;
	}
	//目前時間 使用md5加密
	private String md5Time() throws Exception {	
		Date   dateTime   =   new   Date();   
        SimpleDateFormat   format   =   new   SimpleDateFormat("yyyy-MM-dd   HH:mm:ss");   
        String   strTime   =   format.format(dateTime);
		//使用md5加密
        String md5Time = getMd5(strTime);
        return md5Time;
	}
	
	
	//轉成MD5
	private String getMd5(String str){
		MessageDigest md = null;
		try {
			md = MessageDigest.getInstance("MD5");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		md.update(str.getBytes());
		byte[] b = md.digest();
		str = byte2hex(b);
		return str;
	}
	
	//轉換
	private String byte2hex(byte[] b) {
		String hs = "";
		String stmp = "";
		for (int n = 0; n < b.length; n++) {
			stmp = (Integer.toHexString(b[n] & 0Xff));
			if (stmp.length() == 1) {
				hs = hs + "0" + stmp;
			} else {
				hs = hs + stmp;
			}
		}
		return hs;
	}
	
	
	/**
	 * never use
	 */
//	private HashMap<String, String> getDate(){
//		//建立帳號(Mantis部分)
//		Date   dateTime   =   new   Date();   
//        SimpleDateFormat   format   =   new   SimpleDateFormat("yyyy-MM-dd   HH:mm:ss");   
//        String   createDate   =   format.format(dateTime);
//        String   lastVisitDate   =   format.format(dateTime);
//        HashMap<String, String> date = new HashMap<String, String>();
//        date.put("createDate", createDate);
//        date.put("lastVisitDate", lastVisitDate);
//        return date;
//	}
	
	
}
