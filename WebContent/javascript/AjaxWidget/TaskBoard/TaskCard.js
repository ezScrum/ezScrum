/*******************************************************************************
 * Task Card
 ******************************************************************************/
function renderTaskTitle(record) {
	return String.format('  [Task] <a href="{1}">{0}</a>', record.Id,
			record.Link);
}

// Story最上層的Title，用一個Table包起來，裡面放Imp. Est. 與動作的Icon
function renderTaskHeader(record, edit, history, upload) {
	return String.format(
			'<table class="TaskCard_Header"><td><td><h2>{0}</h2></td>'
					+ '<td align="right">{3}{4}{5}</td>' + '</table>',
			renderTaskTitle(record), record.Estimate, record.RemainHours, edit,
			history, upload);
}

function renderTaskContext(record) {
	return String
			.format(
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
							+ '<a href="#" onClick="deleteAttachFile({2}, {3}); false;"><image src="./images/drop2.png"></a>&nbsp;&nbsp;{4}</p>',
				fileList[i].FilePath, fileList[i].FileName, fileList[i].FileId, taskId, fileList[i].UploadDate);
	}
	return result;

}

// 顯示Name與Estimate
function taskRenderDescription(description,value , valueName) {
	return String.format(
			'<tr><td class="TaskCard_Description"><h1>{0}</h1></td>'
			+'<td class="TaskCard_Value" >{1} hr</td>'
			+'</tr>',description,value);
}
// 顯示Remain Hours與 Handler
function taskRenderRHH(handler,partners) {
	if(handler.length > 0)
	{
	    var handler = String.format(
	            '<tr><td>By\t<span class="TaskCard_Handler">{0}</span>',handler);
	    if(partners.length > 0)
	    	handler+=' + '+partners;
	    handler+='</td></tr>';
	    return handler;
	}
	else
		return "";
}

function createTaskContent(task)
{
    var editIcon = '<a href="javascript:editTask(' + task.Id + ')" title="Edit the Task"><img src="images/edit.png" border="0"></a>'
    var historyIcon = '<a href="javascript:showHistory(' + task.Id + ')" title="Show History"><img src="images/history.png" class="LinkBorder"></a>'
    var uploadIcon = '<a href="javascript:attachFile(' + task.Id + ')" title="Upload File"><img src="images/upload.png" class="LinkBorder"></a>'
    return '<table class="TaskCard_Table">'
                    +'<tr><td colspan=2>'
                    + renderTaskHeader(task, editIcon, historyIcon, uploadIcon)
                    + '</td></tr>'
                    // ============ Story的描述內容 ==============
                    +taskRenderDescription(task.Name,task.RemainHours,'RemainHours')
                    // ============ Handler與Remain Hours
                    +taskRenderRHH(task.Handler,task.Partners)
                    // ============ 附加檔案 ==============
                    +'<tr><td colspan="2">' 
                    + renderAttachFile(task)
                    + '</td></tr>' 
          + '</table>'   
}

function createTaskCard(task,storyID) {

	var taskCard = new Ext.Panel( {
		data:task,
		borderBorder : false,
		border : false,
        setHandler:function(handler)
        {
            var data = this.data;
            data.Handler = handler;
            this.items.get(0).update(createTaskContent(data));
        },
        setRemainHours:function(remainHours)
        {
        	var data = this.data;
        	data.RemainHours = remainHours;
        	 this.items.get(0).update(createTaskContent(data));
        },
		items : [ {
			bodyBorder : false,
			border : false,
			html : createTaskContent(task)
		} ]
	});
	
    // 設定TaskCard的拖拉物件
    taskCard.draggable = {
            realObject:taskCard,
            taskId : task.Id,
            status : task.Status,
            ddGroup:storyID,
            afterDragDrop : function(target, e, targetID) {
                var status = this.status;
                // 如果Status都一樣的話，不做任何動作
                if(target.status == status)
                {
                    return
                }
                if(target.status == 'assigned')
                {   
                    // 如果taskCard status為closed表示為reopen
                    if(status == 'closed')
                        checkIsCurrentSprint(showReOpenIssue,this.taskId,this);
                    else
                        checkIsCurrentSprint(showCheckOutIssue,this.taskId,this);
                }
                // 只有從assigned的狀態才能轉移到closed
                else if(target.status == 'closed' && status =='assigned')
                {
                   checkIsCurrentSprint(showDoneIssue,this.taskId,this);
                }
                // 如果是移動到new的狀態，那麼就是reopen
                else if(target.status == 'new')
                {
                    checkIsCurrentSprint(showReCheckOutTask,this.taskId,this);
                }
                this.target = target;
            },
            moveToTarget : function() {
            	  this.status = this.target.status;
                  if(this.status == 'new')
                  	// 如果移動到New狀態的話，就清空Handler
                  	 this.realObject.setHandler('');
                  else if(this.status =='closed')
                  	// 如果移動到Done的話，更新Remain Hours為0
                  	this.realObject.setRemainHours(0);
                this.target.add(taskCard);
              
            },
            updateData:function(data)
            {
                // 現在也只先更新handler，以後應該要有更完整的Task資料更新
                var handler = data['Handler'];
                this.realObject.setHandler(handler);
            }
        };
	return taskCard;
}