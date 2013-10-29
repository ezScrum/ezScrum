package ntut.csie.ezScrum.test.CreateData;

import java.util.ArrayList;
import java.util.List;

import ntut.csie.ezScrum.service.CustomIssueType;
import ntut.csie.ezScrum.service.IssueBacklog;
import ntut.csie.jcis.resource.core.IProject;

public class CreateCustomIssueType {
	private List<CustomIssueType> CustomIssueType = null;
	private IssueBacklog IB = null;
	private String ProjectName = "";
	private ezScrumInfoConfig config = new ezScrumInfoConfig();
	
	public CreateCustomIssueType(CreateProject CP, int CreateProject_Index) throws Exception {
		if (CreateProject_Index < CP.getProjectList().size()) {
			this.CustomIssueType = new ArrayList<CustomIssueType>();
			
			IProject project = CP.getProjectList().get(CreateProject_Index);
			this.ProjectName = project.getName();
			this.IB = new IssueBacklog(project, config.getUserSession());	// 設定 issuebacklog
		}
	}
	
	public void exeTrueType(String TypeName) {
		this.CustomIssueType.add(this.IB.addIssueType(TypeName, true));		// 新增 Issue Type
		System.out.println("專案 " + this.ProjectName + " 新增一筆 issue type : " + TypeName + ", status : true 成功");
	}
	
	public void exeFalseType(String TypeName) {
		this.CustomIssueType.add(this.IB.addIssueType(TypeName, false));		// 新增 Issue Type
		System.out.println("專案 " + this.ProjectName + " 新增一筆 issue type : " + TypeName + ", status : false 成功");
	}
	
	public List<CustomIssueType> getCustomIssueType() {
		return this.CustomIssueType;
	}
	
	public IssueBacklog getIssueBacklog() {
		return this.IB;
	}
}
