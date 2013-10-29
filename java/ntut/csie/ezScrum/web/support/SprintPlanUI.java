package ntut.csie.ezScrum.web.support;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import ntut.csie.ezScrum.iteration.core.ISprintPlanDesc;

public class SprintPlanUI {
	private List<SprintPlanItem> Sprints = new LinkedList<SprintPlanItem>();
	
	public SprintPlanUI(List<ISprintPlanDesc> descs) {
		if (descs != null) {
			for (ISprintPlanDesc desc : descs) {
				Sprints.add(new SprintPlanItem(desc));
			}
		} else {
			Sprints.add(new SprintPlanItem());
		}
	}
	
	private class SprintPlanItem {
		private String Id = "0";
		private String Goal = "";
		private String StartDate = "";
		private String Interval = "";
		private String Members = "";
		private String AvaliableDays = "";
		private String FocusFactor = "";
		private String DailyScrum = "";
		private String DemoDate = "";
		private String DemoPlace = "";
		private String DueDate = "";
		
		public SprintPlanItem() {
			
		}
		
		public SprintPlanItem(ISprintPlanDesc desc) {
			this.Id = desc.getID();
			this.Goal = desc.getGoal();
			this.StartDate = desc.getStartDate();
			this.Interval = desc.getInterval();
			this.DueDate  = this.calcaulateDueDate();
			this.Members = desc.getMemberNumber();
			this.AvaliableDays = desc.getAvailableDays() + " hours";
			this.FocusFactor = desc.getFocusFactor();
			this.DailyScrum = desc.getNotes();
			this.DemoDate = desc.getDemoDate();
			this.DemoPlace = desc.getDemoPlace();
		}
		/**
		 * @author ninja31312
		 * @calculate dueDate by startDate and interval
		 * */
		public  String calcaulateDueDate(){
			String dueDateString;
			int interval;
			try{
			    interval = Integer.parseInt(this.Interval);
			}catch(NumberFormatException e){
				return "";
			}
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd");
			Date startDate = new Date();
			try {
				startDate = simpleDateFormat.parse( this.StartDate );
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(startDate);
			calendar.add(Calendar.DATE, interval*7-1);
			Date dueDate = calendar.getTime();
			dueDateString = simpleDateFormat.format(dueDate);
			return dueDateString;
		}
	}
}
