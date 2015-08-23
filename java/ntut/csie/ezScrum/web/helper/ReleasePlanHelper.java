package ntut.csie.ezScrum.web.helper;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import ntut.csie.ezScrum.iteration.iternal.ReleaseBoard;
import ntut.csie.ezScrum.web.dataInfo.ReleaseInfo;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.dataObject.ReleaseObject;
import ntut.csie.ezScrum.web.dataObject.SprintObject;
import ntut.csie.ezScrum.web.dataObject.StoryObject;
import ntut.csie.ezScrum.web.dataObject.TagObject;
import ntut.csie.ezScrum.web.mapper.ReleasePlanMapper;
import ntut.csie.ezScrum.web.support.TranslateSpecialChar;
import ntut.csie.ezScrum.web.support.Translation;
import ntut.csie.jcis.core.util.DateUtil;

import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

import edu.emory.mathcs.backport.java.util.Collections;

public class ReleasePlanHelper {
	private ReleasePlanMapper mReleasePlanMapper;
	private ProjectObject mProject;

	public ReleasePlanHelper(ProjectObject project) {
		mProject = project;
		mReleasePlanMapper = new ReleasePlanMapper(mProject);
	}

	// remove later
	public ReleaseObject[] getReleaseArray() {
		ArrayList<ReleaseObject> releases = mReleasePlanMapper.getReleases();
		return releases.toArray(new ReleaseObject[releases.size()]);
	}

	public ArrayList<ReleaseObject> getReleases() {
		return mReleasePlanMapper.getReleases();
	}

	public long getLastReleasePlanNumber() {
		int length = mReleasePlanMapper.getReleases().size();
		ArrayList<ReleaseObject> releases = mReleasePlanMapper.getReleases();
		if (length > 0) {
			return releases.get(length - 1).getId();
		} else {
			return 0;
		}
	}

	public void deleteReleasePlan(long id) {
		mReleasePlanMapper.deleteRelease(id);
	}

	public void editReleasePlan(ReleaseInfo releaseInfo) {
		mReleasePlanMapper.updateRelease(releaseInfo);
	}
	
	public long createReleasePlan(ReleaseInfo releaseInfo) {
		return mReleasePlanMapper.addRelease(releaseInfo);
	}

	// return the release plan of releasePlanID
	public ReleaseObject getReleasePlan(long releaseId) {
		return mReleasePlanMapper.getRelease(releaseId);
	}

	// return the release plans of releasePlanID' string
	public List<ReleaseObject> getReleasePlansByIds(String releasePlanIDs) {
		List<ReleaseObject> plans = new ArrayList<ReleaseObject>();
		if (releasePlanIDs.length() == 0) {
			return plans;
		}
		String[] releasesIdString = releasePlanIDs.split(",");
		for (String releaseIdString : releasesIdString) {
			plans.add(mReleasePlanMapper.getRelease(Long
					.parseLong(releaseIdString)));
		}
		return plans;
	}

	// return the releaseID which has the sprintID
	public String getReleaseId(long sprintId) {
		String releaseId = "0";
		ArrayList<ReleaseObject> releases = mReleasePlanMapper.getReleases();

		for (ReleaseObject release : releases) {
			SprintObject sprint = SprintObject.get(sprintId);
			if (release.containsSprint(sprint)) {
				return String.valueOf(release.getId());
			}
		}

		return releaseId;
	}

	/*
	 * from ShowReleasePlan2Action
	 */

	public ArrayList<ReleaseObject> sortStartDate(ArrayList<ReleaseObject> releases) {
		ArrayList<ReleaseObject> ListReleaseDescs = new ArrayList<ReleaseObject>();
		// ListReleaseDescs 依照 StartDate 排序
		for (ReleaseObject desc : releases) {
			Date addDate = DateUtil.dayFilter(desc.getStartDateString()); // 要新增的  Date

			if (ListReleaseDescs.size() > 0) {
				int index = 0;
				for (index = 0; index < ListReleaseDescs.size(); index++) {
					ReleaseObject Desc = ListReleaseDescs.get(index); // 目前要被比對的 release
					Date cmpDate = DateUtil
							.dayFilter(Desc.getStartDateString()); // 要被比對的
					// Date
					if (addDate.compareTo(cmpDate) < 0) {
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

	public String setJSon(ArrayList<ReleaseObject> releases,
			SprintPlanHelper SPhelper) {
		TranslateSpecialChar tsc = new TranslateSpecialChar();

		String tree = "";
		tree += "[";
		int i = 0;
		for (ReleaseObject release : releases) {
			if (i == 0)
				tree += "{";
			else
				tree += ",{";
			tree += "Type:\'Release\',";
			tree += "ID:\'" + release.getId() + "\',";
			tree += "Name:\'" + tsc.TranslateJSONChar(release.getName())
					+ "\',";
			tree += "StartDate:\'" + release.getStartDateString() + "\',";
			tree += "EndDate:\'" + release.getDueDateString() + "\',";
			tree += "Description:\'"
					+ tsc.TranslateJSONChar(release.getDescription()) + "\',";

			if (release.getSprints() != null
					&& release.getSprints().size() != 0) {
				tree += "expanded: true,";
				tree += "iconCls:\'task-folder\',";

				tree += "children:[";
				tree += setSprintToJSon(release, SPhelper);
				tree += "]";
			} else {
				tree += "leaf: true";
			}
			tree += "}";
			i++;
		}
		// [{task:\'Project: Shopping\',duration:13.25,user:\'Tommy
		// Maintz\',leaf: true}]

		tree += "]";
		return tree;
	}

	/**
	 * from AjaxGetReleasePlanAction, 將release讀出並列成list再轉成JSON
	 */
	public String setReleaseListToJSon(List<ReleaseObject> ListReleaseDescs) {
		JSONObject releaseObject = new JSONObject();
		JSONArray releaseplanlist = new JSONArray();
		try {
			for (ReleaseObject plan : ListReleaseDescs) {
				JSONObject releaseplan = new JSONObject();
				releaseplan.put("ID", plan.getId());
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
	 * from AjaxGetVelocityAction, 將被選到的release plans拿出他們的sprint
	 * point並算出velocity,算出平均值再轉成JSON
	 */
	public String getSprintVelocityToJSon(List<ReleaseObject> ListReleaseDescs,
			SprintBacklogHelper SBhelper) {
		JSONObject velocityobject = new JSONObject();
		JSONArray sprints = new JSONArray();
		HashMap<String, Integer> storyinfo;
		double totalvelocity = 0;
		int sprintcount = 0; // 計算被選的release內的sprint總數
		try {
			for (ReleaseObject release : ListReleaseDescs) {
				if (release == null)
					break;
				for (SprintObject sprint : release.getSprints()) {
					JSONObject sprintplan = new JSONObject();
					sprintplan.put("ID", String.valueOf(sprint.getId()));
					sprintplan.put("Name",
							"Sprint" + String.valueOf(sprint.getId()));
					storyinfo = getStoryInfo(String.valueOf(sprint.getId()),
							SBhelper);
					sprintplan.put("Velocity", storyinfo.get("StoryPoint"));
					sprints.put(sprintplan);
					totalvelocity += storyinfo.get("StoryPoint");
					sprintcount++;
				}
			}
			velocityobject.put("Sprints", sprints);
			if (sprintcount != 0)
				velocityobject.put("Average", totalvelocity / sprintcount);
			else
				velocityobject.put("Average", "");
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return velocityobject.toString();
	}

	/**
	 * form AjaxGetStoryCountAction 將被選到的release plans將所含的sprint中的story
	 * point算出總和,再轉成JSON
	 */
	public String getStoryCountChartJSon(List<ReleaseObject> ListReleaseDescs,
			SprintBacklogHelper SBhelper) {
		JSONObject storycountobject = new JSONObject();
		JSONArray sprints = new JSONArray();
		HashMap<String, Integer> storyinfo;
		int totalstorycount = 0;
		int sprintcount = 0; // 計算被選的release內的sprint總數
		try {
			ArrayList<SprintObject> allSprints = new ArrayList<SprintObject>();
			for (ReleaseObject release : ListReleaseDescs) {
				if (release == null)
					break;
				for (SprintObject sprint : release.getSprints()) {
					allSprints.add(sprint);
				}
			}

			Collections.sort(allSprints, new Comparator<SprintObject>() {
				@Override
				public int compare(SprintObject o1, SprintObject o2) {
					return (int) (o1.getId() - o2.getId());
				}
			});

			for (SprintObject sprint : allSprints) {
				JSONObject sprintplan = new JSONObject();
				sprintplan.put("ID", String.valueOf(sprint.getId()));
				sprintplan.put("Name",
						"Sprint" + String.valueOf(sprint.getId()));
				storyinfo = getStoryInfo(String.valueOf(sprint.getId()),
						SBhelper);
				totalstorycount += storyinfo.get("StoryCount");
				sprintplan.put("StoryDoneCount",
						storyinfo.get("StoryDoneCount"));
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
	private HashMap<String, Integer> getStoryInfo(String sprintID,
			SprintBacklogHelper sprintBacklogHelper) {
		HashMap<String, Integer> storyinfo = new HashMap<String, Integer>();
		ArrayList<StoryObject> stories = sprintBacklogHelper
				.getStoriesSortedByIdInSprint();
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
			// JSON是call by reference!!! 查memory=>System.identityHashCode(Object
			// x)
			JSONArray sprints = (JSONArray) jsoninfo.get("Sprints");
			int sprintcount = jsoninfo.getInt("TotalSprintCount");
			int storycount = jsoninfo.getInt("TotalStoryCount");
			int storyremaining = jsoninfo.getInt("TotalStoryCount");
			double idealrange = (double) storycount / sprintcount;
			for (int i = 0; i < sprints.length(); i++) {
				JSONObject sprintplan = sprints.getJSONObject(i);
				storyremaining -= sprintplan.getInt("StoryDoneCount");
				sprintplan.put("StoryRemainingCount", storyremaining);
				sprintplan.put("StoryIdealCount", storycount
						- (idealrange * (i + 1)));
			}
			jsoninfo.put("Sprints", sprints);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return jsoninfo;
	}

	// 透過release des將sprint的資訊寫成JSon
	private String setSprintToJSon(ReleaseObject IRDesc,
			SprintPlanHelper SPhelper) {
		TranslateSpecialChar tsc = new TranslateSpecialChar();
		String sprintTree = "";
		if (IRDesc.getSprints() != null) { // 有 sprint 資訊，則抓取 sprint 的 xml 資料
			int i = 0;
			// 將資訊設定成 JSon 輸出格式
			for (SprintObject sprint : IRDesc.getSprints()) {
				if (i == 0)
					sprintTree += "{";
				else
					sprintTree += ",{";
				sprintTree += "Type:\'Sprint\',";
				sprintTree += "ID:\'" + String.valueOf(sprint.getId()) + "\',";
				sprintTree += "Name:\'"
						+ tsc.TranslateJSONChar(sprint.getSprintGoal()) + "\',";
				sprintTree += "StartDate:\'" + sprint.getStartDateString()
						+ "\',";
				sprintTree += "EndDate:\'" + sprint.getDueDateString() + "\',";
				sprintTree += "Interval:\'" + sprint.getInterval() + "\',";
				sprintTree += "Description:\' \',";
				sprintTree += "iconCls:\'task\',";
				sprintTree += "leaf: true";
				sprintTree += "}";
				i++;
			}
		}
		return sprintTree;
	}

	/**
	 * from AjaxShowStoryFromReleaseAction
	 */

	public StringBuilder showStoryFromRelease(long releaseId) {
		ReleaseObject release = mReleasePlanMapper.getRelease(releaseId);

		if (releaseId > 0) {
			ArrayList<StoryObject> stories = release.getStories();
			stories = sortStory(stories);

			// write stories to XML format
			StringBuilder stringBuilder = new StringBuilder();
			stringBuilder.append("<ExistingStories>");

			for (int i = 0; i < stories.size(); i++) {

				long sprintId = stories.get(i).getSprintId();
				stringBuilder.append("<Story>");
				stringBuilder.append("<Id>" + stories.get(i).getId() + "</Id>");
				stringBuilder.append("<Link></Link>");
				stringBuilder.append("<Name>"
						+ replaceStr(stories.get(i).getName()) + "</Name>");
				stringBuilder.append("<Value>" + stories.get(i).getValue()
						+ "</Value>");
				stringBuilder.append("<Importance>"
						+ stories.get(i).getImportance() + "</Importance>");
				stringBuilder.append("<Estimate>"
						+ stories.get(i).getEstimate() + "</Estimate>");
				stringBuilder.append("<Status>" + stories.get(i).getStatus()
						+ "</Status>");
				stringBuilder.append("<Notes>"
						+ replaceStr(stories.get(i).getNotes()) + "</Notes>");
				stringBuilder.append("<HowToDemo>"
						+ replaceStr(stories.get(i).getHowToDemo())
						+ "</HowToDemo>");
				stringBuilder.append("<Release>" + releaseId + "</Release>");
				stringBuilder.append("<Sprint>" + sprintId + "</Sprint>");
				stringBuilder.append("<Tag>"
						+ replaceStr(joinTagOnStory(stories.get(i)
								.getTags(), ",")) + "</Tag>");
				stringBuilder.append("</Story>");
			}
			stringBuilder.append("</ExistingStories>");

			return stringBuilder;
		} else {
			return null;
		}
	}

	// sort story information by importance
	private ArrayList<StoryObject> sortStory(ArrayList<StoryObject> stories) {
		ArrayList<StoryObject> sortedStories = new ArrayList<StoryObject>();

		for (StoryObject story : stories) {
			int index = 0;
			for (index = 0; index < sortedStories.size(); index++) {
				if (story.getImportance() > sortedStories.get(index).getImportance()) {
					break;
				}
			}
			sortedStories.add(index, story);
		}

		return sortedStories;
	}

	private String joinTagOnStory(List<TagObject> tags, String delimiter) {
		if (tags.isEmpty())
			return "";

		StringBuilder stringBuilder = new StringBuilder();

		for (TagObject x : tags)
			stringBuilder.append(x.getName() + delimiter);

		stringBuilder.delete(stringBuilder.length() - delimiter.length(), stringBuilder.length());

		return stringBuilder.toString();
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
		long id = getLastReleasePlanNumber() + 1; // 依照目前最近ID count 累加

		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("<Root><Release>");
		stringBuilder.append("<ID>" + id + "</ID>");
		stringBuilder.append("</Release></Root>");

		return stringBuilder;
	}

	/*
	 * from CheckReleaseDateAction
	 */

	public StringBuilder checkReleaseDate(String releaseId, String startDate,
			String endDate, String action) {
		ArrayList<ReleaseObject> releases = mReleasePlanMapper.getReleases();
		String result = "legal";
		for (ReleaseObject release : releases) {
			if (action.equals("edit") && releaseId.equals(release.getId())) {// 不與自己比較
				continue;
			}
			// check日期的頭尾是否有在各個RP日期範圍內
			if ((startDate.compareTo(release.getStartDateString()) >= 0 && startDate
					.compareTo(release.getDueDateString()) <= 0)
					|| (endDate.compareTo(release.getStartDateString()) >= 0 && endDate
							.compareTo(release.getDueDateString()) <= 0)) {
				result = "illegal";
				break;
			}
		}

		return new StringBuilder(result);
	}

	/*
	 * from GetReleaseBurndownChartDataAction
	 */
	public StringBuilder getReleaseBurndownChartData(long releaseId) {
		ReleaseObject release = mReleasePlanMapper.getRelease(releaseId);
		StringBuilder result = new StringBuilder("");
		try {
			ReleaseBoard releaseBoard = new ReleaseBoard(release);
			result.append(Translation.translateBurndownChartDataToJson(
					releaseBoard.getStoryIdealPointMap(),
					releaseBoard.getStoryRealPointMap()));
		} catch (Exception e) {
			result.append("{success: \"false\"}");
		}

		return result;
	}

}
