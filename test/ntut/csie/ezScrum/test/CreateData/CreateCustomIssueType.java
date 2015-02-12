package ntut.csie.ezScrum.test.CreateData;

import java.util.ArrayList;
import java.util.List;
import ntut.csie.ezScrum.issue.sql.service.core.Configuration;
import ntut.csie.ezScrum.service.CustomIssueType;
import ntut.csie.ezScrum.service.IssueBacklog;
import ntut.csie.jcis.resource.core.IProject;

public class CreateCustomIssueType {
	private List<CustomIssueType> mCustomIssueTypes = null;
	private IssueBacklog mIssueBacklog = null;
	private String mProjectName = "";
	private Configuration mConfig = new Configuration();
	
	public CreateCustomIssueType(CreateProject CP, int CreateProject_Index) throws Exception {
		if (CreateProject_Index < CP.getProjectList().size()) {
			mCustomIssueTypes = new ArrayList<CustomIssueType>();
			IProject project = CP.getProjectList().get(CreateProject_Index);
			mProjectName = project.getName();
			mIssueBacklog = new IssueBacklog(project, mConfig.getUserSession());	// 設定 issuebacklog
		}
	}
	
	public void exeTrueType(String TypeName) {
		mCustomIssueTypes.add(mIssueBacklog.addIssueType(TypeName, true));		// 新增 Issue Type
		System.out.println("專案 " + mProjectName + " 新增一筆 issue type : " + TypeName + ", status : true 成功");
	}
	
	public void exeFalseType(String TypeName) {
		mCustomIssueTypes.add(mIssueBacklog.addIssueType(TypeName, false));		// 新增 Issue Type
		System.out.println("專案 " + mProjectName + " 新增一筆 issue type : " + TypeName + ", status : false 成功");
	}
	
	public List<CustomIssueType> getCustomIssueType() {
		return mCustomIssueTypes;
	}
	
	public IssueBacklog getIssueBacklog() {
		return mIssueBacklog;
	}
}
