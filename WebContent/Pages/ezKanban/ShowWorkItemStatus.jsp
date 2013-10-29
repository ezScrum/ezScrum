<%@ page contentType="text/html; charset=utf-8"%>
<%@ taglib uri="http://struts.apache.org/tags-bean" prefix="bean"%>
<%@ taglib uri="http://struts.apache.org/tags-html" prefix="html"%>
<%@ taglib uri="http://struts.apache.org/tags-logic" prefix="logic"%>
<%@ taglib uri="http://struts.apache.org/tags-tiles" prefix="tiles"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

	<script type="text/javascript" src="javascript/KanbanWidget/Common.js"></script>
	<script type="text/javascript" src="javascript/KanbanWidget/CreateStatusWidget.js"></script>
	<script type="text/javascript" src="javascript/KanbanWidget/EditStatusWidget.js"></script>
	<script type="text/javascript" src="javascript/KanbanWidget/DeleteStatusWidget.js"></script>
	<script type="text/javascript" src="javascript/KanbanWidget/SaveStatusWidget.js"></script>
	<script type="text/javascript" src="javascript/KanbanWidget/ShowMessageWidget.js"></script>
	
	<script type="text/javascript" src="javascript/AjaxWidget/Message.js"></script>
	
	<link rel="stylesheet" type="text/css" href="css/Message.css"/>

<script type="text/javascript">
	Ext.ns('ezScrum');
	
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
	       return Ext.getCmp('StatusesWidget');
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
	        //return Ext.dd.DropZone.prototype.dropAllowed;
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
                // 儲存 Description
                var description = dragData.field.el.dom.getElementsByTagName('textarea')[0].innerHTML;
                var flag = false;

                // 儲存數量
                var count = target.items.length;
                // 判斷拖曳位置去頭尾(Backlog & Live)
                for (i = 1; i < count - 1; i = i + 1)
                {
                	// 拖曳至同位置則不進入, 位置不變
                	if (target.get(i).getId() != dragData.field.getId())
                	{
	                	// 判斷拖曳項目是否拖曳至此項目位置
	                	if (target.get(i).getEl().getLeft() < e.getXY()[0] &&
	                		target.get(i).getEl().getRight() > e.getXY()[0] &&
	                		target.get(i).getEl().getTop() < e.getXY()[1] &&
	                		target.get(i).getEl().getBottom() > e.getXY()[1])
	               		{
	               			target.remove(dragData.field, true);
	                		target.insert(i, clonePanel);
	                		flag = true;
	                		break;
	               		}
           			}
                }
                if (flag)
                {
	                // 設定Id + Name + Description
					clonePanel.id = Id;
					clonePanel.setTitle(name);
					// 畫面更新
	                target.doLayout();
	                clonePanel.update('<textarea style="height:95%; width:100%">' + description + '</textarea>');
                }
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
	    	// 找出被 Drag 的 Panel(不包含Backlog & Live)
	    	var dragPanel;
	    	for (i = 1; i < statuses.items.length - 1; i = i + 1)
            {
            	if (statuses.get(i).getEl().getLeft() < e.getXY()[0] &&
            		statuses.get(i).getEl().getRight() > e.getXY()[0] &&
            		statuses.get(i).getEl().getTop() < e.getXY()[1] &&
               		statuses.get(i).getEl().getBottom() > e.getXY()[1]) 
            	{
            		dragPanel = statuses.get(i);
            		handlePanelClick(dragPanel, true);
            		break;
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

				//  create a drag item to display
	            Ext.fly(dragPanel.el.dom).setWidth(dragPanel.getEl().getWidth());
	            return {
	                field: dragPanel,
	                ddel: dragPanel.el.dom
	            };
	        }
	    },
	
		//  The coordinates to slide the drag proxy back to on failed drop.
	    getRepairXY: function() {
	        return this.dragData.field.getEl().getXY();
	    }
	});
	
	// 處理Status Panel選取
	function handlePanelClick(panel, isClick) {
		// 紀錄被選取 Status 的 ID
		document.getElementById("panelID").value = panel.id;

		// 取消選取其他 Panel
		var array = Ext.getCmp('StatusesWidget').items;
		for (i = 1; i < array.getCount() - 1; i = i + 1)
		{
			// 改回原本的CSS
		    var other = array.itemAt(i);
		    other.getEl().first().applyStyles('border-color: #99bbe8');
		    other.getEl().last().first().applyStyles('border-color: #99bbe8');
		}
		
		// 選取 Panel
		var header = panel.getEl().first();
		var bodyContent = panel.getEl().last().first();
		// 改變CSS
		header.applyStyles('border-color: red;');
		bodyContent.applyStyles('border-color: red;');
    }
    
    function cleanSelectedStatus(){
    	document.getElementById("panelID").value = '';
    }
    
    // 建立 Status Panel
    function createStatusPanel(id, name, description, limit) {
		// 判斷字串
		if (description == null)
			description = '';
		if (limit == null || limit == -1 || limit == '')
			limit = '';
		else
			limit = '(' + limit + ')';
		
		// 將目前 Kanban 中的 Status 建立成 Panel
		var newPanel = new Ext.Panel({
	        id : id,
	        header : true,
	        layout : 'fit',
			height : 180,
			width : 200,
			html : '<textarea style="height:95%; width:100%">Description:\n' + description + '</textarea>'
	    });
	    newPanel.setTitle(name + limit);
	    
	    return newPanel;
    }

	/* Grid View */
	Ext.onReady(function() {
		Ext.QuickTips.init();

		/* 取得 WorkItem Status 頁面所需資料 */
		function LoadData(option)
		{
			/* 取得 WorkItem Status 資料 */
			Ext.Ajax.request({
				url:'showWorkItemStatusAction.do',
				success:function(response){
					// 取得 IssueTypeID (Type:Status)
					issueTypeStore.loadData(Ext.decode(response.responseText));
					// 將 IssueTypeID 存入 Input
	    			document.getElementById("typeID").value = issueTypeStore.getAt(0).get('Id');
					
					// 初始化 Status Panel
					statusStore.loadData(Ext.decode(response.responseText));
					for (i = 0; i < statusStore.getCount(); i = i + 1)
					{
					    var newPanel = createStatusPanel(statusStore.getAt(i).get('Id').toString(), statusStore.getAt(i).get('Name'),
												statusStore.getAt(i).get('Description'), statusStore.getAt(i).get('Limit'));
						// Panel 加入至 StatusesWidget
						Ext.getCmp('StatusesWidget').insert(i + 1, newPanel);
						/*
						// 畫面更新
						Ext.getCmp('StatusesWidget').doLayout();
						// Status Panel 項目顏色改變, 改變CSS
						var header = newPanel.getEl().first();
						header.applyStyles(panelColors[i + 1]);
						*/
					}
					// 畫面更新
					Ext.getCmp('StatusesWidget').doLayout();
				}
			});
			/* 取得 WorkItem Status 權限資料 */
			Ext.Ajax.request({
				url:'AjaxGetMASPermission.do',
				success:function(response){
					var permissionRs = MASPermissionReader.readRecords(response.responseXML);
			 		var permissionCount = permissionRs.totalRecords;
			 		for(var i = 0; i < permissionCount; i++)
			 		{
			 			var permissionRecord = permissionRs.records[i];
						statusesWidget.loadPermission(permissionRecord.data['AddStatus'], permissionRecord.data['EditStatus'],permissionRecord.data['DeleteStatus'],permissionRecord.data['SaveStatus']);     
			 		}
				}
			});
		}
        
        // Create Status Widget
		var createStatusWidget = new ezScrum.AddNewStatusWidget({
			listeners:{
				CreateSuccess:function(win, form, response, record){
				    var newPanel = createStatusPanel(record.data['Id'].toString(), record.data['Name'],
												record.data['Description'], record.data['Limit']);
					// Status 加入 Widget
			 		Ext.getCmp('StatusesWidget').insert(Ext.getCmp('StatusesWidget').items.length - 1, newPanel);
			 		Ext.getCmp('StatusesWidget').doLayout();
			 		this.hide();
			 		Ext.example.msg('Create Status', 'Create Status Success.');
			 		
				},
				CreateFailure:function(win, form, response, issueId){
					Ext.example.msg('Create Status', 'Create Status Failure.');
				}
			}
		});
		
		// Edit Status Widget
		var editStatusWidget = new ezScrum.EditStatusWidget({
			listeners:{
				LoadSuccess:function(win, form, response, record){
					// Load Status Success
				},
				LoadFailure:function(win, form, response, issueId){
					Ext.example.msg('Load Status', 'Load Status Failure.');
				},
				EditSuccess:function(win, form, response, record){
					// 建立新 Status Panel
					var newPanel = createStatusPanel(record.data['Id'].toString(), record.data['Name'],
												record.data['Description'], record.data['Limit']);
					var statusWidget = Ext.getCmp('StatusesWidget');
					// 找出舊 Status Panel 的位置
					var index = 0;
					for (i = 0; i < statusWidget.items.length; i = i + 1)
					{					    
					    if (statusWidget.get(i).getId() == record.data['Id']){
					    	index = i;
					    	// 移除舊 Status Panel
					    	statusWidget.remove(statusWidget.get(i), true);
					    	break;
					    }
					}
					// Status Panel 加入至 StatusesWidget
					statusWidget.insert(index, newPanel);
					statusWidget.doLayout();
					this.hide();
					Ext.example.msg('Edit Status', 'Edit Status Success.');
					
				},
				EditFailure:function(win, form, response, issueId){
					Ext.example.msg('Edit Status', 'Edit Status Failure.');
				}
			}
		});
		
		// Delete Status Widget
		var deleteStatusWidget = new ezScrum.DeleteStatusWidget({
			listeners:{
				DeleteSuccess:function(win, response, issueId){
					// 刪除 Status Panel 需要 Destory
					Ext.getCmp('StatusesWidget').remove(issueId, true);
					Ext.getCmp('StatusesWidget').doLayout();
					this.hide();
					Ext.example.msg('Delete Status', 'Delete Status Success.');
				},
				DeleteFailure:function(win, response, issueId){
					Ext.example.msg('Delete Status', 'Delete Status Failure.');
				}
			}
		});
		
		// Save Status Order Widget
		var saveStatusWidget = new ezScrum.SaveStatusWidget({
			listeners:{
				SaveSuccess:function(win, response){
					this.hide();
					Ext.example.msg('Save Status', 'Save Status Success.');
				},
				SaveFailure:function(win, response){
					Ext.example.msg('Save Status', 'Save Status Failure.');
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

		// Kanban 固定有的 Status
		var statusBacklog = new Ext.Panel({
			id:'Backlog',
			title : 'Backlog',
			height : 180,
			width : 200,
			html : '<textarea style="height:95%; width:100%">Description:\n系統預設狀態</textarea>'
		});
		var statusLive = new Ext.Panel({
			id:'Live',
			title : 'Live',		
			height : 180,
			width : 200,
			html : '<textarea style="height:95%; width:100%">Description:\n系統預設完成狀態</textarea>'
		});
		
		// 包含所有 Status 的 Panel
		var statusesWidget = new Ext.Panel({
			id:'StatusesWidget',		
			layout: 'column',
			layoutConfig: {
			    padding: '10',
			    align: 'left'
			},
			padding: '10',
			title : 'WorkItem Status',
			region : 'center',
			autoDestroy : false,
			plugins: [new Ext.ux.PanelFieldDragZone(), new Ext.ux.PanelFieldDropZone],
			addStatusPermission:false,
			editStatusPermission:false,
			deleteStatusPermission:false,
			saveStatusPermission:false,
			// Add Status action
			addStatus:function()
			{
				// 傳入 IssueTypeID
				createStatusWidget.showWidget(document.getElementById("typeID").value);
			},
			// Edit Status action
			editStatus:function()
			{
				// 判斷是否存在
				var widget = Ext.getCmp(document.getElementById("panelID").value);
				if(widget != null)
				{
					editStatusWidget.loadEditStatus(document.getElementById("panelID").value, document.getElementById("typeID").value);
				}
				else
					messageWidget.showMessage();
			},
			// Delete Status action
			deleteStatus:function()
			{
				// 判斷是否存在
				var widget = Ext.getCmp(document.getElementById("panelID").value);
				if(widget != null)
				{
					deleteStatusWidget.deleteStatus(document.getElementById("panelID").value);
				}
				else
					messageWidget.showMessage();
			},
			// Save Status action
			saveStatusOrder:function()
			{
				var issueIds = '';
				// 儲存數量
                var count = statusesWidget.items.length;
                // 取出 Status 順序, 去頭尾(Backlog & Live)
                for (i = 1; i < count - 1; i = i + 1)
                {
                	issueIds = issueIds + statusesWidget.get(i).getId() + ',';                	
                }
                // 儲存 Status 順序
				saveStatusWidget.saveStatus(issueIds);
			},
			tbar: [
				{id:'addStatusBtn', disabled:false, text:'Add Status', icon:'images/add3.png', handler: function(){statusesWidget.addStatus();}},
				{id:'editStatusBtn', disabled:false, text:'Edit Status', icon:'images/edit.png', handler:function(){statusesWidget.editStatus();}},
				{id:'deleteStatusBtn', disabled:false, text:'Delete Status', icon:'images/delete.png', handler: function(){statusesWidget.deleteStatus();}},
				{id:'saveStatusBtn', disabled:false, text:'Save Status', icon:'images/check.png', handler: function(){statusesWidget.saveStatusOrder();}}
			],
			items:[ statusBacklog,statusLive
			],
			loadPermission:function(addStatusPermission, editStatusPermission, deleteStatusPermission, saveStatusPermission)
			{
				this.addStatusPermission = addStatusPermission == "true";
				this.editStatusPermission = editStatusPermission == "true";
				this.deleteStatusPermission = deleteStatusPermission == "true";
				this.saveStatusPermission = saveStatusPermission == "true";
				
				this.getTopToolbar().get('addStatusBtn').setDisabled(!this.addStatusPermission);
				this.getTopToolbar().get('editStatusBtn').setDisabled(!this.editStatusPermission);
				this.getTopToolbar().get('deleteStatusBtn').setDisabled(!this.deleteStatusPermission);
				this.getTopToolbar().get('saveStatusBtn').setDisabled(!this.saveStatusPermission);
			}
		});
		
		// 最外層
		var contentWidget = new Ext.Panel({
			height: 600,
			layout : 'border',
			renderTo: Ext.get("content"),
			items : [statusesWidget]			
		});
		
		LoadData();
	});
	
</script>

<input type="hidden" value="" id="typeID" name="typeID">
<input type="hidden" value="" id="panelID" name="panelID">
<div id = "content">
</div>
<div id="SideShowItem" style="display:none;">showWorkItemStatus</div>