package ntut.csie.ezScrum.issue.internal;


// 儲存issue之間relation的關係
public class IssueRelationship {
	
	private long m_issueID;
	private long r_issueID;
	private String type;
	
	public IssueRelationship(long issueID){
		this.m_issueID = issueID;
		this.r_issueID = 0;
		this.type = "";
	}

	public long getIssueID(){
		return this.m_issueID;
	}
	
	public void setRelationIssueID(long id){
		this.r_issueID = id;
	}
	
	public long getRelationIssueID(){
		return this.r_issueID;
	}	
	
	public void setRelationType(String type){
		this.type = type;
	}
	
	public String getRelationType(){
		return this.type;
	}
}
