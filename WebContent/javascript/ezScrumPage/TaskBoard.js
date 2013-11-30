Ext.ns('ezScrum');
Ext.ns('ezScrum.TaskBoard');

// Task Board include 3 panel(Sprint Information, Burndown Chart, Card Panel)
// The extension point of TaskBoard only "Card Panel"
ezScrum.TaskBoardPage = Ext.extend( Ext.Panel, {
	id 			: 'TaskBoard_Page',
	layout 		: 'anchor',
	autoScroll	: true,
	initComponent : function(){
		var boardPlugin = [{ptype: 'ezScrumInnerTaskBoard'}];
		
		// if there is a Task Board plug-in, replace the ezScrum "inner Task Board"
		// ezScrum.TaskBoard.CardPanel is in "TaskBoardCardPanel.jsp"
		// we have to make sure the plug-in would be generated before "ezScrum.TaskBoardPage" is shown 
		if( ezScrum.TaskBoard.CardPanel != ''){
			boardPlugin = [{ptype: ezScrum.TaskBoard.CardPanel}];
		}
		
		var config = {
				items: [
				        { ref: 'TaskBoard_SprintDescForm_ID', xtype: 'TaskBoard_SprintDescForm' },
				        { ref: 'TaskBoard_BurndownChartForm_ID', xtype: 'BurndownChartForm' }
		        ],
		        plugins: boardPlugin
		}
		
		this.addEvents('notifyReloadAllForm',
					   'notifyReloadSprintInfoForm',
					   'notifyReloadBurndownChartForm',
					   'notifyReloadTaskBoardCard',
					   'getOperatingSprintID');
		// seems Observer notify
		this.on('notifyReloadAllForm', this.reloadAllForm, this);
		this.on('notifyReloadSprintInfoForm', this.reloadSprintInfoForm, this);
		this.on('notifyReloadBurndownChartForm', this.reloadBurndownChartForm, this);
		this.on('notifyReloadTaskBoardCard', this.reloadTaskBoardCard, this);
		
		// get panel: TaskBoard_SprintDescForm ComboBox sprint ID
		this.on('getOperatingSprintID', this.getOperatingSprintID, this);
		
		Ext.apply(this, Ext.apply(this.initialConfig, config));
		ezScrum.TaskBoardPage.superclass.initComponent.apply(this, arguments);
	},
	listeners : {
		// init all items
		'show' : function(){
			// Sprint Info
			this.TaskBoard_SprintDescForm_ID.loadDataModel();
			// Burndown Chart
			this.TaskBoard_BurndownChartForm_ID.loadDataModel();
			
			// Card Panel, 如果TaskBoard_SprintDescForm_ID.getSprintID()!=undefined表示之前已經有瀏覽記錄，取出後送reload的event
			if(this.plugins != ''){
				if(this.TaskBoard_SprintDescForm_ID.getSprintID() == undefined) {
					this.plugins[0].fireEvent('initloadData', this);
				} else {
					this.plugins[0].fireEvent('reloadData', this);
				}
			}
		}
	},
    reloadAllForm: function(sprintID, userID) {
    	this.reloadSprintInfoForm(sprintID);
    	this.reloadBurndownChartForm(sprintID);
		this.reloadTaskBoardCard(sprintID, userID);
    },
    reloadSprintInfoForm: function(sprintID) {
    	// reload TaskBoardSprintForm
    	this.TaskBoard_SprintDescForm_ID.setSprintID(sprintID);
    	this.TaskBoard_SprintDescForm_ID.loadDataModel();
    },
    reloadBurndownChartForm: function(sprintID) {
    	// reload BurndownCharForm
    	this.TaskBoard_BurndownChartForm_ID.setSprintID(sprintID);
    	this.TaskBoard_BurndownChartForm_ID.loadDataModel();   	
    },
    reloadTaskBoardCard: function(sprintID, userID) {
    	console.log("reload task board");
    	if(this.plugins != ''){
    		// reload TaskBoardCardForm 
    		this.plugins[0].fireEvent('reloadData', sprintID, userID);
    	}
    },
    getOperatingSprintID: function( notifyObj ) {
    	var comboSprintID = this.TaskBoard_SprintDescForm_ID.getCombo_SprintID();
    	notifyObj.setOperatingSprintID( comboSprintID ); 
    }
});

var TaskBoardPage = new ezScrum.TaskBoardPage();