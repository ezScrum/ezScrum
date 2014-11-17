package ntut.csie.ezScrum.dao;

import java.sql.SQLException;

import ntut.csie.ezScrum.web.dataObject.StoryObject;

public class StoryDAO extends AbstractDAO<StoryObject, StoryObject> {

	public static StoryDAO getInstance() {
		if (sInstance == null) {
			sInstance = new StoryDAO();
		}
		return (StoryDAO) sInstance;
	}
	
	@Override
    public long add(StoryObject infoObject) {
	    return 0;
    }

	@Override
    public StoryObject get(long id) throws SQLException {
	    return null;
    }

	@Override
    public boolean edit(StoryObject dataObject) {
	    return false;
    }

	@Override
    public boolean delete(long id) {
	    return false;
    }
}
