package ntut.csie.ezScrum.pic.core;


/**
 * @author py2k
 */
public class ScrumRole {
	private String m_projectName="";
	private String m_roleName="";
	
	private boolean m_productBacklog = false;
	private boolean m_releasePlan = false;
	private boolean m_sprintPlan = false;
	private boolean m_taskboard = false;
	private boolean m_sprintBacklog = false;
	private boolean m_unplanned = false;
	private boolean m_retrospective = false;
	private boolean m_report = false;
	private boolean m_editProject = false;
	
	private boolean m_kanbanBacklog = false;
	private boolean m_manageStatus = false;
	private boolean m_kanbanBoard = false;
	private boolean m_kanbanReport = false;
	
	// add for guest permission
	private boolean m_isGuest = false;
	private boolean m_isAdmin = false;
	
	public boolean isAdmin() {
		return m_isAdmin;
	}

	public void setisAdmin(boolean permission) {
		this.m_isAdmin = permission;
	}

	public ScrumRole(String projectName, String roleName) {
		m_projectName = projectName;
		m_roleName = roleName;
	}
	
	public String getProjectName() {
		return m_projectName;
	}

	public String getRoleName() {
		return m_roleName;
	}
	
	//get permission
	public boolean getAccessProductBacklog(){
		return m_productBacklog;
	}
	
	public boolean getAccessReleasePlan(){
		return m_releasePlan;
	}
	
	public boolean getAccessSprintPlan(){
		return m_sprintPlan;
	}
	
	public boolean getAccessTaskBoard(){
		return m_taskboard;
	}
	
	public boolean getAccessSprintBacklog(){
		return m_sprintBacklog;
	}
	
	public boolean getAccessUnplannedItem(){
		return m_unplanned;
	}
	
	public boolean getAccessRetrospective(){
		return m_retrospective;
	}
	
	public boolean getReadReport(){
		return m_report;
	}	
	
	public boolean getEditProject(){
		return m_editProject;
	}
	
	// Kanban
	public boolean getAccessKanbanBacklog(){
		return m_kanbanBacklog;
	}
	
	public boolean getAccessManageStatus(){
		return m_manageStatus;
	}
	
	public boolean getAccessKanbanBoard(){
		return m_kanbanBoard;
	}
	
	public boolean getAccessKanbanReport(){
		return m_kanbanReport;
	}
	
	// add for guest permission
	public boolean isGuest() {
		return m_isGuest;
	}
	
	//set permission
	public void setAccessProductBacklog(boolean permission){
		m_productBacklog = permission;
	}
	
	public void setAccessReleasePlan(boolean permission){
		m_releasePlan = permission;
	}
	
	public void setAccessSprintPlan(boolean permission){
		m_sprintPlan = permission;
	}
	
	public void setAccessTaskBoard(boolean permission){
		m_taskboard = permission;
	}
	
	public void setAccessSprintBacklog(boolean permission){
		m_sprintBacklog = permission;
	}
	
	public void setAccessUnplannedItem(boolean permission){
		m_unplanned = permission;
	}
	
	public void setAccessRetrospective(boolean permission){
		m_retrospective = permission;
	}
	
	public void setReadReport(boolean permission){
		m_report = permission;
	}	
	
	public void setEditProject(boolean permission){
		m_editProject = permission;
	}
	
	// Kanban
	public void setAccessKanbanBacklog(boolean permission){
		m_kanbanBacklog = permission;
	}
	
	public void setAccessManageStatus(boolean permission){
		m_manageStatus = permission;
	}
	
	public void setAccessKanbanBoard(boolean permission){
		m_kanbanBoard = permission;
	}
	
	public void setAccessKanbanReport(boolean permission){
		m_kanbanReport = permission;
	}
	
	public void setisGuest(boolean permission) {
		m_isGuest = permission;
	}
	
	// 使用Kanban流程則Scrum流程的權限全部設為False
	public void setToKanbanPermission(){
		m_productBacklog = false;
		m_releasePlan = false;
		m_sprintPlan = false;
		m_taskboard = false;
		m_sprintBacklog = false;
		m_unplanned = false;
		m_retrospective = false;
	}
	// 使用Scrum流程則Kanban流程的權限全部設為False
	public void setToScrumPermission(){
		m_kanbanBacklog = false;
		m_manageStatus = false;
		m_kanbanBoard = false;
	}
}