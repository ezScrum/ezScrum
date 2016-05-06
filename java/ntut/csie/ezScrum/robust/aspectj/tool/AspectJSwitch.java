package ntut.csie.ezScrum.robust.aspectj.tool;

public class AspectJSwitch {
	private static AspectJSwitch sInstance = null;
	private String mInjectingActionName = null;
	
	public static AspectJSwitch getInstance() {
		if(sInstance == null){
			sInstance = new AspectJSwitch();
		}
		return sInstance;
	}
	
	public boolean isSwitchOn(String actionName) {
		if (mInjectingActionName == null || mInjectingActionName.isEmpty() || actionName == null || actionName.isEmpty()) {
			return false;
		}
		return mInjectingActionName.equals(actionName);
	}
	
	public void turnOnByActionName(String actionName) {
		if (actionName != null && !actionName.isEmpty()) {
			mInjectingActionName = actionName;
		}
	}
	
	public void turnOff() {
		mInjectingActionName = null;
	}
}
