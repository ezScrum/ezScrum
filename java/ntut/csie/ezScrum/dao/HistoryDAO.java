package ntut.csie.ezScrum.dao;

import ntut.csie.ezScrum.web.dataObject.HistoryObject;

public class HistoryDAO extends AbstractDAO<HistoryObject, HistoryObject> {
	
	public HistoryDAO getInstance() {
		if (sInstance == null) {
			sInstance = new HistoryDAO();
		}
		return (HistoryDAO) sInstance;
	}

	@Override
    public long add(HistoryObject objectInfo) {
	    // TODO Auto-generated method stub
	    return 0;
    }

	@Override
    public HistoryObject get(long id) {
	    // TODO Auto-generated method stub
	    return null;
    }

	@Override
    public boolean edit(HistoryObject object) {
	    // TODO Auto-generated method stub
	    return false;
    }

	@Override
    public boolean delete(long id) {
	    // TODO Auto-generated method stub
	    return false;
    }

}
