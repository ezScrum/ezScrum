Ext.ns('ezScrum');

/* Handler Data */
var handlerComboStoreForEdit = new Ext.data.Store({
    id		: 0,
    fields	: [{name: 'Name'}],
    reader	: ActorReader
});

/* Handler ComboBox */
var handlerComboForEdit = new Ext.form.ComboBox({
    typeAhead: true,
    triggerAction: 'all',
    lazyRender: true,
    editable: false,
    mode: 'local',
    store: handlerComboStoreForEdit,
    name: 'HandlerComboBox',
    valueField: 'Name',
    displayField: 'Name',
    fieldLabel: 'Handler',
    id: 'HandlerComboBoxForEdit'
});

/* Create Task Form */
ezScrum.EditTaskForm = Ext.extend(Ext.form.FormPanel, {
	// Default issue id
	issueId : '-1',
	bodyStyle: 'padding:15px',
	border : false,
	defaultType: 'textfield',
	labelAlign : 'right',
	labelWidth : 100,
	defaults: {
        width: 500,
        msgTarget: 'side'
    },
    monitorValid: true,
	initComponent:function() {
		var config = {
			// Ajax edit Task url 
			url : 'ajaxEditTask.do',
			// Ajax load Task url
			loadUrl : 'getEditTaskInfo.do',
			
			items: [{
		            fieldLabel: 'ID',
		            name: 'issueID',
					readOnly:true
		        },{
		            fieldLabel: 'Name',
		            name: 'Name',
		            allowBlank: false,	
					maxLength: 128
		        },
		        handlerComboForEdit
		        ,{
		        	fieldLabel: 'Partners',
		            name: 'Partners'
		        }, {
		            fieldLabel: 'Estimate',
		            name: 'Estimate',
		            vtype:'Float'
		        }, {
		        	fieldLabel: 'Remains',
		            name: 'Remains',
		            vtype:'Float'
		        },  {
		        	fieldLabel: 'Actual',
		            name: 'Actual',
		            vtype:'Float'
		        }, {
		        	fieldLabel: 'Notes',
		            xtype: 'textarea',
		            name: 'Notes',
		            height:200
		        }, {
		            name: 'sprintId',
		            hidden: true
		        }
		    ],
		    buttons: 
		    [{
		    	formBind:true,
	    		text: 'Submit',
	    		scope:this,
	    		handler: this.submit
	    	},
	        {
	        	text: 'Cancel',
	        	scope:this,
	        	handler: function(){this.ownerCt.hide();}
	        }]
        }
        
		Ext.apply(this, Ext.apply(this.initialConfig, config));
		ezScrum.EditTaskForm.superclass.initComponent.apply(this, arguments);
					
		this.addEvents('LoadSuccess', 'LoadFailure', 'EditSuccess', 'EditFailure');
	},
	onRender:function() {
		ezScrum.EditTaskForm.superclass.onRender.apply(this, arguments);
		this.getForm().waitMsgTarget = this.getEl();

	},
	// Edit Task action 
	submit : function()
	{
		var myMask = new Ext.LoadMask(this.getEl(), {msg:"Please wait..."});
		myMask.show();	
		var obj = this;
		var form = this.getForm();
		
		Ext.Ajax.request({
			url:this.url,
			success:function(response){obj.onEditSuccess(response);},
			failure:function(response){obj.onEditFailure(response);},
			params:form.getValues()
		});
	},
	// Load Task success
	onLoadSuccess:function(response) 
	{
		var myMask = new Ext.LoadMask(this.getEl(), {msg:"Please wait..."});
		myMask.hide();
		
		ConfirmWidget.loadData(response);
		if (ConfirmWidget.confirmAction()) {
			var rs = taskReader.readRecords(response.responseXML);
			if(rs.success) {
				var record = rs.records[0];
				
				if(record) {
					handlerComboStoreForEdit.loadData(response.responseXML);
					handlerComboForEdit.originalValue = record.data['Handler'];
					this.getForm().reset();
					this.getForm().setValues({
						issueID	: record.data['Id'], 
						Name	: record.data['Name'], 
						Partners: record.data['Partners'], 
						Estimate	: record.data['Estimate'], 
						Actual	: record.data['Actual'],
						Notes	: record.data['Notes'],
						Remains	: record.data['Remains']
					});
					
					this.fireEvent('LoadSuccess', this, response, record);
				}
			}
		}
	},
	onLoadFailure:function(response) 
	{
		var myMask = new Ext.LoadMask(this.getEl(), {msg:"Please wait..."});
		myMask.hide();
		this.fireEvent('LoadFailure', this, response, this.issueId);
	},
	// Update Task success
	onEditSuccess:function(response) 
	{
		var myMask = new Ext.LoadMask(this.getEl(), {msg:"Please wait..."});
		myMask.hide();
		
		ConfirmWidget.loadData(response);
		if (ConfirmWidget.confirmAction()) {
			var rs = taskReader.readRecords(response.responseXML);
			if(rs.success) {
				var record = rs.records[0];
				if(record) {
					this.fireEvent('EditSuccess', this, response, record);
				}
			} else {
				this.fireEvent('EditFailure', this, response, this.issueId);
			}
		}
	},
	// Update Task failure
	onEditFailure:function(response) 
	{
		var myMask = new Ext.LoadMask(this.getEl(), {msg:"Please wait..."});
		myMask.hide();
		this.fireEvent('EditFailure', this, response, this.issueId);
	},
	loadStore:function()
	{
		var obj = this;
		var myMask = new Ext.LoadMask(obj.getEl(), {msg:"Please wait..."});
		myMask.show();
		Ext.Ajax.request({
			url:this.loadUrl,
			success:function(response){obj.onLoadSuccess(response);},
			failure:function(response){obj.onLoadFailure(response);},
			params : {issueID : this.issueId, sprintID : this.sprintId}
		});
	},
	reset:function()
	{
		this.getForm().reset();
	}

});
Ext.reg('editTaskForm', ezScrum.EditTaskForm);

ezScrum.EditTaskWidget = Ext.extend(Ext.Window, {
	title:'Edit Task',
	width:700,
	constrain:true,
	modal:true,
	closeAction:'hide',
	initComponent:function() {
		var config = {
			layout:'form',
			items : [{xtype:'editTaskForm'}]
        }
			Ext.apply(this, Ext.apply(this.initialConfig, config));
		ezScrum.EditTaskWidget.superclass.initComponent.apply(this, arguments);
		
		this.addEvents('LoadSuccess', 'LoadFailure', 'EditSuccess', 'EditFailure');
		
		this.items.get(0).on('LoadSuccess', function(obj, response, record){ this.fireEvent('LoadSuccess', this, obj, response, record); }, this);
		this.items.get(0).on('LoadFailure', function(obj, response, issueId){ this.fireEvent('LoadFailure', this, obj, response, issueId); }, this);
		this.items.get(0).on('EditSuccess', function(obj, response, record){ this.fireEvent('EditSuccess', this, obj, response, record); }, this);
		this.items.get(0).on('EditFailure', function(obj, response, issueId){ this.fireEvent('EditFailure', this, obj, response, issueId); }, this);
	},
	loadEditTask:function(sprintID, issueId){
		this.items.get(0).issueId = issueId;

		this.items.get(0).reset();
		this.items.get(0).getForm().setValues({sprintId : sprintID});
		this.show();
		this.items.get(0).loadStore();
	}
});