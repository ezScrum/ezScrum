package ntut.csie.ezScrum.web.dataObject;

import java.util.ArrayList;

import ntut.csie.ezScrum.dao.AccountDAO;
import ntut.csie.ezScrum.dao.HistoryDAO;
import ntut.csie.ezScrum.dao.UnplanDAO;
import ntut.csie.ezScrum.web.databaseEnum.IssueTypeEnum;
import ntut.csie.ezScrum.web.databaseEnum.UnplanEnum;

import org.apache.commons.collections.CollectionUtils;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

/**
 * @author AllenHuang 2015/08/21
 */

public class UnplanObject implements IBaseObject {
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
	private ArrayList<Long> mPartnersId = new ArrayList<>();
	private String mName = "";
	private String mNotes = "";
	private int mEstimate = 0;
	private int mActual = 0;
	private int mStatus = STATUS_UNCHECK;
	private long mCreateTime = DEFAULT_VALUE;
	private long mUpdateTime = DEFAULT_VALUE;
	
	public static UnplanObject get(long id) {
		return UnplanDAO.getInstance().get(id);
	}
	
	public UnplanObject(long sprintId, long projectId) {
		mSprintId = sprintId;
		mProjectId = projectId;
	}
	
	public UnplanObject(long id, long serialId, long projectId) {
		mId = id;
		mSerialId = serialId;
		mProjectId = projectId;
	}
	
	public UnplanObject setName(String name) {
		mName = name;
		return this;
	}
	
	public UnplanObject setHandlerId(long handlerId) {
		mHandlerId = handlerId;
		return this;
	}
	
	public UnplanObject setEstimate(int estimate) {
		mEstimate = estimate;
		return this;
	}
	
	public UnplanObject setActual(int actual) {
		mActual = actual;
		return this;
	}
	
	public UnplanObject setNotes(String notes) {
		mNotes = handleSpecialChar(notes);
		return this;
	}

	public UnplanObject setStatus(int status) {
		mStatus = status;
		return this;
	}

	public UnplanObject setSprintId(long sprintId) {
		mSprintId = sprintId;
		return this;
	}

	public UnplanObject setCreateTime(long createtime) {
		mCreateTime = createtime;
		return this;
	}

	public UnplanObject setUpdateTime(long updatetime) {
		mUpdateTime = updatetime;
		return this;
	}
	
	public UnplanObject setPartnersId(ArrayList<Long> newPartnersId) {
		mPartnersId = newPartnersId;
		return this;
	}

	public void addPartner(long partnerId) {
		AccountObject partner = AccountObject.get(partnerId);
		if (partner != null && !UnplanDAO.getInstance().partnerExists(mId, partnerId)) {
			UnplanDAO.getInstance().addPartner(mId, partnerId);
		}
	}

	public void removePartner(long partnerId) {
		UnplanDAO.getInstance().removePartner(mId, partnerId);
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
		ArrayList<Long> partnersId = UnplanDAO.getInstance().getPartnersId(mId);
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
				.getHistoriesByIssue(mId, IssueTypeEnum.TYPE_UNPLAN);
		return histories;
	}

	@Override
	public JSONObject toJSON() throws JSONException {
		JSONObject unplan = new JSONObject();
		JSONArray partners = new JSONArray();
		JSONArray histories = new JSONArray();

		for (AccountObject partner : getPartners()) {
			partners.put(partner.toJSON());
		}

		for (HistoryObject history : getHistories()) {
			histories.put(history.toJSON());
		}
		
		unplan.put(UnplanEnum.NAME, mName)
				.put(UnplanEnum.ESTIMATE, mEstimate)
				.put(UnplanEnum.ACTUAL, mActual)
				.put(UnplanEnum.SPRINT_ID, mSprintId)
				.put(UnplanEnum.PROJECT_ID, mProjectId)
				.put(UnplanEnum.NOTES, mNotes)
				.put(UnplanEnum.STATUS, mStatus)
				.put(UnplanEnum.SERIAL_ID, mSerialId)
				.put(UnplanEnum.ID, mId)
				.put(UnplanEnum.CREATE_TIME, mCreateTime)
				.put(UnplanEnum.UPDATE_TIME, mUpdateTime)
				.put("partners", partners)
				.put("histories", histories);

		if (getHandler() != null) {
			unplan.put(UnplanEnum.HANDLER, getHandler().toJSON());
		} else {
			unplan.put(UnplanEnum.HANDLER, new JSONObject());
		}
		
		return unplan;
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
			UnplanObject unplan = UnplanDAO.getInstance().get(mId);
			resetData(unplan);
		}
	}

	@Override
	public boolean delete() {
		HistoryDAO.getInstance().deleteByIssue(mId,	IssueTypeEnum.TYPE_UNPLAN);
		boolean success = UnplanDAO.getInstance().delete(mId);
		if (success) {
			mId = DEFAULT_VALUE;
			mSerialId = DEFAULT_VALUE;
		}
		return success;
	}

	@Override
	public boolean exists() {
		UnplanObject unplan = UnplanDAO.getInstance().get(mId);
		return unplan != null;
	}

	private void resetData(UnplanObject unplan) {
		mId = unplan.getId();
		mSerialId = unplan.getSerialId();
		mProjectId = unplan.getProjectId();
		setName(unplan.getName());
		setNotes(unplan.getNotes());
		setSprintId(unplan.getSprintId());
		setEstimate(unplan.getEstimate());
		setActual(unplan.getActual());
		setHandlerId(unplan.getHandlerId());
		setStatus(unplan.getStatus());
		setCreateTime(unplan.getCreateTime());
		setUpdateTime(unplan.getUpdateTime());
	}

	private void doCreate() {
		mActual = mEstimate;
		
		mId = UnplanDAO.getInstance().create(this);
		for (long partnerId : mPartnersId) {
			addPartner(partnerId);
		}
		// 為了拿到 update time 來新增 history, 所以需要 reload 一次從 DB 拿回時間
		reload();
		HistoryObject history = new HistoryObject(mId, IssueTypeEnum.TYPE_UNPLAN,
				HistoryObject.TYPE_CREATE, "", "", mCreateTime);
		history.save();
	}

	private void doUpdate() {
		UnplanObject oldUnplan = UnplanObject.get(mId);

		UnplanDAO.getInstance().update(this);
		if (!mName.equals(oldUnplan.getName())) {
			addHistory(HistoryObject.TYPE_NAME, oldUnplan.getName(), mName);
		}
		if (!mNotes.equals(oldUnplan.getNotes())) {
			addHistory(HistoryObject.TYPE_NOTE, oldUnplan.getNotes(), mNotes);
		}
		if (mStatus != oldUnplan.getStatus()) {
			addHistory(HistoryObject.TYPE_STATUS, oldUnplan.getStatus(), mStatus);
		}
		if (mEstimate != oldUnplan.getEstimate()) {
			addHistory(HistoryObject.TYPE_ESTIMATE, oldUnplan.getEstimate(),
					mEstimate);
		}
		if (mActual != oldUnplan.getActual()) {
			addHistory(HistoryObject.TYPE_ACTUAL, oldUnplan.getActual(), mActual);
		}
		if (mSprintId != oldUnplan.getSprintId()) {
			addHistory(HistoryObject.TYPE_SPRINT_ID, oldUnplan.getSprintId(), mSprintId);
		}
		if (mHandlerId != oldUnplan.getHandlerId()) {
			addHistory(HistoryObject.TYPE_HANDLER, oldUnplan.getHandlerId(),
					mHandlerId);
		}
		
		setPartnersAndHistory(System.currentTimeMillis());
	}

	// for specific time update
	private void doUpdate(long specificTime) {
		UnplanObject oldUnplan = UnplanObject.get(mId);
		
		UnplanDAO.getInstance().update(this);
		if (!mName.equals(oldUnplan.getName())) {
			addHistory(HistoryObject.TYPE_NAME, oldUnplan.getName(), mName,
					specificTime);
		}
		if (!mNotes.equals(oldUnplan.getNotes())) {
			addHistory(HistoryObject.TYPE_NOTE, oldUnplan.getNotes(), mNotes,
					specificTime);
		}
		if (mEstimate != oldUnplan.getEstimate()) {
			addHistory(HistoryObject.TYPE_ESTIMATE, oldUnplan.getEstimate(),
					mEstimate, specificTime);
		}
		if (mActual != oldUnplan.getActual()) {
			addHistory(HistoryObject.TYPE_ACTUAL, oldUnplan.getActual(), mActual,
					specificTime);
		}
		if (mStatus != oldUnplan.getStatus()) {
			addHistory(HistoryObject.TYPE_STATUS, oldUnplan.getStatus(), mStatus,
					specificTime);
		}
		if (mSprintId != oldUnplan.getSprintId()) {
			addHistory(HistoryObject.TYPE_SPRINT_ID, oldUnplan.getSprintId(), mSprintId,
					specificTime);
		}
		if (mHandlerId != oldUnplan.getHandlerId()) {
			addHistory(HistoryObject.TYPE_HANDLER, oldUnplan.getHandlerId(),
					mHandlerId, specificTime);
		}
		
		setPartnersAndHistory(specificTime);
	}
	
	private void setPartnersAndHistory(long specificTime) {
		ArrayList<Long> oldPartnersId = getPartnersId();
		@SuppressWarnings("unchecked")
		ArrayList<Long> intersectionPartnersId = (ArrayList<Long>) CollectionUtils
				.intersection(oldPartnersId, mPartnersId);
		@SuppressWarnings("unchecked")
		ArrayList<Long> shouldRemovePartnersId = (ArrayList<Long>) CollectionUtils
				.subtract(oldPartnersId, intersectionPartnersId);
		@SuppressWarnings("unchecked")
		ArrayList<Long> shouldAddPartnersId = (ArrayList<Long>) CollectionUtils.subtract(
				mPartnersId, intersectionPartnersId);
		for (long partnerId : shouldRemovePartnersId) {
			removePartner(partnerId);
			addHistory(HistoryObject.TYPE_REMOVE_PARTNER, "", String.valueOf(partnerId), specificTime);
		}
		for (long partnerId : shouldAddPartnersId) {
			addPartner(partnerId);
			addHistory(HistoryObject.TYPE_ADD_PARTNER, "", String.valueOf(partnerId), specificTime);
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
		HistoryObject history = new HistoryObject(mId, IssueTypeEnum.TYPE_UNPLAN,
				type, oldValue, newValue, System.currentTimeMillis());
		history.save();
	}

	// add history for specific time
	private void addHistory(int type, String oldValue, String newValue,
			long specificTime) {
		HistoryObject history = new HistoryObject(mId, IssueTypeEnum.TYPE_UNPLAN,
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
