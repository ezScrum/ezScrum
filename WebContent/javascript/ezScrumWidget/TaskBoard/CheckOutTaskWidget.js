Ext.ns('ezScrum');
Ext.ns('ezScrum.layout');
Ext.ns('ezScrum.window');

/***********************
 * for partner menu
 */

var PartnerTriggerField_CheckOut = new Ext.form.TriggerField({
    fieldLabel : 'Partners',
    name       : 'Partners',
    editable   : false
});

var PartnerMenu = new Ext.menu.Menu({
    /*
     * 當CheckItem被點選之後，更新TagTriggerField上的文字
     */
    onCheckItemClick : function(item, checked) {
        var tagRaw = PartnerTriggerField_CheckOut.getValue();
        if (tagRaw.length != 0) {
            tags = tagRaw.split(";");
        } else {
            tags = [];
        }
        
        if (checked) {
            tags.push(item.text);
        } else {
            var index = tags.indexOf(item.text);
            tags.splice(index, 1);
        }
        
        PartnerTriggerField_CheckOut.setValue(tags.join(";"));
    }
});

var PartnerStore_ForCheckOut = new Ext.data.Store({
    id: 0,
    fields: [
   		{name: 'Name'}
    ],
    reader : PartnerReader
});

PartnerStore_ForCheckOut.on('load', function(store, records, options) {
	PartnerMenu.removeAll();

	for(var i=0; i<this.getCount(); i++) {
		var record = this.getAt(i);
		var info = record.get('Name');
		
		PartnerMenu.add({
			tagId 	: info,
			text	: info,
			xtype	: 'menucheckitem',
			hideOnClick	: false,
			checkHandler: PartnerMenu.onCheckItemClick
		});
	}
});

PartnerTriggerField_CheckOut.onTriggerClick = function() {
	PartnerMenu.showAt(PartnerTriggerField_CheckOut.getPosition());
};

/**
 * for partner menu
 ***********************/


var TaskStore_ForCheckOutTask = new Ext.data.Store({
	idIndex	: 0,
	id		: 0,
	fields	:[
		{name : 'Id'},
		{name : 'Name'},
		{name : 'Notes'},
		{name : 'Handler'},
		{name : 'Partners'}
		],
	reader	: taskJSReader
});

// Check out Issue Form
ezScrum.CheckOutForm = Ext.extend(ezScrum.layout.TaskBoardCardWindowForm, {
    initComponent : function() {
        var config = {
            url     : 'checkOutTask.do',
            loadUrl : 'showCheckOutIssue.do',
            items   : 
            		[{
                        fieldLabel	: 'ID',
                        name      	: 'Id',
                        readOnly	: true,
                        xtype: 'hidden'
                    },
                    {
                        fieldLabel	: 'Task Name',
                        name      	: 'Name',
                        allowBlank	: false
                    },
                    {
    		        	id			: 'CheckOutTask_HandlerCombo',
    		        	fieldLabel	: 'Handler',
    		            name		: 'Handler',
    		            xtype		: 'HandlerComboBox'
    		        },
    		        PartnerTriggerField_CheckOut,
                    {
                        fieldLabel	: 'Notes',
                        xtype     	: 'textarea',
                        name      	: 'Notes',
                        height    	: 150
                    },
                    {
                    	allowBlank	: true,
                    	fieldLabel	: 'Specific Checked Out Time',
                    	name		: 'ChangeDate',
                    	format		: 'Y/m/d-H:i:s',
                    	xtype		: 'datefield'
                    },{
                    	xtype: 'RequireFieldLabel'
                    }],
            buttons : 
            		[{
						formBind : true,
                        text     : 'Check Out',
                        scope    : this,
                        handler  : this.submit,
                        disabled : true
                    }, {
                        text    : 'Cancel',
                        scope   : this,
                        handler : function() {	this.ownerCt.hide();  }
                    }]
        }

        Ext.apply(this, Ext.apply(this.initialConfig, config));
        ezScrum.CheckOutForm.superclass.initComponent.apply(this, arguments);

        this.HandlerCombo = this.items.items[2];
        this.addEvents('COSuccess', 'COFailure', 'LoadTaskFailure');
    },
    submit: function() {
        var form = this.getForm();
        var obj = this;
        
        Ext.Ajax.request({
			url     : this.url,
			params  : form.getValues(),
			success : function(response) {
				obj.onEditSuccess(response);
			},
			failure : function(response) {
				obj.onEditFailure(response);
			}		
		});
    },
    onEditSuccess: function(response) {
    	ConfirmWidget.loadData(response);
    	
    	if (ConfirmWidget.confirmAction()) {
			var rs = jsonIssueReader.read(response);
			if(rs.success) {
				var record = rs.records[0];
				if(record)
				{
					this.fireEvent('COSuccess', this, response, record);
				}
			} else {
				this.fireEvent('COFailure', this, response);
			}
		}
    },
    onEditFailure: function(response) {
        this.fireEvent('COFailure', this, response);
    },
    onLoadSuccess: function(response) {
    	ConfirmWidget.loadData(response);
    	if (ConfirmWidget.confirmAction()) {
			TaskStore_ForCheckOutTask.loadData(Ext.decode(response.responseText));	// load task info
			var record = TaskStore_ForCheckOutTask.getAt(0);
			if(record) {
				this.getForm().setValues({
					Id: record.data['Id'],
					Name: record.data['Name'], 
					Partners: record.data['Partners'],
					Notes: record.data['Notes']
				});
				
				this.HandlerCombo.originalValue = record.data['Handler'];
				this.HandlerCombo.reset();
				
				// append issueID to window title. "CheckOutTaskWindow" define in TaskBoardCardFormPanel.js
				CheckOutTaskWindow.setTitle('Check Out Task #' + record.data['Id']);
			}
    	}
    },
    onLoadFailure: function(response) {
        this.fireEvent('LoadTaskFailure', this, response);
    },
    reset: function() {
        this.getForm().reset();
    },
    loadTask: function(id) {
        var obj = this;
    	Ext.Ajax.request({
			url: obj.loadUrl,
			params : {issueID : id, issueType : 'Task'},
			success: function(response) { 
				obj.onLoadSuccess(response);
			},
			failure: function(response) { obj.onLoadFailure(response); }
		});
		
		Ext.Ajax.request({
			url: 'AjaxGetPartnerList.do',
			success: function(response) { PartnerStore_ForCheckOut.loadData(response.responseXML); }
		});
    }
});

Ext.reg('CheckOutTaskForm', ezScrum.CheckOutForm);

ezScrum.window.CheckOutWindow = Ext.extend(ezScrum.layout.Window, {
	title	: 'Check Out Task',
	initComponent : function() {
		var config = {
			layout : 'form',
			items  : [{	xtype : 'CheckOutTaskForm'	}]
		}
		
		Ext.apply(this, Ext.apply(this.initialConfig, config));
		ezScrum.window.CheckOutWindow.superclass.initComponent.apply(this, arguments);

		this.addEvents('CheckOutSuccess', 'CheckOutFailure', 'LoadFailure');		
		this.items.get(0).on('COSuccess', function(obj, response, record) { this.fireEvent('CheckOutSuccess', this, response, record); }, this);
		this.items.get(0).on('COFailure', function(obj, response) { this.fireEvent('CheckOutFailure', this, response); }, this);
		this.items.get(0).on('LoadTaskFailure', function(obj, response) { this.fireEvent('LoadFailure', this, response); }, this);
	},
	getFormValues:function(valueName) {
		return this.items.get(0).getForm().getValues();
	},
	showWidget : function(taskID) {
		this.items.get(0).reset();
		this.items.get(0).loadTask(taskID);
		this.show();
	}
});