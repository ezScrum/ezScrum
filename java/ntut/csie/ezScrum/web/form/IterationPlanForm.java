package ntut.csie.ezScrum.web.form;

import org.apache.struts.action.ActionForm;

public class IterationPlanForm extends ActionForm {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1636595026446471915L;
	
    private String m_iterStartDate = "";
    private String m_iterIterval = "";
    private String m_iterMemberNumber = "";
    private String m_id = "";
    private String m_focusFactor = "";
    private String m_goal ="";
    private String m_availableDays = "";
    private String m_demoDays = "";
    private String m_notes = "";
    private String m_demoPlace = "";
    
    public void reset(){
    	m_iterStartDate = "";
        m_iterIterval = "";
        m_iterMemberNumber = "";
        m_id = "";
        m_focusFactor = "";
        m_goal ="";
        m_availableDays = "";
        m_demoDays = "";
        m_notes = "";
        m_demoPlace = "";
    }

    public String getIterStartDate() {
		return m_iterStartDate;
	}
	
	public void setIterStartDate(String startDate) {
		m_iterStartDate = startDate;
	}
	
	public String getIterIterval() {
		return m_iterIterval;
	}
	
	public void setIterIterval(String iterval) {
		m_iterIterval = iterval;
	}
	
	public String getIterMemberNumber() {
		return m_iterMemberNumber;
	}
	
	public void setIterMemberNumber(String memberNumber) {
		m_iterMemberNumber = memberNumber;
	}
	
	public void setID(String id){
		m_id = id;
	}
	
	public String getID(){
		return m_id;
	}
	
	public void setFocusFactor(String factor){
		m_focusFactor = factor;
	}
	
	public String getFocusFactor(){
		return m_focusFactor;
	}
	
	public void setGoal(String goal){
		m_goal = goal;
	}
	
	public String getGoal(){
		return m_goal;
	}
	
	public void setAvailableDays(String aDays){
		this.m_availableDays = aDays;
	}
	
	public String getAvailableDays(){
		return this.m_availableDays;
	}
	
	public void setDemoDate(String demoDate){
		this.m_demoDays = demoDate;
	}
	
	public String getDemoDate(){
		return this.m_demoDays;
	}
	
	public void setNotes(String notes){
		this.m_notes = notes;
	}
	
	public String getNotes(){
		return this.m_notes;
	}
	
	public void setDemoPlace(String place){
		m_demoPlace = place;
	}
	
	public String getDemoPlace(){
		return m_demoPlace;
	}
}
