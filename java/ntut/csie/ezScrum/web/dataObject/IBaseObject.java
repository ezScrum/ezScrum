package ntut.csie.ezScrum.web.dataObject;

public interface IBaseObject {
	public void save();
	public void reload() throws Exception;
	public boolean delete();
}
