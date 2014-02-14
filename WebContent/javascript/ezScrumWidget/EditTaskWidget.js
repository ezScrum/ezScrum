Ext.ns('ezScrum');
Ext.ns('ezScrum.layout');
Ext.ns('ezScrum.window');

/***********************
 * for edit task partner menu
 */
var PartnerTriggerField_EditTask = new Ext.form.TriggerField({
    fieldLabel : 'Partners',
    name : 'Partners',
    editable   : false
});

var PartnerMenuForEditTask = new Ext.menu.Menu({
    /*
     * 當CheckItem被點選之後，更新TagTriggerField上的文字
     */
	id: 'PartnerMenu',
    onCheckItemClick : function(item, checked) {
        var tagRaw = PartnerTriggerField_EditTask.getValue();
        var tags;
        if (tagRaw.length != 0) {
            tags = tagRaw.split(";");
        } else {
            tags = [];
        }
        
        if (checked) {
        	if(tagRaw.search(item.text)<0) {
        		// 若field中已經存在該text, 不將該對應item 勾選
            	tags.push(item.text);
        	}
        } else {
            var index = tags.indexOf(item.text);
            tags.splice(index, 1);
        }
        PartnerTriggerField_EditTask.setValue(tags.join(";"));
    },
	loadPartnerList : function() {
		// to request partner list
		Ext.Ajax.request({
			url: 'AjaxGetPartnerList.do',
			success: function(response) {
				PartnerStore_ForEditTask.loadData(response.responseXML);
			}
		}); 
	}
});

var PartnerStore_ForEditTask = new Ext.data.Store({
    id: 0,
    fields: [
   		{name: 'Name'}
    ],
    reader : PartnerReader
});

PartnerStore_ForEditTask.on('load', function(store, records, options) {
	PartnerMenuForEditTask.removeAll();
	
	for(var i=0; i<this.getCount(); i++) {
		var record = this.getAt(i);
		var info = record.get('Name');
			
		PartnerMenuForEditTask.add({
			id		: info,
			tagId 	: info,
			text	: info,
			xtype	: 'menucheckitem',
			hideOnClick	: false,
			checkHandler: PartnerMenuForEditTask.onCheckItemClick
		});
	}
});

PartnerTriggerField_EditTask.onTriggerClick = function() {

	// A array of items of the menu
	var checkedItem = Ext.getCmp('PartnerMenu').findByType('menucheckitem');
	
	// the name list of the project team
	var partnerMenuList = PartnerTriggerField_EditTask.getValue().split(';');

	// initial the checked items
	for(var i=0; i<checkedItem.length; i++) {
		Ext.getCmp('PartnerMenu').get(checkedItem[i].text).setChecked(false);
	}
	
	// 將 field 欄位中的有的 partner, 在其對應的 menu item 打勾
	for(var i=0; i<checkedItem.length; i++) {
		for(var j=0; j<checkedItem.length; j++) {
			if(partnerMenuList[i] == checkedItem[j].text) {
				Ext.getCmp('PartnerMenu').get(checkedItem[j].text).setChecked(true);
			}
		}
	}
	
	PartnerMenuForEditTask.showAt(PartnerTriggerField_EditTask.getPosition());
};

/**
 * for edit task partner menu
 ***********************/

var HandlerComboStore_ForEditTask = new Ext.data.Store({
	id : 0,
	fields : [ {
		name : 'Name'
	} ],
	reader : ActorReader
});

/* Create Task Form */
ezScrum.EditTaskForm = Ext.extend(ezScrum.layout.TaskBoardCardWindowForm, {
	// Default issue id
	issueId : '-1',
	notifyPanel : undefined,
	initComponent:function() {
		
		var config = {
			// Ajax edit Task url 
			url : 'ajaxEditTask.do',
			// Ajax load Task url
			loadUrl : 'getEditTaskInfo.do',
			items: [{
		            fieldLabel: 'ID',
		            name: 'issueID',
					readOnly: true,
					xtype: 'hidden'
		        }, {
		            fieldLabel: 'Name',
		            name: 'Name',
		            allowBlank: false,	
					maxLength: 128
		        }, {
		        	fieldLabel: 'Handler',
		            name: 'HandlerComboBox_ForEditTask',
		            xtype: 'HandlerComboBox',		            
		            allowNegative: false
		        }, 
		        PartnerTriggerField_EditTask,
		        {
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
		this.HandlerCombo = this.items.items[2];
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
			params:form.getValues(),
			success:function(response){obj.onEditSuccess(response);},
			failure:function(response){obj.onEditFailure(response);}
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
					
					this.HandlerCombo.originalValue = record.data['Handler'];
					this.HandlerCombo.reset();
					
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
					
					// append issueID to window title
					EditTaskWindow.setTitle('Edit Task #' + record.data['Id']);
				}
			}
		}
	},
	onLoadFailure:function(response) {
		var myMask = new Ext.LoadMask(this.getEl(), {msg:"Please wait..."});
		myMask.hide();
		this.hide();
		Ext.example.msg('Load Task', 'Load Task Failure');
	},
	// Update Task success
	onEditSuccess:function(response) {
		var myMask = new Ext.LoadMask(this.getEl(), {msg:"Please wait..."});
		myMask.hide();
		
		var success = false;
    	var record = undefined;
		
		ConfirmWidget.loadData(response);
		if (ConfirmWidget.confirmAction()) {
			var rs = taskReader.readRecords(response.responseXML);
			if(rs.success) {
				var record = rs.records[0];
				if(record) {
					// 通知各個observer做更新
					this.notifyPanel.notify_EditTask(record);
				}
			} else {
				this.onEditFailure(response);
			}
		}
	},
	// Update Task failure
	onEditFailure:function(response) 
	{
		var myMask = new Ext.LoadMask(this.getEl(), {msg:"Please wait..."});
		myMask.hide();
		this.hide();
		Ext.example.msg('EditTask','Edit Task Failure.');
	},
	loadStore : function() {
		var obj = this;
		var myMask = new Ext.LoadMask(obj.getEl(), {
			msg : "Please wait..."
		});
		myMask.show();
		Ext.Ajax.request({
			url : obj.loadUrl,
			params : {
				issueID : obj.issueId,
				sprintID : obj.sprintId
			},
			success : function(response) {
				obj.onLoadSuccess(response);
			},
			failure : function(response) {
				obj.onLoadFailure(response);
			}
		});
		
		PartnerMenuForEditTask.loadPartnerList();
	},
	reset:function() {
		this.getForm().reset();
	}
});
Ext.reg('editTaskForm', ezScrum.EditTaskForm);

ezScrum.window.EditTaskWindow = Ext.extend(ezScrum.layout.Window, {
	title:'Edit Task',
	initComponent:function() {
		var config = {
			layout:'form',
			items : [{xtype:'editTaskForm'}]
        }
		
		Ext.apply(this, Ext.apply(this.initialConfig, config));
		ezScrum.window.EditTaskWindow.superclass.initComponent.apply(this, arguments);
	},
	loadEditTask:function(sprintID, issueId, panel){
		// 儲存對應的 panel，以 Observer 的形式各自運算 
		this.items.get(0).notifyPanel = panel;

		this.items.get(0).issueId = issueId;

		this.items.get(0).reset();
		this.items.get(0).getForm().setValues({sprintId : sprintID});

		this.show();
		this.items.get(0).loadStore();
	}
});


/*
 * 1. SprintBacklog.js
 * 2. TaskBoardCardFormPanel.ja
 * call method
 * 		1. editTask:function() 
 * 		2. function editTask(id)
 * 
 * notify method
 * 		1. notify_EditTask: function(record) 
 * 
 * shared with page: 
 * 		1. SprintBacklog
 * 		2. TaskBoard
 * */
var EditTaskWindow = new ezScrum.window.EditTaskWindow();