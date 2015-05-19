package ntut.csie.ezScrum.test;

import java.security.MessageDigest;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import ntut.csie.ezScrum.pic.core.IUserSession;
import ntut.csie.ezScrum.web.dataObject.ProjectObject;
// import ntut.csie.ezScrum.web.dataObject.SprintBacklogDateColumn;
import ntut.csie.ezScrum.web.logic.SprintBacklogLogic;
import ntut.csie.jcis.account.core.IRole;
import ntut.csie.jcis.core.util.DateUtil;

public class TestTool {

	public String getRole(String res, String op) {
		return res + "_" + op;
	}

	//轉成MD5
	public String getMd5(String str) {
		MessageDigest md = null;
		try {
			md = MessageDigest.getInstance("MD5");
		} catch (Exception e) {
			e.printStackTrace();
		}
		md.update(str.getBytes());
		byte[] b = md.digest();
		str = byte2hex(b);
		return str;
	}

	//轉換
	private String byte2hex(byte[] b) {
		String hs = "";
		String stmp = "";
		for (int n = 0; n < b.length; n++) {
			stmp = (Integer.toHexString(b[n] & 0Xff));
			if (stmp.length() == 1) {
				hs = hs + "0" + stmp;
			} else {
				hs = hs + stmp;
			}
		}
		return hs;
	}

	public String getRolesString(IRole[] roles) {
		String[] roleArr = this.sortRoleID(roles);

		String roleString = "";
		for (int i = 0; i < roleArr.length; i++) {
			roleString += roleArr[i];
			if ((i + 1) != roleArr.length) {
				roleString += ", ";
			}
		}
		return roleString;
	}

	private String[] sortRoleID(IRole[] roles) {
		String[] Role_ID = new String[roles.length];
		for (int i = 0; i < roles.length; i++) {
			Role_ID[i] = roles[i].getRoleId();
		}
		// 利用 Rold ID 抽出來後排序，再做比對
		Arrays.sort(Role_ID);

		return Role_ID;
	}

	/**
	 * 計算該Sprint的總天數，不包含週末。
	 * 
	 * @param project
	 * @param userSession
	 * @return
	 */
	public List<String> getSprintDate(ProjectObject project, IUserSession userSession) {
		//		SprintBacklogMapper m_backlog = new SprintBacklogMapper(project, userSession);
		// Sprint的起始與結束日期資訊
		//		Date iter_Start_Work_Date = m_backlog.getSprintStartWorkDate();
		//		Date iter_End_Work_Date = m_backlog.getSprintEndWorkDate();
		// Sprint的起始與結束日期資訊
		//		SprintBacklogMapper m_backlog = new SprintBacklogMapper(project, userSession);
		SprintBacklogLogic sprintBacklogLogic = new SprintBacklogLogic(project, -1);
		Date iter_Start_Work_Date = sprintBacklogLogic.getSprintStartWorkDate();
		Date iter_End_Work_Date = sprintBacklogLogic.getSprintEndWorkDate();

		Calendar indexDate = Calendar.getInstance();
		indexDate.setTime(iter_Start_Work_Date);
		long endTime = iter_End_Work_Date.getTime();

		List<String> workDateList = new ArrayList<String>();
		DateFormat dateFormate = new SimpleDateFormat("yyyy/MM/dd");
		while (!(indexDate.getTimeInMillis() > endTime) || indexDate.getTimeInMillis() == endTime) {
			Date key = indexDate.getTime();
			// 扣除假日
			if (!DateUtil.isHoliday(key)) {
				workDateList.add(dateFormate.format(key));
			}
			indexDate.add(Calendar.DATE, 1);
		}
		return workDateList;
	}

	/**
	 * 計算 Story 的理想線 理想線直線方程式 y = - (起始點數 / 總天數) * 第幾天 + 起始點數
	 * 
	 * @param totalStoryPoints
	 * @param totalWorkDates
	 * @return
	 */
	public List<String> getStoryIdealLinePoint(int totalWorkDates, double totalStoryPoints) {
		List<String> idealLinePoints = new ArrayList<String>();
		idealLinePoints.add(String.valueOf(totalStoryPoints));
		for (int i = 1; i <= totalWorkDates; i++) {
			double points = (((-totalStoryPoints) / (totalWorkDates)) * i) + totalStoryPoints;
			idealLinePoints.add(String.valueOf(points));
		}
		return idealLinePoints;
	}

	/**
	 * 計算 Task 的理想線 理想線直線方程式 y = - (起始點數 / 總天數) * 第幾天 + 起始點數
	 * 
	 * @param totalWorkDates
	 * @param totalTaskPoints
	 * @return
	 */
	public List<String> getTaskIdealLinePoint(int totalWorkDates, double totalTaskPoints) {
		List<String> idealLinePoints = new ArrayList<String>();
		idealLinePoints.add(String.valueOf(totalTaskPoints));
		for (int i = 1; i <= totalWorkDates; i++) {
			double points = (((-totalTaskPoints) / (totalWorkDates)) * i) + totalTaskPoints;
			idealLinePoints.add(String.valueOf(points));
		}
		return idealLinePoints;
	}

	/**
	 * 取得該Sprint的最後一天，不包含週末。
	 * 
	 * @param dueDate
	 * @return
	 */
	public Date getSprintEndDate(String inter, Date startDate) {
		String dueDate = this.calcaulateDueDate(inter, startDate);
		Date endDate = DateUtil.nearWorkDate(DateUtil.dayFilter(dueDate), DateUtil.FRONT_DIRECTION);
		return endDate;
	}

	public Date getSprintStartDate(String inter, Date date) {
		Date startDate = DateUtil.nearWorkDate(DateUtil.dayFilter(date), DateUtil.BACK_DIRECTION);
		return startDate;
	}

	public String transformDate(Date startDate) {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd");
		String startDateStr;
		startDateStr = simpleDateFormat.format(startDate.getTime());
		return startDateStr;
	}

	/**
	 * 計算該Sprint的最後一天，包含週末。
	 * 
	 * @param dueDate
	 * @return
	 */
	public String calcaulateDueDate(String inter, Date startDate) {
		String dueDateString;
		int interval;
		try {
			interval = Integer.parseInt(inter);
		} catch (NumberFormatException e) {
			return "";
		}
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd");
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(startDate);
		calendar.add(Calendar.DATE, interval * 7 - 1);
		Date dueDate = calendar.getTime();
		dueDateString = simpleDateFormat.format(dueDate);
		return dueDateString;
	}

	/**
	 * 根據開始日期和可工作天數，計算出data column上的日期。
	 * 
	 * @param startDate
	 * @param availableDays
	 * @return
	 */
	public List<String> getDateList(Date startDate, int availableDays) throws ParseException {
		List<String> dates = new ArrayList<String>();

		Calendar cal = Calendar.getInstance();
		cal.setTime(startDate);		// 設定為現在時間

		while (availableDays-- > 0) {
			while (DateUtil.isHoliday(cal.getTime())) {	// 判斷假日
				cal.add(Calendar.DATE, 1);		// 跳過此一工作天
			}

			SimpleDateFormat format = new SimpleDateFormat("MMM d, yyyy", Locale.US);
			String date = format.format(cal.getTime());

			dates.add(date);
			cal.add(Calendar.DATE, 1);			// 加一工作天
		}

		return dates;
	}
}
