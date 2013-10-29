Ext.ns('ezScrum');

/* Import Stories Form */
ezScrum.ImportStoriesForm = Ext.extend(Ext.form.FormPanel, {
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
			url : 'importStories.do',
			fileUpload : true,							
			items: [
				{
					xtype:'fileuploadfield',
					emptyText: 'Select a file',
		            fieldLabel: 'Import Stories',
		            name: 'file',		            
		            allowBlank: false,
		            vtype: 'checkXLS'		        	            
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
		
		ezScrum.ImportStoriesForm.superclass.initComponent.apply(this, arguments);
		
		this.addEvents('importSuccess', 'importFailure');
	},	
	onRender:function() {
		ezScrum.ImportStoriesForm.superclass.onRender.apply(this, arguments);
		this.getForm().waitMsgTarget = this.getEl();
	},
	submit : function()
	{
		if(this.getForm().isValid()){
			var myMask = new Ext.LoadMask(this.getEl(), {msg:"Please wait..."});
			myMask.show();
			var obj = this;
			var readUrl = this.url;
			var form = this.getForm().submit({
				url:readUrl,
				success: function(response) {
					obj.onSuccess(response);
				},
			    failure:function(response){obj.onFailure(response);}
			});
		}

	},
	onSuccess:function(response) 
	{
		var myMask = new Ext.LoadMask(this.getEl(), {msg:"Please wait..."});
		myMask.hide();
		
		ConfirmWidget.loadData(response);
		if (ConfirmWidget.confirmAction()) {
			this.fireEvent('importSuccess', this);
		}
	},
	onFailure:function(response) 
	{
		var myMask = new Ext.LoadMask(this.getEl(), {msg:"Please wait..."});
		myMask.hide();
		
		var msg = "The format of file is incorrect.";
		if(response.result && response.result.msg)
			msg = response.result.msg;
			
		this.fireEvent('importFailure', this, msg);
	},
	reset:function(){
		this.getForm().reset();
	}
});

Ext.reg('importStoriesForm', ezScrum.ImportStoriesForm);

ezScrum.ImportStoriesWidget = Ext.extend(Ext.Window, {
	title:'Import Stories By XLS',
	width:700,
	modal:true,
	closeAction:'hide',
	constrain : true,
	initComponent:function() {
		var config = {
			layout:'form',
			items : [{xtype:'importStoriesForm'}]
        }
		Ext.apply(this, Ext.apply(this.initialConfig, config));
		ezScrum.ImportStoriesWidget.superclass.initComponent.apply(this, arguments);	
			
		this.addEvents('ImportSuccess', 'ImportFailure');
		
		this.attachForm = this.items.get(0); 
		this.attachForm.on('importSuccess', function(obj) { this.fireEvent('ImportSuccess', this); }, this);
		this.attachForm.on('importFailure', function(obj, msg) { this.fireEvent('ImportFailure', this, msg); }, this);
	},
	importFile:function(){
		this.attachForm.reset();		 
		this.show();
	}
});