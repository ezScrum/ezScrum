Ext.ns('ezScrum');
Ext.ns('ezScrum.window');
Ext.ns('ezScrum.layout');
// 用來確定AjaxTag是否已經成功，因為需要兩個Ajax的資料互相比對，目前找不到EXT的同步解法，所以用全域變數解決
var isAjaxTagReady = false;

var IssueTagStore = new Ext.data.Store({
	fields: [{
		name: 'Id',
		type: 'int'
	}, {
		name: 'Name'
	}],
	url: 'AjaxGetTagList.do',
	reader: IssueTagReader,
	autoLoad: true
});

var tagTriggerField = new Ext.form.TriggerField({
	fieldLabel: 'Tags',
	name: 'Tags',
	editable: false
});

tagTriggerField.onTriggerClick = function() {
	IssueTagMenu.showAt(tagTriggerField.getPosition());
};

var tagIDTextField = new Ext.form.TextField({
	xtype: 'textarea',
	name: 'TagIDs',
	hidden: true
});

IssueTagStore.on('load', function(store, records, options) {
	isAjaxTagReady = true;
	IssueTagMenu.removeAll();
	var tagCount = store.getTotalCount();
	for ( var i = 0; i < tagCount; i++) {
		var tagRecord = store.getAt(i);
		IssueTagMenu.add({
			tagId: tagRecord.data['Id'],
			text: tagRecord.data['Name'],
			xtype: 'menucheckitem',
			hideOnClick: false,
			checkHandler: IssueTagMenu.onCheckItemClick
		});
	}
});

var IssueTagMenu = new Ext.menu.Menu({
	onCheckItemClick: function(item, checked) {
		var tagRaw = tagTriggerField.getValue();
		var tagIDRaw = tagIDTextField.getValue();
		if (tagRaw.length != 0) {
			tags = tagRaw.split(",");
			tagIDs = tagIDRaw.split(",");
		} else {
			tags = [];
			tagIDs = [];
		}

		if (checked) {
			tags.push(item.text);
			tagIDs.push(item.tagId);
		} else {
			var index = tags.indexOf(item.text);
			tags.splice(index, 1);
			tagIDs.splice(index, 1);
		}

		tagTriggerField.setValue(tags.join(","));
		tagIDTextField.setValue(tagIDs.join(","));
	},
	setInitTagInfo: function(record) {
		// reload store data
		if (record !== undefined) {
			var tags = record.data['Tag'].split(',');

			for ( var i = 0; i < this.items.length; i++) {
				for ( var j = 0; j < tags.length; j++) {
					if (this.items.get(i).text == tags[j]) {
						this.items.get(i).setChecked(true);
					}
				}
			}
		}
	}
});

ezScrum.StoryForm = Ext.extend(Ext.form.FormPanel, {
	addurl: 'ajaxAddNewStory.do', // for add story action
	editurl: 'ajaxEditStory.do', // for edit story action
	loadUrl: 'getEditStoryInfo.do', // for load story action
	CurrentIssueID: '-1',
	EditRecord: undefined, // edit record
	isCreate: false, // set isCreate
	notifyPanel: undefined, // notify panel

	frame: false,
	autoScroll: true,
	bodyStyle: 'padding:15px',
	border: false,
	defaultType: 'textfield',
	labelAlign: 'right',
	labelWidth: 100,
	monitorValid: true,
	defaults: {
		width: 500,
		msgTarget: 'side'
	},
	initComponent: function() {
		var config = {
			items: [{
				fieldLabel: 'ID',
				name: 'issueID',
				readOnly: true,
				ref: 'Project_ProductBacklog_Story_refID',
				emptyText: '',
				xtype: 'hidden'
			}, {
				fieldLabel: 'Name',
				name: 'Name',
				allowBlank: false,
				maxLength: 128,
				height: 66,
				xtype: 'textarea'
			}, {
				fieldLabel: 'Value',
				name: 'Value',
				vtype: 'Number'
			}, {
				fieldLabel: 'Estimate',
				name: 'Estimate',
				vtype: 'Float'
			}, {
				fieldLabel: 'Importance',
				name: 'Importance',
				vtype: 'Number'
			}, {
				fieldLabel: 'Notes',
				xtype: 'textarea',
				name: 'Notes',
				height: 150
			}, tagTriggerField, tagIDTextField, {
				fieldLabel: 'How To Demo',
				xtype: 'textarea',
				name: 'HowToDemo',
				height: 150
			}, {
				name: 'SprintId',
				hidden: true
			}, {
				xtype: 'RequireFieldLabel'
			}],
			buttons: [{
				formBind: true,
				text: 'Submit',
				scope: this,
				disabled: true,
				handler: function() {
					if (this.isCreate) {
						this.AddSubmit();
					} else {
						this.EditSubmit();
					}
				}
			}, {
				text: 'Cancel',
				scope: this,
				handler: function() {
					this.ownerCt.hide();
				}
			}]
		}

		Ext.apply(this, Ext.apply(this.initialConfig, config));
		ezScrum.StoryForm.superclass.initComponent.apply(this, arguments);
	},
	AddSubmit: function() {
		var obj = this;
		var form = this.getForm();

		Ext.Ajax.request({
			url: obj.addurl,
			params: form.getValues(),
			success: function(response) {
				obj.onSuccess(response);
			},
			failure: function(response) { /* notify logon form, not finish yet */
			}
		});
	},
	EditSubmit: function() {
		var obj = this;
		var form = this.getForm();
		// update tag info
		IssueTagMenu.items.each(function() {
			obj.UpdateStoryTag(this.tagId, this.text, this.checked);
		});
		
		
		
		Ext.Ajax.request({
			url: obj.editurl,
			params: form.getValues(),
			success: function(response) {
				obj.onEditSuccess(response);
			},
			failure: function(response) { /* notify logon form, not finish yet */
			}
		});
	},
	onSuccess: function(response) {
		var success = false;
		var record = undefined;

		ConfirmWidget.loadData(response);
		if (ConfirmWidget.confirmAction()) {
			var rs = jsonStoryReader.read(response);
			success = rs.success;
			if (rs.success) {
				record = rs.records[0];
			}
		}

		this.notifyPanel.notify_CreateStory(success, response, record);
	},
	onEditSuccess: function(response) {
		var success = false;
		var record = undefined;

		ConfirmWidget.loadData(response);
		if (ConfirmWidget.confirmAction()) {
			var rs = jsonStoryReader.read(response);
			success = rs.success;
			if (rs.success) {
				record = rs.records[0];
			}
		}

		this.notifyPanel.notify_EditStory(success, response, record);
	},
	loadDataModel: function() {
		var obj = this;
		var form = this.getForm();

		Ext.Ajax.request({
			url: obj.loadUrl,
			params: {
				issueID: this.CurrentIssueID
			},
			success: function(response) {
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
			var rs = myReader.readRecords(response.responseXML);
			success = rs.success;
			if (rs.success) {
				record = rs.records[0];
				if (record) {
					this.EditRecord = record;

					// set initial tag value
					if (isAjaxTagReady) {
						IssueTagMenu.setInitTagInfo(record);
						isAjaxTagReady = false;
					} else {
						setTimeout(function() {
							IssueTagMenu.setInitTagInfo(record);
						}, 1000)
					}

					var replaced_Name = replaceJsonSpecialChar(record.data['Name']);
					var replaced_Notes = replaceJsonSpecialChar(record.data['Notes']);
					var replaced_HowToDemo = replaceJsonSpecialChar(record.data['HowToDemo']);

					// set initial from value
					this.getForm().setValues({
						issueID: record.data['Id'],
						Name: replaced_Name,
						Value: record.data['Value'],
						Importance: record.data['Importance'],
						Estimate: record.data['Estimate'],
						Notes: replaced_Notes,
						HowToDemo: replaced_HowToDemo
					});

					// append issueID to window title
					Story_Window.setTitle(Story_Window.title + ' #' + record.data['Id']);
				}
			}
		}
	},
	UpdateStoryTag: function(tagId, text, checked) {
		var recordTags = this.EditRecord.data['Tag'].split(',');
		var storyid = this.EditRecord.data['Id'];
		var tagExist = false;

		for ( var i = 0; i < recordTags.length; i++) {
			if (text == recordTags[i]) {
				tagExist = true;
				i = recordTags.length;
			}
		}

		if (tagExist == true && checked == true) {
		} else if (tagExist == true && checked == false) {
			Ext.Ajax.request({
				url: 'AjaxRemoveStoryTag.do',
				success: function(response) {
				},
				params: {
					storyId: storyid,
					tagId: tagId
				}
			});
		} else if (tagExist == false && checked == true) {
			Ext.Ajax.request({
				url: 'AjaxAddStoryTag.do',
				success: function(response) {
				},
				params: {
					storyId: storyid,
					tagId: tagId
				}
			});
		} else if (tagExist == false && checked == false) {
		}
	},
	reset: function() {
		this.getForm().reset();
		this.isCreate = true; // default
		IssueTagStore.reload();
	},
	initialAddForm: function() {
		this.reset();
		this.Project_ProductBacklog_Story_refID.disable();
	},
	initialEditForm: function() {
		this.reset();
		this.Project_ProductBacklog_Story_refID.enable();
	}
});

Ext.reg('TheStoryForm', ezScrum.StoryForm);

ezScrum.window.StoryWindow = Ext.extend(ezScrum.layout.Window, {
	title: ' ',
	bodyStyle: 'padding: 5px',
	initComponent: function() {
		var config = {
			layout: 'form',
			items: [{
				xtype: 'TheStoryForm'
			}]
		}

		Ext.apply(this, Ext.apply(this.initialConfig, config));
		ezScrum.window.StoryWindow.superclass.initComponent.apply(this, arguments);

		this.StoryForm = this.items.get(0);
	},
	showTheWindow_Add: function(panel) {
		// initial form info
		this.StoryForm.reset();
		this.StoryForm.notifyPanel = panel;

		// initial window info
		this.setTitle('Add New Story');
		this.show();
	},
	showTheWindow_Add: function(panel, sprintID) {
		// initial form info
		this.StoryForm.initialAddForm();
		this.StoryForm.getForm().setValues({
			SprintId: sprintID
		});
		this.StoryForm.notifyPanel = panel;

		// initial window info
		this.setTitle('Add New Story');
		this.show();
	},
	showTheWindow_Edit: function(panel, issuueID) {
		// initial form info
		this.StoryForm.initialEditForm();
		this.StoryForm.CurrentIssueID = issuueID;
		this.StoryForm.notifyPanel = panel;
		this.StoryForm.isCreate = false;
		this.StoryForm.loadDataModel();

		// initial window info
		this.setTitle('Edit Story');
		this.show();
	}
});

/*
 * call method 1. showTheWindow_Add: function(panel, sprintID) 2. showTheWindow_Edit: function(panel, issuueID)
 * 
 * notify method 1. notify_CreateStory: function(success, response, record) 2. notify_EditStory: function(success, response, record)
 * 
 * shared with: 1. ProductBacklog 2. SprintBacklog 3. TaskBoard
 */
var Story_Window = new ezScrum.window.StoryWindow();