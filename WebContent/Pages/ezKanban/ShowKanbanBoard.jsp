<%@ page contentType="text/html; charset=utf-8"%>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean"%>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html"%>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic"%>
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

	<script type="text/javascript" src="javascript/KanbanWidget/Common.js"></script>
	<script type="text/javascript" src="javascript/AjaxWidget/CommonFormVTypes.js"></script>
	<script type="text/javascript" src="javascript/KanbanWidget/ChangeWorkItemStatusWidget.js"></script>
	<script type="text/javascript" src="javascript/KanbanWidget/CreateWorkItemWidget.js"></script>
	<script type="text/javascript" src="javascript/KanbanWidget/EditWorkItemWidget.js"></script>
	<script type="text/javascript" src="javascript/KanbanWidget/DeleteWorkItemWidget.js"></script>
	<script type="text/javascript" src="javascript/KanbanWidget/ShowMessageWidget.js"></script>
	<script type="text/javascript" src="javascript/KanbanWidget/CreateTaskWidget.js"></script>
	
	<script type="text/javascript" src="javascript/AjaxWidget/Message.js"></script>
	
	<link rel="stylesheet" type="text/css" href="css/Message.css"/>

<script type="text/javascript">
	Ext.ns('ezScrum');
	
	function getBasicInfo(){
        /* 取得 Handler 資料 */
		Ext.Ajax.request({
			url:'getAddWorkItemInfo.do',
			success:function(response){
				typeStoreForCreate.loadData(response.responseXML);
				priorityStoreForCreate.loadData(response.responseXML);
				handlerStoreForCreate.loadData(response.responseXML);
				typeStoreForTask.loadData(response.responseXML);
				handlerStoreForTask.loadData(response.responseXML);
				
				typeStoreForEdit.loadData(response.responseXML);
				workstateStoreForEdit.loadData(response.responseXML);
				priorityStoreForEdit.loadData(response.responseXML);
				handlerStoreForEdit.loadData(response.responseXML);
			}
		});
    }

	// 處理WorkItem Panel選取
	function handlePanelClick(workitem, isClick) {
		// 儲存被點選的 workitem
		document.getElementById("workItemPanelID").value = workitem.id;
		
		// 取消選取其他 WorkItem Panel
		var array = Ext.getCmp('StatusesWidget').items;
		var other;
		// Statuses
		for (var i = 0; i < array.getCount(); i = i + 1)
		{
			// WorkItems
			for (var j = 0; j < array.get(i).items.length; j = j + 1)
            {
            	// 改回原本的CSS
            	other = array.get(i).get(j);
		    	other.getEl().first().applyStyles('border-color: #99bbe8');
		    	other.getEl().last().first().applyStyles('border-color: #99bbe8');
		    	if (!other.collapsed)
		    	{
			    	// Tasks
			    	for (var k = 0; k < array.get(i).get(j).items.length; k = k + 1)
			    	{
			    		// 改回原本的CSS
		            	other = array.get(i).get(j).get(k);
				    	other.getEl().first().applyStyles('border-color: #99bbe8');
				    	other.getEl().last().first().applyStyles('border-color: #99bbe8');
			    	}
		    	}
           	}
		}
		
		// 選取 WorkItem Panel 項目顏色改變
		var header = workitem.getEl().first();
		var bodyContent = workitem.getEl().last().first();
		// 改變CSS
		header.applyStyles('border-color: red;');
		bodyContent.applyStyles('border-color: red;');
    }
    
    // 建立 Status Panel
    function createStatusPanel(id, name, limit) {
		// 判斷字串
		if (limit == null || limit == -1 || limit == '')
			limit = '';
		else
			limit = '(' + limit + ')';
		
		// 將目前 Kanban 中的 Status 建立成 Panel
		var newPanel = new Ext.Panel({
	        id : id,
	        header : true,
	        padding: '5',
	        autoScroll : true,
	        margins: '0 5 0 0',
	        height : 520,
			width : 200
	    });
	    newPanel.setTitle(name + limit);
	    
	    return newPanel;
    }
    
    // 建立 Story WorkItem Panel
    function createWorkItemPanel(id, name, handler, priority, workstate, deadline) {
		// 判斷字串
		if (handler == null)
			handler = '';
		if (priority == null)
			priority = '';
		
		// 將目前 Status 中的 WorkItem 建立成 Panel
		var newPanel = new Ext.Panel({
	        id : id,
	        type : 'User Story',
	        header : true,
	        autoScroll : true,
	        margins: '0 0 5 0',
	        height : 140,
			width : 170,
			html : '<textarea style="height:95%; width:100%; cursor:move">Handler:' + handler + '\nPriority:' + priority +
					 '\nWorkState:' + workstate + '\nDeadline:' + deadline + '</textarea>'
	    });
	    newPanel.setTitle(name);
	    
	    return newPanel;
    }
    
    // 建立 Task Panel
    function createTaskPanel(id, name, handler, workstate) {
		// 判斷字串
		if (handler == null)
			handler = '';

		// 將目前 Status 中的 WorkItem 建立成 Panel
		var newPanel = new Ext.Panel({
	        id : id,
	        type : 'Task',
	        header : true,
	        autoScroll : true,
	        margins: '0 0 5 0',
	        //padding: '0px 0px ' + paddingPx + ' 0px',
	        height : 70,
			width : 150,
			html : '<textarea style="height:95%; width:100%; background-color: #dfe8f6; cursor:default">Handler:' + handler + '\nWorkState:' + workstate + '</textarea>'
	    });
	    newPanel.setTitle(name);
	    
	    return newPanel;
    }
    
    // 空白 Panel
    function createWhitePanel(id) {
		// 空白 Panel
		var newPanel = new Ext.Panel({
	        id : id + '-Tasks',
	        title : 'Tasks',
	        type : 'Tasks',
	        parentId : id,
	        autoScroll : true,
        	collapsible : true,
        	collapsed : true,
	        height : 100,
			width : 170
	    });
	    return newPanel;
    }
    
    function workitemPanelAdd(record, isUpdate){
    	var newPanel = createWorkItemPanel(record.data['Id'].toString(), record.data['Name'],
							record.data['Handler'], record.data['Priority'], record.data['WorkState'], record.data['Deadline']);
		var statusWidget = Ext.getCmp('StatusesWidget');
		var statusIndex = 0;
		if (isUpdate){
			// 找出 WorkItem Status Panel
			var status = Ext.getCmp(document.getElementById("oldStatusID").value);
			// 移除舊 WorkItem Panel
	    	status.remove(record.data['Id'], true);
	    	// Panel Index
	    	for (i = 0; i < statusWidget.items.length; i = i + 1)
			{
		    	if (statusWidget.get(i).getId() == document.getElementById("oldStatusID").value){
		    		// 紀錄 Status 在 StatusesWidget 中的位置
			    	statusIndex = i;
			    	break;
			    }
			}
	    	
	    	// WorkItem Panel 加入至 StatusPanel
			status.insert(document.getElementById("oldPosition").value, newPanel);
			status.doLayout();
		}
		else{
			// WorkItem Panel 加入至 StatusesWidget(Backlog)
			statusWidget.get(0).add(newPanel);
			statusWidget.get(0).add(createWhitePanel(record.data['Id'].toString()));
			statusWidget.get(0).doLayout();
		}
		// WorkItem Panel 項目顏色改變, 改變CSS
		var header = newPanel.getEl().first();
		header.applyStyles(storyPanelStyle);
    }
    
    function taskPanelAdd(record, isUpdate){
    	// 建立新 Task Panel
    	var newPanel = createTaskPanel(record.data['Id'].toString(), record.data['Name'],
									record.data['Handler'], record.data['WorkState']);
		var workitemTasks;
		if (isUpdate){
			// 找出 WorkItem Tasks Panel
			workitemTasks = Ext.getCmp(document.getElementById("parentPanelID").value);
			// 移除舊 Task Panel
	    	workitemTasks.remove(record.data['Id'], true);	    	
	    	// Task Panel 加入至 TasksPanel
			workitemTasks.insert(document.getElementById("oldPosition").value, newPanel);
		}
		else{
			// 找出欲加入 Task 的 WorkItem
			var workitem = Ext.getCmp(document.getElementById("workItemPanelID").value);
			workitemTasks = Ext.getCmp(workitem.getId() + '-Tasks');
			// Task Panel 加入至 TasksPanel
			workitemTasks.add(newPanel);
		}
		workitemTasks.doLayout();
    }

	/* Grid View */
	Ext.onReady(function() {
		Ext.QuickTips.init();
		
		// 移除托拉後原本狀態中的 Task
		function removeOldTaskPanel(taskId){
			// 最外層的 Panel
	    	var statuses = Ext.getCmp('StatusesWidget');
	    	// 找出被 Drag 的 Panel(Task)
	    	var targetPanel;
	    	for (i = 0; i < statuses.items.length; i = i + 1)
            {
	            statuses.get(i).remove(taskId, true);
            }
		}

		// 取得並建立 Task
	    function createTaskWorkItem(currentPanel, parentId, taskIndex){	    	
	    	/* 根據 Story 取得 Tasks 資料 */
			Ext.Ajax.request({
				url:'getTaskWorkItemsByStoryID.do?issueID=' + parentId,
				async: true,	// 同步執行參數
				success:function(response){
					// 清除前一次資料
					workitemStore.removeAll(true);
					// 讀取目前資料
					workitemStore.loadData(response.responseXML);
					// 沒有資料則跳出
					if (workitemStore.getCount() == 0)
						return;
					// Story Panel 加入 Task Panel
			 		for(var j = 0; j < workitemStore.getCount(); j++)
			 		{
			 			var index = parseInt(taskIndex); 
			 			// 刪除舊的 Task Panel
			 			removeOldTaskPanel(workitemStore.getAt(j).get('Id').toString());
						// 建立新的
			 			var itemPanel = createTaskPanel(workitemStore.getAt(j).get('Id').toString(), workitemStore.getAt(j).get('Name'),
									workitemStore.getAt(j).get('Handler'), workitemStore.getAt(j).get('WorkState'));
						currentPanel.insert(index + j, itemPanel);

						// 畫面更新
			 			currentPanel.doLayout();
			 		}
			 		// 畫面更新
			 		currentPanel.doLayout();
			 		
			 		// 畫面更新
					Ext.getCmp('StatusesWidget').doLayout();
				}
			});
			//*/
	    }
	    
	    // 取得並建立 WorkItem
	    function createWorkItem(statusId){
			/* 根據 Status 取得 WorkItems 資料 */
			Ext.Ajax.request({
				url:'getWorkItemsByStatusID.do?issueID=' + statusId,
				async: true,
				success:function(response){
					// 清除前一次資料
					workitemStore.removeAll(true);
					// 讀取目前資料
					workitemStore.loadData(response.responseXML);
					// 沒有資料則跳出
					if (workitemStore.getCount() == 0)
						return;
					// 取得該 Status Panel + index
					var currentPanel = Ext.getCmp('StatusesWidget').get(workitemStore.getAt(0).get('Status').toString());
					var index = Ext.getCmp('StatusesWidget').items.indexOf(currentPanel);
					// Status Panel 加入 WorkItem Panel
			 		for(var j = 0; j < workitemStore.getCount(); j++)
			 		{
			 			var itemPanel = createWorkItemPanel(workitemStore.getAt(j).get('Id').toString(), workitemStore.getAt(j).get('Name'),
									workitemStore.getAt(j).get('Handler'), workitemStore.getAt(j).get('Priority'), workitemStore.getAt(j).get('WorkState'), 
									workitemStore.getAt(j).get('Deadline'));
						currentPanel.insert(j + 1, itemPanel);
						
						// 畫面更新
			 			currentPanel.doLayout();
			 			
			 			// Story Panel 項目顏色改變, 改變CSS
						var childHeader = itemPanel.getEl().first();
						childHeader.applyStyles(storyPanelStyle);
						//createTaskWorkItem(currentPanel, workitemStore.getAt(workitemStore.getCount() - 1 - j).get('Id').toString(), workitemStore.getCount() - j);
			 		}
			 		// Status Panel 加入 Task Panel
			 		for(var k = workitemStore.getCount() - 1; k >= 0 ; k--)
			 		{
			 			var tasksPanel = createWhitePanel(workitemStore.getAt(k).get('Id').toString());
			 			createTaskWorkItem(tasksPanel, workitemStore.getAt(k).get('Id').toString(), k + 1);
			 			currentPanel.insert(k + 1, tasksPanel);
						//createTaskWorkItem(currentPanel, workitemStore.getAt(k).get('Id').toString(), k + 1);
			 		}
			 		// 畫面更新
			 		currentPanel.doLayout();
				}
			});
	    }
    
		/* 取得 WorkItem Status 頁面所需資料 */
		function LoadData(option)
		{
			/* 取得 WorkItem Status 資料 */
			Ext.Ajax.request({
				url:'showKanbanBoardAction.do',
				async: true,
				success:function(response){
					// 取得  Priority + Handler 選項
					getBasicInfo();
					
					// 取得 IssueTypeID (Type:WorkItem)
					issueTypeStore.loadData(Ext.decode(response.responseText));
					// 將 IssueTypeID 存入 Input
	    			document.getElementById("typeID").value = issueTypeStore.getAt(0).get('Id');
					
					// 初始化 Status Panel
					statusStore.loadData(Ext.decode(response.responseText));
					for (var i = 0; i < statusStore.getCount(); i++)
					{
						var statusId = statusStore.getAt(i).get('Id').toString();
					    var newPanel = createStatusPanel(statusStore.getAt(i).get('Id').toString(), statusStore.getAt(i).get('Name'),
												statusStore.getAt(i).get('Limit'));
						
						// Panel 加入至 StatusesWidget
						Ext.getCmp('StatusesWidget').insert(i + 1, newPanel);
						
						// 如果 Status 寬度加總超過 Container 寬則 Container 增加寬度
						var containerWidth = Ext.getCmp('StatusesWidget').getInnerWidth();
						if (containerWidth < ((i + 1) * newPanel.initialConfig.width))
							Ext.getCmp('StatusesWidget').setWidth(containerWidth + newPanel.initialConfig.width);
						
						/* 根據 Status 取得 WorkItems 資料 */
						createWorkItem(statusId);
						
						// 畫面更新
						Ext.getCmp('StatusesWidget').doLayout();

						// Status Panel 項目顏色改變, 改變CSS
						var header = newPanel.getEl().first();
						header.applyStyles(panelColors[i]);
					}
				}
			});
			
			/* 取得 Kanban Board 權限資料 */
			Ext.Ajax.request({
				url:'AjaxGetKBDPermission.do',
				success:function(response){
					var permissionRs = KBDPermissionReader.readRecords(response.responseXML);
			 		var permissionCount = permissionRs.totalRecords;
			 		for(var i = 0; i < permissionCount; i++)
			 		{
			 			var permissionRecord = permissionRs.records[i];
						statusesWidget.loadPermission(permissionRecord.data['AddWorkItem'],permissionRecord.data['EditWorkItem'],permissionRecord.data['DeleteWorkItem']);     
			 		}
				}
			});
		}
		
		// Change WorkItem Status Widget (變更狀態確認)
		var changeStatusWidget = new ezScrum.ChangeWorkItemStatusWidget({
			listeners:{
				ChangeSuccess:function(win, response){				
					this.hide();
					Ext.example.msg('Change Status', 'Change Status Success.');
				},
				ChangeFailure:function(win, response){
					Ext.example.msg('Change Status', 'Change Status Failure.');
				},
				ChangeCancel:function(win){
					/* 將 workitem 搬回原本的狀態 */
					// 取得先前拖曳的項目和項目所在的狀態, 狀態索引(等於WorkItem Panel header顏色索引)
					var workitem = Ext.getCmp(win.issueId);
					var status = Ext.getCmp(win.oldStatusId);
					var index = Ext.getCmp('StatusesWidget').items.indexOf(status);
					
					// 複製一份拖曳項目(使用一開始的 config, 所以 title 是空的)
	                var clonePanel = workitem.cloneConfig(workitem.initialConfig);
	                // 儲存id
	                var Id = workitem.getId();
	                // 儲存 Name
	                var name = workitem.title;
					
					// 原本的 Panel 移除拖曳項目
					var parentElement = workitem.findParentByType('panel');
	                parentElement.remove(workitem, true);
	                parentElement.remove(Id + "-Tasks", true);
	                // 目標 Panel 加入拖曳項目(以後需改成依照Priority高低插入)
	           		status.insert(win.oldPosition, clonePanel);
	                // 設定Id + Name
					clonePanel.id = Id;
					clonePanel.setTitle(name);
					
					// 畫面更新
					parentElement.doLayout();
	                status.doLayout();
	                
	                // WorkItem Panel 項目顏色改變, 改變CSS
					var header = clonePanel.getEl().first();
					header.applyStyles(storyPanelStyle);
					
					// 原本 WorkItem 在 Status 中的順序
					var index = parseInt(win.oldPosition);
					// 建立新的 Task Panel
					var tasksPanel = createWhitePanel(Id);
					createTaskWorkItem(tasksPanel, Id, index + 1);
					status.insert(index + 1, tasksPanel);
					
					// 畫面更新
	                status.doLayout();
				}
			}
		});
	
		// A DropZone which cooperates with DragZones whose dragData contains
		// a "field" property representing a form Field. Fields may be dropped onto
		// grid data cells containing a matching data type.
		Ext.ux.PanelFieldDropZone = Ext.extend(Ext.dd.DropZone, {
		    constructor: function(){},
		
			//  Call the DropZone constructor using the View's scrolling element
			//  only after the grid has been rendered.
		    init: function(panel) {	 
		        if (panel.rendered) {
	                Ext.ux.PanelFieldDropZone.superclass.constructor.call(this, panel.getEl());
	                var i = Ext.fly(panel.getEl());
	                i.unselectable();
	            } else {
	                panel.on('afterlayout', this.init, this, {single: true});
	            }
		    },
			//  Scroll the main configured Element when we drag close to the edge
		    containerScroll: true,
		
		    getTargetFromEvent: function(e) {
				// 判斷拖曳項目拖曳至哪個Status項目位置
		    	var container = Ext.getCmp('StatusesWidget');
		    	var count = container.items.length;
		    	for (i = 0; i < count; i = i + 1)
	            {
	            	if (container.get(i).getEl().getLeft() < e.getXY()[0] &&
	            		container.get(i).getEl().getRight() > e.getXY()[0] &&
	            		container.get(i).getEl().getTop() < e.getXY()[1] &&
	               		container.get(i).getEl().getBottom() > e.getXY()[1])
	           		{
	           			// 回傳此Status項目
	           			return container.get(i);
	           		}
	            }
		    },
		
			//  On Node enter, see if it is valid for us to drop the field on that type of column.
		    onNodeEnter: function(target, dd, e, dragData) {
		        delete this.dropOK;
		        if (!target) {
		            return;
		        }
				//  Check that a field is being dragged.
		        var f = dragData.field;
		        if (!f) {
		            return;
		        }
		        this.dropOK = true;
		        Ext.fly(target).addClass('x-drop-target-active');
		    },
		
			//  Return the class name to add to the drag proxy. This provides a visual indication
			//  of drop allowed or not allowed.
		    onNodeOver: function(target, dd, e, dragData) {
		        return this.dropOK ? this.dropAllowed : this.dropNotAllowed;
		    },
			//  nhighlight the target node.
		    onNodeOut: function(target, dd, e, dragData) {
		        Ext.fly(target).removeClass('x-drop-target-active');
		    },
			//  Process the drop event if we have previously ascertained that a drop is OK.
		    onNodeDrop: function(target, dd, e, dragData) {
		        if (this.dropOK) {
	                // 複製一份拖曳項目(使用一開始的 config, 所以 title 是空的)
	                var clonePanel = dragData.field.cloneConfig(dragData.field.initialConfig);
	                // 儲存id
	                var Id = dragData.field.getId();
	                // 儲存 Name
	                var name = dragData.field.title;
	                // 儲存 Handler Priority
	                var detail = dragData.field.el.dom.getElementsByTagName('textarea')[0].innerHTML;
	                var flag = false;
	                
	                // 儲存目標狀態裡 WorkItem 數量
	                var count = 0;
	                // 狀態裡 Panel 數量
	                for (var k = 0; k < target.items.length; k = k + 1)
	                {
	                	// 屬於 WorkItem Panel 數量
	                	if (target.get(k).type == "User Story")
		                	count++;
	                }
	                // 有限制才判斷
	                if (target.title.lastIndexOf('(') != -1){
		                // 取出 Status Limit 數量
		                var str = target.title.substr(target.title.lastIndexOf('('));
		                var limitNumber = str.substr(1, str.length - 2);
		                // 如果已經達到 Status Limit 數量則不能再加入新的 WorkItem
		                if (limitNumber == count){
		                	messageWidget.showCustomMessage("該狀態已達到WIP Limit上限!<br/>");
		                	return;
	                	}
	                }
	
	                // 判斷是否拖曳至原狀態
	                for (var i = 0; i < target.items.length; i = i + 1)
	                {
	                	// 拖曳至同位置(狀態)則狀態不變
	                	if (target.get(i).getId() == dragData.field.getId())
		                	return true;
	                }
					// 原本的 Panel 移除拖曳項目
					var parentElement = dragData.field.findParentByType('panel');
	                parentElement.remove(dragData.field, true);
	                parentElement.remove(Id + "-Tasks", true);
	                // 目標 Panel 加入拖曳項目(以後需改成依照Priority高低插入)
	           		target.add(clonePanel);
	                // 設定Id + Name + Handler Priority
					clonePanel.id = Id;
					clonePanel.setTitle(name);
					// 畫面更新
					parentElement.doLayout();
	                target.doLayout();
	                
	                // 設定描述
	                clonePanel.update('<textarea style="height:95%; width:100%; cursor:move">' + detail + '</textarea>');
	                
	                // 取得目標 Panel 索引(與顏色索引相同)
	                var index = Ext.getCmp('StatusesWidget').items.indexOf(target);
	                // WorkItem Panel 項目顏色改變, 改變CSS
					var header = clonePanel.getEl().first();
					header.applyStyles(storyPanelStyle);
					
					// 建立新的 Task Panel
					var tasksPanel = createWhitePanel(Id);
					createTaskWorkItem(tasksPanel, Id, target.items.length + 1);
					target.add(tasksPanel);

					// 畫面更新
	                target.doLayout();
	                
	                // 改變 WorkItem 狀態 (call Ajax 動作)
					changeStatusWidget.changeStatus(Id, target.id, document.getElementById("oldStatusID").value, document.getElementById("oldPosition").value);
		            return true;
		        }
		    }
		});
		
		//  A class which makes Fields within a Panel draggable.
		//  the dragData delivered to a coooperating DropZone's methods contains
		//  the dragged Field in the property "field".
		Ext.ux.PanelFieldDragZone = Ext.extend(Ext.dd.DragZone, {
		    constructor: function(){},
			//  Call the DRagZone's constructor. The Panel must have been rendered.
		    init: function(panel) {
		        if (panel.nodeType) {
		            Ext.ux.PanelFieldDragZone.superclass.init.apply(this, arguments);
		        } else {
		            if (panel.rendered) {
		                Ext.ux.PanelFieldDragZone.superclass.constructor.call(this, panel.getEl());
		                var i = Ext.fly(panel.getEl());
		                i.unselectable();
		            } else {
		                panel.on('afterlayout', this.init, this, {single: true});
		            }
		        }
		    },
		    scroll: false,
		
			//  On mousedown, we ascertain whether it is on one of our draggable Fields.
			//  If so, we collect data about the draggable object, and return a drag data
			//  object which contains our own data, plus a "ddel" property which is a DOM
			//  node which provides a "view" of the dragged data.
		    getDragData: function(e) {
		    	// 最外層的 Panel
		    	var statuses = Ext.getCmp('StatusesWidget');
		    	// Header + 上層工具列的高度
		    	var titleHeight = statuses.header.getBottom(true) + statuses.getTopToolbar().getPosition()[1];
		    	// 托拉的對象在上層工具列以下開始算起, 以上則不考慮
		    	if (titleHeight > e.getXY()[1])
		    		return;
		    		
		    	// 找出被 Drag 的 Panel(WorkItem)
		    	var dragPanel;
		    	// Status
		    	for (var i = 0; i < statuses.items.length; i = i + 1)
	            {
	            	// WorkItems
		            for (var j = 0; j < statuses.get(i).items.length; j = j + 1)
		            {
		            	if (statuses.get(i).get(j).getEl().getLeft() < e.getXY()[0] &&
		            		statuses.get(i).get(j).getEl().getRight() > e.getXY()[0] &&
		            		statuses.get(i).get(j).getEl().getTop() < e.getXY()[1] &&
		               		statuses.get(i).get(j).getEl().getBottom() > e.getXY()[1]) 
		            	{
		            		dragPanel = statuses.get(i).get(j);
		            		
		            		// 儲存 workitem 原本的狀態
							document.getElementById("oldStatusID").value = statuses.get(i).id;
							// 儲存 workitem 原本的位置
							document.getElementById("oldPosition").value = j;
							
							if (dragPanel.type == "Tasks" && !dragPanel.collapsed){
								// 儲存 workitem Tasks Panel ID
								document.getElementById("parentPanelID").value = dragPanel.id;
								// Tasks
								for (var k = 0; k < dragPanel.items.length; k = k + 1){
									//alert(dragPanel.get(k).type);
									if (dragPanel.get(k).getEl().getLeft() < e.getXY()[0] &&
					            		dragPanel.get(k).getEl().getRight() > e.getXY()[0] &&
					            		dragPanel.get(k).getEl().getTop() < e.getXY()[1] &&
					               		dragPanel.get(k).getEl().getBottom() > e.getXY()[1]) 
					            	{
					            		dragPanel = dragPanel.get(k);
					            		// 儲存 workitem 原本的位置
										document.getElementById("oldPosition").value = k;
					            		break;
					            	}
								}
							}
							// Tasks 無法點選
		            		if (dragPanel.type == "Tasks")
		            			return;
		            			
		            		// Panel 點選
		            		handlePanelClick(dragPanel, true);
		            		
		            		// Task 無法拖曳, 跟 Story 一起移動
		            		if (dragPanel.type == "Task" || dragPanel.type == "Tasks")
		            			return;
		            			
		            		break;
		            	}
	            	}
	            }

		        if (dragPanel) {
		            e.stopEvent();
					//  Ugly code to "detach" the drag gesture from the input field.
					//  Without this, Opera never changes the mouseover target from the input field
					//  even when dragging outside of the field - it just keeps selecting.
		            if (Ext.isOpera) {
		                Ext.fly(dragPanel).on('mousemove', function(e1){
		                    dragPanel.style.visibility = 'hidden';
		                    (function(){
		                        dragPanel.style.visibility = '';
		                    }).defer(1);
		                }, null, {single:true});
		            }
					
					//  create a drag item to display(fly)
					var flyElement = dragPanel.el.dom.cloneNode(true);
					flyElement.setAttribute('id', 'fly');
					flyElement.setAttribute('style', 'width:' + dragPanel.getEl().getWidth());				
		            Ext.fly(flyElement).setWidth(dragPanel.getEl().getWidth());
		            return {
		                field: dragPanel,
		                ddel: flyElement
		            };
		        }
		    },
		
			//  The coordinates to slide the drag proxy back to on failed drop.
		    getRepairXY: function() {
		        return this.dragData.field.getEl().getXY();
		    }
		});
		
		// Create WorkItem Widget
		var createWorkItemWidget = new ezScrum.AddNewWorkItemWidget({
			listeners:{
				CreateSuccess:function(win, form, response, record, workitemType){
					workitemPanelAdd(record, false);
					
			 		this.hide();
			 		Ext.example.msg('Create WorkItem', 'Create WorkItem Success.');
				},
				CreateFailure:function(win, form, response, issueId){
					Ext.example.msg('Create WorkItem', 'Create WorkItem Failure.');
				}
			}
		});
		
		// Create Task Widget
		var createTaskWidget = new ezScrum.AddNewTaskWidget({
			listeners:{
				CreateSuccess:function(win, form, response, record, workitemType){
					// 建立 Panel
					taskPanelAdd(record, false);
					
			 		this.hide();
			 		Ext.example.msg('Create Task', 'Create Task Success.');
				},
				CreateFailure:function(win, form, response, issueId){
					Ext.example.msg('Create Task', 'Create Task Failure.');
				}
			}
		});
		
		// Edit WorkItem Widget
		var editWorkItemWidget = new ezScrum.EditWorkItemWidget({
			listeners:{
				LoadSuccess:function(win, form, response, record){
					// Load WorkItem Success
				},
				LoadFailure:function(win, form, response, issueId){
					Ext.example.msg('Load WorkItem', 'Load WorkItem Failure.');
				},
				EditSuccess:function(win, form, record, workitemType){
					// 建立 Panel
					if (workitemType == "User Story")
						workitemPanelAdd(record, true);
					else
						taskPanelAdd(record, true);
					
					this.hide();
					Ext.example.msg('Edit WorkItem', 'Edit WorkItem Success.');
					
				},
				EditFailure:function(win, form, response, issueId){
					Ext.example.msg('Edit WorkItem', 'Edit WorkItem Failure.');
				}
			}
		});
		
		// Delete WorkItem Widget
		var deleteWorkItemWidget = new ezScrum.DeleteWorkItemWidget({
			listeners:{
				DeleteSuccess:function(win, response, issueId, workitemType){
					var statusPanel = Ext.getCmp('StatusesWidget').get(document.getElementById("oldStatusID").value);
					if (workitemType == "User Story"){
						// 刪除 Story
						statusPanel.remove(issueId, true);
						statusPanel.remove(issueId + "-Tasks", true);
					}
					else{
						// 刪除 Task
						var tasksPanel = statusPanel.get(document.getElementById("parentPanelID").value);
						tasksPanel.remove(issueId, true);
					}
					statusPanel.doLayout();
					this.hide();
					Ext.example.msg('Delete WorkItem', 'Delete WorkItem Success.');
				},
				DeleteFailure:function(win, response, issueId){
					Ext.example.msg('Delete WorkItem', 'Delete WorkItem Failure.');
				}
			}
		});
		
		// Show Message Widget
		var messageWidget = new ezScrum.ShowMessageWidget({});

		// create the Data Store
	    var issueTypeStore = new Ext.data.Store({
			fields:[
				{name : 'Id', sortType:'asInt'}
			],
			reader:jsonIssueTypeReader
		});
		
		// Data store
		var statusStore = new Ext.data.Store({
			fields:[
				{name:'Id', type:'int'},
				{name:'Name'},
				{name : 'Description'}
			],
			reader:jsonStatusReader
		});
		
		// Data store
		var workitemStore = new Ext.data.Store({
			fields:[
				{name:'Id', type:'int'},
				{name:'Link'},
				{name:'Name'},
				{name : 'Status'},
				{name : 'Priority'},
				{name : 'Size'},
				{name : 'Handler'},
				{name : 'Deadline'},
				{name : 'Description'}
			],
			reader:workItemReader
		});
		
		// 包含所有 Status 的 Panel
		var statusesWidget = new Ext.Panel({
			id:'StatusesWidget',
			layout: 'column',
			layoutConfig: {
			    padding: '10',
			    align: 'stretch'
			},
			title : 'Kanban Board',
			autoScroll : true,
			autoDestroy : false,
			plugins: [ new Ext.ux.PanelFieldDragZone(), new Ext.ux.PanelFieldDropZone() ],
			addWorkItemPermission:false,
			editWorkItemPermission:false,
			deleteWorkItemPermission:false,
			// Add workitem action
			addWorkItem:function(type)
			{
				if (type == "User Story"){
					// 傳入 IssueTypeID
					createWorkItemWidget.showWidget(document.getElementById("typeID").value, type, -1);
				}
				else
				{
					var parentID = document.getElementById("workItemPanelID").value;
					// 加入 Task Defect 前須先判斷是否先選擇Story
					var widget = Ext.getCmp(parentID);
					// 沒選擇任何 Item or 沒選擇 Story WorkItem
					if(widget == null || widget.type != "User Story"){
						messageWidget.showMessage();
						return;
					}
					
					createTaskWidget.showWidget(document.getElementById("typeID").value, type, parentID);
				}
			},
			// Edit workitem action
			editWorkItem:function()
			{
				// 判斷是否存在
				var widget = Ext.getCmp(document.getElementById("workItemPanelID").value);
				if(widget != null)
				{
					editWorkItemWidget.loadEditWorkItem(document.getElementById("workItemPanelID").value, document.getElementById("typeID").value);
				}
				else
					messageWidget.showMessage();
			},
			// Delete WorkItem action
			deleteWorkItem:function()
			{
				// 判斷是否存在
				var widget = Ext.getCmp(document.getElementById("workItemPanelID").value);
				if(widget != null)
				{
					deleteWorkItemWidget.deleteWorkItem(document.getElementById("workItemPanelID").value, widget.type);
				}
				else
					messageWidget.showMessage();
			},
			tbar: [
				{id:'addWorkItemBtn', disabled:false, text:'Add', icon:'images/add3.png', 
					menu: { 
						items:[{
							id:'addStoryBtn',
							text:'Add WorkItem',
							handler:function(){statusesWidget.addWorkItem("User Story");},
							icon:'images/add3.png'
						},{
							id:'addTaskBtn',
							text:'Add Task',
							handler:function(){statusesWidget.addWorkItem("Task");}, 
							icon:'images/add3.png'
						}]
					}
				},
				{id:'editWorkItemBtn', disabled:false, text:'Edit Item', icon:'images/edit.png', handler:function(){statusesWidget.editWorkItem();}},
				{id:'deleteWorkItemBtn', disabled:false, text:'Delete Item', icon:'images/delete.png', handler: function(){statusesWidget.deleteWorkItem();}}
			],
			items:[
			],
			loadPermission:function(addWorkItemPermission, editWorkItemPermission, deleteWorkItemPermission)
			{
				this.addWorkItemPermission = addWorkItemPermission == "true";
				this.editWorkItemPermission = editWorkItemPermission == "true";
				this.deleteWorkItemPermission = deleteWorkItemPermission == "true";
				
				this.getTopToolbar().get('addWorkItemBtn').setDisabled(!this.addWorkItemPermission);					
				this.getTopToolbar().get('editWorkItemBtn').setDisabled(!this.editWorkItemPermission);
				this.getTopToolbar().get('deleteWorkItemBtn').setDisabled(!this.deleteWorkItemPermission);
			}
		});
		
		// 最外層
		var contentWidget = new Ext.Panel({
			id : 'contentWidget',
			height : 600,
			renderTo: Ext.get("content"),
			items : [statusesWidget]
		});
		
		LoadData();
	});
	
</script>

<input type="hidden" value="" id="typeID" name="typeID">
<input type="hidden" value="" id="workItemPanelID" name="workItemPanelID">
<input type="hidden" value="" id="parentPanelID" name="parentPanelID">
<input type="hidden" value="" id="oldStatusID" name="oldStatusID">
<input type="hidden" value="" id="oldPosition" name="oldPosition">
<div id = "content">
</div>
<div id="SideShowItem" style="display:none;">showKanbanBoard</div>