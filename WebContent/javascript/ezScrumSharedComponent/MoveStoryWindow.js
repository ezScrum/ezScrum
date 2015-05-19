var notYetEndReleaseStore = new Ext.data.JsonStore({
    url		: 'notYetEndReleaseForMoveStory.do',
    fields	: ['ID', 'Goal']
});

var MoveReleaseIDArr;
notYetEndReleaseStore.on('load',function(store, records, options) {
	MoveReleaseIDArr = new Array();
    notYetEndReleaseStore.each(function (record) {
        MoveReleaseIDArr.push([record.get('ID'), record.get('Goal')]);
    });
});

var notYetStartSprintStore = new Ext.data.JsonStore({
    url: 'notYetEndSprintForMoveStory.do',
    fields: ['ID', 'Goal']
});

var MoveSprintIDArr;
notYetStartSprintStore.on('load',function(store,records,options) {
	MoveSprintIDArr = new Array();
	notYetStartSprintStore.each(function (record) {
    	MoveSprintIDArr.push([record.get('ID'), record.get('Goal')]);
    });
});

//只是單純用來顯示要移動到Release或Sprint的資料
var ROSstore = new Ext.data.SimpleStore({
    fields: ['Id', 'Text'],
    data: [
        ["sprint", "Sprint"]
    ]
});

var MoveIDStore = new Ext.data.ArrayStore({
    fields: ['Id', 'Goal']
});

ezScrum.MoveStoreDetailForm = Ext.extend(Ext.FormPanel, {
	IssueID		: '-1',
	MoveToID	: '0',
	notifyPanel	: undefined,
	
	monitorValid: true,
	layout		: 'anchor',
	bodyStyle	: 'padding:5px',
	initComponent: function () {
        var config = {
            url: 'moveStorySprint.do',
            items: [{
            	xtype		: 'combo',
            	ref			: 'PlanTypeCombo',
                store		: ROSstore,
                typeAhead	: true,
                valueField	: 'Id',
                displayField: 'Text',
                mode		: 'local',
                triggerAction: 'all',
                allowBlank	: false,
                selectOnFocus: true,
                emptyText	: 'Select Type: Release or Sprint...',
                editable	: false,
                anchor		: '100%'
            }, {
            	xtype		: 'combo',
            	ref			: 'MoveIDCombo',
                tpl			: '<tpl for="."><div ext:qtip="{Goal}" class="x-combo-list-item">{Id}: {Goal}</div></tpl>',
                displayField: 'Id',
                typeAhead	: true,
                store		: MoveIDStore,
                mode		: 'local',
                allowBlank	: false,
                triggerAction: 'all',
                selectOnFocus: true,
                editable	: false,
                anchor		: '100%'
            }],
            buttons: [{
                text		: 'Move',
                formBind	: true,
                scope		: this,
                handler		: this.doMove
            }, {
            	text		: 'Close',
	        	scope		: this,
	        	handler		: function(){this.ownerCt.hide();}
            }]
        }

        Ext.apply(this, Ext.apply(this.initialConfig, config));
        ezScrum.MoveStoreDetailForm.superclass.initComponent.apply(this, arguments);
        
        var obj = this;
        this.PlanTypeCombo.addListener(
			'select', function() {
				// load mapping data
				if (this.getValue() == "sprint") {
					MoveIDStore.loadData(MoveSprintIDArr);
				} else {
					MoveIDStore.loadData([]);
				}
				
				// reload combo info
				obj.MoveIDCombo.selectedIndex = -1;
				obj.MoveIDCombo.value = '';
		        obj.MoveIDCombo.reset();
			}
		);
        
        this.MoveIDCombo.addListener(
			'select', function() {
				obj.MoveToID = this.getStore().getAt(this.selectedIndex).get('Id');
			}
		);
    },
    doMove: function () {
        var obj = this;
        Ext.Ajax.request({
            url: obj.url,
            params: {
                issueID	: obj.IssueID,
                moveID	: obj.MoveToID,
                type	: obj.PlanTypeCombo.getValue()
            },
            success: function (response) { obj.onSuccess(response); }
        });
    },
    onSuccess: function (response) {
    	this.notifyPanel.notify_MoveSuccess(response);
    },
    reset: function() {
    	// load data
        notYetEndReleaseStore.load();
        notYetStartSprintStore.load();
        
        this.PlanTypeCombo.selectedIndex = -1;
        this.PlanTypeCombo.value = '';
        this.PlanTypeCombo.reset();
        
        this.MoveIDCombo.selectedIndex = -1;
        this.MoveIDCombo.value = '';
        this.MoveIDCombo.reset();
    }
});
Ext.reg('MoveStoreDetailForm', ezScrum.MoveStoreDetailForm);

ezScrum.window.MoveStoreWindow = Ext.extend(ezScrum.layout.Window, {
    title	: 'Move Story',
    width	: 500,
    initComponent: function () {
    	var config = {
			items : [{
				ref		: 'MoveStoryForm',
				xtype	: 'MoveStoreDetailForm'
			}]
		}
    	
		Ext.apply(this, Ext.apply(this.initialConfig, config));
    	ezScrum.window.MoveStoreWindow.superclass.initComponent.apply(this, arguments);
    },
    showTheWindow_MoveStory: function (panel, IssueId, SprintID) {
    	this.MoveStoryForm.notifyPanel = panel;
        this.MoveStoryForm.IssueID = IssueId;
        this.MoveStoryForm.reset();
        
        this.show();
    }
});

/*
 * call method
 * 		1. showTheWindow_MoveStory: function (panel, IssueId, SprintID, ReleaseID) {
 * 
 * notify method
 * 		1. notify_MoveSuccess: function(response)
 * 
 * shared with: 
 * 		1. ReleasePlan
 * 		2. SprintBacklog
 * */
var MoveStory_Window = new ezScrum.window.MoveStoreWindow();