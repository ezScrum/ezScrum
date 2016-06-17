Ext.ns('ezScrum');
Ext.ns('ezScrum.layout');
Ext.ns('ezScrum.window');

var Page_Selected_items = new Array();

/*var ExistedStory_Expander = new Ext.ux.grid.RowExpander({
    tpl : new Ext.Template(
        '<br><p><b>Name:</b><br /> {Name:nl2br}</p>',
        '<p><b>Notes:</b><br /> {Notes:nl2br}</p>',
        '<p><b>How To Demo:</b><br /> {HowToDemo:nl2br}</p><br />'
    )
});*/

var SelectedTasks_CheckBoxModel = new Ext.grid.CheckboxSelectionModel({
	listeners:{
		selectionchange: function() {
   			if (this.getCount() > 0 || Page_Selected_items.length > 0) {
				Ext.getCmp('SelectTasks_Window').getTopToolbar().get('SelectedTasksBtn').enable();
   			} else {
				Ext.getCmp('SelectTasks_Window').getTopToolbar().get('SelectedTasksBtn').disable();
   			}
		}
	}
});

var SelectedTasks_Filter = new Ext.ux.grid.GridFilters({
	local:true,
    filters: [{
        type: 'numeric',
        dataIndex: 'Id'
    },{
    	type: 'list',
    	dataIndex: 'Status',
    	options: ['new', 'closed']
    }]
});

var SelectedTasksColumnModel = function() {
    var columns = [

		//ExistedStory_Expander, 
		SelectedTasks_CheckBoxModel,
		{dataIndex: 'Id', header: 'Id', width: 50, filterable: true/*, renderer: makeIssueDetailUrl*/},
		{dataIndex: 'Name', header: 'Name', width: 250},
		{dataIndex: 'StoryId', header: 'StoryId', width: 70},
		{dataIndex: 'Estimate', header: 'Estimate', width: 70},
		{dataIndex: 'Status', header: 'Status', width: 70}
	];

    return new Ext.grid.ColumnModel({
        columns: columns,
        defaults: {
            sortable: true
        }
    });
};

var SelectedTaskStore = new Ext.data.Store({
	fields: [
		{name: 'Id', type: 'int'},
		{name: 'Link'},
		{name: 'Name'},
		{name: 'StoryId', type: 'int'},
		{name: 'Estimate', type: 'float'},
		{name: 'Status'},
		{name: 'Notes'},
	],
	reader : SelectTaskReader,
	proxy : new Ext.ux.data.PagingMemoryProxy(),
	remoteSort : true
});

ezScrum.SelectedTasksGridPanel = Ext.extend(Ext.grid.GridPanel, {
	frame		: false,
	stripeRows	: true,
	sprintID	: '-1',
	storyID		: '-1',
	url			: 'showSelectableTask.do',
	store		: SelectedTaskStore,
	colModel	: SelectedTasksColumnModel(),
	plugins		: [ SelectedTasks_Filter ],
	sm			: SelectedTasks_CheckBoxModel,
	viewConfig	: {
        forceFit: true
    },
   
    loadDataModel: function() {
    	var obj = this;
    	var loadmask = new Ext.LoadMask(obj.getEl(), {msg: "loading info..."});
		loadmask.show();
    	Ext.Ajax.request({
			url: obj.url,
			params: {
				sprintID: obj.sprintID,
				storyID: obj.storyID
			},
			success: function(response) {
				console.log("here is success");
				console.log(response);
				ConfirmWidget.loadData(response);
				console.log(response);
    			if (ConfirmWidget.confirmAction()) {
					SelectedTaskStore.loadData(response.responseXML);
					SelectedTaskStore.proxy.data = response;
					SelectedTaskStore.proxy.reload = true;
					SelectedTaskStore.load({params: {start: 0, limit: 15}});
    			}
    			
    			var loadmask = new Ext.LoadMask(obj.getEl(), {msg: "loading info..."});
    			loadmask.hide();
			},
			failure: function(response) {
    			var loadmask = new Ext.LoadMask(obj.getEl(), {msg: "loading info..."});
    			loadmask.hide();
    			Ext.example.msg('Server Error', 'Sorry, the connection is failure.');
    		}
		});
    },
    reset: function() {
    	SelectedTaskStore.removeAll();
    }
});

Ext.reg('SelectedTasksGrid', ezScrum.SelectedTasksGridPanel);

ezScrum.window.SelectTaskWindow = Ext.extend(ezScrum.layout.Window, {
	url			: 'showPrintableTasks.do',
	notifyPanel	: undefined,
	sprintID	: '-1',
	storyID	: '-1',
	
	id			: 'SelectTasks_Window',
	title		: 'Select Tasks',
	height		: 500,	
	viewConfig	: {
        forceFit: true
    },
    listeners: {
    	hide: function() {
    		this.SelectedTaskGrid.reset();
    	}
    },
	initComponent : function() {
		var config = {
			layout : 'fit',
			items  : [{
				xtype: 'SelectedTasksGrid'
			}],
			tbar: [{
				id		: 'SelectedTasksBtn',
				text	: 'Select Tasks',
				icon	: 'images/add3.png',
				disabled: true,
				handler	: this.doPrintingSelectedTask
			}, {
				id		: 'CancelBtn',
				text	: 'Cancel',
				icon	: 'images/cancel.png',
				disabled: false,
				scope   : this,
                handler : function() { this.hide(); }
			}],
			bbar: new Ext.PagingToolbar({
	            pageSize: 15,
	            store: SelectedTaskStore,
	            displayInfo: true,
	            displayMsg: 'Displaying topics {0} - {1} of {2}',
	            emptyMsg: "No topics to display",
	            items:[
	                '-', {
	                	text : 'Reload',
	                	icon : 'images/refresh.png',
	                	handler: function() { Ext.getCmp('SelectTasks_Window').SelectedTaskGrid.loadDataModel(); }
	                }, {
	                	text : 'Clean Filters',
	                	icon : 'images/clear2.png',
	                	handler : function() { Ext.getCmp('SelectTasks_Window').SelectedTaskGrid.filters.clearFilters(); }
					}
	            ],
	            listeners: {
					render: function(c) {
						c.refresh.hideParent = true;
						c.refresh.hide();
					},
					beforechange: function() {
						var currentPageSelected = Ext.getCmp('SelectTasks_Window').getSelections();
						Page_Selected_items = Page_Selected_items.concat(currentPageSelected);	// 串接所有分頁被選取的項目
					},
					change: function() {
						var currentData = new Array();
						var otherData = new Array();
						for (var i=0 ; i<Page_Selected_items.length ; i++) {
							var record = SelectedTaskStore.getById(Page_Selected_items[i]);
							if (record) {
								currentData.push(record);
							} else {
								otherData.push(Page_Selected_items[i]);
							}
						}
						
						Page_Selected_items = otherData;
						
						if (SelectedTasks_CheckBoxModel.grid) {
							SelectedTasks_CheckBoxModel.selectRecords(currentData);
						}
					}
				}
			})
		}
		
		Ext.apply(this, Ext.apply(this.initialConfig, config));
		ezScrum.window.SelectTaskWindow.superclass.initComponent.apply(this, arguments);
		
		this.SelectedTaskGrid = this.items.get(0);
	},
	// Show All printable Story    
    showPrintableTask:function() {
    	var selectedIndex = this.SprintBacklog_SprintCombo.selectedIndex;
		var sprintID = this.SprintBacklog_SprintCombo.getStore().getAt(selectedIndex).get('Id');
		
		openURLWithCheckSession( "showPrintableStory.do?sprintID=" + sprintID );
    },
    showTheWindow_Sprint: function(panel, sprintID) {
    	// show from sprint backlog page
    	
    	// initial window info
    	this.notifyPanel = panel;
    	this.sprintID = sprintID;
    	this.show();
    	
    	// initial grid info
    	this.SelectedTaskGrid.sprintID = sprintID;
    	this.SelectedTaskGrid.loadDataModel();
    },
    doPrintingSelectedTask: function() {
    	var obj = Ext.getCmp('SelectTasks_Window');
    	var selects = Page_Selected_items.concat(Ext.getCmp('SelectTasks_Window').getSelections());
    	
    	window.open(obj.url + "?selects=" + selects);
    	// get selection items
    	
    	console.log(selects);
    	var loadmask = new Ext.LoadMask(obj.getEl(), {msg: "Select Tasks..."});
		loadmask.show();
//		Ext.Ajax.request({
//			url: obj.url,
//			params: { 
//				sprintID: obj.sprintID,
//				selects: selections
//			},
//			success: function(response) {
//				openURLWithCheckSession( "showPrintableTasks.do?");
//				//var selectedIndex = this.SprintBacklog_SprintCombo.selectedIndex;
//				//var sprintID = this.SprintBacklog_SprintCombo.getStore().getAt(selectedIndex).get('Id');
//				
//				
//				// check action permission
//				//ConfirmWidget.loadData(response);
//				//if (ConfirmWidget.confirmAction()) {
//					//obj.notifyPanel.notify_AddExistedStorySuccess();
//				//}
//				
		var loadmask = new Ext.LoadMask(obj.getEl(), {msg: "Select Tasks..."});
		loadmask.hide();
//			},
//			failure: function(response) {
//				var loadmask = new Ext.LoadMask(obj.getEl(), {msg: "Select Tasks..."});
//				loadmask.hide();
//				Ext.example.msg('Server Error', 'Sorry, please try again.');
//			}
//		});
    },
    getSelections: function() {
		// 回傳目前有被勾選的資料
		var NewArr = new Array();
		var SelectedArr = SelectedTasks_CheckBoxModel.getSelections();
		for(var i = 0 ; i<SelectedArr.length ; i++) {
			NewArr.push(SelectedArr[i].data['Id']);
		}
		
		return NewArr;
    }
});

/*
 * call method
 * 		1. showTheWindow_Release: function(panel, storyID)
 * 		2. showTheWindow_Sprint: function(panel, sprintID)
 * 
 * notify method
 * 		1. notify_AddExistedStorySuccess
 * 
 * shared with: 
 * 		1. Release Plan
 * 		2. Sprint Backlog
 * */
var SelectTasks_Window = new ezScrum.window.SelectTaskWindow();