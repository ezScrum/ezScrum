Ext.ns('ezScrum.layout');

// 將 window 的設定固定大小、屬性、狀態
// 暫時只想到這些，其他有用的再自行加入
ezScrum.layout.Window = Ext.extend(Ext.Window, {
	width       	: 700,
    modal       	: true,
    closeAction 	: 'hide',		// 當按下關閉時隱藏
    constrain		: true,			// 限制視窗不能超出目前頁面
    resizable		: true,
    frame			: true
});


// 將 TaskBoard 移動時會跳出的視窗內的 Form 設定屬性
ezScrum.layout.TaskBoardCardWindowForm = Ext.extend(Ext.form.FormPanel, {
	bodyStyle     : 'padding:15px',
    defaultType   : 'textfield',
    labelAlign    : 'right',
    labelWidth    : 100,
    border        : false,
    monitorValid  : true,
    defaults      : {
    	width: 500,
        msgTarget : 'side'
    }
});


// 顯示資料用的 Form
ezScrum.layout.InfoForm = Ext.extend(Ext.form.FormPanel, {
	bodyStyle     : 'padding:5px',
    defaultType   : 'textfield',
    labelAlign    : 'right',
    labelWidth    : 150,
    border        : false,
    frame		  : true
});


// Burndown Chart
ezScrum.layout.Chart = Ext.extend(Ext.Panel, {
    frame		: true,
    border		: true,
    anchor		: '49%, 100%',
    style		: 'float:left;top:0;left:0;margin:10px;',
    layout		: 'anchor',
    floating	: false,
    margins		: '5 5 5 5'
});


// 整個大 Panel 的 Load Mask 顯示，for Project Management 所有頁面
function MainLoadMaskShow() {
	var obj = Ext.getCmp('ProjectInfoMainLayout');
	if(obj != undefined){
		var mask = new Ext.LoadMask(obj.getEl(), {msg:"loading info..."});
		mask.show();
	}
}


//整個大 Panel 的 Load Mask 隱藏，for Project Management 所有頁面
function MainLoadMaskHide() {
	var obj = Ext.getCmp('ProjectInfoMainLayout');
	if(obj != undefined){
		var mask = new Ext.LoadMask(obj.getEl(), {msg:"loading info..."});
		mask.hide();
	}
}


//整個大 Panel 的 Load Mask 顯示, for Admin Management 所有頁面
function ManagementMainLoadMaskShow() {
	var obj = Ext.getCmp('ManagementMainLayout');
	
	var mask = new Ext.LoadMask(obj.getEl(), {msg:"loading info..."});
	mask.show();
}


//整個大 Panel 的 Load Mask 隱藏，for Admin Management 所有頁面
function ManagementMainLoadMaskHide() {
	var obj = Ext.getCmp('ManagementMainLayout');
	
	var mask = new Ext.LoadMask(obj.getEl(), {msg:"loading info..."});
	mask.hide();
}


//整個大 Panel 的 Load Mask 顯示, for USer Management 所有頁面
function UserManagementMainLoadMaskShow() {
	var obj = Ext.getCmp('UserManagementMainLayout');
	
	var mask = new Ext.LoadMask(obj.getEl(), {msg:"loading info..."});
	mask.show();
}


//整個大 Panel 的 Load Mask 隱藏，for USer Management 所有頁面
function UserManagementMainLoadMaskHide() {
	var obj = Ext.getCmp('UserManagementMainLayout');
	
	var mask = new Ext.LoadMask(obj.getEl(), {msg:"loading info..."});
	mask.hide();
}


// 處理 Json 特殊字元，轉換成正常顯示給使用者觀看的字元
// 原因是因為後端會處理特殊字元後再丟出來
function replaceJsonSpecialChar(str) {
	var replaced_str = str.replace(/&lt;/ig, "<");
	replaced_str = replaced_str.replace(/&gt;/ig, ">");
	replaced_str = replaced_str.replace(/&apos;/ig, "'");
	
	replaced_str = replaced_str.replace(/&quot;/ig, "\"");
	replaced_str = replaced_str.replace(/&amp;/ig, "&");
	
	return replaced_str;
}
