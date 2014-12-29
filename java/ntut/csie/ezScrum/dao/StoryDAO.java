package ntut.csie.ezScrum.dao;

import java.sql.SQLException;

import ntut.csie.ezScrum.web.dataObject.StoryObject;

public class StoryDAO extends AbstractDAO<StoryObject, StoryObject> {

	private static StoryDAO sInstance = null;
	
	public static StoryDAO getInstance() {
		if (sInstance == null) {
			sInstance = new StoryDAO();
		}
		return sInstance;
	}
	
	@Override
    public long create(StoryObject infoObject) {
	    return new Long(0);
    }

	@Override
    public StoryObject get(long id) {
	    return null;
    }

	@Override
    public boolean update(StoryObject dataObject) {
	    return false;
    }

	@Override
    public boolean delete(long id) {
	    return false;
    }
}
