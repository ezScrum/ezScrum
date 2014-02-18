/*
 * this java script supports ProductBacklog Functions: 
 * 		Search,  
 * 		multiple-sort,
 * 		Filter(Backlogged, Detailed, Done),
 */

/* =============================================
 * Search Function
 * =============================================*/

// Search Field
var search_field = new Ext.form.TextField({
	fieldLabel: 'Search',
	enableKeyEvents: true,
	comboBoxValue: '',
	disabled: true,
	initEvents: function() {
		var keyPress = function(e) {
			this.search(searchComboBox.getValue(), this.getValue());
		};
		this.el.on("keyup", keyPress, this);
	},
	setComboBoxValue: function(value) {
		this.comboBoxValue = value;
		if (value != searchFieldStore.getAt(0).data['dataValue']) { // 選回default
			if (this.getValue().length > 0) {
				this.search(value, this.getValue()); // 如果textField已經有值，則回復search狀態
			}
			this.setDisabled(false);
		} else {
			this.reset(); // 回到default狀態，列出所有story。
			this.setDisabled(true);
		}
	},
	/*
	 * 利用comboBox value和textfield value到store篩選資料
	 */
	search: function(value, text) {
		ProductBacklogStore.proxy.setSearchComboBoxValue(value);
		ProductBacklogStore.proxy.setSearchText(text);
		if (text.length > 0) {
			ProductBacklogStore.load({
				params: {
					start: 0,
					limit: pageSize
				}
			});
		} else {
			ProductBacklogStore.load({
				params: {
					start: 0,
					limit: pageSize
				}
			});
			ProductBacklogStore.proxy.reload = true;
			initShowDetail(); // 回到default 狀態，找出冰山狀態頁碼。
		}
	},
	reset: function() {
		this.setDisabled(true);
		this.setValue('');
		this.comboBoxValue = '';
		this.search(searchFieldStore.getAt(0).data['dataValue'], this.getValue());
	}
});
// search comboBox
var searchFieldStore = new Ext.data.SimpleStore({ // 由於comboBox所需要的內容為靜態而不是來自資料庫，所以使用SimpleStore
	fields: ['dataValue'],
	data: [['Please select ...'], ['Story Name'], ['Notes'], ['How To Demo'], ['Release'], ['Sprint ID']]
});

var searchComboBox = new Ext.form.ComboBox({
	typeAhead: true,
	triggerAction: 'all',
	lazyRender: true,
	editable: false,
	mode: 'local',
	store: searchFieldStore,
	fieldLabel: 'searchComboBOX',
	blankText: searchFieldStore.getAt(0).data['dataValue'],
	emptyText: searchFieldStore.getAt(0).data['dataValue'],
	valueField: 'dataValue', // 選擇display項目相對的賦與值。
	displayField: 'dataValue', // 選擇store中field裡的一個欄位名稱。
	id: 'searchComboBoxID',
	listeners: {
		select: function() {
			var record = this.getStore().getAt(this.selectedIndex);
			var selectSearchFilter = record.data['dataValue'];
			search_field.setComboBoxValue(selectSearchFilter);
		}
	}
});

/*
 * ============================================= multiple-sort =============================================
 */

// Ext Toolbar
var reorderer = new Ext.ux.ToolbarReorderer();
var droppable = new Ext.ux.ToolbarDroppable({
	/**
	 * Creates the new toolbar item from the drop event
	 */
	createItem: function(store) {
		var column = this.getColumnFromDragDrop(store);

		return createSorterButton({
			text: column.header,
			sortData: {
				field: column.dataIndex,
				direction: "ASC"
			}
		});
	},

	/**
	 * Custom canDrop implementation which returns true if a column can be added to the toolbar
	 * 
	 * @param {Object} data Arbitrary data from the drag source
	 * @return {Boolean} True if the drop is allowed
	 */
	canDrop: function(dragSource, event, store) {
		var sorters = getSorters(), column = this.getColumnFromDragDrop(store);

		for ( var i = 0; i < sorters.length; i++) {
			if (sorters[i].field == column.dataIndex) return false;
		}

		return true;
	},

	 afterLayout: doSort,

	/**
	 * Helper function used to find the column that was dragged
	 * 
	 * @param {Object} data Arbitrary data from
	 */
	getColumnFromDragDrop: function(store) {
		var index = store.header.cellIndex, colModel = grid.colModel, column = colModel.getColumnById(colModel.getColumnId(index));

		return column;
	}
});

function changeSortDirection(button, changeDirection) {
	var sortData = button.sortData, iconCls = button.iconCls;

	if (sortData != undefined) {
		if (changeDirection !== false) {
			button.sortData.direction = button.sortData.direction.toggle("ASC", "DESC");
			button.setIconClass(iconCls.toggle("sort-asc", "sort-desc"));
		}

		ProductBacklogStore.clearFilter();
		if (ProductBacklogStore.sortInfo !== undefined) {
			// override single sort Info.
			ProductBacklogStore.sortInfo = [];
		}
		ProductBacklogStore.proxy.getSorters(getSorters());
		doSort();
	}
};

// multisort tbar (render no ProductBacklogPanel)
var tbar_multisort = new Ext.Toolbar({
	items: ['Search:', searchComboBox, search_field, 'Sorting order:', '-'],
	plugins: [reorderer, droppable],
	listeners: {
		scope: this,
		reordered: function(button) {
			changeSortDirection(button, false);
		}
	}
});

// add,remove button, sort data
function initButton_MultiSort() {
	while (tbar_multisort.get(4) !== undefined) {
		tbar_multisort.remove(4);
	}

	var reorderable = true; // false means not allow change

	// the highest priority, not allow to change
	tbar_multisort.add(createSorterButton({
		text: 'Status',
		sortData: {
			field: 'FilterType',
			direction: 'DESC'
		}
	}, reorderable));
	tbar_multisort.add(createSorterButton({
		text: 'Sprint',
		sortData: {
			field: 'Sprint',
			direction: 'ASC'
		}
	}, reorderable));
	tbar_multisort.add(createSorterButton({
		text: 'Importance',
		sortData: {
			field: 'Importance',
			direction: 'DESC'
		}
	}, reorderable));
	tbar_multisort.add(createSorterButton({
		text: 'Value',
		sortData: {
			field: 'Value',
			direction: 'DESC'
		}
	}, reorderable));
	tbar_multisort.add(createSorterButton({
		text: 'Estimate',
		sortData: {
			field: 'Estimate',
			direction: 'DESC'
		}
	}, reorderable));

	// Layout
	tbar_multisort.doLayout();

};

function createSorterButton(config, reorder) {
	config = config || {};

	Ext.applyIf(config, {
		listeners: {
			click: function(button, e) {
				changeSortDirection(button, true);
			}
		},
		iconCls: 'sort-' + config.sortData.direction.toLowerCase(),
		reorderable: reorder
	});
	return new Ext.Button(config);
};

function doSort() {
	ProductBacklogStore.load({
		params: {
			start: Ext.getCmp('productBacklogGridPanel').getBottomToolbar().cursor,
			limit: pageSize
		}
	});
};

function getSorters() {
	var sorters = [];
	Ext.each(tbar_multisort.findByType('button'), function(button) {
		sorters.push(button.sortData);
	}, this);

	return sorters;
};

/*
 * ============================================= Filters (Backlogged, Detailed, Done) =============================================
 */
// 過濾後的record(get sort,filter,search)
function getProxyRecords() {
	ProductBacklogRecords = ProductBacklogStore.proxy.returnRecords();
};

function initShowDetail() {
	getProxyRecords();
	var firstData = 0;
	var firstDetail = firstBacklog = firstDone = 0;
	var isDetail = isBacklog = isDone = false;

	Ext.each(ProductBacklogRecords, function(value, index, length) {
		if (value.data['FilterType'] == 'DETAIL' && isDetail == false) {
			firstDetail = index;
			isDetail = true;
		} else if (value.data['FilterType'] == 'BACKLOG' && isBacklog == false) {
			firstBacklog = index;
			isBacklog = true;
		} else if (value.data['FilterType'] == 'DONE' && isDone == false) {
			firstDone = index;
			isDone = true;
		}

		if (isDetail == true && isBacklog == true && isDone == true) return false;
	});

	if (isDetail) firstData = firstDetail;
	else if (isBacklog) firstData = firstBacklog;
	else if (isDone) firstData = firstDone;
	else Ext.example.msg('Stories', 'no topics to display !!');

	ProductBacklogStore.load({
		params: {
			start: parseInt(firstData / pageSize) * pageSize,
			limit: pageSize
		}
	});
	Ext.getCmp('productBacklogGridPanel').getBottomToolbar().inputItem.setValue(parseInt(firstData / pageSize) + 1);
};
