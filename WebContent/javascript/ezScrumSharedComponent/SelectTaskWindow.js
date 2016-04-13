#SelectTaskWindow.js
Ext.ns('ezScrum');
Ext.ns('ezScrum.layout');
Ext.ns('ezScrum.window');

var Page_Selected_items = new Array();

ezScrum.AddExistedStoryGridPanel = Ext.extend(Ext.grid.GridPanel, {
	frame		: false,
	stripeRows	: true,
	sprintID	: '-1',
	releaseID	: '-1',
	url			: 'showExistedStory.do',
	store		: ExistedStoryStore,
	colModel	: ExistedStoryColumnModel(),
	plugins		: [ExistedStory_Filter, ExistedStory_Expander],
	sm			: ExistedStory_CheckBoxModel,
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
				releaseID: obj.releaseID
			},
			success: function(response) {
				ConfirmWidget.loadData(response);
    			if (ConfirmWidget.confirmAction()) {
					ExistedStoryStore.loadData(response.responseXML);
					ExistedStoryStore.proxy.data = response;
					ExistedStoryStore.proxy.reload = true;
					ExistedStoryStore.load({params: {start: 0, limit: 15}});
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
    	ExistedStoryStore.removeAll();
    }
});

ezScrum.window.AddExistedStoryWindow = Ext.extend(ezScrum.layout.Window, {
	url			: 'printSelectTasks.do',
	notifyPanel	: undefined,
	sprintID	: '-1',
	releaseID	: '-1',
	
	id			: 'SelectTasks_Window',
	title		: 'Select Printable Tasks',
	height		: 500,	
	viewConfig	: {
        forceFit: true
    },
    listeners: {
    	hide: function() {
    		this.ExistedStoryGrid.reset();
    	}
    },
    initComponent : function() {
        var config = {
            layout : 'fit',
            items  : [{
                xtype: 'AddExistedStoryGrid'
            }],
            tbar: [{
                id      : 'AddExistedStoryBtn',
                text    : 'Add Existed Stories',
                icon    : 'images/add3.png',
                disabled: true,
                handler : this.doAddExistedStory
            }, {
                id      : 'CancelBtn',
                text    : 'Cancel',
                icon    : 'images/cancel.png',
                disabled: false,
                scope   : this,
                handler : function() { this.hide(); }
            }],
            bbar: new Ext.PagingToolbar({
                pageSize: 15,
                store: ExistedStoryStore,
                displayInfo: true,
                displayMsg: 'Displaying topics {0} - {1} of {2}',
                emptyMsg: "No topics to display",
                items:[
                    '-', {
                        text : 'Reload',
                        icon : 'images/refresh.png',
                        handler: function() { Ext.getCmp('AddExistedStory_Window').ExistedStoryGrid.loadDataModel(); }
                    }, {
                        text : 'Clean Filters',
                        icon : 'images/clear2.png',
                        handler : function() { Ext.getCmp('AddExistedStory_Window').ExistedStoryGrid.filters.clearFilters(); }
                    }, {
                        text : 'Expand All',
                        icon : 'images/folder_out.png',
                        handler : function() { ExistedStory_Expander.expandAll(); }
                    }, {
                        text : 'Collapse All',
                        icon : 'images/folder_into.png',
                        handler : function() { ExistedStory_Expander.collapseAll(); }
                    }
                ],
                listeners: {
                    render: function(c) {
                        c.refresh.hideParent = true;
                        c.refresh.hide();
                    },
                    beforechange: function() {
                        var currentPageSelected = Ext.getCmp('AddExistedStory_Window').getSelections();
                        Page_Selected_items = Page_Selected_items.concat(currentPageSelected);  // 串接所有分頁被選取的項目
                    },
                    change: function() {
                        var currentData = new Array();
                        var otherData = new Array();
                        for (var i=0 ; i<Page_Selected_items.length ; i++) {
                            var record = ExistedStoryStore.getById(Page_Selected_items[i]);
                            if (record) {
                                currentData.push(record);
                            } else {
                                otherData.push(Page_Selected_items[i]);
                            }
                        }
                        
                        Page_Selected_items = otherData;
                        
                        if (ExistedStory_CheckBoxModel.grid) {
                            ExistedStory_CheckBoxModel.selectRecords(currentData);
                        }
                    }
                }
            })
        
        }
        
        Ext.apply(this, Ext.apply(this.initialConfig, config));
        ezScrum.window.AddExistedStoryWindow.superclass.initComponent.apply(this, arguments);
        
        this.ExistedStoryGrid = this.items.get(0);
    },
	showTheWindow_Sprint: function(panel, sprintID) {
    	// show from sprint backlog page
    	
    	// initial window info
    	this.notifyPanel = panel;
    	this.releaseID = '-1';
    	this.sprintID = sprintID;
    	this.show();
    	
    	// initial grid info
    	this.ExistedStoryGrid.releaseID = '-1';
    	this.ExistedStoryGrid.sprintID = sprintID;
    	this.ExistedStoryGrid.loadDataModel();
    },
    doAddExistedStory: function() {
        var obj = Ext.getCmp('AddExistedStory_Window');
        
        // get selection items
        var selections = Page_Selected_items.concat(Ext.getCmp('AddExistedStory_Window').getSelections());
        
        var loadmask = new Ext.LoadMask(obj.getEl(), {msg: "Add Existed Stories..."});
        loadmask.show();
        Ext.Ajax.request({
            url: obj.url,
            params: { 
                sprintID: obj.sprintID,
                releaseID: obj.releaseID,
                selects: selections
            },
            success: function(response) {
                // check action permission
                ConfirmWidget.loadData(response);
                if (ConfirmWidget.confirmAction()) {
                    obj.notifyPanel.notify_AddExistedStorySuccess();
                }
                
                var loadmask = new Ext.LoadMask(obj.getEl(), {msg: "Add Existed Stories..."});
                loadmask.hide();
            },
            failure: function(response) {
                var loadmask = new Ext.LoadMask(obj.getEl(), {msg: "Add Existed Stories..."});
                loadmask.hide();
                Ext.example.msg('Server Error', 'Sorry, please try again.');
            }
        });
    },
    getSelections: function() {
        // 回傳目前有被勾選的資料
        var NewArr = new Array();
        var SelectedArr = ExistedStory_CheckBoxModel.getSelections();
        for(var i = 0 ; i<SelectedArr.length ; i++) {
            NewArr.push(SelectedArr[i].data['Id']);
        }
        
        return NewArr;
    }
});
