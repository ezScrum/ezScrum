Ext.ns('ezScrum');
Ext.ns('ezScrum.window');

var filter_type_Backlog = "BACKLOG";
var filter_type_Detail = "DETAIL";
var filter_type_Done = "DONE";

var productBacklogTagMenu = null;
var productBacklogFilterMenu = null;
// Page size
var pageSize = 21;

// Delete Story Widget
var deleteStoryWidget = new ezScrum.DeleteStoryWidget({
	listeners: {
		DeleteSuccess: function(win, response, issueId) {
			Ext.getCmp('productBacklogGridPanel').deleteRecord(issueId);
			this.hide();
			Ext.example.msg('Delete Story', 'Delete Story Success.');
		},
		DeleteFailure: function(win, response, issueId) {
			Ext.example.msg('Delete Story', 'Delete Story Failure.');
		}
	}
});

// Import Stories Widget
var importStoriesWidget = new ezScrum.ImportStoriesWidget({
	listeners: {
		ImportSuccess: function(win) {
			LoadData_PB(null);
			this.hide();
			Ext.example.msg('Import Stories', 'Import Stories Success.');
		},
		ImportFailure: function(win, msg) {
			Ext.example.msg('Import Stories', msg);
		}
	}
});

function LoadData_PB(ft) {
	MainLoadMaskShow();

	Ext.Ajax.request({
		url: 'showProductBacklog2.do',
		params: {
			FilterType: ft
		},
		success: function(response) {
			ProductBacklogStore.proxy.data = response;
			ProductBacklogStore.proxy.reload = true;
			ProductBacklogStore.proxy.getSorters(getSorters());
			ProductBacklogStore.load({
				params: {
					start: 0,
					limit: pageSize
				}
			});
			initShowDetail();
			MainLoadMaskHide();
		}
	});

	// add grid panel tag filter
	Ext.Ajax.request({
		url: 'AjaxGetTagList.do',
		success: function(response) {
			var tagRs = tagReader.readRecords(response.responseXML);
			var tagCount = tagRs.totalRecords;

			var tagMenu = [];

			if (productBacklogTagMenu != null) {
				for ( var j = 0; j < tagCount; j++) {
					var tagRecord = tagRs.records[j];
					productBacklogTagMenu.menu.add({
						tagId: tagRecord.data['Id'],
						text: tagRecord.data['Name'],
						xtype: 'menucheckitem',
						hideOnClick: false
					});
					tagMenu.push(tagRecord.data['Name']);
				}
				IssueGridPanelFilter.addFilter({
					type: 'tag',
					dataIndex: 'Tag',
					options: tagMenu
				});
			}
		}
	});
}

function loadTagList(record) {
	var tagRs = [];
	var tagCount = 0;

	var loadmask = new Ext.LoadMask(productBacklogTagMenu.menu.getEl(), {
		msg: "loading info..."
	});
	loadmask.show();

	// get tag from auto load
	Ext.Ajax.request({
		url: 'AjaxGetTagList.do',
		success: function(response) {
			tagRs = tagReader.readRecords(response.responseXML);
			setTagInfo(tagRs, record);

			loadmask.hide();
		}
	});
}

function setTagInfo(tagRs, record) {
	productBacklogTagMenu.menu.removeAll();
	var tagMenu = [];
	for ( var j = 0; j < tagRs.totalRecords; j++) {
		var tagRecord = tagRs.records[j];
		productBacklogTagMenu.menu.add({
			tagId: tagRecord.data['Id'],
			text: tagRecord.data['Name'],
			xtype: 'menucheckitem',
			hideOnClick: false
		});
		tagMenu.push(tagRecord.data['Name']);
	}

	IssueGridPanelFilter.addFilter({
		type: 'tag',
		dataIndex: 'Tag',
		options: tagMenu
	});

	// set click items
	var tagRaw = record.data['Tag'];
	var tags = tagRaw.split(",");
	productBacklogTagMenu.menu.items.each(function() {
		this.setChecked(false);
		for ( var i = 0; i < tags.length; i++) {
			if (tags[i] != "" && this.text == tags[i]) {
				this.setChecked(true);
			}
		}
	});
}

// Product Backlog Panel
ezScrum.ProductBacklogGrid = Ext.extend(ezScrum.IssueGridPanel, {
	id: 'productBacklogGridPanel',
	store: ProductBacklogStore,
	colModel: ProductBacklogCreateColModel(),
	plugins: [IssueGridPanelFilter, ProductBacklogExpander],
	updateRecord: function(record) {
		var id = record.data['Id'];
		
		var data = this.getStore().getById(id);
		var index = this.getStore().indexOf(data);
		var expand = ProductBacklogExpander.isExpand(index);
		data.data = record.data;
		// 暫時使用這種方法強迫Store更新資料
		data.commit(true);
		this.getStore().afterCommit(data);
		if (expand) ProductBacklogExpander.expandRow(index);
		this.getSelectionModel().selectRow(index);
		this.getView().focusRow(index);
		this.getStore().proxy.updateRecord(record);
	},
	initComponent: function() {
		var config = {
			bbar: new Ext.PagingToolbar({
				pageSize: pageSize,
				store: ProductBacklogStore,
				displayInfo: true,
				displayMsg: 'Displaying topics {0} - {1} of {2}',
				emptyMsg: "No topics to display"
			})
		}

		Ext.apply(this, Ext.apply(this.initialConfig, config));
		ezScrum.ProductBacklogGrid.superclass.initComponent.apply(this, arguments);

		this.getSelectionModel().on({
			'selectionchange': {
				buffer: 10,
				fn: function() {
					Ext.getCmp('productBacklogMasterPanel').selectionChange();
				}
			}
		});
	}
});
Ext.reg('ProductBacklogGridPanel', ezScrum.ProductBacklogGrid);

/* Master Widget */
ezScrum.ProductBacklogPage = Ext.extend(Ext.Panel, {
	id: 'productBacklogMasterPanel',
	title: 'Product Backlog',
	panelName: 'ProductBacklog',
	layout: 'fit',
	isInit: false, // 判斷是否已經初始化元件,
	entryPoint: 'ProductBacklog',
	initComponent: function() {
		var tb = new ezScrum.productBacklog.TopToolbar({
			items: [{
				itemId: 'ProductBacklog_excelStoryBtn',
				text: 'Import / Export Story',
				icon: 'images/excel.png',
				menu: {
					items: [{
						text: 'Import Story',
						icon: 'images/import.png',
						handler: function() {
							importStoriesWidget.importFile();
						}
					}, {
						text: 'Export Story',
						icon: 'images/export.png',
						handler: function() {
							openURLWithCheckSession('exportStories.do');
						}
					}]
				}
			}, {
				itemId: 'ProductBacklog_addStoryBtn',
				text: 'Add Story',
				icon: 'images/add3.png',
				handler: function() {
					Ext.getCmp('productBacklogMasterPanel').AddStoryAction();
				}
			}, {
				itemId: 'ProductBacklog_editStoryBtn',
				disabled: true,
				text: 'Edit Story',
				icon: 'images/edit.png',
				handler: function() {
					Ext.getCmp('productBacklogMasterPanel').editStory();
				}
			}, {
				itemId: 'ProductBacklog_deleteStoryBtn',
				disabled: true,
				text: 'Delete Story',
				icon: 'images/delete.png',
				handler: function() {
					Ext.getCmp('productBacklogMasterPanel').deleteStory();
				}
			}, {
				itemId: 'ProductBacklog_showHistoryBtn',
				disabled: true,
				text: 'Show History',
				icon: 'images/history.png',
				handler: function() {
					Ext.getCmp('productBacklogMasterPanel').showHistory()
				}
			}, {
				itemId: 'ProductBacklog_manageTagBtn',
				text: 'Manage Tag',
				icon: 'images/magic-wand.png',
				handler: function() {
					Manage_Tag_Window.show();
				}
			}, {
				itemId: 'ProductBacklog_tagMenu',
				disabled: true,
				text: 'Tag',
				icon: 'images/folder.png',
				menu: {
					items: [],
					listeners: {
						itemclick: function(baseItem, e) {
							Ext.getCmp('productBacklogMasterPanel').itemClick(baseItem, e);
						},
						show: function(menu) {
							Ext.getCmp('productBacklogMasterPanel').showTagMenu();
						}
					}
				}
			}, {
				itemId: 'ProductBacklog_attachFileBtn',
				disabled: true,
				text: 'Attach File',
				icon: 'images/paperclip.png',
				handler: function() {
					Ext.getCmp('productBacklogMasterPanel').attachFile();
				}
			}, {
				itemId: 'ProductBacklog_filterMenu',
				disabled: false,
				text: 'Filter',
				icon: 'images/Filter.png',
				hideOnClick: true,
				menu: {
					items: [{
						tagId: 'Backlog_Filter',
						text: 'Backlogged',
						hideOnClick: true,
						icon: 'images/Filter.png',
						handler: function() {
							LoadData_PB(filter_type_Backlog);
							productBacklogFilterMenu.setText("Backlogged");
						}
					}, {
						tagId: 'Detail_Filter',
						text: 'Detailed',
						hideOnClick: true,
						icon: 'images/Filter.png',
						handler: function() {
							LoadData_PB(filter_type_Detail);
							productBacklogFilterMenu.setText("Detailed");
						}
					}, {
						tagId: 'Done_Filter',
						text: 'Done',
						hideOnClick: true,
						icon: 'images/Filter.png',
						handler: function() {
							LoadData_PB(filter_type_Done);
							productBacklogFilterMenu.setText("Done");
						}
					}, {
						tagId: 'No_Filter',
						text: 'Default',
						hideOnClick: true,
						icon: 'images/clear2.png',
						handler: function() {
							LoadData_PB(null);
							productBacklogFilterMenu.setText("Filter");
						}
					}]
				}
			}
			/*, {
				itemId: 'ProductBacklog_goToNewPage',
				text: 'New ProductBacklog V2',
				icon: 'images/arrow_right.png',
				handler: function() {
					window.location.assign("/ezScrum/showProductBacklogV2.do?PID=" + getURLParameter('PID'));
				}
			}*/
			]
		});

		// bottom bar
		var bb = new Ext.Toolbar({
			items: ['->', {
				text: 'Reload',
				icon: 'images/refresh.png',
				handler: function() {
					LoadData_PB(null);
				}
			}, {
				text: 'Clean All',
				icon: 'images/clear2.png',
				handler: function() {
					IssueGridPanelFilter.clearFilters();

					// clear search setting
					searchComboBox.reset();
					search_field.reset();
				}
			}, {
				text: 'Expand All',
				icon: 'images/folder_out.png',
				handler: function() {
					ProductBacklogExpander.expandAll();
				}
			}, {
				text: 'Collapse All',
				icon: 'images/folder_into.png',
				handler: function() {
					ProductBacklogExpander.collapseAll();
				}
			}]
		});

		var config = {
			items: [{
				xtype: 'ProductBacklogGridPanel',
				ref: 'ProductBacklog_GridPanel_ID'
			}],
			tbar: tb,
			bbar: bb
		}
		Ext.apply(this, Ext.apply(this.initialConfig, config));
		ezScrum.ProductBacklogPage.superclass.initComponent.apply(this, arguments);
	},
	listeners: {
		// before render view, we will get view info from server to show plug-in view
		'beforerender': function() {
			productBacklogTagMenu = Ext.getCmp('productBacklogToolbarId').getComponent('ProductBacklog_tagMenu');
			productBacklogFilterMenu = Ext.getCmp('productBacklogToolbarId').getComponent('ProductBacklog_filterMenu');
		}
	},
	// add story action
	AddStoryAction: function() {
		Story_Window.showTheWindow_Add(this);
	},
	notify_CreateStory: function(success, response, record) {
		Story_Window.hide();
		var title = 'Create Story';
		if (success) {
			Ext.getCmp('productBacklogGridPanel').addRecord(record);
			Ext.example.msg(title, 'Success.');
		} else {
			Ext.example.msg(title, 'Sorry, please try again.');
		}
	},
	notify_EditStory: function(success, response, record) {
		Story_Window.hide();
		var title = 'Edit Story';
		if (success) {
			Ext.getCmp('productBacklogGridPanel').updateRecord(record);
			Ext.example.msg(title, 'Success.');
		} else {
			Ext.example.msg(title, 'Sorry, please try again.');
		}
	},
	notify_AttachFile: function(success, record, msg) {
		AttachFile_Window.hide();

		if (success) {
			Ext.example.msg('Attach File', 'Attach File Success.');
			Ext.getCmp('productBacklogMasterPanel').ProductBacklog_GridPanel_ID.updateRecord(record);
		} else {
			Ext.example.msg('Attach File', msg);
		}
	},
	// Edit story action
	editStory: function() {
		var record = Ext.getCmp('productBacklogGridPanel').getSelectionModel().getSelected();
		if (record != null) {
			var id = record.data['Id'];

			// 如果Story的Status是closed，彈出詢問視窗
			if (record.data['Status'] == "closed") {
				var edit = true;
				var confirmInfo = 'The story is closed, are you sure to edit it?';
				this.overdueConfirm(confirmInfo, id, edit);
			} else {
				Story_Window.showTheWindow_Edit(this, id);
			}
		}
	},
	overdueConfirm: function(info, id, edit) {
		var panel = this;
		Ext.MessageBox.confirm('Confirm', info, function(btn) {
			if (btn == 'yes') {
				if (edit == true) {
					Story_Window.showTheWindow_Edit(panel, id);
				} else {
					deleteStoryWidget.deleteStory(id);
				}
			}
		});
	},
	// Delete story action
	deleteStory: function() {
		var record = Ext.getCmp('productBacklogGridPanel').getSelectionModel().getSelected();
		if (record != null) {
			var id = record.data['Id'];

			// 如果Story的Status是closed，彈出詢問視窗
			if (record.data['Status'] == "closed") {
				var edit = false;
				var confirmInfo = 'The story is closed, are you sure to delete it?';
				this.overdueConfirm(confirmInfo, id, edit);
			} else {
				deleteStoryWidget.deleteStory(id);
			}
		}
	},
	// Delete AttachFile action
	deleteAttachFile: function(file_Id, issue_Id) {
		Ext.MessageBox.confirm('Confirm', 'Are you sure you want to do that?', function(btn) {
			if (btn === 'yes') {
				Ext.Ajax.request({
					url: 'ajaxDeleteFile.do',
					params: {
						fileId: file_Id,
						issueId: issue_Id,
						issueType : 'Story'
					},
					success: function(response) {
						var rs = jsonStoryReader.read(response);
						if (rs.totalRecords == 1) {
							Ext.getCmp('productBacklogMasterPanel').ProductBacklog_GridPanel_ID.updateRecord(rs.records[0]);
							Ext.example.msg('Delete File', 'Success.');
						} else {
							Ext.example.msg('Delete File', 'Failure.');
						}
					},
					failure: function(response) {
						Ext.example.msg('Delete File', 'Failure.');
					}
				});
			}
		});
	},
	// Show history action
	showHistory: function() {
		var record = Ext.getCmp('productBacklogGridPanel').getSelectionModel().getSelected();
		if (record != null) {
			var id = record.data['Id'];
			var type = record.json['Type'];
			IssueHistory_Window.showTheWindow(id, type);
		}
	},
	// Show label menu action
	showTagMenu: function() {
		var record = Ext.getCmp('productBacklogGridPanel').getSelectionModel().getSelected();
		if (record != null) {
			loadTagList(record);
		}
	},
	attachFile: function() {
		var record = Ext.getCmp('productBacklogGridPanel').getSelectionModel().getSelected();
		if (record != null) {
			var id = record.data['Id'];
			var type = record.json['Type'];
			AttachFile_Window.attachFile(this, id, type);
		}
	},
	itemClick: function(baseItem, e) {
		var issueID = Ext.getCmp('productBacklogGridPanel').getSelectionModel().getSelected().data['Id'];

		if (baseItem.checked == false) {
			Ext.Ajax.request({
				url: 'AjaxAddStoryTag.do',
				params: {
					storyId: issueID,
					tagId: baseItem.tagId
				},
				success: function(response) {
					var rs = jsonStoryReader.read(response);
					if (rs.totalRecords == 1) {
						Ext.getCmp('productBacklogMasterPanel').ProductBacklog_GridPanel_ID.updateRecord(rs.records[0]);
					}
				}
			});
		} else {
			Ext.Ajax.request({
				url: 'AjaxRemoveStoryTag.do',
				params: {
					storyId: issueID,
					tagId: baseItem.tagId
				},
				success: function(response) {
					var rs = jsonStoryReader.read(response);
					if (rs.totalRecords == 1) Ext.getCmp('productBacklogMasterPanel').ProductBacklog_GridPanel_ID.updateRecord(rs.records[0]);
				}
			});
		}
	},
	selectionChange: function() {
		Ext.getCmp('productBacklogToolbarId').getComponent('ProductBacklog_editStoryBtn').enable();
		Ext.getCmp('productBacklogToolbarId').getComponent('ProductBacklog_deleteStoryBtn').enable();
		Ext.getCmp('productBacklogToolbarId').getComponent('ProductBacklog_showHistoryBtn').enable();
		productBacklogTagMenu.enable();
		Ext.getCmp('productBacklogToolbarId').getComponent('ProductBacklog_attachFileBtn').enable();
	},
	loadDataModel: function() {
		if (!this.isInit) {
			tbar_multisort.render(Ext.getCmp('productBacklogMasterPanel').tbar);
			// initial tool bar
			initButton_MultiSort();

			this.isInit = true;
		}
		LoadData_PB(null);
	}
});
Ext.reg('ProductBacklogPage', ezScrum.ProductBacklogPage);