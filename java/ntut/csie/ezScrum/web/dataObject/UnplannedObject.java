package ntut.csie.ezScrum.web.dataObject;

import java.util.ArrayList;

import ntut.csie.ezScrum.dao.AccountDAO;
import ntut.csie.ezScrum.dao.HistoryDAO;
import ntut.csie.ezScrum.dao.UnplannedDAO;
import ntut.csie.ezScrum.web.databasEnum.UnplannedEnum;
import ntut.csie.ezScrum.web.databaseEnum.IssueTypeEnum;

import org.apache.commons.collections.CollectionUtils;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

/**
 * @author AllenHuang 2015/08/21
 */

public class UnplannedObject implements IBaseObject {
	public final static int STATUS_UNCHECK = 1;
	public final static int STATUS_CHECK = 2;
	public final static int STATUS_DONE = 3;
	public final static int NO_PARENT = -1;
	private final static int DEFAULT_VALUE = -1;
	private long mId = DEFAULT_VALUE;
	private long mSerialId = DEFAULT_VALUE;
	private long mProjectId = DEFAULT_VALUE;
	private long mSprintId = DEFAULT_VALUE;
	private long mHandlerId = DEFAULT_VALUE;
	private String mName = "";
	private String mNotes = "";
	private int mEstimate = 0;
	private int mActual = 0;
	private int mStatus = STATUS_UNCHECK;
	private long mCreateTime = DEFAULT_VALUE;
	private long mUpdateTime = DEFAULT_VALUE;
	
	public static UnplannedObject get(long id) {
		return UnplannedDAO.getInstance().get(id);
	}
	
	public UnplannedObject(long sprintId, long projectId) {
		mSprintId = sprintId;
		mProjectId = projectId;
	}
	
	public UnplannedObject(long id, long serialId, long projectId) {
		mId = id;
		mSerialId = serialId;
		mProjectId = projectId;
	}
	
	public UnplannedObject setName(String name) {
		mName = name;
		return this;
	}
	
	public UnplannedObject setHandlerId(long handlerId) {
		mHandlerId = handlerId;
		return this;
	}
	
	public UnplannedObject setEstimate(int estimate) {
		mEstimate = estimate;
		return this;
	}
	
	public UnplannedObject setActual(int actual) {
		mActual = actual;
		return this;
	}
	
	public UnplannedObject setNotes(String notes) {
		mNotes = handleSpecialChar(notes);
		return this;
	}

	public UnplannedObject setStatus(int status) {
		mStatus = status;
		return this;
	}

	public UnplannedObject setSprintId(long sprintId) {
		mSprintId = sprintId;
		return this;
	}

	public UnplannedObject setCreateTime(long createtime) {
		mCreateTime = createtime;
		return this;
	}

	public UnplannedObject setUpdateTime(long updatetime) {
		mUpdateTime = updatetime;
		return this;
	}
	
	@SuppressWarnings("unchecked")
	public UnplannedObject setPartnersId(ArrayList<Long> newPartnersId) {
		ArrayList<Long> oldPartnersId = getPartnersId();
		ArrayList<Long> intersectionPartnersId = (ArrayList<Long>) CollectionUtils
				.intersection(oldPartnersId, newPartnersId);
		ArrayList<Long> shouldRemovePartnersId = (ArrayList<Long>) CollectionUtils
				.subtract(oldPartnersId, intersectionPartnersId);
		ArrayList<Long> shouldAddPartnersId = (ArrayList<Long>) CollectionUtils.subtract(
				newPartnersId, intersectionPartnersId);
		for (long partnerId : shouldRemovePartnersId) {
			UnplannedDAO.getInstance().removePartner(mId, partnerId);
		}
		for (long partnerId : shouldAddPartnersId) {
			UnplannedDAO.getInstance().addPartner(mId, partnerId);
		}
		return this;
	}

	public void addPartner(long partnerId) {
		if (!UnplannedDAO.getInstance().partnerExists(mId, partnerId)) {
			UnplannedDAO.getInstance().addPartner(mId, partnerId);
		}
	}

	public void removePartner(long partnerId) {
		UnplannedDAO.getInstance().removePartner(mId, partnerId);
	}

	public long getId() {
		return mId;
	}

	public long getSerialId() {
		return mSerialId;
	}

	public String getName() {
		return mName;
	}

	public long getHandlerId() {
		return mHandlerId;
	}

	public AccountObject getHandler() {
		AccountObject handler = AccountDAO.getInstance().get(mHandlerId);
		return handler;
	}
	
	public String getHandlerName() {
		if (getHandler() == null) {
			return "";
		}
		return getHandler().getUsername();
	}

	public int getEstimate() {
		return mEstimate;
	}

	public int getActual() {
		return mActual;
	}

	public String getNotes() {
		return mNotes;
	}

	public int getStatus() {
		return mStatus;
	}

	/**
	 * 之後要拔掉,為了符合目前的 IIssue
	 */
	public String getStatusString() {
		if (mStatus == STATUS_UNCHECK) {
			return "new";
		} else if (mStatus == STATUS_CHECK) {
			return "assigned";
		} else {
			return "closed";
		}
	}

	public long getProjectId() {
		return mProjectId;
	}

	public long getSprintId() {
		return mSprintId;
	}

	public long getCreateTime() {
		return mCreateTime;
	}

	public long getUpdateTime() {
		return mUpdateTime;
	}

	public ArrayList<Long> getPartnersId() {
		ArrayList<Long> partnersId = UnplannedDAO.getInstance().getPartnersId(mId);
		return partnersId;
	}

	public ArrayList<AccountObject> getPartners() {
		ArrayList<AccountObject> partners = new ArrayList<AccountObject>();
		ArrayList<Long> partnersId = getPartnersId();
		for (long partnerId : partnersId) {
			AccountObject partner = AccountDAO.getInstance().get(partnerId);
			if (partner != null) {
				partners.add(partner);
			}
		}
		return partners;
	}

	public String getPartnersUsername() {
		StringBuilder partnersName = new StringBuilder();
		ArrayList<AccountObject> partners = getPartners();
		int partnersAmount = partners.size();
		int lastIndex = partnersAmount - 1;
		for (int i = 0; i < partnersAmount; i++) {
			partnersName.append(partners.get(i).getUsername());
			if (i != lastIndex) {
				partnersName.append(";");
			}
		}
		return partnersName.toString();
	}

	public ArrayList<HistoryObject> getHistories() {
		ArrayList<HistoryObject> histories = HistoryDAO.getInstance()
				.getHistoriesByIssue(mId, IssueTypeEnum.TYPE_UNPLANNED);
		return histories;
	}

	@Override
	public JSONObject toJSON() throws JSONException {
		JSONObject unplanned = new JSONObject();
		JSONArray partners = new JSONArray();
		JSONArray histories = new JSONArray();

		for (AccountObject partner : getPartners()) {
			partners.put(partner.toJSON());
		}

		for (HistoryObject history : getHistories()) {
			histories.put(history.toJSON());
		}
		
		unplanned.put(UnplannedEnum.NAME, mName)
				.put(UnplannedEnum.ESTIMATE, mEstimate)
				.put(UnplannedEnum.ACTUAL, mActual)
				.put(UnplannedEnum.SPRINT_ID, mSprintId)
				.put(UnplannedEnum.PROJECT_ID, mProjectId)
				.put(UnplannedEnum.NOTES, mNotes)
				.put(UnplannedEnum.STATUS, mStatus)
				.put(UnplannedEnum.SERIAL_ID, mSerialId)
				.put(UnplannedEnum.ID, mId)
				.put(UnplannedEnum.CREATE_TIME, mCreateTime)
				.put(UnplannedEnum.UPDATE_TIME, mUpdateTime)
				.put("partners", partners)
				.put("histories", histories);

		if (getHandler() != null) {
			unplanned.put(UnplannedEnum.HANDLER, getHandler().toJSON());
		} else {
			unplanned.put(UnplannedEnum.HANDLER, new JSONObject());
		}
		
		return unplanned;
	}

	public String toString() {
		try {
			return toJSON().toString();
		} catch (JSONException e) {
			return "JSON Exception";
		}
	}

	@Override
	public void save() {
		if (exists()) {
			mUpdateTime = System.currentTimeMillis();
			doUpdate();
		} else {
			doCreate();
		}
	}

	/**
	 * Update time will equal to parameter you passed.
	 * 
	 * @param specificTime
	 */
	public void save(long specificTime) {
		if (exists()) {
			mUpdateTime = specificTime;
			doUpdate(specificTime);
		} else {
			doCreate();
		}
	}

	@Override
	public void reload() {
		if (exists()) {
			UnplannedObject unplanned = UnplannedDAO.getInstance().get(mId);
			resetData(unplanned);
		}
	}

	@Override
	public boolean delete() {
		HistoryDAO.getInstance().deleteByIssue(mId,	IssueTypeEnum.TYPE_UNPLANNED);
		boolean success = UnplannedDAO.getInstance().delete(mId);
		if (success) {
			mId = DEFAULT_VALUE;
			mSerialId = DEFAULT_VALUE;
		}
		return success;
	}

	@Override
	public boolean exists() {
		UnplannedObject unplanned = UnplannedDAO.getInstance().get(mId);
		return unplanned != null;
	}

	private void resetData(UnplannedObject unplanned) {
		mId = unplanned.getId();
		mSerialId = unplanned.getSerialId();
		mProjectId = unplanned.getProjectId();
		setName(unplanned.getName());
		setNotes(unplanned.getNotes());
		setSprintId(unplanned.getSprintId());
		setEstimate(unplanned.getEstimate());
		setActual(unplanned.getActual());
		setHandlerId(unplanned.getHandlerId());
		setStatus(unplanned.getStatus());
		setCreateTime(unplanned.getCreateTime());
		setUpdateTime(unplanned.getUpdateTime());
	}

	private void doCreate() {
		mActual = mEstimate;
		
		mId = UnplannedDAO.getInstance().create(this);
		// 為了拿到 update time 來新增 history, 所以需要 reload 一次從 DB 拿回時間
		reload();
		HistoryObject history = new HistoryObject(mId, IssueTypeEnum.TYPE_UNPLANNED,
				HistoryObject.TYPE_CREATE, "", "", mCreateTime);
		history.save();
	}

	private void doUpdate() {
		UnplannedObject oldUnplanned = UnplannedObject.get(mId);

		UnplannedDAO.getInstance().update(this);
		if (!mName.equals(oldUnplanned.getName())) {
			addHistory(HistoryObject.TYPE_NAME, oldUnplanned.getName(), mName);
		}
		if (!mNotes.equals(oldUnplanned.getNotes())) {
			addHistory(HistoryObject.TYPE_NOTE, oldUnplanned.getNotes(), mNotes);
		}
		if (mStatus != oldUnplanned.getStatus()) {
			addHistory(HistoryObject.TYPE_STATUS, oldUnplanned.getStatus(), mStatus);
		}
		if (mEstimate != oldUnplanned.getEstimate()) {
			addHistory(HistoryObject.TYPE_ESTIMATE, oldUnplanned.getEstimate(),
					mEstimate);
		}
		if (mActual != oldUnplanned.getActual()) {
			addHistory(HistoryObject.TYPE_ACTUAL, oldUnplanned.getActual(), mActual);
		}
		if (mSprintId != oldUnplanned.getSprintId()) {
			addHistory(HistoryObject.TYPE_SPRINT_ID, oldUnplanned.getSprintId(), mSprintId);
		}
		if (mHandlerId != oldUnplanned.getHandlerId()) {
			addHistory(HistoryObject.TYPE_HANDLER, oldUnplanned.getHandlerId(),
					mHandlerId);
		}
	}

	// for specific time update
	private void doUpdate(long specificTime) {
		UnplannedObject oldUnplanned = UnplannedObject.get(mId);
		
		UnplannedDAO.getInstance().update(this);
		if (!mName.equals(oldUnplanned.getName())) {
			addHistory(HistoryObject.TYPE_NAME, oldUnplanned.getName(), mName,
					specificTime);
		}
		if (!mNotes.equals(oldUnplanned.getNotes())) {
			addHistory(HistoryObject.TYPE_NOTE, oldUnplanned.getNotes(), mNotes,
					specificTime);
		}
		if (mEstimate != oldUnplanned.getEstimate()) {
			addHistory(HistoryObject.TYPE_ESTIMATE, oldUnplanned.getEstimate(),
					mEstimate, specificTime);
		}
		if (mActual != oldUnplanned.getActual()) {
			addHistory(HistoryObject.TYPE_ACTUAL, oldUnplanned.getActual(), mActual,
					specificTime);
		}
		if (mStatus != oldUnplanned.getStatus()) {
			addHistory(HistoryObject.TYPE_STATUS, oldUnplanned.getStatus(), mStatus,
					specificTime);
		}
		if (mSprintId != oldUnplanned.getSprintId()) {
			addHistory(HistoryObject.TYPE_SPRINT_ID, oldUnplanned.getSprintId(), mSprintId,
					specificTime);
		}
		if (mHandlerId != oldUnplanned.getHandlerId()) {
			addHistory(HistoryObject.TYPE_HANDLER, oldUnplanned.getHandlerId(),
					mHandlerId, specificTime);
		}
	}

	private void addHistory(int type, long oldValue, long newValue) {
		addHistory(type, String.valueOf(oldValue), String.valueOf(newValue));
	}

	// add history for specific time
	private void addHistory(int type, long oldValue, long newValue,
			long specificTime) {
		addHistory(type, String.valueOf(oldValue), String.valueOf(newValue),
				specificTime);
	}

	private void addHistory(int type, String oldValue, String newValue) {
		HistoryObject history = new HistoryObject(mId, IssueTypeEnum.TYPE_UNPLANNED,
				type, oldValue, newValue, System.currentTimeMillis());
		history.save();
	}

	// add history for specific time
	private void addHistory(int type, String oldValue, String newValue,
			long specificTime) {
		HistoryObject history = new HistoryObject(mId, IssueTypeEnum.TYPE_UNPLANNED,
				type, oldValue, newValue, specificTime);
		history.save();
	}
	
	private String handleSpecialChar(String str) {
		if (str.contains("\n")) {
			str = str.replaceAll("\n", "<br/>");
		}
		return str;
	}
}
