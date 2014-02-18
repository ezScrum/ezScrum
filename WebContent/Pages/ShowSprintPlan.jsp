<%@ page language="java" contentType="text/html" pageEncoding="UTF-8"%>

<script type="text/javascript" src="javascript/AjaxWidget/Common.js"></script>
<script type="text/javascript" src="javascript/AjaxWidget/CreateSprintWidget.js"></script>
<script type="text/javascript" src="javascript/AjaxWidget/DeleteSprintWidget.js"></script>
<script type="text/javascript" src="javascript/AjaxWidget/MoveSprintWidget.js"></script>

<script type="text/javascript" src="javascript/AjaxWidget/Message.js"></script>
<link rel="stylesheet" type="text/css" href="css/Message.css"/>

<script type="text/javascript">
Ext.ns('ezScrum');

/*******************************************************************************
 * 
 * 欄位宣告，與資料儲存結構區域
 * 
 * 
 ******************************************************************************/
/*-----------------------------------------------------------
*   使用者權限資料的儲存結構
-------------------------------------------------------------*/
var SPpermissionRecord = Ext.data.Record.create(
    ['AddSprint','EditSprint','MoveSprint','DeleteSprint']
);
var SPpermissionReader = new Ext.data.XmlReader(
{
   record: 'Function'
},SPpermissionRecord
);

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
                        name : 'Tag'
                    }, {
                        name : 'Link'
                    }, {
                        name : 'Name'
                    }, {
                        name : 'Value',
                        type : 'int'
                    }, {
                        name : 'Estimate',
                        type : 'float'
                    }, {
                        name : 'Status'
                    }, {
                        name : 'Importance',
                        type : 'int'
                    }, {
                        name : 'Release'
                    }, {
                        name : 'Sprint'
                    }, {
                        name : 'Notes'
                    }, {
                        name : 'HowToDemo'
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
        dataIndex : 'Tag',
        header : 'Tag',
        width : 90
    }, {
        dataIndex : 'Name',
        header : 'Story Name',
        width : 400
    },{
        dataIndex : 'Value',
        header : 'Value',
        width : 90
    }, {
        dataIndex : 'Estimate',
        header : 'Estimate',
        width : 90
    }, {
        dataIndex : 'Importance',
        header : 'Importance',
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

                            // 改變Story List的title顯示Sprint的名稱與ID
                            storyList.setTitle('Sprint' + r.get('Id') + ':'
                                    + r.get('Goal') + '\t\t[Story Point:'
                                    + point + ']');
                        }
                    });

            // 計算Due Day，與判斷此Sprint是否可編輯
            var start = Date.parseDate(r.get('StartDate'),'Y/m/d');
            
            topToolbar.get('editSprint').enable();
	        topToolbar.get('removeSprint').enable();
	        
            if (start != null) {
                // 計算DueDate
                var today = new Date();
                due = start.add(Date.DAY, parseInt(r.get('Interval')) * 7);
               
                
                /*
	            if(due < today)
				{
				    topToolbar.get('editSprint').disable();
				    topToolbar.get('removeSprint').disable();			
				}
				else
	            {
	                topToolbar.get('editSprint').enable();
	                topToolbar.get('removeSprint').enable();
	            }
	            */
                
                if(start < today)
                {
                    topToolbar.get('moveSprint').disable();
                }else
                {
                    topToolbar.get('moveSprint').enable(); 
                }
                
                topToolbar.checkPermission();
            }

        });

ezScrum.SprintMaster = Ext.extend(Ext.grid.GridPanel, {
            title : 'Sprint List',
            store : sprintStore,
            colModel : createSprintCloumns(),
            sm : sprintSelectionModel,
            initComponent:function()
            {
                // 當有修改或新增Sprint的時候就需要更新
                detailWin.on('Success',function(response,values)
                {
                    refreshSprintStore();
                    Ext.example.msg('Modify Sprint', 'Modify Sprint Success.');
                });
                detailWin.on('Failure',function(obj,response,values)
                {
                
                });
                // 有刪除Sprint的時候就需要更新
                deleteWin.on('DeleteSuccess',function(obj,response)
                {
                    refreshSprintStore();
                    Ext.example.msg('Delete Sprint', 'Delete Sprint Success.');
                });
                // Sprint有移動的時候也需要更新
                moveWin.on('MoveSuccess',function(obj,response)
                {
                    refreshSprintStore();
                });
				ezScrum.SprintMaster.superclass.initComponent.apply(
                        this, arguments);
                 
               
               
            },
			editSprint: function(record) {
				var edit = true;
                var Info = "The sprint is overdue, are you sure to edit it?";
				this.overdueConfirm(Info, record, edit);
			},
			overdueConfirm: function(info, record, edit) {
				// compute overdue or not
				var start = Date.parseDate(record.get('StartDate'),'Y/m/d');
				var today = new Date();
                due = start.add(Date.DAY, parseInt(record.get('Interval')) * 7);


				var goal = record.get('Goal');
				goal = goal.replace(/&lt;/ig, "<");
				goal = goal.replace(/&gt;/ig, ">");
				goal = goal.replace(/&apos;/ig, "'");
				goal = goal.replace(/&quot;/ig, "\"");
				goal = goal.replace(/&amp;/ig, "&");
				record.set('Goal', goal);
				
				var demoplace = record.get('DemoPlace');
				demoplace = demoplace.replace(/&lt;/ig, "<");
				demoplace = demoplace.replace(/&gt;/ig, ">");
				demoplace = demoplace.replace(/&apos;/ig, "'");
				demoplace = demoplace.replace(/&quot;/ig, "\"");
				demoplace = demoplace.replace(/&amp;/ig, "&");
				record.set('DemoPlace', demoplace);
				
				var dailyscrum = record.get('DailyScrum');
				dailyscrum = dailyscrum.replace(/&lt;/ig, "<");
				dailyscrum = dailyscrum.replace(/&gt;/ig, ">");
				dailyscrum = dailyscrum.replace(/&apos;/ig, "'");
				dailyscrum = dailyscrum.replace(/&quot;/ig, "\"");
				dailyscrum = dailyscrum.replace(/&amp;/ig, "&");
				record.set('DailyScrum', dailyscrum);

               
	            if(due < today) {
					Ext.MessageBox.confirm('Confirm', info, function(btn) {
						if(btn == 'yes') {
							if (edit == true) {				
								detailWin.resetForm();
								detailWin.loadData(record);
								detailWin.showWidget('Edit Sprint');
								detailWin.setIsCreate(false);
							} else {
								deleteWin.deleteSprint(record);
							}
						}
					});
				} else {
					if (edit == true) {				
						detailWin.resetForm();
						detailWin.loadData(record);
						detailWin.showWidget('Edit Sprint');
						detailWin.setIsCreate(false);
					} else {
						deleteWin.deleteSprint(record);
					}
				}
			},
			deleteSprint: function(record) {
				var edit = false;
				var Info = "The sprint is overdue, are you sure to delete it?";
				this.overdueConfirm(Info, record, edit);
			}
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
// 顯示移動Sprint的視窗，並且負責與Server端連線處理移動Sprint的動作
var moveWin = new ezScrum.MoveSprintWidget();

// 顯示刪除視窗，並且負責與Server端連線處理刪除資料的動作
var deleteWin = new ezScrum.DeleteSprintWidget();

// 負責以Form的方式顯示Sprint的詳細資料
var detailWin = new ezScrum.ShowSprintDetailWin();

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
                        /*-----------------------------------------------------------
                         *      新增一筆Sprint的Button
                         *-------------------------------------------------------------*/
                        id : 'newSprint',
                        text : 'New',
                        icon : 'images/add3.png',
                        handler : function() {
                            // 從Sprint List中取得最新的一筆Sprint資料，以供New的時候自動化的填寫一些表格
                            var s = sprintList.getStore();
                            // 依照ID排序
                            s.sort('Id','DESC');
                            var lastData = s.getAt(0);
                            detailWin.resetForm();
                            detailWin.loadDataForNewSprint(lastData);
                            detailWin.showWidget('Create Sprint');
                            detailWin.setIsCreate(true);
                        }
                    },{
                        /*-----------------------------------------------------------
                         *      編輯一筆Sprint的Button
                         *-------------------------------------------------------------*/
                        id : 'editSprint',
                        text : 'Edit',
                        icon : 'images/edit.png',
                        disabled : true,
                        handler : function() {
                            // 取得現在Sprint List點選的Sprint欄位
                            var selectData = sprintList.getSelectionModel().getSelected();
                            sprintList.editSprint(selectData);
                        }
                         /*-----------------------------------------------------------
                         *      移動一筆Sprint的Button
                         *-------------------------------------------------------------*/
                    },  {
                        id : 'moveSprint',
                        text : 'Move',
                        disabled : true,
                        icon : 'images/arrow_right.png',
                        handler:function()
                        {
                            // 取得目前選取的Sprint
                            var selectId = sprintList.getSelectionModel().getSelected().get('Id')^0;
                            
                            // 找出可以移動的Sprint
                           var canMoveSprint = new Array();
                           var count = sprintStore.getCount();
                           var today = new Date();
                           for(i=0;i<count;i++)
                           {
                                tmp = sprintStore.getAt(i);
                                s = Date.parseDate(tmp.get('StartDate'),'Y/m/d');
                                id = tmp.get('Id')^0;
                                if(s > today && id != selectId)
                                {
                                    canMoveSprint.push([id,tmp.get('Goal')]);
                                }
                           }
                            
                            moveWin.moveSprint(canMoveSprint,selectId);
                        }
                    },
                        /*-----------------------------------------------------------
                         *      刪除一筆Sprint的Button
                         *-------------------------------------------------------------*/
                     {
                        id : 'removeSprint',
                        text : 'Delete',
                        icon : 'images/delete.png',
                        disabled : true,
                        handler:function()
                        {
                        	// 取得所選擇的Sprint 資料
							var selectData = sprintList.getSelectionModel().getSelected();
                            sprintList.deleteSprint(selectData);
                        }
                    }],
			checkPermission:function()
			{
            // 所有enable的部份都要考慮權限
            topToolbar.addPermission?topToolbar.get('newSprint').enable():topToolbar.get('newSprint').disable();
            (topToolbar.editPermission&&(!topToolbar.get('editSprint').disabled))?topToolbar.get('editSprint').enable():topToolbar.get('editSprint').disable();
            (topToolbar.movePermission&&(!topToolbar.get('moveSprint').disabled))?topToolbar.get('moveSprint').enable():topToolbar.get('moveSprint').disable();
            (topToolbar.deletePermission&&(!topToolbar.get('removeSprint').disabled))?topToolbar.get('removeSprint').enable():topToolbar.get('removeSprint').disable();
            
			}
});
function refreshSprintStore()
{
    /*-----------------------------------------------------------
     * 讀取所有的Sprint資訊，以顯示在畫面上
     *-------------------------------------------------------------*/
            Ext.Ajax.request({
                        url : 'showAllSprintForSprintPlan.do',
                        success : function(response) {
                            sprintStore.loadData(response.responseXML);
                        }
                    });
            sprintStore.setDefaultSort('Id', 'DESC');
}
/*
 * 當網頁讀取完畢之後，開始進行初始化的動作
 */
Ext.onReady(function() {
            Ext.QuickTips.init();
            var sprintPlanView = new Ext.Panel({
                        height : 600,
                        layout : 'border',
                        tbar : topToolbar,
                        items : [sprintList, storyList]
            });
            // 讀取角色權限
            Ext.Ajax.request({
            url:'AjaxGetSPPermission.do',
            success:function(response){
                var P_Records = SPpermissionReader.readRecords(response.responseXML);
                topToolbar.addPermission = P_Records.records[0].get('AddSprint')=='true'?true:false;
                topToolbar.movePermission = P_Records.records[0].get('EditSprint')=='true'?true:false;
                topToolbar.deletePermission = P_Records.records[0].get('DeleteSprint')=='true'?true:false;
                topToolbar.editPermission = P_Records.records[0].get('EditSprint')=='true'?true:false;
                topToolbar.checkPermission();   
            }
        });     
            refreshSprintStore();
            sprintPlanView.render('Sprint-Plan');
        });


</script>

<div id = "Sprint-Plan"></div>

<div id="SideShowItem" style="display:none;">showSprintPlan</div>