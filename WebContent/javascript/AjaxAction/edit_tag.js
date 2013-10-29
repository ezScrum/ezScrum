function AddNewTag(NewTagName)
{
	AjaxRequestAddNewTag("./AjaxAddNewTag.do", Complete, NewTagName);
}

function AjaxRequestAddNewTag(url, successFunc, newTagName) {	
		Ext.Ajax.request(
	{
		url : url,
		success : successFunc,
		params : {newTagName:newTagName}
	});
}

function DeleteTag(TagId)
{
	AjaxRequestDeleteTag("./AjaxDeleteTag.do", Complete, TagId);
}

function AjaxRequestDeleteTag(url, successFunc, tagId) {	
		Ext.Ajax.request(
	{
		url : url,
		success : successFunc,
		params : {tagId:tagId}
	});
}

function AddStoryTag(StoryId, TagId)
{
	AjaxRequestAddStoryTag("./AjaxAddStoryTag.do", Complete, StoryId, TagId);
}

function AjaxRequestAddStoryTag(url, successFunc, storyId, tagId) {	
		Ext.Ajax.request(
	{
		url : url,
		success : successFunc,
		params : {storyId:storyId, tagId:tagId}
	});
}

function RemoveStoryTag(StoryId)
{
	AjaxRequestRemoveStoryTag("./AjaxRemoveStoryTag.do", Complete, StoryId);
}

function AjaxRequestRemoveStoryTag(url, successFunc, storyId) {	
		Ext.Ajax.request(
	{
		url : url,
		success : successFunc,
		params : {storyId:storyId}
	});
}

function Complete()
{
	alert("Complete");
}