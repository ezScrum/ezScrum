Ext.ns('ezScrum');

/* Type Data */
var typeStoreForEdit = new Ext.data.Store({
    id: 0,
    fields: [
   		{name: 'Name'}
    ],
    reader:typeReader
});

/* Priority Data */
var priorityStoreForEdit = new Ext.data.Store({
    id: 0,
    fields: [
   		{name: 'Name'}
    ],
    reader:priorityReader
});

/* Handler Data */
var handlerStoreForEdit = new Ext.data.Store({
    id: 0,
    fields: [
   		{name: 'Name'}
    ],
    reader:handlerReader
});

/* WorkState Data */
var workstateStoreForEdit = new Ext.data.Store({
    id: 0,
    fields: [
   		{name: 'Name'}
    ],
    reader:workstateReader
});

/* Type ComboBox */
var typeComboForEdit = new Ext.form.ComboBox({
    typeAhead: true,
    triggerAction: 'all',
    lazyRender: true,
    editable: false,
    mode: 'local',
    store: typeStoreForEdit,
    name: 'TypeComboBox',
    valueField: 'Name',
    displayField: 'Name',
    fieldLabel: 'Type',
    id: 'TypeComboBoxForEdit'
});

/* Priority ComboBox */
var priorityComboForEdit = new Ext.form.ComboBox({
    typeAhead: true,
    triggerAction: 'all',
    lazyRender: true,
    editable: false,
    mode: 'local',
    store: priorityStoreForEdit,
    name: 'PriorityComboBox',
    valueField: 'Name',
    displayField: 'Name',
    fieldLabel: 'Priority',
    id: 'PriorityComboBoxForEdit'
});

/* Handler ComboBox */
var handlerComboForEdit = new Ext.form.ComboBox({
    typeAhead: true,
    triggerAction: 'all',
    lazyRender: true,
    editable: false,
    mode: 'local',
    store: handlerStoreForEdit,
    name: 'HandlerComboBox',
    valueField: 'Name',
    displayField: 'Name',
    fieldLabel: 'Handler',
    id: 'HandlerComboBoxForEdit'
});

/* WorkState ComboBox */
var workstateComboForEdit = new Ext.form.ComboBox({
    typeAhead: true,
    triggerAction: 'all',
    lazyRender: true,
    editable: false,
    mode: 'local',
    store: workstateStoreForEdit,
    name: 'WorkStateComboBox',
    valueField: 'Name',
    displayField: 'Name',
    fieldLabel: 'WorkState',
    id: 'WorkStateComboBoxForEdit'
});

var deadlineForEdit = new Ext.form.DateField({
	fieldLabel: 'Deadline',
	xtype: 'datefield',
	name: 'Deadline',
	format: 'Y/m/d',
	altFormats: 'Y/m/d'
});

/* Edit WorkItem Form */
ezScrum.EditWorkItemForm = Ext.extend(Ext.form.FormPanel, {
	// Default issue id
	issueId : '-1',
	typeId : '-1',
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
			// Ajax edit workitem url 
			url : 'ajaxEditWorkItem.do',
			// Ajax load workitem url
			loadUrl : 'getEditWorkItemInfo.do',
			
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
		        typeComboForEdit,
		        workstateComboForEdit,
		        priorityComboForEdit,
		        handlerComboForEdit,
		        {
		            fieldLabel: 'Size',
		            xtype: 'numberfield',
		            name: 'Size',
		            maxLength: 4
		        },
		        deadlineForEdit,
		        {
		        	fieldLabel: 'Description',
		            xtype: 'textarea',
		            name: 'Description',
		            height:200
		        }, {
		            name: 'issueTypeID',
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
		ezScrum.EditWorkItemForm.superclass.initComponent.apply(this, arguments);
					
		this.addEvents('LoadSuccess', 'LoadFailure', 'EditSuccess', 'EditFailure');
	},
	onRender:function() {
		ezScrum.EditWorkItemForm.superclass.onRender.apply(this, arguments);
		this.getForm().waitMsgTarget = this.getEl();

	},
	// Edit workitem action 
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
	// Load workitem success
	onLoadSuccess:function(response) 
	{
		var myMask = new Ext.LoadMask(this.getEl(), {msg:"Please wait..."});
		myMask.hide();
		var rs = jsonWorkItemReader.read(response);
		if(rs.success)
		{
			var record = rs.records[0];
			if(record)
			{
				typeComboForEdit.originalValue = record.data['Type'];
				typeComboForEdit.disabled = true;
				priorityComboForEdit.originalValue = '';
				handlerComboForEdit.originalValue = record.data['Handler'];
				workstateComboForEdit.originalValue = record.data['WorkState'];
				deadlineForEdit.originalValue = '';
				if (record.data['Type'] == "Task"){
					priorityComboForEdit.disable();
					deadlineForEdit.disable();
				}else{
					priorityComboForEdit.originalValue = record.data['Priority'];
					deadlineForEdit.originalValue = record.data['Deadline'];
					priorityComboForEdit.enable();
					deadlineForEdit.enable();
				}
				this.getForm().reset();
				this.getForm().setValues({issueID:record.data['Id'], Name : record.data['Name'], Size : record.data['Size'], Description : record.data['Description']});
				this.fireEvent('LoadSuccess', this, response, record);
			}
		}
	},
	onLoadFailure:function(response) 
	{
		var myMask = new Ext.LoadMask(this.getEl(), {msg:"Please wait..."});
		myMask.hide();
		this.fireEvent('LoadFailure', this, response, this.issueId);
	},
	// Update WorkItem success
	onEditSuccess:function(response) 
	{
		var myMask = new Ext.LoadMask(this.getEl(), {msg:"Please wait..."});
		myMask.hide();
		var rs = jsonWorkItemReader.read(response);
		if(rs.success)
		{
			var record = rs.records[0];
			if(record)
			{
				this.fireEvent('EditSuccess', this, record, typeComboForEdit.originalValue);
			}
		}
		else
			this.fireEvent('EditFailure', this, response, this.issueId);
	},
	// Update workitem failure
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
			params : {issueID : this.issueId, typeID : this.typeId}
		});
	},
	reset:function()
	{
		this.getForm().reset();
	}

});
Ext.reg('editWorkItemForm', ezScrum.EditWorkItemForm);

ezScrum.EditWorkItemWidget = Ext.extend(Ext.Window, {
	title:'Edit WorkItem',
	width:700,
	modal:false,
	closeAction:'hide',
	initComponent:function() {
		var config = {
			layout:'form',
			items : [{xtype:'editWorkItemForm'}]
        }
			Ext.apply(this, Ext.apply(this.initialConfig, config));
		ezScrum.EditWorkItemWidget.superclass.initComponent.apply(this, arguments);

		this.addEvents('LoadSuccess', 'LoadFailure', 'EditSuccess', 'EditFailure');
		
		this.items.get(0).on('LoadSuccess', function(obj, response, record){ this.fireEvent('LoadSuccess', this, obj, response, record); }, this);
		this.items.get(0).on('LoadFailure', function(obj, response, issueId){ this.fireEvent('LoadFailure', this, obj, response, issueId); }, this);
		this.items.get(0).on('EditSuccess', function(obj, record, workitemType){ this.fireEvent('EditSuccess', this, obj, record, workitemType); }, this);
		this.items.get(0).on('EditFailure', function(obj, response, issueId){ this.fireEvent('EditFailure', this, obj, response, issueId); }, this);
	},
	loadEditWorkItem:function(issueId, issueTypeID){
		this.items.get(0).reset();
		this.items.get(0).getForm().setValues({issueTypeID : issueTypeID});
		this.items.get(0).typeId = issueTypeID;
		this.items.get(0).issueId = issueId;
		this.show();
		this.items.get(0).loadStore();
	}
});