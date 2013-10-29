Ext.ns('ezScrum');
function renderStoryTitle(record) {
	return String.format('  [Story] {0}', record.id);
}

// Story最上層的Title，用一個Table包起來，裡面放Imp. Est. 與動作的Icon
function renderStoryHeader(record) {
	return String.format(
			'<table class="StoryCard_Header"><td><h2>{0}</h2></td></table>',
			renderStoryTitle(record));
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
		result += String.format('<p>'+ (i+1) +'.&nbsp;{0}&nbsp;&nbsp;{1}</p>',
				fileList[i].FileName, fileList[i].UploadDate);
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
	var storyCard = new Ext.Panel({
				id : story.id,
				bodyBorder : false,
				border : false,
				items : [{
					bodyBorder : false,
					border : false,
					html : '<table class="StoryCard_Table">'
							+ '<tr><td colspan="2">'
							// ============= Story Title================
							+ renderStoryHeader(story)
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

	return storyCard;
}