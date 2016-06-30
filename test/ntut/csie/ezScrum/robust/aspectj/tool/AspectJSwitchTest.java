package ntut.csie.ezScrum.robust.aspectj.tool;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import ntut.csie.ezScrum.robust.aspectj.tool.AspectJSwitch;

public class AspectJSwitchTest {
	@Test
	public void testTurnOnByActionName() {
		String actionName = "ShowEditUnplanItemAction";
		assertFalse(AspectJSwitch.getInstance().isSwitchOn(actionName));
		AspectJSwitch.getInstance().turnOnByActionName(actionName);
		assertTrue(AspectJSwitch.getInstance().isSwitchOn(actionName));
		AspectJSwitch.getInstance().turnOff();
		assertFalse(AspectJSwitch.getInstance().isSwitchOn(actionName));
	}
	
	@Test
	public void testTurnOnByActionName_WithEmptyString() {
		String actionName = "";
		assertFalse(AspectJSwitch.getInstance().isSwitchOn(actionName));
		AspectJSwitch.getInstance().turnOnByActionName(actionName);
		assertFalse(AspectJSwitch.getInstance().isSwitchOn(actionName));
		AspectJSwitch.getInstance().turnOff();
		assertFalse(AspectJSwitch.getInstance().isSwitchOn(actionName));
	}
	
	@Test
	public void testTurnOnByActionName_WithNull() {
		String actionName = null;
		assertFalse(AspectJSwitch.getInstance().isSwitchOn(actionName));
		AspectJSwitch.getInstance().turnOnByActionName(actionName);
		assertFalse(AspectJSwitch.getInstance().isSwitchOn(actionName));
		AspectJSwitch.getInstance().turnOff();
		assertFalse(AspectJSwitch.getInstance().isSwitchOn(actionName));
	}
}
