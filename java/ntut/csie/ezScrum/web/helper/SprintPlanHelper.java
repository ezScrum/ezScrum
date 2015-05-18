package ntut.csie.ezScrum.web.helper;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import ntut.csie.ezScrum.iteration.core.ISprintPlanDesc;
import ntut.csie.ezScrum.iteration.iternal.SprintPlanDesc;
import ntut.csie.ezScrum.pic.core.IUserSession;
import ntut.csie.ezScrum.restful.mobile.support.ConvertSprint;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
import ntut.csie.ezScrum.web.dataObject.SprintObject;
import ntut.csie.ezScrum.web.dataObject.StoryObject;
import ntut.csie.ezScrum.web.dataObject.TaskObject;
import ntut.csie.ezScrum.web.form.IterationPlanForm;
import ntut.csie.ezScrum.web.logic.ProductBacklogLogic;
import ntut.csie.ezScrum.web.logic.SprintBacklogLogic;
import ntut.csie.ezScrum.web.logic.SprintPlanLogic;
import ntut.csie.ezScrum.web.mapper.SprintBacklogMapper;
import ntut.csie.ezScrum.web.mapper.SprintPlanMapper;
import ntut.csie.jcis.core.util.DateUtil;
import ntut.csie.jcis.resource.core.IProject;

public class SprintPlanHelper {
	private SprintPlanMapper mSprintPlanMapper;
	private SprintPlanLogic mSprintPlanLogic;
	private SprintBacklogMapper mSprintBacklogMapper;

	@Deprecated
	public SprintPlanHelper(IProject project) {
		mSprintPlanMapper = new SprintPlanMapper(project);
		mSprintPlanLogic = new SprintPlanLogic(project);
		mSprintBacklogMapper = (new SprintBacklogLogic(project, getCurrentSprintID()))
				.getSprintBacklogMapper();
	}
	
	public SprintPlanHelper(ProjectObject project) {
		mSprintPlanMapper = new SprintPlanMapper(project);
		mSprintPlanLogic = new SprintPlanLogic(project);
		mSprintBacklogMapper = (new SprintBacklogLogic(project, getCurrentSprintID())).getSprintBacklogMapper();
	}

	public List<ISprintPlanDesc> loadListPlans() {
		return mSprintPlanLogic.getSprintPlanListAndSortByStartDate();
	}

	public int getCurrentSprintID() {
		return mSprintPlanLogic.getCurrentSprintID();
	}

	// load the last plan, so perhaps the return is not the current plan.
	public ISprintPlanDesc loadCurrentPlan() {
		return mSprintPlanLogic.loadCurrentPlan();
	}

	// get next demoDate
	public String getNextDemoDate() {
		List<ISprintPlanDesc> descs = mSprintPlanLogic
				.getSprintPlanListAndSortById();
		if (descs.size() == 0)
			return null;
		if (!String.valueOf(getCurrentSprintID()).equals("-1")) {
			ISprintPlanDesc sprintPlanDesc = mSprintPlanMapper
					.getSprintPlan(String.valueOf(getCurrentSprintID()));
			if (sprintPlanDesc.getDemoDate().equals(""))
				return null;
			else
				return sprintPlanDesc.getDemoDate();
		}
		String demoDate = null;
		Date current = new Date();
		// compare the demo date to find the closed date
		for (ISprintPlanDesc desc : descs) {
			String descDemoDate = desc.getDemoDate();
			if (descDemoDate.equals(""))
				continue;
			// judge whether the descDemoDate is larger than now
			if (DateUtil.dayFilter(descDemoDate).getTime() > current.getTime()) {
				if (demoDate == null)
					demoDate = descDemoDate;
				// judge whether the demoDate is larger than descDemoDate
				else if (DateUtil.dayFilter(demoDate).getTime() > DateUtil
						.dayFilter(descDemoDate).getTime()) {
					demoDate = descDemoDate;
				}
			}
		}
		return demoDate;
	}

	public void editIterationPlanForm(IterationPlanForm form) {
		SprintPlanDesc desc = new SprintPlanDesc();
		desc.setInterval(form.getIterIterval());
		desc.setMemberNumber(form.getIterMemberNumber());
		desc.setStartDate(form.getIterStartDate());
		desc.setID(form.getID());
		desc.setFocusFactor(form.getFocusFactor());
		desc.setGoal(form.getGoal());
		desc.setAvailableDays(form.getAvailableDays());
		desc.setDemoDate(form.getDemoDate());
		desc.setNotes(form.getNotes());
		desc.setDemoPlace(form.getDemoPlace());
		mSprintPlanMapper.updateSprintPlan(desc);
	}

	public void saveIterationPlanForm(IterationPlanForm form) {
		SprintPlanDesc desc = new SprintPlanDesc();
		desc.setInterval(form.getIterIterval());
		desc.setMemberNumber(form.getIterMemberNumber());
		desc.setStartDate(form.getIterStartDate());
		desc.setID(form.getID());
		desc.setFocusFactor(form.getFocusFactor());
		desc.setGoal(form.getGoal());
		desc.setAvailableDays(form.getAvailableDays());
		desc.setDemoDate(form.getDemoDate());
		desc.setNotes(form.getNotes());
		desc.setDemoPlace(form.getDemoPlace());
		mSprintPlanMapper.addSprintPlan(desc);
	}

	/**
	 * 只取得一筆 sprint
	 * 
	 * @param lastsprint
	 * @param sprintID
	 * @return
	 */
	public ISprintPlanDesc getOneSprintInformation(String lastsprint,
			String sprintID) {
		int SprintID = -1;
		if (lastsprint != null && Boolean.parseBoolean(lastsprint)) {
			SprintID = getLastSprintId();
		} else if (sprintID != null) {
			SprintID = Integer.parseInt(sprintID);
		}

		if (SprintID > 0) {
			return loadPlan(SprintID);
		} else {
			return new SprintPlanDesc();
		}
	}

	public Date getProjectStartDate() {
		List<ISprintPlanDesc> sprintList = loadListPlans();
		return DateUtil.dayFilter(sprintList.get(0).getStartDate());
	}

	public Date getProjectEndDate() {
		List<ISprintPlanDesc> sprintList = loadListPlans();

		if (sprintList.size() > 0) {
			return DateUtil.dayFilter(sprintList.get(sprintList.size() - 1)
					.getEndDate());
		} else {
			return null;
		}
	}

	public int getLastSprintId() {
		List<ISprintPlanDesc> descs = mSprintPlanLogic
				.getSprintPlanListAndSortById();
		if (descs.size() == 0)
			return -1;
		else
			return Integer.parseInt(descs.get(descs.size() - 1).getID());
	}

	public long getSprintIDbyDate(Date date) {
		long sprintId = -1;
		List<ISprintPlanDesc> sprints = mSprintPlanLogic
				.getSprintPlanListAndSortByStartDate();

		for (ISprintPlanDesc sp : sprints) {
			// 此 sprint 的結束日期在 date 之後
			if (DateUtil.dayFilter(sp.getEndDate()).getTime() >= (DateUtil
					.dayFilter(date)).getTime()) {
				// 此 sprint 的開始日期在使用者設定之前
				// 兩者成立表示此使用者設定的日期在這個 sprint 區間內，回傳此 sprint ID
				if (DateUtil.dayFilter(date).getTime() >= (DateUtil
						.dayFilter(sp.getStartDate())).getTime()) {
					sprintId = Long.parseLong(sp.getID());
					break;
				}
			}
		}

		return sprintId;
	}

	/*
	 * from AjaxMoveSprintAction
	 */
	public void moveSprintPlan(ProjectObject project, IUserSession session, int oldId, int newId) {
		List<ISprintPlanDesc> descs = loadListPlans();
		moveSprint(oldId, newId);

		ProductBacklogHelper PBHelper = new ProductBacklogHelper(project);
		Map<Long, ArrayList<StoryObject>> map = PBHelper.getSprintHashMap();

		ArrayList<Integer> sprintsId = new ArrayList<Integer>();
		// 取出需要修改的sprint ID
		if (oldId > newId) {
			for (int i = newId; i <= oldId; i++) {
				if (isSprintPlan(descs, i))
					sprintsId.add(i);
			}
		} else {
			for (int i = 0; i <= newId - oldId; i++) {
				if (isSprintPlan(descs, newId - i))
					sprintsId.add(newId - i);
			}
		}

		ProductBacklogLogic productBacklogLogic = new ProductBacklogLogic(project);
		// 將story的中sprint id做修改
		if (sprintsId.size() != 0) {
			for (int i = 0; i < sprintsId.size(); i++) {
				if ((i + 1) != sprintsId.size()) {
					long sprintID = sprintsId.get(i);
					long nextSprintID = sprintsId.get(i + 1);
					ArrayList<StoryObject> stories = map.get(sprintID);
					if (stories != null) {
						ArrayList<Long> total = convertToLong(stories);
						productBacklogLogic.addStoriesToSprint(total, nextSprintID);
					}
				} else {
					long sprintID = sprintsId.get(i);
					long nextSprintID = sprintsId.get(0);
					ArrayList<StoryObject> stories = map.get(sprintID);
					if (stories != null) {
						ArrayList<Long> total = convertToLong(stories);
						productBacklogLogic.addStoriesToSprint(total, nextSprintID);
					}
				}
			}
		}
	}

	private ArrayList<Long> convertToLong(ArrayList<StoryObject> stories) {
		ArrayList<Long> total = new ArrayList<Long>();
		for (StoryObject story : stories) {
			total.add(story.getId());
		}
		return total;
	}

	private boolean isSprintPlan(List<ISprintPlanDesc> descs, int iteration) {
		String iter = String.valueOf(iteration);
		for (ISprintPlanDesc desc : descs) {
			if (desc.getID().equals(iter)) {
				return Boolean.TRUE;
			}
		}
		return Boolean.FALSE;
	}

	public ISprintPlanDesc loadPlan(String ID) {
		return mSprintPlanMapper.getSprintPlan(ID);
	}

	/*
	 * move from Mapper
	 */
	// move the specific sprint to other sprint
	public void moveSprint(int oldID, int newID) {
		// 移動iterPlan.xml的資訊
		mSprintPlanMapper.moveSprintPlan(oldID, newID);
	}

	public ISprintPlanDesc loadPlan(int iteration) {
		return mSprintPlanMapper.getSprintPlan(Integer.toString(iteration));
	}

	public void editSprintPlanForActualCost(String sprintID, String actualCost) {
		ISprintPlanDesc sprintPlan = loadPlan(sprintID);
		sprintPlan.setActualCost(actualCost);
		mSprintPlanMapper.updateSprintPlanForActualCost(sprintPlan);
	}

	public void createSprint(SprintObject sprint) {
		mSprintPlanMapper.addSprintPlan(ConvertSprint
				.convertSprintObjectToDesc(sprint));
	}

	public void deleteSprint(String id) {
		mSprintPlanMapper.deleteSprintPlan(id);
	}

	public void updateSprint(SprintObject sprintObject) {
		mSprintPlanMapper.updateSprintPlan(ConvertSprint
				.convertSprintObjectToDesc(sprintObject));
	}

	public List<SprintObject> getAllSprint() {
		return ConvertSprint.convertSprintDescToObject(mSprintPlanMapper
				.getSprintPlanList());
	}

	public SprintObject getSprint(String sprintId) throws SQLException {
		SprintObject sprint = new SprintObject(loadPlan(sprintId));
		// 找出 sprint 中所有的 story
		ArrayList<StoryObject> storyIIssues = mSprintBacklogMapper.getStoriesBySprintId(Long.parseLong(sprintId));
		for (StoryObject story : storyIIssues) {
			// 找出 story 中所有的 task
			ArrayList<TaskObject> tasks = mSprintBacklogMapper.getTasksByStoryId(story.getId());
			for (TaskObject task : tasks) {
				task.setStoryId(story.getId());
			}
			sprint.addStory(story);
		}
		return sprint;
	}
}
