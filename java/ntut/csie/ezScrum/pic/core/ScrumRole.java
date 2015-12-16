package ntut.csie.ezScrum.pic.core;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import ntut.csie.ezScrum.web.databaseEnum.RoleEnum;
import ntut.csie.ezScrum.web.databaseEnum.ScrumRoleEnum;

public class ScrumRole {
	private String mProjectName = "";
	private String mRoleName = "";

	private Boolean mProductBacklog = false;
	private Boolean mReleasePlan = false;
	private Boolean mSprintPlan = false;
	private Boolean mTaskboard = false;
	private Boolean mSprintBacklog = false;
	private Boolean mUnplan = false;
	private Boolean mRetrospective = false;
	private Boolean mAccessReport = false;
	private Boolean mAccessEditProject = false;

	// add for guest permission
	private Boolean mIsGuest = false;
	private Boolean mIsAdmin = false;

	public Boolean isAdmin() {
		return mIsAdmin;
	}

	public void setisAdmin(Boolean permission) {
		this.mIsAdmin = permission;
		setAccessProductBacklog(true);
		setAccessReleasePlan(true);
		setAccessRetrospective(true);
		setAccessSprintBacklog(true);
		setAccessSprintPlan(true);
		setAccessTaskBoard(true);
		setAccessUnplanItem(true);
		setAccessEditProject(true);
		setAccessReport(true);
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
		setAccessUnplanItem(false);
		setAccessEditProject(false);
		setAccessReport(true);
    }

	private void setScrumTeamRole() {
		setAccessProductBacklog(true);
		setAccessReleasePlan(true);
		setAccessRetrospective(true);
		setAccessSprintBacklog(true);
		setAccessSprintPlan(true);
		setAccessTaskBoard(true);
		setAccessUnplanItem(true);
		setAccessEditProject(false);
		setAccessReport(true);
    }

	private void setScrumMasterRole() {
		setAccessProductBacklog(true);
		setAccessReleasePlan(true);
		setAccessRetrospective(true);
		setAccessSprintBacklog(true);
		setAccessSprintPlan(true);
		setAccessTaskBoard(true);
		setAccessUnplanItem(true);
		setAccessEditProject(true);
		setAccessReport(true);
    }

	private void setProductOwnerRole() {
		setAccessProductBacklog(true);
		setAccessReleasePlan(true);
		setAccessRetrospective(true);
		setAccessSprintBacklog(true);
		setAccessSprintPlan(true);
		setAccessTaskBoard(true);
		setAccessUnplanItem(true);
		setAccessEditProject(true);
		setAccessReport(true);
    }
	
	private void setGuestRole() {
		setAccessProductBacklog(false);
		setAccessReleasePlan(false);
		setAccessRetrospective(false);
		setAccessSprintBacklog(false);
		setAccessSprintPlan(false);
		setAccessTaskBoard(false);
		setAccessUnplanItem(false);
		setAccessEditProject(false);
		setAccessReport(false);
    }
	
	public JSONObject toJSON() throws JSONException {
		JSONObject scrumRoleJSON = new JSONObject();
		scrumRoleJSON.put(ScrumRoleEnum.ACCESS_PRODUCT_BACKLOG, mProductBacklog);
		scrumRoleJSON.put(ScrumRoleEnum.ACCESS_RELEASE_PLAN, mReleasePlan);
		scrumRoleJSON.put(ScrumRoleEnum.ACCESS_RETROSPECTIVE, mRetrospective);
		scrumRoleJSON.put(ScrumRoleEnum.ACCESS_SPRINT_BACKLOG, mSprintBacklog);
		scrumRoleJSON.put(ScrumRoleEnum.ACCESS_SPRINT_PLAN, mSprintPlan);
		scrumRoleJSON.put(ScrumRoleEnum.ACCESS_TASKBOARD, mTaskboard);
		scrumRoleJSON.put(ScrumRoleEnum.ACCESS_UNPLAN, mUnplan);
		scrumRoleJSON.put(ScrumRoleEnum.ACCESS_EDIT_PROJECT, mAccessEditProject);
		scrumRoleJSON.put(ScrumRoleEnum.ACCESS_REPORT, mAccessReport);
		return scrumRoleJSON;
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

	public Boolean getAccessUnplanItem() {
		return mUnplan;
	}

	public Boolean getAccessRetrospective() {
		return mRetrospective;
	}

	public Boolean getAccessReport() {
		return mAccessReport;
	}

	public Boolean getAccessEditProject() {
		return mAccessEditProject;
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

	public void setAccessUnplanItem(Boolean permission) {
		mUnplan = permission;
	}

	public void setAccessRetrospective(Boolean permission) {
		mRetrospective = permission;
	}

	public void setAccessReport(Boolean permission) {
		mAccessReport = permission;
	}

	public void setAccessEditProject(Boolean permission) {
		mAccessEditProject = permission;
	}

	public void setisGuest(Boolean permission) {
		mIsGuest = permission;
	}
}