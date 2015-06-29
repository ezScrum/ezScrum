package ntut.csie.ezScrum.issue.sql.service.core;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ntut.csie.ezScrum.issue.core.IIssue;
import ntut.csie.ezScrum.issue.core.IIssueNote;
import ntut.csie.ezScrum.iteration.core.IStory;
import ntut.csie.ezScrum.web.dataInfo.AttachFileInfo;
import ntut.csie.ezScrum.web.dataObject.AttachFileObject;
import ntut.csie.ezScrum.web.dataObject.HistoryObject;
import ntut.csie.ezScrum.web.dataObject.TagObject;

public interface IITSService {
	final static public String JCIS_TAG = "JCIS"; 
	final public static String TAG_UPDATE = "TagUpdate";
	final public static String TAG_ADD = "TagADD";
	
	public void openConnect();
	public void closeConnect();
	/**
	 * 新增一筆Issue
	 * @author Silvius
	 * @param issue
	 * @return 回傳在ITS上新增Issue的id,新增失敗則回傳-1
	 */
	public long newIssue(IIssue issue);
	/**
	 * 利用Project Name來取得Issue的列表
	 * 要注意Issue中只包含在ITS上有的資訊,不包含在JCIS上的資訊,ex:IntegrationID
	 * @author Silvius
	 * @param ITS上的專案名稱
	 * @return IIssue(JCIS與ITS溝通的格式)
	 */
	public IIssue[] getIssues(String projectName);
	public IIssue[] getIssues(String projectName,String category) throws SQLException;
	public IIssue[] getIssues(String projectName,String category,String releaseID,String sprintID,Date startDate,Date endDate);
	public IIssue[] getIssues(String projectName,String category,String releaseID,String sprintID,Date date);
	/**
	 * 利用指定的IssueID來取得Issue
	 * 要注意Issue中只包含在ITS上有的資訊,不包含在JCIS上的資訊,ex:IntegrationID
	 * @author Silvius
	 * @param 指定Issue的ID
	 * @return IIssue(JCIS與ITS溝通的格式)
	 */
	public IIssue getIssue(long issueID);
	
//	/**
//	 * 取得mantis上所有的專案
//	 * @author Silvius
//	 * @return IITSProject(JCIS與ITS溝通的格式)
//	 */
//	public IITSProject[] getProjects();
	
//	/**
//	 * @author Silvius
//	 * @param ITS上的專案名稱
//	 * @return IITSProject(JCIS與ITS溝通的格式)
//	 */
//	public IITSProject getProject(String projectName);
	public String[] getCategories(String projectName);
	public String[] getActors(String projectName, int accessLevel);	
	public String getServiceID();
	public int count(String projectName, String type, Date date);
	@Deprecated
	public void updateBugNote(IIssue issue);
	public void updateIssueNote(IIssue issue, IIssueNote note);
	public void removeRelationship(long sourceID, long targetID, int type);
	public void addRelationship(long sourceID, long targetID, int type, Date date);
	public void updateHandler(IIssue issue, String actor, Date modifyDate);
	public void changeStatusToClosed(long issueID, int resolution,
			String bugNote, Date closeDate);
	public void reopenStatusToAssigned(long issueID,String name, String bugNote, Date closeDate);
	public void resetStatusToNew(long issueID,String name, String bugNote, Date closeDate);
	public void insertBugNote(long issueID, String note);
	public void updateIssueContent(IIssue modifiedIssue);
	public void removeIssue(String ID);
	public void updateName(IIssue task, String name, Date modifyDate);
	//刪除story
	public void deleteStory(String ID);
	//刪除 task
	public void deleteTask(long taskID);
	// 刪除 story 和 task 的關係
	public void deleteRelationship(long storyID, long taskID);
	//新增使用者
	public void addUser(String name,String password,String email, String realName,String access_Level,String cookie_string,String createDate,String lastVisitDate) throws Exception;
	//新增使用者跟Project之間的關係
	public void addUserProjectRelation(String projectName,String name,String access_Level)throws Exception;
	//刪除使用者
	public void deleteUser(String userName) throws Exception;
	//刪除使用者跟Project之間的關係
	public void deleteUserProjectRelation(String userName,String projectName) throws Exception;
	//刪除Project所有屬於access_level的使用者
	public void deleteUserProjectRelationByAccessLevel(String projectName,String access_level)throws Exception;
	//回傳是否User和任何一個Project有關聯
	public boolean isUserHasRelationByAnyProject(String userName)throws Exception;
	//更新user的資料
	public void updateUserProfile(String userID,String realName,String password,String email,String enable) throws Exception;
	//是否存在這個使用者
	public boolean existUser(String userID);
	//上傳attach file
	public long addAttachFile(AttachFileInfo attachFileInfo);
	//刪除attach file
	public void deleteAttachFile(long fileId);
	//取得attach file
	public AttachFileObject getAttachFile(long fileId);
	//新增自訂分類標籤
	public long addNewTag(String name, String projectName);
	// 刪除自訂分類標籤
	public void deleteTag(long id, String projectName);
	// 取得自訂分類標籤列表
	public ArrayList<TagObject> getTagList(String projectName);
	// 對Story設定自訂分類標籤
	public void addStoryTag(String storyID, long tagID);
	// 移除Story的自訂分類標籤
	public void removeStoryTag(String storyID, long tagID);
	// 新增一筆 project 同時也新增到 mantis 上
	public void createProject(String ProjectName) throws Exception;
	public void testConnect() throws Exception;
	public List<IStory> getStorys(String name);
	
	// 建立Story與Sprint Story與Project之間的關聯關係
	public void updateStoryRelationTable(long storyId, String projectId, String releaseID,String sprintID,String estimation,String importance,Date date);
	public void updateTag(long tagId, String tagName, String projectName);
	public boolean isTagExist(String name, String projectName);
	public TagObject getTagByName(String name,String projectName);
}
