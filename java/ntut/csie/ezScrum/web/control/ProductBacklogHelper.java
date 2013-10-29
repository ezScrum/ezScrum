package ntut.csie.ezScrum.web.control;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ntut.csie.ezScrum.issue.core.IIssue;
import ntut.csie.ezScrum.issue.core.IIssueHistory;
import ntut.csie.ezScrum.iteration.core.IReleasePlanDesc;
import ntut.csie.ezScrum.iteration.core.IStory;
import ntut.csie.ezScrum.iteration.core.ScrumEnum;
import ntut.csie.ezScrum.pic.core.IUserSession;
import ntut.csie.ezScrum.web.logic.ProductBacklogLogic;
import ntut.csie.ezScrum.web.mapper.ProductBacklogMapper;
import ntut.csie.jcis.core.util.DateUtil;
import ntut.csie.jcis.resource.core.IProject;

import org.jdom.Element;

public class ProductBacklogHelper {
//	private ProductBacklog m_backlog;
	private ProductBacklogLogic productBacklogLogic;
	private ProductBacklogMapper productBacklogMapper;
	
	public ProductBacklogHelper(IProject project, IUserSession userSession) {
		productBacklogLogic = new ProductBacklogLogic(userSession, project);
		productBacklogMapper = new ProductBacklogMapper(project, userSession);
	}

	/**
	 * 取得 story 或 task
	 * @param id
	 * @return
	 */
	public IIssue getIssue(long id) {
		return this.productBacklogMapper.getIssue(id);
	}
	// 秀出此 release 加入的 stories，以及此 release 的 sprint 包含的 stories
	public IStory[] getStoriesByRelease(IReleasePlanDesc desc) {
		try {
			IStory[] stories = this.productBacklogLogic.getStoriesByRelease(desc);
			
			List<IStory> list = new ArrayList<IStory>();

			for (IStory story : stories) {
				String R_id = story.getReleaseID();

				if (R_id != null) {
					if (R_id.equals(desc.getID())) { // story 有此 release ID
														// 的資訊，則加入為 release
														// backlog
						list.add(story);
					}
				}
			}

			return list.toArray(new IStory[list.size()]);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new IStory[0];
	}
	
//	// release plan select stories 
//	// 2010.06.02 by taoyu modify
//	public IStory[] getAddableStories() {
//		IStory[] issues = this.productBacklogLogic.getUnclosedIssues(ScrumEnum.STORY_ISSUE_TYPE);
//		
//		// 不能直接使用Arrays.asList,因為沒有實作到remove,所以必須要使用Arrays
//		List<IIssue> list = new ArrayList<IIssue>();
//		
//		for (IStory issue : issues) {
//			String story_SID = issue.getSprintID();
//			String story_RID = issue.getReleaseID();
//			
//			// 此 story ID 有包含於 sprint ID，則不列入 list
//			if ( (story_SID!=null) && (Integer.parseInt(story_SID)>0) ) {
//				continue;
//			}
//			
//			// 此 story ID 有 release ID，則不列入 list
//			if ( (story_RID!=null) && (Integer.parseInt(story_RID)>0) ) {
//				continue;
//			}
//
//			list.add(issue);
//		}
//		
//		return list.toArray(new IStory[list.size()]);
//	}
//	
//	// sprint backlog select stories
//	// 2009.12.18 by chiachi
//	public IStory[] getAddableStories(String sprintID, String releaseID) {
//		IStory[] issues = this.productBacklogLogic.getUnclosedIssues(ScrumEnum.STORY_ISSUE_TYPE);
//		
//		// 不能直接使用Arrays.asList,因為沒有實作到remove,所以必須要使用Arrays
//		List<IIssue> list = new ArrayList<IIssue>();
//		
//		for (IStory issue : issues) {
//			String story_SID = issue.getSprintID();
//			String story_RID = issue.getReleaseID();
//			
//			// 此 story 有包含 sprint ID，則不列入 list
//			if ( (story_SID!=null) && (Integer.parseInt(story_SID)>0) ) {
//				continue;
//			}
//			
//			// 此 story 有 包含非本release ID，，則不列入 list
//			if ( (story_RID!=null) && (Integer.parseInt(story_RID)>0) ) {
//				if (!story_RID.equals(releaseID))
//					continue;				
//			}
//			
//			list.add(issue);
//		}
//		
//		return list.toArray(new IStory[list.size()]);
//	}

	public IIssue[] getAddableTasks() {
		IIssue[] issues = this.productBacklogMapper.getIssues(ScrumEnum.TASK_ISSUE_TYPE);

		// 不能直接使用Arrays.asList,因為沒有實作到remove,所以必需要使用ArrayList
		List<IIssue> list = new ArrayList<IIssue>();
		list.addAll(Arrays.asList(issues));
		for (int i = list.size() - 1; i >= 0; i--) {
			IIssue issue = list.get(i);
			List<Long> parentsID = issue.getParentsID();
			if (parentsID.size() > 0)
				list.remove(i);
		}
		return list.toArray(new IIssue[list.size()]);
	}

	// remove <Release/> && <Iteration/> tag Info.
	// become to <Release>0</Release> && <Iteration>0</Iteration>
	public void removeReleaseSprint(String issueID) {
//		IIssue issue = m_backlog.getIssue(Integer.parseInt(issueID));
		IIssue issue = this.productBacklogMapper.getIssue(Integer.parseInt(issueID));

		// history node
		Element history = new Element(ScrumEnum.HISTORY_TAG);

		Date current = new Date();
		history.setAttribute(ScrumEnum.ID_HISTORY_ATTR, DateUtil.format(
				current, DateUtil._16DIGIT_DATE_TIME_2));

		// release node
		Element release = new Element(ScrumEnum.RELEASE_TAG);
		release.setText(ScrumEnum.DIGITAL_BLANK_VALUE);
		history.addContent(release);

		// iteration node
		Element iteration = new Element(ScrumEnum.SPRINT_ID);
		iteration.setText(ScrumEnum.DIGITAL_BLANK_VALUE);
		history.addContent(iteration);

		issue.addTagValue(history);

		// 最後將修改的結果更新至DB
		this.productBacklogMapper.updateIssueValue(issue);
		
		// 將Stroy與Release對應的關係從StoryRelationTable移除
		this.productBacklogMapper.updateStoryRelation(issueID, "-1", ScrumEnum.DIGITAL_BLANK_VALUE, null, null, current);
	}
	
	public void updateHistoryModifiedDate(long issueID, long historyID, Date date) {
		IIssue issue = this.getIssue(issueID);
		for (IIssueHistory history : issue.getIssueHistories()) {
			if (history.getHistoryID() == historyID) {
				String current = DateUtil.format(new Date(history
						.getModifyDate()), DateUtil._16DIGIT_DATE_TIME_MYSQL);
				String modify = DateUtil.format(date,
						DateUtil._16DIGIT_DATE_TIME_MYSQL);
				if (current.equals(modify))
					break;
				this.productBacklogMapper.updateHistoryModifiedDate(issueID, historyID, date);
				break;

			}
		}
	}

	// 透過map得到所有sprint的stories
	public Map<String, List<IIssue>> getSprintHashMap() {
		IStory[] stories;
		Map<String, List<IIssue>> map = new HashMap<String, List<IIssue>>();
		try {
			stories = this.productBacklogLogic.getStories();
			for (IStory story : stories) {
				String iteration = story.getSprintID();
				if (map.get(iteration) == null) {
					List<IIssue> list = new ArrayList<IIssue>();
					list.add(story);
					map.put(iteration, list);
				} else {
					List<IIssue> list = map.get(iteration);
					list.add(story);
				}
			}
			return map;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public void addAttachFile(long issueID, String targetPath) {
		this.productBacklogMapper.addAttachFile(issueID, targetPath);
	}

	public void deleteAttachFile(long fileID) {
		this.productBacklogMapper.deleteAttachFile(fileID);
	}
	
	public File getAttachFile(String fileID) {
		return this.productBacklogMapper.getAttachfile(fileID);	
	}
	
	public File getAttachFileByName(String fileName) {
		return this.productBacklogMapper.getAttachfileByName(fileName);	
	}
}