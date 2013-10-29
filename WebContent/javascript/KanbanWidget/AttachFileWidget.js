Ext.ns('ezScrum');

/* Create Story Form */
ezScrum.AttachFileForm = Ext.extend(Ext.form.FormPanel, {
	issueId:'-1',
	bodyStyle: 'padding:15px',
	border : false,
	defaultType: 'textfield',
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
			items: [
				{
					xtype:'fileuploadfield',
					emptyText: 'Select a file',
		            fieldLabel: 'Update File',
		            name: 'file',
		            vtype: 'checkUploadFileName',
		            allowBlank: false
		        }
		    ],
		    buttons: 
		    [{
		    	formBind:true,
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
			var readUrl = this.url + "?issueID=" + this.issueId;
			var form = this.getForm().submit({
				url:readUrl,
				params:{entryPoint: "WorkItem"},
				success: function(form, action) {
			       obj.onSuccess(action);
			    },
			    failure:function(form, action){obj.onFailure(action);}
			});
		}

	},
	onSuccess:function(response) 
	{
		var myMask = new Ext.LoadMask(this.getEl(), {msg:"Please wait..."});
		myMask.hide();
		
		var rs = jsonWorkItemReader.readRecords(response.result);
		if(rs.success && rs.totalRecords > 0)
		{
			var record = rs.records[0];
			if(record)
			{
				this.fireEvent('AttachSuccess', this, response, record);
			}
		}
		else
		{
			this.onFailure(onFailure);
		}
	},
	onFailure:function(response) 
	{
		var myMask = new Ext.LoadMask(this.getEl(), {msg:"Please wait..."});
		myMask.hide();
		var msg = 'Attach File Failure';
		if(response.result && response.result.msg)
			msg = response.result.msg;
		this.fireEvent('AttachFailure', this, response, msg);
	},
	reset:function(){
		this.getForm().reset();
	}
});

Ext.reg('attachFileForm', ezScrum.AttachFileForm);

ezScrum.AttachFileWidget = Ext.extend(Ext.Window, {
	title:'Attach File',
	width:700,
	modal:false,
	closeAction:'hide',
	initComponent:function() {
		var config = {
			layout:'form',
			items : [{xtype:'attachFileForm'}]
        }
		Ext.apply(this, Ext.apply(this.initialConfig, config));
		ezScrum.AttachFileWidget.superclass.initComponent.apply(this, arguments);
		
		this.addEvents('AttachSuccess', 'AttachFailure');
		this.attachForm = this.items.get(0); 
		this.attachForm.on('AttachSuccess', function(obj, response, record){ this.fireEvent('AttachSuccess', this, obj, response, record); }, this);
		this.attachForm.on('AttachFailure', function(obj, response, msg){ this.fireEvent('AttachFailure', this, obj, response, msg); }, this);
	},
	attachFile:function(id){
		this.attachForm.reset();
		this.attachForm.issueId = id; 
		this.show();
	}
});