Ext.ns('ezScrum');

function renderStoryTitle(record) {
//	var value = makeIssueDetailUrl2(record.get('Link'), record.id);
	var value = record.id;
	return String.format('[Story]  #{0}', value);
}

// Story最上層的Title，用一個Table包起來，裡面放Imp. Est. 與動作的Icon
function renderStoryHeader(record, edit, history, upload) {
	return String.format(
			'<table class="StoryCard_Header">' + 
				'<td><h2>{0}</h2></td>'	+
				'<td align="right">{1}{2}{3}</td>' + 
			'</table>',
			renderStoryTitle(record), edit, history, upload);
}

// 顯示Story的Attach File
function renderStoryAttachFile(record) {
	// <p><b>Attach Files:</b><br /><a href="{0}" target="_blank">{1}</a>
	
	var storyId = record.get('Id');
	var fileList = record.get('AttachFileList');

	if (fileList.length == 0)
		return "";

	var result = "<p><b>[Attach Files]</b></p>";
	for (var i = 0; i < fileList.length; i++) {
		result += String.format('<p>'+ (i+1) +'. <a href="{0}" target="_blank">{1}</a>&nbsp;&nbsp;'
		 					  + '<a href="#" onClick="deleteAttachFile({2}, {3}); false;"><image src="./images/drop2.png"></a>&nbsp;&nbsp;{4}</p>',
				fileList[i].FilePath, fileList[i].FileName, fileList[i].FileId, storyId, fileList[i].UploadDate);
	}
	return result;
}

// 顯示Estimate與Importance的方框
function renderValue(value) {
	return String.format('<td align="center" class ="StoryCard_Value">{0}</td>', value);
}

// 顯示Desciption與Importance
function renderDescription(description, value, valueName) {
	return String.format(
			'<tr><td class="StroyCard_Description"><h1>{0}</h1></td>'
					+ '<td class="StoryCard_Value">{1} Point</td></tr>',
			description, value);
}

// 顯示Note與Estimate
function renderNotes(notes, value) {
	return String.format(
					'<tr><td><b>NOTES:</b></td><td align="center"><b>Estimate</b></td></tr>'
							+ '<tr><td class="StroyCard_Notes">{0}</td>{1}</tr>',
					value);

}
/*******************************************************************************
 * 將讀入的Story資料建立成TaskBoard StroyCard的格式
 ******************************************************************************/
function createStoryContent(story) {
	// 幾個動作Icon的超連結
	var editIcon = '<a href="javascript:editStory('	+ story.id + ')" title="Edit the Story"><img src="images/edit.png" border="0"></a>';
	var historyIcon = '<a href="javascript:showHistory(' + story.id + ', "Story")" title="Show History"><img src="images/history.png" class="LinkBorder"></a>';
	var uploadIcon = '<a href="javascript:attachFile(' + story.id + ')" title="Upload File"><img src="images/upload.png" class="LinkBorder"></a>';
	
	return '<table class="StoryCard_Table">'
				+ '<tr><td colspan=2>'
				// ============= Story Title================
				+ renderStoryHeader(story, editIcon, historyIcon, uploadIcon)
				+ '</td></tr>'
				// ============ Story的描述內容 ==============
				+ renderDescription(story.get('Name'), story.get('Estimate'))
				// ============ 附加檔案 =====================
				+ '<tr><td colspan="2">'
				+ renderStoryAttachFile(story) + '</td></tr>'
			+ '</table>'
}

function createStoryCard(story) {

	var storyCard = new Ext.Panel({
		id			: 'Story:' + story.id,	// for front-end
		stroyId		: story.id,  // for back-end
		data		: story,
		bodyBorder	: false,
		border		: false,
		items		: [{
			bodyBorder	: false,
			border		: false,
			html		: createStoryContent(story)
		}],
		// 在 taskboard 上編輯 story 後, update card 內容
		updateData_Edit : function(name, point) {
			var data = this.data;
			data.set('Name', name);
			data.set('Estimate', point);
        	this.items.get(0).update(createStoryContent(data));
		},
		updateData_AttachFile : function(attachFileList) {
			var data = this.data;
			data.set('AttachFileList', attachFileList);
        	this.items.get(0).update(createStoryContent(data));
		},
		updateData: function(recordData) { // 目前只需更新 name
			var data = this.data;
			data.set('Name', recordData['Name']);
        	this.items.get(0).update(createStoryContent(data));
		}
	});
	
	storyCard.draggable = {
		readObject : storyCard,
		ddGroup : story.id,
		storyId	: story.id,
		issueType:'Story',
		status : story.get('Status'),
		afterDragDrop : function(target, e, targetID) {
			// 如果狀態沒有轉換，就不作動作
			if (this.status == target.status) {
				return
			} else if (target.status == 'closed') {// 如果Story是被移動到Closed
				// 取得 Story 底下所有的 Task
				var tasks = story.get('Tasks');
				for ( var k = tasks.length-1 ; k >= 0; k--) {
					var taskStatus = Ext.getCmp( 'Task:' + tasks[k].Id ).draggable.status; 
					// 若有任一 Task 不為 done，則 Story 無法移至 done
					if( taskStatus != 'closed' ){
						Ext.MessageBox.alert('warning!', 'Please check all the tasks of Story #' + story.id + ' are done.');
						return
					}
				}
				// 顯示確認Done Story的視窗，很可怕的是這個function在TaskBoard.jsp上面 
                Ext.getCmp('TaskBoard_Card_Panel').checkIsCurrentSprint(showDoneIssue, this.storyId, this);
			} else if (target.status == 'new') {
				// 顯示Reopen的視窗
				Ext.getCmp('TaskBoard_Card_Panel').checkIsCurrentSprint(showReOpenIssue, this.storyId, this);
			}
			this.target = target;
		},
		moveToTarget : function() {
			// 將 story 插入第一個位置(在 task 之上)
			this.target.insert(0, this.readObject);
			this.status = this.target.status;
			storyCard.draggable.status = this.target.status;// 抓到的值竟然與 this.status不同, 所以直接塞 
		},
		updateData : function(data) {			// 目前只需更新 name
			this.readObject.updateData(data);
		}
	};
	
	return storyCard;
}