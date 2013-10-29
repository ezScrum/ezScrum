
//'Sequential','Parallel','Work'
//根據WorkSet的Type顯示不同的圖片
function getIMG(type){
	return type + ".jpg";
}

//Show the pop mune when the user click the right button on tree
var removeID = "";
function onRightClick(id,event){
	this.selectItem(id,false,true);
	var linkset = "";
	//根據work的型態顯示pop menu內容
	if( isGroupID(id) ){
		linkset="<a href=\"javascript:createWorkGroup(" + id + ",'PARALLEL_GROUP')\">Create Parallel Group</a>";
		linkset+="<a href=\"javascript:createWorkGroup(" + id + ",'SEQUENCE_GROUP')\">Create Sequence Group</a>";
		linkset+="<a href=\"javascript:showCreateBuilderWorkTable(" + id + ")\">Create Builder Work</a>";		
		linkset+="<a href=\"javascript:switchGroupType(" + id + ")\">Switch Group Type</a>";		
		linkset+="<a href=\"javascript:removeWorkSet()\">Remove</a>";
	} else if( id == "root"){
		if( tree.getAllSubItems(id) == null ||
			tree.getAllSubItems(id) == "" ){
			linkset="<a href=\"javascript:createWorkGroup('" + id + "','PARALLEL_GROUP')\">Create Parallel Group</a>";
			linkset+="<a href=\"javascript:createWorkGroup('" + id + "','SEQUENCE_GROUP')\">Create Sequence Group</a>";
			linkset+="<a href=\"javascript:showCreateBuilderWorkTable('" + id + "')\">Create Builder Work</a>";		
		} else {
			return;
		}
	} else {
		linkset="<a href=\"javascript:removeWorkSet()\">Remove</a>";
	}
	removeID = id;
	showmenu(event,linkset);
}

//Switch the work group type
function switchGroupType(id){
	new Ajax.Request('switchWorkGroupType.do',
 	{
 		parameters: { id : id },
    	onSuccess: function(transport){
      		var work = transport.responseXML.documentElement.getElementsByTagName("work")[0];
      		var text = work.getAttribute('text');
      		var type = work.getAttribute('type');
      		var img;
      		if( type == WorkGroupType[0] ){
      			img = "flat.gif";
      		} else {
      			img = "hierarchical.gif";
      		}
      		tree.setItemText(id,text);
      		tree.setItemImage2(id,img,img,img);
    	},
		onFailure: function(){
			window.alert("與Server連線發生問題, 請重新再試.");
		}
  	});		
}

//Remove the WorkSet by Ajax
function removeWorkSet(){
	var cresult = window.confirm("確認刪除?");
	
	if( cresult == false ) {
		removeID = "";
		return;
	}
	var rid = removeID; //Assign成local確保在與Server連線時不會被初始化或修改
	new Ajax.Request('removeBuilderWorkSet.do',
 	{
 		parameters: { id : rid },
    	onSuccess: function(transport){
      		var result = transport.responseText;
      		if( result == "true" ){
      			tree.deleteItem(rid,null);
			} else {
				//show the error message
				window.alert(result);
			}
			$('actionSwitch').innerHTML = "&nbsp;";
			$('showBuilderInfo').innerHTML = "&nbsp;";
    	},
		onFailure: function(){
			window.alert("與Server連線發生問題, 請重新再試.");
		}
  	});	
	removeID = "";
}

//Show the create builder work table when user select create BuilderWork command
function showCreateBuilderWorkTable(id){
	//Create Builder Work
	new Ajax.Request('GetBuilderInfoByProjectsAction.do',
 	{
    	onSuccess: function(transport){
 			var xmlDocument = transport.responseXML; 			
 			var projects = xmlDocument.getElementsByTagName('Project');
      		var content = "&nbsp;<label class=\"SelectBuilderMessage\">Select a Builder&nbsp;</label><select id=\"selectbuilder\"";
      	    content += " onchange=\"javascript:showBuilderInfo()\">";
      	    var hiddenContent = "";
			content += "<option value=\"None"+"\">None</option>";
	      	for( var i = 0 ; i < projects.length ; i++ ){
      			var projectName = projects[i].getAttribute('name');
      			var builders = projects[i].childNodes;
      			for( var j = 0 ; j <builders.length ; j++ ){
      					var builderName = builders[j].getAttribute('name');
      					var builderId = builders[j].getAttribute('id');
      					if( isValidId(projectName,builderName,builderId) ){
      						content += "<option value=\"" + i + j + builders[j].getAttribute('id') +"\">" + projectName + ",&nbsp;";
      						content += builderName + "&nbsp;";
      						content +="</option>";
      						hiddenContent += "<input type=hidden id=\""+ i + j + builderId + "pn" + "\" value=\"" + projectName + "\"></input>";
      						hiddenContent += "<input type=hidden id=\""+ i + j + builderId + "bn" +  "\" value=\"" + builderName + "\"></input>";
      						hiddenContent += "<input type=hidden id=\""+ i + j + builderId  + "bi" + "\" value=\"" + builderId +  "\"></input>";
      						hiddenContent += "<input type=hidden id=\""+ i + j + builderId  + "osn" + "\" value=\"" + builders[j].getAttribute('osname') +  "\"></input>";
      						hiddenContent += "<input type=hidden id=\""+ i + j + builderId  + "desc" + "\" value=\"" + builders[j].getAttribute('description') +  "\"></input>";
      					}
      			}
      		}
      		content += "</select>";
			content += hiddenContent;
      		content += "&nbsp;<input type='button' onclick='createBuilderWork(" + id + ")' value='Add'>";
      		$('actionSwitch').innerHTML = content;
      		//showProjectBuilder(projects[i].getAttribute('name'));
    	},
		onFailure: function(){
			window.alert("與Server連線發生問題, 請重新再試.");
		}
  	});			
}

function showProjectBuilderID(){
	var pname = $F('s_project');
	var bname = $F('s_builder');
	
	new Ajax.Request('getProjectBuilderIDList.do',
 	{
 		parameters: { projectName : pname, builderName : bname },
    	onSuccess: function(transport){
      		var result = transport.responseXML.documentElement.getElementsByTagName('Builder');
      		var element = new Element('option',{ 'value': 'None' }).update('None');
      		var select = $('s_builderid');
      		select.innerHTML = "";
      		select.insert(element);
      		for( var i = 0 ; i < result.length ; i++ ){
      			var id = result[i].getAttribute('id');
      			element = new Element('option',{ 'value': id }).update(id);
      			select.insert(element);
      		}     		
    	},
		onFailure: function(){
			window.alert("與Server連線發生問題, 請重新再試.");
		}
  	});		
}

function showProjectBuilder(pname){

	new Ajax.Request('getProjectBuilderList.do',
 	{
 		parameters: { name : pname },
    	onSuccess: function(transport){
      		var result = transport.responseXML.documentElement.getElementsByTagName('Builder');
      		var element = new Element('option',{ 'value': 'None' }).update('None');
      		var select = $('s_builder');
      		select.innerHTML = "";
      		select.insert(element);
      		for( var i = 0 ; i < result.length ; i++ ){
      			var name = result[i].getAttribute('name');
      			if( isValidId(pname,name) ){
      				element = new Element('option',{ 'value': name }).update(name);
      				select.insert(element);
      			}
      		}
    	},
		onFailure: function(){
			window.alert("與Server連線發生問題, 請重新再試.");
		}
  	});		
}

function isValidId(pname,bname){
	var r_result = false;
	new Ajax.Request('isTheBuilderExist.do',
 	{
 		asynchronous : false,
 		parameters: { project : pname, builder : bname },
    	onSuccess: function(transport){
      		var result = transport.responseText;
      		if( result == "true" ){
      			r_result = true;
      		} else{
      			r_result = false;
      		}
    	},
		onFailure: function(){
			window.alert("與Server連線發生問題, 請重新再試.");
		}
  	});	
  	return r_result;
}

function isValidId(pname,bname,bid){
	var r_result = false;
	new Ajax.Request('isTheBuilderExist.do',
 	{
 		asynchronous : false,
 		parameters: { project : pname, builder : bname, builderId : bid },
    	onSuccess: function(transport){
      		var result = transport.responseText;
      		if( result == "true" ){
      			r_result = true;
      		} else{
      			r_result = false;
      		}
    	},
		onFailure: function(){
			window.alert("與Server連線發生問題, 請重新再試.");
		}
  	});	
  	return r_result;
}

//Create the builder work by Ajax
function createBuilderWork(id){

	var temp = $F('selectbuilder');
	var projectName = document.getElementById(temp+"pn").value;
	var builderName = document.getElementById(temp+"bn").value;
	var builderId = document.getElementById(temp+"bi").value;
	var osName = document.getElementById(temp+"osn").value;
    var builderDesc = document.getElementById(temp+"desc").value;
	//Verify the text field is not empty
	if( projectName == "" ){
		window.alert("The Project Name isn't empty.");
		return;
	} else if( builderName == "" ){
		window.alert("The Builder Name isn't empty.");
		return;
	} else if( builderName == "None" || builderId == "None" ){
		window.alert("There isn't any exist builder.");
		return;	
	}
	//Create Builder Work
	new Ajax.Request('createBuilderWork.do',
 	{
 		parameters: { id : id, projectName : projectName, builderName : builderName, builderId: builderId, osName : osName , builderDesc : builderDesc },
    	onSuccess: function(transport){
      		var result = transport.responseText;
      		if( result != "false" ){
      			var img = "console.gif";
      			tree.insertNewItem( id ,result,result,0,img,img,img,"");
      			$('actionSwitch').innerHTML = "&nbsp;";
      			$('showBuilderInfo').innerHTML = "&nbsp;";
			} else {
				//show the error message
				window.alert("Create the Builder Work Failure, please retry.");
			}
    	},
		onFailure: function(){
			window.alert("與Server連線發生問題, 請重新再試.");
		}
  	});		
}

//Work Group Type
var WorkGroupType = new Array();
WorkGroupType[0] = "PARALLEL_GROUP";
WorkGroupType[1] = "SEQUENCE_GROUP";
//Create the work group by Ajax
function createWorkGroup(id,type){
	new Ajax.Request('createWorkGroup.do',
 	{
 		parameters: { id : id, type : type },
    	onSuccess: function(transport){
      		var result = transport.responseText;
      		if( result != "false" ){
      			var img;
      			if( type == WorkGroupType[0] ){
      				img = "flat.gif";
      			} else {
      				img = "hierarchical.gif";
      			}
      			tree.insertNewItem(id,result,type,0,img,img,img,"");
      			$('actionSwitch').innerHTML = "&nbsp;";
			} else {
				//show the error message
				window.alert(result);
			}
    	},
		onFailure: function(){
			window.alert("與Server連線發生問題, 請重新再試.");
		}
  	});		
}

//搬移的目標為同一層則進行swap, 其它則判斷是否為群組然後移過去
function onDrop(source,target){
	var parentID1 = tree.getParentId(source);
	var parentID2 = tree.getParentId(target);
	var result = isGroupID(target);
	//若搬移為同層且目標非group, 則進行Swap
	if( parentID1 == parentID2 && !result ){
		onMoveSiblingNext(source,target);
	} else if( result ){
		if( moveFlag ){
			return result;
		}
		//使用同步方式執行, 使得執行結束才會決定是否drag
		new Ajax.Request('dragWorkSet.do',
 		{
 			asynchronous : false,
 			parameters: { source : source, target : target },
    		onSuccess: function(transport){
      			var result = transport.responseText;
 				if( result == "false" ){
 					window.alert("拖移發生問題, 請重新再試.");
 				}
    		},
			onFailure: function(){
				result = false;
				window.alert("與Server連線發生問題, 請重新再試.");
			}
  		});				
	}
	return result;
}


var moveFlag = false;
function onMoveSiblingNext(source,target){
	new Ajax.Request('moveSiblingNext.do',
 	{
 		parameters: { source : source, target : target },
    	onSuccess: function(transport){
      		var result = transport.responseText;
      		if( result == "true" ){
      			moveFlag = true;
      			tree.moveItem(source,"item_sibling_next",target);
      			moveFlag = false;
      		} else {
      			window.alert("改變順序失敗, 請重新再試.");
      		}
    	},
		onFailure: function(){
			window.alert("與Server連線發生問題, 請重新再試.");
		}
  	});	
}

//若搬移到的目標為一個work or root則回傳ｆａｌｓｅ
function isGroupID(id){
	//root
	if( id == '0' ){
		return false;
	}
	//check is a group
	for(var i = 0 ; i < id.length ; i++){
		var char = id.charAt(i);
		if( char > '9' || char < '0' ){
			return false;
		}
	}
	return true;
}


function showBuilderInfo(){
	//window.alert("111");
	var temp = $F('selectbuilder');
	if( temp != "None" ){
		var osName = document.getElementById(temp+"osn").value;
		var builderDesc = document.getElementById(temp+"desc").value;
		var tableContent = "";
		//Builder Preference 
		//$('showBuilderInfo').innerHTML = "osName: " + osName + "&nbsp;" + ", builderDesc: " + builderDesc;
		tableContent += "<table align=\"center\" width=\"95%\" border=\"1\" cellpadding=\"1\"";
		tableContent += "cellspacing=\"1\" class=\"TableListBorder\">";
		tableContent += "<tr class=\"TableListTitle\"><td width=\"95%\" colspan=\"2\">Builder&nbsp;Preference</td></tr>";
		tableContent += "<tr><td class=\"TableListHead\" width=\"30%\">OS&nbsp;Name</td><td width=\"65%\">"+osName+"&nbsp;"+"</td></tr>";
		tableContent += "<tr><td class=\"TableListHead\" width=\"30%\">Builder&nbsp;Description</td><td width=\"65%\">"+builderDesc+"&nbsp;"+"</td></tr>";
		tableContent += "</table>";
		$('showBuilderInfo').innerHTML = tableContent;
	}else{
		$('showBuilderInfo').innerHTML = "&nbsp;";
	}
}
function validateWorkflow(checkType){
	var flag = false;
	new Ajax.Request('validateWorkFlow.do',
 	{
    	asynchronous : false,
    	parameters: { check : checkType },
    	onSuccess: function(transport){
      		var result = transport.responseText;
      		if( result == "true" ){
      			flag =  true;
      		} else if( result == "sizefailure" ){
      			hideLoadMask(msg,divId);
      			window.alert("仍有Builder(s)未被加入到workflow.");
      		}else {
      			window.alert("儲存檔案失敗, 請重新再試.");
      		}
    	},
		onFailure: function(){
			hideLoadMask(msg,divId);
			window.alert("與Server連線發生問題, 請重新再試.");
		}
  	});
  	return flag;
}
