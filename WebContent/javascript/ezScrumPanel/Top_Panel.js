// the function is go for top title panel
function GoProjectList() {
	replaceURL("./viewList.do");
	//window.document.location = "./viewList.do" 
}

function GoManagement() {
	replaceURL("./viewManagement.do" );
	//window.document.location = "./viewManagement.do"
}

function GoLogout() {
	replaceURL( "./logout.do" );
	//window.document.location = "./logout.do"
}

function SubscriptNotificationService(){
	var UserNameInfo = Ext.getDom("UserNameInfo_Project").innerHTML;
	var leftBracketIndex = UserNameInfo.indexOf("(");
	var userName = UserNameInfo.slice(0,leftBracketIndex);
	var firebaseToken = Ext.getDom("Notification_Subscript").firebaseToken;
	Ext.Ajax.request({
		url: '127.0.0.1:5000/subscript',
		type: "POST",
		dataType: "json",
		params:{
			username: userName,
			token:firebaseToken
		},
		success:function(data){
			SetNotifyImgSubscript();
		},
		error:function(XMLHttpRequest,s,o){
			alert("Subscript fail");
		}
	});
	
}

function UnSubscriptNotificationService(){
	if(confirm("Are you sure to cancle subscription?")){
		var UserNameInfo = Ext.getDom("UserNameInfo_Project").innerHTML;
		var leftBracketIndex = UserNameInfo.indexOf("(");
		var userName = UserNameInfo.slice(0,leftBracketIndex);
		var firebaseToken = Ext.getDom("Notification_Subscript").firebaseToken;
		Ext.Ajax.request({
			url: '127.0.0.1:5000/unSubscript',
			type: "POST",
			dataType: "json",
			params:{
				username: userName,
				token:firebaseToken
			},
			success:function(data){
				SetNotifyImgUnSubscript();
			},
			error:function(XMLHttpRequest,s,o){
				alert("UnSubscript fail");
			}
		});
	}
}

function SetNotifyImgSubscript(){
	Ext.getDom("Notification_Subscript").src = "images/NotifyBell.png";
	Ext.getDom("Notification_Subscript").onclick = UnSubscriptNotificationService;
}

function SetNotifyImgUnSubscript(){
	Ext.getDom("Notification_Subscript").src = "images/NotifyBell_UnSubscript.png";
	Ext.getDom("Notification_Subscript").onclick = SubscriptNotificationService;
}

function SetNotifyImgFail(){
	Ext.getDom("Notification_Subscript").src = "images/NotifyBell_NoUsed.png";
	Ext.getDom("Notification_Subscript").onclick = "";
}

function ConnectNotificationService(userName, firebaseToken){
	Ext.Ajax.request({
		url: '127.0.0.1:5000/NotifyLogin',
		type: "POST",
		dataType: "json",
		params:{
			username: userName,
			token:firebaseToken
		},
		success:function(data){
			if(data.responseText == "Subscript"){
				SetNotifyImgSubscript();
			}
			else if(data.responseText == "UnSubscript"){
				SetNotifyImgUnSubscript();
			}
		},
		error:function(XMLHttpRequest,s,o){
			console.log(s)
			SetNotifyImgFail();
		}
	});
}

function SettingNotification(){
	Ext.Ajax.request({
		url: './firebase.json',
		type: "GET",
		dataType: "json",
		success: function(jdata){
			var j = JSON.parse(jdata.responseText);
			var config = {
					apiKey: j.apiKey,
					authDomain: j.authDomain,
					databaseURL: j.databaseURL,
					projectId: j.projectId,
					storageBucket: j.storageBucket,
					messagingSenderId: j.messagingSenderId
			}
			firebase.initializeApp(config);
			
			const messaging = firebase.messaging();
			navigator.serviceWorker.register("firebase-messaging-sw.js", {scope: "firebase-cloud-messaging-push-scope"})
			.then(function (registration) {
				messaging.useServiceWorker(registration);
				messaging.requestPermission()
				.then(function(){
					return messaging.getToken();
				})
				.then(function(token){
					//TODO Notify login and setting subscript button
					Ext.getDom("Notification_Subscript").firebaseToken = token;
					SetNotifyImgSubscript();
					console.log(token);
//					ConnectNotificationService(obj.Username,token);
				})
				.catch(function(err){
					SetNotifyImgFail();
				});
			});
			messaging.onMessage(function(payload){
				var option = {
						body: payload.notification.body,
						icon: "./images/scrum_16.png"
				}
			    var notification = new Notification(payload.notification.title,option);
			});
		},
		error: function(){console.log("Setting faile.")}
	});
}

ezScrum.TitlePanel = Ext.extend(Ext.Panel, {
	layout	: 'fit',
    collapsible	: false,
	border		: false,
	frame		: false,
	animCollapse	: false,
	animate			: false,
	hideCollapseTool: true,
	rootVisible		: false,
	lines			: false,
	frame			: false,
    height			: 86,
    minSize			: 56,
    maxSize			: 56,
    lines			: false,
    initComponent: function() {
		ezScrum.TitlePanel.superclass.initComponent.call(this);
    }
});

ezScrum.Project_TopPanel = new ezScrum.TitlePanel({
	region		: 'north',		// position
	
	id			: 'Project_Top_Panel',
	items : [{
    	html: '<table width="100%" height="55" border="0" cellpadding="0" cellspacing="0"><tr><td rowspan="2" align="left"><img height="55" width="411" src="images/Title_Caption.gif"><img height="55" src="images/Title_Caption_RightBlock.gif"></td><td colspan="5" height="27" width="100%" align="right" background="images/Title_RightTopBG.gif"><div id="UserNameInfo_Project" class="UserProfile" style="width: 30%"></div></td></tr><tr><td background="images/TopMenu_SelectBG.gif" height="28" class="TopMenu SelectColor" onclick="GoProjectList()">ProjectList</td><td><img height="28" width="23" src="images/TopMenu_SelectRight.gif"></td><td background="images/TopMenu_UnSelectBG.gif" class="TopMenu UnSelectColor" onclick="GoManagement()">Management</td><td><img height="28" width="23" src="images/TopMenu_UnSelectEnd.gif"></td><td align="right" background="images/Title_RightDownBG.gif" width="100%"><img style="margin-right: 10px" id="Notification_Subscript" height="25" width="20"><img height="18" width="45" class="TopMenu" onclick="GoLogout()" src="images/logout.gif"></td></tr><tr background="images/Title_RightDownBG.gif" height="30" width="100%"><td colspan="6"><div id="ProjectNameInfo" style="width: 30%"></div></td></tr></table>'	
    }],
    listeners: {
    	beforerender: function() {
    		var obj = this;
    		console.log("1");
    		Ext.Ajax.request({
    			url: 'GetTopTitleInfo.do',
    			success: function(response) {
    				var obj = Ext.util.JSON.decode(response.responseText);

    				theProjectName = obj.ProjectName;
    				var projectName = obj.ProjectName;
    				var username = obj.Username;
    				var nickname = obj.Nickname;
    				
    				SettingNotification();
    				
    				Ext.getDom("UserNameInfo_Project").innerHTML = username + "(" + nickname + ")";
    				Ext.getDom("ProjectNameInfo").innerHTML = "Project&nbsp;:&nbsp;&nbsp;" + projectName;
    			},
    			failure : function(){
    				Ext.example.msg('Server Error', 'Sorry, the connection is failure.');
    			}
    		});
    	}
    }
});

ezScrum.Management_TopPanel = new ezScrum.TitlePanel({
	region		: 'north',		// position
	
	id			: 'Management_Top_Panel', 
	items : [{
    	html: '<table width="100%" height="55" border="0" cellpadding="0" cellspacing="0"><tr><td rowspan="2" align="left"><img height="55" width="411" src="images/Title_Caption.gif"><img height="55" src="images/Title_Caption_RightBlock2.gif"></td><td colspan="5" height="27" width="100%" align="right" background="images/Title_RightTopBG.gif"><div id="UserNameInfo_Management" class="UserProfile" style="width: 30%"></div></td></tr><tr><td background="images/TopMenu_UnSelectBG.gif" height="28" class="TopMenu UnSelectColor" onclick="GoProjectList()">ProjectList</td><td><img height="28" width="23" src="images/TopMenu_SelectLeft.gif"></td><td background="images/TopMenu_SelectBG.gif" class="TopMenu SelectColor" onclick="GoManagement()">Management</td><td><img height="28" width="23" src="images/TopMenu_SelectEnd.gif"></td><td align="right" background="images/Title_RightDownBG.gif" width="100%"><img height="18" width="45" class="TopMenu" onclick="GoLogout()" src="images/logout.gif"></td></tr><tr background="images/Title_RightDownBG.gif" height="30" width="100%"><td colspan="6">Account Management</td></tr></table>'	
    }],
    listeners: {
    	beforerender: function() {
    		var obj = this;
    		
    		Ext.Ajax.request({
    			url: 'GetTopTitleInfo.do',
    			success: function(response) {
    				var obj = Ext.util.JSON.decode(response.responseText);
    				var username = obj.Username;
    				var nickname = obj.Nickname;
    				
    				Ext.getDom("UserNameInfo_Management").innerHTML = username + "(" + nickname + ")";
    			},
    			failure : function(){
    				Ext.example.msg('Server Error', 'Sorry, the connection is failure.');
    			}
    		});
    	}
    }
});