package ntut.csie.ezScrum.issue.core;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import ntut.csie.ezScrum.issue.internal.IssueRelationship;
import ntut.csie.ezScrum.issue.internal.IssueTypeField;
import ntut.csie.ezScrum.web.dataObject.AttachFileObject;
import ntut.csie.ezScrum.web.dataObject.HistoryObject;
import ntut.csie.ezScrum.web.dataObject.TagObject;

import org.jdom.Element;

/**
 * 包含一個Issue在ITS中中必要的欄位及內容,因此定義大量的Get及Set
 * 之後若要加入其它ITS時,必需修正其共同性
 * 
 * JCIS -> ITS才會產生的變數為:
 * projectID
 * integrationID
 * builderID
 * 
 * ITS -> JCIS才會產生的變數為:
 * issueID
 * status
 * submittedDate
 * urlLink
 * 
 * 剩下為ITS <-> JCIS會互相共用的
 * 所以在使用IIssue時要注意是從哪邊產生的
 * 
 * @author Silvius
 */
public interface IIssue extends Serializable{	
	public String getCategory();
	public void setCategory(String category);
	public int getIssueType();
	
	public String getReproducibility();
	public void setReproducibility(String reproducibility);
	
	public String getSeverity();
	public void setSeverity(String severity);
	
	public String getPriority();
	public void setPriority(String priority);
	
	public String getSummary();
	public void setSummary(String summary);
	
	public String getDescription();
	public void setDescription(String description);
	
	public String getAdditional();
	public void setAdditional(String additional);
	
	public String getViewStatus();
	public void setViewState(String viewStatus);
	
	public String getAssignto();
	public void setAssignto(String assignto);
	
	/**
	 * PID
	 */
	public String getProjectID();
	/**
	 * PID
	 */
	public void setProjectID(String projectID);
	
		
	public String getIntegrationId();
	public void setIntegrationID(String integrationID);
	
	public String getBuilderID();
	public void setBuilderID(String builderID);
	
	public long getIssueID();
	public void setIssueID(long issueID);
	
	public String getStatus();
	public void setStatus(String status);
	
	public long getSubmittedDate();
	public void setSubmittedDate(long submitted);
	
	public String getIssueLink();
	public void setIssueLink(String issueLink);
	
	public void setIssue(IIssue issue) throws SQLException;
	public String getReporter();
	
	public void setReporter(String m_reporter);
	//新增對變更時間的了解
	public long getWorkingUpdated();
	public Date getLastUpdate();
	public Date getStatusUpdated(int status);
	public Date getStatusUpdated(Date date, int status);
	
	public void setWorkingUpdated(long workingDate);
	public int getStatusValue();
	
	// temp function for ezScrum v1.8
	public void addHistory(HistoryObject history);
	public ArrayList<HistoryObject> getHistories() throws SQLException;
	public void setHistories(ArrayList<HistoryObject> histories);
	
//	public List<IssueRelationship> getRelationships();
	
	public List<IIssueNote> getIssueNotes();
	public void setIssueNotes(List<IIssueNote> notes);
	public void addIssueNote(IIssueNote note);
	public void addIssueNote(String text);
	
	//支援Scrum的欄位
	public String getEstimated();
	public String getRemains();
	public String getValue();
	public String getImportance();
	public String getSprintID();
	public String getReleaseID();
	public String getHowToDemo();
	public String getNotes();
	@Deprecated
	public String getPartners();
//	@Deprecated
//	public List<Long> getChildrenID();
//	@Deprecated
//	public List<Long> getChildrenID(Date date);
	
	
	public void setChildrenId(ArrayList<Long> childrenId);
	public ArrayList<Long> getChildrenId();
	
	public void setParentId(long parentID);
	public long getParentId();
	
	// parentId 好像沒人用它，目前暫時註解待查證
//	public void setParentsId(ArrayList<Long> parentsId);
//	public ArrayList<Long> getParentsId();
	
	public String getActualHour();

	//支援scrum但需要修改的操作
	public void setTagContent(Element history);
	public Element getTagContentRoot();

	public void addTagValue(Element element);
	public String getTagValue(String name, Date date);

	@Deprecated
	public String getTagValue(String name);
	public Map<Date, String> getTagValueList(String name);
	public int getDateStatus(Date date);
	public long getDoneDate();
	public long getCheckOutDate();
	public String getCreateBy();
	public long getAssignedDate();
	public long getCloseDate();
	
	public ArrayList<AttachFileObject> getAttachFiles();
	public void setAttachFiles(ArrayList<AttachFileObject> fileList);
	public void addAttachFile(AttachFileObject attach);
	
	// 對Story的自訂分類標籤
	public List<TagObject> getTags();
	public void setTags(List<TagObject> tags);
	public void addTag(TagObject tag);
	
	public String getProjectName();
	public void setProjectName(String projectName);

	// 設定 Issue 客製化欄位
	// IssueTypeField 存放 ID+名稱+類型+數值
	public void addField(IssueTypeField field);
	public String getFieldValue(String name);
	public IssueTypeField getField(String name);
	public void setFieldValue(int id, String value);
	public void setFieldValue(String name, String value);
	public void setFields(List<IssueTypeField> fields);
	public List<IssueTypeField> getFields();
	// 設定 Issue 客製化 TypeName(類型名稱)
	public void setCategoryName(String name);
	public String getCategoryName();
}
