package ntut.csie.ezScrum.web.helper;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import ntut.csie.ezScrum.issue.core.IIssue;
import ntut.csie.ezScrum.issue.core.IIssueTag;
import ntut.csie.ezScrum.issue.core.ITSEnum;
import ntut.csie.ezScrum.iteration.core.IReleasePlanDesc;
import ntut.csie.ezScrum.iteration.core.ISprintPlanDesc;
import ntut.csie.ezScrum.iteration.core.IStory;
import ntut.csie.ezScrum.iteration.iternal.ReleaseBacklog;
import ntut.csie.ezScrum.iteration.iternal.ReleaseBoard;
import ntut.csie.ezScrum.iteration.iternal.ReleasePlanDesc;
import ntut.csie.ezScrum.pic.core.IUserSession;
import ntut.csie.ezScrum.web.control.ProductBacklogHelper;
import ntut.csie.ezScrum.web.logic.ProductBacklogLogic;
import ntut.csie.ezScrum.web.logic.SprintBacklogLogic;
import ntut.csie.ezScrum.web.mapper.ReleasePlanMapper;
import ntut.csie.ezScrum.web.mapper.SprintPlanMapper;
import ntut.csie.ezScrum.web.support.TranslateSpecialChar;
import ntut.csie.ezScrum.web.support.Translation;
import ntut.csie.jcis.core.util.DateUtil;
import ntut.csie.jcis.resource.core.IProject;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

public class ReleasePlanHelper {
	private ReleasePlanMapper rpMapper;
	private IProject m_project;
	
	public ReleasePlanHelper(IProject project){
		m_project = project;
		rpMapper = new ReleasePlanMapper(project);
	}
	
	// remove later
	public IReleasePlanDesc[] loadReleasePlans(){
		List<IReleasePlanDesc> list = rpMapper.getReleasePlanList();
		list = linkReleasePlanWithSprintList(list);
		return list.toArray(new IReleasePlanDesc[list.size()]);		
	}
	
	public List<IReleasePlanDesc> loadReleasePlansList(){
		List<IReleasePlanDesc> list = rpMapper.getReleasePlanList();
		list = linkReleasePlanWithSprintList(list);
		return list;
	}
	
	public int getLastReleasePlanNumber() {
		int length = rpMapper.getReleasePlanList().size();				// get array's length
		List<IReleasePlanDesc> IRPD = rpMapper.getReleasePlanList();	// get the array data
		
		if (length > 0) {
			return Integer.parseInt(IRPD.get(length-1).getID());
		} else {
			return 0;
		}				
	}
	
	// 連結SprintList動作移到ReleaesPlanHelper, 從Mapper搬過來的
	private List<IReleasePlanDesc> linkReleasePlanWithSprintList(List<IReleasePlanDesc> descList) {
		List<IReleasePlanDesc> newDescList = new LinkedList<IReleasePlanDesc>();
		
		for (int i=0; i<descList.size(); i++)
		{			
			IReleasePlanDesc desc = new ReleasePlanDesc();
		
			desc.setID(descList.get(i).getID());
			desc.setName(descList.get(i).getName());
			desc.setStartDate(descList.get(i).getStartDate());
			desc.setEndDate(descList.get(i).getEndDate());
			desc.setDescription(descList.get(i).getDescription());		
			
			// 自動尋找此 release date 內的 sprint plan
			List<ISprintPlanDesc> ReleaseSprintList = new LinkedList<ISprintPlanDesc>();
			SprintPlanMapper spMapper = new SprintPlanMapper(m_project);
			List<ISprintPlanDesc> AllSprintList = spMapper.getSprintPlanList();
			
			Date releaseStartDate = DateUtil.dayFilter(desc.getStartDate());	// release start date get from XML
			Date releaseEndDate = DateUtil.dayFilter(desc.getEndDate());		// release end date get from XML
			
			for (ISprintPlanDesc sprintDesc : AllSprintList) {
				Date sprintStartDate = DateUtil.dayFilter(sprintDesc.getStartDate());
				Date sprintEndDate = DateUtil.dayFilter(sprintDesc.getEndDate());
				
				// 判斷 sprint plan 日期是否為 release plan 內的日期
				if (sprintStartDate.compareTo(releaseStartDate) >= 0) {
					if (releaseEndDate.compareTo(sprintEndDate) >= 0) {						
						ReleaseSprintList.add(sprintDesc);			// add to the list
					}
				}
			}
			
			desc.setSprintDescList(ReleaseSprintList);
			newDescList.add(desc);
		}
		return newDescList;
	}
	
	public void deleteReleasePlan(String id) {
		rpMapper.deleteReleasePlan(id);
	}
	
	public void editReleasePlan(String ID, String Name, String StartDate, String EndDate, String Description, String action) {
		ReleasePlanDesc desc = new ReleasePlanDesc();
		desc.setID(ID);						// set ID
		desc.setName(Name);					// set Name
		desc.setStartDate(StartDate);		// set Start Date
		desc.setEndDate(EndDate);			// set End Date
		desc.setDescription(Description);	// set Description
				
		if (action.equals("save")) {
			rpMapper.addReleasePlan(desc);
		} else if (action.equals("edit")) {
			rpMapper.updateReleasePlan(desc);
		}
	}

	// return the release plan of releasePlanID
	public IReleasePlanDesc getReleasePlan(String releasePlanID) {
		IReleasePlanDesc[] descs = loadReleasePlans();
		
		for (IReleasePlanDesc desc : descs) {
			if (desc.getID().equals(releasePlanID))
				return desc;
		}
		
		return null;
	}
	
	// return the release plans of releasePlanID' string
	public List<IReleasePlanDesc> getReleasePlans(String releasePlanIDs) {
		String[] plansString = releasePlanIDs.split(",");
		List<IReleasePlanDesc> plans = new ArrayList<IReleasePlanDesc>();
		
		for (String releasePlanID : plansString) {
			plans.add(getReleasePlan(releasePlanID));
		}
		
		return plans;
	}
	
	// return the releaseID which has the sprintID
	public String getReleaseID(String sprintID) {
		String rid = "0";
		IReleasePlanDesc[] plans = loadReleasePlans();
		
		for (IReleasePlanDesc plan : plans) {
			if (plan.getSprintDescList() != null) {
				for (ISprintPlanDesc s_id : plan.getSprintDescList()) {
					if (s_id.getID().equals(sprintID)) {		// 找到此 sprint 所被包含的 release ID
						return plan.getID();
					}
				}
			}
		}		
		
		return rid;
	}

	/*
	 * from ShowReleasePlan2Action
	 */
	
	public List<IReleasePlanDesc> sortStartDate(List<IReleasePlanDesc> releaseDescs){	
		List<IReleasePlanDesc> ListReleaseDescs = new ArrayList<IReleasePlanDesc>();
		// ListReleaseDescs 依照 StartDate 排序
		for (IReleasePlanDesc desc : releaseDescs) {
			Date addDate = DateUtil.dayFilter(desc.getStartDate());			// 要新增的 Date
			
			if (ListReleaseDescs.size() > 0) {
				int index = 0;
				for (index=0 ; index<ListReleaseDescs.size() ; index++) {
					IReleasePlanDesc Desc = ListReleaseDescs.get(index);		// 目前要被比對的 relase
					Date cmpDate = DateUtil.dayFilter(Desc.getStartDate());		// 要被比對的 Date
					if ( addDate.compareTo(cmpDate) < 0 ) {
						break;
					}
				}
				ListReleaseDescs.add(index, desc);
			} else {
				ListReleaseDescs.add(desc);
			}
		}
		return ListReleaseDescs;
	}
	
	public String setJSon(List<IReleasePlanDesc> ListReleaseDescs, SprintPlanHelper SPhelper){
		TranslateSpecialChar tsc = new TranslateSpecialChar();
		
		String tree = "";
		tree += "[";
		int i = 0;
		for(IReleasePlanDesc des: ListReleaseDescs){
			if(i==0)
				tree+="{";
			else
				tree+=",{";
			tree+="Type:\'Release\',";
			tree+="ID:\'"+des.getID()+"\',";
			tree+="Name:\'"+tsc.TranslateJSONChar(des.getName())+"\',";
			tree+="StartDate:\'"+des.getStartDate()+"\',";
			tree+="EndDate:\'"+des.getEndDate()+"\',";
			tree+="Description:\'"+ tsc.TranslateJSONChar(des.getDescription()) +"\',";
			
			if(des.getSprintDescList()!=null&&des.getSprintDescList().size()!=0){
				tree+= "expanded: true,";
				tree+="iconCls:\'task-folder\',";

				tree+= "children:[";
				tree+= this.setSprintToJSon(des, SPhelper);
				tree+= "]";
			}
			else{
				tree+="leaf: true";
			}
			tree+= "}";
			i++;
		}
		//[{task:\'Project: Shopping\',duration:13.25,user:\'Tommy Maintz\',leaf: true}]
	
		tree += "]";
		return tree;
	}
	/**
	 *  from AjaxGetReleasePlanAction,
	 *  將release讀出並列成list再轉成JSON
	 */
    public String setReleaseListToJSon (List<IReleasePlanDesc> ListReleaseDescs) {
    	JSONObject releaseObject = new JSONObject();
    	JSONArray releaseplanlist = new JSONArray();
    	try {
			for (IReleasePlanDesc plan : ListReleaseDescs) {
				JSONObject releaseplan = new JSONObject();
				releaseplan.put("ID", plan.getID());
		        releaseplan.put("Name", plan.getName());
				releaseplanlist.put(releaseplan);
			}
			releaseObject.put("Releases", releaseplanlist);
    	} catch (JSONException e) {
            e.printStackTrace();
        }
		
		return releaseObject.toString();	
	}
    
    /**
     *  from AjaxGetVelocityAction,
     *  將被選到的release plans拿出他們的sprint point並算出velocity,算出平均值再轉成JSON
     */
    public String setSprintVelocityToJSon(List<IReleasePlanDesc> ListReleaseDescs, SprintBacklogHelper SBhelper) {
    	JSONObject velocityObject = new JSONObject();
    	JSONArray sprints = new JSONArray();
    	double totalVelocity = 0;
    	int sprintCount = 0; // 計算被選的release內的sprint總數
    	try {
	    	for (IReleasePlanDesc release : ListReleaseDescs) {
	    		for (ISprintPlanDesc sprint : release.getSprintDescList()) {
	    			JSONObject sprintplan = new JSONObject();
	    			sprintplan.put("ID", sprint.getID());
	    			sprintplan.put("Name", "Sprint" + sprint.getID());
	    			int sprintVelocity = calculateStoryDonePoint(sprint.getID(), SBhelper);
	    			sprintplan.put("Velocity", sprintVelocity);
	    			totalVelocity += sprintVelocity;
	    			sprints.put(sprintplan);
	    			sprintCount++;
	    		}
	    	}
	    	velocityObject.put("Sprints", sprints);
	    	velocityObject.put("Average", totalVelocity/sprintCount);
    	} catch (JSONException e) {
            e.printStackTrace();
        }
		return velocityObject.toString();
    }
    
    /**
     * form AjaxGetStoryCountAction
     * 將被選到的release plans將所含的sprint中的story point算出總和,再轉成JSON
     */
    public String setStoryCountToJSon(List<IReleasePlanDesc> ListReleaseDescs, SprintBacklogHelper SBhelper) {
    	JSONObject velocityObject = new JSONObject();
    	JSONArray sprints = new JSONArray();
    	int TotalStoryCount = 0;
    	int StoryDoneCount = 0;
    	int sprintCount = 0; // 計算被選的release內的sprint總數
    	try {
    		for (IReleasePlanDesc release : ListReleaseDescs) {
	    		for (ISprintPlanDesc sprint : release.getSprintDescList()) {
	    			JSONObject sprintplan = new JSONObject();
	    			sprintplan.put("ID", sprint.getID());
	    			sprintplan.put("Name", "Sprint" + sprint.getID());
	    			TotalStoryCount += getStoryCount(sprint.getID(), SBhelper);
	    			sprintplan.put("StoryDoneCount", getStoryDoneCount(sprint.getID(), SBhelper));
	    			sprints.put(sprintplan);
	    			sprintCount++;
	    		}
	    	}
	    	velocityObject.put("Sprints", sprints);
	    	velocityObject.put("TotalStoryCount", TotalStoryCount);
    	} catch (JSONException e) {
            e.printStackTrace();
    	}
    	return null;
    }
    
    // 計算此sprint內的story done的story point
    private int calculateStoryDonePoint(String sprintID, SprintBacklogHelper SBhelper) {
    	IIssue[] stories = SBhelper.getStoryInSprint(sprintID);
    	int storypoint = 0;
    	for (IIssue story : stories) {
    		if (story.getStatus() == ITSEnum.S_CLOSED_STATUS) {
    			storypoint += Integer.valueOf(story.getEstimated());
    		}
    	}
    	return storypoint;
    }
    
    // 計算sprint的story總數
    private int getStoryCount(String sprintID, SprintBacklogHelper SBhelper) {
    	IIssue[] stories = SBhelper.getStoryInSprint(sprintID);
    	int StoryCount = stories.length;
    	return StoryCount;
    }
    
    private int getStoryDoneCount(String sprintID, SprintBacklogHelper SBhelper) {
    	IIssue[] stories = SBhelper.getStoryInSprint(sprintID);
    	int storydonecount = 0;
		for (IIssue story : stories) {
    		if (story.getStatus() == ITSEnum.S_CLOSED_STATUS) {
    			storydonecount++;
    		}
    	}
		return storydonecount;
    }
	
	//透過release des將sprint的資訊寫成JSon
	private String setSprintToJSon (IReleasePlanDesc IRDesc, SprintPlanHelper SPhelper){
		TranslateSpecialChar tsc = new TranslateSpecialChar();
		String sprintTree="";
		if (IRDesc.getSprintDescList() != null) {				// 有 sprint 資訊，則抓取 sprint 的 xml 資料
			int i=0;
			// 將資訊設定成 JSon 輸出格式
			for (ISprintPlanDesc desc : IRDesc.getSprintDescList()) {
				if(i==0)
					sprintTree+="{";
				else
					sprintTree+=",{";
				sprintTree+="Type:\'Sprint\',";
				sprintTree+="ID:\'"+desc.getID()+"\',";
				sprintTree+="Name:\'"+tsc.TranslateJSONChar(desc.getGoal())+"\',";
				sprintTree+="StartDate:\'"+desc.getStartDate()+"\',";
				sprintTree+="EndDate:\'"+desc.getEndDate()+"\',";
				sprintTree+="Interval:\'"+desc.getInterval()+"\',";
				sprintTree+="Description:\' \',";
				sprintTree+="iconCls:\'task\',";
				sprintTree+="leaf: true";
				sprintTree+= "}";
				i++;
			}
		}
		return sprintTree;
	}
	
	/**
	 * from AjaxShowStoryFromReleaseAction
	 */
	
	public StringBuilder showStoryFromReleae(IProject project, String R_ID, IStory[] storyList) {
		IReleasePlanDesc plan = this.getReleasePlan(R_ID);
		
		ReleaseBacklog releaseBacklog;
		try {
			releaseBacklog = new ReleaseBacklog(project, plan, storyList);
		}
		catch (Exception e) {
			// TODO: handle exception
			releaseBacklog = null;
		}
		
		if (R_ID != null) {			
			IIssue[] stories = releaseBacklog.getStory();
			stories = this.sortStory(stories);
			
			// write stories to XML format
			StringBuilder sb = new StringBuilder();
			sb.append("<ExistingStories>");			
			for(int i = 0; i < stories.length; i++)
			{
				String releaseId = stories[i].getReleaseID();
				if(releaseId.equals("") || releaseId.equals("0") || releaseId.equals("-1"))
					releaseId = "None";
				
				String sprintId = stories[i].getSprintID();
				if(sprintId.equals("") || sprintId.equals("0") || sprintId.equals("-1"))
					sprintId = "None";
				sb.append("<Story>");
				sb.append("<Id>" + stories[i].getIssueID() + "</Id>");
				sb.append("<Link>" + this.replaceStr(stories[i].getIssueLink()) + "</Link>");
				sb.append("<Name>" + this.replaceStr(stories[i].getSummary())+ "</Name>");
				sb.append("<Value>" + stories[i].getValue()+"</Value>");
				sb.append("<Importance>" + stories[i].getImportance() + "</Importance>");
				sb.append("<Estimation>" + stories[i].getEstimated() + "</Estimation>");
				sb.append("<Status>" + stories[i].getStatus() + "</Status>");
				sb.append("<Notes>" + this.replaceStr(stories[i].getNotes()) + "</Notes>");
				sb.append("<HowToDemo>" + this.replaceStr(stories[i].getHowToDemo()) + "</HowToDemo>");
				sb.append("<Release>" + releaseId + "</Release>");
				sb.append("<Sprint>" + sprintId + "</Sprint>");
				sb.append("<Tag>" + this.replaceStr(this.joinTagOnStory(stories[i].getTag(), ",")) + "</Tag>");
				sb.append("</Story>");
			}
			sb.append("</ExistingStories>");
			
			return sb;
		} else {
			return null;
		}		
	}

	// sort story information by importance
	private IIssue[] sortStory(IIssue[] issues) {
		List<IIssue> list = new ArrayList();
	
		for (IIssue issue : issues) {
			int index = 0;
			for (index=0 ; index<list.size() ; index++) {
				if ( Integer.parseInt(issue.getImportance()) > Integer.parseInt(list.get(index).getImportance()) ) {
					break;
				}
			}
			list.add(index, issue);
		}
	
		return list.toArray(new IIssue[list.size()]);
	}
	
	private String joinTagOnStory(List<IIssueTag> tags, String delimiter)
	{
	    if (tags.isEmpty())
	    	return "";
	 
	    StringBuilder sb = new StringBuilder();
	 
	    for (IIssueTag x : tags)
	    	sb.append(x.getTagName() + delimiter);
	 
	    sb.delete(sb.length()-delimiter.length(), sb.length());
	 
	    return sb.toString();
	}

	private String replaceStr(String str) {
		if (str != null) {
			if (str.contains("&")) {
				str = str.replaceAll("&", "&amp;");
			}
			
			if (str.contains("\"")) {
				str = str.replaceAll("\"", "&quot;");
			}
			
			if (str.contains("<")) {
				str = str.replaceAll("<", "&lt;");
			}
			
			if (str.contains(">")) {
				str = str.replaceAll(">", "&gt;");
			}
		}
		
		return str;
	}	

	/*
	 * from AjaxGetNewReleaseIDAction
	 */
	
	public StringBuilder getNewReleaseId() {
		int id = this.getLastReleasePlanNumber() + 1;	// 依照目前最近ID count 累加

		StringBuilder sb = new StringBuilder();
		sb.append("<Root><Release>");
		sb.append("<ID>" + id + "</ID>");
		sb.append("</Release></Root>");
		
		return sb;		
	}
	
	/*
	 * from CheckReleaseDateAction
	 */
	
	public StringBuilder checkReleaseDate(String releaseId, String startDate, String endDate, String action) {
		List<IReleasePlanDesc> rpList = this.loadReleasePlansList();
		String result = "legal";
		
		for (IReleasePlanDesc rp : rpList) {
			if (action.equals("edit")
					&& releaseId.equals(rp.getID())) {// 不與自己比較
				continue;
			}
			// check日期的頭尾是否有在各個RP日期範圍內
			if ((startDate.compareTo(rp.getStartDate()) >= 0 
					&& startDate.compareTo(rp.getEndDate()) <= 0)
					|| (endDate.compareTo(rp.getStartDate()) >= 0 
					&& endDate.compareTo(rp.getEndDate()) <= 0)) {
				result = "illegal";
				break;
			}
		}
		
		return new StringBuilder(result);		
	}
	
	/*
	 * from SaveReleasePlanAction
	 */	
	
	//加入 Release 日期範圍內 Sprint 底下的 Story
	public void addReleaseSprintStory(IProject project, IUserSession session, String ID, List<ISprintPlanDesc> oldSprintList, IReleasePlanDesc reDesc){
		List<ISprintPlanDesc> newSprintList =  reDesc.getSprintDescList();
		
//		ProductBacklogHelper pbHelper = new ProductBacklogHelper(project, session);
		ProductBacklogLogic productBacklogLogic = new ProductBacklogLogic(session, project);
		
		ArrayList<Long> storyList;
		
		//For deleting old sprint. Taking original SprintList to compare with new SprintList.
		if(oldSprintList != null){	
			storyList = this.compareReleaseSprint(oldSprintList, newSprintList, project, session);
			for(long story : storyList) {
				productBacklogLogic.removeReleaseTagFromIssue(String.valueOf(story));
			}
			storyList.clear();
		}

		//For adding new sprint. Taking new SprintList to compare with original SprintList.
		storyList = this.compareReleaseSprint(newSprintList, oldSprintList, project, session);
		productBacklogLogic.addReleaseTagToIssue(storyList, ID);

	}
	
	//SprintList，舊日期的list 與 新日期的list做比對
	private ArrayList<Long> compareReleaseSprint(List<ISprintPlanDesc> sprintList1, List<ISprintPlanDesc> sprintList2,
											   IProject project, IUserSession session) {
//		SprintBacklogMapper sprintBacklog;
		SprintBacklogLogic sprintBacklogLogic;
		ArrayList<Long> storyList = new ArrayList<Long>();
		boolean deleteOrAdd = true;
		
		if(sprintList2 != null) {
			for(ISprintPlanDesc list1 : sprintList1) {
				for(ISprintPlanDesc list2 : sprintList2) {
					if(list1.getID().equals(list2.getID())) {//Sprint still exists.
						deleteOrAdd = false;
						break;
					}
				}
				if(deleteOrAdd == true) {//Finding sprint not existing in list2.
//					sprintBacklog = new SprintBacklogMapper(project, session, Integer.parseInt(list1.getID()));
//					List<IIssue> stories = sprintBacklog.getStories();
					sprintBacklogLogic =  new SprintBacklogLogic(project, session, list1.getID());
					List<IIssue> stories = sprintBacklogLogic.getStories();
					for(IIssue story : stories) {
						storyList.add(story.getIssueID());
					}
				}
				deleteOrAdd = true;//For next sprint.
			}
		}
		else { // For creating a new sprint
			for(ISprintPlanDesc list1 : sprintList1) {
//				sprintBacklog = new SprintBacklogMapper(project, session, Integer.parseInt(list1.getID()));
//				List<IIssue> stories = sprintBacklog.getStories();
				sprintBacklogLogic = new SprintBacklogLogic(project, session, list1.getID());
				List<IIssue> stories = sprintBacklogLogic.getStories();
				for(IIssue story : stories) {
					storyList.add(story.getIssueID());
				}
			}
		}
		return storyList;
	}

	/*
	 * from GetReleaseBurndownChartDataAction
	 */
	
	public StringBuilder getReleaseBurndownChartData(IProject project, IUserSession session, String releaseId) {
		ProductBacklogHelper pbHelper = new ProductBacklogHelper(project, session);
		IReleasePlanDesc plan = this.getReleasePlan(releaseId);
		
		ReleaseBacklog releaseBacklog = null;
		StringBuilder result = new StringBuilder("");
		try {
			releaseBacklog = new ReleaseBacklog(project, plan, pbHelper.getStoriesByRelease(plan));
			ReleaseBoard board = new ReleaseBoard(releaseBacklog);
			Translation tr = new Translation();
			result.append(tr.translateBurndownChartDataToJson(board.getStoryIdealPointMap(), board.getStoryRealPointMap()));
		} catch (Exception e) {
			releaseBacklog = null;
			result.append("{success: \"false\"}");
		}
		
		return result;		
	}
	
}
