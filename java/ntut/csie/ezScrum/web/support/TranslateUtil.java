package ntut.csie.ezScrum.web.support;

import java.util.ArrayList;
import java.util.List;

import ntut.csie.jcis.account.core.IActor;
import ntut.csie.jcis.account.core.IPermission;
import ntut.csie.jcis.account.core.IRole;

public class TranslateUtil {

	/**
	 * 將所有role id轉成字串 以,隔開
	 * 
	 * @param roles
	 * @return
	 */
	static public String getRolesString(IRole[] roles) {
		String roleString = "";
		for (int i=0; i<roles.length ; i++) {
			String id = roles[i].getRoleId();
			roleString += id;
			if ((i+1) != roles.length) {
				roleString += ", ";
			}
		}
		return roleString;
	}
	
	static public String getPermissionString(IPermission[] permissions){
		String permissionString = "";
		for (int i = 0; i < permissions.length; i++){
			String permissionname = permissions[i].getPermissionName();
			permissionString += permissionname;
			if ((i + 1) != permissions.length){
				permissionString += ",";
			}
		}
		return permissionString;
	}
	static public String getPermissionStringForView(IPermission[] permissions){
		String permissionString = getPermissionString(permissions);
		permissionString = permissionString.replaceAll(",", ", ");
		return permissionString;
	}
	

	/**
	 * 將Actors中的account id以,隔開
	 * 
	 * @param actors
	 * @return
	 */
	static public String getAccountsString(IActor[] actors) {
		String actorString = "";

		for (int i = 0; i < actors.length; i++) {
			IActor actor = actors[i];
			if (actor.getType() == IActor.TYPE_ACCOUNT) {
				actorString += actor.getID() + ",";
			}
		}

		return actorString;
	}

	/**
	 * 將Actors中的group id以,隔開
	 * 
	 * @param actors
	 * @return
	 */
	static public String getGroupsString(IActor[] actors) {
		String actorString = "";

		for (int i = 0; i < actors.length; i++) {
			IActor actor = actors[i];
			if (actor.getType() == IActor.TYPE_GROUP) {
				actorString += actor.getID() + ",";
			}
		}

		return actorString;
	}

	/**
	 * 將Roles String轉成Role String List
	 * 
	 * @return
	 */
	static public List<String> translateRoleString(String roleString) {
		String[] roleArray = roleString.split(",");
		List<String> roleList = new ArrayList<String>();

		for (int i = 0; i < roleArray.length; i++) {
			String tmp = roleArray[i];
			tmp = tmp.trim();
			if (tmp.isEmpty())
				continue;
			roleList.add(roleArray[i]);
		}

		return roleList;
	}

}
