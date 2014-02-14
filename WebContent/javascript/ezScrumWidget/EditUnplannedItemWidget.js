Ext.ns('ezScrum');

var SprintCombo_EditUnplannedItem = new ezScrum.SprintComboWidget({
	id	: 'SprintCombo_EditUnplannedItem',
	fieldLabel: 'Sprint ID',
    name: 'SprintID',
    setNewValue: function(value) {
    	this.originalValue = value;
    	this.reset();
    }
});

var HandlerCombo_EditUnplannedItem = new ezScrum.HandlerComboWidget({
	fieldLabel: 'Handler',
    name: 'Handler'
});

HandlerCombo_EditUnplannedItem.addListener(
	'select', function(record, index) {
		if (index == 0) {
			StatusCombo_EditUnplannedItem.disabled = false;
		} else {
			StatusCombo_EditUnplannedItem.originalValue = "assigned";
			StatusCombo_EditUnplannedItem.reset();
		}
	}
);

var StatusCombo_EditUnplannedItem = new ezScrum.StatusComboWidget({
	fieldLabel: 'Status',
    name: 'Status',
	listeners: {
		'select': function(combo, record, index) {
    		if (record.data['status'] == "assigned") {
    			HandlerCombo_EditUnplannedItem.allowBlank = false;
    		} else if (record.data['status'] == "new") {
    			HandlerCombo_EditUnplannedItem.allowBlank = true;
    			HandlerCombo_EditUnplannedItem.originalValue = "";
    			HandlerCombo_EditUnplannedItem.reset();
   			} else {
   				HandlerCombo_EditUnplannedItem.allowBlank = true;
   			}
    	}
	}
});

/* Edit Unplanned Item Form */
ezScrum.EditUnplannedItemForm = Ext.extend(Ext.form.FormPanel, {
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
			// Ajax edit Unplanned Item url SprintCombo_EditUnplannedItem
			url : 'editUnplannedItem.do',
			// Ajax load Unplanned Item url
			loadUrl : 'showEditUnplannedItem.do',
			
			items: [
				{
		            name: 'issueID',
					hidden: true
		        },
				{
		            fieldLabel: 'Name',
		            name: 'Name',
		            allowBlank: false,
		            maxLength: 128
		        },
		        SprintCombo_EditUnplannedItem,
		        HandlerCombo_EditUnplannedItem,
		        StatusCombo_EditUnplannedItem,
		        {
		        	fieldLabel: 'Partners',
		            name: 'Partners'
		        },
		        {
		        	fieldLabel: 'Estimate',
		            name: 'Estimate',
		            xtype: 'numberfield',
		            allowNegative: false
		        },
		        {
		        	fieldLabel: 'Actual Hour',
		            name: 'ActualHour',
		            xtype: 'numberfield',
		            allowNegative: false
		        }, 
		        {
		        	fieldLabel: 'Notes',
		            xtype: 'textarea',
		            name: 'Notes',
		            height:200
		        },
		        {
		        	fieldLabel: 'Specific Time',
		        	xtype: 'datefield',
		            name: 'SpecificTime',
		            format: 'Y-m-d'
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
		ezScrum.EditUnplannedItemForm.superclass.initComponent.apply(this, arguments);
					
		this.addEvents('LoadSuccess', 'LoadFailure', 'EditSuccess', 'EditFailure');
	},
	onRender:function() {
		ezScrum.EditUnplannedItemForm.superclass.onRender.apply(this, arguments);
		this.getForm().waitMsgTarget = this.getEl();
	},
	// Edit Unplanned Item action 
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
	// Load Unplanned Item success
	onLoadSuccess:function(response) 
	{
		var myMask = new Ext.LoadMask(this.getEl(), {msg:"Please wait..."});
		myMask.hide();
		
		// check action permission
		ConfirmWidget.loadData(response);
		if (ConfirmWidget.confirmAction()) {
			var rs = UnplannedItemReader.readRecords(response.responseXML);
			if(rs.success)
			{
				var record = rs.records[0];
				if(record)
				{
					/* 初始化控制項為 Unplanned item 原本的值 */
					SprintCombo_EditUnplannedItem.setNewValue("Sprint #" + record.data['SprintID']);
					
					this.getForm().setValues({issueID : record.data['Id'], Name : record.data['Name'], Estimate : record.data['Estimate'], Status : record.data['Status'], ActualHour : record.data['ActualHour'], Handler : record.data['Handler'], Partners : record.data['Partners'], Notes : record.data['Notes']});
					this.fireEvent('LoadSuccess', this, response, record);
					
					// append issueID to window title
					EditUnplannedWidget.setTitle('Edit Unplanned Item #' + record.data['Id']);
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
	// Update Unplanned Item success
	onEditSuccess:function(response) 
	{
		var myMask = new Ext.LoadMask(this.getEl(), {msg:"Please wait..."});
		myMask.hide();
		
		// check action permission
		ConfirmWidget.loadData(response);
		if (ConfirmWidget.confirmAction()) {
			var rs = UnplannedItemReader.readRecords(response.responseXML);
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
		}
	},
	// Update Unplanned Item failure
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
Ext.reg('editUnplannedItemForm', ezScrum.EditUnplannedItemForm);

ezScrum.EditUnplannedItemWidget = Ext.extend(Ext.Window, {
	title:'Edit Unplanned Item',
	width:700,
	modal: true,
	constrain: true,
	closeAction:'hide',
	initComponent:function() {
		var config = {
			layout:'form',
			items : [{xtype:'editUnplannedItemForm'}]
        }
		
		Ext.apply(this, Ext.apply(this.initialConfig, config));
		ezScrum.EditUnplannedItemWidget.superclass.initComponent.apply(this, arguments);
		
		this.addEvents('LoadSuccess', 'LoadFailure', 'EditSuccess', 'EditFailure');
		
		this.items.get(0).on('LoadSuccess', function(obj, response, record){ this.fireEvent('LoadSuccess', this, obj, response, record); }, this);
		this.items.get(0).on('LoadFailure', function(obj, response, issueId){ this.fireEvent('LoadFailure', this, obj, response, issueId); }, this);
		this.items.get(0).on('EditSuccess', function(obj, response, record){ this.fireEvent('EditSuccess', this, obj, response, record); }, this);
		this.items.get(0).on('EditFailure', function(obj, response, issueId){ this.fireEvent('EditFailure', this, obj, response, issueId); }, this);
	},
	loadEditUnplannedItem: function(sprintID, issueId) {
		SprintCombo_EditUnplannedItem.setNewValue("Sprint #" + sprintID);
		
		this.items.get(0).issueId = issueId;
		this.items.get(0).reset();
		
		this.show();
		
		this.items.get(0).loadStore();
	}
});