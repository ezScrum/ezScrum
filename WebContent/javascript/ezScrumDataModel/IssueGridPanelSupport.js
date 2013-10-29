/**
 * support IssueGridPanel
 */

// 將 Issue ID 產生一個 hyper link to view issue page, for render function
function makeIssueDetailUrl(value, metaData, record, rowIndex, colIndex, store) {
	return "<a href=\"" + record.data['Link'] + "\" target=\"_blank\">" + value + "</a>";
}

// 將 Issue ID 產生一個 hyper link to view issue page
function makeIssueDetailUrl2(url, Id) {
	return "<a href=\"" + url + "\" target=\"_blank\">" + Id + "</a>";
}

// 將被轉換的特殊字元轉回來
function SpecialChar_Translate(str) {
	str = str.replace(/&lt;/ig, "<");
	str = str.replace(/&gt;/ig, ">");
	str = str.replace(/&apos;/ig, "'");
	str = str.replace(/&quot;/ig, "\"");
	str = str.replace(/&amp;/ig, "&");
	return str;
}

//Filter
var IssueGridPanelFilter = new Ext.ux.grid.GridFilters({
	local:false,
	filters: [{
		type: 'numeric',
		dataIndex: 'Id'
	},{
		type: 'list',
		dataIndex: 'Status',
		options: ['new', 'closed']
	},{
		type: 'tag',
		dataIndex: 'Tag',
		options: ['new', 'closed']
	}]
});

