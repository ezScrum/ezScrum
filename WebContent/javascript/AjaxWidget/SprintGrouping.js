Ext.ns('ezScrum');

/*******************************************************************************
 * 
 * 欄位宣告，與資料儲存結構區域
 * 
 * 
 ******************************************************************************/

/*-----------------------------------------------------------
 *   Sprint 的儲存結構
 -------------------------------------------------------------*/
// create the Data Store
var sprintStore = new Ext.data.Store({
			autoDestroy : true,
			fields : [{
						name : 'Id',
						type : 'int'
					}, {
						name : 'Goal'
					}, {
						name : 'StartDate'
					}, {
						name : 'Interval'
					}, {
						name : 'Members'
					}, {
						name : 'AvaliableDays'
					}, {
						name : 'FocusFactor'
					}, {
						name : 'DailyScrum'
					}, {
						name : 'DemoDate'
					}, {
						name : 'DemoPlace'
					}],
			reader : SprintReader
		});

/*-----------------------------------------------------------
 *   Story的儲存結構
 -------------------------------------------------------------*/
var storyStore = new Ext.data.Store({
			autoDestroy : true,
			fields : [{
						name : 'Id',
						type : 'int'
					}, {
						name : 'Link'
					}, {
						name : 'Name'
					}, {
						name : 'Importance',
						type : 'int'
					}, {
						name : 'Estimate',
						type : 'float'
					}, {
						name : 'Status'
					}, {
						name : 'Release'
					}, {
						name : 'Sprint'
					}, {
						name : 'Notes'
					}, {
						name : 'HowToDemo'
					}, {
						name : 'Tag'
					}],
			reader : myReader
		});

/*-----------------------------------------------------------
 *   提供給Sprint List的欄位內容
 -------------------------------------------------------------*/
var createSprintCloumns = function() {
	var columns = [{
				dataIndex : 'Id',
				header : 'ID',
				width : 50,
				sortable : 'true'
			}, {
				dataIndex : 'Goal',
				header : 'Sprint Goal',
				width : 350
			}, {
				dataIndex : 'StartDate',
				header : 'Start Date',
				width : 90
			}, {
				dataIndex : 'DemoDate',
				header : 'Demo Date',
				width : 90
			}, {
				dataIndex : 'Interval',
				header : 'Interval',
				width : 90
			}, {
				dataIndex : 'Members',
				header : 'Members',
				width : 90
			}, {
				dataIndex : 'AvaliableDays',
				header : 'Avaliable Days',
				width : 150
			}, {
				dataIndex : 'FocusFactor',
				header : 'Focus Factor',
				width : 100
			}, {
				dataIndex : 'DailyScrum',
				header : 'Daily Meeting',
				width : 150
			}, {
				dataIndex : 'DemoPlace',
				header : 'Demo Place',
				width : 100
			}];

	return new Ext.grid.ColumnModel({
				columns : columns,
				defaults : {
					sortable : false
				}
			});
};

/*
 * {name : 'Id', type:'int'}, {name : 'Link'}, {name : 'Name'}, {name :
 * 'Importance', type:'int'}, {name : 'Estimate', type:'float'}, {name :
 * 'Status'}, {name : 'Release'}, {name : 'Sprint'}, {name : 'Notes'}, {name :
 * 'HowToDemo'}, {name : 'Tag'}
 */
var createStoryCloumns = function() {
	var columns = [{
		dataIndex : 'Id',
		renderer : function(value, metaData, record, rowIndex, colIndex, store) {
			return "<a href=\"" + record.data['Link'] + "\" target=\"_blank\">"
					+ value + "</a>";
		},
		header : 'ID',
		width : 50
	}, {
		dataIndex : 'Name',
		header : 'Story Name',
		width : 400
	}, {
		dataIndex : 'Importance',
		header : 'Importance',
		width : 90
	}, {
		dataIndex : 'Estimate',
		header : 'Estimate',
		width : 90
	}, {
		dataIndex : 'Status',
		header : 'Status',
		width : 90
	}, {
		hidden : true,
		dataIndex : 'Release',
		header : 'Release',
		width : 90
	}, {
		dataIndex : 'Tag',
		header : 'Tag',
		width : 90
	}];

	return new Ext.grid.ColumnModel({
				columns : columns,
				defaults : {
					sortable : true
				}
			});
};

/*******************************************************************************
 * 
 * Master頁面，負責列出所有的Sprint
 * 
 ******************************************************************************/
/*-----------------------------------------------------------
 *   處理Sprint Select事件
 -------------------------------------------------------------*/

var sprintSelectionModel = new Ext.grid.RowSelectionModel({
			singleSelect : true
		});
sprintSelectionModel.on('rowselect', function(sm, rowIdx, r) {

			/*-----------------------------------------------------------
			 * 更新Story List為這個Sprint內的Story
			 -------------------------------------------------------------*/
			Sid = r.get('Id');
			Ext.Ajax.request({
						url : 'AjaxShowStoryfromSprint.do?Sid=' + Sid,
						success : function(response) {

							// 計算此Sprint的Story Point
							storyStore.loadData(response.responseXML);
							var point = 0;
							storyStore.each(function(rec) {
										point += (rec.get('Estimate') - 0);
									});
							StoryPoint.setValue(point);

							// 改變Story List的title顯示Sprint的名稱與ID
							storyList.setTitle('Sprint' + r.get('Id') + ':'
									+ r.get('Goal') + '\t\t[Story Point:'
									+ point + ']');
						}
					});
			// /*-----------------------------------------------------------
			// * 更新Sprint的詳細資料
			// -------------------------------------------------------------*/
			// /*
			// * fields : [{ name : 'Id', type : 'int' }, { name : 'Goal' }, {
			// * name : 'StartDate' }, { name : 'Interval' }, { name : 'Members'
			// }, {
			// * name : 'AvaliableDays' }, { name : 'FocusFactor' }, { name :
			// * 'DailyScrum' }, { name : 'DemoDate' }, { name : 'DemoPlace' }],
			// */
			//
			// Id.setValue(r.get('Id'));
			// SprintGoal.setValue(r.get('Goal'));
			// DailyScrum.setValue(r.get('DailyScrum'));
			StartDate.setValue(r.get('StartDate'));
			// Interval.setValue(r.get('Interval'));
			// Members.setValue(r.get('Members'));
			// ManDays.setValue(r.get('AvaliableDays'));
			// FocusFactor.setValue(r.get('FocusFactor'));
			// DemoDate.setValue(r.get('DemoDate'));
			// DemoPlace.setValue(r.get('DemoPlace'));

			// 計算Due Day，與判斷此Sprint是否可編輯
			var start = StartDate.getValue();
			if (start != null) {
				// 計算DueDate
				var today = new Date();
				due = start.add(Date.DAY, parseInt(Interval.getValue()) * 7);

			}

		});

ezScrum.SprintMaster = Ext.extend(Ext.grid.GridPanel, {
			title : 'Sprint List',
			store : sprintStore,
			colModel : createSprintCloumns(),
			sm : sprintSelectionModel
		});

/*******************************************************************************
 * 
 * Sidebar頁面，負責列出所有的某Sprint底下的所有Story
 * 
 ******************************************************************************/
ezScrum.SprintSideBar = Ext.extend(Ext.grid.GridPanel, {
			title : 'Story List',
			store : storyStore,
			colModel : createStoryCloumns()
		});




/*******************************************************************************
 * 
 * Sprint Plan的整體頁面配置區
 * 
 ******************************************************************************/
/*-----------------------------------------------------------
 *   頁面配置的宣告區
 -------------------------------------------------------------*/

// 負責以Form的方式顯示Sprint的詳細資料
var detailFWin = new ezScrum.ShowSprintDetailWin();

// 顯示Sprint列表
var sprintList = new ezScrum.SprintMaster({
			frame : true,
			region : 'center',
			margins : '5 0 0 2'
		});

// 顯示Story列表
var storyList = new ezScrum.SprintSideBar({
			collapsible : true,
			split : true,
			region : 'south',
			margins : '5 2 0 0',
			height : 200
		});
// 顯示於頁面最上層的ToolBar
var topToolbar = new Ext.Toolbar({
			items : [{
						id : 'newSprint',
						text : 'New',
						icon : 'images/add3.png',
						handler : function() {
							detailFWin.showWidget('Create Sprint');
						}
					}, {
						id : 'removeSprint',
						text : 'Delete',
						icon : 'images/delete.png',
						disabled : true
					}, {
						id : 'editSprint',
						text : 'Edit',
						icon : 'images/edit.png',
						disabled : true
					}, {
						id : 'moveSprint',
						text : 'Move',
						disabled : true,
						icon : 'images/arrow_right.png'
					}]
		});
/*
 * 當網頁讀取完畢之後，開始進行初始化的動作
 */
Ext.onReady(function() {
			var sprintPlanView = new Ext.Panel({
						height : 600,
						layout : 'border',
						tbar : topToolbar,
						items : [sprintList, storyList]
					});
			/*-----------------------------------------------------------
			 * 讀取所有的Sprint資訊，以顯示在畫面上
			 -------------------------------------------------------------*/
			Ext.Ajax.request({
						url : 'showAllSprintForSprintPlan.do',
						success : function(response) {
							sprintStore.loadData(response.responseXML);
						}
					});
			sprintStore.setDefaultSort('Id', 'DESC');
			sprintPlanView.render('Sprint-Plan');
		});
