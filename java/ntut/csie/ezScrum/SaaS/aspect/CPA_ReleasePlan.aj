package ntut.csie.ezScrum.SaaS.aspect;

import java.util.ArrayList;
import java.util.List;

import javax.jdo.JDOObjectNotFoundException;
import javax.jdo.PersistenceManager;

import ntut.csie.ezScrum.SaaS.PMF;
import ntut.csie.ezScrum.SaaS.database.ProjectDataStore;
import ntut.csie.ezScrum.SaaS.database.ReleaseDataStore;
import ntut.csie.ezScrum.issue.sql.service.core.ITSPrefsStorage;
import ntut.csie.ezScrum.iteration.core.IReleasePlanDesc;
import ntut.csie.ezScrum.iteration.core.IStory;
import ntut.csie.ezScrum.iteration.iternal.ReleasePlanDesc;
import ntut.csie.ezScrum.pic.core.IUserSession;
import ntut.csie.ezScrum.web.control.ProductBacklogHelper;
import ntut.csie.ezScrum.web.logic.ProductBacklogLogic;
import ntut.csie.ezScrum.web.mapper.ReleasePlanMapper;
import ntut.csie.jcis.resource.core.IProject;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;


// 有關資料庫物件存取的程式碼 (DataStore)必須做 data nucleus enhance 的動作
public aspect CPA_ReleasePlan {

	private String projectId;	
	
	/*
	 *  ReleasePlanMapper
	 */	

	// replace: constructor of ReleasePlanMapper.new()
	pointcut ReleasePlanMapperPC(IProject project) 
	: execution(ReleasePlanMapper.new(IProject)) && args(project);

	void around(IProject project) 
	: ReleasePlanMapperPC(project) {
		System.out.println("replaced by AOP...ReleasePlanMapperPC: " + thisJoinPoint);
		
		this.projectId = project.getName();		
	}
	
	// replace: public List<IReleasePlanDesc> getReleasePlanList()
	pointcut getReleasePlanListPC() 
	: execution(List<IReleasePlanDesc> ReleasePlanMapper.getReleasePlanList());

	List<IReleasePlanDesc> around()
	: getReleasePlanListPC() {
		System.out.println("replaced by AOP...getReleasePlanListPC: " + thisJoinPoint);
		
		PersistenceManager pm = PMF.get().getPersistenceManager();
		List<IReleasePlanDesc> releases = new ArrayList<IReleasePlanDesc>();
		List<ReleaseDataStore> releaseListDS = new ArrayList<ReleaseDataStore>();
		
		try {
			Key projectkey = KeyFactory.createKey(ProjectDataStore.class.getSimpleName(), this.projectId);
			ProjectDataStore projectDS = pm.getObjectById(ProjectDataStore.class, projectkey);
			releaseListDS.addAll(projectDS.getReleases());
		} finally {
			pm.close();  
		}
		
		for (int i = 0; i < releaseListDS.size(); i++) {
			ReleaseDataStore releaseDS = releaseListDS.get(i);

			IReleasePlanDesc release = new ReleasePlanDesc();
			release.setID(releaseDS.getID());
			release.setName(releaseDS.getName());
			release.setDescription(releaseDS.getDescription());
			release.setStartDate(releaseDS.getStartDate());
			release.setEndDate(releaseDS.getEndDate());
			
//			this.getReleasePlanDesc(release);	// for sorting & set Sprint List
			releases.add(release);
		}
		
		return releases;		
	}
	
	// replace: public void addReleasePlan(IReleasePlanDesc desc)
	pointcut addReleasePlanPC(IReleasePlanDesc desc) 
	: execution(void ReleasePlanMapper.addReleasePlan(IReleasePlanDesc)) && args(desc);

	void around(IReleasePlanDesc desc) 
	: addReleasePlanPC(desc) {
		System.out.println("replaced by AOP...addReleasePlanPC: " + thisJoinPoint);
		
		PersistenceManager pm = PMF.get().getPersistenceManager();
		Key prjKey = KeyFactory.createKey(ProjectDataStore.class.getSimpleName(), this.projectId);

		ProjectDataStore projectDS = pm.getObjectById(ProjectDataStore.class, prjKey);
		Key key = new KeyFactory.Builder(prjKey).addChild(ReleaseDataStore.class.getSimpleName(), desc.getID()).getKey();

		ReleaseDataStore releaseDS = new ReleaseDataStore(key);
		releaseDS.setID(desc.getID());
		releaseDS.setName(desc.getName());
		releaseDS.setDescription(desc.getDescription());
		releaseDS.setStartDate(desc.getStartDate());
		releaseDS.setEndDate(desc.getEndDate());

		projectDS.getReleases().add(releaseDS);
		try {
			pm.makePersistent(releaseDS);
		} finally {
			pm.close();
		}

	}
	
	// replace: public void updateReleasePlan(IReleasePlanDesc desc)
	pointcut updateReleasePlanPC(IReleasePlanDesc desc) 
	: execution(void ReleasePlanMapper.updateReleasePlan(IReleasePlanDesc)) && args(desc);

	void around(IReleasePlanDesc desc) 
	: updateReleasePlanPC(desc) {
		System.out.println("replaced by AOP...updateReleasePlanPC: " + thisJoinPoint);
		
		PersistenceManager pm = PMF.get().getPersistenceManager();
		
		Key prjKey = KeyFactory.createKey(ProjectDataStore.class.getSimpleName(), this.projectId);
		Key key = new KeyFactory.Builder(prjKey).addChild(ReleaseDataStore.class.getSimpleName(), desc.getID()).getKey();
		
		try {
			ReleaseDataStore releaseDS = pm.getObjectById(ReleaseDataStore.class, key);
			releaseDS.setID(desc.getID());
			releaseDS.setName(desc.getName());
			releaseDS.setDescription(desc.getDescription());
			releaseDS.setStartDate(desc.getStartDate());
			releaseDS.setEndDate(desc.getEndDate());
			
			pm.makePersistent(releaseDS);
		} catch (JDOObjectNotFoundException e) {
			
		}
		finally {
			pm.close();
		}		
	}
	
	// replace: public void deleteReleasePlan(String id)
	pointcut deleteReleasePlanPC(String id) 
	: execution(void ReleasePlanMapper.deleteReleasePlan(String)) && args(id);

	void around(String id) 
	: deleteReleasePlanPC(id) {
		System.out.println("replaced by AOP...deleteReleasePlanPC: " + thisJoinPoint);
		
		PersistenceManager pm = PMF.get().getPersistenceManager();
		
		Key prjKey = KeyFactory.createKey(ProjectDataStore.class.getSimpleName(), this.projectId);
		Key key = new KeyFactory.Builder(prjKey).addChild(ReleaseDataStore.class.getSimpleName(), id).getKey();
		
		try {
			ReleaseDataStore releaseDS = pm.getObjectById(ReleaseDataStore.class, key);		
			if (releaseDS != null) {
				ProjectDataStore projectDS = pm.getObjectById(ProjectDataStore.class, prjKey);
				projectDS.getReleases().remove(releaseDS);
				
				pm.deletePersistent(releaseDS);
			}
		} 
		catch (JDOObjectNotFoundException e) {
			
		}
		finally {
			pm.close();
		}		
	}	

	/*
	 * ReleasePlanHelper use ProductBacklogHelper
	 * 暫時取代 等修正後再移除
	 */	
	
	// replace: public IStory[] getStoriesByRelease(IReleasePlanDesc desc) 
	pointcut getStoriesByReleasePC(IReleasePlanDesc desc) 
	: execution(IStory[] ProductBacklogHelper.getStoriesByRelease(IReleasePlanDesc)) && args(desc);

	IStory[] around(IReleasePlanDesc desc)
	: getStoriesByReleasePC(desc) {
		System.out.println("replaced by AOP...getStoriesByReleasePC: " + thisJoinPoint);
		
		List<IStory> list = new ArrayList<IStory>();		
		return list.toArray(new IStory[list.size()]);		
	}		
	
//	// replace: public void removeRelease(String issueID)
//	pointcut removeReleaseTagFromIssuePC(String issueID)
//	: execution(void ProductBacklogLogic.removeReleaseTagFromIssue(String)) && args(issueID);
//
//	void around(String issueID) 
//	: removeReleaseTagFromIssuePC(issueID) {
//		System.out.println("replaced by AOP...removeReleasePC: " + thisJoinPoint);		
//	}

//	// replace: public void addRelease(ArrayList<Long> list, String releaseID)
//	pointcut addReleaseTagToIssuePC(List<Long> list, String releaseID)
//	: execution(void ProductBacklogLogic.addReleaseTagToIssue(List<Long>, String)) && args(list, releaseID);
//
//	void around(List<Long> list, String releaseID) 
//	: addReleaseTagToIssuePC(list, releaseID) {
//		System.out.println("replaced by AOP...addReleaseTagToIssuePC: " + thisJoinPoint);		
//	}	

	/*
	 * Others 
	 */
	
	// fix later
	pointcut allITSPrefsStoragePC () 
	: execution(void ntut.csie.ezScrum.issue.sql.service.core.ITSPrefsStorage..*(..));

	void around () 
	: allITSPrefsStoragePC() {
		System.out.println("replaced by AOP...: <<ITS>> allITSPrefsStoragePC: " + thisJoinPoint);
	}

	// replace: constructor of 	public ITSPrefsStorage(IProject project, IUserSession userSession)
	pointcut ITSPrefsStoragePC(IProject project, IUserSession userSession) 
	: execution(ITSPrefsStorage.new(IProject, IUserSession)) && args(project, userSession);

	void around(IProject project, IUserSession userSession) 
	: ITSPrefsStoragePC(project, userSession) {
		System.out.println("replaced by AOP...: <<ITS>> ITSPrefsStoragePC: " + thisJoinPoint);
	}	
	
}