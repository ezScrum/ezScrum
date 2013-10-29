Ext.ns('ezScrum');
function renderStoryTitle(record) {
	return String.format('  [Story] <a href="{1}">{0}</a>', record.id, record
					.get('Link'));
}

// Story最上層的Title，用一個Table包起來，裡面放Imp. Est. 與動作的Icon
function renderStoryHeader(record, edit, history, upload) {
	return String.format(
			'<table class="StoryCard_Header"><td><h2>{0}</h2></td>'
					+ '<td align="right">{3}{4}{5}</td>' + '</table>',
			renderStoryTitle(record), record.get('Importance'), record
					.get('Estimate'), edit, history, upload);
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
	return String.format(
			'<td align="center" class ="StoryCard_Value">{0}</td>', value);
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
	return String
			.format(
					'<tr><td><b>NOTES:</b></td><td align="center"><b>Estimate</b></td></tr>'
							+ '<tr><td class="StroyCard_Notes">{0}</td>{1}</tr>',
					value);

}
/*******************************************************************************
 * 將讀入的Story資料建立成TaskBoard StroyCard的格式
 ******************************************************************************/
function createStoryCard(story) {

	// 幾個動作Icon的超連結
	var editIcon = '<a href="javascript:editStory('
			+ story.id
			+ ')" title="Edit the Story"><img src="images/edit.png" border="0"></a>';
	var historyIcon = '<a href="javascript:showHistory('
			+ story.id
			+ ')" title="Show History"><img src="images/history.png" class="LinkBorder"></a>';
	var uploadIcon = '<a href="javascript:attachFile('
			+ story.id
			+ ')" title="Upload File"><img src="images/upload.png"  class="LinkBorder"></a>';

	var storyCard = new Ext.Panel({
				id : story.id,
				bodyBorder : false,
				border : false,
				items : [{
					bodyBorder : false,
					border : false,
					html : '<table class="StoryCard_Table">'
							+ '<tr><td colspan=2>'
							// ============= Story Title================
							+ renderStoryHeader(story, editIcon, historyIcon,
									uploadIcon)
							+ '</td></tr>'
							// ============ Story的描述內容 ==============
							+ renderDescription(story.get('Name'), story
											.get('Estimate'))
							// ============ 附加檔案 =====================
							+ '<tr><td colspan="2">'
							+ renderStoryAttachFile(story) + '</td></tr>'
							+ '</table>'
				}]
			}

	);
	storyCard.draggable = {
		readObject : storyCard,
		ddGroup : story.id,
		status : story.get('Status'),
		afterDragDrop : function(target, e, targetID) {
			// 如果狀態沒有轉換，就不作動作
			if (this.status == target.status) {
				return
			}
			// 如果Story是被移動到Closed
			else if (target.status == 'closed') {
				// 顯示確認Done Story的視窗，很可怕的是這個function在TaskBoard.jsp上面 
                // @TODO想辦法把這個可怕的Function拿走 
                checkIsCurrentSprint(showDoneIssue,this.id,this);
			} else if (target.status == 'new') {
				// 顯示Reopen的視窗
				checkIsCurrentSprint(showReOpenIssue,this.id, this);
			}
			this.target = target;
		},
		moveToTarget : function() {
			this.target.add(this.readObject);
			this.status = this.target.status;
		},
		updateData : function(data) {
			// Story現在還不知道要更新啥
		}
	}
	return storyCard;
}