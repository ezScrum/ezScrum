/*******************************************************************************
 * Task Card
 ******************************************************************************/
function renderTaskTitle(record) {
//	var value = makeIssueDetailUrl2(record.Link, record.Id);
	var value = record.Id;
	return String.format('[Task]  #{0}', value);
}

// Story最上層的Title，用一個Table包起來，裡面放Imp. Est. 與動作的Icon
function renderTaskHeader(record, edit, history, upload) {
	return String.format(
			'<table class="TaskCard_Header">' +
				'<td><td><h2>{0}</h2></td>' +
				'<td align="right">{1}{2}{3}</td>' +
			'</table>',
			renderTaskTitle(record), edit, history, upload);
}

function renderTaskContext(record) {
	return String.format(
				'<b><p>{0}</p></b>' + '<b><p>Handler:</b>{1}</p>' + '<b><p>Remain Hours:</b>{2}</p>',
				record.Name, record.Handler, record.RemainHours
			);
}
// 顯示Story的Attach File
function renderAttachFile(record) {
	var taskId = record.Id;
	var fileList = record.AttachFileList;

	if (fileList.length == 0)
		return "";

	var result = "<p><b>[Attach Files]</b></p>";
	for ( var i = 0; i < fileList.length; i++) {
		result += String.format('<p>'+ (i+1) +'. <a href="{0}" target="_blank">{1}</a>&nbsp;&nbsp;'
							+ '<a href="#" onClick="deleteTaskAttachFile({2}, {3}); false;"><image src="./images/drop2.png"></a>&nbsp;&nbsp;{4}</p>',
				fileList[i].FilePath, fileList[i].FileName, fileList[i].FileId, taskId, fileList[i].UploadDate);
	}
	return result;

}

// 顯示Name與Estimate
function taskRenderDescription(description, value, valueName) {
	return String.format(
			'<tr><td class="TaskCard_Description"><h1>{0}</h1></td>'
			+'<td class="TaskCard_Value" >{1} hr</td>'
			+'</tr>',description,value);
}
// 顯示Remain Hours與 Handler
function taskRenderRHH(handler, partners) {
	if(handler.length > 0) {
	    var handler = String.format(
	            '<tr><td>By\t<span class="TaskCard_Handler">{0}</span>', handler);
	    if(partners.length > 0)
	    	handler += ' + ' + partners;
	    handler += '</td></tr>';
	    return handler;
	}
	else
		return "";
}

function createTaskContent(task)
{
    var editIcon = '<a href="javascript:editTask(' + task.Id + ')" title="Edit the Task"><img src="images/edit.png" border="0"></a>'
    var historyIcon = '<a href="javascript:showHistory(' + task.Id + ', \'Task\')" title="Show History"><img src="images/history.png" class="LinkBorder"></a>'
    var uploadIcon = '<a href="javascript:taskAttachFile(' + task.Id + ')" title="Upload File"><img src="images/upload.png" class="LinkBorder"></a>'
    
    return '<table class="TaskCard_Table">'
                +'<tr><td colspan=2>'
                + renderTaskHeader(task, editIcon, historyIcon, uploadIcon)
                + '</td></tr>'
                // ============ Story的描述內容 ==============
                +taskRenderDescription(task.Name, task.RemainHours, 'RemainHours')
                // ============ Handler與Remain Hours
                +taskRenderRHH(task.HandlerUserName, task.Partners)
                // ============ 附加檔案 ==============
                +'<tr><td colspan="2">' 
                + renderAttachFile(task)
                + '</td></tr>' 
          + '</table>'   
}

function createTaskCard(task, storyID) {

	var taskCard = new Ext.Panel( {
		id				: 'Task:' + task.Id,	// for front-end
		taskId			: task.Id, // for back-end
		data			: task,
		borderBorder	: false,
		border			: false,
        setHandlerPartners:function(handler, partners) {//set handler and partners
            var data = this.data;
            data.Handler = handler;
            data.HandlerUserName = handler;
            data.Partners = partners;
            this.items.get(0).update(createTaskContent(data));
        },
        setRemainHours:function(remainHours) {
        	var data = this.data;
        	data.RemainHours = remainHours;
        	this.items.get(0).update(createTaskContent(data));
        },
        // 在 taskboard 上編輯 task 後, update card 內容
		updateData_Edit : function(name, handler, partners, remainHours) {
			var data = this.data;
			data.Name = name;
			data.Handler = handler;
			data.HandlerUserName = handler;
			data.Partners = partners;
			data.RemainHours = remainHours;
        	this.items.get(0).update(createTaskContent(data));
		},
		updateData_AttachFile : function(attachFileList) {
			var data = this.data;
			data.AttachFileList = attachFileList;
            console.log(data);
        	this.items.get(0).update(createTaskContent(data));
		},
		updateName : function(name) {
			var data = this.data;
			data.Name = name;
        	this.items.get(0).update(createTaskContent(data));
		},
		// update Name, Handler, Partners
		updateData : function(recordData) {
			var data = this.data;
			data.Name = recordData['Name'];
			data.Handler = recordData['Handler'];
            data.HandlerUserName = recordData['Handler'];
			data.Partners = recordData['Partners'];
        	this.items.get(0).update(createTaskContent(data));
		},
		items : [ {
			bodyBorder : false,
			border : false,
			html : createTaskContent(task)
		} ]
	});
	
    // 設定 TaskCard 的拖拉物件
    taskCard.draggable = {
        realObject		: taskCard,
        taskId			: task.Id,
        issueType		: 'Task',
        status			: task.Status,
        ddGroup			: storyID,
        parentId		: storyID,
        afterDragDrop	: function(target, e, targetID) {
            var status = this.status;
            // 如果Status都一樣的話，不做任何動作
            if(target.status == status) {
                return
            }
            if(target.status == 'assigned') {
                // 如果taskCard status為closed表示為reopen
                if(status == 'closed'){
                	// 取得 Story card 的狀態
                	var storyStatus = Ext.getCmp('Story:' + this.parentId).draggable.status;
                	if( storyStatus == 'new' ){
                		Ext.getCmp('TaskBoard_Card_Panel').checkIsCurrentSprint(showReOpenIssue,this.taskId,this);
                	}else if( storyStatus == 'closed' ){
                		Ext.MessageBox.alert('warning!', 'Please re-open Story #'+ this.parentId + ' first.');
                	}
                }else{ // check out
                	Ext.getCmp('TaskBoard_Card_Panel').checkIsCurrentSprint(showCheckOutIssue,this.taskId,this);
                }
            }
            // 只有從assigned的狀態才能轉移到closed
            else if(target.status == 'closed' && status =='assigned') {
            	Ext.getCmp('TaskBoard_Card_Panel').checkIsCurrentSprint(showDoneIssue,this.taskId,this);
            }
            // 如果是移動到new的狀態，那麼就是re check out
            else if(target.status == 'new' && status =='assigned') {
            	Ext.getCmp('TaskBoard_Card_Panel').checkIsCurrentSprint(showReCheckOutTask,this.taskId,this);
            }

        	this.target = target;
        },
        moveToTarget : function() {
        	  this.status = this.target.status;
        	  taskCard.draggable.status = this.target.status; // 抓到的值竟然與 this.status不同, 所以直接塞
        	  
              if(this.status == 'new') {
            	  // 如果移動到New狀態的話，就清空Handler, Partners
            	  this.realObject.setHandlerPartners('','');
              }
              else if(this.status =='closed') {
            	  // 如果移動到Done的話，更新Remain Hours為0
            	  this.realObject.setRemainHours(0);
              }
            this.target.add(taskCard);
        },
        updateName : function(name) {
            this.realObject.updateName(name);
        },
        updateData : function(data) {
            this.realObject.updateData(data);
        }
    };
    
	return taskCard;
}

