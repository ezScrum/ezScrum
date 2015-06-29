<%@ page language="java" contentType="text/html" pageEncoding="UTF-8"%>
<html  lang="en">
<head>
	<title>ezScrum, SSLab NTUT</title>
	<link rel="shortcut icon" href="images/scrum_16.png"/>
</head>

<!--json tools  -->
<script type="text/javascript" src="javascript/JSONTools/json2.js"></script>


<!-- extjs -->
<script type="text/javascript" src="javascript/ext-base.js"></script>
<script type="text/javascript" src="javascript/ext-all.js"></script>

<!--ezScrum team design tools  -->
<script type="text/javascript" src="javascript/ezScrumJSTool.js"></script>

<script type="text/javascript" src="javascript/ux/gridfilters/menu/RangeMenu.js"></script>
<script type="text/javascript" src="javascript/ux/gridfilters/menu/ListMenu.js"></script>
<script type="text/javascript" src="javascript/ux/gridfilters/GridFilters.js"></script>
<script type="text/javascript" src="javascript/ux/gridfilters/filter/Filter.js"></script>
<script type="text/javascript" src="javascript/ux/gridfilters/filter/StringFilter.js"></script>
<script type="text/javascript" src="javascript/ux/gridfilters/filter/DateFilter.js"></script>
<script type="text/javascript" src="javascript/ux/gridfilters/filter/ListFilter.js"></script>
<script type="text/javascript" src="javascript/ux/gridfilters/filter/NumericFilter.js"></script>
<script type="text/javascript" src="javascript/ux/gridfilters/filter/BooleanFilter.js"></script>
<script type="text/javascript" src="javascript/ux/gridfilters/filter/TagFilter.js"></script>


<script type="text/javascript" src="javascript/ux/BufferView.js"></script>
<script type="text/javascript" src="javascript/ux/RowExpander.js"></script>
<script type="text/javascript" src="javascript/ux/RowEditor.js"></script>
<script type="text/javascript" src="javascript/ux/PagingMemoryProxy.js"></script>
<script type="text/javascript" src="javascript/ux/MultiSingleSortingPagingMemoryProxy.js"></script>
<script type="text/javascript" src="javascript/ux/fileuploadfield/FileUploadField.js"></script>

<script type="text/javascript" src="javascript/ux/Reorderer.js"></script>
<script type="text/javascript" src="javascript/ux/ToolbarReorderer.js"></script>
<script type="text/javascript" src="javascript/ux/ToolbarDroppable.js"></script>

<script type="text/javascript" src="javascript/ux/treegrid/TreeGridSorter.js"></script>
<script type="text/javascript" src="javascript/ux/treegrid/TreeGridColumnResizer.js"></script>
<script type="text/javascript" src="javascript/ux/treegrid/TreeGridNodeUI.js"></script>
<script type="text/javascript" src="javascript/ux/treegrid/TreeGridLoader.js"></script>
<script type="text/javascript" src="javascript/ux/treegrid/TreeGridColumns.js"></script>
<script type="text/javascript" src="javascript/ux/treegrid/TreeGrid.js"></script>

<!-- extjs -->


<script type="text/javascript">
	// namespace setting
	Ext.ns('ezScrum');
	Ext.ns('ezScrum.review');
	Ext.ns('ezScrum.window');
</script>

<!-- ezScrum plugin import -->
<jsp:include page="ImportPluginList.jsp"/>

<!--
    ezScrum shared component, should be imported before all panel or window render.
    These componets are created for reuse it.
-->
<script type="text/javascript" src="javascript/ezScrumLayout/ezScrumLayoutSupport.js"></script> 
<script type="text/javascript" src="javascript/ezScrumDataModel/IssueGridPanelSupport.js"></script>
<script type="text/javascript" src="javascript/ezScrumSharedComponent/DataRecord.js"></script>
<script type="text/javascript" src="javascript/ezScrumSharedComponent/ModifyStoryWindow.js"></script>
<script type="text/javascript" src="javascript/ezScrumSharedComponent/ModifySprintWindow.js"></script>
<script type="text/javascript" src="javascript/ezScrumSharedComponent/IssueHistoryWindow.js"></script>
<script type="text/javascript" src="javascript/ezScrumSharedComponent/AddExistedStoryWindow.js"></script>
<script type="text/javascript" src="javascript/ezScrumSharedComponent/AttachFileWindow.js"></script>
<script type="text/javascript" src="javascript/ezScrumSharedComponent/MoveStoryWindow.js"></script>


<!-- other support -->
<script type="text/javascript" src="javascript/AjaxWidget/CommonFormVTypes.js"></script>
<script type="text/javascript" src="javascript/AjaxWidget/Common.js"></script>
<script type="text/javascript" src="javascript/CustomWidget/Common.js"></script>
<script type="text/javascript" src="javascript/CommonUtility.js"></script>
<script type="text/javascript" src="javascript/AjaxWidget/Message.js"></script>
<link rel="stylesheet" type="text/css" href="css/Message.css"/>
<script type="text/javascript" src="javascript/AjaxAction/edit_tag.js"></script>



<!-- DataModel -->
<script type="text/javascript" src="javascript/ezScrumDataModel/IssueGridPanelSupport.js"></script>

<script type="text/javascript" src="javascript/ezScrumDataModel/ProjectDescription.js"></script>
<script type="text/javascript" src="javascript/ezScrumDataModel/TaskBoardDescription.js"></script>
<script type="text/javascript" src="javascript/ezScrumDataModel/ProjectMembers.js"></script>
<script type="text/javascript" src="javascript/ezScrumDataModel/IssueTag.js"></script>
<script type="text/javascript" src="javascript/ezScrumDataModel/Story.js"></script>
<script type="text/javascript" src="javascript/ezScrumDataModel/Handler.js"></script>
<script type="text/javascript" src="javascript/ezScrumDataModel/SprintPlan.js"></script>
<script type="text/javascript" src="javascript/ezScrumDataModel/Retrospective.js"></script>
<script type="text/javascript" src="javascript/ezScrumDataModel/UnplannedItem.js"></script>
<script type="text/javascript" src="javascript/ezScrumDataModel/ITSConfigDescription.js"></script>
<script type="text/javascript" src="javascript/ezScrumDataModel/ProductBacklogDataModel.js"></script>
<script type="text/javascript" src="javascript/ezScrumDataModel/ReleasePlanDataModel.js"></script>
<script type="text/javascript" src="javascript/ezScrumDataModel/SprintBacklog.js"></script>


<!-- Widget -->
<script type="text/javascript" src="javascript/ezScrumWidget/DeleteStoryWidget.js"></script>
<script type="text/javascript" src="javascript/ezScrumWidget/HandlerComboWidget.js"></script>
<script type="text/javascript" src="javascript/ezScrumWidget/EditTaskWidget.js"></script>
<script type="text/javascript" src="javascript/ezScrumWidget/ManageTagWidget.js"></script>
<script type="text/javascript" src="javascript/ezScrumWidget/StatusComboWidget.js"></script>
<script type="text/javascript" src="javascript/ezScrumWidget/SprintPlanComboWidget.js"></script>
<script type="text/javascript" src="javascript/ezScrumWidget/SprintPlanComboWidgetAll.js"></script>
<script type="text/javascript" src="javascript/ezScrumWidget/DeleteSprintWidget.js"></script>
<script type="text/javascript" src="javascript/ezScrumWidget/MoveSprintWidget.js"></script>
<script type="text/javascript" src="javascript/ezScrumWidget/CreateRetrospectiveWidget.js"></script>
<script type="text/javascript" src="javascript/ezScrumWidget/EditRetrospectiveWidget.js"></script>
<script type="text/javascript" src="javascript/ezScrumWidget/DeleteRetrospectiveWidget.js"></script>
<script type="text/javascript" src="javascript/ezScrumWidget/CreateUnplannedItemWidget.js"></script>
<script type="text/javascript" src="javascript/ezScrumWidget/EditUnplannedItemWidget.js"></script>
<script type="text/javascript" src="javascript/ezScrumWidget/DeleteUnplannedItemWidget.js"></script>
<script type="text/javascript" src="javascript/ezScrumWidget/ImportStoriesWidget.js"></script>
<script type="text/javascript" src="javascript/ezScrumWidget/AddMemberWidget.js"></script>

<script type="text/javascript" src="javascript/ezScrumWidget/TaskBoard/CheckOutTaskWidget.js"></script>
<script type="text/javascript" src="javascript/ezScrumWidget/TaskBoard/ReCheckOutTaskWidget.js"></script>
<script type="text/javascript" src="javascript/ezScrumWidget/TaskBoard/DoneIssueWidget.js"></script>
<script type="text/javascript" src="javascript/ezScrumWidget/TaskBoard/ReOpenIssueWidget.js"></script>
<script type="text/javascript" src="javascript/ezScrumWidget/TaskBoard/StatusPanel.js"></script>
<script type="text/javascript" src="javascript/ezScrumWidget/TaskBoard/StoryCard.js"></script>
<script type="text/javascript" src="javascript/ezScrumWidget/TaskBoard/TaskBoardWidget.js"></script>
<script type="text/javascript" src="javascript/ezScrumWidget/TaskBoard/TaskCard.js"></script>

<script type="text/javascript" src="javascript/CustomWidget/CreateCustomIssueWidget.js"></script>
<script type="text/javascript" src="javascript/CustomWidget/EditCustomIssueWidget.js"></script>
<script type="text/javascript" src="javascript/CustomWidget/DeleteCustomIssueWidget.js"></script>
<script type="text/javascript" src="javascript/CustomWidget/TransformToStoryWidget.js"></script>
<script type="text/javascript" src="javascript/CustomWidget/TransformToUnplannedItemWidget.js"></script>
<script type="text/javascript" src="javascript/CustomWidget/AddCommentWidget.js"></script>

<script type="text/javascript" src="javascript/ezTrackWidget/CreateCustomIssueTypeWidget.js"></script>
<script type="text/javascript" src="javascript/ezTrackWidget/ManageCustomIssueStatusWidget.js"></script>

<!--sprintBacklog Widget-->  
<script type="text/javascript" src="javascript/ezScrumWidget/DropStoryWidget.js"></script>
<script type="text/javascript" src="javascript/ezScrumWidget/CreateTaskWidget.js"></script>
<script type="text/javascript" src="javascript/ezScrumWidget/DropTaskWidget.js"></script>
<script type="text/javascript" src="javascript/ezScrumWidget/DeleteTaskWidget.js"></script>
<script type="text/javascript" src="javascript/ezScrumWidget/ExistedTaskWidget.js"></script>
<link rel="stylesheet" type="text/css" href="css/ezScrum/Issue.css"/>

<!-- release plan widget -->
<script type="text/javascript" src="javascript/ezScrumWidget/CreateReleaseWidget.js"></script>
<script type="text/javascript" src="javascript/ezScrumWidget/DeleteReleaseWidget.js"></script>
<script type="text/javascript" src="javascript/ezScrumWidget/DropStoryFromReleaseWidget.js"></script>


<!-- panel -->
<script type="text/javascript" src="javascript/ezScrumPanel/ProjectDescFormPanel.js"></script>
<script type="text/javascript" src="javascript/ezScrumPanel/TaskBoardDescFormPanel.js"></script>
<script type="text/javascript" src="javascript/ezScrumPanel/SprintPlanPanel.js"></script>
<script type="text/javascript" src="javascript/ezScrumPanel/BurndownChartFormPanel.js"></script>
<script type="text/javascript" src="javascript/ezScrumPanel/ReleaseBurndownChartFormPanel.js"></script>
<script type="text/javascript" src="javascript/ezScrumPanel/MembersGridPanel.js"></script>
<script type="text/javascript" src="javascript/ezScrumPanel/RetrospectiveGridPanel.js"></script>
<script type="text/javascript" src="javascript/ezScrumPanel/UnplannedGridPanel.js"></script>
<script type="text/javascript" src="javascript/ezScrumPanel/TaskBoardSprintDescFormPanel.js"></script>
<script type="text/javascript" src="javascript/ezScrumPanel/TaskBoardCardFormPanel.js"></script>
<script type="text/javascript" src="javascript/ezScrumPanel/ezScrumReportTabPanel.js"></script>
<script type="text/javascript" src="javascript/ezScrumPanel/ITSConfigFormPanel.js"></script>
<script type="text/javascript" src="javascript/ezScrumPanel/SprintBacklogTreePanel.js"></script>


<!--use jsp wrap js content, because ext widget must get plugin info synchronously at initial time  -->
<jsp:include page="ntut/csie/ezScrum/stapler/ProjectLeftTree.jsp"/>
<jsp:include page="ntut/csie/ezScrum/stapler/ReleasePlan/ReleasePlanTopToolbar.jsp"/> 
<jsp:include page="ntut/csie/ezScrum/stapler/ProductBacklog/ProductBacklogTopToolbar.jsp"/> 
<jsp:include page="ntut/csie/ezScrum/stapler/TaskBoard/TaskBoardCardPanel.jsp"/>



<script type="text/javascript" src="javascript/ezScrumPanel/ProductBacklogFunctionSupport.js"></script>
<script type="text/javascript" src="javascript/ezScrumPanel/IssueGridPanel.js"></script>
<script type="text/javascript" src="javascript/ezScrumPanel/ProductBacklogPanel.js"></script>

<script type="text/javascript" src="javascript/ezScrumPanel/ReleaseBacklogGridPanel.js"></script>
<script type="text/javascript" src="javascript/ezScrumWidget/ShowReleaseBacklogWindow.js"></script><!-- Widget after "ReleaseBacklogGridPanel.js", "ReleaseBurndownChartFormPanel.js"-->
<script type="text/javascript" src="javascript/ezScrumPanel/ReleasePlanTreePanel.js"></script>
<script type="text/javascript" src="javascript/ezScrumPanel/ReleasePlanPanel.js"></script>


<!-- Page -->
<script type="text/javascript" src="javascript/ezScrumPage/Summary.js"></script>
<script type="text/javascript" src="javascript/ezScrumPage/ModifyConfig.js"></script>
<script type="text/javascript" src="javascript/ezScrumPage/Members.js"></script>
<script type="text/javascript" src="javascript/ezScrumPage/ProductBacklog.js"></script>
<script type="text/javascript" src="javascript/ezScrumPage/ReleasePlan.js"></script>
<script type="text/javascript" src="javascript/ezScrumPage/SprintPlan.js"></script>
<script type="text/javascript" src="javascript/ezScrumPage/SprintBacklog.js"></script>
<script type="text/javascript" src="javascript/ezScrumPage/TaskBoard.js"></script>
<script type="text/javascript" src="javascript/ezScrumPage/Retrospective.js"></script>
<script type="text/javascript" src="javascript/ezScrumPage/Unplanned.js"></script>
<script type="text/javascript" src="javascript/ezScrumPage/ezScrumReport.js"></script>
<script type="text/javascript" src="javascript/ezScrumPage/ITSConfig.js"></script>
<script type="text/javascript" src="javascript/ezScrumPage/PluginConfig.js"></script>
<script type="text/javascript" src="javascript/ezScrumPage/ProductBacklog.js"></script>
<script type="text/javascript" src="javascript/ezScrumPage/ValidateUserEvent.js"></script>

<!-- Content Panel -->
<script type="text/javascript" src="javascript/ezScrumLayout/ProjectLeftTreePanel.js"></script>
<script type="text/javascript" src="javascript/ezScrumLayout/ProjectLeftTreePanelBtnEvent.js"></script>
<script type="text/javascript" src="javascript/ezScrumPanel/Top_Panel.js"></script>
<script type="text/javascript" src="javascript/ezScrumPanel/LeftSide_Panel.js"></script>
<script type="text/javascript" src="javascript/ezScrumPanel/Content_Panel.js"></script>
<script type="text/javascript" src="javascript/ezScrumPanel/Footer_Panel.js"></script>

<!-- Base layout -->
<script type="text/javascript" src="javascript/ezScrumLayout/ezScrumProjectUI.js"></script>
<link rel="stylesheet" type="text/css" href="css/ext/css/ext-all.css" />
<link rel="stylesheet" type="text/css" href="css/ezScrum/TreePanel.css" />
<link rel="stylesheet" type="text/css" href="css/ezScrum/TopPanel.css" />
<link rel="stylesheet" type="text/css" href="css/TaskBoard.css"/>

<link rel="stylesheet" type="text/css" href="javascript/ux/treegrid/treegrid.css" rel="stylesheet" />
<link rel="stylesheet" type="text/css" href="javascript/ux/gridfilters/css/GridFilters.css" />
<link rel="stylesheet" type="text/css" href="javascript/ux/gridfilters/css/RangeMenu.css" />
<link rel="stylesheet" type="text/css" href="javascript/ux/css/RowEditor.css" />
<link rel="stylesheet" type="text/css" href="javascript/ux/fileuploadfield/css/fileuploadfield.css"/>

<link rel="stylesheet" type="text/css" href="css/ext/css/visual/multiple-sorting.css" />
<link rel="stylesheet" type="text/css" href="css/ezScrum/ProductBacklog.css"/>
<link rel="stylesheet" type="text/css" href="css/ezScrum/IssueHistory.css"/>


<!-- ezScrum import -->
<script type="text/javascript">
	Ext.onReady(function() {
		/*
		 * 針對 session 過期先作判斷, 若過期則無需跟 server 要資料.
		 * 例如在此頁 logout 後, 回上一頁(此頁)會掛掉
		 * 所以利用 check session, 若過期則導回 logon 頁面
		 * ezScrumContent(Summary), ViewList, ezScrumUserManagementUI 都先暫時用此方法
		 * note: 若在 before render event 時 check session, 還是會先有 init 的動作, 會浪費資源
		 */
		checkUserSession();
		
		Ext.QuickTips.init();
		
		var ezScrumProjectContent = new ezScrum.ProjectMaiUI();
		ezScrumProjectContent.render("content");
	});
</script>
 
<div id="content"></div>
</html>