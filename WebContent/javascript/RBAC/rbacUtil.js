//temp: a list , id: compared string
//Use to check the id in the temp list
function isSelectedElement(temp,id){	
	for( var i = 0 ; i < temp.length ; i++ ){
		if( temp[i] == id ){
		return true;			
		}
	}
	return false;	
}

//check box id & name
var roleCheckBoxName = "roleCheckBox",
	amCheckBoxName = "amCheckBox",
	gmCheckBoxName = "gmCheckBox",
	permissionCheckBoxName = "permissionCheckBox",
	roleParentCheckBoxName = "roleParentCheckBox",
	roleChildCheckBoxName = "roleChildCheckBox";
	permissionResourceCheckBoxName = "permissionResourceCheckBox";
	permissionOperationCheckBoxName = "permissionOperationCheckBox";

//Translate the selected roles to a String
function getRoleString(){
	var roleString = "";
	var rolecbs = document.getElementsByName(roleCheckBoxName);
	for( var i = 0 ; i < rolecbs.length ; i++ ){
		if( rolecbs[i].checked ){
			roleString += rolecbs[i].value + ",";
		}	
	}
	return roleString;
}

function getParentsString(){
	var parentsStr = "";
	
	var parentscbs = document.getElementsByName(roleParentCheckBoxName);
	for(var i = 0; i < parentscbs.length; i++){
		if(parentscbs[i].checked){
			parentsStr += parentscbs[i].value + ",";
		}
	}
	return parentsStr;
}

function getChildrenString(){
	var childrenStr = "";
	var childrencbs = document.getElementsByName(roleChildCheckBoxName);
	for(var i = 0; i < childrencbs.length; i++){
		if(childrencbs[i].checked){
			childrenStr += childrencbs[i].value + ",";
		}
	}
	return childrenStr;
}

function getPermissionString(){
	var permissionString = "";
	
	var permissioncbs = document.getElementsByName(permissionCheckBoxName);

	for(var i = 0; i < permissioncbs.length; i++){
		if(permissioncbs[i].checked){
			permissionString += permissioncbs[i].value + ",";
		}
	}

	return permissionString;
}

function getOperationString(){
	var operationStr = "";
	var operationcbs = document.getElementsByName(permissionOperationCheckBoxName);
	for(var i = 0; i < operationcbs.length; i++){
		if(operationcbs[i].checked){
			operationStr += operationcbs[i].value;
		}
	}
	
	return operationStr;
}

function getResourceString(){
	var resourceStr = "";
	
	var resourcecbs = document.getElementsByName(permissionResourceCheckBoxName);
	
	for(var i = 0; i < resourcecbs.length; i++){
		if(resourcecbs[i].checked){
			resourceStr += resourcecbs[i].value;
		}
	}
	
	return resourceStr;
}

//Translate the selected account member to a String
function getAMString(){
	var amString = "";
	var amcbs = document.getElementsByName(amCheckBoxName);
	for( var i = 0 ; i < amcbs.length ; i++ ){
		if( amcbs[i].checked ){
			amString += amcbs[i].value + ",";
		}	
	}
	return amString;
}

//Translate the selected group member to a String
function getGMString(){
	var gmString = "";
	var gmcbs = document.getElementsByName(gmCheckBoxName);
	for( var i = 0 ; i < gmcbs.length ; i++ ){
		if( gmcbs[i].checked ){
			gmString += gmcbs[i].value + ",";
		}	
	}
	return gmString;
}

function showCheckImg(labelId,state){
	var label = $(labelId);
	if( state == true ){
		label.innerHTML = "<img src='images/check.png'/>";
	} else {
		label.innerHTML = "<img src='images/drop2.png'/>";
	}
}