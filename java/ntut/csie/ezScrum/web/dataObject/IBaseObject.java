package ntut.csie.ezScrum.web.dataObject;

import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;

public interface IBaseObject {
	public void save();
	public void reload();
	public boolean delete();
	public boolean exists();
	public JSONObject toJSON() throws JSONException;
}
