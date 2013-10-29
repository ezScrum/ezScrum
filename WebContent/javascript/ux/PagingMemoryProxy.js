/*!
 * Ext JS Library 3.0.3
 * Copyright(c) 2006-2009 Ext JS, LLC
 * licensing@extjs.com
 * http://www.extjs.com/license
 */

/* Fix for Opera, which does not seem to include the map function on Array's */
if (!Array.prototype.map) {
    Array.prototype.map = function(fun){
        var len = this.length;
        if (typeof fun != 'function') {
            throw new TypeError();
        }
        var res = new Array(len);
        var thisp = arguments[1];
        for (var i = 0; i < len; i++) {
            if (i in this) {
                res[i] = fun.call(thisp, this[i], i, this);
            }
        }
        return res;
    };
}

Ext.ns('Ext.ux.data');

/**
 * @class Ext.ux.data.PagingMemoryProxy
 * @extends Ext.data.MemoryProxy
 * <p>Paging Memory Proxy, allows to use paging grid with in memory dataset</p>
 */
Ext.ux.data.PagingMemoryProxy = Ext.extend(Ext.data.MemoryProxy, {
    constructor : function(data, filter){
        Ext.ux.data.PagingMemoryProxy.superclass.constructor.call(this);
        this.data = data;
        this.filter = filter;
        this.reload = false;
    },
    doRequest : function(action, rs, params, reader, callback, scope, options){
        params = params ||
        {};
        var result = {success:true, records:[], totalRecords:0};
        try {
        	if((!this.storeData) || this.reload)
        	{
        		this.reload = false;
        		this.storeData = reader.read(this.data);
        	}
            result.records = this.storeData.records;
            result.success = this.storeData.success;
            result.totalRecords = this.storeData.totalRecords;
        } 
        catch (e) {
            this.fireEvent('loadexception', this, options, null, e);
            callback.call(scope, null, options, false);
            return;
        }

        // filtering
        //result.records = result.records.filter(this.getFilter());
        var fun = this.getFilter();
        var tempRecord = new Array();
        for(var i = 0; i < result.records.length; i++)
        {
        	if(fun(result.records[i]))
        		tempRecord.push(result.records[i]);
        }
        result.records = tempRecord;
        result.totalRecords = result.records.length;

        
        // sorting
        if (params.sort !== undefined) {
            // use integer as params.sort to specify column, since arrays are not named
            // params.sort=0; would also match a array without columns
            var recordType = reader.recordType;
            var fields = recordType.prototype.fields;
			var dir = String(params.dir).toUpperCase() == 'DESC' ? -1 : 1;
			var f = params.sort;
	        var st = fields.get(f).sortType;
	        //var st = new  function(a){return a;};
	        var fn = function(r1, r2){
	            var v1 = st(r1.data[f]), v2 = st(r2.data[f]);
	            return (v1 > v2 ? 1 : (v1 < v2 ? -1 : 0)) * dir;
	        };
	        result.records.sort(fn);
        }
        // paging (use undefined cause start can also be 0 (thus false))
        if (params.start !== undefined && params.limit !== undefined) {
            result.records = result.records.slice(params.start, params.start + params.limit);
        }
        callback.call(scope, result, options, true);
    },
    getFilter : function()
    {
    	if(this.filter == null)
    		return function(record){return true}; 
    	var f = [], len, i;
        this.filter.filters.each(function (filter) {
            if (filter.active) {
                f.push(filter);
            }
        });
        
        
        return function (record) {
            
			len = f.length;
            for (i = 0; i < len; i++) {
                if (!f[i].validateRecord(record)) {
                    return false;
                }
            }
            return true;
        };
    },
    reloadData:function(url)
    {
    	Ext.Ajax.request({
			url:url,
			scope:this,
			success:function(response){
				this.onReloadDataSuccess(response);
			}
		});
    },
    onReloadDataSuccess:function(response)
    {
    	this.data = response;
    	this.reload = true;
    },
    updateRecord:function(record)
    {
    	var m_record = this.getRecordById(record.id);
    	m_record.data = record.data;
    },
    insertRecord:function(record)
    {
    	this.storeData.records.push(record);
    },
    deleteRecord:function(id)
    {
    	for(var i = 0; i < this.storeData.records.length; i++)
    	{
    		if(this.storeData.records[i].id == id)
    			this.storeData.records.splice(i, 1);
    	}
    },
    getRecordById:function(id)
    {
    	for(var i = 0; i < this.storeData.records.length; i++)
    	{
    		if(this.storeData.records[i].id == id)
    			return this.storeData.records[i];
    	}
    	return null;
    }
});

//backwards compat.
Ext.data.PagingMemoryProxy = Ext.ux.data.PagingMemoryProxy;
