Ext.ns('ezScrum');

/* Retrospective Type ComboBox 預設值為Good */
var typeComboForEdit = new Ext.form.ComboBox({
    typeAhead: true,
    triggerAction: 'all',
    lazyRender: true,
    editable: false,
    mode: 'local',
    store: new Ext.data.ArrayStore({
        id: 0,
        fields: [
            {name: 'Id'},
       		{name: 'Name'}
        ],
        data: [['Good', 'Good'], ['Improvement', 'Improvement']]
    }),
    name: 'Type',
    valueField: 'Id',
    displayField: 'Name',
    fieldLabel: 'Type',
    originalValue: 'Good'
});



/* Retrospective Status ComboBox 預設值為new */
var statusCombo = new Ext.form.ComboBox({
    typeAhead: true,
    triggerAction: 'all',
    lazyRender: true,
    editable: false,
    mode: 'local',
    store: new Ext.data.ArrayStore({
        id: 0,
        fields: [
            {name: 'Id'},
       		{name: 'Name'}
        ],
        data: [['new', 'new'], ['closed', 'closed'], ['resolved', 'resolved'], ['assigned', 'assigned']]
    }),
    name: 'Status',
    valueField: 'Id',
    displayField: 'Name',
    fieldLabel: 'Status',
    originalValue: 'New'
});

var sprintComboStoreForEdit = new Ext.data.Store({
    id: 0,
    fields: [
        {name: 'Id', type: 'int'},
   		{name: 'Name'}
    ],
    reader:sprintForComboReader
});

/* Sprint ComboBox */
var sprintComboForEdit = new Ext.form.ComboBox({
    typeAhead: true,
    triggerAction: 'all',
    lazyRender: true,
    editable: false,
    mode: 'local',
    store: sprintComboStoreForEdit,
    name: 'SprintComboBox',
    valueField: 'Id',
    displayField: 'Name',
    fieldLabel: 'Sprint',
    id: 'SprintComboBoxForEdit'
});

/* 取得 Sprint 資料 */
Ext.Ajax.request({
	url:'getAddNewRetrospectiveInfo.do',
	success:function(response){
		sprintComboStoreForEdit.loadData(response.responseXML);
	}
});

/* Edit Retrospective Form */
ezScrum.EditRetrospectiveForm = Ext.extend(Ext.form.FormPanel, {
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
			// Ajax edit Retrospective url 
			url : 'ajaxEditRetrospective.do',
			// Ajax load Retrospective url
			loadUrl : 'getEditRetrospectiveInfo.do',
			
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
		        sprintComboForEdit
		        ,
		        typeComboForEdit
		        ,
		        statusCombo
		        ,{
		        	fieldLabel: 'Description',
		            xtype: 'textarea',
		            name: 'Description',
		            height:200
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
		ezScrum.EditRetrospectiveForm.superclass.initComponent.apply(this, arguments);
					
		this.addEvents('LoadSuccess', 'LoadFailure', 'EditSuccess', 'EditFailure');
	},
	onRender:function() {
		ezScrum.EditRetrospectiveForm.superclass.onRender.apply(this, arguments);
		this.getForm().waitMsgTarget = this.getEl();

	},
	// Edit Retrospective action 
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
	// Load Retrospective success
	onLoadSuccess:function(response) 
	{
		var myMask = new Ext.LoadMask(this.getEl(), {msg:"Please wait..."});
		myMask.hide();
		// check action permission
		ConfirmWidget.loadData(response);
		if (ConfirmWidget.confirmAction()) {
			var rs = retReader.readRecords(response.responseXML);
			if(rs.success)
			{
				var record = rs.records[0];
				if(record)
				{
					/* 初始化控制項為 Retrospective 原本的值 */
					sprintComboForEdit.originalValue = "Sprint #" + record.data['SprintID'];
					typeComboForEdit.originalValue = record.data['Type'];
					statusCombo.originalValue = record.data['Status'];
					this.getForm().reset();
					this.getForm().setValues({issueID:record.data['Id'], Name : record.data['Name'], Description : record.data['Description']});
					
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
	// Update Retrospective success
	onEditSuccess:function(response) 
	{
		var myMask = new Ext.LoadMask(this.getEl(), {msg:"Please wait..."});
		myMask.hide();
		var rs = retReader.readRecords(response.responseXML);
		if(rs.success)
		{
			var record = rs.records[0];
			if(record)
			{
				this.fireEvent('EditSuccess', this, response, record);
			}
		}
		else
			this.fireEvent('EditFailure', this, response, this.issueId);
	},
	// Update Retrospective failure
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
			params : {issueID : this.issueId}
		});
	},
	reset:function()
	{
		this.getForm().reset();
	}

});
Ext.reg('editRetrospectiveForm', ezScrum.EditRetrospectiveForm);

ezScrum.EditRetrospectiveWidget = Ext.extend(Ext.Window, {
	title:'Edit Retrospective',
	width:700,
	modal: true,
	constrain : true,
	closeAction:'hide',
	initComponent:function() {
		var config = {
			layout:'form',
			items : [{xtype:'editRetrospectiveForm'}]
        }
			Ext.apply(this, Ext.apply(this.initialConfig, config));
		ezScrum.EditRetrospectiveWidget.superclass.initComponent.apply(this, arguments);
		
		this.addEvents('LoadSuccess', 'LoadFailure', 'EditSuccess', 'EditFailure');
		
		this.items.get(0).on('LoadSuccess', function(obj, response, record){ this.fireEvent('LoadSuccess', this, obj, response, record); }, this);
		this.items.get(0).on('LoadFailure', function(obj, response, issueId){ this.fireEvent('LoadFailure', this, obj, response, issueId); }, this);
		this.items.get(0).on('EditSuccess', function(obj, response, record){ this.fireEvent('EditSuccess', this, obj, response, record); }, this);
		this.items.get(0).on('EditFailure', function(obj, response, issueId){ this.fireEvent('EditFailure', this, obj, response, issueId); }, this);
	},
	loadEditRetrospective:function(issueId){
		this.items.get(0).issueId = issueId;
		this.items.get(0).reset();
		this.show();
		this.items.get(0).loadStore();
	}
});