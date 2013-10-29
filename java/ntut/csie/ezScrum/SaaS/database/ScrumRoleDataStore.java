package ntut.csie.ezScrum.SaaS.database;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import ntut.csie.ezScrum.pic.core.ScrumRole;

import com.google.appengine.api.datastore.Key;

@PersistenceCapable
public class ScrumRoleDataStore {
	@SuppressWarnings("unused")
	@PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    private Key key;
	
	@SuppressWarnings("unused")
	@Persistent
	private ProjectDataStore project;
	
	@Persistent
	private String _roleName = "";	
	@Persistent
	private boolean _productBacklog = false;	
	@Persistent
	private boolean _releasePlan = false;	
	@Persistent
	private boolean _sprintPlan = false;	
	@Persistent
	private boolean _taskboard = false;	
	@Persistent
	private boolean _sprintBacklog = false;	
	@Persistent
	private boolean _unplanned = false;	
	@Persistent
	private boolean _retrospective = false;	
	@Persistent
	private boolean _report = false;	
	@Persistent
	private boolean _editProject = false;
	
	public ScrumRoleDataStore(String roleName) {
		_roleName = roleName;
	}
	
	public ScrumRoleDataStore(ScrumRole scrumRole) {
		_roleName = scrumRole.getRoleName();
		
		_productBacklog = scrumRole.getAccessProductBacklog();
		_releasePlan = scrumRole.getAccessReleasePlan();
		_sprintPlan = scrumRole.getAccessSprintBacklog();
		_taskboard = scrumRole.getAccessTaskBoard();
		_sprintBacklog = scrumRole.getAccessSprintBacklog();
		_unplanned = scrumRole.getAccessUnplannedItem();
		_retrospective = scrumRole.getAccessRetrospective();
		_report = scrumRole.getReadReport();
		_editProject = scrumRole.getEditProject();		
	}
	
	public String getRoleName() {
		return _roleName;
	}
	
	//get permission
	public boolean getAccessProductBacklog(){
		return _productBacklog;
	}
	
	public boolean getAccessReleasePlan(){
		return _releasePlan;
	}
	
	public boolean getAccessSprintPlan(){
		return _sprintPlan;
	}
	
	public boolean getAccessTaskBoard(){
		return _taskboard;
	}
	
	public boolean getAccessSprintBacklog(){
		return _sprintBacklog;
	}
	
	public boolean getAccessUnplannedItem(){
		return _unplanned;
	}
	
	public boolean getAccessRetrospective(){
		return _retrospective;
	}
	
	public boolean getReadReport(){
		return _report;
	}	
	
	public boolean getEditProject(){
		return _editProject;
	}
	
	//set permission
	public void setAccessProductBacklog(boolean permission){
		_productBacklog = permission;
	}
	
	public void setAccessReleasePlan(boolean permission){
		_releasePlan = permission;
	}
	
	public void setAccessSprintPlan(boolean permission){
		_sprintPlan = permission;
	}
	
	public void setAccessTaskBoard(boolean permission){
		_taskboard = permission;
	}
	
	public void setAccessSprintBacklog(boolean permission){
		_sprintBacklog = permission;
	}
	
	public void setAccessUnplannedItem(boolean permission){
		_unplanned = permission;
	}
	
	public void setAccessRetrospective(boolean permission){
		_retrospective = permission;
	}
	
	public void setReadReport(boolean permission){
		_report = permission;
	}	
	
	public void setEditProject(boolean permission){
		_editProject = permission;
	}
}