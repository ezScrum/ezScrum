Ext.ns('ezScrum');

/* Create Task Form */
ezScrum.CreateTaskForm = Ext.extend(Ext.form.FormPanel, {
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
			url : 'ajaxAddSprintTask.do',
			items: [{
		            fieldLabel: 'Name',
		            name: 'Name',
		            allowBlank: false,
		            maxLength: 128
		        }, {
		            fieldLabel: 'Estimate',
		            name: 'Estimate',
		            vtype: 'Number'
		        }, {
		        	fieldLabel: 'Notes',
		            xtype: 'textarea',
		            name: 'Notes',
		            height:200
		        }, {
		        	fieldLabel: 'Specific New Task Time',
		        	xtype: 'datefield',
		            name: 'SpecificTime',
		            format: 'Y-m-d'
		        }, {
		            name: 'sprintId',
		            hidden: true
		        }, {
		            name: 'issueID',
		            hidden: true
		        },{
		        	xtype: 'RequireFieldLabel'
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
		ezScrum.CreateTaskForm.superclass.initComponent.apply(this, arguments);
		
		this.addEvents('CreateSuccess', 'CreateFailure');
	},
	onRender:function() {
		ezScrum.CreateTaskForm.superclass.onRender.apply(this, arguments);
		this.getForm().waitMsgTarget = this.getEl();
	},
	submit : function()
	{
		var myMask = new Ext.LoadMask(this.getEl(), {msg:"Please wait..."});
		myMask.show();	
		var form = this.getForm();

		var obj = this;
		Ext.Ajax.request({
			url:this.url,
			params:form.getValues(),
			success:function(response){obj.onSuccess(response);},
			failure:function(response){obj.onFailure(response);}
		});
	},
	onSuccess:function(response) 
	{
		var myMask = new Ext.LoadMask(this.getEl(), {msg:"Please wait..."});
		myMask.hide();
		// check action permission
		ConfirmWidget.loadData(response);
		if (ConfirmWidget.confirmAction()) {
			var rs = taskReader.readRecords(response.responseXML);
			if(rs.success)
			{
				var record = rs.records[0];
				if(record)
				{
					this.fireEvent('CreateSuccess', this, response, record);
				}
	
			}
			else
				this.fireEvent('CreateFailure', this, response);
		}
	},
	onFailure:function(response) 
	{
		var myMask = new Ext.LoadMask(this.getEl(), {msg:"Please wait..."});
		myMask.hide();
		this.fireEvent('CreateFailure', this, response);
	},
	reset:function(){
		this.getForm().reset();
	}
});

Ext.reg('createTaskForm', ezScrum.CreateTaskForm);

ezScrum.AddNewTaskWidget = Ext.extend(Ext.Window, {
	title:'Add New Task',
	id:'addTaskWin',
	width:700,
	modal:true,
	constrain  : true,
	closeAction:'hide',
	initComponent:function() {
		var config = {
			layout:'form',
			items : [{xtype:'createTaskForm'}]
        }
		Ext.apply(this, Ext.apply(this.initialConfig, config));
		ezScrum.AddNewTaskWidget.superclass.initComponent.apply(this, arguments);
		
		this.addEvents('CreateSuccess', 'CreateFailure');
		
		this.items.get(0).on('CreateSuccess', function(obj, response, record){ this.fireEvent('CreateSuccess', this, obj, response, record); }, this);
		this.items.get(0).on('CreateFailure', function(obj, response){ this.fireEvent('CreateFailure', this, obj, response, issueId); }, this);
	},
	showWidget:function(sprintID, storyID){
		this.items.get(0).reset();
		this.items.get(0).getForm().setValues({sprintId : sprintID, issueID : storyID});
		this.show();
	}
});