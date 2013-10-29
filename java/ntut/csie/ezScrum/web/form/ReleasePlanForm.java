package ntut.csie.ezScrum.web.form;

import org.apache.struts.action.ActionForm;

public class ReleasePlanForm extends ActionForm {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1636595026446471915L;
	
    private String m_importance = "";
    private String m_description = "";
    private String m_color = "";
    private String m_id = "";

    
    public void reset(){
    	m_importance = "";
    	m_description = "";
    	m_color = "";
        m_id = "";
    }

    public String getImportance() {
		return m_importance;
	}
	
	public void setImportance(String importance) {
		m_importance = importance;
	}
	
	public String getDescription() {
		return m_description;
	}
	
	public void setDescription(String Description) {
		m_description = Description;
	}
	
	public String getColor() {
		return m_color;
	}
	
	public void setColor(String color) {
		m_color = color;
	}
	
	public void setID(String id){
		m_id = id;
	}
	
	public String getID(){
		return m_id;
	}
	
}
