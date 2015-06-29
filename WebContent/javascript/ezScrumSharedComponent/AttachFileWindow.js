Ext.ns('ezScrum');
Ext.ns('ezScrum.window');

/* Attach File Form */
ezScrum.AttachFileForm = Ext.extend(Ext.form.FormPanel, {
	issueId:'-1',
	issueType: '',
	projectName:undefined,
	bodyStyle: 'padding:15px',
	border : false,
	defaultType: 'textfield',
	notifyPanel		: undefined,
	labelAlign : 'right',
	labelWidth : 100,
	defaults: {
        width: 500,
        msgTarget: 'side'
    },
    monitorValid:true,
	initComponent:function() {
		var config = {
			url : 'ajaxAttachFile.do',
			fileUpload : true,
			items: [{
					xtype:'fileuploadfield',
					emptyText: 'Select a file',
		            fieldLabel: 'Update File',
		            name: 'file',
		            vtype: 'checkUploadFileName',
		            allowBlank: false
			}],
		    buttons: 
		    [{
		    	formBind:true,
		    	id: 'SubmitBtn',
	    		text: 'Submit',
	    		scope:this,
	    		handler: this.submit,
	    		disabled:true
	    	},
	        {
	        	text: 'Cancel',
	        	scope:this,
	        	handler: function(){this.ownerCt.hide();}
	        }]
        }
        
		Ext.apply(this, Ext.apply(this.initialConfig, config));
		
		ezScrum.AttachFileForm.superclass.initComponent.apply(this, arguments);
		
		this.addEvents('AttachSuccess', 'AttachFailure');
	},
	onRender:function() {
		ezScrum.AttachFileForm.superclass.onRender.apply(this, arguments);
		this.getForm().waitMsgTarget = this.getEl();
	},
	submit : function()
	{
		if(this.getForm().isValid()){
			var myMask = new Ext.LoadMask(this.getEl(), {msg:"Please wait..."});
			myMask.show();
			var obj = this;
			var readUrl = this.url + "?issueID=" + this.issueId + '&issueType=' + this.issueType;
			
			this.getForm().submit({
				url:readUrl,
				params: {projectName: obj.projectName,
						 entryPoint: obj.notifyPanel.entryPoint},
				success: function(form, action) {
			       obj.onSuccess(action);
			    },
			    failure:function(form, action){
			    	obj.onFailure(action);
			    }
			});
		}

	},
	onSuccess:function(response) 
	{
		var success = false;
		
		var myMask = new Ext.LoadMask(this.getEl(), {msg:"Please wait..."});
		myMask.hide();

		// because ViewReportIssues.jsp 讓外部使用者回報的頁面，不能擋權限，所以暫時先註解
//		ConfirmWidget.loadData(response);
//		if (ConfirmWidget.confirmAction()) {
		
		if(this.notifyPanel.entryPoint == "CustomIssue" ||
		   this.notifyPanel.entryPoint == "ReportIssues")
		{
			var rs = jsonCustomIssueReader.readRecords(response.result);
		}else{
			var rs = jsonStoryReader.readRecords(response.result);
		}
		success = rs.success;
		if(rs.success && rs.totalRecords > 0) {
			var record = rs.records[0];
			record.issueType = this.issueType;
			if(record) {
				this.notifyPanel.notify_AttachFile(success, record, null);
			}
		} else {
			this.onFailure(onFailure);
		}
//		}
	},
	onFailure:function(response) 
	{
		var myMask = new Ext.LoadMask(this.getEl(), {msg:"Please wait..."});
		myMask.hide();
		var msg = 'Attach File Failure';
		if(response.result && response.result.msg)
			msg = response.result.msg;
		this.notifyPanel.notify_AttachFile(false, record, msg);
		
	},
	reset:function(){
		this.getForm().reset();
	}
});

Ext.reg('attachFileForm', ezScrum.AttachFileForm);

ezScrum.window.AttachFileWindow = Ext.extend(Ext.Window, {
	title:'Attach File',
	width:700,
	modal:true,
	closeAction:'hide',
	constrain : true,
	initComponent:function() {
		var config = {
			layout:'form',
			items : [{xtype:'attachFileForm'}]
        }
		Ext.apply(this, Ext.apply(this.initialConfig, config));
		ezScrum.window.AttachFileWindow.superclass.initComponent.apply(this, arguments);
		
		this.attachForm = this.items.get(0); 
	},
	attachFile:function(panel, id, issueType){
		this.attachForm.reset();
		this.attachForm.issueId = id;
		this.attachForm.issueType = issueType;
		// initial form info
        this.attachForm.notifyPanel = panel;
        // initial window info 
        this.setTitle('Attach File');
		this.show();
	},
	attachFile_External:function(panel, id, projectName){
		this.attachForm.reset();
		this.attachForm.issueId = id; 
		this.attachForm.projectName = projectName;
		// initial form info
        this.attachForm.notifyPanel = panel;
		// initial window info 
        this.setTitle('Attach File'); 
        this.show();
	}
});

var AttachFile_Window = new ezScrum.window.AttachFileWindow();
