Ext.ns('ezScrum');
Ext.ns('ezScrum.window');
Ext.ns('ezScrum.TaskBoard');

ezScrum.TaskboardCardPanel = Ext.extend(Ext.Panel, {
	layout : 'table',
	autoHeight: true,
	title : 'Task Board Card',
	defaults : {
		bodyStyle : 'padding:5px;'
	},
	layoutConfig : {
		tableAttrs : {
			style : {
				width : '100%',
				align : 'center',
				cellpadding : 5,
				cellspacing : 5,
				'border-collapse' : 'collapse',
				'table-layout' : 'fixed'
			}
		},
		columns : 3
	},
	init_StatusPanel: function() {
		this.removeAll();
		this.add(TaskBoard_IssueStatus);
		this.doLayout();
	}
});
Ext.reg('ezScrum.TaskboardCardPanel', ezScrum.TaskboardCardPanel);

ezScrum.Taskboard_Content_Panel = Ext.extend(Ext.Panel, {
	id	   : 'TaskBoard_Card_Panel',
	header : false,
	layout : 'fit',
    initComponent : function() {
		var config = {
			items : [{ ref: 'TaskBoardCardPanel', xtype: 'ezScrum.TaskboardCardPanel' }]
		}
		
		Ext.apply(this, Ext.apply(this.initialConfig, config));
		ezScrum.Taskboard_Content_Panel.superclass.initComponent.apply(this, arguments);
	},
    loadDataModel: function() {
    	this.loadData('', 'ALL');
    },
    loadData: function(sID, handler) {
    	var obj = this;
    	Ext.Ajax.request({
    		url : 'getTaskBoardStoryTaskList.do',
    		params : {
    			sprintID : sID,
    			UserID	 : handler
    		},
    		async : false,
    		success : function(response) {
    			TaskBoard_StoriesStore.loadData(Ext.decode(response.responseText));
    			obj.initialTaskBoard();
    		},
    		failure: function() {
				Ext.example.msg('Server Error', 'Sorry, the connection is failure.');
			}
    	});
	},
	initialTaskBoard: function() {
		// remove all items (for 選擇其它 sprint 時以 AJAX 的形式更新，取代切換頁面的形式) 
		this.TaskBoardCardPanel.init_StatusPanel();
		
		for ( var i = 0; i < TaskBoard_StoriesStore.getCount(); i++) {
			// Issue 的三種狀態 'new', 'assigned', 'closed';
			var story = TaskBoard_StoriesStore.getAt(i);
			

			// 建立 StatusPanel，GroupID 則是依照 StoryID，並且每個 Panel 再設置其所代表的 Status
			// createStoryStatusPanel function 在  ezScrumWidget/TaskBoard/StatusPanel.js
			var statusPanel = createStoryStatusPanel(story.id);
			this.TaskBoardCardPanel.add(statusPanel);

			// createStoryCard function 在 ezScrumWidget/TaskBoard/StoryCard.js
			var storyCard = createStoryCard(story);

			statusPanel.get(story.id + '_' + story.get('Status')).add(storyCard);
			
			// 相同 Story 的 Task 會放在同一個 Panel 裡面
			var tasks = story.get('Tasks');
			for ( var k = tasks.length-1 ; k >= 0; k--) {
				var task = tasks[k];

				// createTaskCard function 在 ezScrumWidget/TaskBoard/TaskCard.js
				var taskCard = createTaskCard(task, story.id);
				statusPanel.get(story.id + '_' + task.Status).add(taskCard);
			}
			
			// 讓 Taskboard 重新進行 Layout 以便可以計算 Story 或 Task 的高度，再去重設其他沒有放 Story 或 Task 的 Panel
			this.TaskBoardCardPanel.doLayout();
			statusPanel.resetCellHeight();
		}
	},
	/**
	 * 傳入的參數第一個為要執行的Function，後面為他的參數， 如果使用者按下確認要繼續執行，那麼才會執行這個參數
	 */
	checkIsCurrentSprint: function() {	// 此 function 是給 TaskCard、StoryCard 移動時會彈出視窗前確認是否為過期的Sprint
		// 將所有參數轉為真正的Array
		var args = Array.prototype.slice.call(arguments);
		var fun = args.shift();
	
		// 跟最上面的 Sprint Info Form 取得資訊，判斷目前的˙Sprint 是否為過期的
		var checkCurrent = Ext.getCmp('TaskBoardSprintDesc')
				.isCurrentSprint();
	
		if (checkCurrent) {
			fun.apply(this, args);
		} else {
			Ext.MessageBox.confirm("Warning!", 'The sprint is overdue.', function(btn) {
				// 如果使用者按下Yes才會繼續執行動作
				if (btn == 'yes') {
					fun.apply(this, args);
				}
			});
		}
	},
	notify_EditStory: function(success, response, record) {
		Story_Window.hide();
		
		var title = 'Edit Story';
		if (success) {
			Ext.example.msg(title, 'Success.');
			
			// update Story Card Info
			var storyId = record.data['Id'];
			var storyName = record.data['Name'];
			var storyEstimate = record.data['Estimate'];
			Ext.getCmp('Story:' + storyId).updateData_Edit(storyName, storyEstimate);

			// update Sprint Desc. and Burndown Chart
			var sprintID = Ext.getCmp('TaskBoardSprintDesc').getCombo_SprintID();
			Ext.getCmp('TaskBoard_Page').reloadSprintInfoForm(sprintID);
			Ext.getCmp('TaskBoard_Page').reloadBurndownChartForm(sprintID);
		} else {
			Ext.example.msg(title, 'Sorry, please try again.');
		}
		this.initialTaskBoard();
	},
	notify_EditTask: function(record) {
		EditTaskWindow.hide();// share component
		Ext.example.msg('Edit Task', 'Success.');
		
		// update Task Card Info
		var taskId = record.data['Id'];
		var taskName = record.data['Name'];
		var taskHandler = record.data['Handler'];
		var taskPartners = record.data['Partners'];
		var taskRemainHours = record.data['Remains'];
		Ext.getCmp('Task:' + taskId).updateData_Edit(taskName, taskHandler, taskPartners, taskRemainHours);
		
		// update Sprint Desc. and Burndown Chart
		var sprintID = Ext.getCmp('TaskBoardSprintDesc').getCombo_SprintID();
		Ext.getCmp('TaskBoard_Page').reloadSprintInfoForm(sprintID);
		Ext.getCmp('TaskBoard_Page').reloadBurndownChartForm(sprintID);
		this.initialTaskBoard();
	},
	notify_AttachFile: function(success, record, msg) {
		AttachFile_Window.hide();
		
		var title = 'Attach File';
		if(success) {
			Ext.example.msg(title, 'Success.');
			
			// update Task Card Info
			var issueId = record.data['Id'];
			var issueType = record.issueType;
			var issueAttachFileList = record.data['AttachFileList'];
			Ext.getCmp(issueType + ':' + issueId).updateData_AttachFile(issueAttachFileList);			
		}else{
			Ext.example.msg(title, msg);
		}
	}
});
//Ext.reg('TaskBoardCardContentPanel', ezScrum.Taskboard_Content_Panel);

ezScrum.TaskBoard.InnerPlugin = Ext.extend(Ext.util.Observable,{
	board:[],
	init:function(cmp){// owner component
		this.board = new ezScrum.Taskboard_Content_Panel();

		this.hostCmp = cmp;
		this.hostCmp.add( this.board );
		this.hostCmp.doLayout();
		
		// plug-in event: loadData, loadDataModel
		this.addEvents('initloadData', 'reloadData');
		this.on('initloadData', this.initloadData);
		this.on('reloadData', this.reloadData);

	},
	initloadData: function(){
		this.board.loadDataModel();
	},
	reloadData:function(sprintID, userID){
		this.board.loadData(sprintID, userID);
	}
});
// register plugin, id: TaskBoardPlugin 
Ext.preg('ezScrumInnerTaskBoard', ezScrum.TaskBoard.InnerPlugin);



/* Edit Task widget: 
 * ../ezScrumWidget/EditTaskWidget.js
 * EditTaskWindow
 */ 

// Check out Task 
var CheckOutTaskWindow = new ezScrum.window.CheckOutWindow({
	taskCard: '',
	listeners:{
		LoadFailure: function(win, response) {
			Ext.MessageBox.confirm('Load Failure', 'Sorry, Load Failure');
			this.hide();
		},
		CheckOutSuccess: function(win, response, record) {
			this.hide();
			Ext.example.msg('Check Out Task', 'Success.');
			
			// update task data : name, handler, partners, note
			this.taskCard.moveToTarget();
			this.taskCard.updateData(record.data);
		},
		CheckOutFailure: function(win, response) {
			this.hide();
			Ext.MessageBox.confirm('Check Out Failure', 'Sorry, Check Out Failure');
		}
	},
	setCard: function(card) {
		this.taskCard = card;
	}
});

// Re-CO Task, task: CO -> Not-CO
var RE_CheckOutTaskWindow = new ezScrum.window.ReCheckOutWindow({
	taskCard: '',
	listeners:{
		LoadFailure: function(win, response) {
			Ext.MessageBox.confirm('Load Failure', 'Sorry, Load Failure');
			this.hide();
		},
		RECheckOutSuccess: function(win, response, record) {
			this.hide();
			Ext.example.msg('Reset Task', 'Success.');
			
			// move task card, 清除 handler, partner 的動作在 TaskCard.js 做了
			this.taskCard.moveToTarget();
			this.taskCard.updateName(record.data['Name']);
		},
		RECheckOutFailure: function(win, response) {
			this.hide();
			Ext.MessageBox.confirm('Check Out Failure', 'Sorry, Check Out Failure');
		}
	},
	setCard: function(c) {
		this.taskCard = c;
	}
});

// Done Story or task
var DoneIssueWindow = new ezScrum.window.DoneIssueWindow({
	issueCard: '', // story or task
	listeners:{
		LoadFailure: function(win, response) {
			Ext.MessageBox.confirm('Load Failure', 'Sorry, Load Failure');
			this.hide();
		},
		DoneSuccess: function(win, response, record) {
			this.hide();
			Ext.example.msg('Done Issue', 'Success.');
			
			// move story or task card
			this.issueCard.moveToTarget();
			this.issueCard.updateData(record.data);
			
			// update Sprint Desc. and Burndown Chart
			var sprintID = Ext.getCmp('TaskBoardSprintDesc').getCombo_SprintID();
			Ext.getCmp('TaskBoard_Page').reloadSprintInfoForm(sprintID);
			Ext.getCmp('TaskBoard_Page').reloadBurndownChartForm(sprintID);
		},
		DoneFailure: function(win, response) {
			this.hide();
			Ext.MessageBox.confirm('Done Issue Failure', 'Sorry, Done Issue Failure');
		}
	},
	setCard: function(c) {
		this.issueCard = c;
	}
});

// Re-Open story or task. Story: Done -> Not-CO; Task: Done -> CO
var RE_OpenIssueWindow = new ezScrum.window.ReOpenIssueWindow({
	issueCard: '',
	listeners:{
		LoadFailure: function(win, response) {
			Ext.MessageBox.confirm('Load Failure', 'Sorry, Load Failure');
			this.hide();
		},
		ReOpenSuccess: function(win, response, record) {
			this.hide();
			Ext.example.msg('Re Open Issue', 'Success.');

			// move story or task card
			this.issueCard.moveToTarget();
			this.issueCard.updateData(record.data);
			
			/* 
			 * update Sprint Desc. and Burndown Chart
			 * Story Re-Open 需調整 Sprint Desc. and Burndown Chart的資訊
			 *  Task Re-Open 目前的機制是不要復原 Remain Hour, 所以 move card 就好
			 */
			if(this.issueCard.issueType == 'story') {
				var sprintID = Ext.getCmp('TaskBoardSprintDesc').getCombo_SprintID();
				Ext.getCmp('TaskBoard_Page').reloadSprintInfoForm(sprintID);
				Ext.getCmp('TaskBoard_Page').reloadBurndownChartForm(sprintID);
			}
		},
		ReOpenFailure: function(win, response) {
			this.hide();
			Ext.MessageBox.confirm('Re Open Issue Failure', 'Sorry, Re Open Issue Failure');
		}
	},
	setCard: function(c) {
		this.issueCard = c;
	}
});

// show edit story
function editStory(id) {
	Story_Window.showTheWindow_Edit(Ext.getCmp('TaskBoard_Card_Panel'), id);
}

// show edit task
function editTask(id) {
	var sprintID = Ext.getCmp('TaskBoardSprintDesc').getCombo_SprintID();
	EditTaskWindow.loadEditTask(sprintID, id, Ext.getCmp('TaskBoard_Card_Panel'));
}

// show check out task
function showCheckOutIssue(id, card) {
	CheckOutTaskWindow.setCard(card);
	CheckOutTaskWindow.showWidget(id);
}

// show Reset check out Task
function showReCheckOutTask(id, card) {
	RE_CheckOutTaskWindow.setCard(card);
	RE_CheckOutTaskWindow.showWidget(id);
}

// show done issue
function showDoneIssue(id, card) {
	DoneIssueWindow.setCard(card);
	DoneIssueWindow.showWidget(id, card.issueType);
}

// show reopen issue
function showReOpenIssue(id, card) {
	RE_OpenIssueWindow.setCard(card);
	RE_OpenIssueWindow.showWidget(id, card.issueType);
}

// attach file for task
function taskAttachFile(issueID) {
	AttachFile_Window.attachFile(Ext.getCmp('TaskBoard_Card_Panel'), issueID, 'Task');
}

// attach file for story
function storyAttachFile(issueID) {
	AttachFile_Window.attachFile(Ext.getCmp('TaskBoard_Card_Panel'), issueID, 'Story');
}

// show issue history
function showHistory(issueId, issueType) {
	IssueHistory_Window.showTheWindow(issueId, issueType);
}

// delete file for story
function deleteStoryAttachFile(file_Id, issue_Id) {
	Ext.MessageBox.confirm('Confirm', 'Are you sure you want to delete this attached file?', function(btn){
		if(btn === 'yes') {
			Ext.Ajax.request({
				url : 'ajaxDeleteFile.do',
				params : {fileId:file_Id, issueId:issue_Id, issueType: 'Story'},
				success : function(response) {
					Ext.example.msg('Delete File', 'Success.');
					
					var records = jsonStoryReader.read(response);
					
					if(records.success && records.totalRecords > 0) {
						var record = records.records[0];
						if(record) {
							// update card content
							var issueId = record.data['Id'];
							var issueAttachFileList = record.data['AttachFileList'];
							Ext.getCmp('Story:' + issueId).updateData_AttachFile(issueAttachFileList);
						}
					}
				},
				failure : function(response) {
					Ext.example.msg('Delete File', 'Failure.');
				}
			});
		}
	});
}

//delete file for task
function deleteTaskAttachFile(file_Id, issue_Id) {
	Ext.MessageBox.confirm('Confirm', 'Are you sure you want to delete this attached file?', function(btn){
		if(btn === 'yes') {
			Ext.Ajax.request({
				url : 'ajaxDeleteFile.do',
				params : {fileId:file_Id, issueId:issue_Id, issueType: 'Task'},
				success : function(response) {
					Ext.example.msg('Delete File', 'Success.');
					
					var records = jsonStoryReader.read(response);
					
					if(records.success && records.totalRecords > 0) {
						var record = records.records[0];
						if(record) {
							// update card content
							var issueId = record.data['Id'];
							var issueAttachFileList = record.data['AttachFileList'];
							Ext.getCmp('Task:' + issueId).updateData_AttachFile(issueAttachFileList);
						}
					}
				},
				failure : function(response) {
					Ext.example.msg('Delete File', 'Failure.');
				}
			});
		}
	});
}