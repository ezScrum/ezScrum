package ntut.csie.ezScrum.web.helper;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import ntut.csie.ezScrum.iteration.core.IReleasePlanDesc;
import ntut.csie.ezScrum.iteration.core.ISprintPlanDesc;
import ntut.csie.ezScrum.iteration.iternal.ReleaseBacklog;
import ntut.csie.ezScrum.iteration.iternal.ReleaseBoard;
import ntut.csie.ezScrum.iteration.iternal.ReleasePlanDesc;
import ntut.csie.ezScrum.pic.core.IUserSession;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.dataObject.StoryObject;
import ntut.csie.ezScrum.web.dataObject.TagObject;
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

import edu.emory.mathcs.backport.java.util.Collections;

public class ReleasePlanHelper {
	private ReleasePlanMapper rpMapper;
	private ProjectObject mProject;
	
	public ReleasePlanHelper(ProjectObject project){
		mProject = project;
		rpMapper = new ReleasePlanMapper(mProject);
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
			SprintPlanMapper spMapper = new SprintPlanMapper(mProject);
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
	public List<IReleasePlanDesc> getReleasePlansByIDs(String releasePlanIDs) {
		List<IReleasePlanDesc> plans = new ArrayList<IReleasePlanDesc>();
		if (releasePlanIDs.length() == 0) {
			return plans;
		}
		String[] plansString = releasePlanIDs.split(",");
		for (String releasePlanID : plansString) {
			plans.add(getReleasePlan(releasePlanID));
		}
		return plans;
	}
	
	// return the releaseID which has the sprintID
	public String getReleaseID(long sprintId) {
		String rid = "0";
		IReleasePlanDesc[] plans = loadReleasePlans();
		
		for (IReleasePlanDesc plan : plans) {
			if (plan.getSprintDescList() != null) {
				for (ISprintPlanDesc s_id : plan.getSprintDescList()) {
					// 找到此 sprint 所被包含的 release ID
					if (s_id.getID().equals(String.valueOf(sprintId))) {
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
    public String getSprintVelocityToJSon(List<IReleasePlanDesc> ListReleaseDescs, SprintBacklogHelper SBhelper) {
    	JSONObject velocityobject = new JSONObject();
    	JSONArray sprints = new JSONArray();
    	HashMap<String, Integer> storyinfo;
    	double totalvelocity = 0;
    	int sprintcount = 0; // 計算被選的release內的sprint總數
    	try {
	    	for (IReleasePlanDesc release : ListReleaseDescs) {
	    		if (release == null)
	    			break;
	    		for (ISprintPlanDesc sprint : release.getSprintDescList()) {
	    			JSONObject sprintplan = new JSONObject();
	    			sprintplan.put("ID", sprint.getID());
	    			sprintplan.put("Name", "Sprint" + sprint.getID());
	    			storyinfo = getStoryInfo(sprint.getID(), SBhelper);
	    			sprintplan.put("Velocity", storyinfo.get("StoryPoint"));
	    			sprints.put(sprintplan);
	    			totalvelocity += storyinfo.get("StoryPoint");
					sprintcount++;
	    		}
	    	}
	    	velocityobject.put("Sprints", sprints);
	    	if (sprintcount != 0)
	    		velocityobject.put("Average", totalvelocity/sprintcount);
	    	else
	    		velocityobject.put("Average", "");
    	} catch (JSONException e) {
            e.printStackTrace();
        }
		return velocityobject.toString();
    }
    
    /**
     * form AjaxGetStoryCountAction
     * 將被選到的release plans將所含的sprint中的story point算出總和,再轉成JSON
     */
    public String getStoryCountChartJSon(List<IReleasePlanDesc> ListReleaseDescs, SprintBacklogHelper SBhelper) {
    	JSONObject storycountobject = new JSONObject();
    	JSONArray sprints = new JSONArray();
    	HashMap<String, Integer> storyinfo;
    	int totalstorycount = 0;
    	int sprintcount = 0; // 計算被選的release內的sprint總數
    	try {
    		ArrayList<ISprintPlanDesc> allSprints = new ArrayList<ISprintPlanDesc>();
    		for (IReleasePlanDesc release : ListReleaseDescs) {
    			if (release == null)
    				break;
	    		for (ISprintPlanDesc sprint : release.getSprintDescList()) {
	    			allSprints.add(sprint);
	    		}
	    	}
    		
    		Collections.sort(allSprints, new Comparator<ISprintPlanDesc>() {
				@Override
				public int compare(ISprintPlanDesc o1, ISprintPlanDesc o2) {
					return Integer.parseInt(o1.getID()) - Integer.parseInt(o2.getID());
				}
    		});
    		
    		for(ISprintPlanDesc sprint : allSprints) {
    			JSONObject sprintplan = new JSONObject();
    			sprintplan.put("ID", sprint.getID());
    			sprintplan.put("Name", "Sprint" + sprint.getID());
    			storyinfo = getStoryInfo(sprint.getID(), SBhelper);
    			totalstorycount += storyinfo.get("StoryCount");
    			sprintplan.put("StoryDoneCount", storyinfo.get("StoryDoneCount"));
    			sprints.put(sprintplan);
    			sprintcount++;
    		}
	    	storycountobject.put("Sprints", sprints);
	    	storycountobject.put("TotalSprintCount", sprintcount);
	    	storycountobject.put("TotalStoryCount", totalstorycount);
    	} catch (JSONException e) {
            e.printStackTrace();
    	}
    	updateJSonInfo(storycountobject);
    	return storycountobject.toString();
    }
    
    // 取得Sprint的Story資訊
    private HashMap<String, Integer> getStoryInfo(String sprintID, SprintBacklogHelper SBhelper) {
    	HashMap<String, Integer> storyinfo = new HashMap<String, Integer>(); 
    	ArrayList<StoryObject> stories = SBhelper.getStoryBySprintId(Long.parseLong(sprintID));
    	int storypoint = 0;
    	int storydonecount = 0;
    	for (StoryObject story : stories) {
    		if (story.getStatus() == StoryObject.STATUS_DONE) {
    			storypoint += story.getEstimate();
    			storydonecount++;
    		}
    	}
    	storyinfo.put("StoryPoint", storypoint);
    	storyinfo.put("StoryCount", stories.size());
    	storyinfo.put("StoryDoneCount", storydonecount);
    	return storyinfo;
    }
    
    // 更新JSON string裡面的資訊, 第一次只建立story data
    private JSONObject updateJSonInfo(JSONObject jsoninfo) {
    	try {
    		 // JSON是call by reference!!! 查memory=>System.identityHashCode(Object x)
	        JSONArray sprints = (JSONArray)jsoninfo.get("Sprints");
	        int sprintcount = jsoninfo.getInt("TotalSprintCount");
	        int storycount = jsoninfo.getInt("TotalStoryCount");
	        int storyremaining = jsoninfo.getInt("TotalStoryCount");
	        double idealrange = (double)storycount / sprintcount;
	        for (int i = 0; i < sprints.length(); i++) {
	        	JSONObject sprintplan = sprints.getJSONObject(i);
	        	storyremaining -= sprintplan.getInt("StoryDoneCount");
	        	sprintplan.put("StoryRemainingCount", storyremaining);
	        	sprintplan.put("StoryIdealCount", storycount - (idealrange * (i + 1)));
	        }
	        jsoninfo.put("Sprints", sprints);
        } catch (JSONException e) {
	        e.printStackTrace();
        }
    	return jsoninfo;
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
	
	public StringBuilder showStoryFromRelease(IProject project, String R_ID, ArrayList<StoryObject> storyList) {
		IReleasePlanDesc plan = getReleasePlan(R_ID);
		
		ReleaseBacklog releaseBacklog;
		try {
			releaseBacklog = new ReleaseBacklog(project, plan, storyList);
		} catch (Exception e) {
			// TODO: handle exception
			releaseBacklog = null;
		}
		
		if (R_ID != null) {
			ArrayList<StoryObject> stories = releaseBacklog.getStory();
			stories = sortStory(stories);
			
			// write stories to XML format
			StringBuilder sb = new StringBuilder();
			sb.append("<ExistingStories>");
			
			for (int i = 0; i < stories.size(); i++) {
				
				long sprintId = stories.get(i).getSprintId();
				sb.append("<Story>");
				sb.append("<Id>" + stories.get(i).getId() + "</Id>");
				sb.append("<Link></Link>");
				sb.append("<Name>" + replaceStr(stories.get(i).getName()) + "</Name>");
				sb.append("<Value>" + stories.get(i).getValue() + "</Value>");
				sb.append("<Importance>" + stories.get(i).getImportance() + "</Importance>");
				sb.append("<Estimate>" + stories.get(i).getEstimate() + "</Estimate>");
				sb.append("<Status>" + stories.get(i).getStatus() + "</Status>");
				sb.append("<Notes>" + replaceStr(stories.get(i).getNotes()) + "</Notes>");
				sb.append("<HowToDemo>" + replaceStr(stories.get(i).getHowToDemo()) + "</HowToDemo>");
				sb.append("<Release>" + R_ID + "</Release>");
				sb.append("<Sprint>" + sprintId + "</Sprint>");
				sb.append("<Tag>" + replaceStr(this.joinTagOnStory(stories.get(i).getTags(), ",")) + "</Tag>");
				sb.append("</Story>");
			}
			sb.append("</ExistingStories>");
			
			return sb;
		} else {
			return null;
		}		
	}

	// sort story information by importance
	private ArrayList<StoryObject> sortStory(ArrayList<StoryObject> stories) {
		ArrayList<StoryObject> list = new ArrayList<StoryObject>();

		for (StoryObject issue : stories) {
			int index = 0;
			for (index = 0; index < list.size(); index++) {
				if (issue.getImportance() > list.get(index).getImportance()) {
					break;
				}
			}
			list.add(index, issue);
		}

		return list;
	}
	
	private String joinTagOnStory(List<TagObject> tags, String delimiter)
	{
	    if (tags.isEmpty())
	    	return "";
	 
	    StringBuilder sb = new StringBuilder();
	 
	    for (TagObject x : tags)
	    	sb.append(x.getName() + delimiter);
	 
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
	public void addReleaseSprintStory(ProjectObject project, IUserSession session, String ID, List<ISprintPlanDesc> oldSprintList, IReleasePlanDesc reDesc){
		List<ISprintPlanDesc> newSprintList =  reDesc.getSprintDescList();
		ProductBacklogLogic productBacklogLogic = new ProductBacklogLogic(project);
		ArrayList<Long> storyList;
		
		//For deleting old sprint. Taking original SprintList to compare with new SprintList.
		if(oldSprintList != null){	
			storyList = compareReleaseSprint(oldSprintList, newSprintList, project, session);
			storyList.clear();
		}

		//For adding new sprint. Taking new SprintList to compare with original SprintList.
		storyList = compareReleaseSprint(newSprintList, oldSprintList, project, session);
	}
	
	//SprintList，舊日期的list 與 新日期的list做比對
	private ArrayList<Long> compareReleaseSprint(List<ISprintPlanDesc> sprintList1, List<ISprintPlanDesc> sprintList2,
											   ProjectObject project, IUserSession session) {
//		SprintBacklogMapper sprintBacklog;
		SprintBacklogLogic sprintBacklogLogic;
		ArrayList<Long> storyList = new ArrayList<Long>();
		boolean deleteOrAdd = true;

		if (sprintList2 != null) {
			for (ISprintPlanDesc list1 : sprintList1) {
				for (ISprintPlanDesc list2 : sprintList2) {
					if (list1.getID().equals(list2.getID())) {//Sprint still exists.
						deleteOrAdd = false;
						break;
					}
				}
				if (deleteOrAdd == true) {//Finding sprint not existing in list2.
					sprintBacklogLogic = new SprintBacklogLogic(project, Long.parseLong(list1.getID()));
					ArrayList<StoryObject> stories = sprintBacklogLogic.getStories();
					for (StoryObject story : stories) {
						storyList.add(story.getId());
					}
				}
				deleteOrAdd = true;//For next sprint.
			}
		} else { // For creating a new sprint
			for (ISprintPlanDesc list1 : sprintList1) {
				sprintBacklogLogic = new SprintBacklogLogic(project, Long.parseLong(list1.getID()));
				ArrayList<StoryObject> stories = sprintBacklogLogic.getStories();
				for (StoryObject story : stories) {
					storyList.add(story.getId());
				}
			}
		}
		return storyList;
	}

	/*
	 * from GetReleaseBurndownChartDataAction
	 */
	public StringBuilder getReleaseBurndownChartData(ProjectObject project, IUserSession session, String releaseId) {
		ProductBacklogHelper pbHelper = new ProductBacklogHelper(project);
		IReleasePlanDesc plan = this.getReleasePlan(releaseId);

		ReleaseBacklog releaseBacklog = null;
		StringBuilder result = new StringBuilder("");
		try {
			releaseBacklog = new ReleaseBacklog(project, plan, pbHelper.getStoriesByRelease(plan));
			ReleaseBoard board = new ReleaseBoard(releaseBacklog);
			result.append(Translation.translateBurndownChartDataToJson(board.getStoryIdealPointMap(), board.getStoryRealPointMap()));
		} catch (Exception e) {
			releaseBacklog = null;
			result.append("{success: \"false\"}");
		}

		return result;
	}
	
}
