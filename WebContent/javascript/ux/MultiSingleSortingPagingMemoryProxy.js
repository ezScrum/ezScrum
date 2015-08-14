/*
 * ! Ext JS Library 3.0.3 Copyright(c) 2006-2009 Ext JS, LLC licensing@extjs.com
 * http://www.extjs.com/license
 */

/* Fix for Opera, which does not seem to include the map function on Array's */
if (!Array.prototype.map) {
	Array.prototype.map = function(fun) {
		var len = this.length;
		if (typeof fun != 'function') {
			throw new TypeError();
		}
		var res = new Array(len);
		var thisp = arguments[1];
		for ( var i = 0; i < len; i++) {
			if (i in this) {
				res[i] = fun.call(thisp, this[i], i, this);
			}
		}
		return res;
	};
}

Ext.ns('Ext.ux.data');

/**
 * @class Ext.ux.data.MultiSingleSortingPagingMemoryProxy
 * @extends Ext.data.MemoryProxy
 * <p>
 * Paging Memory Proxy, allows to use paging grid with in memory dataset
 * </p>
 */
Ext.ux.data.MultiSingleSortingPagingMemoryProxy = Ext.extend(Ext.data.MemoryProxy, {
	constructor: function(data, filter) {
		Ext.ux.data.MultiSingleSortingPagingMemoryProxy.superclass.constructor.call(this);
		this.data = data;
		this.filter = filter;
		this.reload = false;
		this.SorterList;
		this.Records_cp;
		this.SearchText;
		this.SearchComboBoxValue;
	},
	doRequest: function(action, rs, params, reader, callback, scope, options) {
		params = params || {};
		var result = {
			success: true,
			records: [],
			totalRecords: 0
		};
		try {
			if ((!this.storeData) || this.reload) {
				this.reload = false;
				this.storeData = reader.read(this.data);
			}
			result.records = this.storeData.records;// ~~~
			result.success = this.storeData.success;
			result.totalRecords = this.storeData.totalRecords;
		} catch (e) {
			this.fireEvent('loadexception', this, options, null, e);
			callback.call(scope, null, options, false);
			return;
		}

		/*
		 * 1.欄位的Filter，依照每個欄位所勾選或設定的Filter條件 Ex.Tag欄位勾選"Bug"，那麼就只會顯示欄位內含Bug這個標籤的資料出來
		 * 
		 * 2.依照使用者輸入的文字進行搜尋過濾 Ex.使用者在Search的Field輸入"使用者",那麼就找出所有Name有包含使用者的Story
		 */
		var fun = this.getFilter();
		var tempRecord = new Array();
		for ( var i = 0; i < result.records.length; i++) {
			if (fun(result.records[i]) && this.searchFilter(this.SearchComboBoxValue, result.records[i])) tempRecord.push(result.records[i]);
		}
		result.records = tempRecord;
		result.totalRecords = result.records.length;

		/*
		 * MulitSort的處理，By 得宇大大
		 */
		var recordType = reader.recordType;
		var fields = recordType.prototype.fields;
		var direction = direction || "ASC", sorters = [], sortFns = [];

		if (params.sort == undefined) {	// multi sort
			sorters = this.SorterList;	// getMultiSorters

			if (multiSortInfo && direction == multiSortInfo.direction) {
				direction = direction.toggle("ASC", "DESC");
			}

			var multiSortInfo = {
				sorters: sorters,
				direction: direction
			};
		} else {// single sort
			sorters = [{
				direction: params.dir,
				field: params.sort
			}];
		}

		for ( var i = 0, j = sorters.length; i < j; i++) {
			sortFns.push(this.createSortFunction(sorters[i].field, sorters[i].direction, fields));
		}
		var directionModifier = direction.toUpperCase() == "DESC" ? -1 : 1;

		var fn = function(r1, r2) {
			var result = sortFns[0].call(this, r1, r2);

			// if we have more than one sorter, OR any
			// additional sorter
			// functions together
			if (sortFns.length > 1) {
				for ( var i = 1, j = sortFns.length; i < j; i++) {
					result = result || sortFns[i].call(this, r1, r2);
				}
			}
			end = directionModifier * result;

			return directionModifier * result;
		};

		result.records.sort(fn);

		// return the sorted record to ShowProductBacklog
		this.Records_cp = result.records;

		// paging (use undefined cause start can also be 0 (thus false))
		if (params.start !== undefined && params.limit !== undefined) {
			result.records = result.records.slice(params.start, params.start + params.limit);
		}

		callback.call(scope, result, options, true);
	},
	createSortFunction: function(field, direction, fields) {// create
		// Sorting
		// Function
		direction = direction || "ASC";
		var directionModifier = direction.toUpperCase() == "DESC" ? -1 : 1;

		var sortType = fields.get(field).sortType;

		// create a comparison function. Takes 2 records,
		// returns 1 if
		// record 1 is greater,
		// -1 if record 2 is greater or 0 if they are equal
		return function(r1, r2) {
			var v1 = sortType(r1.data[field]), v2 = sortType(r2.data[field]);

			return directionModifier * (v1 > v2 ? 1 : (v1 < v2 ? -1 : 0));
		};
	},
	// get the multisort's sorters
	getSorters: function(sorters) {
		this.SorterList = sorters;
	},
	setSearchText: function(text) {
		if (text.length != 0) this.SearchText = text;
		else this.SearchText = null;
	},
	setSearchComboBoxValue: function(value) {
		if (value.length != 0) // 0相當於""(空字串)。
		this.SearchComboBoxValue = value;
		else this.SearchComboBoxValue = null;
	},
	/*
	 * 依照SearchText進行Search過濾，判斷Record是否有符合Search條件
	 */
	searchFilter: function(comboBoxValue, record) {
		if (this.SearchText != null) {
			if (comboBoxValue == "Story Name") {
				if (record.get('Name').search(this.SearchText) != -1) return true;
			} else if (comboBoxValue == "Notes") {
				if (record.get('Notes').search(this.SearchText) != -1) return true;
			} else if (comboBoxValue == "How To Demo") {
				if (record.get('HowToDemo').search(this.SearchText) != -1) return true;
			} else if (comboBoxValue == "Release") {
				if (record.get('Release').search(this.SearchText) != -1) return true;
			} else if (comboBoxValue == "Sprint ID") {
				if (record.get('Sprint') == this.SearchText) return true;
			} else return false;
		} else {
			return true;
		}

	},
	returnRecords: function() {
		return this.Records_cp;
	},
	getFilter: function() {
		if (this.filter == null) return function(record) {
			return true
		};
		var f = [], len, i;
		this.filter.filters.each(function(filter) {
			if (filter.active) {
				f.push(filter);
			}
		});

		return function(record) {

			len = f.length;
			for (i = 0; i < len; i++) {
				if (!f[i].validateRecord(record)) {
					return false;
				}
			}
			return true;
		};
	},
	reloadData: function(url) {
		Ext.Ajax.request({
			url: url,
			scope: this,
			success: function(response) {
				this.onReloadDataSuccess(response);
			}
		});
	},
	onReloadDataSuccess: function(response) {
		this.data = response;
		this.reload = true;
	},
	updateRecord: function(record) {
		var m_record = this.getRecordById(record.id);
		m_record.data = record.data;
	},
	insertRecord: function(record) {
		this.storeData.records.push(record);
	},
	deleteRecord: function(id) {
		for ( var i = 0; i < this.storeData.records.length; i++) {
			if (this.storeData.records[i].id == id) this.storeData.records.splice(i, 1);
		}
	},
	getRecordById: function(id) {
		for ( var i = 0; i < this.storeData.records.length; i++) {
			if (this.storeData.records[i].id == id) return this.storeData.records[i];
		}
		return null;
	}
});

// backwards compat.
Ext.data.MultiSingleSortingPagingMemoryProxy = Ext.ux.data.MultiSingleSortingPagingMemoryProxy
