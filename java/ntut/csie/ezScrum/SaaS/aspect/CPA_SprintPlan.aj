package ntut.csie.ezScrum.SaaS.aspect;

import java.util.ArrayList;
import java.util.List;

import javax.jdo.JDOObjectNotFoundException;
import javax.jdo.PersistenceManager;

import ntut.csie.ezScrum.SaaS.PMF;
import ntut.csie.ezScrum.SaaS.database.ProjectDataStore;
import ntut.csie.ezScrum.SaaS.database.SprintDataStore;
import ntut.csie.ezScrum.iteration.core.ISprintPlanDesc;
import ntut.csie.ezScrum.iteration.iternal.SprintPlanDesc;
import ntut.csie.ezScrum.web.mapper.SprintPlanMapper;
import ntut.csie.jcis.resource.core.IProject;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;


// 有關資料庫物件存取的程式碼 (DataStore)必須做 data nucleus enhance 的動作
public aspect CPA_SprintPlan {

	private String projectId;
	
	/*
	 *  ReleasePlanMapper
	 */	

	// replace: constructor of SprintPlanMapper.new()
	pointcut SprintPlanMapperPC(IProject project) 
	: execution(SprintPlanMapper.new(IProject)) && args(project);

	void around(IProject project) 
	: SprintPlanMapperPC(project) {
		System.out.println("replaced by AOP...SprintPlanMapperPC: " + thisJoinPoint);
		
		this.projectId = project.getName();		
	}
	
	// replace: public ISprintPlanDesc getSprintPlan(String sprintId)
	pointcut getSprintPlanPC(String sprintId) 
	: execution(ISprintPlanDesc SprintPlanMapper.getSprintPlan(String)) && args(sprintId);

	ISprintPlanDesc around(String sprintId)
	: getSprintPlanPC(sprintId) {
		System.out.println("replaced by AOP...getSprintPlanPC: " + thisJoinPoint);

		PersistenceManager pm = PMF.get().getPersistenceManager();
		Key prjKey = KeyFactory.createKey(ProjectDataStore.class.getSimpleName(), this.projectId);
		Key key = new KeyFactory.Builder(prjKey).addChild(SprintDataStore.class.getSimpleName(), sprintId).getKey();

		try {
			SprintDataStore sprintDS = pm.getObjectById(SprintDataStore.class, key);

			ISprintPlanDesc sprint = new SprintPlanDesc();
			sprint.setID(sprintDS.getID());
			sprint.setGoal(sprintDS.getGoal());
			sprint.setInterval(sprintDS.getInterval());
			sprint.setMemberNumber(sprintDS.getMemberNumber());
			sprint.setFocusFactor(sprintDS.getFactor());
			sprint.setAvailableDays(sprintDS.getAvailableDays());
			sprint.setStartDate(sprintDS.getStartDate());
			sprint.setDemoDate(sprintDS.getDemoDate());
			sprint.setDemoPlace(sprintDS.getDemoPlace());
			sprint.setNotes(sprintDS.getNotes());

			return sprint;
		} catch (JDOObjectNotFoundException e) {

		} finally {
			pm.close();
		}

		return null;
	}
	
	// replace: public List<ISprintPlanDesc> getSprintPlanList()
	pointcut getSprintPlanListPC() 
	: execution(List<ISprintPlanDesc> SprintPlanMapper.getSprintPlanList());

	List<ISprintPlanDesc> around()
	: getSprintPlanListPC() {
		System.out.println("replaced by AOP...getSprintPlanListPC: " + thisJoinPoint);
				
		List<ISprintPlanDesc> sprintList = new ArrayList<ISprintPlanDesc>();

		PersistenceManager pm = PMF.get().getPersistenceManager();
		Key prjKey = KeyFactory.createKey(ProjectDataStore.class.getSimpleName(), this.projectId);
		try {
			ProjectDataStore projectDS = pm.getObjectById(ProjectDataStore.class, prjKey);
			List<SprintDataStore> sprintDSs = projectDS.getSprints();
			for (int i = 0; i < sprintDSs.size(); i++) {
				SprintDataStore sprintDS = sprintDSs.get(i);

				ISprintPlanDesc sprint = new SprintPlanDesc();
				sprint.setID(sprintDS.getID());
				sprint.setGoal(sprintDS.getGoal());
				sprint.setInterval(sprintDS.getInterval());
				sprint.setMemberNumber(sprintDS.getMemberNumber());
				sprint.setFocusFactor(sprintDS.getFactor());
				sprint.setAvailableDays(sprintDS.getAvailableDays());
				sprint.setStartDate(sprintDS.getStartDate());
				sprint.setDemoDate(sprintDS.getDemoDate());
				sprint.setDemoPlace(sprintDS.getDemoPlace());
				sprint.setNotes(sprintDS.getNotes());

				sprintList.add(sprint);
			}

			// sorting
//			Comparator<ISprint> comparator;
//
//			if (order.equals("startDate"))
//				comparator = new StartDateComparator();
//			else	// "id": default sort
//				comparator = new IdComparator();
//
//			Collections.sort(sprints, comparator);
			return sprintList;
		} catch (JDOObjectNotFoundException e) {

		} finally {
			pm.close();
		}

		return null;	
	}
	
	// replace: public void addSprintPlan(ISprintPlanDesc desc) 
	pointcut addSprintPlanPC(ISprintPlanDesc desc) 
	: execution(void SprintPlanMapper.addSprintPlan(ISprintPlanDesc)) && args(desc);

	void around(ISprintPlanDesc desc)
	: addSprintPlanPC(desc) {
		System.out.println("replaced by AOP...addSprintPlanPC: " + thisJoinPoint);
		
		PersistenceManager pm = PMF.get().getPersistenceManager();
		Key prjKey = KeyFactory.createKey(ProjectDataStore.class.getSimpleName(), this.projectId);
		ProjectDataStore projectDS = pm.getObjectById(ProjectDataStore.class, prjKey);
		Key key = new KeyFactory.Builder(prjKey).addChild(SprintDataStore.class.getSimpleName(), desc.getID()).getKey();

		SprintDataStore sprintDS = new SprintDataStore(key);
		sprintDS.setID(desc.getID());
		sprintDS.setGoal(desc.getGoal());
		sprintDS.setInterval(desc.getInterval());
		sprintDS.setMemberNumber(desc.getMemberNumber());
		sprintDS.setFactor(desc.getFocusFactor());
		sprintDS.setAvailableDays(desc.getAvailableDays());
		sprintDS.setStartDate(desc.getStartDate());
		sprintDS.setDemoDate(desc.getDemoDate());
		sprintDS.setDemoPlace(desc.getDemoPlace());
		sprintDS.setNotes(desc.getNotes());

		projectDS.getSprints().add(sprintDS);	// link with Project
		try {
			pm.makePersistent(sprintDS);
		} finally {
			pm.close();
		}		
	}
	
	// replace: public void updateSprintPlan(ISprintPlanDesc desc)
	pointcut updateSprintPlanPC(ISprintPlanDesc desc) 
	: execution(void SprintPlanMapper.updateSprintPlan(ISprintPlanDesc)) && args(desc);

	void around(ISprintPlanDesc desc)
	: updateSprintPlanPC(desc) {
		System.out.println("replaced by AOP...updateSprintPlanPC: " + thisJoinPoint);
		
		PersistenceManager pm = PMF.get().getPersistenceManager();		
		Key prjKey = KeyFactory.createKey(ProjectDataStore.class.getSimpleName(), this.projectId);
		Key key = new KeyFactory.Builder(prjKey).addChild(SprintDataStore.class.getSimpleName(), desc.getID()).getKey();
		
		try {
			SprintDataStore sprintDS = pm.getObjectById(SprintDataStore.class, key);
			sprintDS.setID(desc.getID());
			sprintDS.setGoal(desc.getGoal());
			sprintDS.setInterval(desc.getInterval());
			sprintDS.setMemberNumber(desc.getMemberNumber());
			sprintDS.setFactor(desc.getFocusFactor());
			sprintDS.setAvailableDays(desc.getAvailableDays());
			sprintDS.setStartDate(desc.getStartDate());
			sprintDS.setDemoDate(desc.getDemoDate());
			sprintDS.setDemoPlace(desc.getDemoPlace());
			sprintDS.setNotes(desc.getNotes());
			
			pm.makePersistent(sprintDS);
		} catch (JDOObjectNotFoundException e) {
			
		}
		finally {
			pm.close();
		}		
	}
	
	// replace: public void updateSprintPlanForActualCost(ISprintPlanDesc desc)
	pointcut updateSprintPlanForActualCostPC(ISprintPlanDesc desc) 
	: execution(void SprintPlanMapper.updateSprintPlanForActualCost(ISprintPlanDesc)) && args(desc);

	void around(ISprintPlanDesc desc)
	: updateSprintPlanForActualCostPC(desc) {
		System.out.println("replaced by AOP...updateSprintPlanForActualCostPC: " + thisJoinPoint);
		
		// 額外儲存ActualCost資訊,有用到嗎?
		System.out.println("??? updateSprintPlanForActualCost ???");
	}
	
	// replace: public void deleteSprintPlan(String sprintId)
	pointcut deleteSprintPlanPC(String sprintId) 
	: execution(void SprintPlanMapper.deleteSprintPlan(String)) && args(sprintId);

	void around(String sprintId)
	: deleteSprintPlanPC(sprintId) {
		System.out.println("replaced by AOP...deleteSprintPlanPC: " + thisJoinPoint);

		PersistenceManager pm = PMF.get().getPersistenceManager();
		
		Key prjKey = KeyFactory.createKey(ProjectDataStore.class.getSimpleName(), this.projectId);
		Key key = new KeyFactory.Builder(prjKey).addChild(SprintDataStore.class.getSimpleName(), sprintId).getKey();
		
		try {
			SprintDataStore sprintDS = pm.getObjectById(SprintDataStore.class, key);		
			if (sprintDS != null) {
				ProjectDataStore projectDS = pm.getObjectById(ProjectDataStore.class, prjKey);
				projectDS.getSprints().remove(sprintDS);
				
				pm.deletePersistent(sprintDS);
			}
		} 
		catch (JDOObjectNotFoundException e) {
			
		}
		finally {
			pm.close();
		}		
	}
	
	// replace: public void moveSprintPlan(int oldId, int newId)
	pointcut moveSprintPlanPC(int oldId, int newId) 
	: execution(void SprintPlanMapper.moveSprintPlan(int, int)) && args(oldId, newId);

	void around(int oldId, int newId)
	: moveSprintPlanPC(oldId, newId) {
		System.out.println("replaced by AOP...moveSprintPlanPC: " + thisJoinPoint);
		
		String oldID = Integer.toString(oldId);
		String newID = Integer.toString(newId);	
		
		PersistenceManager pm = PMF.get().getPersistenceManager();
		Key prjKey = KeyFactory.createKey(ProjectDataStore.class.getSimpleName(), this.projectId);
		Key key_old = new KeyFactory.Builder(prjKey).addChild(SprintDataStore.class.getSimpleName(), oldID).getKey();
		Key key_new = new KeyFactory.Builder(prjKey).addChild(SprintDataStore.class.getSimpleName(), newID).getKey();
		
		try {
			SprintDataStore sprintDS_old = pm.getObjectById(SprintDataStore.class, key_old);
			SprintDataStore sprintDS_new = pm.getObjectById(SprintDataStore.class, key_new);

			// load old to Temp sprint
			ISprintPlanDesc sprint = new SprintPlanDesc();
//			sprint.setID(sprintDS_old.getID());	// ID不變
			sprint.setGoal(sprintDS_old.getGoal());
			sprint.setInterval(sprintDS_old.getInterval());
			sprint.setMemberNumber(sprintDS_old.getMemberNumber());
			sprint.setFocusFactor(sprintDS_old.getFactor());
			sprint.setAvailableDays(sprintDS_old.getAvailableDays());
//			sprint.setStartDate(sprintDS_old.getStartDate());	// 須重新計算
//			sprint.setDemoDate(sprintDS_old.getDemoDate());	// 須重新計算
			sprint.setDemoPlace(sprintDS_old.getDemoPlace());
			sprint.setNotes(sprintDS_old.getNotes());
			
			// save new to old sprint
//			sprintDS_old.setID(sprintDS_new.getID());	// ID不變
			sprintDS_old.setGoal(sprintDS_new.getGoal());
			sprintDS_old.setInterval(sprintDS_new.getInterval());
			sprintDS_old.setMemberNumber(sprintDS_new.getMemberNumber());
			sprintDS_old.setFactor(sprintDS_new.getFactor());
			sprintDS_old.setAvailableDays(sprintDS_new.getAvailableDays());
//			sprintDS_old.setStartDate(sprintDS_new.getStartDate());	// 須重新計算
//			sprintDS_old.setDemoDate(sprintDS_new.getDemoDate());	// 須重新計算
			sprintDS_old.setDemoPlace(sprintDS_new.getDemoPlace());
			sprintDS_old.setNotes(sprintDS_new.getNotes());

			// save old to new sprint
//			sprintDS_new.setID(sprint.getID());	// 只有ID不變
			sprintDS_new.setGoal(sprint.getGoal());
			sprintDS_new.setInterval(sprint.getInterval());
			sprintDS_new.setMemberNumber(sprint.getMemberNumber());
			sprintDS_new.setFactor(sprint.getFocusFactor());
			sprintDS_new.setAvailableDays(sprint.getAvailableDays());
//			sprintDS_new.setStartDate(sprint.getStartDate());	// 須重新計算
//			sprintDS_new.setDemoDate(sprint.getDemoDate());	// 須重新計算
			sprintDS_new.setDemoPlace(sprint.getDemoPlace());
			sprintDS_new.setNotes(sprint.getNotes());			
			
			// 
			pm.makePersistent(sprintDS_new);
			pm.makePersistent(sprintDS_old);		
		} catch (JDOObjectNotFoundException e) {

		} finally {
			pm.close();
		}
		
		
	}	
	
}