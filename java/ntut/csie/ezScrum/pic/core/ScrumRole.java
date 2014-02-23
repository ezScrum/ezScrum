package ntut.csie.ezScrum.pic.core;

import ntut.csie.ezScrum.web.dataObject.RoleEnum;

/**
 * @author py2k
 */
public class ScrumRole {
	private String mProjectName = "";
	private String mRoleName = "";

	private Boolean mProductBacklog = false;
	private Boolean mReleasePlan = false;
	private Boolean mSprintPlan = false;
	private Boolean mTaskboard = false;
	private Boolean mSprintBacklog = false;
	private Boolean mUnplanned = false;
	private Boolean mRetrospective = false;
	private Boolean mReport = false;
	private Boolean mEditProject = false;

	private Boolean mKanbanBacklog = false;
	private Boolean mManageStatus = false;
	private Boolean mKanbanBoard = false;
	private Boolean mKanbanReport = false;

	// add for guest permission
	private Boolean mIsGuest = false;
	private Boolean mIsAdmin = false;

	public Boolean isAdmin() {
		return mIsAdmin;
	}

	public void setisAdmin(Boolean permission) {
		this.mIsAdmin = permission;
	}

	public ScrumRole(String projectName, String roleName) {
		mProjectName = projectName;
		mRoleName = roleName;
	}	
	
	public ScrumRole(RoleEnum role) {
		// 設定預設的Role權限
		switch(role) {
			case Guest:
				setGuestRole();
				break;
			case ProductOwner:
				setProductOwnerRole();
				break;
			case ScrumMaster:
				setScrumMasterRole();
				break;
			case ScrumTeam:
				setScrumTeamRole();
				break;
			case Stakeholder:
				setStakeHolderRole();
				break;
			default:
				break;
		}
	}
	
	private void setStakeHolderRole() {
		setAccessProductBacklog(false);
		setAccessReleasePlan(false);
		setAccessRetrospective(false);
		setAccessSprintBacklog(false);
		setAccessSprintPlan(false);
		setAccessTaskBoard(false);
		setAccessUnplannedItem(false);
		setEditProject(false);
		setReadReport(true);
    }

	private void setScrumTeamRole() {
		setAccessProductBacklog(true);
		setAccessReleasePlan(true);
		setAccessRetrospective(true);
		setAccessSprintBacklog(true);
		setAccessSprintPlan(true);
		setAccessTaskBoard(true);
		setAccessUnplannedItem(true);
		setEditProject(false);
		setReadReport(true);
    }

	private void setScrumMasterRole() {
		setAccessProductBacklog(true);
		setAccessReleasePlan(true);
		setAccessRetrospective(true);
		setAccessSprintBacklog(true);
		setAccessSprintPlan(true);
		setAccessTaskBoard(true);
		setAccessUnplannedItem(true);
		setEditProject(true);
		setReadReport(true);
    }

	private void setProductOwnerRole() {
		setAccessProductBacklog(true);
		setAccessReleasePlan(true);
		setAccessRetrospective(true);
		setAccessSprintBacklog(true);
		setAccessSprintPlan(true);
		setAccessTaskBoard(true);
		setAccessUnplannedItem(true);
		setEditProject(true);
		setReadReport(true);
    }
	
	private void setGuestRole() {
		setAccessProductBacklog(false);
		setAccessReleasePlan(false);
		setAccessRetrospective(false);
		setAccessSprintBacklog(false);
		setAccessSprintPlan(false);
		setAccessTaskBoard(false);
		setAccessUnplannedItem(false);
		setEditProject(false);
		setReadReport(false);
    }

	public String getProjectName() {
		return mProjectName;
	}

	public String getRoleName() {
		return mRoleName;
	}

	// get permission
	public Boolean getAccessProductBacklog() {
		return mProductBacklog;
	}

	public Boolean getAccessReleasePlan() {
		return mReleasePlan;
	}

	public Boolean getAccessSprintPlan() {
		return mSprintPlan;
	}

	public Boolean getAccessTaskBoard() {
		return mTaskboard;
	}

	public Boolean getAccessSprintBacklog() {
		return mSprintBacklog;
	}

	public Boolean getAccessUnplannedItem() {
		return mUnplanned;
	}

	public Boolean getAccessRetrospective() {
		return mRetrospective;
	}

	public Boolean getReadReport() {
		return mReport;
	}

	public Boolean getEditProject() {
		return mEditProject;
	}

	// Kanban
	public Boolean getAccessKanbanBacklog() {
		return mKanbanBacklog;
	}

	public Boolean getAccessManageStatus() {
		return mManageStatus;
	}

	public Boolean getAccessKanbanBoard() {
		return mKanbanBoard;
	}

	public Boolean getAccessKanbanReport() {
		return mKanbanReport;
	}

	// add for guest permission
	public Boolean isGuest() {
		return mIsGuest;
	}

	// set permission
	public void setAccessProductBacklog(Boolean permission) {
		mProductBacklog = permission;
	}

	public void setAccessReleasePlan(Boolean permission) {
		mReleasePlan = permission;
	}

	public void setAccessSprintPlan(Boolean permission) {
		mSprintPlan = permission;
	}

	public void setAccessTaskBoard(Boolean permission) {
		mTaskboard = permission;
	}

	public void setAccessSprintBacklog(Boolean permission) {
		mSprintBacklog = permission;
	}

	public void setAccessUnplannedItem(Boolean permission) {
		mUnplanned = permission;
	}

	public void setAccessRetrospective(Boolean permission) {
		mRetrospective = permission;
	}

	public void setReadReport(Boolean permission) {
		mReport = permission;
	}

	public void setEditProject(Boolean permission) {
		mEditProject = permission;
	}

	// Kanban
	public void setAccessKanbanBacklog(Boolean permission) {
		mKanbanBacklog = permission;
	}

	public void setAccessManageStatus(Boolean permission) {
		mManageStatus = permission;
	}

	public void setAccessKanbanBoard(Boolean permission) {
		mKanbanBoard = permission;
	}

	public void setAccessKanbanReport(Boolean permission) {
		mKanbanReport = permission;
	}

	public void setisGuest(Boolean permission) {
		mIsGuest = permission;
	}

	// 使用Kanban流程則Scrum流程的權限全部設為False
	public void setToKanbanPermission() {
		mProductBacklog = false;
		mReleasePlan = false;
		mSprintPlan = false;
		mTaskboard = false;
		mSprintBacklog = false;
		mUnplanned = false;
		mRetrospective = false;
	}

	// 使用Scrum流程則Kanban流程的權限全部設為False
	public void setToScrumPermission() {
		mKanbanBacklog = false;
		mManageStatus = false;
		mKanbanBoard = false;
	}
}