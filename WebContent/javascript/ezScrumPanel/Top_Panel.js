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
	NotifyLogout();
	replaceURL( "./logout.do" );
	//window.document.location = "./logout.do"
}

function NotifyLogout(){
	try{
		Ext.Ajax.request({
			url: 'notifyLogout.do',
			success:function(data){
			},
			failure:function(XMLHttpRequest,s,o){
			}
		});
	}catch(e){}
}

function SubscriptNotificationService(){
	if(confirm("Are you sure to subscribe?")){
		Ext.Ajax.request({
			url: 'switchNotification.do',
			params:{
				event:"Subscribe"
			},
			success:function(data){
				if(data.responseText == "Success")
					SetNotifyImg("Subscription");
				else
					{
					alert(data.responseText);
					}
					
			},
			failure:function(XMLHttpRequest,s,o){
				alert("Subscript fail");
			}
		});
	}
}

function CancelSubscribeNotificationService(){
	if(confirm("Are you sure to cancel subscription?")){
		var userName = Ext.getDom("Notification_Subscript").userName;
		var firebaseToken = Ext.getDom("Notification_Subscript").firebaseToken;
		Ext.Ajax.request({
			url: 'switchNotification.do',
			params:{
				event:"Cancel"
			},
			success:function(data){
				if(data.responseText == "Success")
					SetNotifyImg("No-Subscription");
				else
					alert(data.responseText);
			},
			failure:function(XMLHttpRequest,s,o){
				alert("Subscribe fail");
			}
		});
	}
}

function SetNotifyImg(subscription){
	if(subscription == "Subscription"){
		Ext.getDom("Notification_Subscript").src = "images/NotifyBell.png";
		Ext.getDom("Notification_Subscript").onclick = CancelSubscribeNotificationService;
	}
	else if(subscription == "No-Subscription"){
		Ext.getDom("Notification_Subscript").src = "images/NotifyBell_NoSubscription.png";
		Ext.getDom("Notification_Subscript").onclick = SubscriptNotificationService;
	}
	else{
		Ext.getDom("Notification_Subscript").src = "images/NotifyBell_NoUsed.png";
		Ext.getDom("Notification_Subscript").onclick = "";
		Ext.example.msg("Notification",subscription);
	}	
}

function SettingNotification(){
	var promise = new Promise(function(resolve,reject){
		Ext.Ajax.request({
			url: './firebase.json',
			type: "GET",
			dataType: 'jsonp',
			jsonp: "mycallback",
			crossDomain: true,
			success: function(mycallback){
				var j = JSON.parse(mycallback.responseText);
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
				navigator.serviceWorker.register("./javascript/firebase-messaging-sw.js")
				.then(function (registration) {
					messaging.useServiceWorker(registration);
					messaging.requestPermission()
					.then(function(){
						messaging.getToken()
						.then(function(token){
							resolve(token);
						})
						.catch(function(err){
							reject(err)
						})
					})
					.catch(function(){
						SetNotifyImg("No Permission");
					});
				})
				.catch(function(e){
					console.log(e);
				});
				messaging.onMessage(function(payload){
					var option = {
							body: payload.notification.body,
							icon: "./images/scrum_16.png"
					}
				    var notification = new Notification(payload.notification.title,option);
					notification.onclick = function(event){
						event.preventDefault();
						window.open(payload.notification.click_action);
					}
				});
			},
			failure: function(){SetNotifyImg("Error");}
		});
	})
	return promise;
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
    		Ext.Ajax.request({
    			url: 'GetTopTitleInfo.do',
    			success: function(response) {
    				var obj = Ext.util.JSON.decode(response.responseText);
    				
    				theProjectName = obj.ProjectName;
    				var projectName = obj.ProjectName;
    				var username = obj.Username;
    				var nickname = obj.Nickname;
    				Ext.getDom("UserNameInfo_Project").innerHTML = username + "(" + nickname + ")";
    				Ext.getDom("ProjectNameInfo").innerHTML = "Project&nbsp;:&nbsp;&nbsp;" + projectName;
    			},
    			failure : function(){
    				Ext.example.msg('Server Error', 'Sorry, the connection is failure.');
    			}
    		});
    		
    		SettingNotification().then(function(data){
    			if(data != null){
    				Ext.Ajax.request({
            			url: 'getSubscriptStatus.do',
            			params:{firebaseToken:data},
            			success: function(response){
            				var obj = Ext.util.JSON.decode(response.responseText);
            				var subscriptStatue = obj.SubscriptStatus;
            				SetNotifyImg(subscriptStatue);
            			}
        			});
    			}					
				else
					SetNotifyImg("Can not get firebaseToken");
    			
    			
    		})
    		.catch(function(err){
    			SetNotifyImg("Error");
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