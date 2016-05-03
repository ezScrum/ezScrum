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

	public ArrayList<ReleaseObject> getReleases() {
		return mReleasePlanMapper.getReleases();
	}

	public void deleteReleasePlan(long id) {
		mReleasePlanMapper.deleteRelease(id);
	}

	public void editRelease(ReleaseInfo releaseInfo) {
		mReleasePlanMapper.updateRelease(releaseInfo);
	}
	
	public long createRelease(ReleaseInfo releaseInfo) {
		return mReleasePlanMapper.addRelease(releaseInfo);
	}

	// return the release plan of releasePlanID
	public ReleaseObject getReleasePlan(long releaseId) {
		return mReleasePlanMapper.getRelease(releaseId);
	}

	// return the release plans of releasePlanID' string
	public ArrayList<ReleaseObject> getReleasesByIds(String releaseIds) {
		ArrayList<ReleaseObject> releases = new ArrayList<ReleaseObject>();
		if (releaseIds.length() == 0) {
			return releases;
		}
		String[] releasesIdString = releaseIds.split(",");
		for (String releaseIdString : releasesIdString) {
			releases.add(mReleasePlanMapper.getRelease(Long
					.parseLong(releaseIdString)));
		}
		return releases;
	}

	// return the releaseID which has the sprintID
	public long getReleaseIdBySprintId(long sprintId) {
		long releaseId = -1;
		ArrayList<ReleaseObject> releases = mReleasePlanMapper.getReleases();

		for (ReleaseObject release : releases) {
			SprintObject sprint = SprintObject.get(sprintId);
			if (sprint == null) {
			      continue;
		    }
			if (release.containsSprint(sprint)) {
				return release.getId();
			}
		}

		return releaseId;
	}

	/*
	 * from ShowReleasePlan2Action
	 */

	public ArrayList<ReleaseObject> sortStartDate(ArrayList<ReleaseObject> releases) {
		ArrayList<ReleaseObject> sortedReleases = new ArrayList<ReleaseObject>();
		// ListReleaseDescs 依照 StartDate 排序
		for (ReleaseObject release : releases) {
			Date addDate = DateUtil.dayFilter(release.getStartDateString()); // 要新增的  Date

			if (!sortedReleases.isEmpty()) {
				int index = 0;
				for (index = 0; index < sortedReleases.size(); index++) {
					ReleaseObject Desc = sortedReleases.get(index); // 目前要被比對的 release
					Date comparedDate = DateUtil
							.dayFilter(Desc.getStartDateString()); // 要被比對的
					// Date
					if (addDate.compareTo(comparedDate) < 0) {
						break;
					}
				}
				sortedReleases.add(index, release);
			} else {
				sortedReleases.add(release);
			}
		}
		return sortedReleases;
	}

	public String setJson(ArrayList<ReleaseObject> releases,
			SprintPlanHelper sprintPlanHelper) {
		StringBuilder stringBuilder = new StringBuilder("[");
		boolean isFirstRelease = true;
		for (ReleaseObject release : releases) {
			if (isFirstRelease) {
				stringBuilder.append("{");
				isFirstRelease = false;
			}
			else {
				stringBuilder.append(",{");
			}
			stringBuilder.append("Type:\'Release\',");
			stringBuilder.append("ID:\'");
			stringBuilder.append(release.getSerialId());
			stringBuilder.append("\',");
			stringBuilder.append("Name:\'");
			stringBuilder.append(TranslateSpecialChar.TranslateJSONChar(release.getName()));
			stringBuilder.append("\',");
			stringBuilder.append("StartDate:\'");
			stringBuilder.append(release.getStartDateString());
			stringBuilder.append("\',");
			stringBuilder.append("EndDate:\'");
			stringBuilder.append(release.getEndDateString());
			stringBuilder.append("\',");
			stringBuilder.append("Description:\'");
			stringBuilder.append(TranslateSpecialChar.TranslateJSONChar(release.getDescription()));
			stringBuilder.append("\',");
			

			if (release.getSprints() != null
					&& !release.getSprints().isEmpty()) {
				stringBuilder.append("expanded: true,");
				stringBuilder.append("iconCls:\'task-folder\',");
				stringBuilder.append("children:[");
				stringBuilder.append(setSprintToJson(release, sprintPlanHelper));
				stringBuilder.append("]");
			} else {
				stringBuilder.append("leaf: true");
			}
			stringBuilder.append("}");
		}
		stringBuilder.append("]");
		return stringBuilder.toString();
	}

	/**
	 * from AjaxGetReleasePlanAction, 將release讀出並列成list再轉成JSON
	 */
	public String setReleaseListToJson(List<ReleaseObject> releases) {
		JSONObject releaseJson = new JSONObject();
		JSONArray releaseJsonArray = new JSONArray();
		try {
			for (ReleaseObject release : releases) {
				releaseJsonArray.put(release.toJSON());
			}
			releaseJson.put("Releases", releaseJsonArray);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return releaseJson.toString();
	}

	/**
	 * from AjaxGetVelocityAction, 將被選到的release plans拿出他們的sprint
	 * point並算出velocity,算出平均值再轉成JSON
	 */
	public String getSprintVelocityToJson(ArrayList<ReleaseObject> releases,
			SprintBacklogHelper sprintPlanHelper) {
		JSONObject velocityJson = new JSONObject();
		JSONArray sprintJsonArray = new JSONArray();
		HashMap<String, Integer> storyInfoMap;
		double totalVelocity = 0;
		int sprintCount = 0; // 計算被選的release內的sprint總數
		try {
			for (ReleaseObject release : releases) {
				if (release == null) {
					break;
				}
				for (SprintObject sprint : release.getSprints()) {
					JSONObject sprintplan = new JSONObject();
					sprintplan.put("ID", String.valueOf(sprint.getId()));
					sprintplan.put("Name",
							"Sprint" + String.valueOf(sprint.getId()));
					storyInfoMap = getStoryInfo(String.valueOf(sprint.getId()),
							sprintPlanHelper);
					sprintplan.put("Velocity", storyInfoMap.get("StoryPoint"));
					sprintJsonArray.put(sprintplan);
					totalVelocity += storyInfoMap.get("StoryPoint");
					sprintCount++;
				}
			}
			velocityJson.put("Sprints", sprintJsonArray);
			if (sprintCount != 0) {
				velocityJson.put("Average", totalVelocity / sprintCount);
			}
			else {
				velocityJson.put("Average", "");
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return velocityJson.toString();
	}

	/**
	 * form AjaxGetStoryCountAction 將被選到的release plans將所含的sprint中的story
	 * point算出總和,再轉成JSON
	 */
	public String getStoryCountChartJson(List<ReleaseObject> releases,
			SprintBacklogHelper sprintPlanHelper) {
		JSONObject storyCountJson = new JSONObject();
		JSONArray sprintJsonArray = new JSONArray();
		HashMap<String, Integer> storyInfoMap;
		int totalStoryCount = 0;
		int sprintCount = 0; // 計算被選的release內的sprint總數
		try {
			ArrayList<SprintObject> allSprints = new ArrayList<SprintObject>();
			for (ReleaseObject release : releases) {
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
				JSONObject sprintJson = new JSONObject();
				sprintJson.put("ID", String.valueOf(sprint.getId()));
				sprintJson.put("Name",
						"Sprint" + String.valueOf(sprint.getId()));
				storyInfoMap = getStoryInfo(String.valueOf(sprint.getId()),
						sprintPlanHelper);
				totalStoryCount += storyInfoMap.get("StoryCount");
				sprintJson.put("StoryDoneCount",
						storyInfoMap.get("StoryDoneCount"));
				sprintJsonArray.put(sprintJson);
				sprintCount++;
			}
			storyCountJson.put("Sprints", sprintJsonArray);
			storyCountJson.put("TotalSprintCount", sprintCount);
			storyCountJson.put("TotalStoryCount", totalStoryCount);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		updateJsonInfo(storyCountJson);
		return storyCountJson.toString();
	}

	// 取得Sprint的Story資訊
	private HashMap<String, Integer> getStoryInfo(String sprintId,
			SprintBacklogHelper sprintBacklogHelper) {
		HashMap<String, Integer> storyInfoMap = new HashMap<String, Integer>();
		ArrayList<StoryObject> stories = sprintBacklogHelper
				.getStoriesSortedByIdInSprint();
		int storyPoint = 0;
		int storyDoneCount = 0;
		for (StoryObject story : stories) {
			if (story.getStatus() == StoryObject.STATUS_DONE) {
				storyPoint += story.getEstimate();
				storyDoneCount++;
			}
		}
		storyInfoMap.put("StoryPoint", storyPoint);
		storyInfoMap.put("StoryCount", stories.size());
		storyInfoMap.put("StoryDoneCount", storyDoneCount);
		return storyInfoMap;
	}

	// 更新JSON string裡面的資訊, 第一次只建立story data
	private JSONObject updateJsonInfo(JSONObject jsonInfo) {
		try {
			// JSON是call by reference!!! 查memory=>System.identityHashCode(Object
			// x)
			JSONArray sprintJsonArray = (JSONArray) jsonInfo.get("Sprints");
			int sprintCount = jsonInfo.getInt("TotalSprintCount");
			int storyCount = jsonInfo.getInt("TotalStoryCount");
			int storyRemaining = jsonInfo.getInt("TotalStoryCount");
			double idealRange = (double) storyCount / sprintCount;
			for (int i = 0; i < sprintJsonArray.length(); i++) {
				JSONObject sprintJson = sprintJsonArray.getJSONObject(i);
				storyRemaining -= sprintJson.getInt("StoryDoneCount");
				sprintJson.put("StoryRemainingCount", storyRemaining);
				sprintJson.put("StoryIdealCount", storyCount
						- (idealRange * (i + 1)));
			}
			jsonInfo.put("Sprints", sprintJsonArray);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return jsonInfo;
	}

	// 透過release 將 sprint 的資訊寫成 Json
	private String setSprintToJson(ReleaseObject release,
			SprintPlanHelper sprintPlanHelper) {
		StringBuilder stringBuilder = new StringBuilder("");
		if (release.getSprints() != null) { // 有 sprint 資訊，則抓取 sprint 的 xml 資料
			boolean isFirstSprint = true;
			// 將資訊設定成 JSon 輸出格式
			for (SprintObject sprint : release.getSprints()) {
				if (isFirstSprint) {
					stringBuilder.append("{");
					isFirstSprint = false;
				}
				else {
					stringBuilder.append(",{");
				}
				stringBuilder.append("Type:\'Sprint\',");
				stringBuilder.append("ID:\'");
				stringBuilder.append(sprint.getSerialId());
				stringBuilder.append("\',");
				stringBuilder.append("Name:\'");
				stringBuilder.append(TranslateSpecialChar.TranslateJSONChar(sprint.getGoal()));
				stringBuilder.append("\',");
				stringBuilder.append("StartDate:\'");
				stringBuilder.append(sprint.getStartDateString());
				stringBuilder.append("\',");
				stringBuilder.append("EndDate:\'");
				stringBuilder.append(sprint.getEndDateString());
				stringBuilder.append("\',");
				stringBuilder.append("Interval:\'");
				stringBuilder.append(sprint.getInterval());
				stringBuilder.append("\',");
				stringBuilder.append("Description:\' \',");
				stringBuilder.append("iconCls:\'task\',");
				stringBuilder.append("leaf: true");
				stringBuilder.append("}");
			}
		}
		return stringBuilder.toString();
	}

	/**
	 * from AjaxShowStoryFromReleaseAction
	 */

	public StringBuilder showStoryFromRelease(long releaseId) {
		ReleaseObject release = mReleasePlanMapper.getRelease(releaseId);

		if (releaseId > 0) {
			ArrayList<StoryObject> stories = release.getStories();
			stories = sortStories(stories);

			// write stories to XML format
			StringBuilder stringBuilder = new StringBuilder();
			stringBuilder.append("<ExistingStories>");

			for (int i = 0; i < stories.size(); i++) {

				long sprintId = stories.get(i).getSprintId();
				// Get serial sprint id
				long serialSprintId = -1;
				SprintObject sprint = SprintObject.get(sprintId);
				if (sprint != null) {
					serialSprintId = sprint.getSerialId();
				}
				stringBuilder.append("<Story>");
				stringBuilder.append("<Id>" + stories.get(i).getSerialId() + "</Id>");
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
				stringBuilder.append("<Release>" + release.getSerialId() + "</Release>");
				stringBuilder.append("<Sprint>" + serialSprintId + "</Sprint>");
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
	private ArrayList<StoryObject> sortStories(ArrayList<StoryObject> stories) {
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
	
	public StringBuilder checkReleaseDateOverlapping(long releaseId, String startDateString,
			String endDateString, String action) {
		ArrayList<ReleaseObject> releases = mReleasePlanMapper.getReleases();
		Date startDate = DateUtil.dayFilter(startDateString);
		Date endDate = DateUtil.dayFilter(endDateString);
		String result = "legal";
		for (ReleaseObject release : releases) {
			if (action.equals("edit") && (releaseId == release.getId())) {// 不與自己比較
				continue;
			}
			// check 日期的頭尾是否有在各個 release plan 日期範圍內
			if (release.contains(startDate) || release.contains(endDate)) {
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
