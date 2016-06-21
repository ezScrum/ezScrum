Ext.ns('ezScrum');
Ext.ns('ezScrum.window');
Ext.ns('ezScrum.layout');

var isParents = false;

var PartnerStore_ForSprintInfo = new Ext.data.Store({
    id: 0,
    fields: [
   		{name: 'Name'}
    ],
    reader : PartnersReader
});

var PartnerTriggerField_SprintInfo = new Ext.form.TriggerField({
    fieldLabel : 'Receivers (ezScrum members)',
    name : 'Receivers (ezScrum members)',
    editable   : false,
    allowBlank: false
});
PartnerStore_ForSprintInfo.on('load', function(store, records, options) {
	isParents = true;
	PartnerMenuForSprintInfo.removeAll();
	
	for(var i=0; i<this.getCount(); i++) {
		var record = this.getAt(i);
		var info = record.get('Name');

		PartnerMenuForSprintInfo.add({
			id		: info,
			tagId 	: info,
			text	: info,
			xtype	: 'menucheckitem',
			hideOnClick	: false,
			checkHandler : PartnerMenuForSprintInfo.onCheckItemClick
		});
	}
});
PartnerTriggerField_SprintInfo.onTriggerClick = function() {
	
	// A array of items of the menu
	var checkedItem = Ext.getCmp('PartnerMenu').findByType('menucheckitem');

	// the name list of the project team
	var partnerMenuList = PartnerTriggerField_SprintInfo.getValue().split(';');
	
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
	PartnerMenuForSprintInfo.showAt(PartnerTriggerField_SprintInfo.getPosition());
};


var PartnerMenuForSprintInfo = new Ext.menu.Menu({
	/*
     * When CheckItem was checked，update TagTriggerField's text
     */
	id: 'PartnerMenu',
	onCheckItemClick : function(item, checked){
		var tagRaw = PartnerTriggerField_SprintInfo.getValue();
		var tags;
		if (tagRaw.length != 0) {
            tags = tagRaw.split(";");
        } else {
            tags = [];
        }
		if (checked) {
        	if(tagRaw.search(item.text)<0) {
        		// if field has text, should not check item
            	tags.push(item.text);
        	}
        } else {
            var index = tags.indexOf(item.text);
            tags.splice(index, 1);
        }
        PartnerTriggerField_SprintInfo.setValue(tags.join(";"));
	},
	loadPartnerList : function() {
		// to request partner list
		Ext.Ajax.request({
			url: 'AjaxGetPartnerList.do',
			success: function(response) {
				PartnerStore_ForSprintInfo.loadData(response.responseXML);
			}
		}); 
	}
});

ezScrum.SprintInfoForm = Ext.extend(Ext.form.FormPanel, {
	loadUrl : 'generatePreviewContent.do',
	sendUrl : 'SendSprintMail.do',
	bodyStyle: 'padding:15px',
	labelWidth : 150,
	SprintRecord: undefined,
	notifyPanel: undefined, // notify panel
	defaultType: 'textfield',
	labelAlign : 'right',
	monitorValid: true,
	defaults: {
		width: 500,
		msgTarget: 'side'
	},
	initComponent: function() {
		var config = {
				items: [{
		        	fieldLabel: 'subject',
		            name: 'subject',
		            allowBlank: false,
		            maxLength: 128
		        },PartnerTriggerField_SprintInfo,
		        {
		        	fieldLabel: 'Sprint Goal',
		            name: 'sprintGoal',
		            allowBlank: false,
		            maxLength: 128
		        }, {
		        	fieldLabel: 'Schedule',
		            name: 'schedule',
		            xtype: 'textarea',
		            allowBlank: false
		        },{
		        	fieldLabel: 'Stroy Info',
		            name: 'storyInfo',
		            xtype: 'textarea',
		            allowBlank: false,
					height: 150
		        },{
		            name: 'sprintId',
		            hidden: true
		        }],
		        buttons:
		        [{
		        	formBind:true,
		    		text: 'Send',
		    		scope:this,
		    		disabled: true,
		    		handler: this.send
		        },{
		    		text: 'Cancel',
		    		scope:this,
		    		handler: function(){this.ownerCt.hide();}
		        }]
		}
		Ext.apply(this, Ext.apply(this.initialConfig, config));
		ezScrum.SprintInfoForm.superclass.initComponent.apply(this, arguments);
	},
	send : function()
	{
		var obj = this;
		var form = this.getForm();
		Ext.Ajax.request({
			url:this.sendUrl,
			params:form.getValues(),
			success:function(response){
				obj.onSuccess(response);
			},
			failure:function(response){}
		});
	},
	loadDataModel: function() {
		var obj = this;
		var form = this.getForm();
		
		Ext.Ajax.request({
			url: obj.loadUrl,
			params: {
				sprintID : this.sprintID
			},
			success: function(response) {
//				console.log(response);
				obj.onLoadSuccess(response);
			},
			failure: function(response) { /* notify logon form, not finish yet */
			}
		});
	},
	onLoadSuccess: function(response) {
		var success = false;
		var record = undefined;
		ConfirmWidget.loadData(response);
		if (ConfirmWidget.confirmAction()) {
			var rs = PreviewReader.readRecords(response.responseXML);
			success = rs.success;
			console.log(response);
			if (rs.success) {
				record = rs.records[0];
				console.log(rs);
				if (record) {
					console.log(response);
					this.SprintRecord = record;
					
					var replaced_Subject = replaceJsonSpecialChar(record.data['subject']);
					var replaced_SprintGoal = replaceJsonSpecialChar(record.data['sprintGoal']);
					var replaced_StoryInfo = replaceJsonSpecialChar(record.data['storyInfo']);
					var replaced_Schedule = replaceJsonSpecialChar(record.data['schedule']);
					// set initial from value
					this.getForm().setValues({
						subject: record.data['subject'],
						sprintGoal: replaced_SprintGoal,
						storyInfo: replaced_StoryInfo,
						schedule: replaced_Schedule
					});
					
				}
			}
		}
	},
	reset: function(){
		this.getForm().reset();
		PartnerMenuForSprintInfo.loadPartnerList();
	}
});
Ext.reg('sprintInfoForm', ezScrum.SprintInfoForm);

ezScrum.window.SendSprintInfoEmailWindow = Ext.extend(ezScrum.layout.Window, {
	title: 'Send Sprint Info',
	initComponent: function(){
		var config = {
			layout:'form',
			items : [{xtype:'sprintInfoForm'}]
		}
		Ext.apply(this, Ext.apply(this.initialConfig, config));
		ezScrum.window.SendSprintInfoEmailWindow.superclass.initComponent.apply(this, arguments);
		
		this.SprintInfoForm = this.items.get(0);
	},
	showTheWindow: function(panel, sprintID){
		this.SprintInfoForm.notifyPanel = panel;
		this.SprintInfoForm.sprintID = sprintID;
		this.SprintInfoForm.reset();
		this.SprintInfoForm.getForm().setValues({SprintId: sprintID});
		
		this.SprintInfoForm.loadDataModel();
		this.show();
	}
});
var SendSprintInfoEmailWindow = new ezScrum.window.SendSprintInfoEmailWindow();